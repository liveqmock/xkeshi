package com.xpos.common.entity;

import java.math.BigDecimal;

import javax.persistence.Column;

import com.xpos.common.entity.face.Business.BusinessType;
import com.xpos.common.entity.security.Account;

public class Payment extends BaseEntity{
	
	private static final long serialVersionUID = 8084290598172238201L;
	
	@Column
	private String serialNo; //内部订单号
	
	@Column
	private Long businessId; //充值商户ID
	
	@Column
	private BusinessType businessType; //充值商户类型
	
	@Column
	private BigDecimal amount; //充值金额

	@Column
	private Account account;
	
	@Column
	private String description;
	
	@Column
	private PaymentStatus status;
	
	@Column
	private String outerNum;  //外部支付号
	
	public enum PaymentStatus{
		WAITFORPAY, PAID, FAILD
	}
	
	public String getSerialNo() {
		return serialNo;
	}
	public void setSerialNo(String serialNo) {
		this.serialNo = serialNo;
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
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public PaymentStatus getStatus() {
		return status;
	}
	public void setStatus(PaymentStatus status) {
		this.status = status;
	}
	public String getOuterNum() {
		return outerNum;
	}
	public void setOuterNum(String outerNum) {
		this.outerNum = outerNum;
	}
	public Account getAccount() {
		return account;
	}
	public void setAccount(Account account) {
		this.account = account;
	}
	
}
