package com.xpos.common.persistence.mybatis;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.xpos.common.entity.Order;
import com.xpos.common.entity.statistics.CategoryRatioStatisticsDetail;
import com.xpos.common.entity.statistics.ItemRatioStatistics;
import com.xpos.common.entity.statistics.ItemRatioStatisticsDetail;
import com.xpos.common.entity.statistics.OrderTrendDateStatisticsDetail;
import com.xpos.common.entity.statistics.OrderTrendHourlyStatisticsDetail;
import com.xpos.common.entity.statistics.ShopOrderRatioStatisticsDetail;
import com.xpos.common.persistence.BaseMapper;

public interface OrderStatisticsMapper extends BaseMapper<Order>{

	/** 查询指定时间段点单总量 */
	BigDecimal querySpecifiedPeriodOrderAmount(@Param("startDate")Date startDate, @Param("endDate")Date endDate, @Param("shopIds")Long[] shopIds);
	
	/** 查询指定时间段点单总金额 */
	BigDecimal querySpecifiedPeriodOrderSum(@Param("startDate")Date startDate, @Param("endDate")Date endDate, @Param("shopIds")Long[] shopIds);
	
	/** 查询指定时间段订单总销量 */
	BigDecimal querySpecifiedPeriodItemSales(@Param("startDate")Date startDate, @Param("endDate")Date endDate, @Param("shopIds")Long[] shopIds);
	
	/** 按小时查询指定日期的点单统计 */
	List<OrderTrendHourlyStatisticsDetail> queryHourlyOrderTrendByOneDay(@Param("date")Date date, @Param("shopIds")Long[] shopIds, @Param("type") String type);

	/** 按天查询指定日期段的点单统计 */
	List<OrderTrendDateStatisticsDetail> queryDailyOrderTrendByMultiDays(@Param("startDate")Date startDate, @Param("endDate")Date endDate,
														@Param("shopIds")Long[] shopIds, @Param("type") String type);

	/** 查询商户点单相关各项数据占比 */
	List<ShopOrderRatioStatisticsDetail> queryShopOrderRatios(@Param("startDate")Date startDate, @Param("endDate")Date endDate,
														@Param("shopIds")Long[] shopIds, @Param("type") String type);

	/** 查询品类分析相关各项数据总和&占比 */
	List<CategoryRatioStatisticsDetail> queryCategoryRatios(@Param("startDate")Date startDate, @Param("endDate")Date endDate,
														@Param("shopIds")Long[] shopIds, @Param("type") String type);
	
	/** 统计指定时间、商户范围内的商品销售总和概况 */
	ItemRatioStatistics queryItemSalesSummary(@Param("startDate")Date startDate, @Param("endDate")Date endDate, @Param("shopIds")Long[] shopIds);

	/** 查询商品分析相关各项数据总和&占比 */
	List<ItemRatioStatisticsDetail> queryItemStatisticsDetail(@Param("startDate")Date startDate, @Param("endDate")Date endDate,
														@Param("shopIds")Long[] shopIds, @Param("type") String type);

}