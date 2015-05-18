package com.xpos.api;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.json.JSONObject;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Delete;
import org.restlet.resource.Post;
import org.restlet.resource.Put;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.xpos.api.result.ResCode;
import com.xpos.api.result.ValidateError;
import com.xpos.common.entity.Order;
import com.xpos.common.entity.Order.Status;
import com.xpos.common.entity.Shop;
import com.xpos.common.entity.pos.POSGatewayAccount;
import com.xpos.common.entity.pos.POSGatewayAccount.POSGatewayAccountType;
import com.xpos.common.entity.pos.POSTransaction;
import com.xpos.common.entity.pos.POSTransaction.POSTransactionStatus;
import com.xpos.common.service.OrderService;
import com.xpos.common.service.POSGatewayAccountService;
import com.xpos.common.service.POSTransactionService;
import com.xpos.common.service.UpYunServiceImpl;
import com.xpos.common.utils.FileMD5;

public class BOCOrderResource extends CacheableResource {
	private Logger logger = LoggerFactory.getLogger(BOCOrderResource.class);
	
	@Autowired
	private POSTransactionService transactionService;
	@Autowired
	private UpYunServiceImpl upYunService;
	@Autowired
	private POSGatewayAccountService posGatewayAccountService;
	@Autowired
	private OrderService orderService;
	
	/** 中行刷卡支付结果通知 */
	@SuppressWarnings("rawtypes")
	@Post("json")
	public Representation paymentNotify(JsonRepresentation entity){
		if(entity == null){
			return new JsonRepresentation(new ValidateError("-2","post请求body的json参数不能为空"));
		}
		
		//1.解析参数
		JSONObject jsonObj = entity.getJsonObject();
		Map<String, String> params = new TreeMap<>();
		for (Iterator iter = jsonObj.keys(); iter.hasNext();) {
			String key = (String)iter.next();
			String value = jsonObj.getString(key);
			params.put(key, value);
		}
		
		//find posGatewayAccount and signKey
		Long shopId = NumberUtils.toLong(params.get("mid"), -1);
		Shop shop = new Shop();
		shop.setId(shopId);
		POSGatewayAccount account = posGatewayAccountService.findByBusinessAndType(shop, POSGatewayAccountType.BOC);
		if(account == null){
			return new JsonRepresentation(new ValidateError("2801","订单更新失败(商户中行账号设置异常)"));
		}
		
		//valid params
		if(!verify(params, account.getSignKey())){
			return new JsonRepresentation(new ValidateError("2802","订单更新失败(签名异常)"));
		}
		
		//valid posTransaction
		String orderCode = params.get("orderCode");
		POSTransaction transaction = transactionService.findTransactionByCode(orderCode);
		if(transaction == null
				|| !transaction.getBusinessId().equals(shopId)){
			return new JsonRepresentation(new ValidateError("2803","订单更新失败(订单不存在)"));
		}
		
		JsonRepresentation re = null;
		
		//更新数据库
		try{
			String jsonStr = jsonObj.toString();
			logger.info("中行支付通知接口：\n"+jsonStr);
			if(StringUtils.equals("00", params.get("responseCode"))){
				transaction.setStatus(POSTransactionStatus.PAID_SUCCESS);
				transaction.setResponseCode(params.get("responseCode"));
				transaction.setRefNo(params.get("referenceNumber"));
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
				Date tradeDate = sdf.parse(params.get("transDate") + params.get("transTime"));
				transaction.setTradeDate(tradeDate);
				transaction.setCardNumber(params.get("cardNumber"));
				transaction.setCardOrg(params.get("cardOrg"));
				transaction.setTraceNo(params.get("transNo"));
				transaction.setAuthCode(params.get("transAuno"));
				transaction.setBatchNo(params.get("batchNo"));
				transaction.setTerminal(params.get("terminalId"));
				transaction.setIssueCode(params.get("issueCode"));
				transaction.setIssueName(params.get("issueName"));
				transaction.setRemark(jsonStr);
			} else { //非00均为失败
				transaction.setStatus(POSTransactionStatus.PAID_FAIL);
				transaction.setRemark(jsonStr);
				transaction.setResponseCode(params.get("responseCode"));
			}
			if(transactionService.updatePOSTransactionByCode(transaction)){
				re = new JsonRepresentation(ResCode.General.OK);
			}else{
				re = new JsonRepresentation(new ValidateError("2804","订单更新失败"));
			}
		}catch(Exception e){
			logger.error("处理中行支付通知时发生异常！", e);
			return new JsonRepresentation(ResCode.General.SERVER_INTERNAL_ERROR);
		}
		return re;
	}
	
	/** 中行刷卡支付结果通知 */
	@SuppressWarnings("rawtypes")
	@Delete("json")
	public Representation cancelNotify(JsonRepresentation entity){
		if(entity == null){
			return new JsonRepresentation(new ValidateError("-2","post请求body的json参数不能为空"));
		}
		
		//1.解析参数
		JSONObject jsonObj = entity.getJsonObject();
		Map<String, String> params = new TreeMap<>();
		for (Iterator iter = jsonObj.keys(); iter.hasNext();) {
			String key = (String)iter.next();
			String value = jsonObj.getString(key);
			params.put(key, value);
		}
		
		//find posGatewayAccount and signKey
		Long shopId = NumberUtils.toLong(params.get("mid"), -1);
		Shop shop = new Shop();
		shop.setId(shopId);
		POSGatewayAccount account = posGatewayAccountService.findByBusinessAndType(shop, POSGatewayAccountType.BOC);
		if(account == null){
			return new JsonRepresentation(new ValidateError("4801","订单更新失败(商户中行账号设置异常)"));
		}
		
		//valid params
		if(!verify(params, account.getSignKey())){
			return new JsonRepresentation(new ValidateError("4802","订单更新失败(签名异常)"));
		}
		
		//valid posTransaction
		String orderCode = params.get("orderCode");
		POSTransaction transaction = transactionService.findTransactionByCode(orderCode);
		if(transaction == null
				|| !transaction.getBusinessId().equals(shopId)){
			return new JsonRepresentation(new ValidateError("4803","订单更新失败(订单不存在)"));
		}
		
		JsonRepresentation re = null;
		
		//更新数据库
		try{
			String jsonStr = jsonObj.toString();
			logger.info("中行撤销通知接口：\n"+jsonStr);
			if(StringUtils.equals("00", params.get("responseCode"))){ //非00均为失败，不做处理
				transaction.setStatus(POSTransactionStatus.PAID_REFUND);
				transaction.setResponseCode(params.get("responseCode"));
				transaction.setRemark(jsonStr);
			}
			
			if(transactionService.updatePOSTransactionByCode(transaction)){
				//撤销相关点单，改为CANCEL状态
				Order order = orderService.findOrderByPOSTransactionId(transaction.getId());
				if(order != null){
					orderService.discardOrderAndReturnItemInventory(order.getOrderNumber(), Status.REFUND);
				}
				re = new JsonRepresentation(ResCode.General.OK);
			}else{
				re = new JsonRepresentation(new ValidateError("4804","订单更新失败"));
			}
		}catch(Exception e){
			logger.error("处理中行撤销通知时发生异常！", e);
			return new JsonRepresentation(ResCode.General.SERVER_INTERNAL_ERROR);
		}
		return re;
	}
	
	private boolean verify(Map<String, String> params, String signKey) {
		String sign = params.remove("sign");
		StringBuilder sb = new StringBuilder();
		for(Entry<String, String> entry : params.entrySet()){
			if(StringUtils.isNotBlank(entry.getValue())){
				sb.append("&").append(entry.getKey()).append("=").append(entry.getValue());
			}
		}
		sb.append("&signKey=").append(signKey);
		String sign2 = "invalid";
		try {
			sign2 = FileMD5.getFileMD5String(sb.toString().getBytes());
		} catch (IOException e) {
			logger.error("校验中行支付消息签名失败！", e);
		}
		return StringUtils.equalsIgnoreCase(sign, sign2);
	}


	/** 中行客户签字图片回调 */
	@Put("json")
	public Representation signatureUpload(JsonRepresentation entity) throws Exception {
		if(entity ==null){
			return new JsonRepresentation(new ValidateError("-1","请求body的json参数不能为空"));
		}
		JsonRepresentation re = null;
		try {
			JSONObject jsonObj = entity.getJsonObject();
			Long shopId = jsonObj.getLong("mid"); //商户号
			String registerMid = jsonObj.getString("registerMid");
			String orderCode = jsonObj.getString("orderCode");
			String picContent = jsonObj.getString("content");
			
			//verify params
			POSTransaction transaction = transactionService.findTransactionByCode(orderCode);
			if(transaction == null 
					|| !transaction.getBusinessId().equals(shopId) 
					|| !transaction.getGatewayAccount().equals(registerMid)){
				return new JsonRepresentation(new ValidateError("4201","订单不存在"));
			}else if(StringUtils.isBlank(picContent)){
				return new JsonRepresentation(new ValidateError("4202","图片内容为空"));
			}
			
			byte[] picBinary = Base64.decodeBase64(picContent);
			boolean result = upYunService.uploadImg(StringUtils.join("/order/BOC_signature/",transaction.getGatewayAccount(),"/",transaction.getCode(),".png"), picBinary);
			
			if(result){
				re = new JsonRepresentation(ResCode.General.OK);
			}else{
				logger.error("fail to save BOC signature to upyun, order code:"+transaction.getCode());
				re = new JsonRepresentation(new ValidateError("4203","图片上传失败"));
			}
			return re;
		} catch (Exception e) {
			logger.error("Cannot save signature to upyun due to "+ e.getMessage(), e);
			return new JsonRepresentation(ResCode.General.SERVER_INTERNAL_ERROR);
		}
	}
}
