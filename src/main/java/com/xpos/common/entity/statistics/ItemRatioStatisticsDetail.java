package com.xpos.common.entity.statistics;

import java.math.BigDecimal;

public class ItemRatioStatisticsDetail {
	private String name;
	private BigDecimal sales_amount = new BigDecimal(0); //每种商品销量
	private BigDecimal sales_amount_ratio = new BigDecimal(0); //每种商品销量占比
	private BigDecimal order_amount = new BigDecimal(0); //单个商品的订单总量
	private BigDecimal order_amount_ratio = new BigDecimal(0); //每种商品订单量占比
	private BigDecimal order_sum = new BigDecimal(0); //每种商品销售总金额
	private BigDecimal order_sum_ratio = new BigDecimal(0); //每种商品总金额占比
	private BigDecimal sales_amount_per_order = new BigDecimal(0); //每种商品单均销量
	private BigDecimal avg_price = new BigDecimal(0); //每种商品均价
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public BigDecimal getSales_amount() {
		return sales_amount;
	}
	public void setSales_amount(BigDecimal sales_amount) {
		this.sales_amount = sales_amount;
	}
	public BigDecimal getSales_amount_ratio() {
		return sales_amount_ratio;
	}
	public void setSales_amount_ratio(BigDecimal sales_amount_ratio) {
		this.sales_amount_ratio = sales_amount_ratio;
	}
	public BigDecimal getOrder_amount() {
		return order_amount;
	}
	public void setOrder_amount(BigDecimal order_amount) {
		this.order_amount = order_amount;
	}
	public BigDecimal getOrder_amount_ratio() {
		return order_amount_ratio;
	}
	public void setOrder_amount_ratio(BigDecimal order_amount_ratio) {
		this.order_amount_ratio = order_amount_ratio;
	}
	public BigDecimal getOrder_sum() {
		return order_sum;
	}
	public void setOrder_sum(BigDecimal order_sum) {
		this.order_sum = order_sum;
	}
	public BigDecimal getOrder_sum_ratio() {
		return order_sum_ratio;
	}
	public void setOrder_sum_ratio(BigDecimal order_sum_ratio) {
		this.order_sum_ratio = order_sum_ratio;
	}
	public BigDecimal getSales_amount_per_order() {
		return sales_amount_per_order;
	}
	public void setSales_amount_per_order(BigDecimal sales_amount_per_order) {
		this.sales_amount_per_order = sales_amount_per_order;
	}
	public BigDecimal getAvg_price() {
		return avg_price;
	}
	public void setAvg_price(BigDecimal avg_price) {
		this.avg_price = avg_price;
	}
}
