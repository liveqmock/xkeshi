package com.xpos.common.searcher.physicalCoupon;

import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;

import com.xpos.common.entity.Order.Status;

public class PhysicalCouponOrderSearcher{
	
	private String date; //时间(接收url后的某一天时间)
	
	private Long physicalCouponId;//实体券ID

	private Status status;//订单状态
	
	private String operatorName;//收银员
	
	private String name;//实体券名称
	
	private BigDecimal amount;//金额
	
	private String startTime;//起始时间
	
	private String endTime; //截止时间
	
	private String orderNumber;//订单号
	
	private Long [] shopIds;
	
	private String operatorSessionCode;
	
	public boolean getHasParameter(){
		return StringUtils.isNotBlank(orderNumber) || StringUtils.isNotBlank(startTime) || StringUtils.isNotBlank(endTime) 
				|| shopIds != null || status != null || StringUtils.isNotBlank(name) || StringUtils.isNotBlank(operatorName) ;
	}
	
	public Long[] getShopIds() {
		return shopIds;
	}
	public void setShopIds(Long[] shopIds) {
		this.shopIds = shopIds;
	}
	public String getOrderNumber() {
		return orderNumber;
	}
	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public Long getPhysicalCouponId() {
		return physicalCouponId;
	}
	public void setPhysicalCouponId(Long physicalCouponId) {
		this.physicalCouponId = physicalCouponId;
	}
	public Status getStatus() {
		return status;
	}
	public void setStatus(Status status) {
		this.status = status;
	}

	public String getOperatorName() {
		return operatorName;
	}

	public void setOperatorName(String operatorName) {
		this.operatorName = operatorName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	public String getStartTime() {
		if(StringUtils.isBlank(startTime)) {
			return null;
		}
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getEndTime() {
		if(StringUtils.isBlank(endTime)) {
			return null;
		}
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getOperatorSessionCode() {
		return operatorSessionCode;
	}

	public void setOperatorSessionCode(String operatorSessionCode) {
		this.operatorSessionCode = operatorSessionCode;
	}

	

	
	
}
