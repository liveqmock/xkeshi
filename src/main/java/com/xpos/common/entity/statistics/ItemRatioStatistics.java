package com.xpos.common.entity.statistics;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 【商品分析】
 * @author chengj
 */
public class ItemRatioStatistics {
	private boolean is_success = true; //是否查询成功
	private String error_msg; //错误信息
	
	private Long shop_id;
	@JsonFormat(pattern="yyyy-MM-dd",timezone = "GMT+8")
	private Date start_date;
	@JsonFormat(pattern="yyyy-MM-dd",timezone = "GMT+8")
	private Date end_date;
	
	private ItemRatioStatisticsType type;
	
	private BigDecimal today = new BigDecimal(0);
	private BigDecimal yesterday = new BigDecimal(0);
	private BigDecimal last_week = new BigDecimal(0);
	private BigDecimal last_month = new BigDecimal(0);
	
	@JsonIgnore
	private BigDecimal salesTotalAmount = new BigDecimal(0); //商品销售总量
	@JsonIgnore
	private BigDecimal orderTotalAmount = new BigDecimal(0); //点单总量
	@JsonIgnore
	private BigDecimal orderTotalSum = new BigDecimal(0); //金额总量
	
	private List<ItemRatioStatisticsDetail> detail_list = new ArrayList<>();
	
	public enum ItemRatioStatisticsType{
		SALES("销量"), //商品销量
		AMOUNT("点单量"); //点单总量
//		SUM("点单总金额"); //销售总金额
		
		private String description;
		private ItemRatioStatisticsType(String description){
			this.setDescription(description);
		}
		public String getDescription() {
			return description;
		}
		public void setDescription(String description) {
			this.description = description;
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
	public Long getShop_id() {
		return shop_id;
	}
	public void setShop_id(Long shop_id) {
		this.shop_id = shop_id;
	}
	public Date getStart_date() {
		return start_date;
	}
	public void setStart_date(Date start_date) {
		this.start_date = start_date;
	}
	public Date getEnd_date() {
		return end_date;
	}
	public void setEnd_date(Date end_date) {
		this.end_date = end_date;
	}
	public List<ItemRatioStatisticsDetail> getDetail_list() {
		return detail_list;
	}
	public void setDetail_list(List<ItemRatioStatisticsDetail> detail_list) {
		this.detail_list = detail_list;
	}
	public ItemRatioStatisticsType getType() {
		return type;
	}
	public void setType(ItemRatioStatisticsType type) {
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
	public BigDecimal getSalesTotalAmount() {
		return salesTotalAmount;
	}
	public void setSalesTotalAmount(BigDecimal salesTotalAmount) {
		this.salesTotalAmount = salesTotalAmount;
	}
	public BigDecimal getOrderTotalAmount() {
		return orderTotalAmount;
	}
	public void setOrderTotalAmount(BigDecimal orderTotalAmount) {
		this.orderTotalAmount = orderTotalAmount;
	}
	public BigDecimal getOrderTotalSum() {
		return orderTotalSum;
	}
	public void setOrderTotalSum(BigDecimal orderTotalSum) {
		this.orderTotalSum = orderTotalSum;
	}
}
