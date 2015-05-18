package com.xkeshi.pojo.vo.offline;

import java.math.BigDecimal;

public class OrderItemVO {
	
	
	private Long itemId;//商品ID
	
	private String itemName;//商品名称
	
	private BigDecimal price;//单价
	
	private Integer quantity;//购买数量

	public Long getItemId() {
		return itemId;
	}

	public void setItemId(Long itemId) {
		this.itemId = itemId;
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
	
	
}
