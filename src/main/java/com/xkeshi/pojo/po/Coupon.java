package com.xkeshi.pojo.po;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Table;
@Table(name="coupon")
public class Coupon   {
	
	@Column(name="id")
	protected Long id;
	
	@Column(name="couponInfo_id")
	private Long couponInfoId;
	
	@Column(name="couponCode")
	private String couponCode;
	
	/**唯一的暴露的短信优惠码*/
	@Column(name="unique_code")
	private String uniqueCode;
	
	@Column(name="status")
	private String status;
	
	@Column(name="type")
	private String type;
	
	@Column(name="parent_id")
	private Long parentId;
	
	@Column(name="packageSerial")
	private String packageSerial;
	
	@Column(name="user_id")
	private Long user;
	
	@Column(name="member_id")
	private Long memberId;
	
	@Column(name="mobile")
	private String mobile;
	
	@Column(name="businessId")
	private Long businessId;
	
	@Column(name="businessType")
	private String businessType;
	
	@Column(name="payment_id")
	private Long paymentId;
	
	@Column(name="refund_id")
	private Long refundId;
	
	@Column(name="operator_id")
	private Long operatorId;
	
	@Column(name="consumeDate")
	private Date consumeDate;// 核销时间

	public Long getCouponInfoId() {
		return couponInfoId;
	}
	public void setCouponInfoId(Long couponInfoId) {
		this.couponInfoId = couponInfoId;
	}

	public String getCouponCode() {
		return couponCode;
	}

	public void setCouponCode(String couponCode) {
		this.couponCode = couponCode;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	public String getPackageSerial() {
		return packageSerial;
	}

	public void setPackageSerial(String packageSerial) {
		this.packageSerial = packageSerial;
	}

	public Long getUser() {
		return user;
	}

	public void setUser(Long user) {
		this.user = user;
	}

	public String getUniqueCode() {
		return uniqueCode;
	}
	public void setUniqueCode(String uniqueCode) {
		this.uniqueCode = uniqueCode;
	}
	public Long getMemberId() {
		return memberId;
	}

	public void setMemberId(Long memberId) {
		this.memberId = memberId;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public Long getBusinessId() {
		return businessId;
	}

	public void setBusinessId(Long businessId) {
		this.businessId = businessId;
	}

	public String getBusinessType() {
		return businessType;
	}

	public void setBusinessType(String businessType) {
		this.businessType = businessType;
	}

	public Long getPaymentId() {
		return paymentId;
	}

	public void setPaymentId(Long paymentId) {
		this.paymentId = paymentId;
	}

	public Long getRefundId() {
		return refundId;
	}

	public void setRefundId(Long refundId) {
		this.refundId = refundId;
	}

	public Long getOperatorId() {
		return operatorId;
	}

	public void setOperatorId(Long operatorId) {
		this.operatorId = operatorId;
	}
	public Date getConsumeDate() {
		return consumeDate;
	}
	public void setConsumeDate(Date consumeDate) {
		this.consumeDate = consumeDate;
	}



}
