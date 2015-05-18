package com.xpos.common.entity.statistics;

import java.math.BigDecimal;

public class ShopOrderRatioStatisticsDetail {
	private Long shop_id;
	private String shop_name;
	private Integer amount = new Integer(0);
	private BigDecimal amount_ratio = new BigDecimal(0);
	private BigDecimal sum = new BigDecimal(0);
	private BigDecimal sum_ratio = new BigDecimal(0);
	
	public Long getShop_id() {
		return shop_id;
	}
	public void setShop_id(Long shop_id) {
		this.shop_id = shop_id;
	}
	public String getShop_name() {
		return shop_name;
	}
	public void setShop_name(String shop_name) {
		this.shop_name = shop_name;
	}
	public Integer getAmount() {
		return amount;
	}
	public void setAmount(Integer amount) {
		this.amount = amount;
	}
	public BigDecimal getAmount_ratio() {
		return amount_ratio;
	}
	public void setAmount_ratio(BigDecimal amount_ratio) {
		this.amount_ratio = amount_ratio;
	}
	public BigDecimal getSum() {
		return sum;
	}
	public void setSum(BigDecimal sum) {
		this.sum = sum;
	}
	public BigDecimal getSum_ratio() {
		return sum_ratio;
	}
	public void setSum_ratio(BigDecimal sum_ratio) {
		this.sum_ratio = sum_ratio;
	}
	
}
