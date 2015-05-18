package com.xkeshi.dao;

import java.math.BigDecimal;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.xkeshi.pojo.po.PhysicalCoupon;
import com.xkeshi.pojo.po.PhysicalCouponOrder;

/**
 * Created by david-y on 2015/1/23.
 */
public interface PhysicalCouponDAO extends BaseDAO<PhysicalCoupon> {
    /**
     * 实体券是否可用于此商户
     *
     * Enable coupon.
     *
     * @param physicalCouponId the physical coupon id
     * @param shopId the shop id
     * @return the boolean
     */
    boolean enableCoupon(@Param("physicalCouponId") Long physicalCouponId, @Param("shopId") Long shopId);

    BigDecimal sumCouponAmount(@Param("physicalCouponIds") Long[] physicalCouponIds, @Param("shopId") Long shopId);

	List<PhysicalCoupon> getAllByShopIdIgnoreStatus(@Param("shopId")Long shopId);

	/**
	 * 批量插入<b>XPOS_ORDER</b>类型订单的实体券核销记录
	 */
	int batchInsertXkeshiOrder(@Param("physicalCouponOrderList") List<PhysicalCouponOrder> physicalCouponOrderList);

	int deleteByOrderNumber(@Param("orderNumber")String orderNumber);
}
