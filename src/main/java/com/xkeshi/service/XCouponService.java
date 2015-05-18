package com.xkeshi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xkeshi.dao.CouponDAO;
import com.xkeshi.pojo.po.Coupon;
import com.xpos.common.utils.CouponUtil;

@Service
public class XCouponService {

	@Autowired(required = false)
	private CouponDAO  couponDAO  ;
	
	@Transactional
	public boolean insert(Coupon   coupon){
		if (coupon == null) 
			return false;
		coupon.setCouponCode(CouponUtil.newCode());
		coupon.setUniqueCode(CouponUtil.getUniqueCode());
		return couponDAO.insert(coupon) >0;
	}
	 
}
