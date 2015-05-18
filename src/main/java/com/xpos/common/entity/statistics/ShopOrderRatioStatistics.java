package com.xpos.common.entity.statistics;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class ShopOrderRatioStatistics {
	private boolean is_success = true; //是否查询成功
	private String error_msg; //错误信息
	
	private BigDecimal today = new BigDecimal(0);
	private BigDecimal yesterday = new BigDecimal(0);
	private BigDecimal last_week = new BigDecimal(0);
	private BigDecimal last_month = new BigDecimal(0);
	
	@JsonFormat(pattern="yyyy-MM-dd",timezone = "GMT+8")
	private Date start_date;
	@JsonFormat(pattern="yyyy-MM-dd",timezone = "GMT+8")
	private Date end_date;
	
	private List<ShopOrderRatioStatisticsDetail> shop_ratios = new ArrayList<>();
	
	private ShopOrderRatioStatisticsType type;
	public enum ShopOrderRatioStatisticsType{
		AMOUNT, //点单量
		SUM; //点单总金额
	}
	
	public boolean isIs_success() {
		return is_success;
	}
	public void setIs_success(boolean is_success) {
		this.is_success = is_success;
	}
	public String getError_msg() {
		return error_msg;
	}
	public void setError_msg(String error_msg) {
		this.error_msg = error_msg;
	}
	public BigDecimal getToday() {
		return today;
	}
	public void setToday(BigDecimal today) {
		if(today != null){
			this.today = today;
		}
	}
	public BigDecimal getYesterday() {
		return yesterday;
	}
	public void setYesterday(BigDecimal yesterday) {
		if(yesterday != null){
			this.yesterday = yesterday;
		}
	}
	public BigDecimal getLast_week() {
		return last_week;
	}
	public void setLast_week(BigDecimal last_week) {
		if(last_week != null){
			this.last_week = last_week;
		}
	}
	public BigDecimal getLast_month() {
		return last_month;
	}
	public void setLast_month(BigDecimal last_month) {
		if(last_month != null){
			this.last_month = last_month;
		}
	}
	public Date getStart_date() {
		return start_date;
	}
	@JsonIgnore
	public DateTime getStart_dateTime(){
		return start_date != null ? new DateTime(start_date) : null;
	}
	public void setStart_date(Date start_date) {
		this.start_date = start_date;
	}
	public Date getEnd_date() {
		return end_date;
	}
	@JsonIgnore
	public DateTime getEnd_dateTime(){
		return end_date != null ? new DateTime(end_date) : null;
	}
	public void setEnd_date(Date end_date) {
		this.end_date = end_date;
	}
	public List<ShopOrderRatioStatisticsDetail> getShop_ratios() {
		return shop_ratios;
	}
	public void setShop_ratios(List<ShopOrderRatioStatisticsDetail> shop_ratios) {
		this.shop_ratios = shop_ratios;
	}
	public ShopOrderRatioStatisticsType getType() {
		return type;
	}
	public void setType(ShopOrderRatioStatisticsType type) {
		this.type = type;
	}
}
