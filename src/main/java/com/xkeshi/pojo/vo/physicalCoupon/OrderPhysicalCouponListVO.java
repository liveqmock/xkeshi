package com.xkeshi.pojo.vo.physicalCoupon;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author snoopy
 *
 */
public class OrderPhysicalCouponListVO{
	
	private List<OrderPhysicalCouponVO> physicalCoupons = new ArrayList<>();
	
	private int totalCount;
	
	private BigDecimal totalAmount;
	
	public OrderPhysicalCouponListVO(){
	}
	
	public OrderPhysicalCouponListVO(int totalCount , BigDecimal totalAmount , List<OrderPhysicalCouponVO> physicalCoupons){
		this.totalCount = totalCount;
		this.totalAmount = totalAmount;
		this.physicalCoupons = physicalCoupons;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	public List<OrderPhysicalCouponVO> getPhysicalCoupons() {
		return physicalCoupons;
	}

	public void setPhysicalCoupons(List<OrderPhysicalCouponVO> physicalCoupons) {
		this.physicalCoupons = physicalCoupons;
	}

	


}