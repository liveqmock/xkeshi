package com.xpos.common.entity.statistics;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 【品类分析】
 * @author chengj
 */
public class CategoryRatioStatistics {
	private boolean is_success = true; //是否查询成功
	private String error_msg; //错误信息
	
	private Long shop_id;
	@JsonFormat(pattern="yyyy-MM-dd",timezone = "GMT+8")
	private Date start_date;
	@JsonFormat(pattern="yyyy-MM-dd",timezone = "GMT+8")
	private Date end_date;
	
	private CategoryRatioStatisticsType type;
	
	private BigDecimal today = new BigDecimal(0);
	private BigDecimal yesterday = new BigDecimal(0);
	private BigDecimal last_week = new BigDecimal(0);
	private BigDecimal last_month = new BigDecimal(0);
	
	private List<CategoryRatioStatisticsDetail> detail_list = new ArrayList<>();
	
	public enum CategoryRatioStatisticsType{
		SALES("商品销量"); //商品销量
//		SUM("销售总金额"); //销售总金额
		
		private String description;
		private CategoryRatioStatisticsType(String description){
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
	public CategoryRatioStatisticsType getType() {
		return type;
	}
	public void setType(CategoryRatioStatisticsType type) {
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
	public List<CategoryRatioStatisticsDetail> getDetail_list() {
		return detail_list;
	}
	public void setDetail_list(List<CategoryRatioStatisticsDetail> detail_list) {
		this.detail_list = detail_list;
	}
}
