package com.xkeshi.pojo.vo.physicalCoupon;

import java.util.Date;




public class PhysicalCouponWriteOffVO {
	
	private Date writeOffTime;
	
	private String physicalCouponName;
	
	private String amount;
	
	private String orderNumber;
	
	private String status;
	
	private String operatorName;
	
	private String shopName;

	
	

	public Date getWriteOffTime() {
		return writeOffTime;
	}

	public void setWriteOffTime(Date writeOffTime) {
		this.writeOffTime = writeOffTime;
	}

	public String getPhysicalCouponName() {
		return physicalCouponName;
	}

	public void setPhysicalCouponName(String physicalCouponName) {
		this.physicalCouponName = physicalCouponName;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getOperatorName() {
		return operatorName;
	}

	public void setOperatorName(String operatorName) {
		this.operatorName = operatorName;
	}

	public String getShopName() {
		return shopName;
	}

	public void setShopName(String shopName) {
		this.shopName = shopName;
	}
	
	
	

}
