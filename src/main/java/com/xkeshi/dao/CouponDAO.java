package com.xkeshi.dao;

import com.xkeshi.pojo.po.Coupon;
/**
 * 
 * @author xk
 *
 */
public interface CouponDAO  extends  BaseDAO<Coupon> {

	int insert(Coupon  coupon);
	
}
