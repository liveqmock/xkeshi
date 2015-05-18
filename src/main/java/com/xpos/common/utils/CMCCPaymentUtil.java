package com.xpos.common.utils;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.umpay.SignEncException;
import com.umpay.mer.UmMerHelper;
import com.xpos.common.entity.pos.POSTransaction;

@Component
public class CMCCPaymentUtil {
	private final static Logger logger = LoggerFactory.getLogger(CMCCPaymentUtil.class);

	@Value("#{settings['cmccTicket.orderUrl']}")
	public String CMCC_TICKET_ORDER_URL;
	
	@Value("#{settings['cmccTicket.merId']}")
	public String CMCC_TICKET_MER_ID;

	@Value("#{settings['cmccTicket.channelId']}")
	public String CMCC_TICKET_CHANNELID;
	
	@Value("#{settings['cmccTicket.umMerHelper.certFilePath']}")
	public String certFilePath;
	
	@Value("#{settings['cmccTicket.umMerHelper.privateKeyFilePath']}")
	public String privateKeyFilePath;
	
	private UmMerHelper umMerHelper;
	
	private boolean initialized = false;

	public void init() throws IOException{
		if(initialized){
			return;
		}
		umMerHelper = new UmMerHelper();
		umMerHelper.setCertFilePath(certFilePath);
		umMerHelper.setPrivateKeyFilePath(privateKeyFilePath);
		umMerHelper.init();
		initialized = true;
	}

	/** 生成电子券订单支付XML */
	public final String generateCMCCTicketPayOrderXml(POSTransaction transaction, String uid, String deviceNumber) {
		List<String> rsaList = new ArrayList<>();
		rsaList.add("PWD");
		
		DateTime now = new DateTime();
		LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
		params.put("MCODE", "101460");
		params.put("MID", now.toString("yyyyMMddHHmmss"));
		params.put("DATE", now.toString("yyyyMMdd"));
		params.put("TIME", now.toString("HHmmss"));
		params.put("BUSTYP", "WAT01");
		params.put("UID", uid);
		params.put("MOBILEID", transaction.getMobile());
		params.put("MERID", CMCC_TICKET_MER_ID);
		params.put("CHANNELID", CMCC_TICKET_CHANNELID);
		params.put("ORDERID", transaction.getCode());
		params.put("AMOUT", ""+BigDecimal.valueOf(100).multiply(transaction.getSum()).setScale(2, RoundingMode.HALF_UP).intValue());
		params.put("ORDERDATE", now.toString("yyyyMMdd"));
		params.put("TXNTYP", "B");
		params.put("PRODUCTDESC", "XPOS电子券支付");
		params.put("PRODUCTID", "00001");
		params.put("PRODUCTNAME", "XPOS电子券支付");
		params.put("ACCESSTYPE", "P");
		params.put("PWD", transaction.getPassword());
		params.put("POSID", deviceNumber);
		params.put("SIGN", "");
		try{
			String xmlStr = umMerHelper.setupRequestParams(params, "GBK", rsaList);
			logger.debug("提交移动平台电子券订单支付XML\n【" + xmlStr + "】");
			return xmlStr;
		} catch (Exception e){
			throw new RuntimeException(e);
		}
	}

	/** 生成电子券余额查询XML */
	public final String generateCMCCTicketBalanceQueryXml(String mobile){
		LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
		DateTime now = new DateTime();
		
		params.put("MCODE", "101760");
		params.put("MID", now.toString("yyyyMMddHHmmss"));
		params.put("DATE", now.toString("yyyyMMdd"));
		params.put("TIME", now.toString("HHmmss"));
		params.put("MERID", CMCC_TICKET_MER_ID);
		params.put("CHANNELID", CMCC_TICKET_CHANNELID);
		params.put("MOBILEID", mobile);
		params.put("SIGN", "");

		try{
			String xmlStr = umMerHelper.setupRequestParams(params, false);
			logger.debug("提交移动平台电子券余额查询XML\n【" + xmlStr + "】");
			return xmlStr;
		} catch (Exception e){
			throw new RuntimeException(e);
		}
	}
	
	/** 生成电子券开通支付个人协议XML */
	public final String generateCMCCTicketSignAgreementXml(String mobile){
		LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
		DateTime now = new DateTime();
		params.put("MCODE", "101472");
		params.put("MID", now.toString("yyyyMMddHHmmss"));
		params.put("DATE", now.toString("yyyyMMdd"));
		params.put("TIME", now.toString("HHmmss"));
		params.put("BUSTYP", "WAT01");
		params.put("MERID", CMCC_TICKET_MER_ID);
		params.put("CHANNELID", CMCC_TICKET_CHANNELID);
		params.put("MOBILEID", mobile);
		params.put("REMARK", "xpos");
		params.put("MUSRID", mobile);
		params.put("CONFIRMFLG", "Y");
//		params.put("EFFDATE", now.toString("yyyyMMdd"));
		params.put("EFFDATE", "20410908");
		params.put("EXPDATE", "21000101");
		params.put("SIGN", "");
		
		try{
			String xmlStr = umMerHelper.setupRequestParams(params, false);
			logger.debug("提交移动平台电子券开通个人支付协议XML\n 【" + xmlStr + "】");
			return xmlStr;
		} catch (Exception e){
			throw new RuntimeException(e);
		}
	}
	
	/** 无磁有密支付个人协议查询XML */
	public final String generateCMCCTicketAgreementQueryXml(String mobile){
		LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
		DateTime now = new DateTime();
		params.put("MCODE", "101474");
		params.put("MID", now.toString("yyyyMMddHHmmss"));
		params.put("DATE", now.toString("yyyyMMdd"));
		params.put("TIME", now.toString("HHmmss"));
		params.put("BUSTYP", "WAT01");
		params.put("MERID", CMCC_TICKET_MER_ID);
		params.put("CHANNELID", CMCC_TICKET_CHANNELID);
		params.put("MUSRID", mobile);
		params.put("SIGN", "");
		
		try{
			String xmlStr = umMerHelper.setupRequestParams(params, false);
			logger.debug("提交移动平台电子券开通个人支付协议XML\n 【" + xmlStr + "】");
			return xmlStr;
		} catch (Exception e){
			throw new RuntimeException(e);
		}
	}
	
	/** 商户无磁有密/无密支付冲正XML */
	public final String generateCMCCTicketRevocationXml(POSTransaction transaction) {
		LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
		DateTime now = new DateTime();
		params.put("MCODE", "101469");
		params.put("MID", now.toString("yyyyMMddHHmmss"));
		params.put("DATE", now.toString("yyyyMMdd"));
		params.put("TIME", now.toString("HHmmss"));
		params.put("MERID", CMCC_TICKET_MER_ID);
		params.put("CHANNELID", CMCC_TICKET_CHANNELID);
		params.put("ORDERID", transaction.getCode());
		params.put("ODATE", new DateTime(transaction.getCreateDate()).toString("yyyyMMdd"));
		params.put("SIGN", "");
		
		try{
			String xmlStr = umMerHelper.setupRequestParams(params, false);
			logger.debug("提交移动平台电子券商户无磁有密/无密支付冲正XML\n 【" + xmlStr + "】");
			return xmlStr;
		} catch (Exception e){
			throw new RuntimeException(e);
		}
	}
	
	/** 移动电子券：验签，并将收到的数据转成MAP */
	public final Map<String, String> parseResponseParams(String respStr) throws SignEncException {
		Map<String, String> responseMap = new HashMap<>();
		if(!StringUtils.isBlank(respStr)){
			try {
				responseMap = umMerHelper.parseResponseParams(respStr);
			} catch (SignEncException e) {
				logger.error("解析移动电子券响应的XML文本异常！", e);
				throw e;
			}
		}
		return responseMap;
	}

	public static void main(String[] args) throws IOException {
		UmMerHelper umMerHelper = new UmMerHelper();
		umMerHelper.setCertFilePath("D:/umpay/cmcc_ticket/testUmpay.cert.crt");
		umMerHelper.setPrivateKeyFilePath("D:/umpay/cmcc_ticket/testMer.key.p8");
		umMerHelper.init();
		String xml = new CMCCPaymentUtil().generateCMCCTicketAgreementQueryXml("15088790007");
		System.out.println(xml);
	}
}
