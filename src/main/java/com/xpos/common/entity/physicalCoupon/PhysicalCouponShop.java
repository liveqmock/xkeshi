package com.xpos.common.entity.physicalCoupon;

import com.xpos.common.entity.NewBaseEntity;
import com.xpos.common.entity.Shop;

public class PhysicalCouponShop extends NewBaseEntity{
	
	private static final long serialVersionUID = -3103130635194979218L;
	
	/**
	 * 实体券
	 */
	private PhysicalCoupon physicalCoupon;
	
	/**
	 * 适用的商户
	 */
	private Shop shop;

	public PhysicalCoupon getPhysicalCoupon() {
		return physicalCoupon;
	}

	public void setPhysicalCoupon(PhysicalCoupon physicalCoupon) {
		this.physicalCoupon = physicalCoupon;
	}

	public Shop getShop() {
		return shop;
	}

	public void setShop(Shop shop) {
		this.shop = shop;
	}
	

	
	

}
