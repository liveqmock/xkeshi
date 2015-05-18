package com.xpos.common.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xpos.common.entity.Shop;
import com.xpos.common.entity.face.Business;
import com.xpos.common.entity.face.Business.BusinessType;
import com.xpos.common.entity.statistics.CategoryRatioStatistics;
import com.xpos.common.entity.statistics.CategoryRatioStatistics.CategoryRatioStatisticsType;
import com.xpos.common.entity.statistics.CategoryRatioStatisticsDetail;
import com.xpos.common.entity.statistics.ItemRatioStatistics;
import com.xpos.common.entity.statistics.ItemRatioStatistics.ItemRatioStatisticsType;
import com.xpos.common.entity.statistics.ItemRatioStatisticsDetail;
import com.xpos.common.entity.statistics.OrderTrendDailyStatistics;
import com.xpos.common.entity.statistics.OrderTrendDateStatisticsDetail;
import com.xpos.common.entity.statistics.OrderTrendHourlyStatistics;
import com.xpos.common.entity.statistics.OrderTrendHourlyStatisticsDetail;
import com.xpos.common.entity.statistics.OrderTrendStatistics;
import com.xpos.common.entity.statistics.OrderTrendStatistics.OrderTrendStatisticsType;
import com.xpos.common.entity.statistics.OrderTrendStatisticsExport;
import com.xpos.common.entity.statistics.ShopOrderRatioStatistics;
import com.xpos.common.entity.statistics.ShopOrderRatioStatistics.ShopOrderRatioStatisticsType;
import com.xpos.common.entity.statistics.ShopOrderRatioStatisticsDetail;
import com.xpos.common.persistence.mybatis.ItemCategoryMapper;
import com.xpos.common.persistence.mybatis.ItemMapper;
import com.xpos.common.persistence.mybatis.OrderStatisticsMapper;

@Service
public class OrderStatisticsServiceImpl implements OrderStatisticsService{
	private Logger logger = LoggerFactory.getLogger(OrderStatisticsServiceImpl.class);
	
	@Autowired
	private ShopService shopService;
	@Autowired
	private OrderStatisticsMapper orderStatisticsMapper;
	@Autowired
	private ItemMapper itemMapper;
	@Autowired
	private ItemCategoryMapper itemCategoryMapper;
	
	/**
	 * 统计分析【点单分析】
	 */
	@Override
	public OrderTrendStatistics orderTrendStatistics(OrderTrendStatistics orderTrendStatistics, Business business) {
		Date startDate_1 = orderTrendStatistics.getStart_date_1();
		Date endDate_1 = orderTrendStatistics.getEnd_date_1();
		Date startDate_2 = orderTrendStatistics.getStart_date_2();
		Date endDate_2 = orderTrendStatistics.getEnd_date_2();
		
		//1.date validate
		String errorMsg = dateValidation(startDate_1, endDate_1, startDate_2, endDate_2);
		if(StringUtils.isNotBlank(errorMsg)){
			orderTrendStatistics.setIs_success(false);
			orderTrendStatistics.setError_msg(errorMsg);
			return orderTrendStatistics;
		}else{
			orderTrendStatistics.setIs_multi_days(
						Days.daysBetween(orderTrendStatistics.getStart_dateTime_1(), orderTrendStatistics.getEnd_dateTime_1()).getDays() > 0);
			orderTrendStatistics.setIs_compare(startDate_2 != null);
		}
		
		//2.authorize validate with
		StringBuffer errMsg = new StringBuffer();
		Long[] shopIds = getShopIdScope(errMsg, orderTrendStatistics.getShop_id(), business);
		if(shopIds == null){
			orderTrendStatistics.setIs_success(false);
			orderTrendStatistics.setError_msg(errMsg.toString());
			return orderTrendStatistics;
		}
		
		//3.fetch data
		try{
			//查询当天、昨天、近7天、近30天4个固定日期的数据
			BigDecimal[] array = null;
			if(OrderTrendStatisticsType.AMOUNT.equals(orderTrendStatistics.getType())){
				array = queryFixedDaysOrderAmount(shopIds);
			}else if(OrderTrendStatisticsType.SUM.equals(orderTrendStatistics.getType())){
				array = queryFixedDaysOrderSum(shopIds);
			}else{
				orderTrendStatistics.setIs_success(false);
				orderTrendStatistics.setError_msg("查询类型不存在");
				return orderTrendStatistics;
			}
			orderTrendStatistics.setToday(array[0]);
			orderTrendStatistics.setYesterday(array[1]);
			orderTrendStatistics.setLast_week(array[2]);
			orderTrendStatistics.setLast_month(array[3]);
			
			
			
			if(orderTrendStatistics.isIs_multi_days()){
				//跨日期查询
				List<OrderTrendDateStatisticsDetail> dateDetailList = orderStatisticsMapper.queryDailyOrderTrendByMultiDays(startDate_1, endDate_1,
																								shopIds, orderTrendStatistics.getType().name());
				OrderTrendDailyStatistics otds = fillDateStatistics(dateDetailList, startDate_1, endDate_1);
				orderTrendStatistics.getDaily_statistics().add(otds);
				if(orderTrendStatistics.isIs_compare()){
					dateDetailList = orderStatisticsMapper.queryDailyOrderTrendByMultiDays(startDate_2, endDate_2, shopIds, orderTrendStatistics.getType().name());
					otds = fillDateStatistics(dateDetailList, startDate_2, endDate_2);
					orderTrendStatistics.getDaily_statistics().add(otds);
				}
			}else{ //单日期查询
				List<OrderTrendHourlyStatisticsDetail> hourlyDetailList = orderStatisticsMapper.queryHourlyOrderTrendByOneDay(startDate_1, shopIds, orderTrendStatistics.getType().name());
				OrderTrendHourlyStatistics oths = fillHourlyStatistics(hourlyDetailList, orderTrendStatistics.getStart_dateTime_1(), orderTrendStatistics.isIs_compare());
				orderTrendStatistics.getHourly_statistics().add(oths);
				if(orderTrendStatistics.isIs_compare()){
					hourlyDetailList = orderStatisticsMapper.queryHourlyOrderTrendByOneDay(startDate_2, shopIds, orderTrendStatistics.getType().name());
					oths = fillHourlyStatistics(hourlyDetailList, orderTrendStatistics.getStart_dateTime_2(), true);
					orderTrendStatistics.getHourly_statistics().add(oths);
				}
			}
		} catch (Exception e){
			logger.error("查询【点单分析】-【点单趋势】时发生错误！", e);
			orderTrendStatistics.setIs_success(false);
			orderTrendStatistics.setError_msg("查询异常，请稍后再试");
		}
		
		return orderTrendStatistics;
	}

	/**
	 * 页面传入的4个起止日期的校验，返回校验结果错误信息。如果校验通过，返回null
	 */
	private String dateValidation(Date startDate1, Date endDate1, Date startDate2, Date endDate2) {
		DateTime today = new DateTime().withTimeAtStartOfDay();
		DateTime startDate_1 = startDate1 != null ? new DateTime(startDate1) : null;
		DateTime endDate_1 = endDate1 != null ? new DateTime(endDate1) : null;
		DateTime startDate_2 = startDate2!= null ? new DateTime(startDate2) : null;
		DateTime endDate_2 = endDate2 != null ? new DateTime(endDate2) : null;
		
		String msg = null;
		
		//校验参照日期
		if(startDate_1 == null || endDate_1 == null){
			msg = "开始/结束日期不能为空";
		}else if(startDate_1.isAfter(endDate_1)){
			msg = "开始日期不能晚于结束日期";
		}else if(today.isBefore(endDate_1)){
			msg = "结束日期不能超过当天";
		}else if(Days.daysBetween(startDate_1, endDate_1).getDays() > 90){
			msg = "最多查询90天数据";
		}else{
			//校验对比日期
			if((startDate_2 == null && endDate_2 != null)
					|| (startDate_2 != null && endDate_2 == null)){ //对比日期其中一个为空
				msg = "请完善对比的开始/结束日期";
			}else if(startDate_2 != null && endDate_2 != null){ //对比日期都不为空
				if(startDate_2.isAfter(endDate_2)){
					msg = "对比开始日期不能晚于结束日期";
				}else if(today.isBefore(endDate_2)){
					msg = "对比结束日期不能超过当天";
				}else if(Days.daysBetween(startDate_1, endDate_1).getDays() != Days.daysBetween(startDate_2, endDate_2).getDays()){
					msg = "日期跨度不一致";
				}else if(startDate_1.equals(startDate_2) && endDate_2.equals(endDate_2)){
					msg = "对比的日期范围重复";
				}
			}
		}
		return msg;
	}
	
	/**
	 * 统计分析【点单分析】当前登录账户与指定查询商户范围的校验
	 */
	private Long[] getShopIdScope(StringBuffer errorMsg, Long shopId, Business business) {
		if(business == null){
			errorMsg.append("请先登录后再重试查询");
			return null;
		}else if(BusinessType.MERCHANT.equals(business.getSelfBusinessType())){
			//先校验集团下是否有子商户
			Long[] shopIds = shopService.findShopIdsByMerchantId(business.getSelfBusinessId(), true);
			if(shopIds == null || shopIds.length == 0){
				errorMsg.append("集团未关联子商户，请登陆后台编辑后再重试查询");
				return null;
			}
			
			if(shopId != null && Arrays.asList(shopIds).contains(shopId)){ //页面指定集团下具体子商户
				return new Long[]{shopId};
			}else if(shopId != null && !Arrays.asList(shopIds).contains(shopId)){
				errorMsg.append("集团不存在该子商户");
				return null;
			}else{ //未指定子商户，返回集团下所有商户
				return shopIds;
			}
		}else if(BusinessType.SHOP.equals(business.getSelfBusinessType())){
			return new Long[]{business.getSelfBusinessId()};
		}
		return null;
	}

	/**
	 * 统计分析【点单分析】查询“当天”、“昨天”、“近7天”、“近30天”4个固定时间段的点单量
	 */
	private BigDecimal[] queryFixedDaysOrderAmount(Long[] shopIds) {
		BigDecimal[] amounts = new BigDecimal[4];
		Date today = new DateTime().withTimeAtStartOfDay().toDate();
		Date tomorrow = new DateTime().withTimeAtStartOfDay().plusDays(1).toDate();
		amounts[0] = orderStatisticsMapper.querySpecifiedPeriodOrderAmount(today, tomorrow, shopIds);
		amounts[1] = orderStatisticsMapper.querySpecifiedPeriodOrderAmount(new DateTime(today).minusDays(1).toDate(), today, shopIds);
		amounts[2] = orderStatisticsMapper.querySpecifiedPeriodOrderAmount(new DateTime(today).minusDays(6).toDate(), tomorrow, shopIds);
		amounts[3] = orderStatisticsMapper.querySpecifiedPeriodOrderAmount(new DateTime(today).minusDays(29).toDate(), tomorrow, shopIds);
		return amounts;
	}
	
	/**
	 * 【点单分析】补全单日查询每小时统计数据
	 */
	private OrderTrendHourlyStatistics fillHourlyStatistics(List<OrderTrendHourlyStatisticsDetail> hourlyDetailList, DateTime refer, boolean isCompare) {
		OrderTrendHourlyStatistics oths = new OrderTrendHourlyStatistics();
		OrderTrendHourlyStatisticsDetail[] hourlyDetailArray = new OrderTrendHourlyStatisticsDetail[24];
		int hour = 23;//默认查看全天数据
		//但是如果非对比情况下，并且查看今天数据，截止到当前小时为止。例如现在是14:20，则最近时间显示为“14:00-14:59”
		if(refer.equals(DateTime.now().withTimeAtStartOfDay()) && !isCompare){
			hour = DateTime.now().getHourOfDay();
		}
		
		DateTime tempDate = null;
		for(OrderTrendHourlyStatisticsDetail othsd : hourlyDetailList){ //遍历现有数据，放入Array
			OrderTrendHourlyStatisticsDetail othsd2 = hourlyDetailArray[23-othsd.getHr()];
			if(othsd2 == null){
				othsd2 = new OrderTrendHourlyStatisticsDetail();
				othsd2.setHour(othsd.getHour() + ":00-" + othsd.getHour() + ":59");
				hourlyDetailArray[23-othsd.getHr()] = othsd2;
			}
			
			tempDate = new DateTime(othsd.getDate());
			if(Days.daysBetween(tempDate, refer).getDays() == 0){
				othsd2.setToday_amount(othsd.getTemp_amount());
				if(hour >= othsd.getHr()){
					oths.setToday_total_amount(oths.getToday_total_amount().add(othsd.getTemp_amount()));
				}
			}else if(Days.daysBetween(tempDate, refer).getDays() == 1){
				othsd2.setYesterday_amount(othsd.getTemp_amount());
				if(hour >= othsd.getHr()){
					oths.setYesterday_total_amount(oths.getYesterday_total_amount().add(othsd.getTemp_amount()));
				}
			}else if(Days.daysBetween(tempDate, refer).getDays() == 7){
				othsd2.setLast_week_amount(othsd.getTemp_amount());
				if(hour >= othsd.getHr()){
					oths.setLast_week_total_amount(oths.getLast_week_total_amount().add(othsd.getTemp_amount()));
				}
			}
		}
		
		for(int i = 0; i < 24; i++){ //往数组里填充空值
			if(hourlyDetailArray[i] == null){
				OrderTrendHourlyStatisticsDetail othsd = new OrderTrendHourlyStatisticsDetail();
				othsd.setToday_amount(new BigDecimal(0));
				othsd.setYesterday_amount(new BigDecimal(0));
				othsd.setLast_week_amount(new BigDecimal(0));
				String hourStr = StringUtils.leftPad(""+(23-i), 2, '0');
				othsd.setHour(hourStr + ":00-" + hourStr + ":59");
				hourlyDetailArray[i] = othsd;
			}
		}
		
		oths.setHour_detail(Arrays.asList(ArrayUtils.subarray(hourlyDetailArray, 23-hour, 24)));
		return oths;
	}
	
	/**
	 * 【点单分析】补全跨日期查询每日统计数据
	 */
	private OrderTrendDailyStatistics fillDateStatistics(List<OrderTrendDateStatisticsDetail> tempDateDetailList, Date start, Date end) {
		//初始化变量
		OrderTrendDailyStatistics otds = new OrderTrendDailyStatistics();
		DateTime startDate = new DateTime(start);
		DateTime endDate = new DateTime(end);
		List<OrderTrendDateStatisticsDetail> dateDetailList = new ArrayList<>();;
		
		//tempDateDetailList转换成Map<Date, BigDecimal>
		Map<Date, BigDecimal> tempMap = new TreeMap<>();
		for(OrderTrendDateStatisticsDetail otdsd : tempDateDetailList){
			tempMap.put(otdsd.getDate(), otdsd.getTemp_amount());
		}
		
		for(DateTime dt = endDate; !startDate.isAfter(dt); dt = dt.minusDays(1)){
			OrderTrendDateStatisticsDetail otdsd = new OrderTrendDateStatisticsDetail();
			otdsd.setDate(dt.toDate());
			if(tempMap.get(dt.toDate()) != null){ //today_amount
				otdsd.setToday_amount(tempMap.get(dt.toDate()));
				otds.setTotal_amount(otds.getTotal_amount().add(tempMap.get(dt.toDate())));
			}
			if(tempMap.get(dt.minusDays(1).toDate()) != null){ //yesterday_amount
				otdsd.setYesterday_amount(tempMap.get(dt.minusDays(1).toDate()));
			}
			if(tempMap.get(dt.minusDays(7).toDate()) != null){ //last_week_amount
				otdsd.setLast_week_amount(tempMap.get(dt.minusDays(7).toDate()));
			}
			dateDetailList.add(otdsd);
		}
		
		otds.setDate_detail(dateDetailList);
		return otds;
	}

	/**
	 * 统计分析【点单分析】查询“当天”、“昨天”、“近7天”、“近30天”4个固定时间段的点单总金额
	 */
	private BigDecimal[] queryFixedDaysOrderSum(Long[] shopIds) {
		BigDecimal[] sums = new BigDecimal[4];
		Date today = new DateTime().withTimeAtStartOfDay().toDate();
		Date tomorrow = new DateTime().withTimeAtStartOfDay().plusDays(1).toDate();
		sums[0] = orderStatisticsMapper.querySpecifiedPeriodOrderSum(today, tomorrow, shopIds);
		sums[1] = orderStatisticsMapper.querySpecifiedPeriodOrderSum(new DateTime(today).minusDays(1).toDate(), today, shopIds);
		sums[2] = orderStatisticsMapper.querySpecifiedPeriodOrderSum(new DateTime(today).minusDays(6).toDate(), tomorrow, shopIds);
		sums[3] = orderStatisticsMapper.querySpecifiedPeriodOrderSum(new DateTime(today).minusDays(29).toDate(), tomorrow, shopIds);
		return sums;
	}
	
	/**
	 * 统计分析【点单分析】查询“当天”、“昨天”、“近7天”、“近30天”4个固定时间段的商品销量
	 */
	private BigDecimal[] queryFixedDaysItemSales(Long[] shopIds) {
		BigDecimal[] sums = new BigDecimal[4];
		Date today = new DateTime().withTimeAtStartOfDay().toDate();
		Date tomorrow = new DateTime().withTimeAtStartOfDay().plusDays(1).toDate();
		sums[0] = orderStatisticsMapper.querySpecifiedPeriodItemSales(today, tomorrow, shopIds);
		sums[1] = orderStatisticsMapper.querySpecifiedPeriodItemSales(new DateTime(today).minusDays(1).toDate(), today, shopIds);
		sums[2] = orderStatisticsMapper.querySpecifiedPeriodItemSales(new DateTime(today).minusDays(6).toDate(), tomorrow, shopIds);
		sums[3] = orderStatisticsMapper.querySpecifiedPeriodItemSales(new DateTime(today).minusDays(29).toDate(), tomorrow, shopIds);
		return sums;
	}

	/**
	 * 【商户分布】点单量及点单总金额的商户占比
	 */
	@Override
	public ShopOrderRatioStatistics orderRatioStatisticsByShop(ShopOrderRatioStatistics shopOrderRatioStatistics, Business business) {
		Date startDate = shopOrderRatioStatistics.getStart_date();
		Date endDate = shopOrderRatioStatistics.getEnd_date();
		
		//1.date validate
		String errorMsg = dateValidation(startDate, endDate, null, null);
		if(StringUtils.isNotBlank(errorMsg)){
			shopOrderRatioStatistics.setIs_success(false);
			shopOrderRatioStatistics.setError_msg(errorMsg);
			return shopOrderRatioStatistics;
		}
		
		//2.authorize account and shopIds
		StringBuffer errMsg = new StringBuffer();
		Long[] shopIds = getShopIdScope(errMsg, business);
		if(shopIds == null){
			shopOrderRatioStatistics.setIs_success(false);
			shopOrderRatioStatistics.setError_msg(errMsg.toString());
			return shopOrderRatioStatistics;
		}
		
		//3.fetch data
		try{
			
			//查询当天、昨天、近7天、近30天4个固定日期的数据
			BigDecimal[] array = null;
			if(ShopOrderRatioStatisticsType.AMOUNT.equals(shopOrderRatioStatistics.getType())){
				array = queryFixedDaysOrderAmount(shopIds);
			}else if(ShopOrderRatioStatisticsType.SUM.equals(shopOrderRatioStatistics.getType())){
				array = queryFixedDaysOrderSum(shopIds);
			}else{
				shopOrderRatioStatistics.setIs_success(false);
				shopOrderRatioStatistics.setError_msg("查询类型不存在");
				return shopOrderRatioStatistics;
			}
			shopOrderRatioStatistics.setToday(array[0]);
			shopOrderRatioStatistics.setYesterday(array[1]);
			shopOrderRatioStatistics.setLast_week(array[2]);
			shopOrderRatioStatistics.setLast_month(array[3]);
			
			List<ShopOrderRatioStatisticsDetail> detailList = orderStatisticsMapper.queryShopOrderRatios(startDate, endDate,
																					shopIds, shopOrderRatioStatistics.getType().name());
			detailList = fillShopOrderRatioStatistics(detailList, shopIds);
			shopOrderRatioStatistics.setShop_ratios(detailList);
		} catch (Exception e){
			logger.error("查询【点单分析】-【商户分布】时发生错误！", e);
			shopOrderRatioStatistics.setIs_success(false);
			shopOrderRatioStatistics.setError_msg("查询异常，请稍后再试");
		}
		
		return shopOrderRatioStatistics;
	}

	/**
	 * 统计分析【商户分布】当前登录账户查询权限的校验，返回集团下关联子商户
	 */
	private Long[] getShopIdScope(StringBuffer errorMsg, Business business) {
		if(business == null){
			errorMsg.append("请先登录后再重试查询");
			return null;
		}else if(BusinessType.MERCHANT.equals(business.getSelfBusinessType())){
			//先校验集团下是否有子商户
			Long[] shopIds = shopService.findShopIdsByMerchantId(business.getSelfBusinessId(), true);
			if(shopIds == null || shopIds.length == 0){
				errorMsg.append("集团未关联子商户，请登陆后台编辑后再重试查询");
				return null;
			}else{
				return shopIds;
			}
		}else if(BusinessType.SHOP.equals(business.getSelfBusinessType())){
			errorMsg.append("商户分布仅对集团账号开放");
			return null;
		}
		return null;
	}
	
	/**
	 * 【商户分布】补全指定日期段点单相关数据的商户分布占比等
	 */
	private List<ShopOrderRatioStatisticsDetail> fillShopOrderRatioStatistics(List<ShopOrderRatioStatisticsDetail> detailList, Long[] shopIds) {
		List<Long> shopIdList = new ArrayList<>(Arrays.asList(shopIds));
		for(ShopOrderRatioStatisticsDetail sorsd : detailList){
			shopIdList.remove(sorsd.getShop_id());
		}
		for(Long shopId : shopIdList){
			ShopOrderRatioStatisticsDetail detail = new ShopOrderRatioStatisticsDetail();
			Shop shop = shopService.findShopByIdIgnoreVisible(shopId);
			if(shop !=null) {
				detail.setShop_id(shopId);
				detail.setShop_name(shop.getName());
				detailList.add(detail);
			}
		}
		return detailList;
	}

	/**
	 * 【品类分析】点单量及点单总金额的类目占比
	 */
	@Override
	public CategoryRatioStatistics categoryRatioStatisticsByShop(CategoryRatioStatistics categoryRatioStatistics, Business business) {
		Date startDate = categoryRatioStatistics.getStart_date();
		Date endDate = categoryRatioStatistics.getEnd_date();
		
		//1.date validate
		String errorMsg = dateValidation(startDate, endDate, null, null);
		if(StringUtils.isNotBlank(errorMsg)){
			categoryRatioStatistics.setIs_success(false);
			categoryRatioStatistics.setError_msg(errorMsg);
			return categoryRatioStatistics;
		}
		
		//2.authorize account and shopIds
		StringBuffer errMsg = new StringBuffer();
		Long[] shopIds = getShopIdScope(errMsg, categoryRatioStatistics.getShop_id(), business);
		if(shopIds == null){
			categoryRatioStatistics.setIs_success(false);
			categoryRatioStatistics.setError_msg(errMsg.toString());
			return categoryRatioStatistics;
		}
		
		//3.fetch data
		try{
			//查询当天、昨天、近7天、近30天4个固定日期的数据
			BigDecimal[] array = null;
//			if(CategoryRatioStatisticsType.SUM.equals(categoryRatioStatistics.getType())){
//				array = queryFixedDaysOrderSum(shopIds);
//			}else 
			if(CategoryRatioStatisticsType.SALES.equals(categoryRatioStatistics.getType())){
				array = queryFixedDaysItemSales(shopIds);
			}else{
				categoryRatioStatistics.setIs_success(false);
				categoryRatioStatistics.setError_msg("查询类型不存在");
				return categoryRatioStatistics;
			}
			categoryRatioStatistics.setToday(array[0]);
			categoryRatioStatistics.setYesterday(array[1]);
			categoryRatioStatistics.setLast_week(array[2]);
			categoryRatioStatistics.setLast_month(array[3]);
			
			List<CategoryRatioStatisticsDetail> detailList = orderStatisticsMapper.queryCategoryRatios(startDate, endDate,
																					shopIds, categoryRatioStatistics.getType().name());
			
			Set<String> categoryNameSet = itemCategoryMapper.selectCategoryNamesByShopIds(shopIds);
			for(CategoryRatioStatisticsDetail crsc : detailList){
				categoryNameSet.remove(crsc.getName());
			}
			for(String name : categoryNameSet){
				CategoryRatioStatisticsDetail crsd = new CategoryRatioStatisticsDetail();
				crsd.setName(name);
				detailList.add(crsd);
			}
			categoryRatioStatistics.setDetail_list(detailList);
		} catch (Exception e){
			logger.error("查询【统计分析】-【品类分析】时发生错误！", e);
			categoryRatioStatistics.setIs_success(false);
			categoryRatioStatistics.setError_msg("查询异常，请稍后再试");
		}
		
		return categoryRatioStatistics;
	}
	
	/**
	 * 【商品分析】点单量及点单总金额的商品占比
	 */
	@Override
	public ItemRatioStatistics itemRatioStatisticsByShop(ItemRatioStatistics itemRatioStatistics,Business business) {
		Date startDate = itemRatioStatistics.getStart_date();
		Date endDate = itemRatioStatistics.getEnd_date();
		
		//1.date validate
		String errorMsg = dateValidation(startDate, endDate, null, null);
		if(StringUtils.isNotBlank(errorMsg)){
			itemRatioStatistics.setIs_success(false);
			itemRatioStatistics.setError_msg(errorMsg);
			return itemRatioStatistics;
		}
		
		//2.authorize account and shopIds
		StringBuffer errMsg = new StringBuffer();
		Long[] shopIds = getShopIdScope(errMsg, itemRatioStatistics.getShop_id(), business);
		if(shopIds == null){
			itemRatioStatistics.setIs_success(false);
			itemRatioStatistics.setError_msg(errMsg.toString());
			return itemRatioStatistics;
		}
		
		//3.fetch data
		try{
			//查询当天、昨天、近7天、近30天4个固定日期的数据
			BigDecimal[] array = null;
			if(ItemRatioStatisticsType.AMOUNT.equals(itemRatioStatistics.getType())){
				array = queryFixedDaysOrderAmount(shopIds);
//			}else if(ItemRatioStatisticsType.SUM.equals(itemRatioStatistics.getType())){
//				array = queryFixedDaysOrderSum(shopIds);
			}else if(ItemRatioStatisticsType.SALES.equals(itemRatioStatistics.getType())){
				array = queryFixedDaysItemSales(shopIds);
			}else{
				itemRatioStatistics.setIs_success(false);
				itemRatioStatistics.setError_msg("查询类型不存在");
				return itemRatioStatistics;
			}
			itemRatioStatistics.setToday(array[0]);
			itemRatioStatistics.setYesterday(array[1]);
			itemRatioStatistics.setLast_week(array[2]);
			itemRatioStatistics.setLast_month(array[3]);
			
			ItemRatioStatistics itemRatioStatistics2 = orderStatisticsMapper.queryItemSalesSummary(startDate, endDate, shopIds);
			itemRatioStatistics.setSalesTotalAmount(itemRatioStatistics2.getSalesTotalAmount());
			itemRatioStatistics.setOrderTotalAmount(itemRatioStatistics2.getOrderTotalAmount());
			itemRatioStatistics.setOrderTotalSum(itemRatioStatistics2.getOrderTotalSum());
			List<ItemRatioStatisticsDetail> detailList = orderStatisticsMapper.queryItemStatisticsDetail(startDate, endDate, shopIds, itemRatioStatistics.getType().name());
			detailList = fillItemStatistics(detailList,itemRatioStatistics, shopIds);
			itemRatioStatistics.setDetail_list(detailList);
		} catch (Exception e){
			logger.error("查询【统计分析】-【商品分析】时发生错误！", e);
			itemRatioStatistics.setIs_success(false);
			itemRatioStatistics.setError_msg("查询异常，请稍后再试");
		}
		
		return itemRatioStatistics;
	}

	/** 【商品分析】补全查询指定日期、商户范围的统计数据（点单量、点单金额、销量等） */
	private List<ItemRatioStatisticsDetail> fillItemStatistics(List<ItemRatioStatisticsDetail> detailList,
												ItemRatioStatistics itemRatioStatistics, Long[] shopIds) {
		List<ItemRatioStatisticsDetail> itemRatioList = new ArrayList<>();
		BigDecimal b = new BigDecimal(100);
		
		for(ItemRatioStatisticsDetail irsd : detailList){
			//计算销量占比
			irsd.setSales_amount_ratio(irsd.getSales_amount().multiply(b).divide(itemRatioStatistics.getSalesTotalAmount(), 2, RoundingMode.HALF_UP));
			//计算订单量占比
			irsd.setOrder_amount_ratio(irsd.getOrder_amount().multiply(b).divide(itemRatioStatistics.getOrderTotalAmount(), 2, RoundingMode.HALF_UP));
			//计算总金额占比
			irsd.setOrder_sum_ratio(irsd.getOrder_sum().multiply(b).divide(itemRatioStatistics.getOrderTotalSum(), 2, RoundingMode.HALF_UP));
			//计算单均销量
			irsd.setSales_amount_per_order(irsd.getSales_amount().divide(irsd.getOrder_amount(), 2, RoundingMode.HALF_UP));
			//计算均价
			irsd.setAvg_price(irsd.getOrder_sum().divide(irsd.getSales_amount(), 2, RoundingMode.HALF_UP));
			itemRatioList.add(irsd);
		}
		
		List<String> unOrderedItems = itemMapper.selectUnOrderedItemNamesByShopIds(itemRatioStatistics.getStart_date(),itemRatioStatistics.getEnd_date(),shopIds);
		for(String name : unOrderedItems){
			ItemRatioStatisticsDetail irsd = new ItemRatioStatisticsDetail();
			irsd.setName(name);
			itemRatioList.add(irsd);
		}
		
		return itemRatioList;
	}


	/** 【点单分析】导出excel */
	@Override
	public OrderTrendStatisticsExport orderTrendStatisticsForExport(OrderTrendStatisticsExport orderTrendStatisticsExport, Business business) {
		Date startDate = orderTrendStatisticsExport.getStart_date_1();
		Date endDate = orderTrendStatisticsExport.getEnd_date_1();
		
		//1.date validate
		String errorMsg = dateValidation(startDate, endDate, null, null);
		if(StringUtils.isNotBlank(errorMsg)){
			orderTrendStatisticsExport.setIs_success(false);
			orderTrendStatisticsExport.setError_msg(errorMsg);
			return orderTrendStatisticsExport;
		}else{
			orderTrendStatisticsExport.setIs_multi_days(
					Days.daysBetween(orderTrendStatisticsExport.getStart_dateTime_1(), orderTrendStatisticsExport.getEnd_dateTime_1()).getDays() > 0);
		}
		
		//2.authorize account and shopIds
		StringBuffer errMsg = new StringBuffer();
		Long[] shopIds = getShopIdScope(errMsg, orderTrendStatisticsExport.getShop_id(), business);
		if(shopIds == null){
			orderTrendStatisticsExport.setIs_success(false);
			orderTrendStatisticsExport.setError_msg(errMsg.toString());
			return orderTrendStatisticsExport;
		}
		
		//3.fetch data
		try{
			if(orderTrendStatisticsExport.isIs_multi_days()){
				//跨日期查询
				Map<OrderTrendStatisticsType, List<OrderTrendDateStatisticsDetail>> dailyMap = new HashMap<>();
				for(OrderTrendStatisticsType type : OrderTrendStatisticsType.values()){
					List<OrderTrendDateStatisticsDetail> dateDetailList = orderStatisticsMapper.queryDailyOrderTrendByMultiDays(startDate, endDate,
																									shopIds, type.name());
					OrderTrendDailyStatistics otds = fillDateStatistics(dateDetailList, startDate, endDate);
					dailyMap.put(type, otds.getDate_detail());
				}
				orderTrendStatisticsExport.setDaily_export(dailyMap);
			}else{ //单日期查询
				Map<OrderTrendStatisticsType, List<OrderTrendHourlyStatisticsDetail>> hourlyMap = new HashMap<>();
				for(OrderTrendStatisticsType type : OrderTrendStatisticsType.values()){
					List<OrderTrendHourlyStatisticsDetail> hourlyDetailList = orderStatisticsMapper.queryHourlyOrderTrendByOneDay(startDate, shopIds, type.name());
					OrderTrendHourlyStatistics oths = fillHourlyStatistics(hourlyDetailList, orderTrendStatisticsExport.getStart_dateTime_1(), false);
					hourlyMap.put(type, oths.getHourly_detail());
				}
				orderTrendStatisticsExport.setHourly_export(hourlyMap);
			}
		} catch (Exception e){
			logger.error("查询【点单分析】-【点单趋势】的EXCEL数据时发生错误！", e);
			orderTrendStatisticsExport.setIs_success(false);
			orderTrendStatisticsExport.setError_msg("数据查询异常，请稍后再试");
		}
		
		return orderTrendStatisticsExport;
	}

}
