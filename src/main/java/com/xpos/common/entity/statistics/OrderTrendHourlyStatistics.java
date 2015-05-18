package com.xpos.common.entity.statistics;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class OrderTrendHourlyStatistics {
	private BigDecimal today_total_amount = new BigDecimal(0); //参考日期当天总计(点单总量、点单总金额等)
	private BigDecimal yesterday_total_amount = new BigDecimal(0); //参考日期前一天总计(点单总量、点单总金额等)
	private BigDecimal last_week_total_amount = new BigDecimal(0); //参考日期上周总计(点单总量、点单总金额等)
	private List<OrderTrendHourlyStatisticsDetail> hourly_detail = new ArrayList<>(); //单日每小时统计详细数据
	
	public BigDecimal getToday_total_amount() {
		return today_total_amount;
	}
	public void setToday_total_amount(BigDecimal today_total_amount) {
		this.today_total_amount = today_total_amount;
	}
	public BigDecimal getYesterday_total_amount() {
		return yesterday_total_amount;
	}
	public void setYesterday_total_amount(BigDecimal yesterday_total_amount) {
		this.yesterday_total_amount = yesterday_total_amount;
	}
	public BigDecimal getLast_week_total_amount() {
		return last_week_total_amount;
	}
	public void setLast_week_total_amount(BigDecimal last_week_total_amount) {
		this.last_week_total_amount = last_week_total_amount;
	}
	public List<OrderTrendHourlyStatisticsDetail> getHourly_detail() {
		return hourly_detail;
	}
	public void setHour_detail(List<OrderTrendHourlyStatisticsDetail> hourly_detail) {
		this.hourly_detail = hourly_detail;
	}
}
