package com.xpos.common.searcher;

import org.apache.commons.lang3.StringUtils;

/**
 * 
 * @author xk
 * 
 */
public class OperatorShiftSearcher {

	private String key  ;
	
	private String shopName ;
	
	private Long   shopId ;
	
	private String startTime ;
	
	private String endTime  ;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getShopName() {
		return shopName;
	}

	public void setShopName(String shopName) {
		this.shopName = shopName;
	}

	public String getStartTime() {
		return startTime;
	}
	
	public String getShiftedStartTime() {
		if (StringUtils.isNotBlank(startTime)) 
			return startTime+" 00:00:00";
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}
	
	public String getShiftedEndTime() {
		if (StringUtils.isNotBlank(endTime)) 
			return endTime+" 23:59:59";
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public Long getShopId() {
		return shopId;
	}

	public void setShopId(Long shopId) {
		this.shopId = shopId;
	}
	
	
	
	
}
