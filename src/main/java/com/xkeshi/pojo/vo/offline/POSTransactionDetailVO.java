package com.xkeshi.pojo.vo.offline;

import java.math.BigDecimal;

public class POSTransactionDetailVO {

	private String gatewayAccount;
	private String gatewayType;
	private BigDecimal amount;//应收的现金
	private String serial;//支付流水
	private String externalSerial;
	private int status;//状态
	private String cardNumber;
	private String responseCode;
	private String terminal;
	private String location;
	private String authCode;
	private String refNo;
	private String batchNo;
	private String traceNo;
	private String cardOrg;
	private String issueCode;
	private String issueName;
	private String createdTime;//创建时间
	private String tradeTime;//付款成功时间
	private String updatedTime;//修改时间
	
	public String getGatewayAccount() {
		return gatewayAccount;
	}
	public void setGatewayAccount(String gatewayAccount) {
		this.gatewayAccount = gatewayAccount;
	}
	public String getGatewayType() {
		return gatewayType;
	}
	public void setGatewayType(String gatewayType) {
		this.gatewayType = gatewayType;
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
	public String getRefNo() {
		return refNo;
	}
	public void setRefNo(String refNo) {
		this.refNo = refNo;
	}
	public String getBatchNo() {
		return batchNo;
	}
	public void setBatchNo(String batchNo) {
		this.batchNo = batchNo;
	}
	public String getTraceNo() {
		return traceNo;
	}
	public void setTraceNo(String traceNo) {
		this.traceNo = traceNo;
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
