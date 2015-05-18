package com.xpos.common.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Transient;

import com.xpos.common.entity.face.EncryptId;

/**
 * 
 * 第三方优惠券
 * @author Snoopy
 *
 */
public class ThirdpartyCoupon extends BaseEntity implements EncryptId{

	private static final long serialVersionUID = 8743095809484479139L;

	@Column
	private ThirdpartyCouponInfo thirdpartycouponInfo;
	@Column
	private String couponCode;
	@Column
	private String password;
	@Column
	private ThirdCouponStatus status;
	@Column
	private String mobile;
	
	
	@Transient
	public Boolean usable(){
		
		//优惠券异常
		if(thirdpartycouponInfo == null || thirdpartycouponInfo.getDeleted())
			return false;
		
		//未发布
		if(!thirdpartycouponInfo.getPublished())
			return false;
		
		Date now = new Date();
		
		//未开始或者已结束
		if(thirdpartycouponInfo.getStartDate().after(now) || thirdpartycouponInfo.getEndDate().before(now))
			return false;

		return true;
	}
	
	public enum ThirdCouponStatus{
		
		PENDING(2),
		AVAILABLE(3),
		USED(4),
		EXPIRED(5);
		
		private int state;
		ThirdCouponStatus(int state){
			this.state = state;
		}
		
		public int getState() {
			return state;
		}
		
		public static final ThirdCouponStatus queryByState(int state){
			for(ThirdCouponStatus couponStatus : ThirdCouponStatus.values()){
				if(couponStatus.getState() == state){
					return couponStatus;
				}
			}
			return null;
		}
	}



	public ThirdpartyCouponInfo getCouponInfo() {
		return thirdpartycouponInfo;
	}
	public void setCouponInfo(ThirdpartyCouponInfo couponInfo) {
		this.thirdpartycouponInfo = couponInfo;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getCouponCode() {
		return couponCode;
	}
	public void setCouponCode(String couponCode) {
		this.couponCode = couponCode;
	}
	public ThirdCouponStatus getStatus() {
		Date now = new Date();
		if (this.status.equals(ThirdCouponStatus.AVAILABLE) && (thirdpartycouponInfo.getEndDate().before(now))) {
			return ThirdCouponStatus.EXPIRED;
		}
		return status;
	}
	public void setStatus(ThirdCouponStatus status) {
		this.status = status;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	

}
