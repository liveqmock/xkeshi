package com.xpos.common.service;

import com.drongam.hermes.entity.SMS;
import com.xkeshi.common.em.Payment;
import com.xkeshi.common.em.Refund;
import com.xkeshi.common.globality.GlobalSource;
import com.xkeshi.pojo.vo.Result;
import com.xkeshi.pojo.vo.SystemParam;
import com.xkeshi.pojo.vo.param.*;
import com.xkeshi.service.payment.PaymentService;
import com.xpos.common.entity.Order;
import com.xpos.common.entity.Order.Status;
import com.xpos.common.entity.Shop;
import com.xpos.common.entity.example.POSTransactionExample;
import com.xpos.common.entity.face.Business;
import com.xpos.common.entity.face.Business.BusinessType;
import com.xpos.common.entity.pos.POSGatewayAccount;
import com.xpos.common.entity.pos.POSTransaction;
import com.xpos.common.entity.pos.POSTransaction.POSTransactionStatus;
import com.xpos.common.entity.pos.POSTransaction.POSTransactionType;
import com.xpos.common.persistence.mybatis.CMCCTicketAgreementMapper;
import com.xpos.common.persistence.mybatis.POSTransactionMapper;
import com.xpos.common.searcher.POSTransactionSearcher;
import com.xpos.common.utils.BusinessSQLBuilder;
import com.xpos.common.utils.FileMD5;
import com.xpos.common.utils.Pager;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class POSTransactionServiceImpl extends PaymentService implements POSTransactionService{
	private final static Logger logger = LoggerFactory.getLogger(POSTransactionServiceImpl.class);
	
	@Autowired
	private ExternalHttpInvokeService externalHttpInvokeService;
	
	@Autowired
	private OrderService orderService;
	
	@Autowired
	private UpYunServiceImpl upYunService;
	
	@Autowired
	private ShopService shopService;
	
	@Autowired
	private SMSService smsService;
	
	@Autowired
	private POSTransactionMapper posTransactionMapper;
	
	@Autowired
	private CMCCTicketAgreementMapper agreementMapper;
	
	@Override
	public Pager<POSTransaction> findTransactions(Business business, POSTransactionSearcher searcher, Pager<POSTransaction> pager) {
		POSTransactionExample example = (POSTransactionExample)searcher.getExample();
		if(example == null){
			example = new POSTransactionExample();
			example.createCriteria();
		}
		if(StringUtils.isNotBlank(searcher.getNickName())){
			example.appendCriterion(BusinessSQLBuilder.getBusinessSQLByShopNickName(business.getSelfBusinessType(), business.getSelfBusinessId(), searcher.getNickName()));
		}else{
			example.appendCriterion(BusinessSQLBuilder.getBusinessSQL(business.getSelfBusinessType(), business.getSelfBusinessId()));
		}
		example.appendCriterion("deleted=", false);
		List<POSTransaction> list = posTransactionMapper.selectByExample(example, pager);
		int totalCount = posTransactionMapper.countByExample(example);
		pager.setTotalCount(totalCount);
		pager.setList(list);
		
		return pager;
	}

	@Override
	public POSTransaction findTransactionByCode(String code) {
		return posTransactionMapper.selectByCode(code);
	}
	
	@Override
	public POSTransaction findTransactionById(Long id) {
		return posTransactionMapper.selectById(id);
	}

	@Override
	public boolean savePOSTransaction(POSTransaction posTransaction) {
		posTransaction.setStatus(POSTransactionStatus.UNPAID); //创建默认“待付款”状态
		return posTransactionMapper.save(posTransaction) > 0;
	}

	@Override
	public boolean updatePOSTransaction(POSTransaction posTransaction) {
		return posTransactionMapper.updateById(posTransaction) > 0;
	}

	@Override
	@Transactional
	public boolean updatePOSTransactionByCode(POSTransaction posTransaction) {
		try{
			POSTransaction originalTransaction = findTransactionByCode(posTransaction.getCode());
			POSTransactionStatus originalStatus = originalTransaction.getStatus();
			POSTransactionStatus curStatus = posTransaction.getStatus();
			
			if(originalStatus.equals(curStatus)){
				return true;
			}else if(originalStatus.equals(POSTransactionStatus.UNPAID)){
				//等待付款状态，直接更新
				POSTransactionExample example = new POSTransactionExample();
				example.createCriteria().addCriterion("deleted = ", false)
										.addCriterion("code = ", posTransaction.getCode());
				return posTransactionMapper.updateByExample(posTransaction, example) == 1;
			}else if(originalStatus.equals(POSTransactionStatus.PAID_SUCCESS)){
				//付款成功的状态
				if(curStatus.equals(POSTransactionStatus.PAID_REVOCATION) || curStatus.equals(POSTransactionStatus.PAID_REFUND)) {
					//新状态为撤销或退款
					POSTransactionExample example = new POSTransactionExample();
					example.createCriteria().addCriterion("deleted = ", false)
					.addCriterion("code = ", posTransaction.getCode());
					boolean result = posTransactionMapper.updateByExample(posTransaction, example) == 1;
					
					//撤销相关点单，改为CANCEL状态
					Order order = orderService.findOrderByPOSTransactionId(posTransaction.getId());
					if(order != null){
						result &= orderService.discardOrderAndReturnItemInventory(order.getOrderNumber(), Status.REFUND);
					}
					return result;
				}else{
					//否则忽略，直接返回
					return true;
				}
			}else if(originalStatus.equals(POSTransactionStatus.PAID_FAIL)){
				if(curStatus.equals(POSTransactionStatus.PAID_SUCCESS) || curStatus.equals(POSTransactionStatus.PAID_REVOCATION) || curStatus.equals(POSTransactionStatus.PAID_REFUND)){
					//付款失败的状态，只有通知付款成功、撤销、退款才更新状态，否则忽略新的通知
					POSTransactionExample example = new POSTransactionExample();
					example.createCriteria().addCriterion("deleted = ", false)
					.addCriterion("code = ", posTransaction.getCode());
					return posTransactionMapper.updateByExample(posTransaction, example) == 1;
				}else{
					return true;
				}
			}
		}catch(DataAccessException dae){
			logger.error("更新POSOrder订单信息失败", dae);
			throw dae;
		}
		return false;
	}
	
	@Override
	public String createCMCCTicketOrder(POSTransaction transaction, String deviceNumber) {
		try{
			logger.info("创建新移动电子券订单。businessId=["+transaction.getBusinessId()+"], sum=["+transaction.getSum()+"]");
			String mobile = transaction.getMobile();
			String uid = agreementMapper.queryUidByMobile(mobile);
			
			if(StringUtils.isBlank(uid)){
				//如果用户还没签约，先调用签约接口
				uid = externalHttpInvokeService.signTicketPaymentAgreement(mobile);
				if(uid == null){
					//调用个人支付协议接口失败
					return "开通个人支付协议失败";
				}
				int row = agreementMapper.save(mobile, uid);
				if(row != 1){
					return "开通个人支付协议失败";
				}
			}
			
			//再次校验电子券余额
			int balance = externalHttpInvokeService.getTicketBalanceByPhone(mobile);
			if(balance < BigDecimal.valueOf(100).multiply(transaction.getSum()).setScale(2, RoundingMode.HALF_UP).intValue()){
				return "账户余额不足";
			}
			
			//调用支付接口提交订单
			externalHttpInvokeService.createCMCCTicketOrder(transaction, uid, deviceNumber);
			if(transaction.getStatus().equals(POSTransactionStatus.PAID_FAIL)){
				if(!StringUtils.isBlank(transaction.getRemark())){
					return transaction.getRemark();
				}else{
					return "支付失败，请稍后再试";
				}
			}
			
			//保存到本地数据库
			int row = posTransactionMapper.insert(transaction);
			if(row == 1){
				return null;
			}else{
				return "支付失败，请稍后再试";
			}
		}catch(Exception e){
			logger.error("创建新移动电子券订单失败", e);
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public String revocationCMCCTicketOrder(POSTransaction transaction) {
		try{
			logger.info("撤销移动电子券订单。businessId=["+transaction.getBusinessId()+"], orderId=["+transaction.getCode()+"]");
			//调用冲正接口撤销订单
			String errorMessage = externalHttpInvokeService.cancelCMCCTicketOrder(transaction);
			
			//保存到本地数据库
			if(StringUtils.isBlank(errorMessage)){
				transaction.setStatus(POSTransactionStatus.PAID_REVOCATION);
				int row = posTransactionMapper.updateByPrimaryKey(transaction);
				if(row == 1){
					return null;
				}else{
					return "冲正失败，请稍后再试";
				}
			}
			return errorMessage;
		}catch(Exception e){
			logger.error("撤销移动电子券订单失败", e);
			throw new RuntimeException(e);
		}
	}

	@Override
	public String[] getElectronicCashStatistic(Business business, POSTransactionSearcher searcher) {
		String[] statistic = new String[2];
		POSTransactionExample example = (POSTransactionExample) searcher.getExample();
		if(example == null){
			example = new POSTransactionExample();
			example.createCriteria();
		}
		example.appendCriterion(BusinessSQLBuilder.getSQL(business));
		example.appendCriterion("deleted = ", false);
		
		statistic[0] = ""+posTransactionMapper.countByExample(example);
		String whereClause = combineWhereClause(business, searcher);
		BigDecimal sum = posTransactionMapper.countTotalAmount(whereClause);
		statistic[1] = sum !=null ? sum.toString() : "0";
		return statistic;
	}
	
	@Override
	public String[] getBankCardStatistic(Business business, POSTransactionSearcher searcher) {
		String[] statistic = new String[2];
		POSTransactionExample example = (POSTransactionExample) searcher.getExample();
		if(example == null){
			example = new POSTransactionExample();
			example.createCriteria();
		}
		example.appendCriterion("deleted = ", false);
		example.appendCriterion(BusinessSQLBuilder.getSQL(business));
		
		statistic[0] = ""+posTransactionMapper.countByExample(example);
		String whereClause = combineWhereClause(business, searcher);
		BigDecimal sum = posTransactionMapper.countTotalAmount(whereClause);
		statistic[1] = sum !=null ? sum.toString() : "0";
		return statistic;
	}

	@Override
	public String[] getAlipayQRCodeStatistic(Business business, POSTransactionSearcher searcher) {
		String[] statistic = new String[2];
		POSTransactionExample example = (POSTransactionExample) searcher.getExample();
		if(example == null){
			example = new POSTransactionExample();
			example.createCriteria();
		}
		example.appendCriterion(BusinessSQLBuilder.getSQL(business));
		example.appendCriterion("deleted = ", false);
		
		statistic[0] = ""+posTransactionMapper.countByExample(example);
		String whereClause = combineWhereClause(business, searcher);
		BigDecimal sum = posTransactionMapper.countTotalAmount(whereClause);
		statistic[1] = sum !=null ? sum.toString() : "0";
		return statistic;
	}

	private String combineWhereClause(Business business, POSTransactionSearcher searcher) {
		StringBuilder whereClause = new StringBuilder("deleted = false");
		whereClause.append(" AND ").append(BusinessSQLBuilder.getSQL(business));
		if(searcher.getStatusSet() != null && searcher.getStatusSet().size() > 0){
			String orsql = " AND (";
			int orCount = 0;
			for(POSTransactionStatus  status: searcher.getStatusSet()){
				if(orCount!=0)
					orsql+=" OR ";
				orCount++;
				orsql+= ("status = \'" + status.toString() + "\'");
			}
			orsql+=")";
			whereClause.append(orsql);
		}
		if(searcher.getType() != null){
			whereClause.append(" AND type = \'").append(searcher.getType().toString()).append("\'");
		}
		if(searcher.getStartDate() != null){
			whereClause.append(" AND tradeDate >= '").append(new DateTime(searcher.getStartDate()).toString("yyyy-MM-dd 00:00:00")).append("'");
		}
		if(searcher.getEndDate() != null){
			whereClause.append(" AND tradeDate <= '").append(new DateTime(searcher.getEndDate()).toString("yyyy-MM-dd 23:59:59")).append("'");
		}
		if(searcher.getMinSum() != null){
			whereClause.append(" AND sum >= ").append(searcher.getMinSum());
		}
		if(searcher.getMaxSum() != null){
			whereClause.append(" AND sum <= ").append(searcher.getMaxSum());
		}
		if(StringUtils.isNotBlank(searcher.getKey())){
			whereClause.append(" AND code = '").append(searcher.getKey()).append("'");
		}
		if(StringUtils.isNotBlank(searcher.getMobile())){
			whereClause.append(" AND mobile like '%").append(searcher.getMobile()).append("'");
		}
		if(searcher.getGatewayAccountType() != null){
			whereClause.append(" AND gatewayType = '").append(searcher.getGatewayAccountType()).append("'");
		}
		return whereClause.toString();
	}

	@Override
	@Transactional
	public Result paymentForPOS(SystemParam systemParam,
			POSPaymentRequestParam posPaymentParam, String orderNumber, POSGatewayAccount account) {
		String orderType = StringUtils.upperCase(posPaymentParam.getOrderType());
		
		//支付前检查订单状态
		Payment paymentStatus = checkOrderForPayment(orderNumber, posPaymentParam);
		if (paymentStatus == Payment.FIRST_PAYMENT){ //首次支付
			
			if (StringUtils.equals(orderType, "XPOS_ORDER")){
                //删除实体券
                clearPhysicalCouponsByOrderNumber(orderNumber);
				//添加实体券优惠
				if (insertPhysicalCoupons(orderNumber, posPaymentParam, systemParam.getMid())){
					return new Result(Payment.INVALID_PHYSICAL_COUPON.getName(), Payment.INVALID_PHYSICAL_COUPON.getCode()); //实体券如果不在商户可用则终止此次支付
				}
                //清空会员折扣
                clearMemberDiscountByOrderNumber(orderNumber);
				//添加会员折扣
				insertMemberDiscountToOrder(orderNumber, posPaymentParam, systemParam.getMid());

                //更新订单的实付金额（订单总金额*折扣-实体优惠券金额）
                updateOrderActualAmount(orderNumber);
			}
			
			//添加刷卡支付流水记录
			Shop shop = shopService.findShopByIdIgnoreVisible(systemParam.getMid());
			//创建支付流水
			POSTransaction po = new POSTransaction();
			po.setBusiness(shop);
			po.setOrderCodeByType(orderNumber, posPaymentParam.getOrderType());
			po.setGatewayAccount(account.getAccount());
			po.setGatewayType(account.getType());
			po.setSum(posPaymentParam.getAmount());
			po.setCode(posPaymentParam.getSerial());
			po.setStatus(POSTransactionStatus.UNPAID);
			po.setType(POSTransactionType.BANK_CARD);
			
			Result result = null;
			if(savePOSTransaction(po)){
				result = new Result(Payment.POS_CREATE_SERIAL_SUCCESS.getName(), Payment.POS_CREATE_SERIAL_SUCCESS.getCode());
				result.setResult(po.getCode());
			}else{
				result = new Result(Payment.POS_CREATE_SERIAL_FAILED.getName(), Payment.POS_CREATE_SERIAL_FAILED.getCode());
				// throw new RuntimeException(); //FIXME 创建流水失败，抛出异常回滚，但是payment具体错误信息无法传递
			}
			
			//更新支付方式
			updateOrderChargeChannel(orderType, orderNumber,
					GlobalSource.getIDByName(GlobalSource.metaPrepaidCardChargeChannelList,"刷卡"));
			

			return result;
		} else if(paymentStatus == Payment.NOT_FIRST_PAYMENT){ //非首次支付
			
			//添加刷卡支付流水记录
			Shop shop = shopService.findShopByIdIgnoreVisible(systemParam.getMid());
			//创建支付流水
			POSTransaction po = new POSTransaction();
			po.setBusiness(shop);
			po.setOrderCodeByType(orderNumber, posPaymentParam.getOrderType());
			po.setGatewayAccount(account.getAccount());
			po.setGatewayType(account.getType());
			po.setSum(posPaymentParam.getAmount());
			po.setCode(posPaymentParam.getSerial());
			po.setStatus(POSTransactionStatus.UNPAID);
			po.setType(POSTransactionType.BANK_CARD);
			
			Result result = null;
			if(savePOSTransaction(po)){
				result = new Result(Payment.POS_CREATE_SERIAL_SUCCESS.getName(), Payment.POS_CREATE_SERIAL_SUCCESS.getCode());
				result.setResult(po.getCode());
			}else{
				result = new Result(Payment.POS_CREATE_SERIAL_FAILED.getName(), Payment.POS_CREATE_SERIAL_FAILED.getCode());
				// throw new RuntimeException(); //FIXME 创建流水失败，抛出异常回滚，但是payment具体错误信息无法传递
			}
			

			return result;
		} else {
			return new Result(paymentStatus.getName(), paymentStatus.getCode()); //非成功
		}
		
	}
	
	@Override
	public Result uploadSignature(SystemParam systemParam, POSSignatureUploadParam signatureParam, 
												String orderNumber, POSGatewayAccount account) {
		POSTransaction po = findTransactionByCode(signatureParam.getSerial());
		if(po == null
			|| !po.getBusinessId().equals(systemParam.getMid())){
			return new Result(Payment.POS_UPLOAG_SIGNATURE_FAILED.getName(), Payment.POS_UPLOAG_SIGNATURE_FAILED.getCode());
		}else if(StringUtils.isBlank(signatureParam.getContent())){
			return new Result(Payment.POS_UPLOAG_SIGNATURE_CONTENT_EMPTY.getName(), Payment.POS_UPLOAG_SIGNATURE_CONTENT_EMPTY.getCode());
		}
		
		try{
			byte[] picBinary = Base64.decodeBase64(signatureParam.getContent());
			String uploadURL = StringUtils.join("/order/", po.getGatewayType().name(), "_signature/", po.getGatewayAccount(), "/" ,po.getCode(), ".png");
			boolean res = upYunService.uploadImg(uploadURL, picBinary);
			if(res){
				return new Result(Payment.POS_UPLOAG_SIGNATURE_SUCCESS.getName(), Payment.POS_UPLOAG_SIGNATURE_SUCCESS.getCode());
			}
		}catch(Exception e){
			logger.error("POS接口上传客户签字图片失败", e);
		}
		return new Result(Payment.POS_UPLOAG_SIGNATURE_FAILED.getName(), Payment.POS_UPLOAG_SIGNATURE_FAILED.getCode());
	}

	@Override
	public Result processPaymentResultCallback(SystemParam systemParam, POSPaymentResultParam paymentResultParam,
												String orderNumber, String serial, POSGatewayAccount account) {
		//validate sign
		if(!verify(paymentResultParam.toString(), paymentResultParam.getSign(), account.getSignKey())){
			return new Result(Payment.POS_INVALID_SIGNATURE.getName(), Payment.POS_INVALID_SIGNATURE.getCode());
		}
		
		POSTransaction po = findTransactionByCode(serial);
		if(po == null
			|| !po.getBusinessId().equals(systemParam.getMid())){
			return new Result(Payment.POS_STATUS_UPDATE_FAILED.getName(), Payment.POS_STATUS_UPDATE_FAILED.getCode());
		}
		
		if(StringUtils.equals("00", paymentResultParam.getResponseCode())){
			po.setStatus(POSTransactionStatus.PAID_SUCCESS);
			po.setCardNumber(paymentResultParam.getCardNumber());
			po.setResponseCode(paymentResultParam.getResponseCode());
			po.setTerminal(paymentResultParam.getTerminalId());
			po.setLocation(paymentResultParam.getLocation());
			po.setAuthCode(paymentResultParam.getTransAuno());
			po.setRefNo(paymentResultParam.getReferenceNumber());
			po.setBatchNo(paymentResultParam.getBatchNo());
			po.setTraceNo(paymentResultParam.getTransNo());
			po.setCardOrg(paymentResultParam.getCardOrg());
			po.setIssueCode(paymentResultParam.getIssueCode());
			po.setIssueName(paymentResultParam.getIssueName());
			po.setRemark(paymentResultParam.toString());
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
			Date tradeDate = null;
			try {
				tradeDate = sdf.parse(StringUtils.join(paymentResultParam.getTransDate() + paymentResultParam.getTransTime()));
			} catch (ParseException e) {
				tradeDate = new Date();
			}
			po.setTradeDate(tradeDate);
		}else{ //非00均为失败
			po.setStatus(POSTransactionStatus.PAID_FAIL);
			po.setRemark(paymentResultParam.toString());
			po.setResponseCode(paymentResultParam.getResponseCode() + "(" + paymentResultParam.getResponseDesc() + ")");
		}
		
		if(updatePOSTransactionByCode(po)){
            //pos支付成功后更新订单支付状态
            if (po.getStatus().equals(POSTransactionStatus.PAID_SUCCESS)){
                updateOrderPaymentStatus(orderNumber, paymentResultParam.getOrderType());
            }
			return new Result(Payment.POS_STATUS_UPDATE_SUCCESS.getName(), Payment.POS_STATUS_UPDATE_SUCCESS.getCode());
		}else{
			return new Result(Payment.POS_STATUS_UPDATE_FAILED.getName(), Payment.POS_STATUS_UPDATE_FAILED.getCode());
		}
	}

	@Override
	public Result processRefundResultCallback(SystemParam systemParam, POSRefundResultParam refundResultParam,
												String orderNumber, String serial, POSGatewayAccount account) {
		
		//validate sign
		if(!verify(refundResultParam.toString(), refundResultParam.getSign(), account.getSignKey())){
			return new Result(Refund.OTHER.getName(), Refund.OTHER.getCode());
		}
		
		//获取支付流水
		POSTransaction transaction = findTransactionByCode(serial);
		if(transaction == null
			|| !transaction.getBusinessId().equals(systemParam.getMid())){
			return new Result(Refund.NON_TRANSACTION.getName(), Refund.NON_TRANSACTION.getCode());
		}
		
		String orderType = null;
		if (transaction.getOrderNumber() != null) {
			orderType = "XPOS_ORDER";
		} else if (transaction.getThirdOrderCode() != null) {
			orderType = "THIRD_ORDER";
		} else if (transaction.getPrepaidCardChargeOrderCode() != null) {
			orderType = "XPOS_PREPAID";
		}

		if (!(StringUtils.equalsIgnoreCase(refundResultParam.getOrderType(), orderType) &&
				StringUtils.equalsIgnoreCase(orderNumber, transaction.getOrderNumber()))) {
			return new Result(Refund.TRANSACTION_UNMATCHED.getName(), Refund.TRANSACTION_UNMATCHED.getCode()); //订单和支付不匹配
		}
		
		//检查订单状态是否可退
		Boolean availableOrderRefund = checkAvailableRefund(orderNumber,orderType);
		if (!availableOrderRefund){
			return new Result(Refund.ORDER_UNABLE_REFUND.getName(), Refund.ORDER_UNABLE_REFUND.getCode());
		}

		//退款操作
		if(StringUtils.equals("00", refundResultParam.getResponseCode())){ //非00均为失败，不做处理
			transaction.setStatus(POSTransactionStatus.PAID_REFUND);
			transaction.setResponseCode(refundResultParam.getResponseCode());
			transaction.setRemark(refundResultParam.toString());
		}else{
			return new Result(Refund.REFUND_TRANSACTION_FAILED.getName(), Refund.REFUND_TRANSACTION_FAILED.getCode());
		}
		
		if(!updatePOSTransactionByCode(transaction)){
			return new Result(Refund.REFUND_TRANSACTION_FAILED.getName(), Refund.REFUND_TRANSACTION_FAILED.getCode());
		}
		
		if (StringUtils.equals(orderType, "XPOS_ORDER")){
			//更新订单的实付金额（订单总金额*折扣-实体优惠券金额）
			updateOrderActualAmount(orderNumber);
		}

		//更新订单退款状态
		updateOrderRefundStatus(orderNumber, orderType);
		
		return new Result(Refund.SUCCESS.getName(), Refund.SUCCESS.getCode());
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

	@Override
	public Result updateConsumerMobile(SystemParam systemParam,
									POSUpdateInfoParam updateInfoParam, String orderNumber, String serial) {
		POSTransaction po = findTransactionByCode(serial);
		if(po == null
			|| !po.getBusinessId().equals(systemParam.getMid())
			|| StringUtils.isBlank(updateInfoParam.getMobile())){
			return new Result(Payment.POS_STATUS_UPDATE_FAILED.getName(), Payment.POS_STATUS_UPDATE_FAILED.getCode());
		}else if(StringUtils.equals(po.getMobile(), updateInfoParam.getMobile())){
			return new Result(Payment.POS_STATUS_UPDATE_SUCCESS.getName(), Payment.POS_STATUS_UPDATE_SUCCESS.getCode());
		}
		
		po.setMobile(updateInfoParam.getMobile());
		
		if(updatePOSTransaction(po)){
			//手机号更新成功后，发送电子账单
			String url = "http://xka.me/ebill/"+serial; //电子账单地址
			Shop shop = shopService.findShopByIdIgnoreVisible(po.getBusinessId());
			StringBuffer content = new StringBuffer();
			content.append("您于").append(new DateTime(po.getTradeDate()).toString("MM月dd日HH时mm分")).append("在 ").append(shop.getName())
			.append(" 消费人民币：").append(po.getSum()).append("元，查看账单详情：").append(url);
			SMS sms = new SMS();
			sms.setMobile(updateInfoParam.getMobile());
			sms.setMessage(content.toString());
			smsService.sendSMSAndDeductions(shop.getId() ,BusinessType.SHOP,sms,null,"发送消费电子账单短信" );
			return new Result(Payment.POS_STATUS_UPDATE_SUCCESS.getName(), Payment.POS_STATUS_UPDATE_SUCCESS.getCode());
		}
		return new Result(Payment.POS_STATUS_UPDATE_FAILED.getName(), Payment.POS_STATUS_UPDATE_FAILED.getCode());
	}

}
