package com.xpos.common.entity.statistics;

import java.math.BigDecimal;

public class CategoryRatioStatisticsDetail {
	private String name;
	private BigDecimal amount = new BigDecimal(0);
	private BigDecimal amount_ratio = new BigDecimal(0);
	
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
	public BigDecimal getAmount_ratio() {
		return amount_ratio;
	}
	public void setAmount_ratio(BigDecimal amount_ratio) {
		this.amount_ratio = amount_ratio;
	}
	
}
