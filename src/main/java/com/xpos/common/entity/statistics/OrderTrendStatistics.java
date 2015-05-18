package com.xpos.common.entity.statistics;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class OrderTrendStatistics {
	private boolean is_success = true; //是否查询成功
	private String error_msg; //错误信息
	private boolean is_multi_days; //是否跨日期
	private boolean is_compare; //是否与其他时间段对比
	private Long shop_id;
	private OrderTrendStatisticsType type;
	
	private BigDecimal today = new BigDecimal(0);
	private BigDecimal yesterday = new BigDecimal(0);
	private BigDecimal last_week = new BigDecimal(0);
	private BigDecimal last_month = new BigDecimal(0);
	
	@JsonFormat(pattern="yyyy-MM-dd",timezone = "GMT+8")
	private Date start_date_1;
	@JsonFormat(pattern="yyyy-MM-dd",timezone = "GMT+8")
	private Date end_date_1;
	@JsonFormat(pattern="yyyy-MM-dd",timezone = "GMT+8")
	private Date start_date_2;
	@JsonFormat(pattern="yyyy-MM-dd",timezone = "GMT+8")
	private Date end_date_2;
	
	private List<OrderTrendHourlyStatistics> hourly_statistics = new ArrayList<>();
	private List<OrderTrendDailyStatistics> daily_statistics = new ArrayList<>();
	
	public enum OrderTrendStatisticsType{
		AMOUNT("点单量"),
		SUM("点单总金额");
		
		private String description;
		
		public void setDescription(String description) {
			this.description = description;
		}
		private OrderTrendStatisticsType(String description){
			this.setDescription(description);
		}
		public String getDescription() {
			return description;
		}
		
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
	public boolean isIs_multi_days() {
		return is_multi_days;
	}
	public void setIs_multi_days(boolean is_multi_days) {
		this.is_multi_days = is_multi_days;
	}
	public boolean isIs_compare() {
		return is_compare;
	}
	public void setIs_compare(boolean is_compare) {
		this.is_compare = is_compare;
	}
	public Long getShop_id() {
		return shop_id;
	}
	public void setShop_id(Long shop_id) {
		this.shop_id = shop_id;
	}
	public OrderTrendStatisticsType getType() {
		return type;
	}
	public void setType(OrderTrendStatisticsType type) {
		this.type = type;
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
	public Date getStart_date_1() {
		return start_date_1;
	}
	@JsonIgnore
	public DateTime getStart_dateTime_1(){
		return start_date_1 != null ? new DateTime(start_date_1) : null;
	}
	public void setStart_date_1(Date start_date_1) {
		this.start_date_1 = start_date_1;
	}
	public Date getEnd_date_1() {
		return end_date_1;
	}
	@JsonIgnore
	public DateTime getEnd_dateTime_1(){
		return end_date_1 != null ? new DateTime(end_date_1) : null;
	}
	public void setEnd_date_1(Date end_date_1) {
		this.end_date_1 = end_date_1;
	}
	public Date getStart_date_2() {
		return start_date_2;
	}
	@JsonIgnore
	public DateTime getStart_dateTime_2(){
		return start_date_2 != null ? new DateTime(start_date_2) : null;
	}
	public void setStart_date_2(Date start_date_2) {
		this.start_date_2 = start_date_2;
	}
	public Date getEnd_date_2() {
		return end_date_2;
	}
	@JsonIgnore
	public DateTime getEnd_dateTime_2(){
		return end_date_2 != null ? new DateTime(end_date_2) : null;
	}
	public void setEnd_date_2(Date end_date_2) {
		this.end_date_2 = end_date_2;
	}
	public List<OrderTrendHourlyStatistics> getHourly_statistics() {
		return hourly_statistics;
	}
	public void setHourly_statistics(
			List<OrderTrendHourlyStatistics> hourly_statistics) {
		this.hourly_statistics = hourly_statistics;
	}
	public List<OrderTrendDailyStatistics> getDaily_statistics() {
		return daily_statistics;
	}
	public void setDaily_statistics(List<OrderTrendDailyStatistics> daily_statistics) {
		this.daily_statistics = daily_statistics;
	}
}
