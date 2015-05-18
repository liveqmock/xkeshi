package com.xkeshi.pojo.vo;

import java.math.BigDecimal;

/**
 * 客户小票里面的商品信息
 */
public class PrintOrderItemSummaryVO {
	private Long printerId;
    private String name;
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
	public Long getPrinterId() {
		return printerId;
	}
	public void setPrinterId(Long printerId) {
		this.printerId = printerId;
	}
    
}
