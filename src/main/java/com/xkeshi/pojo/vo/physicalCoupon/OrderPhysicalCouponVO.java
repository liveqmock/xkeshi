package com.xkeshi.pojo.vo.physicalCoupon;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author snoopy
 *
 */
public class OrderPhysicalCouponVO{
	
	private String orderNumber;
	
	private BigDecimal amount;
	
	private String name;
	
	private String tradeTime;
	
	public OrderPhysicalCouponVO(){
		
	}
	
	public OrderPhysicalCouponVO(String orderNumber , BigDecimal amount , String name, String tradeTime){
		this.orderNumber = orderNumber;
		this.amount = amount;
		this.name = name;
		this.tradeTime = tradeTime;
	}

	public String getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTradeTime() {
		return tradeTime;
	}

	public void setTradeTime(String tradeTime) {
		this.tradeTime = tradeTime;
	}

	

	


}