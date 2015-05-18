package com.xpos.common.service;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.SortedMap;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.tenpay.RequestHandler;
import com.tenpay.client.ClientResponseHandler;
import com.wxpay.client.TenpayHttpClient;
import com.wxpay.util.MD5Util;
import com.xpos.common.entity.Configuration;
import com.xpos.common.entity.Refund;
import com.xpos.common.entity.pos.POSTransaction;
import com.xpos.common.entity.pos.POSTransaction.POSTransactionStatus;
import com.xpos.common.utils.CMCCPaymentUtil;
import com.xpos.common.utils.HttpUtils;
import com.xpos.common.utils.ShengPayUtil;

/**
 * @author chengj
 */
@Service
public class ExternalHttpInvokelServiceImpl implements ExternalHttpInvokeService {
	private final static Logger logger = LoggerFactory.getLogger(ExternalHttpInvokelServiceImpl.class);
	private final static String CMCMOBILENUM_REGEX = "^(1(3[4-9]|5[012789]|8[23478])\\d{8})$"; //中国移动号段正则表达式
	
	@Autowired
	private POSTransactionService transactionService;
	
	@Autowired
	private ConfigurationService confService;
	
	@Value("#{settings['shengPay.privateKeyFilePath']}")
	private String shengPayPrivateKeyPath;
	
	@Value("#{settings['shengPay.publicKeyFilePath']}")
	private String shengPayPublicKeyPath;
	
	@Value("#{settings['shengPay.interfaceUrl']}")
	private String shengPayInterfaceUrl;
	
	@Value("#{settings['wxpay.partner.id']}")
	private String partnerId;
	
	@Value("#{settings['wxpay.partner.key']}")
	private String partnerKey;
	
	@Value("#{settings['tenpay.operator.user.username']}")
	private String tenpayOperatorUsrName;
	
	@Value("#{settings['tenpay.operator.user.password']}")
	private String tenpayOperatorPwd;
	
	@Value("#{settings['tenpay.refund.detailQueryURL']}")
	private String tenpayRefundDetailQueryURL;
	
	@Value("#{settings['tenpay.refund.executeURL']}")
	private String tenpayRefundURL;
	
	@Resource
	private CMCCPaymentUtil cmccPaymentUtil;
	
	/** 查询盛付通订单详情 */
	@Override
	public String queryShengPayOrderDetail(POSTransaction transaction, String type) throws Exception {
		if(transaction == null){
			return "未找到相应盛付通订单";
		}
		
		try {
			//1.发送http请求
			Map<String, String> paramMap = ShengPayUtil.generateQueryParamMap(transaction, type, shengPayPrivateKeyPath);
			String respStr = HttpUtils.keyValuePost(shengPayInterfaceUrl+"/txnQuery", paramMap, "UTF-8", "UTF-8");
			
			//2.解析返回信息
			if(StringUtils.isBlank(respStr)){
				throw new Exception();
			}
			//验签，并将收到的数据转成MAP
			Map<String, String> responseMap = ShengPayUtil.parseResponseParams(respStr, shengPayPublicKeyPath);
			POSTransactionStatus curStatus = transaction.getStatus();
			POSTransaction _transaction = new POSTransaction();
			_transaction.setId(transaction.getId());
			_transaction.setCode(transaction.getCode());
			
			if(StringUtils.equalsIgnoreCase(responseMap.get("respCode"), "00")){
				//对比返回的交易状态和DB的交易状态，做必要更新
				if(StringUtils.equalsIgnoreCase("PUR", type) 
						&& (curStatus.equals(POSTransactionStatus.UNPAID) || curStatus.equals(POSTransactionStatus.PAID_FAIL))){
					//如果查询消费结果返回成功，且DB仍是待付款或失败，则修改DB为交易成功状态
					_transaction.setStatus(POSTransactionStatus.PAID_SUCCESS);
					_transaction.setTerminal(responseMap.get("terminalId"));
					_transaction.setBatchNo(responseMap.get("traceNo"));
					_transaction.setBatchNo(responseMap.get("batchNo"));
					_transaction.setSerial(responseMap.get("orderId"));
					if(StringUtils.isNotBlank(responseMap.get("txnTime"))){
						DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyyMMddHHmmss");
						_transaction.setTradeDate(fmt.parseDateTime(responseMap.get("txnTime")).toDate());
					}
					_transaction.setRefNo(responseMap.get("txnRef"));
					_transaction.setResponseCode(responseMap.get("respCode"));
					_transaction.setCardNumber(responseMap.get("shortPan"));
				}else if(StringUtils.equalsIgnoreCase("VID", type) 
						&& (curStatus.equals(POSTransactionStatus.UNPAID) || curStatus.equals(POSTransactionStatus.PAID_FAIL) || curStatus.equals(POSTransactionStatus.PAID_SUCCESS))) {
					//如果查询撤销结果返回成功，且DB仍是待付款、失败、成功，则修改DB为撤销成功状态
					_transaction.setStatus(POSTransactionStatus.PAID_REVOCATION);
				}else if(StringUtils.equalsIgnoreCase("RFD", type) 
						&& (curStatus.equals(POSTransactionStatus.UNPAID) || curStatus.equals(POSTransactionStatus.PAID_FAIL) || curStatus.equals(POSTransactionStatus.PAID_SUCCESS))) {
					//如果查询退货结果返回成功，且DB仍是待付款、失败、成功，则修改DB为撤销成功状态
					_transaction.setStatus(POSTransactionStatus.PAID_REFUND);
				}
				transactionService.updatePOSTransactionByCode(_transaction);
				
				return "SUCCESS"; //直接返回APP端成功消息。如果上一步保存DB失败，等待通知继续重试修改
			}else if(StringUtils.equalsIgnoreCase(responseMap.get("respCode"), "P0")){ //处理中
				throw new Exception("系统处理中，请稍后重试");
			} else {
				logger.error("盛付通查询接口响应码返回错误，desc：【" + responseMap.get("respCodeDesc") + "】");
				if(StringUtils.equalsIgnoreCase("PUR", type)){ //刷卡交易返回非00类型的应答码，说明交易失败，修改DB为消费失败类型
					_transaction.setStatus(POSTransactionStatus.PAID_FAIL);
					_transaction.setTerminal(responseMap.get("terminalId"));
					_transaction.setBatchNo(responseMap.get("traceNo"));
					_transaction.setBatchNo(responseMap.get("batchNo"));
					_transaction.setSerial(responseMap.get("orderId"));
					if(StringUtils.isNotBlank(responseMap.get("txnTime"))){
						DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyyMMddHHmmss");
						_transaction.setTradeDate(fmt.parseDateTime(responseMap.get("txnTime")).toDate());
					}
					_transaction.setRefNo(responseMap.get("txnRef"));
					_transaction.setResponseCode(responseMap.get("respCode"));
					_transaction.setCardNumber(responseMap.get("shortPan"));
					transactionService.updatePOSTransactionByCode(_transaction);
				}
				return responseMap.get("respCodeDesc");
			}
		} catch (Exception e) {
			logger.error("调用盛付通外部接口【查询订单详情】失败", e);
			throw e;
		}
	}
	@Override
	public POSTransaction createCMCCTicketOrder(POSTransaction transaction, String uid, String deviceNumber) throws Exception {
		try {
			//1.发送http请求
			cmccPaymentUtil.init();
			String xmlStr = cmccPaymentUtil.generateCMCCTicketPayOrderXml(transaction, uid, deviceNumber);
			String respStr = HttpUtils.xmlPost(cmccPaymentUtil.CMCC_TICKET_ORDER_URL, xmlStr, "UTF-8", "GBK");
			
			//2.解析返回信息
			if(StringUtils.isBlank(respStr)){
				throw new Exception();
			}
			//验签，并将收到的数据转成MAP
			Map<String, String> responseMap = cmccPaymentUtil.parseResponseParams(respStr);
			
			if(StringUtils.equalsIgnoreCase(responseMap.get("RCODE"), "000000")){
				transaction.setSerial(responseMap.get("OMID"));
				transaction.setStatus(POSTransactionStatus.PAID_SUCCESS);
				String payDayStr = responseMap.get("DATE")+responseMap.get("TIME");
				DateTime payDay = DateTimeFormat.forPattern("yyyyMMddHHmmss").parseDateTime(payDayStr);
				transaction.setTradeDate(payDay.toDate());
			}else{
				transaction.setStatus(POSTransactionStatus.PAID_FAIL);
				String remark = null;
				switch(responseMap.get("RCODE")){
					case "MB1001": remark = "订单已过期"; break;
					case "MB1002": remark = "用户余额不足"; break;
					case "MB1003": remark = "交易失败"; break;
					case "MB1004": remark = "未找到相应的订单"; break;
					case "MB1005": remark = "订单已被撤消"; break;
					case "MB1006": remark = "没有可撤销的交易"; break;
					case "MB1007": remark = "撤销交易失败"; break;
					case "MB1008": remark = "查询余额失败"; break;
					case "MB9999": remark = "系统繁忙，请稍后再试"; break;
					case "MB0001": remark = "请求参数不正确"; break;
					case "MB0002": remark = "用户信息不存在"; break;
					case "MB0003": remark = "用户密码验证失败"; break;
					case "MB0004": remark = "验证签名失败"; break;
					case "MB0101": remark = "短信模板不存在"; break;
					case "MB0102": remark = "短信下发失败"; break;
					default:remark = responseMap.get("RCODE");
				}
				transaction.setRemark(remark);
			}
			return transaction;
		} catch (Exception e) {
			logger.error("调用外部接口开通【个人支付协议】失败", e);
			throw e;
		}
		
	}
	
	@Override
	public String signTicketPaymentAgreement(String mobile) throws Exception {
		if(StringUtils.isBlank(mobile) || !Pattern.matches(CMCMOBILENUM_REGEX, mobile)){
			return null;
		}
		
		try {
			//1.发送http请求
			cmccPaymentUtil.init();
			String xmlStr = cmccPaymentUtil.generateCMCCTicketSignAgreementXml(mobile);
			String respStr = HttpUtils.xmlPost(cmccPaymentUtil.CMCC_TICKET_ORDER_URL, xmlStr, "UTF-8", "GBK");
			
			//2.解析返回信息
			if(StringUtils.isBlank(respStr)){
				throw new Exception();
			}
			//验签，并将收到的数据转成MAP
			Map<String, String> responseMap = cmccPaymentUtil.parseResponseParams(respStr);
			
			if(StringUtils.equalsIgnoreCase(responseMap.get("RCODE"), "000000")){
				return responseMap.get("UID");
			}else{
				return null;
			}
		} catch (Exception e) {
			logger.error("调用外部接口开通【个人支付协议】失败", e);
			throw e;
		}
	}
	
	@Override
	public int getTicketBalanceByPhone(String mobile) throws Exception {
		if(StringUtils.isBlank(mobile) || !Pattern.matches(CMCMOBILENUM_REGEX, mobile)){
			return -1;
		}
		
		try {
			//1.发送http请求
			cmccPaymentUtil.init();
			String xmlStr = cmccPaymentUtil.generateCMCCTicketBalanceQueryXml(mobile);
			String respStr = HttpUtils.xmlPost(cmccPaymentUtil.CMCC_TICKET_ORDER_URL, xmlStr, "UTF-8", "GBK");
			
			//2.解析返回信息
			if(StringUtils.isBlank(respStr)){
				throw new Exception();
			}
			//验签，并将收到的数据转成MAP
			Map<String, String> responseMap = cmccPaymentUtil.parseResponseParams(respStr);
			if(StringUtils.equalsIgnoreCase(responseMap.get("RCODE"), "000000")){ //查询成功
				String balance = responseMap.get("MER_RED_BAL");
				return Integer.valueOf(balance);
			}else if(StringUtils.equalsIgnoreCase(responseMap.get("RCODE"), "MB1008")){ //未开通电子钱包
				return -2;
			} else {
				return -3;
			}
		} catch (Exception e) {
			logger.error("调用外部接口获取【用户电子券余额】失败", e);
			throw e;
		}
	}

	@Override
	public String cancelCMCCTicketOrder(POSTransaction transaction) throws Exception {
		if(transaction == null){
			return "订单不存在，无法撤销";
		}
		
		try {
			//1.发送http请求
			cmccPaymentUtil.init();
			String xmlStr = cmccPaymentUtil.generateCMCCTicketRevocationXml(transaction);
			String respStr = HttpUtils.xmlPost(cmccPaymentUtil.CMCC_TICKET_ORDER_URL, xmlStr, "UTF-8", "GBK");
			
			//2.解析返回信息
			if(StringUtils.isBlank(respStr)){
				throw new Exception();
			}
			//验签，并将收到的数据转成MAP
			Map<String, String> responseMap = cmccPaymentUtil.parseResponseParams(respStr);
			if(StringUtils.equalsIgnoreCase(responseMap.get("RCODE"), "000000")){ //冲正成功
				return null;
			} else {
				logger.error("移动电子券冲正失败，desc：【"+responseMap.get("DESC")+"】");
				return responseMap.get("DESC");
			}
		} catch (Exception e) {
			logger.error("调用外部接口【商户无磁有密/无密支付冲正】失败", e);
			throw e;
		}
	}

	@Override
	public Map<String, String> queryTenpayRefundDetail(Refund refund) {
		if(refund == null){
			return null;
		}
		
		try{
			//创建查询请求对象
			RequestHandler reqHandler = new RequestHandler(null, null);
			//通信对象
			TenpayHttpClient httpClient = new TenpayHttpClient();
			//应答对象
			ClientResponseHandler resHandler = new ClientResponseHandler();
			
			//-----------------------------
			//设置请求参数
			//-----------------------------
			reqHandler.init();
			reqHandler.setKey(partnerKey);
			reqHandler.setGateUrl(tenpayRefundDetailQueryURL);
			
			//-----------------------------
			//设置接口参数
			//-----------------------------
			reqHandler.setParameter("partner", partnerId);
			reqHandler.setParameter("out_refund_no", refund.getCode());
			reqHandler.setParameter("input_charset", "UTF-8");
			if(StringUtils.isNotBlank(refund.getSerial())){
				reqHandler.setParameter("refund_id", refund.getSerial());
			}
			
			//-----------------------------
			//设置通信参数
			//-----------------------------
			//设置请求返回的等待时间
			httpClient.setTimeOut(5);
			
			//设置发送类型POST
			httpClient.setMethod("POST");
			
			//设置字符集
			httpClient.setCharset("UTF-8");
			
			//设置请求内容
			String requestUrl = reqHandler.getRequestURL();
			httpClient.setReqContent(requestUrl);
			String rescontent = "null";
			
			//后台调用
			if(httpClient.call()) {
				//设置结果参数
				rescontent = httpClient.getResContent();
				resHandler.setContent(rescontent);
				resHandler.setKey(partnerKey);
				
				//获取返回参数
				return resHandler.getAllParameters();
				
				//判断签名及结果，只有签名正确并且retcode为0才是请求成功
			} else {
				logger.error("后台调用财付通退款详情查询通信失败");//有可能因为网络原因，请求已经处理，但未收到应答
			}
		}catch(Exception e){
			logger.error("调用外部接口【财付通退款详情查询】失败");
		}
		return null;
	}

	@Override
	public Map<String, String> executeTenpayRefund(Refund refund) {
		if(refund == null){
			return null;
		}
		
		try{
			//创建查询请求对象
			RequestHandler reqHandler = new RequestHandler(null, null);
			//通信对象
			TenpayHttpClient httpClient = new TenpayHttpClient();
			//应答对象
			ClientResponseHandler resHandler = new ClientResponseHandler();
			
			//-----------------------------
			//设置请求参数
			//-----------------------------
			reqHandler.init();
			reqHandler.setKey(partnerKey);
			reqHandler.setGateUrl(tenpayRefundURL);
			
			//-----------------------------
			//设置接口参数
			//-----------------------------
			reqHandler.setParameter("service_version", "1.1");
			reqHandler.setParameter("input_charset", "UTF-8");
			reqHandler.setParameter("partner", partnerId);
			reqHandler.setParameter("out_trade_no", refund.getPayment().getCode());
			reqHandler.setParameter("transaction_id", refund.getPayment().getSerial());
			reqHandler.setParameter("out_refund_no", refund.getCode());
			int totalFee = refund.getPayment().getSum().multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP).intValue();
			reqHandler.setParameter("total_fee", ""+totalFee);
			int refundFee = refund.getSum().multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP).intValue();
			reqHandler.setParameter("refund_fee", ""+refundFee);
			reqHandler.setParameter("op_user_id", tenpayOperatorUsrName);
			//操作员密码,MD5处理
			reqHandler.setParameter("op_user_passwd", MD5Util.MD5Encode(tenpayOperatorPwd,"UTF-8"));
			
			//-----------------------------
			//设置通信参数
			//-----------------------------
			//设置请求返回的等待时间
			httpClient.setTimeOut(5);
	
			//设置发送类型POST
			httpClient.setMethod("POST");
			
			//设置ca证书
			Configuration caConf = confService.findByName("TENPAY_CERT_PATH");
			httpClient.setCaInfo(new File(caConf != null ? caConf.getValue() : ""));
				
			//设置个人(商户)证书
			Configuration pfxConf = confService.findByName("TENPAY_PFX_PATH");
			httpClient.setCertInfo(new File(pfxConf != null ? pfxConf.getValue() : ""), partnerId);
			
			//设置字符集
			httpClient.setCharset("UTF-8");
			
			//设置请求内容
			String requestUrl = reqHandler.getRequestURL();
			httpClient.setReqContent(requestUrl);
			String rescontent = "null";
	
			//后台调用
			if(httpClient.call()) {
				//设置结果参数
				rescontent = httpClient.getResContent();
				resHandler.setContent(rescontent);
				resHandler.setKey(partnerKey);
				
				//获取返回参数
				SortedMap responseMap = resHandler.getAllParameters();
				return responseMap;
			} else {
				logger.error("后台调用财付通退款操作通信失败");//有可能因为网络原因，请求已经处理，但未收到应答
			}
		}catch(Exception e){
			logger.error("调用外部接口【财付通退款操作】失败");
		}
		
		return null;
	}

}
