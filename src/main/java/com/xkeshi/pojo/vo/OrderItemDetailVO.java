package com.xkeshi.pojo.vo;

import java.math.BigDecimal;

/**
 * 订单详情里面的商品信息
 */
public class OrderItemDetailVO {
    private String name;
    private BigDecimal price;
    private int quantity;
    private BigDecimal amount;
    
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	public BigDecimal getPrice() {
		return price;
	}
	public void setPrice(BigDecimal price) {
		this.price = price;
	}
    
}
