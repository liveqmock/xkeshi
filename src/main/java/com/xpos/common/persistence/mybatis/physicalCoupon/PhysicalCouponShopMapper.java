package com.xpos.common.persistence.mybatis.physicalCoupon;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.xpos.common.entity.physicalCoupon.PhysicalCouponShop;

public interface PhysicalCouponShopMapper{
	
	int insert(@Param("physicalCouponId") Long physicalCouponId, @Param("shopIds") Long[] shopIds);
	
	int delete(@Param("physicalCouponId") Long physicalCouponId);

	List<PhysicalCouponShop> findShopListByPhysicalCouponId(Long id);
}
