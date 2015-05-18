package com.xkeshi.pojo.vo.param;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

/**
 * 银行卡闪付(NFC)支付结果的同步回调请求参数
 * @author chengj
 */
public class BankNFCPaymentResultParam {
	private String orderType; //订单类型
	private String registerMid; //商户在银行注册的账号
	private int channel; //支付通道
	private String responseCode; //响应码
	private String responseDesc; //响应码描述
	private String referenceNumber; //系统参考号
	private String amount; //交易金额
	private String transDate; //交易日期，比如"20150603"表示2015-06-03
	private String transTime; //交易时间，比如"125514"表示12:55:14
	private String cardNumber; //银行卡号
	private String cardOrg; //发卡机构
	private String transNo; //POS流水号
	private String transAuno; //交易授权码
	private String batchNo; //批次号
	private String terminalId; //终端号
	private String location; //地理位置
	private String issueCode; //发卡行代码
	private String issueName; //发卡行名称
	private String sign; //加密签名
	
	public String getOrderType() {
		return orderType;
	}
	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}
	public String getRegisterMid() {
		return registerMid;
	}
	public void setRegisterMid(String registerMid) {
		this.registerMid = registerMid;
	}
	public int getChannel() {
		return channel;
	}
	public void setChannel(int channel) {
		this.channel = channel;
	}
	public String getResponseCode() {
		return responseCode;
	}
	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}
	public String getResponseDesc() {
		return responseDesc;
	}
	public void setResponseDesc(String responseDesc) {
		this.responseDesc = responseDesc;
	}
	public String getReferenceNumber() {
		return referenceNumber;
	}
	public void setReferenceNumber(String referenceNumber) {
		this.referenceNumber = referenceNumber;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getTransDate() {
		return transDate;
	}
	public void setTransDate(String transDate) {
		this.transDate = transDate;
	}
	public String getTransTime() {
		return transTime;
	}
	public void setTransTime(String transTime) {
		this.transTime = transTime;
	}
	public String getCardNumber() {
		return cardNumber;
	}
	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}
	public String getCardOrg() {
		return cardOrg;
	}
	public void setCardOrg(String cardOrg) {
		this.cardOrg = cardOrg;
	}
	public String getTransNo() {
		return transNo;
	}
	public void setTransNo(String transNo) {
		this.transNo = transNo;
	}
	public String getTransAuno() {
		return transAuno;
	}
	public void setTransAuno(String transAuno) {
		this.transAuno = transAuno;
	}
	public String getBatchNo() {
		return batchNo;
	}
	public void setBatchNo(String batchNo) {
		this.batchNo = batchNo;
	}
	public String getTerminalId() {
		return terminalId;
	}
	public void setTerminalId(String terminalId) {
		this.terminalId = terminalId;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getIssueCode() {
		return issueCode;
	}
	public void setIssueCode(String issueCode) {
		this.issueCode = issueCode;
	}
	public String getIssueName() {
		return issueName;
	}
	public void setIssueName(String issueName) {
		this.issueName = issueName;
	}
	public String getSign() {
		return sign;
	}
	public void setSign(String sign) {
		this.sign = sign;
	}
	
	public String toString(){
		Map<String, String> params = new TreeMap<>();
		Field[] fields = BankNFCPaymentResultParam.class.getDeclaredFields();
		for(Field field : fields){
			try {
				if(!StringUtils.equals("sign", field.getName()) && field.get(this) != null){
					params.put(field.getName(), field.get(this).toString());
				}
			} catch (IllegalArgumentException | IllegalAccessException e) {
				continue;
			}
		}
		StringBuilder sb = new StringBuilder();
		for(Entry<String, String> entry : params.entrySet()){
			if(StringUtils.isNotBlank(entry.getValue())){
				sb.append("&").append(entry.getKey()).append("=").append(entry.getValue());
			}
		}
		return sb.toString();
		
	}
}
