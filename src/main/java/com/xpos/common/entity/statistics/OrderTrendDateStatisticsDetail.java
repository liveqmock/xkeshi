package com.xpos.common.entity.statistics;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class OrderTrendDateStatisticsDetail {
	@JsonFormat(pattern="yyyy-MM-dd", timezone="GMT+8")
	private Date date;
	private BigDecimal today_amount = new BigDecimal(0);
	private BigDecimal yesterday_amount = new BigDecimal(0);
	private BigDecimal last_week_amount = new BigDecimal(0);

	@JsonIgnore
	private BigDecimal temp_amount;
	
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public BigDecimal getToday_amount() {
		return today_amount;
	}
	public void setToday_amount(BigDecimal today_amount) {
		this.today_amount = today_amount;
	}
	public BigDecimal getYesterday_amount() {
		return yesterday_amount;
	}
	public void setYesterday_amount(BigDecimal yesterday_amount) {
		this.yesterday_amount = yesterday_amount;
	}
	public BigDecimal getLast_week_amount() {
		return last_week_amount;
	}
	public void setLast_week_amount(BigDecimal last_week_amount) {
		this.last_week_amount = last_week_amount;
	}
	public BigDecimal getTemp_amount() {
		return temp_amount;
	}
	public void setTemp_amount(BigDecimal temp_amount) {
		this.temp_amount = temp_amount;
	}
}
