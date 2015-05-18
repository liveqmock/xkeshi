package com.drongam.hermes.entity;


public class SMS extends BaseEntity {
	private static final long serialVersionUID = 1L;

	private String mobile;

	private String message;

	private String channel; // 通道

	private String requestIdentifiers;

	private String requestStatus;

	private String sendGateway;

	private Status status;

	private int sendCount;
	
	public enum Status {
		PENDING, EMANATE, SUCCESS, FAILD
	}

	public SMS() {
	}

	public SMS(String mobile, String message, String channel,
			String requestIdentifiers, String requestStatus,
			String sendGateway, Status status, int sendCount) {
		super();
		this.mobile = mobile;
		this.message = message;
		this.channel = channel;
		this.requestIdentifiers = requestIdentifiers;
		this.requestStatus = requestStatus;
		this.sendGateway = sendGateway;
		this.status = status;
		this.sendCount = sendCount;
	}

	@Override
	public String toString() {
		return "SMS [mobile=" + mobile + ", message=" + message + ", channel="
				+ channel + ", requestIdentifiers=" + requestIdentifiers
				+ ", requestStatus=" + requestStatus + ", sendGateway="
				+ sendGateway + ", status=" + status + ", sendCount="
				+ sendCount + "]";
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public String getSendGateway() {
		return sendGateway;
	}

	public void setSendGateway(String sendGateway) {
		this.sendGateway = sendGateway;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getRequestIdentifiers() {
		return requestIdentifiers;
	}

	public void setRequestIdentifiers(String requestIdentifiers) {
		this.requestIdentifiers = requestIdentifiers;
	}

	public String getRequestStatus() {
		return requestStatus;
	}

	public void setRequestStatus(String requestStatus) {
		this.requestStatus = requestStatus;
	}

	public int getSendCount() {
		return sendCount;
	}

	public void setSendCount(int sendCount) {
		this.sendCount = sendCount;
	}

}
