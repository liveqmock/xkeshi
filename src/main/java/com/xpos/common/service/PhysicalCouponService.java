package com.xpos.common.service;

import java.math.BigDecimal;
import java.util.List;

import com.xkeshi.pojo.vo.physicalCoupon.PhysicalCouponWriteOffVO;
import com.xpos.common.entity.physicalCoupon.PhysicalCoupon;
import com.xpos.common.entity.physicalCoupon.PhysicalCouponOrder;
import com.xpos.common.entity.physicalCoupon.PhysicalCouponShop;
import com.xpos.common.searcher.physicalCoupon.PhysicalCouponOrderSearcher;
import com.xpos.common.searcher.physicalCoupon.PhysicalCouponSearcher;
import com.xpos.common.utils.Pager;

public interface PhysicalCouponService {
	
	/**
	 * 通过商户ID查询该商户下的可用实体券
	 */
	List<PhysicalCoupon> findAvailablePhysicalCouponListByShopId(Long shopId);
	
	/**
	 * 查询实体券流水
	 */
	Pager<PhysicalCouponOrder> findOrderPhysicalCouponList(PhysicalCouponOrderSearcher orderPhysicalCouponSearcher,Pager<PhysicalCouponOrder> pager);
	
	/**
	 * 查询订单数数量
	 */
	int findCountOrderPhysicalCoupon(PhysicalCouponOrderSearcher orderPhysicalCouponSearcher);

	/**
	 * 查询核销总金额
	 */
	BigDecimal orderPhysicalCouponTotalAmount(PhysicalCouponOrderSearcher orderPhysicalCouponSearcher);

	/**
	 * 查询集团或商户实体券列表
	 */
	Pager<PhysicalCoupon> findPhysicalCouponList(PhysicalCouponSearcher searcher, Pager<PhysicalCoupon> pager);

	/**
	 * 添加实体券
	 */
	boolean add(PhysicalCoupon physicalCoupon, Long[] shopList);

	/**
	 * 修改实体券
	 */
	boolean update(PhysicalCoupon physicalCoupon, Long[] shopList);
	/**
	 * 通过ID查询实体券
	 */
	PhysicalCoupon findPhysicalCouponById(Long id);

	/**
	 * 通过实体券ID查询适用商户
	 */
	List<PhysicalCouponShop> findShopListByPhysicalCouponId(Long id);
	
	/**
	 * 通过orderNum来查询订单实体券的信息  PhysicalCouponOrder
	 */
	List<PhysicalCouponOrder> findPhysicalCouponOrderByOrderNum(String orderNumber);
	/**
	 * 启用或暂停
	 */
	boolean update(PhysicalCoupon pc);
	/**
	 * 根据ordernumber从physical_coupon_order表中计算出实体券的总金额
	*/
	BigDecimal calculatePhyAmount(String orderNum);

	Pager<PhysicalCouponWriteOffVO> getOrderPhysicalCouponList(PhysicalCouponOrderSearcher searcher,Pager<PhysicalCouponWriteOffVO> pager);


}
