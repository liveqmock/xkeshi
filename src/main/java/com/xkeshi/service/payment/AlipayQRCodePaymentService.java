package com.xkeshi.service.payment;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alipay.config.FactoryConfig;
import com.alipay.config.PaySourceConfig.PaySource;
import com.alipay.entity.QRCodeCancelXML;
import com.alipay.entity.QRCodeCancelXML.QRCodeCancelResponse;
import com.alipay.entity.QRCodeCancelXML.QRCodeCancelResponse.QRCodeCancelErrorCode;
import com.alipay.entity.QRCodeCancelXML.QRCodeCancelResponse.QRCodeCancelResultCode;
import com.alipay.entity.QRCodePaymentNotify;
import com.alipay.entity.QRCodePaymentNotify.QRCodePaymentNotifyTradeStatus;
import com.alipay.entity.QRCodePaymentXML;
import com.alipay.entity.QRCodePaymentXML.QRCodePaymentResponse;
import com.alipay.entity.QRCodePaymentXML.QRCodePaymentResponse.QRCodePaymentResultCode;
import com.alipay.entity.QRCodeQueryXML;
import com.alipay.entity.QRCodeQueryXML.QRCodeQueryResponse;
import com.alipay.entity.QRCodeQueryXML.QRCodeQueryResponse.QRCodeQueryResultCode;
import com.alipay.entity.QRCodeQueryXML.QRCodeQueryResponse.QRCodeQueryTradeStatus;
import com.alipay.entity.QRCodeRefundXML;
import com.alipay.entity.QRCodeRefundXML.QRCodeRefundResponse;
import com.alipay.entity.QRCodeRefundXML.QRCodeRefundResponse.QRCodeRefundResultCode;
import com.alipay.util.AlipayCore;
import com.alipay.util.AlipayNotify;
import com.alipay.util.AlipaySubmit;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.mapper.MapperWrapper;
import com.xkeshi.common.em.Payment;
import com.xkeshi.common.em.Refund;
import com.xkeshi.common.em.TransactionPaymentStatus;
import com.xkeshi.common.globality.GlobalSource;
import com.xkeshi.pojo.po.alipay.AlipayTransaction;
import com.xkeshi.pojo.vo.Result;
import com.xkeshi.pojo.vo.SystemParam;
import com.xkeshi.pojo.vo.param.AlipayQRCodeCancelParam;
import com.xkeshi.pojo.vo.param.AlipayQRCodePaymentParam;
import com.xkeshi.pojo.vo.param.AlipayQRCodeQueryParam;
import com.xkeshi.pojo.vo.param.AlipayQRCodeRefundParam;
import com.xkeshi.service.AlipayTransactionService;
import com.xpos.common.entity.Shop;
import com.xpos.common.entity.pos.POSGatewayAccount;
import com.xpos.common.entity.pos.POSGatewayAccount.POSGatewayAccountType;
import com.xpos.common.persistence.mybatis.AlipayTransactionMapper;
import com.xpos.common.service.OrderService;
import com.xpos.common.service.POSGatewayAccountService;
import com.xpos.common.service.ShopService;
import com.xpos.common.utils.DateUtil;
import com.xpos.common.utils.UUIDUtil;

/**
 * 支付宝线下条码/二维码支付
 * @author chengj
 */
@Service
public class AlipayQRCodePaymentService extends PaymentService {
	private static Logger logger = Logger.getLogger(AlipayQRCodePaymentService.class);
	private final static Map<String, Payment> DETAIL_ERROR_MAP = new HashMap<>();
	
	static{
		DETAIL_ERROR_MAP.put("TRADE_HAS_SUCCESS", Payment.ALIPAY_TRADE_HAS_SUCCESS);
		DETAIL_ERROR_MAP.put("TRADE_HAS_CLOSE", Payment.ALIPAY_TRADE_HAS_CLOSE);
		DETAIL_ERROR_MAP.put("REASON_ILLEGAL_STATUS", Payment.ALIPAY_REASON_ILLEGAL_STATUS);
		DETAIL_ERROR_MAP.put("BUYER_ENABLE_STATUS_FORBID", Payment.ALIPAY_BUYER_ENABLE_STATUS_FORBID);
		DETAIL_ERROR_MAP.put("BUYER_PAYMENT_AMOUNT_DAY_LIMIT_ERROR", Payment.ALIPAY_BUYER_PAYMENT_AMOUNT_DAY_LIMIT_ERROR);
		DETAIL_ERROR_MAP.put("CLIENT_VERSION_NOT_MATCH", Payment.ALIPAY_CLIENT_VERSION_NOT_MATCH);
		DETAIL_ERROR_MAP.put("SOUNDWAVE_PARSER_FAIL", Payment.ALIPAY_SOUNDWAVE_PARSER_FAIL);
		DETAIL_ERROR_MAP.put("PULL_MOBILE_CASHIER_FAIL", Payment.ALIPAY_PULL_MOBILE_CASHIER_FAIL);
	}
	
	private final String ALIPAY_ACQUIRE_CREATEANDPAY_SERVICE = "alipay.acquire.createandpay";
	private final String ALIPAY_ACQUIRE_QUERY_SERVICE = "alipay.acquire.query";
	private final String ALIPAY_ACQUIRE_CANCEL_SERVICE = "alipay.acquire.cancel";
	private final String ALIPAY_ACQUIRE_REFUND_SERVICE = "alipay.acquire.refund";
	private final String ALIPAY_ACQUIRE_CREATEANDPAY_NOTIFY_URL = "http://vipoffline.xkeshi.com/api/alipay/payment/qrcode_notify";
	private final String ALIPAY_ACQUIRE_CREATEANDPAY_TIMEOUT = "10m"; //订单超时时间
	private final PaySource PAYSOURCE = PaySource.XKESHI_ALIPAY_DIRECT;
	
	@Autowired
	private AlipayTransactionService alipayTransactionService;
	@Autowired
	private ShopService shopService;
	@Autowired
	private POSGatewayAccountService gatewayAccountService;
	@Autowired
	private OrderService orderService;
	@Autowired
	private AlipayTransactionMapper alipayTransactionMapper;
	
	@Transactional
	public Result paymentForAlipayQRCode(AlipayQRCodePaymentParam paymentParam, SystemParam systemParam, String orderNumber, POSGatewayAccount account){
		String orderType = paymentParam.getOrderType();
        Long shopId = systemParam.getMid();

        //支付前检查订单状态
        Payment paymentStatus = checkOrderForPayment(orderNumber, paymentParam);
        if (paymentStatus == Payment.FIRST_PAYMENT){ //首次支付

            if (StringUtils.equals(orderType, "XPOS_ORDER")){
                //删除实体券
                clearPhysicalCouponsByOrderNumber(orderNumber);

				//添加实体券优惠
				if (insertPhysicalCoupons(orderNumber, paymentParam, shopId)){
					paymentStatus = Payment.INVALID_PHYSICAL_COUPON; //实体券如果不在商户可用则终止此次支付
					return new Result(paymentStatus.getName(), paymentStatus.getCode());
				}

                //清空会员折扣
                clearMemberDiscountByOrderNumber(orderNumber);
				//添加会员折扣
				insertMemberDiscountToOrder(orderNumber, paymentParam, shopId);

                //更新订单的实付金额（订单总金额*折扣-实体优惠券金额）
                updateOrderActualAmount(orderNumber);
			}
			
			//添加支付宝扫码支付流水记录
			Result result = insertAlipayQRCodeTransaction(paymentParam, systemParam, orderNumber, account);
			if(StringUtils.equals(Payment.SUCCESS.getName(), result.getDescription())){
				//更新支付方式
				updateOrderChargeChannel(orderType, orderNumber,
						GlobalSource.getIDByName(GlobalSource.metaPrepaidCardChargeChannelList,"支付宝"));
				
				//更新订单状态
                updateOrderPaymentStatus(orderNumber, orderType);


                return result;
			}else if(StringUtils.equals(Payment.ALIPAY_UNKNOWN_STATUS.getName(), result.getDescription())){
				return result;
			}else{
				throw new RuntimeException(); //FIXME 创建流水失败，抛出异常回滚，但是payment具体错误信息无法传递
			}
		} else if(paymentStatus == Payment.NOT_FIRST_PAYMENT){ //非首次支付
			
			//添加支付宝扫码支付流水记录
			Result result = insertAlipayQRCodeTransaction(paymentParam, systemParam, orderNumber, account);
			if(StringUtils.equals(Payment.SUCCESS.getName(), result.getDescription())){
				
				//更新订单状态
                updateOrderPaymentStatus(orderNumber, orderType);


                return result;
			}else if(StringUtils.equals(Payment.ALIPAY_UNKNOWN_STATUS.getName(), result.getDescription())){
				return result;
			}else{
				throw new RuntimeException(); //FIXME 创建流水失败，抛出异常回滚，但是payment具体错误信息无法传递
			}
		} else {
			return new Result(paymentStatus.getName(), paymentStatus.getCode()); //非成功
		}
		
	}
	
	private Result insertAlipayQRCodeTransaction(AlipayQRCodePaymentParam paymentParam, SystemParam systemParam, String orderNumber, POSGatewayAccount account){
		Result result = new Result();
		Payment payment = null;
		
		String orderType = paymentParam.getOrderType();
		//记录支付流水
		AlipayTransaction po = new AlipayTransaction();
		po.setOrderCodeByType(orderNumber, orderType);
		po.setSellerAccount(account.getAccount());
		po.setAmount(paymentParam.getAmount());
		po.setAlipayPaymentStatus(TransactionPaymentStatus.UNPAID.getValue());
		po.setSerial(paymentParam.getSerial());
		po.setDeviceNumber(systemParam.getDeviceNumber());
		if(alipayTransactionMapper.insert(po) <= 0){
			payment = Payment.ALIPAY_CREATE_SERIAL_FAILED;
			result.setRes(payment.getCode());
			result.setDescription(payment.getName());
			return result;
		}
		
		String responseText = null;
		try {
			//提交到支付宝平台
			responseText = submitAndPay(paymentParam, systemParam, po.getSerial(), account);
		} catch (Exception e) {
			payment = Payment.ALIPAY_SUBMIT_ORDER_TO_PLATFORM_FAILED;
			result.setRes(payment.getCode());
			result.setDescription(payment.getName());
			return result;
		}
		
		try {
			payment = processSubmitAndPayCallback(responseText, account, po);
			result.setRes(payment.getCode());
			result.setDescription(payment.getName());
			result.setResult(po.getSerial());
			return result;
		} catch (Exception e) {
			payment = Payment.ALIPAY_PLATFORM_CREATE_ORDER_FAILED;
			result.setRes(payment.getCode());
			result.setDescription(payment.getName());
			return result;
		}
	}
	
	/** 功能：构造URL并调用接口创建支付宝订单 */
	private String submitAndPay(AlipayQRCodePaymentParam paymentParam, SystemParam systemParam, String serial, POSGatewayAccount account) throws Exception {
		Shop shop = shopService.findShopByIdIgnoreVisible(systemParam.getMid());

		Map<String, String> params = new HashMap<>();
		params.put("service", ALIPAY_ACQUIRE_CREATEANDPAY_SERVICE);
		params.put("partner", account.getAccount());
		params.put("_input_charset", FactoryConfig.getPayConfig(PAYSOURCE).getCharSet());
		params.put("notify_url", ALIPAY_ACQUIRE_CREATEANDPAY_NOTIFY_URL);
		params.put("out_trade_no", serial);
		params.put("subject", shop.getName()+"-线下扫码支付订单"); //订单标题
		params.put("product_code", "BARCODE_PAY_OFFLINE"); //扫码支付
		params.put("total_fee", paymentParam.getAmount().toString());
		params.put("extend_params", "{\"AGENT_ID\":\"11887025h1\"}");
		params.put("show_url","v2.xkeshi.com/shop/"+shop.getId());
		params.put("it_b_pay", ALIPAY_ACQUIRE_CREATEANDPAY_TIMEOUT); //设置超时（考虑到网络以及余额等问题）
		params.put("dynamic_id_type", "qr_code");
		params.put("dynamic_id", paymentParam.getDynamicCode());
		params.put("sign_key", account.getSignKey());
		
		String responseText = AlipaySubmit.buildRequest(null, "", "", params, PAYSOURCE);
		
		logger.debug(responseText);
		
		return responseText;
	}
	
	/** 处理统一下单支付请求的同步返回结果 */
	private Payment processSubmitAndPayCallback(String responseText, POSGatewayAccount account, AlipayTransaction alipayTransaction) throws Exception {
		Object obj = parseXml(responseText, QRCodePaymentXML.class);
		if(obj != null){
			QRCodePaymentXML payment = (QRCodePaymentXML)obj;
			boolean isSuccess = payment.isSuccess();
			if(!isSuccess){
				logger.error("线下扫码支付调用支付宝【统一下单并支付】接口异常，error=["+payment.getError()+"]，serial=["+alipayTransaction.getSerial()+"]");
				throw new Exception();
			}
			
			Map<String, String> param = payment.getResponseParam();
			param.put("sign", payment.getSign());
			param.put("sign_type", payment.getSign_type());
			param.put("sign_key", account.getSignKey());
			if(!verifySign(param, PAYSOURCE)){ //校验sign
				logger.error("线下扫码支付调用支付宝【统一下单并支付】接口异常，校验签名异常。serial=["+alipayTransaction.getSerial()+"]");
				throw new Exception();
			}
			
			QRCodePaymentResponse response = payment.getResponse();
			QRCodePaymentResultCode resultCode = response.getResult_code();
			
			if(QRCodePaymentResultCode.ORDER_FAIL.equals(resultCode)
					|| QRCodePaymentResultCode.ORDER_SUCCESS_PAY_FAIL.equals(resultCode)){ //下单失败 或者 下单成功但支付失败
				//update AlipayTransaction
				if(QRCodePaymentResultCode.ORDER_FAIL.equals(resultCode)){
					alipayTransaction.setResponseCode(QRCodePaymentResultCode.ORDER_FAIL.name());
				}else if(QRCodePaymentResultCode.ORDER_SUCCESS_PAY_FAIL.equals(resultCode)){
					alipayTransaction.setResponseCode(QRCodePaymentResultCode.ORDER_SUCCESS_PAY_FAIL.name());
				}
				alipayTransaction.setAlipaySerial(response.getTrade_no());
				alipayTransaction.setBuyerId(response.getBuyer_user_id());
				alipayTransaction.setBuyerAccount(response.getBuyer_logon_id());
				if(StringUtils.isNotBlank(responseText)){
					alipayTransaction.setComment(alipayTransaction.getComment() + "|||" + responseText);
				}
				alipayTransaction.setAlipayPaymentStatus(TransactionPaymentStatus.FAILED.getValue());
				alipayTransactionService.updateById(alipayTransaction);
				return DETAIL_ERROR_MAP.get(response.getDetail_error_code().toString());
			}else if(QRCodePaymentResultCode.ORDER_SUCCESS_PAY_SUCCESS.equals(resultCode)){ //下单成功并且支付成功
				alipayTransaction.setResponseCode(QRCodePaymentResultCode.ORDER_SUCCESS_PAY_SUCCESS.name());
				alipayTransaction.setAlipaySerial(response.getTrade_no());
				alipayTransaction.setBuyerId(response.getBuyer_user_id());
				alipayTransaction.setBuyerAccount(response.getBuyer_logon_id());
				if(StringUtils.isNotBlank(responseText)){
					alipayTransaction.setComment(alipayTransaction.getComment() + "|||" + responseText);
				}
				alipayTransaction.setAlipayPaymentStatus(TransactionPaymentStatus.SUCCESS.getValue());
				alipayTransaction.setTradeTime(DateUtil.getDateFormatter(response.getGmt_payment()));
				alipayTransactionService.updateById(alipayTransaction);
				return Payment.SUCCESS;
			}else if(QRCodePaymentResultCode.ORDER_SUCCESS_PAY_INPROCESS.equals(resultCode)
					|| QRCodePaymentResultCode.UNKNOWN.equals(resultCode)){ //支付处理中、处理结果未知
				//不做任何修改，需PAD端调用查询接口确认
				alipayTransaction.setAlipayPaymentStatus(TransactionPaymentStatus.UNPAID.getValue());
				alipayTransactionService.updateById(alipayTransaction);
				return Payment.ALIPAY_UNKNOWN_STATUS;
			}
		}
		return Payment.ALIPAY_UNKNOWN_STATUS;
	}
	
	//解析XML，封装成指定的对象
	private Object parseXml(String responseText, Class<?> clz){
		if(StringUtils.isBlank(responseText)){
			return null;
		}
		
		//parse XML
		StringBuilder sb = new StringBuilder(responseText);
		sb.delete(0, sb.indexOf("<alipay>"));
		String xml = StringUtils.remove(sb.toString(), "<alipay>");
		xml = StringUtils.remove(xml, "</alipay>");
		//repeat <resposne> node
		String responseContent = xml.substring(xml.indexOf("<response>")+10, xml.indexOf("</response>"))
								.replaceAll("</\\w+>", "")
								.replaceAll("<", "##")
								.replaceAll(">", "||");
		xml = "<" + clz.getSimpleName() + ">"+ xml + "<responseStr>" + responseContent + "</responseStr></" + clz.getSimpleName() + ">";
		
		XStream xstream = new XStream() {
			// to enable ignoring of unknown elements
			@Override
			protected MapperWrapper wrapMapper(MapperWrapper next) {
				return new MapperWrapper(next) {
					@Override
					public boolean shouldSerializeMember(@SuppressWarnings("rawtypes") Class definedIn, String fieldName) {
						if (definedIn == Object.class) {
							try {
								return this.realClass(fieldName) != null;
							} catch (Exception e) {
								return false;
							}
						}
						return super.shouldSerializeMember(definedIn, fieldName);
					}
				};
			}
		};
		xstream.alias(clz.getSimpleName(), clz);
		xstream.alias("TradeFundBill", com.alipay.entity.TradeFundBill.class);
		return xstream.fromXML(xml);
	}
	
	//校验签名
	private boolean verifySign(Map<String, String> sParaTemp, PaySource paySource){
		//除去数组中的空值和签名参数
		Map<String, String> sPara = AlipayCore.paraFilter(sParaTemp);
		//生成签名结果
		String mysign = AlipaySubmit.buildRequestMysign(sPara,paySource);
		return StringUtils.equalsIgnoreCase(mysign, sParaTemp.get("sign"));
	}

	/** 处理统一下单支付请求的异步返回通知 */
	public boolean processSubmitAndPayNotify(QRCodePaymentNotify notify, String body) {
		try{
			
			//query AlipayTransaction & POSGatewayAccount & orderNumber/orderType
			String signKey = null;
			AlipayTransaction po = alipayTransactionMapper.selectBySerial(notify.getOut_trade_no());
			String orderNumber = null;
			String orderType = null;
			if(po == null){
				logger.error("支付宝线下扫码支付【统一下单并支付】接口异步回调异常，根据code未找到相应订单");
				return false;
			}else{
				POSGatewayAccount buyerAccount = gatewayAccountService.findByAccountAndType(po.getSellerAccount(), POSGatewayAccountType.ALIPAY);
				if(buyerAccount == null){
					logger.error("支付宝线下扫码支付【统一下单并支付】接口异步回调异常，buyerAccount = [" + po.getSellerAccount() + "]未找到对应的POSGatewayAccount");
					return false;
				}else{
					signKey = buyerAccount.getSignKey();
				}
				
				if(StringUtils.isNotBlank(po.getOrderNumber())){
					orderNumber = po.getOrderNumber();
					orderType = "XPOS_ORDER";
				}else if(StringUtils.isNotBlank(po.getPrepaidCardChargeOrderCode())){
					orderNumber = po.getPrepaidCardChargeOrderCode();
					orderType = "XPOS_PREPAID";
				}else if(StringUtils.isNotBlank(po.getThirdOrderCode())){
					orderNumber = po.getThirdOrderCode();
					orderType = "THIRD_ORDER";
				}
			}
			
			//verify
			SortedMap<String, String> params = new TreeMap<>();
			params.put("partner", notify.getSeller_id());
			params.put("sign_key", signKey);
			
			String[] nameValuePairs = StringUtils.split(body, "&");
			for(String str : nameValuePairs){
				String[] nameValuePair = StringUtils.split(str, "=");
				if(nameValuePair != null && nameValuePair.length > 0){
					String key = nameValuePair[0];
					String value = nameValuePair[1];
					params.put(key, value);
				}
			}
			boolean verifyNotify = AlipayNotify.verify(params);
			if(verifyNotify){
				QRCodePaymentNotifyTradeStatus tradeStatus = notify.getTrade_status();
				if(QRCodePaymentNotifyTradeStatus.WAIT_BUYER_PAY.equals(tradeStatus)){
					//交易创建，等待买家付款
					po.setAlipayPaymentStatus(TransactionPaymentStatus.UNPAID.getValue());
					alipayTransactionMapper.updateById(po);
					return true; //不做修改，消费通知消息
				}else if(QRCodePaymentNotifyTradeStatus.TRADE_CLOSED.equals(tradeStatus)){
					TransactionPaymentStatus curStatus = TransactionPaymentStatus.findByValue(po.getAlipayPaymentStatus());
					if(TransactionPaymentStatus.UNPAID.equals(curStatus)){ //1.在指定时间段内未支付时关闭的交易；
						po.setAlipayPaymentStatus(TransactionPaymentStatus.FAILED.getValue());
					}else if(TransactionPaymentStatus.SUCCESS.equals(curStatus)){ //2. 在交易完成全额退款成功时关闭的交易。
						po.setAlipayPaymentStatus(TransactionPaymentStatus.REFUND.getValue());
					}else if(TransactionPaymentStatus.FAILED.equals(curStatus) 
							|| TransactionPaymentStatus.REFUND.equals(curStatus)
							|| TransactionPaymentStatus.REVOCATION.equals(curStatus)){ //数据库已修改，重复通知
						return true; //不做修改，消费通知消息
					}
					po.setResponseCode(QRCodePaymentNotifyTradeStatus.TRADE_CLOSED.name());
					po.setAlipaySerial(notify.getTrade_no());
					po.setBuyerId(notify.getBuyer_id());
					po.setBuyerAccount(notify.getBuyer_email());
					po.setComment(po.getComment() + "|||" + body);
					po.setTradeTime(notify.getGmt_payment());
					return alipayTransactionService.updateById(po);
				}else if(QRCodePaymentNotifyTradeStatus.TRADE_SUCCESS.equals(tradeStatus) //交易成功，且可对该交易做操作，如：多级分润、退款等。
						|| QRCodePaymentNotifyTradeStatus.TRADE_FINISHED.equals(tradeStatus) //交易成功且结束，即不可再做任何操作
						|| QRCodePaymentNotifyTradeStatus.TRADE_PENDING.equals(tradeStatus)){ //等待卖家收款（买家付款后，如果卖家账号被冻结）
					
					po.setResponseCode(tradeStatus.name());
					po.setAlipaySerial(notify.getTrade_no());
					po.setBuyerId(notify.getBuyer_id());
					po.setBuyerAccount(notify.getBuyer_email());
					po.setComment(po.getComment() + "|||" + body);
					po.setAlipayPaymentStatus(TransactionPaymentStatus.SUCCESS.getValue());
					po.setTradeTime(notify.getGmt_payment());
					boolean result = alipayTransactionService.updateById(po);
					if(QRCodePaymentNotifyTradeStatus.TRADE_PENDING.equals(tradeStatus)){
						//卖家账户异常
						logger.error("支付宝线下扫码支付【统一下单并支付】接口异步回调，通知[TRADE_PENDING]卖家账号异常，卖家账号：[" + po.getSellerAccount() + "]");
					}
					
					/*
					 * 更新支付渠道
					 * FIXME 因为是支付宝回调，无法判断是否首次支付。当前预付卡充值默认一次性支付完成，所以暂时采取每次收到消息都更新的方式
					 */
					updateOrderChargeChannel(orderType, orderNumber,
							GlobalSource.getIDByName(GlobalSource.metaPrepaidCardChargeChannelList,"支付宝"));

                    updateOrderPaymentStatus(orderNumber, orderType);
					return result;
				}
			}
		}catch(Exception e){
			logger.error("支付宝线下扫码支付【统一下单并支付】接口异步回调失败", e);
		}
		return false;
	}

	public Result queryPaymentResult(AlipayQRCodeQueryParam queryParam, SystemParam systemParam, String orderNumber, POSGatewayAccount account) {
		Result result = new Result();
		Payment payment = null;
		
		AlipayTransaction transaction = alipayTransactionMapper.selectBySerial(queryParam.getSerial());
		if(transaction == null || !StringUtils.equals(account.getAccount(), transaction.getSellerAccount())){ //未找到订单 或者 支付宝商户号不匹配
			payment = Payment.ALIPAY_ORDER_NOT_FOUND;
			result.setDescription(payment.getName());
			result.setRes(payment.getCode());
			return result;
		}
		
		String responseText = null;
		try {
			responseText = query(account.getSignKey(), transaction);
		} catch (Exception e) {
			payment = Payment.ALIPAY_QUERY_FAIL;
			result.setDescription(payment.getName());
			result.setRes(payment.getCode());
			return result;
		}
		
		try {
			payment = processQueryCallback(responseText, account.getSignKey(), transaction);
		} catch (Exception e) {
			payment = Payment.ALIPAY_QUERY_FAIL;
		}
		result.setRes(payment.getCode());
		result.setDescription(payment.getName());
		return result;
	}
	
	/** 发起查询指定订单的请求
	 * @return response响应内容
	 */
	private String query(String signKey, AlipayTransaction transaction) throws Exception {
		Map<String, String> params = new HashMap<>();
		params.put("service", ALIPAY_ACQUIRE_QUERY_SERVICE);
		params.put("partner", transaction.getSellerAccount());
		params.put("_input_charset", FactoryConfig.getPayConfig(PAYSOURCE).getCharSet());
		params.put("out_trade_no", transaction.getSerial());
		params.put("trade_no", transaction.getAlipaySerial());
		params.put("sign_key", signKey);
		
		String responseText = AlipaySubmit.buildRequest(null, "", "", params, PAYSOURCE);
		
		logger.debug(responseText);
		
		return responseText;
	}

	/** 处理订单查询请求的同步返回结果 */
	private Payment processQueryCallback(String responseText, String signKey, AlipayTransaction transaction) throws Exception {
		Object obj = parseXml(responseText, QRCodeQueryXML.class);
		if(obj != null){
			QRCodeQueryXML query = (QRCodeQueryXML)obj;
			boolean isSuccess = query.isSuccess();
			if(!isSuccess){
				logger.error("线下扫码支付调用支付宝【查询订单】接口异常，error=["+query.getError()+"]");
				throw new Exception("支付宝错误，暂时无法提供查询");
			}
			
			Map<String, String> param = query.getResponseParam();
			param.put("sign", query.getSign());
			param.put("sign_type", query.getSign_type());
			param.put("sign_key", signKey);
			if(!verifySign(param, PAYSOURCE)){ //校验sign
				logger.error("线下扫码支付调用支付宝【查询订单】接口异常，校验签名错误");
				throw new Exception("支付宝错误，暂时无法提供查询");
			}
			
			QRCodeQueryResponse response = query.getResponse();
			QRCodeQueryResultCode resultCode = response.getResult_code();
			if(QRCodeQueryResultCode.FAIL.equals(resultCode) //查询失败
					|| QRCodeQueryResultCode.PROCESS_EXCEPTION.equals(resultCode)){ //处理异常
				return Payment.ALIPAY_QUERY_FAIL;
			}else if(QRCodeQueryResultCode.SUCCESS.equals(resultCode)){ //查询成功
				QRCodeQueryTradeStatus tradeStatus = response.getTrade_status();
				if(QRCodeQueryTradeStatus.WAIT_BUYER_PAY.equals(tradeStatus)){
					//交易创建，等待买家付款
					transaction.setAlipayPaymentStatus(TransactionPaymentStatus.UNPAID.getValue());
					alipayTransactionService.updateById(transaction);
					return Payment.ALIPAY_WAIT_BUYER_PAY;
				}else if(QRCodeQueryTradeStatus.TRADE_CLOSED.equals(tradeStatus)){
					TransactionPaymentStatus curPaymentStatus = TransactionPaymentStatus.findByValue(transaction.getAlipayPaymentStatus());
					if(TransactionPaymentStatus.UNPAID.equals(curPaymentStatus)){ //1.在指定时间段内未支付时关闭的交易；
						transaction.setAlipayPaymentStatus(TransactionPaymentStatus.FAILED.getValue());
					}else if(TransactionPaymentStatus.SUCCESS.equals(curPaymentStatus)){ //2. 在交易完成全额退款成功时关闭的交易。
						transaction.setAlipayPaymentStatus(TransactionPaymentStatus.REFUND.getValue());
					}else if(TransactionPaymentStatus.FAILED.equals(curPaymentStatus) || TransactionPaymentStatus.REFUND.equals(curPaymentStatus)){ //重复查询，DB中状态已修改
						return Payment.ALIPAY_TRADE_HAS_CLOSE;
					}
					
					transaction.setResponseCode(QRCodeQueryTradeStatus.TRADE_CLOSED.name());
					transaction.setAlipaySerial(response.getTrade_no());
					transaction.setBuyerId(response.getBuyer_user_id());
					transaction.setBuyerAccount(response.getBuyer_logon_id());
					if(StringUtils.isNotBlank(responseText)){
						transaction.setComment(transaction.getComment() + "|||" + responseText);
					}
					if(StringUtils.isNotBlank(response.getSend_pay_date())){
						transaction.setTradeTime(DateUtils.parseDate(response.getSend_pay_date(), "yyyy-MM-dd HH:mm:ss"));
					}
					alipayTransactionService.updateById(transaction);
					return Payment.ALIPAY_TRADE_HAS_CLOSE;
				}else if(QRCodeQueryTradeStatus.TRADE_SUCCESS.equals(tradeStatus) //交易成功，且可对该交易做操作，如：多级分润、退款等。
						|| QRCodeQueryTradeStatus.TRADE_FINISHED.equals(tradeStatus) //交易成功且结束，即不可再做任何操作
						|| QRCodeQueryTradeStatus.TRADE_PENDING.equals(tradeStatus)){ //等待卖家收款（买家付款后，如果卖家账号被冻结）
					
					transaction.setResponseCode(tradeStatus.name());
					transaction.setAlipaySerial(response.getTrade_no());
					transaction.setBuyerId(response.getBuyer_user_id());
					transaction.setBuyerAccount(response.getBuyer_logon_id());
					if(StringUtils.isNotBlank(responseText)){
						transaction.setComment(transaction.getComment() + "|||" + responseText);
					}
					transaction.setAlipayPaymentStatus(TransactionPaymentStatus.SUCCESS.getValue());
					if(StringUtils.isNotBlank(response.getSend_pay_date())){
						transaction.setTradeTime(DateUtils.parseDate(response.getSend_pay_date(), "yyyy-MM-dd HH:mm:ss"));
					}
					alipayTransactionService.updateById(transaction);
					if(QRCodeQueryTradeStatus.TRADE_PENDING.equals(tradeStatus)){
						//卖家账户异常
						logger.error("支付宝线下扫码支付【统一下单并支付】接口异步回调，通知[TRADE_PENDING]卖家账号异常，卖家账号：["+transaction.getSellerAccount() + "]");
					}
					return Payment.ALIPAY_TRADE_HAS_SUCCESS;
				}
			}
		}
		return null;
	}
	
	@Transactional
	public Result cancelPayment(AlipayQRCodeCancelParam cancelParam, SystemParam systemParam, String orderNumber, POSGatewayAccount account) {
		Result result = new Result();
		Payment payment = null;
		
		AlipayTransaction transaction = alipayTransactionMapper.selectBySerial(cancelParam.getSerial());
		if(transaction == null || !StringUtils.equals(account.getAccount(), transaction.getSellerAccount())){ //未找到订单 或者 支付宝商户号不匹配
			payment = Payment.ALIPAY_ORDER_CANCEL_FAILED;
			result.setDescription(payment.getName());
			result.setRes(payment.getCode());
			return result;
		}
		
		String responseText = null;
		try {
			responseText = cancel(account.getSignKey(), transaction);
		} catch (Exception e) {
			payment = Payment.ALIPAY_ORDER_CANCEL_FAILED;
			result.setDescription(payment.getName());
			result.setRes(payment.getCode());
			return result;
		}
		
		try {
			payment = processCancelCallback(responseText, account.getSignKey(), transaction);
		} catch (Exception e) {
			payment = Payment.ALIPAY_ORDER_CANCEL_FAILED;
		}
		result.setRes(payment.getCode());
		result.setDescription(payment.getName());
		return result;
	}
	
	@Transactional
	public boolean cancelOfflinePayment(AlipayTransaction transaction, POSGatewayAccount account){
		if(!StringUtils.equals(account.getAccount(), transaction.getSellerAccount())){ //未找到订单 或者 支付宝商户号不匹配
			return false;
		}
		
		String responseText = null;
		try {
			responseText = cancel(account.getSignKey(), transaction);
		} catch (Exception e) {
			throw new RuntimeException();
		}
		
		try {
			Object obj = parseXml(responseText, QRCodeCancelXML.class);
			if(obj != null){
				QRCodeCancelXML cancel = (QRCodeCancelXML)obj;
				boolean isSuccess = cancel.isSuccess();
				if(!isSuccess){
					logger.error("线下扫码支付调用支付宝【撤销订单】接口异常，error=["+cancel.getError()+"]");
					throw new Exception("支付宝错误，无法撤销订单");
				}
				
				Map<String, String> param = cancel.getResponseParam();
				param.put("sign", cancel.getSign());
				param.put("sign_type", cancel.getSign_type());
				param.put("sign_key", account.getSignKey());
				if(!verifySign(param, PAYSOURCE)){ //校验sign
					logger.error("线下扫码支付调用支付宝【撤销订单】接口异常，校验签名错误");
					throw new Exception("支付宝错误，无法撤销订单");
				}
				
				QRCodeCancelResponse response = cancel.getResponse();
				QRCodeCancelResultCode resultCode = response.getResult_code();
				
				if(QRCodeCancelResultCode.SUCCESS.equals(resultCode)){ //处理成功
					return true;
				}
			}
			return false;
		} catch (Exception e) {
			throw new RuntimeException();
		}
	}
	
	/** 发起撤销指定订单的请求 */
	private String cancel(String signKey, AlipayTransaction transaction) throws Exception {
		Map<String, String> params = new HashMap<>();
		params.put("service", ALIPAY_ACQUIRE_CANCEL_SERVICE);
		params.put("partner", transaction.getSellerAccount());
		params.put("_input_charset", FactoryConfig.getPayConfig(PAYSOURCE).getCharSet());
		params.put("out_trade_no", transaction.getSerial());
		params.put("trade_no", transaction.getAlipaySerial());
		params.put("sign_key", signKey);
		
		String responseText = AlipaySubmit.buildRequest(null, "", "", params, PAYSOURCE);
		
		logger.debug(responseText);
		
		return responseText;
	}

	/** 处理订单撤销请求的同步返回结果 */
	private Payment processCancelCallback(String responseText, String signKey, AlipayTransaction transaction) throws Exception {
		Object obj = parseXml(responseText, QRCodeCancelXML.class);
		if(obj != null){
			QRCodeCancelXML cancel = (QRCodeCancelXML)obj;
			boolean isSuccess = cancel.isSuccess();
			if(!isSuccess){
				logger.error("线下扫码支付调用支付宝【撤销订单】接口异常，error=["+cancel.getError()+"]");
				throw new Exception("支付宝错误，无法撤销订单");
			}
			
			Map<String, String> param = cancel.getResponseParam();
			param.put("sign", cancel.getSign());
			param.put("sign_type", cancel.getSign_type());
			param.put("sign_key", signKey);
			if(!verifySign(param, PAYSOURCE)){ //校验sign
				logger.error("线下扫码支付调用支付宝【撤销订单】接口异常，校验签名错误");
				throw new Exception("支付宝错误，无法撤销订单");
			}
			
			QRCodeCancelResponse response = cancel.getResponse();
			QRCodeCancelResultCode resultCode = response.getResult_code();
			
			String orderNumber = null;
			String orderType = null;
			if(StringUtils.isNotBlank(transaction.getOrderNumber())){
				orderNumber = transaction.getOrderNumber();
				orderType = "XPOS_ORDER";
			}else if(StringUtils.isNotBlank(transaction.getPrepaidCardChargeOrderCode())){
				orderNumber = transaction.getPrepaidCardChargeOrderCode();
				orderType = "XPOS_PREPAID";
			}else if(StringUtils.isNotBlank(transaction.getThirdOrderCode())){
				orderNumber = transaction.getThirdOrderCode();
				orderType = "THIRD_ORDER";
			}
			
			if(QRCodeCancelResultCode.UNKNOWN.equals(resultCode)){ //处理异常
				return Payment.ALIPAY_ORDER_CANCEL_UNKNOWN;
			}else if(QRCodeCancelResultCode.FAIL.equals(resultCode)){ //撤销失败
				if(QRCodeCancelErrorCode.TRADE_HAS_FINISHED.equals(response.getDetail_error_code())){ //交易已结束，无法逆向操作
					TransactionPaymentStatus curPaymentStatus = TransactionPaymentStatus.findByValue(transaction.getAlipayPaymentStatus());
					if(TransactionPaymentStatus.UNPAID.equals(curPaymentStatus)){
						//当前订单未付款状态，说明交易直接关闭
						transaction.setAlipayPaymentStatus(TransactionPaymentStatus.FAILED.getValue());
					}else if(TransactionPaymentStatus.SUCCESS.equals(curPaymentStatus)){
						//当前订单付款成功，说明全额退款完成
						transaction.setAlipayPaymentStatus(TransactionPaymentStatus.REVOCATION.getValue());
					}
					transaction.setResponseCode(QRCodeCancelResultCode.FAIL.name());
					transaction.setAlipaySerial(response.getTrade_no());
					if(StringUtils.isNotBlank(responseText)){
						transaction.setComment(transaction.getComment() + "|||" + responseText);
					}
					alipayTransactionService.updateById(transaction);
					return Payment.ALIPAY_ORDER_CANCEL_FAILED;
				}else{
					transaction.setResponseCode(QRCodeCancelResultCode.FAIL.name());
					transaction.setAlipayPaymentStatus(TransactionPaymentStatus.FAILED.getValue());
					transaction.setAlipaySerial(response.getTrade_no());
					if(StringUtils.isNotBlank(responseText)){
						transaction.setComment(transaction.getComment() + "|||" + responseText);
					}
					alipayTransactionService.updateById(transaction);
					return Payment.ALIPAY_ORDER_CANCEL_FAILED;
				}
			}else if(QRCodeCancelResultCode.SUCCESS.equals(resultCode)){ //撤销成功
				TransactionPaymentStatus curPaymentStatus = TransactionPaymentStatus.findByValue(transaction.getAlipayPaymentStatus());
				if(TransactionPaymentStatus.UNPAID.equals(curPaymentStatus)){
					//当前订单未付款状态，说明交易直接关闭
					transaction.setAlipayPaymentStatus(TransactionPaymentStatus.FAILED.getValue());
				}else if(TransactionPaymentStatus.SUCCESS.equals(curPaymentStatus)){
					//当前订单付款成功，说明全额退款完成
					transaction.setAlipayPaymentStatus(TransactionPaymentStatus.REVOCATION.getValue());
				}
				transaction.setResponseCode(QRCodeCancelResultCode.SUCCESS.name());
				transaction.setAlipaySerial(response.getTrade_no());
				if(StringUtils.isNotBlank(responseText)){
					transaction.setComment(transaction.getComment() + "|||" + responseText);
				}
				alipayTransactionService.updateById(transaction);
				
				//如果退款成功，更新订单付款状态
				if(TransactionPaymentStatus.REVOCATION.getValue() == transaction.getAlipayPaymentStatus()){
					updateOrderRefundStatus(orderNumber, orderType);
				}
				
				return Payment.ALIPAY_ORDER_CANCEL_SUCCESS;
			}
		}
		return null;
	}
	
	@Transactional
	public Result refundPayment(AlipayQRCodeRefundParam refundParam, SystemParam systemParam, String orderNumber, POSGatewayAccount account) {
		Refund refund = null;
		
		//根据serial查支付流水
		AlipayTransaction transaction = alipayTransactionMapper.selectBySerial(refundParam.getSerial());
		if(transaction == null || !StringUtils.equals(account.getAccount(), transaction.getSellerAccount())){ //未找到订单 或者 支付宝商户号不匹配
			refund = Refund.NON_TRANSACTION;
			return new Result(refund.getName(), refund.getCode());
		}
		
		//匹配orderType
		String orderType = null;
		if (transaction.getOrderNumber() != null) {
			orderType = "XPOS_ORDER";
		} else if (transaction.getThirdOrderCode() != null) {
			orderType = "THIRD_ORDER";
		} else if (transaction.getPrepaidCardChargeOrderCode() != null) {
			orderType = "XPOS_PREPAID";
		}

		if (!(StringUtils.equalsIgnoreCase(refundParam.getOrderType(), orderType) &&
				StringUtils.equalsIgnoreCase(orderNumber, transaction.getOrderNumber()))) {
			refund = Refund.TRANSACTION_UNMATCHED; //订单和支付不匹配
			return new Result(refund.getName(), refund.getCode());
		}
		
		//检查订单状态是否可退
		Boolean availableOrderRefund = checkAvailableRefund(orderNumber,orderType);
		if (!availableOrderRefund){
			refund = Refund.ORDER_UNABLE_REFUND;
			return new Result(refund.getName(), refund.getCode());
		}
		
		//退款操作
		String responseText = null;
		try {
			responseText = refund(account.getSignKey(), transaction);
		} catch (Exception e) {
			refund = Refund.REFUND_TRANSACTION_FAILED;
			return new Result(refund.getName(), refund.getCode());
		}
		
		try {
			refund = processRefundCallback(responseText, account.getSignKey(), transaction);
		} catch (Exception e) {
			refund = Refund.REFUND_TRANSACTION_FAILED;
		}
		
		if (StringUtils.equals(orderType, "XPOS_ORDER")){
			//更新订单的实付金额（订单总金额*折扣-实体优惠券金额）
			updateOrderActualAmount(orderNumber);
		}

		//更新订单退款状态
		updateOrderRefundStatus(orderNumber, orderType);
		
		return new Result(refund.getName(), refund.getCode());
	}
	
	
	@Transactional
	public boolean refundOfflinePayment(AlipayTransaction transaction, POSGatewayAccount account){
		if(!StringUtils.equals(account.getAccount(), transaction.getSellerAccount())){ //未找到订单 或者 支付宝商户号不匹配
			return false;
		}
		
		String responseText = null;
		try {
			responseText = refund(account.getSignKey(), transaction);
		} catch (Exception e) {
			throw new RuntimeException();
		}
		
		try {
			Object obj = parseXml(responseText, QRCodeCancelXML.class);
			if(obj != null){
				QRCodeRefundXML refund = (QRCodeRefundXML)obj;
				boolean isSuccess = refund.isSuccess();
				if(!isSuccess){
					logger.error("线下扫码支付调用支付宝【订单退款】接口异常，error=["+refund.getError()+"]");
					throw new Exception("支付宝错误，支付宝错误，订单无法退款");
				}
				
				Map<String, String> param = refund.getResponseParam();
				param.put("sign", refund.getSign());
				param.put("sign_type", refund.getSign_type());
				param.put("sign_key", account.getSignKey());
				if(!verifySign(param, PAYSOURCE)){ //校验sign
					logger.error("线下扫码支付调用支付宝【撤销订单】接口异常，校验签名错误");
					throw new Exception("支付宝错误，无法撤销订单");
				}
				
				QRCodeRefundResponse response = refund.getResponse();
				QRCodeRefundResultCode resultCode = response.getResult_code();
				
				if(QRCodeRefundResultCode.SUCCESS.equals(resultCode)){ //处理成功
					return true;
				}
			}
			return false;
		} catch (Exception e) {
			throw new RuntimeException();
		}
	}
	
	/** 发起指定订单的退款请求 */
	private String refund(String signKey, AlipayTransaction transaction) throws Exception {
		Map<String, String> params = new HashMap<>();
		params.put("service", ALIPAY_ACQUIRE_REFUND_SERVICE);
		params.put("partner", transaction.getSellerAccount());
		params.put("_input_charset", FactoryConfig.getPayConfig(PAYSOURCE).getCharSet());
		params.put("out_trade_no", transaction.getSerial());
		params.put("refund_amount", transaction.getAmount().toString());
		params.put("trade_no", transaction.getAlipaySerial());
		params.put("sign_key", signKey);
		
		String responseText = AlipaySubmit.buildRequest(null, "", "", params, PAYSOURCE);
		
		logger.debug(responseText);
		
		return responseText;
	}

	/** 处理订单退款请求的同步返回结果 */
	private Refund processRefundCallback(String responseText, String signKey, AlipayTransaction transaction) throws Exception {
		Object obj = parseXml(responseText, QRCodeRefundXML.class);
		if(obj != null){
			QRCodeRefundXML refund = (QRCodeRefundXML)obj;
			boolean isSuccess = refund.isSuccess();
			if(!isSuccess){
				logger.error("线下扫码支付调用支付宝【订单退款】接口异常，error=["+refund.getError()+"]");
				throw new Exception("支付宝错误，订单无法退款");
			}
			
			Map<String, String> param = refund.getResponseParam();
			param.put("sign", refund.getSign());
			param.put("sign_type", refund.getSign_type());
			param.put("sign_key", signKey);
			if(!verifySign(param, PAYSOURCE)){ //校验sign
				logger.error("线下扫码支付调用支付宝【订单退款】接口异常，校验签名错误");
				throw new Exception("支付宝错误，订单无法退款");
			}
			
			QRCodeRefundResponse response = refund.getResponse();
			QRCodeRefundResultCode resultCode = response.getResult_code();
			
			String orderNumber = null;
			String orderType = null;
			if(StringUtils.isNotBlank(transaction.getOrderNumber())){
				orderNumber = transaction.getOrderNumber();
				orderType = "XPOS_ORDER";
			}else if(StringUtils.isNotBlank(transaction.getPrepaidCardChargeOrderCode())){
				orderNumber = transaction.getPrepaidCardChargeOrderCode();
				orderType = "XPOS_PREPAID";
			}else if(StringUtils.isNotBlank(transaction.getThirdOrderCode())){
				orderNumber = transaction.getThirdOrderCode();
				orderType = "THIRD_ORDER";
			}
			
			if(QRCodeRefundResultCode.UNKNOWN.equals(resultCode)){ //处理异常
				return Refund.REFUND_RESULT_UNKNOW;
			}else if(QRCodeRefundResultCode.FAIL.equals(resultCode)){ //撤销失败
				return Refund.REFUND_TRANSACTION_FAILED;
			}else if(QRCodeRefundResultCode.SUCCESS.equals(resultCode)){ //退款成功
				transaction.setAlipayPaymentStatus(TransactionPaymentStatus.REFUND.getValue());
				transaction.setResponseCode(QRCodeRefundResultCode.SUCCESS.name());
				transaction.setBuyerId(response.getBuyer_user_id());
				transaction.setBuyerAccount(response.getBuyer_logon_id());
				transaction.setAlipaySerial(response.getTrade_no());
				if(StringUtils.isNotBlank(responseText)){
					transaction.setComment(transaction.getComment() + "|||" + responseText);
				}
				alipayTransactionService.updateById(transaction);
				
				updateOrderRefundStatus(orderNumber, orderType);
				return Refund.SUCCESS;
			}
		}
		return null;
	}

}
