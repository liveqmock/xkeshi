package com.xpos.common.utils;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xpos.common.entity.pos.POSTransaction;
import com.xpos.common.exception.SignEncException;

/**
 * 盛付通接口交互工具类
 */
@Component
public class ShengPayUtil {
	private final static Logger logger = LoggerFactory.getLogger(ShengPayUtil.class);
	
	/** 拼接盛付通单笔交易查询接口参数 */
	public static Map<String, String> generateQueryParamMap(POSTransaction transaction, String type, String privateKeyPath) {
		Map<String, String> paramMap = new HashMap<>();
		DateTime dateTime = new DateTime(transaction.getTradeDate());
		paramMap.put("dataType", "1");
		paramMap.put("txnType", type);
		paramMap.put("merchantId", transaction.getGatewayAccount());
		paramMap.put("merOrderId", transaction.getCode());
		paramMap.put("txnDate", dateTime.toString("yyyyMMdd"));
		String originalParamStr = "1" + type + transaction.getGatewayAccount() + transaction.getCode() + dateTime.toString("yyyyMMdd");
		String sign = SignEncodeUtil.shengPaySignData(originalParamStr, privateKeyPath);
		paramMap.put("sign", sign);
		logger.debug("提交盛付通查询接口原始参数：\n 【" + originalParamStr + "】， sign=【" + sign + "】");
		return paramMap;
	}
	
	/** 盛付通：验签，并将收到的数据转成MAP */
	public static Map<String, String> parseResponseParams(String respStr, String publicKeyPath) throws SignEncException {
		Map<String, String> responseMap = new LinkedHashMap<>();
		if(!StringUtils.isBlank(respStr)){
			try {
				JSONObject jsonObject = JSON.parseObject(respStr);
				responseMap.put("txnType", jsonObject.getString("txnType"));
				responseMap.put("cur", jsonObject.getString("cur"));
				responseMap.put("amt", jsonObject.getString("amt"));
				responseMap.put("merchantId", jsonObject.getString("merchantId"));
				responseMap.put("terminalId", jsonObject.getString("terminalId"));
				responseMap.put("traceNo", jsonObject.getString("traceNo"));
				responseMap.put("batchNo", jsonObject.getString("batchNo"));
				responseMap.put("orderId", jsonObject.getString("orderId"));
				responseMap.put("txnTime", jsonObject.getString("txnTime"));
				responseMap.put("txnRef", jsonObject.getString("txnRef"));
				responseMap.put("respCode", jsonObject.getString("respCode"));
				responseMap.put("merOrderId", jsonObject.getString("merOrderId"));
				responseMap.put("shortPan", jsonObject.getString("shortPan"));
				responseMap.put("extData", jsonObject.getString("extData"));
				responseMap.put("sign", jsonObject.getString("sign"));
				
				verifySignature(responseMap, publicKeyPath);
				responseMap.put("respCodeDesc", translateRespCode(jsonObject.getString("respCode")));
			} catch (SignEncException e) {
				logger.error("解析盛付通响应的JSON文本异常！", e);
				throw e;
			}
		}
		return responseMap;
	}

	public static void verifySignature(Map<String, String> responseMap, String publicKeyPath) throws SignEncException {
		String sign = responseMap.remove("sign");
		
		StringBuilder sb = new StringBuilder();
		for(Entry<String, String> entry : responseMap.entrySet()){
			if(!StringUtils.isBlank(entry.getValue())){
				sb.append(entry.getValue());
			}
		}
		
		if(!SignEncodeUtil.shengPayVerify(sb.toString(), sign, publicKeyPath)){
			throw new SignEncException();
		}
	}
	
	private static String translateRespCode(String respCode){
		switch(respCode){
			case "12": return "无效交易";
			case "25": return "找不到原交易";
			case "30": return "格式错误";
			case "58": return "不支持的交易";
			case "63": return "校验签名失败";
			case "96": return "系统异常";
			default: return "";
		}
	}

}
