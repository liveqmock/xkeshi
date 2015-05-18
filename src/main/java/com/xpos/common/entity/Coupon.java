package com.xpos.common.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Transient;

import com.xpos.common.entity.CouponInfo.CouponInfoType;
import com.xpos.common.entity.face.Business;
import com.xpos.common.entity.face.Business.BusinessType;
import com.xpos.common.entity.face.EncryptId;
import com.xpos.common.entity.member.Member;
import com.xpos.common.entity.security.User;

/**
 * 
 * 优惠券
 * @author Johnny
 *
 */
public class Coupon extends BaseEntity implements EncryptId{

	private static final long serialVersionUID = 8743095809484479139L;

	@Column
	private CouponInfo couponInfo;
	@Column
	private String couponCode;
	@Column
	private CouponStatus status;
	@Column
	private CouponInfoType type;
	@Column
	private CouponInfo parent;
	@Column
	private String packageSerial;
	@Column
	private User user;
	@Column
	private Member member;
	@Column
	private String mobile;
	@Column
	private Long businessId;
	@Column
	private BusinessType businessType;
	@Column
	private CouponPayment payment;
	@Column
	private Refund refund;
	@Column
	private Operator operator;
	@Column
	private Date consumeDate;// 核销时间
	
	private String uniqueCode; //v2版优惠券Code
	
	//用于josn取使用时间
	private Date usedDate;
	
	@Transient
	public Boolean usable(){
		
		//优惠券异常
		if(couponInfo == null || couponInfo.getDeleted())
			return false;
		
		//未发布
		if(!couponInfo.getPublished())
			return false;
		
		Date now = new Date();
		
		//未开始或者已结束
		if(couponInfo.getStartDate().after(now) || couponInfo.getEndDate().before(now))
			return false;

		return true;
	}
	
	public enum CouponStatus{
	
		AVAILABLE(3),
		USED(4),
		EXPIRED(5),
		REFUND_APPLY(6), //提出退款申请
		REFUND_ACCEPTED(7), //退款审核通过，退款中
		REFUND_SUCCESS(8), //退款成功
		REFUND_FAIL(9); //退款失败
		
		private int state;
		CouponStatus(int state){
			this.state = state;
		}
		
		public int getState() {
			return state;
		}
		
		public static final CouponStatus queryByState(int state){
			for(CouponStatus couponStatus : CouponStatus.values()){
				if(couponStatus.getState() == state){
					return couponStatus;
				}
			}
			return null;
		}
	}


	public CouponInfo getCouponInfo() {
		return couponInfo;
	}


	public void setCouponInfo(CouponInfo couponInfo) {
		this.couponInfo = couponInfo;
	}


	public String getCouponCode() {
		return couponCode;
	}


	public void setCouponCode(String couponCode) {
		this.couponCode = couponCode;
	}


	public CouponStatus getStatus() {
		return status;
	}

	public void setStatus(CouponStatus status) {
		this.status = status;
	}


	public User getUser() {
		return user;
	}


	public void setUser(User user) {
		this.user = user;
	}
	

	public Member getMember() {
		return member;
	}


	public void setMember(Member member) {
		this.member = member;
	}


	public String getMobile() {
		return mobile;
	}


	public void setMobile(String mobile) {
		if(mobile != null){
			this.mobile = mobile.trim();
		}else{
			this.mobile = null;
		}
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
	
	public Date getUsedDate() {
		return usedDate;
	}

	public void setUsedDate(Date usedDate) {
		this.usedDate = usedDate;
	}

	public void setBusiness(Business business){
		this.businessId = business.getAccessBusinessId(Business.BusinessModel.COUPON);
		this.businessType = business.getAccessBusinessType(Business.BusinessModel.COUPON);
	}

	public CouponInfoType getType() {
		return type;
	}

	public void setType(CouponInfoType type) {
		this.type = type;
	}

	public CouponInfo getParent() {
		return parent;
	}

	public void setParent(CouponInfo parent) {
		this.parent = parent;
	}

	public String getPackageSerial() {
		return packageSerial;
	}

	public void setPackageSerial(String packageSerial) {
		this.packageSerial = packageSerial;
	}

	public CouponPayment getPayment() {
		return payment;
	}

	public void setPayment(CouponPayment payment) {
		this.payment = payment;
	}

	public Refund getRefund() {
		return refund;
	}

	public Operator getOperator() {
		return operator;
	}


	public void setOperator(Operator operator) {
		this.operator = operator;
	}


	public void setRefund(Refund refund) {
		this.refund = refund;
	}
	
	public Date getConsumeDate() {
		return consumeDate;
	}


	public void setConsumeDate(Date consumeDate) {
		this.consumeDate = consumeDate;
	}


	public String getUniqueCode() {
		return uniqueCode;
	}


	public void setUniqueCode(String uniqueCode) {
		this.uniqueCode = uniqueCode;
	}


	@Transient
	public Boolean suportRefund(){
		if(this.couponInfo.getSupportNormalRefund() != null && this.couponInfo.getSupportNormalRefund()==true
			&& (this.status==CouponStatus.AVAILABLE || this.status==CouponStatus.EXPIRED)){
			return true;
		}else if(this.couponInfo.getSupportExpiredRefund() != null && this.couponInfo.getSupportExpiredRefund()==true
				&& this.status==CouponStatus.EXPIRED ){
			return true;
		}
		return false;
	}

}
