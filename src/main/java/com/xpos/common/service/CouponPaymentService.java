package com.xpos.common.service;

import com.xpos.common.entity.Coupon;
import com.xpos.common.entity.CouponPayment;
import com.xpos.common.entity.example.CouponPaymentExample;
import com.xpos.common.searcher.CouponPaymentSearcher;
import com.xpos.common.utils.Pager;

public interface CouponPaymentService {
	
	public Pager<CouponPayment> findPayments(CouponPaymentExample example, Pager<CouponPayment> pager);
	
	public CouponPayment findPaymentByCode(String code);

	public CouponPayment findPaymentById(Long id);

	public boolean saveCouponPayment(CouponPayment payment);
	
	public boolean updateCouponPayment(CouponPayment payment);

	public boolean updateCouponPaymentByCode(CouponPayment payment);

	public void updateCouponPaymentStatus();
	
	/** 优惠销售明细列表 */
	public Pager<CouponPayment> salesList(CouponPaymentSearcher searcher, Pager<CouponPayment> pager);
	
	/** 优惠销售售价、实际支付统计 */
	public String[] priceStatistic(CouponPaymentSearcher searcher);

	/** 优惠核销明细列表 */
	public Pager<Coupon> consumeStatisticsList(CouponPaymentSearcher searcher, Pager<Coupon> pager);

	/** 优惠核销实际支付统计 */
	public String paymentStatistic(CouponPaymentSearcher searcher);
	
	/** 支付成功，创建优惠券
	 * @param isMarkReceived 是否扣除CouponInfo库存（received字段）
	 * */
	public boolean paymentByCreateCoupon(CouponPayment couponPayment, boolean isMarkReceived);

	/**
	 * 判断限额购买时用户是否超出
	 * @param integer 
	 */
	public boolean getUserBuyQualification(Integer num,Integer userLimitCount, Long id,
			String mobile);
	
	/**
	 * 通过订单创建优惠券(检查用户购买限制数量，用户限制为1时不重复创建)
	 * @param couponPayment
	 * @return
	 */
	public boolean createCouponByPayment(CouponPayment couponPayment);
	
	public boolean createCouponByPayment(CouponPayment couponPayment, String sms);
	
	
	
	
}
