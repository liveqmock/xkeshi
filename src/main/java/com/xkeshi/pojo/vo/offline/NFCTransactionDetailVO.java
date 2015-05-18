package com.xkeshi.pojo.vo.offline;

import java.math.BigDecimal;

public class NFCTransactionDetailVO {

	private String registerMid;
	private int posChannelId;
	private BigDecimal amount;//应收的现金
	private String serial;//支付流水
	private String externalSerial;
	private int status;//状态
	private String cardNumber;
	private String responseCode;
	private String terminal;
	private String location;
	private String authCode;
	private String referenceNumber;
	private String batchNumber;
	private String traceNumber;
	private String cardOrg;
	private String issueCode;
	private String issueName;
	private String createdTime;//创建时间
	private String tradeTime;//付款成功时间
	private String updatedTime;//修改时间
	
	public String getRegisterMid() {
		return registerMid;
	}
	public void setRegisterMid(String registerMid) {
		this.registerMid = registerMid;
	}
	public int getPosChannelId() {
		return posChannelId;
	}
	public void setPosChannelId(int posChannelId) {
		this.posChannelId = posChannelId;
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
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getCardNumber() {
		return cardNumber;
	}
	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}
	public String getResponseCode() {
		return responseCode;
	}
	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}
	public String getTerminal() {
		return terminal;
	}
	public void setTerminal(String terminal) {
		this.terminal = terminal;
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
	public String getReferenceNumber() {
		return referenceNumber;
	}
	public void setReferenceNumber(String referenceNumber) {
		this.referenceNumber = referenceNumber;
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
	public String getCreatedTime() {
		return createdTime;
	}
	public void setCreatedTime(String createdTime) {
		this.createdTime = createdTime;
	}
	public String getTradeTime() {
		return tradeTime;
	}
	public void setTradeTime(String tradeTime) {
		this.tradeTime = tradeTime;
	}
	public String getUpdatedTime() {
		return updatedTime;
	}
	public void setUpdatedTime(String updatedTime) {
		this.updatedTime = updatedTime;
	}
	
}
