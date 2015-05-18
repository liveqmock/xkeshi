package com.xpos.controller.api;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.xkeshi.pojo.po.Shop;
import com.xkeshi.pojo.vo.DailyStatisticsSummaryVO;
import com.xkeshi.pojo.vo.Result;
import com.xkeshi.pojo.vo.SystemParam;
import com.xkeshi.service.StatisticsService;
import com.xkeshi.service.XShopService;


/**
 * 各项交易相关数据的统计、汇总
 */
@Controller
@RequestMapping("api/statistics")
public class APIDailyStatisticsController extends BaseAPIController {
	
	@Autowired
	private StatisticsService statisticsService;
	
	@Autowired
	private XShopService  xShopService   ;
	
    /** 每日数据汇总（当前操作员、当天 & 前一天、只统计成功状态） */
    @ResponseBody
    @RequestMapping(value = "summary/daily", method = RequestMethod.GET)
    public Result getDailySummary(@ModelAttribute SystemParam param, @RequestParam("orderType")String orderType) {
		//一个APP只有一种订单类型（XPOS_ORDER/THIRD_ORDER），不可能多种并存
		Result result = new Result("统计信息查询失败", "1001");
		if(StringUtils.equalsIgnoreCase("XPOS_ORDER", orderType)
				|| StringUtils.equalsIgnoreCase("THIRD_ORDER", orderType)){
			DailyStatisticsSummaryVO[] summaryVOs = new DailyStatisticsSummaryVO[2];
			DateTime today = new DateTime();
			DateTime yesterday = today.minusDays(1);
			DailyStatisticsSummaryVO todaySummaryVO = statisticsService.generateDailySummary(param, orderType, today.toDate());
			DailyStatisticsSummaryVO yesterdaySummaryVO = statisticsService.generateDailySummary(param, orderType, yesterday.toDate());
			summaryVOs[0] = todaySummaryVO;
			summaryVOs[1] = yesterdaySummaryVO;
			result = new Result("查询成功", "0");
			result.setResult(summaryVOs);
		}
        return result;
    }





}
