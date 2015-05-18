package com.xpos.common.persistence.mybatis.physicalCoupon;

import java.math.BigDecimal;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.xkeshi.pojo.vo.physicalCoupon.PhysicalCouponWriteOffVO;
import com.xpos.common.entity.face.Business;
import com.xpos.common.entity.physicalCoupon.PhysicalCouponOrder;
import com.xpos.common.searcher.OrderSearcher;
import com.xpos.common.searcher.physicalCoupon.PhysicalCouponOrderSearcher;
import com.xpos.common.utils.Pager;


public interface PhysicalCouponOrderMapper {
	
	/**
	 * 通过ID查询实体券流水
	 */
	PhysicalCouponOrder selectById(Long id);

	/**
	 * 查询实体券流水
	 */
	List<PhysicalCouponOrder> findOrderPhysicalCouponList(@Param("searcher")PhysicalCouponOrderSearcher searcher,
			@Param("pager")Pager<PhysicalCouponOrder> pager);
	
	/**
	 * 查询订单数数量
	 */
	int countOrderPhysicalCoupon(@Param("searcher")PhysicalCouponOrderSearcher searcher);
	
	/**
	 * 实体券核销总金额
	 */
	BigDecimal orderPhysicalCouponTotalAmount(@Param("searcher")PhysicalCouponOrderSearcher searcher);
	
	List<PhysicalCouponOrder> findPhysicalCouponOrderByOrderNum(String orderNumber);
	/**
	 * 根据ordernumber从physical_coupon_order表中计算出实体券的总金额
	 */
	BigDecimal calculatePhyAmount(String orderNum);
	/**实体券的金额统计,这个是在点单列表中所有订单的实体券金额的总和*/
	BigDecimal getPhysicalAmount(@Param("business")Business business, @Param("searcher")OrderSearcher searcher);

	List<PhysicalCouponWriteOffVO> getOrderPhysicalCouponList(@Param("searcher")PhysicalCouponOrderSearcher searcher,@Param("pager")Pager<PhysicalCouponWriteOffVO> pager);

	String getOrderStatus(@Param("physicalCouponWriteOffVO")PhysicalCouponWriteOffVO physicalCouponWriteOffVO);

	Integer getOrderPhysicalCouponListCount(@Param("searcher")PhysicalCouponOrderSearcher searcher);

	
	
	
}
