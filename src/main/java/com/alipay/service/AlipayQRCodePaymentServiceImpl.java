package com.alipay.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

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
import com.xpos.common.entity.Order;
import com.xpos.common.entity.Shop;
import com.xpos.common.entity.Order.Status;
import com.xpos.common.entity.pos.POSGatewayAccount;
import com.xpos.common.entity.pos.POSTransaction;
import com.xpos.common.entity.pos.POSTransaction.POSTransactionStatus;
import com.xpos.common.service.OrderService;
import com.xpos.common.service.POSGatewayAccountService;
import com.xpos.common.service.POSTransactionService;
import com.xpos.common.service.ShopService;

/**
 * 为兼容老接口暂时保留
 */

@Deprecated
@Service
public class AlipayQRCodePaymentServiceImpl implements AlipayQRCodePaymentService {
	private final static Map<String, String> DETAIL_ERROR_MAP = new HashMap<>();
	static{
		DETAIL_ERROR_MAP.put("TRADE_HAS_SUCCESS", "该订单已经支付，请勿重新支付");
		DETAIL_ERROR_MAP.put("TRADE_HAS_CLOSE", "该订单已经关闭，请重新创建订单");
		DETAIL_ERROR_MAP.put("REASON_ILLEGAL_STATUS", "");
		DETAIL_ERROR_MAP.put("BUYER_ENABLE_STATUS_FORBID", "您的支付宝账户异常，无法继续交易");
		DETAIL_ERROR_MAP.put("BUYER_PAYMENT_AMOUNT_DAY_LIMIT_ERROR", "您的付款日限额超限");
		DETAIL_ERROR_MAP.put("CLIENT_VERSION_NOT_MATCH", "您的支付宝钱包版本过低，请升级到最新版本后使用");
		DETAIL_ERROR_MAP.put("SOUNDWAVE_PARSER_FAIL", "您的付款码错误，请重新输入");
		DETAIL_ERROR_MAP.put("PULL_MOBILE_CASHIER_FAIL", "");
	}
	
	private static Logger logger = Logger.getLogger(AlipayQRCodePaymentServiceImpl.class);
	private final String ALIPAY_ACQUIRE_CREATEANDPAY_SERVICE = "alipay.acquire.createandpay";
	private final String ALIPAY_ACQUIRE_CREATEANDPAY_NOTIFY_URL = "http://vipoffline.xkeshi.com/api/alipay/qrcode/notify";
	private final String ALIPAY_ACQUIRE_CREATEANDPAY_TIMEOUT = "10m"; //订单超时时间
	private final String ALIPAY_ACQUIRE_QUERY_SERVICE = "alipay.acquire.query";
	private final String ALIPAY_ACQUIRE_CANCEL_SERVICE = "alipay.acquire.cancel";
	private final String ALIPAY_ACQUIRE_REFUND_SERVICE = "alipay.acquire.refund";
	private final PaySource PAYSOURCE = PaySource.XKESHI_ALIPAY_DIRECT;
	
	@Resource
	private POSTransactionService transactionService;
	@Resource
	private POSGatewayAccountService gatewayAccountService;
	@Resource
	private ShopService shopService;
	@Resource
	private OrderService orderService;

	@Override
	public String submitAndPay(Shop shop, String dynamicId, String signKey, POSTransaction transaction) throws Exception {
		Map<String, String> params = new HashMap<>();
		params.put("service", ALIPAY_ACQUIRE_CREATEANDPAY_SERVICE);
		params.put("partner", transaction.getGatewayAccount());
		params.put("_input_charset", FactoryConfig.getPayConfig(PAYSOURCE).getCharSet());
		params.put("notify_url", ALIPAY_ACQUIRE_CREATEANDPAY_NOTIFY_URL);
		params.put("out_trade_no", transaction.getCode());
		params.put("subject", shop.getName()+"-线下扫码支付订单"); //订单标题
		params.put("product_code", "BARCODE_PAY_OFFLINE"); //扫码支付
		params.put("total_fee", transaction.getSum().toString());
		params.put("operator_type", "1"); //0:支付宝操作员, 1:商户的操作员
		params.put("operator_id", transaction.getOperator());
		params.put("show_url","v2.xkeshi.com/shop/"+shop.getId());
		params.put("it_b_pay", ALIPAY_ACQUIRE_CREATEANDPAY_TIMEOUT); //设置超时（考虑到网络以及余额等问题）
		params.put("dynamic_id_type", "qr_code");
		params.put("dynamic_id",dynamicId);
		params.put("sign_key", signKey);
		
		String responseText = AlipaySubmit.buildRequest(null, "", "", params, PAYSOURCE);
		
		logger.debug(responseText);
		
		return responseText;
	}

	@Override
	public POSTransaction processSubmitAndPayCallback(String responseText, String signKey, POSTransaction transaction) throws Exception {
		Object obj = parseXml(responseText, QRCodePaymentXML.class);
		if(obj != null){
			QRCodePaymentXML payment = (QRCodePaymentXML)obj;
			boolean isSuccess = payment.isSuccess();
			if(!isSuccess){
				logger.error("线下扫码支付调用支付宝【统一下单并支付】接口异常，error=["+payment.getError()+"]");
				throw new Exception("支付宝接口错误，请选择其他付款方式");
			}
			
			Map<String, String> param = payment.getResponseParam();
			param.put("sign", payment.getSign());
			param.put("sign_type", payment.getSign_type());
			param.put("sign_key", signKey);
			if(!verifySign(param, PAYSOURCE)){ //校验sign
				logger.error("线下扫码支付调用支付宝【统一下单并支付】接口异常，校验签名异常");
				throw new Exception("支付宝接口错误，请选择其他付款方式");
			}
			
			QRCodePaymentResponse response = payment.getResponse();
			QRCodePaymentResultCode resultCode = response.getResult_code();
			
			if(QRCodePaymentResultCode.ORDER_FAIL.equals(resultCode)
					|| QRCodePaymentResultCode.ORDER_SUCCESS_PAY_FAIL.equals(resultCode)){ //下单失败 或者 下单成功但支付失败
				//update POSTransaction
				if(QRCodePaymentResultCode.ORDER_FAIL.equals(resultCode)){
					transaction.setResponseCode(QRCodePaymentResultCode.ORDER_FAIL.name());
				}else if(QRCodePaymentResultCode.ORDER_SUCCESS_PAY_FAIL.equals(resultCode)){
					transaction.setResponseCode(QRCodePaymentResultCode.ORDER_SUCCESS_PAY_FAIL.name());
				}
				transaction.setSerial(response.getTrade_no());
				transaction.setCardNumber(response.getBuyer_user_id());
				transaction.setRemark(responseText);
				transaction.setStatus(POSTransactionStatus.PAID_FAIL);
				transaction.setTradeDate(new Date());
				transactionService.updatePOSTransactionByCode(transaction);
				transaction.setRemark(DETAIL_ERROR_MAP.get(response.getDetail_error_code().toString())); //pass error description to controller
			}else if(QRCodePaymentResultCode.ORDER_SUCCESS_PAY_SUCCESS.equals(resultCode)){ //下单成功并且支付成功
				transaction.setResponseCode(QRCodePaymentResultCode.ORDER_SUCCESS_PAY_SUCCESS.name());
				transaction.setSerial(response.getTrade_no());
				transaction.setCardNumber(response.getBuyer_user_id());
				transaction.setRemark(responseText);
				transaction.setStatus(POSTransactionStatus.PAID_SUCCESS);
				transaction.setTradeDate(new Date());
				transactionService.updatePOSTransactionByCode(transaction);
			}else if(QRCodePaymentResultCode.ORDER_SUCCESS_PAY_INPROCESS.equals(resultCode)
					|| QRCodePaymentResultCode.UNKNOWN.equals(resultCode)){ //支付处理中、处理结果未知
				//不做任何修改，需PAD端调用查询接口确认
			}
		}
		return transaction;
	}
	
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
					public boolean shouldSerializeMember(Class definedIn, String fieldName) {
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
		return xstream.fromXML(xml);
	}
	
	private boolean verifySign(Map<String, String> sParaTemp, PaySource paySource){
		//除去数组中的空值和签名参数
		Map<String, String> sPara = AlipayCore.paraFilter(sParaTemp);
		//生成签名结果
		String mysign = AlipaySubmit.buildRequestMysign(sPara,paySource);
		return StringUtils.equalsIgnoreCase(mysign, sParaTemp.get("sign"));
	}

	@Override
	public boolean processSubmitAndPayNotify(QRCodePaymentNotify notify, String body) {
		try{
			
			//query POSTransaction & POSGatewayAccount
			String signKey = null;
			POSTransaction transaction = transactionService.findTransactionByCode(notify.getOut_trade_no());
			if(transaction == null){
				logger.error("支付宝线下扫码支付【统一下单并支付】接口异步回调异常，根据code未找到相应订单");
				return false;
			}else{
				POSGatewayAccount account = gatewayAccountService.findByAccountAndType(transaction.getGatewayAccount(), transaction.getGatewayType());
				if(account == null){
					logger.error("支付宝线下扫码支付【统一下单并支付】接口异步回调异常，gatewayAccount = [" + transaction.getGatewayAccount() + "]未找到对应的POSGatewayAccount");
					return false;
				}else{
					signKey = account.getSignKey();
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
					return true; //不做修改，消费通知消息
				}else if(QRCodePaymentNotifyTradeStatus.TRADE_CLOSED.equals(tradeStatus)){
					if(POSTransactionStatus.UNPAID.equals(transaction.getStatus())){ //1.在指定时间段内未支付时关闭的交易；
						transaction.setStatus(POSTransactionStatus.PAID_FAIL);
					}else if(POSTransactionStatus.PAID_SUCCESS.equals(transaction.getStatus())){ //2. 在交易完成全额退款成功时关闭的交易。
						transaction.setStatus(POSTransactionStatus.PAID_REFUND);
					}else if(POSTransactionStatus.PAID_FAIL.equals(transaction.getStatus()) 
							|| POSTransactionStatus.PAID_REFUND.equals(transaction.getStatus())
							|| POSTransactionStatus.PAID_REVOCATION.equals(transaction.getStatus())){ //数据库已修改，重复通知
						return true; //不做修改，消费通知消息
					}
					//撤销相关点单，改为CANCEL状态
					Order order = orderService.findOrderByPOSTransactionId(transaction.getId());
					if(order != null){
						order.setStatus(Status.CANCEL);
						orderService.updateOrder(order);
					}
					
					transaction.setResponseCode(QRCodePaymentNotifyTradeStatus.TRADE_CLOSED.name());
					transaction.setSerial(notify.getTrade_no());
					transaction.setCardNumber(notify.getBuyer_id());
					transaction.setRemark(body);
					transaction.setTradeDate(notify.getGmt_payment());
					return transactionService.updatePOSTransactionByCode(transaction);
				}else if(QRCodePaymentNotifyTradeStatus.TRADE_SUCCESS.equals(tradeStatus) //交易成功，且可对该交易做操作，如：多级分润、退款等。
						|| QRCodePaymentNotifyTradeStatus.TRADE_FINISHED.equals(tradeStatus) //交易成功且结束，即不可再做任何操作
						|| QRCodePaymentNotifyTradeStatus.TRADE_PENDING.equals(tradeStatus)){ //等待卖家收款（买家付款后，如果卖家账号被冻结）
					
					transaction.setResponseCode(tradeStatus.name());
					transaction.setSerial(notify.getTrade_no());
					transaction.setCardNumber(notify.getBuyer_id());
					transaction.setRemark(body);
					transaction.setStatus(POSTransactionStatus.PAID_SUCCESS);
					transaction.setTradeDate(notify.getGmt_payment());
					boolean result = transactionService.updatePOSTransactionByCode(transaction);
					if(QRCodePaymentNotifyTradeStatus.TRADE_PENDING.equals(tradeStatus)){
						//卖家账户异常
						logger.error("支付宝线下扫码支付【统一下单并支付】接口异步回调，通知[TRADE_PENDING]卖家账号异常，卖家账号："+transaction.getGatewayAccount());
					}
					return result;
				}
			}
		}catch(Exception e){
			logger.error("支付宝线下扫码支付【统一下单并支付】接口异步回调失败", e);
		}
		return false;
	}

	@Override
	public String query(String signKey, POSTransaction transaction) throws Exception {
		Map<String, String> params = new HashMap<>();
		params.put("service", ALIPAY_ACQUIRE_QUERY_SERVICE);
		params.put("partner", transaction.getGatewayAccount());
		params.put("_input_charset", FactoryConfig.getPayConfig(PAYSOURCE).getCharSet());
		params.put("out_trade_no", transaction.getCode());
		params.put("trade_no", transaction.getSerial());
		params.put("sign_key", signKey);
		
		String responseText = AlipaySubmit.buildRequest(null, "", "", params, PAYSOURCE);
		
		logger.debug(responseText);
		
		return responseText;
	}

	@Override
	public String processQueryCallback(String responseText, String signKey, POSTransaction transaction) throws Exception {
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
				return "QUERY_FAIL|||支付宝订单状态异常：" + response.getDetail_error_des();
			}else if(QRCodeQueryResultCode.SUCCESS.equals(resultCode)){ //查询成功
				QRCodeQueryTradeStatus tradeStatus = response.getTrade_status();
				if(QRCodeQueryTradeStatus.WAIT_BUYER_PAY.equals(tradeStatus)){
					//交易创建，等待买家付款
					return QRCodeQueryTradeStatus.WAIT_BUYER_PAY + "|||订单已创建，等待买家付款";
				}else if(QRCodeQueryTradeStatus.TRADE_CLOSED.equals(tradeStatus)){
					if(POSTransactionStatus.UNPAID.equals(transaction.getStatus())){ //1.在指定时间段内未支付时关闭的交易；
						transaction.setStatus(POSTransactionStatus.PAID_FAIL);
					}else if(POSTransactionStatus.PAID_SUCCESS.equals(transaction.getStatus())){ //2. 在交易完成全额退款成功时关闭的交易。
						transaction.setStatus(POSTransactionStatus.PAID_REFUND);
					}else if(POSTransactionStatus.PAID_FAIL.equals(transaction.getStatus()) || POSTransactionStatus.PAID_REFUND.equals(transaction.getStatus())){ //重复查询，DB中状态已修改
						return QRCodeQueryTradeStatus.TRADE_CLOSED + "|||支付宝订单已超时关闭或全额退款完成";
					}
					
					transaction.setResponseCode(QRCodeQueryTradeStatus.TRADE_CLOSED.name());
					transaction.setSerial(response.getTrade_no());
					transaction.setCardNumber(response.getBuyer_user_id());
					transaction.setRemark(responseText);
					if(StringUtils.isNotBlank(response.getSend_pay_date())){
						transaction.setTradeDate(DateUtils.parseDate(response.getSend_pay_date(), "yyyy-MM-dd HH:mm:ss"));
					}
					transactionService.updatePOSTransactionByCode(transaction);
					return QRCodeQueryTradeStatus.TRADE_CLOSED + "|||支付宝订单已超时关闭或全额退款完成";
				}else if(QRCodeQueryTradeStatus.TRADE_SUCCESS.equals(tradeStatus) //交易成功，且可对该交易做操作，如：多级分润、退款等。
						|| QRCodeQueryTradeStatus.TRADE_FINISHED.equals(tradeStatus) //交易成功且结束，即不可再做任何操作
						|| QRCodeQueryTradeStatus.TRADE_PENDING.equals(tradeStatus)){ //等待卖家收款（买家付款后，如果卖家账号被冻结）
					
					transaction.setResponseCode(tradeStatus.name());
					transaction.setSerial(response.getTrade_no());
					transaction.setCardNumber(response.getBuyer_user_id());
					transaction.setRemark(responseText);
					transaction.setStatus(POSTransactionStatus.PAID_SUCCESS);
					if(StringUtils.isNotBlank(response.getSend_pay_date())){
						transaction.setTradeDate(DateUtils.parseDate(response.getSend_pay_date(), "yyyy-MM-dd HH:mm:ss"));
					}
					transactionService.updatePOSTransactionByCode(transaction);
					if(QRCodeQueryTradeStatus.TRADE_PENDING.equals(tradeStatus)){
						//卖家账户异常
						logger.error("支付宝线下扫码支付【统一下单并支付】接口异步回调，通知[TRADE_PENDING]卖家账号异常，卖家账号："+transaction.getGatewayAccount());
					}
					return QRCodeQueryTradeStatus.TRADE_SUCCESS + "|||支付宝订单交易成功";
				}
			}
		}
		return null;
	}


	@Override
	public String cancel(String signKey, POSTransaction transaction) throws Exception {
		Map<String, String> params = new HashMap<>();
		params.put("service", ALIPAY_ACQUIRE_CANCEL_SERVICE);
		params.put("partner", transaction.getGatewayAccount());
		params.put("_input_charset", FactoryConfig.getPayConfig(PAYSOURCE).getCharSet());
		params.put("out_trade_no", transaction.getCode());
		params.put("trade_no", transaction.getSerial());
		params.put("sign_key", signKey);
		
		String responseText = AlipaySubmit.buildRequest(null, "", "", params, PAYSOURCE);
		
		logger.debug(responseText);
		
		return responseText;
	}


	@Override
	public String processCancelCallback(String responseText, String signKey, POSTransaction transaction) throws Exception {
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
			if(QRCodeCancelResultCode.UNKNOWN.equals(resultCode)){ //处理异常
				return "UNKNOWN|||支付宝撤销异常，请重试";
			}else if(QRCodeCancelResultCode.FAIL.equals(resultCode)){ //撤销失败
				if(QRCodeCancelErrorCode.TRADE_HAS_FINISHED.equals(response.getDetail_error_code())){ //交易已结束，无法逆向操作
					if(POSTransactionStatus.UNPAID.equals(transaction.getStatus())){
						//当前订单未付款状态，说明交易直接关闭
						transaction.setStatus(POSTransactionStatus.PAID_FAIL);
					}else if(POSTransactionStatus.PAID_SUCCESS.equals(transaction.getStatus())){
						//当前订单付款成功，说明全额退款完成
						transaction.setStatus(POSTransactionStatus.PAID_REVOCATION);
					}
					transaction.setResponseCode(QRCodeCancelResultCode.FAIL.name());
					transaction.setSerial(response.getTrade_no());
					transaction.setRemark(responseText);
					transactionService.updatePOSTransactionByCode(transaction);
					return "FAIL|||订单撤销失败，原因："+response.getDetail_error_des();
				}else{
					transaction.setResponseCode(QRCodeCancelResultCode.FAIL.name());
					transaction.setStatus(POSTransactionStatus.PAID_FAIL);
					transaction.setSerial(response.getTrade_no());
					transaction.setRemark(responseText);
					transactionService.updatePOSTransactionByCode(transaction);
					return "FAIL|||订单撤销失败，原因："+response.getDetail_error_des();
				}
			}else if(QRCodeCancelResultCode.SUCCESS.equals(resultCode)){ //撤销成功
				if(POSTransactionStatus.UNPAID.equals(transaction.getStatus())){
					//当前订单未付款状态，说明交易直接关闭
					transaction.setStatus(POSTransactionStatus.PAID_FAIL);
				}else if(POSTransactionStatus.PAID_SUCCESS.equals(transaction.getStatus())){
					//当前订单付款成功，说明全额退款完成
					transaction.setStatus(POSTransactionStatus.PAID_REVOCATION);
				}
				transaction.setResponseCode(QRCodeCancelResultCode.SUCCESS.name());
				transaction.setSerial(response.getTrade_no());
				transaction.setRemark(responseText);
				transactionService.updatePOSTransactionByCode(transaction);
				
				//撤销相关点单，改为CANCEL状态
				Order order = orderService.findOrderByPOSTransactionId(transaction.getId());
				if(order != null){
					order.setStatus(Status.CANCEL);
					orderService.updateOrder(order);
				}
				
				return "SUCCESS|||订单撤销成功";
			}
		}
		return null;
	}

	@Override
	public String refund(String signKey, POSTransaction transaction) throws Exception {
		Map<String, String> params = new HashMap<>();
		params.put("service", ALIPAY_ACQUIRE_REFUND_SERVICE);
		params.put("partner", transaction.getGatewayAccount());
		params.put("_input_charset", FactoryConfig.getPayConfig(PAYSOURCE).getCharSet());
		params.put("out_trade_no", transaction.getCode());
		params.put("refund_amount", transaction.getSum().toString());
		params.put("trade_no", transaction.getSerial());
		params.put("sign_key", signKey);
		
		String responseText = AlipaySubmit.buildRequest(null, "", "", params, PAYSOURCE);
		
		logger.debug(responseText);
		
		return responseText;
	}

	@Override
	public String processRefundCallback(String responseText, String signKey, POSTransaction transaction) throws Exception {
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
			if(QRCodeRefundResultCode.UNKNOWN.equals(resultCode)){ //处理异常
				return "UNKOWN|||支付宝退款异常，请重试";
			}else if(QRCodeRefundResultCode.FAIL.equals(resultCode)){ //撤销失败
				return "FAIL|||订单撤销失败，原因："+response.getDetail_error_des();
			}else if(QRCodeRefundResultCode.SUCCESS.equals(resultCode)){ //退款成功
				transaction.setStatus(POSTransactionStatus.PAID_REFUND);
				transaction.setResponseCode(QRCodeRefundResultCode.SUCCESS.name());
				transaction.setCardNumber(response.getBuyer_user_id());
				transaction.setSerial(response.getTrade_no());
				transaction.setRemark(responseText);
				transactionService.updatePOSTransactionByCode(transaction);
				//撤销相关点单，改为REFUND状态
				Order order = orderService.findOrderByPOSTransactionId(transaction.getId());
				if(order != null){
					orderService.discardOrderAndReturnItemInventory(order.getOrderNumber(), Status.REFUND);
				}
				return "SUCCESS|||订单退款成功";
			}
		}
		return null;
	}
}
