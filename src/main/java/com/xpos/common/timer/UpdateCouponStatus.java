package com.xpos.common.timer;

import javax.annotation.Resource;

import com.xpos.common.service.CouponPaymentService;
import com.xpos.common.service.CouponService;

public class UpdateCouponStatus {

	@Resource
	private CouponService couponService;
	
	@Resource
	private CouponPaymentService couponPaymentService;
	
	//用户优惠券有效期超时,更新Coupon.CouponStatus状态为EXPIRED
	//用户付款超时,更新CouponPayment.CouponPaymentStatus状态为PAID_TIMEOUT
	public void updateStatus() {
		couponService.updateCouponStatus();
		couponPaymentService.updateCouponPaymentStatus();
	}
	
	
}
