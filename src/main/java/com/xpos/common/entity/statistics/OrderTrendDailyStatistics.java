package com.xpos.common.entity.statistics;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class OrderTrendDailyStatistics {
	private BigDecimal total_amount = new BigDecimal(0); //总计(点单总量、点单总金额等)
	private List<OrderTrendDateStatisticsDetail> date_detail = new ArrayList<>(); //每日统计详细数据
	
	public BigDecimal getTotal_amount() {
		return total_amount;
	}
	public void setTotal_amount(BigDecimal total_amount) {
		this.total_amount = total_amount;
	}
	public List<OrderTrendDateStatisticsDetail> getDate_detail() {
		return date_detail;
	}
	public void setDate_detail(List<OrderTrendDateStatisticsDetail> date_detail) {
		this.date_detail = date_detail;
	}
	
}
