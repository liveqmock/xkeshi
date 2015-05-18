package com.xpos.common.entity;

import java.math.BigDecimal;

import javax.persistence.Column;

public class OrderItem extends BaseEntity{
	
	private static final long serialVersionUID = -6270660215319702014L;
	
	@Column
	private Order order;
	
	@Column
	private Item item;
	
	@Column
	private String itemName;
	
	@Column
	private BigDecimal price;
	
	@Column
	private Integer quantity;
	
	private BigDecimal amount; //单种商品小计

	public Order getOrder() {
		return order;
	}
	public void setOrder(Order order) {
		this.order = order;
	}
	public Item getItem() {
		return item;
	}
	public void setItem(Item item) {
		this.item = item;
	}
	public String getItemName() {
		return itemName;
	}
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}
	public BigDecimal getPrice() {
		return price;
	}
	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	public Integer getQuantity() {
		return quantity;
	}
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	
	
}
