package com.xpos.common.entity;

import javax.persistence.Column;

public class SMSMessage extends BaseEntity{

	private static final long serialVersionUID = 2837623348557843472L;

	@Column
	private String messageKey;
	
	@Column
	private String mobile;
	
	@Column
	private String content;
	
	@Column
	private SMSMessageStatus status;
	
	@Column
	private SMSTask task;
	
	public enum SMSMessageStatus{
		PENDING("等待发送"),
		SENDING("发送中"),
		SUCCESS("发送成功"),
		FAILED("发送失败"),
		PAUSE("暂停");
		
		private String description;
		SMSMessageStatus(String description){
			this.description = description;
		}
		public String getDescription(){
			return description;
		}
	}

	public String getMessageKey() {
		return messageKey;
	}

	public void setMessageKey(String messageKey) {
		this.messageKey = messageKey;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public SMSMessageStatus getStatus() {
		return status;
	}

	public void setStatus(SMSMessageStatus status) {
		this.status = status;
	}

	public SMSTask getTask() {
		return task;
	}

	public void setTask(SMSTask task) {
		this.task = task;
	}
}
