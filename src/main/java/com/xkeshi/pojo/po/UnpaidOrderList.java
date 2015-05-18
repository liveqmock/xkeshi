package com.xkeshi.pojo.po;

import java.util.Date;

/**
 * <br>Author: Snoopy <br>
 * 2015/2/10.
 */
public class UnpaidOrderList {

    private String orderNumber;
    private Date createdTime;
    private Integer itemCount;
    private String amount;
	
    
    public String getOrderNumber() {
		return orderNumber;
	}
	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}
	public Date getCreatedTime() {
		return createdTime;
	}
	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}
	public Integer getItemCount() {
		return itemCount;
	}
	public void setItemCount(Integer itemCount) {
		this.itemCount = itemCount;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}


  
}
