package com.xkeshi.pojo.vo;

import java.math.BigDecimal;

/**
 * <br>Author: Snoopy <br>
 * 2015/2/10.
 */
public class UnpaidOrderListVO {

    private String orderNumber;
    private String createdTime;
    private Integer itemCount;
    private BigDecimal amount;
	
    
    public String getOrderNumber() {
		return orderNumber;
	}
	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}
	public String getCreatedTime() {
		return createdTime;
	}
	public void setCreatedTime(String createdTime) {
		this.createdTime = createdTime;
	}
	public Integer getItemCount() {
		return itemCount;
	}
	public void setItemCount(Integer itemCount) {
		this.itemCount = itemCount;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	


  
}
