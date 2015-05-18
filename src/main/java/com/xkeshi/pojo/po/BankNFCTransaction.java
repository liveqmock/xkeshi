package com.xkeshi.pojo.po;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.xpos.common.entity.NewBaseEntity;

/**
 * 银行卡闪付（NFC）支付流水
 * @author chengj
 */
public class BankNFCTransaction extends NewBaseEntity{
	private static final long serialVersionUID = 3404784769434293804L;
	
	private String orderNumber; //爱客仕系统内部订单号
	private String prepaidCardChargeOrderCode; //预付卡充值订单号
	private String thirdOrderCode; //第三方平台订单号
	private String mobile; //买家手机号
	private String registerMid; //卖家银行账号
	private int posChannel; //支付通道类型
	private BigDecimal amount;
	private String serial; //该笔支付在爱客仕平台的唯一流水号
	private String externalSerial; //第三方平台的交易流水号
	private int paymentStatus; //支付状态
	private String responseCode;
	private String cardNumber;
	private String referenceNumber;
	private String location;
	private String authCode;
	private String batchNumber;
	private String traceNumber;
	private String cardOrg;
	private String issueCode;
	private String issueName;
	private String terminal;
	private Date tradeTime;
	
	public String getOrderNumber() {
		return orderNumber;
	}
	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}
	public String getPrepaidCardChargeOrderCode() {
		return prepaidCardChargeOrderCode;
	}
	public void setPrepaidCardChargeOrderCode(String prepaidCardChargeOrderCode) {
		this.prepaidCardChargeOrderCode = prepaidCardChargeOrderCode;
	}
	public String getThirdOrderCode() {
		return thirdOrderCode;
	}
	public void setThirdOrderCode(String thirdOrderCode) {
		this.thirdOrderCode = thirdOrderCode;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getRegisterMid() {
		return registerMid;
	}
	public void setRegisterMid(String registerMid) {
		this.registerMid = registerMid;
	}
	public int getPosChannel() {
		return posChannel;
	}
	public void setPosChannel(int posChannel) {
		this.posChannel = posChannel;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	public String getSerial() {
		return serial;
	}
	public void setSerial(String serial) {
		this.serial = serial;
	}
	public String getExternalSerial() {
		return externalSerial;
	}
	public void setExternalSerial(String externalSerial) {
		this.externalSerial = externalSerial;
	}
	public int getPaymentStatus() {
		return paymentStatus;
	}
	public void setPaymentStatus(int paymentStatus) {
		this.paymentStatus = paymentStatus;
	}
	public String getResponseCode() {
		return responseCode;
	}
	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}
	public String getCardNumber() {
		return cardNumber;
	}
	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}
	public String getReferenceNumber() {
		return referenceNumber;
	}
	public void setReferenceNumber(String referenceNumber) {
		this.referenceNumber = referenceNumber;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getAuthCode() {
		return authCode;
	}
	public void setAuthCode(String authCode) {
		this.authCode = authCode;
	}
	public String getBatchNumber() {
		return batchNumber;
	}
	public void setBatchNumber(String batchNumber) {
		this.batchNumber = batchNumber;
	}
	public String getTraceNumber() {
		return traceNumber;
	}
	public void setTraceNumber(String traceNumber) {
		this.traceNumber = traceNumber;
	}
	public String getCardOrg() {
		return cardOrg;
	}
	public void setCardOrg(String cardOrg) {
		this.cardOrg = cardOrg;
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
	public String getTerminal() {
		return terminal;
	}
	public void setTerminal(String terminal) {
		this.terminal = terminal;
	}
	public Date getTradeTime() {
		return tradeTime;
	}
	public void setTradeTime(Date tradeTime) {
		this.tradeTime = tradeTime;
	}

	public String getOrderCodeByType(String orderType){
		if(StringUtils.equals("XPOS_ORDER", orderType)){
			return getOrderNumber();
		}else if(StringUtils.equals("XPOS_PREPAID", orderType)){
			return getPrepaidCardChargeOrderCode();
		}else if(StringUtils.equals("THIRD_ORDER", orderType)){
			return getThirdOrderCode();
		}
		return null;
	}
	
	public void setOrderCodeByType(String orderCode, String orderType){
		if(StringUtils.equals("XPOS_ORDER", orderType)){
			setOrderNumber(orderCode);
		}else if(StringUtils.equals("XPOS_PREPAID", orderType)){
			setPrepaidCardChargeOrderCode(orderCode);
		}else if(StringUtils.equals("THIRD_ORDER", orderType)){
			setThirdOrderCode(orderCode);
		}
	}
}
