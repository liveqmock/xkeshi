package com.xkeshi.pojo.vo.physicalCoupon;

import java.util.ArrayList;
import java.util.List;

/**
 * @author snoopy
 *
 */
public class PhysicalCouponListVO{
	
	private List<PhysicalCouponVO> physicalCoupons = new ArrayList<PhysicalCouponVO>();
	
	public PhysicalCouponListVO(){
		
	}
	
	public PhysicalCouponListVO(List<PhysicalCouponVO> physicalCoupons){
		this.physicalCoupons = physicalCoupons;
	}

	public List<PhysicalCouponVO> getPhysicalCoupons() {
		return physicalCoupons;
	}

	public void setPhysicalCoupons(List<PhysicalCouponVO> physicalCoupons) {
		this.physicalCoupons = physicalCoupons;
	}

}