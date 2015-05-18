package com.xpos.common.persistence.mybatis.physicalCoupon;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.xpos.common.entity.physicalCoupon.PhysicalCoupon;
import com.xpos.common.searcher.physicalCoupon.PhysicalCouponSearcher;
import com.xpos.common.utils.Pager;


public interface PhysicalCouponMapper{
	
	/**
	 * 通过ID查询实体券
	 */
	PhysicalCoupon selectById(Long id);

	/**
	 * 通过商户ID查询可用的实体券
	 */
	List<PhysicalCoupon> findAvailablePhysicalCouponListByShopId(@Param("shopId")Long shopId);

	/**
	 * 查询实体券列表
	 */
	List <PhysicalCoupon> findPhysicalCouponList(@Param("searcher")PhysicalCouponSearcher searcher, @Param("pager") Pager<PhysicalCoupon> pager);

	int insert(PhysicalCoupon physicalCoupon);

	
	int update(@Param("physicalCoupon")PhysicalCoupon physicalCoupon);
	
}
