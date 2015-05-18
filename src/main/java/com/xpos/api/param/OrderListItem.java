package com.xpos.api.param;


public class OrderListItem {
	private long id;
	private long mid;
	private int type;
	private String registerMid;
	private int registerType;
	private String mobile;
	private int sum;
	private String signature;
	private String orderId;
	private String cardNum;
	private String tradeTime;
	private int status;
	private String traceNo;
	private String refNo;
	private String batchNo;
	private String authCode; //授权码
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getMid() {
		return mid;
	}
	public void setMid(long mid) {
		this.mid = mid;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getRegisterMid() {
		return registerMid;
	}
	public void setRegisterMid(String registerMid) {
		this.registerMid = registerMid;
	}
	public int getRegisterType() {
		return registerType;
	}
	public void setRegisterType(int registerType) {
		this.registerType = registerType;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public int getSum() {
		return sum;
	}
	public void setSum(int sum) {
		this.sum = sum;
	}
	public String getSignature() {
		return signature;
	}
	public void setSignature(String signature) {
		this.signature = signature;
	}
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public String getCardNum() {
		return cardNum;
	}
	public void setCardNum(String cardNum) {
		this.cardNum = cardNum;
	}
	public String getTradeTime() {
		return tradeTime;
	}
	public void setTradeTime(String tradeTime) {
		this.tradeTime = tradeTime;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getTraceNo() {
		return traceNo;
	}
	public void setTraceNo(String traceNo) {
		this.traceNo = traceNo;
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
	public String getAuthCode() {
		return authCode;
	}
	public void setAuthCode(String authCode) {
		this.authCode = authCode;
	}
}
