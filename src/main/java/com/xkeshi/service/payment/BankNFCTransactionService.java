package com.xkeshi.service.payment;

import com.drongam.hermes.entity.SMS;
import com.xkeshi.common.em.Payment;
import com.xkeshi.common.em.TransactionPaymentStatus;
import com.xkeshi.common.globality.GlobalSource;
import com.xkeshi.pojo.po.BankNFCTransaction;
import com.xkeshi.pojo.vo.Result;
import com.xkeshi.pojo.vo.SystemParam;
import com.xkeshi.pojo.vo.param.BankNFCPaymentRequestParam;
import com.xkeshi.pojo.vo.param.BankNFCPaymentResultParam;
import com.xkeshi.pojo.vo.param.BankNFCSignatureUploadParam;
import com.xkeshi.pojo.vo.param.BankNFCUpdateInfoParam;
import com.xpos.common.entity.Shop;
import com.xpos.common.entity.face.Business.BusinessType;
import com.xpos.common.entity.pos.POSGatewayAccount;
import com.xpos.common.entity.pos.POSGatewayAccount.POSGatewayAccountType;
import com.xpos.common.persistence.mybatis.BankNFCTransactionMapper;
import com.xpos.common.service.SMSService;
import com.xpos.common.service.ShopService;
import com.xpos.common.service.UpYunServiceImpl;
import com.xpos.common.utils.FileMD5;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 银行类电子现金（NFC）支付
 * @author chengj
 */
@Service
public class BankNFCTransactionService extends PaymentService{
	private final static Logger logger = LoggerFactory.getLogger(BankNFCTransactionService.class);
	
	@Autowired
	private ShopService shopService;
	
	@Autowired
	private SMSService smsService;
	
	@Autowired
	private UpYunServiceImpl upYunService;
	
	@Autowired
	private BankNFCTransactionMapper bankNFCTransactionMapper;
	
	public Result paymentForNFC(SystemParam systemParam,
			BankNFCPaymentRequestParam nfcPaymentParam, String orderNumber, POSGatewayAccount account) {
		String orderType = StringUtils.upperCase(nfcPaymentParam.getOrderType());
		
		//支付前检查订单状态
		Payment paymentStatus = checkOrderForPayment(orderNumber, nfcPaymentParam);
		if (paymentStatus == Payment.FIRST_PAYMENT){ //首次支付
			
			if (StringUtils.equals(orderType, "XPOS_ORDER")){
                //删除实体券
                clearPhysicalCouponsByOrderNumber(orderNumber);
				//添加实体券优惠
				if (insertPhysicalCoupons(orderNumber, nfcPaymentParam, systemParam.getMid())){
					return new Result(Payment.INVALID_PHYSICAL_COUPON.getName(), Payment.INVALID_PHYSICAL_COUPON.getCode()); //实体券如果不在商户可用则终止此次支付
				}

                //清空会员折扣
                clearMemberDiscountByOrderNumber(orderNumber);
				//添加会员折扣
				insertMemberDiscountToOrder(orderNumber, nfcPaymentParam, systemParam.getMid());

                //更新订单的实付金额（订单总金额*折扣-实体优惠券金额）
                updateOrderActualAmount(orderNumber);
			}
			
			//创建支付流水
			BankNFCTransaction po = new BankNFCTransaction();
			po.setOrderCodeByType(orderNumber, nfcPaymentParam.getOrderType());
			po.setRegisterMid(nfcPaymentParam.getRegisterMid());
			po.setPosChannel(account.getType().getState());
			po.setAmount(nfcPaymentParam.getAmount());
			po.setSerial(nfcPaymentParam.getSerial());
			po.setPaymentStatus(TransactionPaymentStatus.UNPAID.getValue());
			
			Result result = null;
			if(saveBankNFCTransaction(po)){
				result = new Result(Payment.POS_CREATE_SERIAL_SUCCESS.getName(), Payment.POS_CREATE_SERIAL_SUCCESS.getCode());
				result.setResult(po.getSerial());
			}else{
				result = new Result(Payment.POS_CREATE_SERIAL_FAILED.getName(), Payment.POS_CREATE_SERIAL_FAILED.getCode());
				// throw new RuntimeException(); //FIXME 创建流水失败，抛出异常回滚，但是payment具体错误信息无法传递
			}
			
			//更新支付方式
			updateOrderChargeChannel(orderType, orderNumber,
					GlobalSource.getIDByName(GlobalSource.metaPrepaidCardChargeChannelList,"闪付"));
			

			return result;
		} else if(paymentStatus == Payment.NOT_FIRST_PAYMENT){ //非首次支付
			
			//创建支付流水
			BankNFCTransaction po = new BankNFCTransaction();
			po.setOrderCodeByType(orderNumber, nfcPaymentParam.getOrderType());
			po.setRegisterMid(nfcPaymentParam.getRegisterMid());
			po.setPosChannel(account.getType().getState());
			po.setAmount(nfcPaymentParam.getAmount());
			po.setSerial(nfcPaymentParam.getSerial());
			po.setPaymentStatus(TransactionPaymentStatus.UNPAID.getValue());
			
			Result result = null;
			if(saveBankNFCTransaction(po)){
				result = new Result(Payment.POS_CREATE_SERIAL_SUCCESS.getName(), Payment.POS_CREATE_SERIAL_SUCCESS.getCode());
				result.setResult(po.getSerial());
			}else{
				result = new Result(Payment.POS_CREATE_SERIAL_FAILED.getName(), Payment.POS_CREATE_SERIAL_FAILED.getCode());
				// throw new RuntimeException(); //FIXME 创建流水失败，抛出异常回滚，但是payment具体错误信息无法传递
			}
			

			return result;
		} else {
			return new Result(paymentStatus.getName(), paymentStatus.getCode()); //非成功
		}
	}
	
	public boolean saveBankNFCTransaction(BankNFCTransaction transaction) {
		return bankNFCTransactionMapper.insert(transaction) > 0;
	}
	
	public Result uploadSignature(SystemParam systemParam, BankNFCSignatureUploadParam signatureParam, 
												String orderNumber, POSGatewayAccount account) {
		BankNFCTransaction po = bankNFCTransactionMapper.selectBySerial(signatureParam.getSerial());
		if(po == null){
			return new Result(Payment.POS_UPLOAG_SIGNATURE_FAILED.getName(), Payment.POS_UPLOAG_SIGNATURE_FAILED.getCode());
		}else if(StringUtils.isBlank(signatureParam.getContent())){
			return new Result(Payment.POS_UPLOAG_SIGNATURE_CONTENT_EMPTY.getName(), Payment.POS_UPLOAG_SIGNATURE_CONTENT_EMPTY.getCode());
		}
		
		try{
			POSGatewayAccountType gatewayType = POSGatewayAccountType.queryByState(po.getPosChannel());
			byte[] picBinary = Base64.decodeBase64(signatureParam.getContent());
			String uploadURL = StringUtils.join("/order/", gatewayType.name(), "_signature/", po.getRegisterMid(), "/" ,po.getSerial(), ".png");
			boolean res = upYunService.uploadImg(uploadURL, picBinary);
			if(res){
				return new Result(Payment.POS_UPLOAG_SIGNATURE_SUCCESS.getName(), Payment.POS_UPLOAG_SIGNATURE_SUCCESS.getCode());
			}
		}catch(Exception e){
			logger.error("POS接口上传客户签字图片失败", e);
		}
		return new Result(Payment.POS_UPLOAG_SIGNATURE_FAILED.getName(), Payment.POS_UPLOAG_SIGNATURE_FAILED.getCode());
	}

	public Result processPaymentResultCallback(SystemParam systemParam, BankNFCPaymentResultParam paymentResultParam,
											String orderNumber, String serial, POSGatewayAccount account) {
		//validate sign
		if(!verify(paymentResultParam.toString(), paymentResultParam.getSign(), account.getSignKey())){
			return new Result(Payment.POS_INVALID_SIGNATURE.getName(), Payment.POS_INVALID_SIGNATURE.getCode());
		}
		
		BankNFCTransaction po = bankNFCTransactionMapper.selectBySerial(serial);
		if(po == null){
			return new Result(Payment.POS_STATUS_UPDATE_FAILED.getName(), Payment.POS_STATUS_UPDATE_FAILED.getCode());
		}
		
		if(StringUtils.equals("00", paymentResultParam.getResponseCode())){
			po.setPaymentStatus(TransactionPaymentStatus.SUCCESS.getValue());
			po.setCardNumber(paymentResultParam.getCardNumber());
			po.setResponseCode(paymentResultParam.getResponseCode());
			po.setTerminal(paymentResultParam.getTerminalId());
			po.setLocation(paymentResultParam.getLocation());
			po.setAuthCode(paymentResultParam.getTransAuno());
			po.setReferenceNumber(paymentResultParam.getReferenceNumber());
			po.setBatchNumber(paymentResultParam.getBatchNo());
			po.setTraceNumber(paymentResultParam.getTransNo());
			po.setCardOrg(paymentResultParam.getCardOrg());
			po.setIssueCode(paymentResultParam.getIssueCode());
			po.setIssueName(paymentResultParam.getIssueName());
			po.setComment(paymentResultParam.toString());
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
			Date tradeTime = null;
			try {
				tradeTime = sdf.parse(StringUtils.join(paymentResultParam.getTransDate() + paymentResultParam.getTransTime()));
			} catch (ParseException e) {
				tradeTime = new Date();
			}
			po.setTradeTime(tradeTime);
			}else{ //非00均为失败
				po.setPaymentStatus(TransactionPaymentStatus.FAILED.getValue());
				po.setComment(paymentResultParam.toString());
				po.setResponseCode(paymentResultParam.getResponseCode() + "(" + paymentResultParam.getResponseDesc() + ")");
			}
		
			if(bankNFCTransactionMapper.updateBySerial(po) > 0){
                //pos支付成功更新订单支付状态
                if (po.getPaymentStatus() == TransactionPaymentStatus.SUCCESS.getValue()){
                    updateOrderPaymentStatus(orderNumber, paymentResultParam.getOrderType());
                }
				return new Result(Payment.POS_STATUS_UPDATE_SUCCESS.getName(), Payment.POS_STATUS_UPDATE_SUCCESS.getCode());
			}else{
				return new Result(Payment.POS_STATUS_UPDATE_FAILED.getName(), Payment.POS_STATUS_UPDATE_FAILED.getCode());
			}
	}
	
	private boolean verify(String params, String sign, String signKey) {
		params = params + "&signKey=" + signKey;
		String sign2 = "invalid";
		try {
			logger.debug("支付同步通知提交参数拼接后的URL格式：" + params);
			sign2 = FileMD5.getFileMD5String(params.getBytes());
		} catch (IOException e) {
			logger.error("校验支付通知消息签名失败！", e);
		}
		return StringUtils.equalsIgnoreCase(sign, sign2);
	}

	public Result updateConsumerMobile(SystemParam systemParam,
									BankNFCUpdateInfoParam updateInfoParam, String orderNumber, String serial) {
		BankNFCTransaction po = bankNFCTransactionMapper.selectBySerial(serial);
		if(po == null
			|| StringUtils.isBlank(updateInfoParam.getMobile())){
			return new Result(Payment.POS_STATUS_UPDATE_FAILED.getName(), Payment.POS_STATUS_UPDATE_FAILED.getCode());
		}else if(StringUtils.equals(po.getMobile(), updateInfoParam.getMobile())){
			return new Result(Payment.POS_STATUS_UPDATE_SUCCESS.getName(), Payment.POS_STATUS_UPDATE_SUCCESS.getCode());
		}
		
		po.setMobile(updateInfoParam.getMobile());
		if(bankNFCTransactionMapper.updateBySerial(po) > 0){
			//手机号更新成功后，发送电子账单
			String url = "http://xka.me/bntd/"+serial; //电子账单地址
			Shop shop = shopService.findShopByIdIgnoreVisible(systemParam.getMid());
			StringBuffer content = new StringBuffer();
			content.append("您于").append(new DateTime(po.getTradeTime()).toString("MM月dd日HH时mm分")).append("在 ").append(shop.getName())
			.append(" 消费人民币：").append(po.getAmount()).append("元，查看账单详情：").append(url);
			SMS sms = new SMS();
			sms.setMobile(updateInfoParam.getMobile());
			sms.setMessage(content.toString());
			smsService.sendSMSAndDeductions(shop.getId() ,BusinessType.SHOP,sms,null,"预付卡支付，电子账单短信" );
			return new Result(Payment.POS_STATUS_UPDATE_SUCCESS.getName(), Payment.POS_STATUS_UPDATE_SUCCESS.getCode());
		}
		return new Result(Payment.POS_STATUS_UPDATE_FAILED.getName(), Payment.POS_STATUS_UPDATE_FAILED.getCode());
	}

}
