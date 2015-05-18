package com.xpos.common.entity.physicalCoupon;

import java.math.BigDecimal;

import com.xpos.common.entity.NewBaseEntity;
import com.xpos.common.entity.Order;

public class PhysicalCouponOrder extends NewBaseEntity{
	
	private static final long serialVersionUID = -3103130635194979218L;
	
	/**
	 * 订单
	 */
	private Order order;
	
	/**
	 * 外部调用时的订单编号
	 */
	private String third_order_code;
	
	/**
	 * 实体券
	 */
	private PhysicalCoupon physicalCoupon;
	
	/**
	 * 实体券名称
	 */
	private String physicalCouponName;
	
	/**
	 * 面值
	 */
	private BigDecimal amount;
	/**
	 * 相同实体券的数量
	 */
	private int counts;

	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}

	public String getThird_order_code() {
		return third_order_code;
	}

	public void setThird_order_code(String third_order_code) {
		this.third_order_code = third_order_code;
	}

	public PhysicalCoupon getPhysicalCoupon() {
		return physicalCoupon;
	}

	public void setPhysicalCoupon(PhysicalCoupon physicalCoupon) {
		this.physicalCoupon = physicalCoupon;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getPhysicalCouponName() {
		return physicalCouponName;
	}

	public void setPhysicalCouponName(String physicalCouponName) {
		this.physicalCouponName = physicalCouponName;
	}

	public int getCounts() {
		return counts;
	}

	public void setCounts(int counts) {
		this.counts = counts;
	}

	
}
