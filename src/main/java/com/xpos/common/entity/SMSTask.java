package com.xpos.common.entity;

import java.util.Date;

import javax.persistence.Column;

import com.xpos.common.entity.face.Business;
import com.xpos.common.entity.face.EncryptId;
import com.xpos.common.entity.face.Business.BusinessType;

public class SMSTask extends BaseEntity implements EncryptId{

	private static final long serialVersionUID = 8993762334855783472L;
	
	@Column
	private int count; //发送数量
	
	@Column
	private int succeeded; //成功发送数量
	
	@Column
	private int failed; //发送失败数量
	
	@Column
	private String template; //短信内容模板
	
	@Column
	private Date sendDate; //指定发送时间
	
	@Column
	private SMSTaskStatus status;
	
	@Column
	private Long businessId;
	
	@Column
	private BusinessType businessType;

	public enum SMSTaskStatus{
		PENDING, SENDING, DONE, PAUSE;
	}
	
	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getSucceeded() {
		return succeeded;
	}

	public void setSucceeded(int succeeded) {
		this.succeeded = succeeded;
	}

	public int getFailed() {
		return failed;
	}

	public void setFailed(int failed) {
		this.failed = failed;
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	public Date getSendDate() {
		return sendDate;
	}

	public void setSendDate(Date sendDate) {
		this.sendDate = sendDate;
	}

	public SMSTaskStatus getStatus() {
		return status;
	}

	public void setStatus(SMSTaskStatus status) {
		this.status = status;
	}

	public Long getBusinessId() {
		return businessId;
	}

	public void setBusinessId(Long businessId) {
		this.businessId = businessId;
	}

	public BusinessType getBusinessType() {
		return businessType;
	}

	public void setBusinessType(BusinessType businessType) {
		this.businessType = businessType;
	}
	
	public void setBusiness(Business business){
		this.businessId = business.getAccessBusinessId(Business.BusinessModel.ACTIVITY);
		this.businessType = business.getAccessBusinessType(Business.BusinessModel.ACTIVITY);
	}

}
