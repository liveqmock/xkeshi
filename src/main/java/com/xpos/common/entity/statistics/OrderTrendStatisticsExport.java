package com.xpos.common.entity.statistics;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 订单分析数据综合、导出Excel
 * @author chengj
 *
 */
public class OrderTrendStatisticsExport extends OrderTrendStatistics{
	private Map<OrderTrendStatisticsType, List<OrderTrendHourlyStatisticsDetail>> hourly_export = new HashMap<>();
	private Map<OrderTrendStatisticsType, List<OrderTrendDateStatisticsDetail>> daily_export = new HashMap<>();
	
	public Map<OrderTrendStatisticsType, List<OrderTrendHourlyStatisticsDetail>> getHourly_export() {
		return hourly_export;
	}
	public void setHourly_export(Map<OrderTrendStatisticsType, List<OrderTrendHourlyStatisticsDetail>> hourly_export) {
		this.hourly_export = hourly_export;
	}
	public Map<OrderTrendStatisticsType, List<OrderTrendDateStatisticsDetail>> getDaily_export() {
		return daily_export;
	}
	public void setDaily_export(Map<OrderTrendStatisticsType, List<OrderTrendDateStatisticsDetail>> daily_export) {
		this.daily_export = daily_export;
	}
}
