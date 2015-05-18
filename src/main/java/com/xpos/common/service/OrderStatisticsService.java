package com.xpos.common.service;

import com.xpos.common.entity.face.Business;
import com.xpos.common.entity.statistics.ItemRatioStatistics;
import com.xpos.common.entity.statistics.OrderTrendStatistics;
import com.xpos.common.entity.statistics.CategoryRatioStatistics;
import com.xpos.common.entity.statistics.OrderTrendStatisticsExport;
import com.xpos.common.entity.statistics.ShopOrderRatioStatistics;

public interface OrderStatisticsService {
	
	/** 查询点单分析相关统计数据 */
	OrderTrendStatistics orderTrendStatistics(OrderTrendStatistics orderTrendStatistics, Business business);
	
	/** 从点单（如点单量、点单总金额等）维度，以商户为单位，统计各商户的销售占比 */
	ShopOrderRatioStatistics orderRatioStatisticsByShop(ShopOrderRatioStatistics shopOrderRatioStatistics, Business business);
	
	/** 从品类维度，查询指定范围内所有商户的点单商品销售数据 */
	CategoryRatioStatistics categoryRatioStatisticsByShop(CategoryRatioStatistics shopCategoryRatioStatistics, Business business);

	/** 从商品维度，查询指定范围内所有商户的点单商品销售数据 */
	ItemRatioStatistics itemRatioStatisticsByShop(ItemRatioStatistics itemRatioStatistics, Business business);

	/** 针对Excel导出功能，将点单分析部分数据综合 */
	OrderTrendStatisticsExport orderTrendStatisticsForExport(OrderTrendStatisticsExport orderTrendStatisticsExport, Business business);
	
}
