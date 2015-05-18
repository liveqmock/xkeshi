package com.xpos.controller;

import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.xpos.common.entity.Coupon;
import com.xpos.common.entity.Coupon.CouponStatus;
import com.xpos.common.entity.CouponInfo.CouponInfoType;
import com.xpos.common.entity.CouponPayment;
import com.xpos.common.entity.CouponPayment.CouponPaymentStatus;
import com.xpos.common.entity.CouponPayment.CouponPaymentType;
import com.xpos.common.entity.Merchant;
import com.xpos.common.entity.Order.Status;
import com.xpos.common.entity.OrderItem;
import com.xpos.common.entity.Shop;
import com.xpos.common.entity.example.CouponExample;
import com.xpos.common.entity.pos.POSTransaction.POSTransactionStatus;
import com.xpos.common.entity.pos.POSTransaction.POSTransactionType;
import com.xpos.common.entity.statistics.CategoryRatioStatistics;
import com.xpos.common.entity.statistics.CategoryRatioStatistics.CategoryRatioStatisticsType;
import com.xpos.common.entity.statistics.ItemRatioStatistics;
import com.xpos.common.entity.statistics.ItemRatioStatistics.ItemRatioStatisticsType;
import com.xpos.common.entity.statistics.OrderTrendDateStatisticsDetail;
import com.xpos.common.entity.statistics.OrderTrendHourlyStatisticsDetail;
import com.xpos.common.entity.statistics.OrderTrendStatistics;
import com.xpos.common.entity.statistics.OrderTrendStatistics.OrderTrendStatisticsType;
import com.xpos.common.entity.statistics.OrderTrendStatisticsExport;
import com.xpos.common.entity.statistics.ShopOrderRatioStatistics;
import com.xpos.common.entity.statistics.ShopOrderRatioStatisticsDetail;
import com.xpos.common.exception.GenericException;
import com.xpos.common.searcher.CouponPaymentSearcher;
import com.xpos.common.searcher.CouponSearcher;
import com.xpos.common.searcher.OrderSearcher;
import com.xpos.common.searcher.POSTransactionSearcher;
import com.xpos.common.service.CouponPaymentService;
import com.xpos.common.service.CouponService;
import com.xpos.common.service.OrderService;
import com.xpos.common.service.OrderStatisticsService;
import com.xpos.common.service.POSTransactionService;
import com.xpos.common.service.ShopService;
import com.xpos.common.utils.BusinessSQLBuilder;
import com.xpos.common.utils.Pager;

@Controller
@RequestMapping("/statistics")
public class StatisticController extends BaseController {
	@Autowired
	private OrderService orderService;
	@Autowired
	private POSTransactionService transactionService;
	@Autowired
	private CouponService couponService;
	@Autowired
	private CouponPaymentService couponPaymentService;
	@Autowired
	private ShopService shopService;
	@Autowired
	private OrderStatisticsService orderStatisticsService;

	@RequestMapping(value="orderItem", method = RequestMethod.GET)
	@ResponseBody
	public String orderItemStatistic(Model model, @RequestParam(value = "type") int type,
			@RequestParam(value = "p", required = false, defaultValue = "1") int currentPage){
		Pager<OrderItem> pager = new Pager<>();
		currentPage = currentPage > 0 ? currentPage : 1;
		pager.setPageNumber(currentPage);
		pager.setPageSize(20);
		
		OrderSearcher searcher = new OrderSearcher();
		searcher.setBusiness(getBusiness());
		DateTime start = new DateTime().withTimeAtStartOfDay();
		DateTime end = start.plus(1000L * 60 * 60 * 24 - 1);
		if(type == 1){
			start = start.minusDays(1);
			end = end.minusDays(1);
			searcher.setStartDate(start.toDate());
			searcher.setEndDate(end.toDate());
			orderService.findOrderItems(searcher, pager);
		}else if(type == 2){
			start = start.minusDays(6);
			searcher.setStartDate(start.toDate());
			searcher.setEndDate(end.toDate());
			orderService.findOrderItems(searcher, pager);
			
		}else{ //默认查询当天
			searcher.setStartDate(start.toDate());
			searcher.setEndDate(end.toDate());
			orderService.findOrderItems(searcher, pager);
		}
		
		//convert to JSON
		String data = convertToJSONResult(pager);
		return data;
	}
	
	private String convertToJSONResult(Pager<OrderItem> pager) {
		StringBuilder sb = new StringBuilder("{");
		sb.append("\"page\":").append(pager.getPageNumber()).append(",")
			.append("\"total\":").append(pager.getTotalCount()==null?0:pager.getTotalCount()).append(",")
			.append("\"next\":").append(pager.isNext()).append(",")
			.append("\"data\":[");
		if(!CollectionUtils.isEmpty(pager.getList())){
			for(OrderItem item : pager.getList()){
				sb.append("{\"name\":\"").append(item.getItemName())
					.append("\",\"count\":").append(item.getQuantity()).append("},");
			}
			sb.deleteCharAt(sb.length() - 1);
		}
		sb.append("]}");
		return sb.toString();
	}

	@RequestMapping(value="summary", method=RequestMethod.GET)
	public String getDailyStatisticsSummary(Model model){
		DateTime today = new DateTime();
		DateTime yesterday = today.minusDays(1);
		
		//1.销售商品统计
		OrderSearcher orderSearcher = new OrderSearcher();
		orderSearcher.setStartDate(today.toDate());
		orderSearcher.setEndDate(today.toDate());
		orderSearcher.setStatus(Status.SUCCESS);
		String[] todayOrderStatistic = orderService.getOrderStatistics(getBusiness(), orderSearcher);
		orderSearcher.setStartDate(yesterday.toDate());
		orderSearcher.setEndDate(yesterday.toDate());
		String[] yesterdayOrderStatistic = orderService.getOrderStatistics(getBusiness(),  orderSearcher);
		model.addAttribute("todayOrderStatistic", todayOrderStatistic);
		model.addAttribute("yesterdayOrderStatistic", yesterdayOrderStatistic);
		
		
		//2.POS刷卡统计
		POSTransactionSearcher posTransactionSearcher = new POSTransactionSearcher();
		Set<POSTransactionStatus> statusSet = new HashSet<>();
		statusSet.add(POSTransactionStatus.PAID_SUCCESS);
		posTransactionSearcher.setBusiness(getBusiness());
		posTransactionSearcher.setStatusSet(statusSet);
		posTransactionSearcher.setStartDate(today.toDate());//当天
		posTransactionSearcher.setEndDate(today.toDate());//当天
		posTransactionSearcher.setType(POSTransactionType.BANK_CARD);//刷卡
		String[] todayPOSStatistic = transactionService.getBankCardStatistic(getBusiness(), posTransactionSearcher);//当天刷卡统计
		posTransactionSearcher.setStartDate(yesterday.toDate());//昨日
		posTransactionSearcher.setEndDate(yesterday.toDate());//昨日
		posTransactionSearcher.setType(POSTransactionType.BANK_CARD);//刷卡
		String[] yesterdayPOSStatistic = transactionService.getBankCardStatistic(getBusiness(), posTransactionSearcher);//昨日刷卡统计
		model.addAttribute("todayPOSStatistic", todayPOSStatistic);
		model.addAttribute("yesterdayPOSStatistic", yesterdayPOSStatistic);
		
		//3.核销、发放优惠券数量统计
		CouponSearcher couponSearcher = new CouponSearcher();
		Set<CouponStatus> couponStatus = new HashSet<>();
		couponStatus.add(CouponStatus.USED);
		couponSearcher.setBusiness(getBusiness());
		couponSearcher.setStatus(couponStatus);
		couponSearcher.setStartDate(today.toDate());
		couponSearcher.setEndDate(today.toDate());
		int todayConsumedCouponCount = couponService.countCoupons((CouponExample)couponSearcher.getExample());
		couponSearcher.setStartDate(yesterday.toDate());
		couponSearcher.setEndDate(yesterday.toDate());
		int yesterdayConsumedCouponCount = couponService.countCoupons((CouponExample)couponSearcher.getExample());
		//发放统计
		CouponExample todayCouponExample = new CouponExample();
		todayCouponExample.createCriteria()
					.addCriterion("createDate >=", new DateTime().toString("yyyy-MM-dd 00:00:00"));
		todayCouponExample.appendCriterion(BusinessSQLBuilder.getSQL(getBusiness()));
		int todaySendCouponCount = couponService.countCoupons(todayCouponExample);
		CouponExample yesterdayCouponExample = new CouponExample();
		yesterdayCouponExample.createCriteria()
					.addCriterion("createDate >=", yesterday.toString("yyyy-MM-dd 00:00:00"))
					.addCriterion("createDate<=", yesterday.toString("yyyy-MM-dd 23:59:59"));
		yesterdayCouponExample.appendCriterion(BusinessSQLBuilder.getSQL(getBusiness()));
		int yesterdaySendCouponCount = couponService.countCoupons(yesterdayCouponExample);
		model.addAttribute("todayConsumedCouponCount", todayConsumedCouponCount);
		model.addAttribute("yesterdayConsumedCouponCount", yesterdayConsumedCouponCount);
		model.addAttribute("todaySendCouponCount", todaySendCouponCount);
		model.addAttribute("yesterdaySendCouponCount", yesterdaySendCouponCount);
		
		return "statistics/summary";
	}
	
	/** 优惠销售统计&明细 */
	@RequestMapping(value="/coupon/sales")
	public String getCouponSalesList(Pager<CouponPayment> pager, CouponPaymentSearcher searcher, Model model){
		//1.销售列表
		searcher.setBusiness(getBusiness());
		searcher.setStatus(CouponPaymentStatus.PAID_SUCCESS);
		pager = couponPaymentService.salesList(searcher, pager);
		model.addAttribute("pager", pager);
		model.addAttribute("searcher", searcher);
		
		//2.售价、实际支付合计
		String[] total = couponPaymentService.priceStatistic(searcher);
		model.addAttribute("total", total);
		return "statistics/coupon_sales_list";
	}
	
	/** 优惠核销统计&明细 */
	@RequestMapping(value="/coupon/consume")
	public String getCouponConsumeStatisticsList(Pager<Coupon> pager, CouponPaymentSearcher searcher,
			@RequestParam(value="queryStr", required=false)String queryStr, Model model){
		//1.核销列表
		searcher.setBusiness(getBusiness());
		//解析queryStr
		if(StringUtils.isNotBlank(queryStr)){
			if(getBusiness() instanceof Merchant){
				if(queryStr.contains("/")){
					if(StringUtils.startsWith(queryStr.trim(), "/")){
						model.addAttribute("status", "failed");
						model.addAttribute("errMsg", "请填写筛选条件中的核销商户名称");
						model.addAttribute("queryStr", queryStr);
						model.addAttribute("searcher", searcher);
						return "statistics/coupon_consume_list";
					}
					String[] str = StringUtils.split(queryStr, "/", 2);
					searcher.setNickName(str[0]);
					searcher.setOperator(str[1]);
				}else{
					searcher.setNickName(queryStr.trim());
				}
			}else if(getBusiness() instanceof Shop){
				searcher.setOperator(queryStr.trim());
			}
			model.addAttribute("queryStr", queryStr);
		}
		pager = couponPaymentService.consumeStatisticsList(searcher, pager);
		model.addAttribute("pager", pager);
		model.addAttribute("searcher", searcher);
		
		//2.实际支付合计
		String total = couponPaymentService.paymentStatistic(searcher);
		model.addAttribute("total", total);
		
		//3.加载子商户
		if(getBusiness() instanceof Merchant){
			//加载适用商户
			Map<String, Shop> applicableShopMap = new HashMap<>();
			List<Shop> shopList = shopService.findShopListByMerchantId(getBusiness().getSelfBusinessId(), true);
			for(Shop shop : shopList){
				applicableShopMap.put(shop.getId().toString(), shop);
			}
			model.addAttribute("applicableShopMap", applicableShopMap);
		}
		return "statistics/coupon_consume_list";
	}
	
	/** 导出EXCEL 电子券销售统计&明细 */
	@RequestMapping(value="/coupon/sales/export", method = RequestMethod.GET)
	public void exportCouponSalesList(CouponPaymentSearcher searcher, HttpServletResponse response){
		Pager<CouponPayment> pager = new Pager<>();
		pager.setPageSize(Integer.MAX_VALUE);
		searcher.setBusiness(getBusiness());
		searcher.setStatus(CouponPaymentStatus.PAID_SUCCESS);
		pager = couponPaymentService.salesList(searcher, pager);
		
		// 第一步，创建一个webbook，对应一个Excel文件
		HSSFWorkbook wb = new HSSFWorkbook();
		// 第二步，在webbook中添加一个sheet,对应Excel文件中的sheet
		HSSFSheet sheet = wb.createSheet("优惠销售统计&明细");
		// 第三步，在sheet中添加表头第0行
		HSSFRow row = sheet.createRow(0);
		// 第四步，创建单元格，并设置值表头 设置表头居中
		HSSFCellStyle style = wb.createCellStyle();
		// 生成一个字体  
		HSSFFont font = wb.createFont();
		font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		// 把字体应用到当前的样式  
		style.setFont(font); 
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式
		
		String[] headers = new String[]{"销售时间", "电子券名称", "手机号码", "单价", "数量", "实际支付", "支付方式", "支付账号", "交易流水号"};
		for (int i = 0; i < headers.length; i++) {
			HSSFCell cell = row.createCell(i);
			cell.setCellStyle(style);  
			HSSFRichTextString text = new HSSFRichTextString(headers[i]);
			cell.setCellValue(text);
		}
		
		// 第五步，写入实体数据
		if(pager != null && pager.getTotalCount() > 0){
			int index = 1; //从第二行开始输出
			int headerLength = headers.length;
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			for(CouponPayment payment : pager.getList()){
				row = sheet.createRow(index++);
				for(int j = 0; j < headerLength; j++){
					HSSFCell cell = row.createCell(j);
					switch(j){
						case 0:
							cell.setCellValue(sdf.format(payment.getTradeDate())); break;
						case 1:
							String couponInfoName = payment.getCouponInfo().getName();
							if(CouponInfoType.PACKAGE.equals(payment.getCouponInfo().getType())){
								couponInfoName = "【套票】" + couponInfoName;
							}
							cell.setCellValue(couponInfoName); break;
						case 2:
							String mobile = "-";
							if(StringUtils.isNotBlank(payment.getMobile()) && payment.getMobile().length() == 11){
								mobile = new StringBuilder(payment.getMobile()).replace(3, 7, "****").toString();
							}
							cell.setCellValue(mobile); break;
						case 3:
							BigDecimal price = payment.getCouponInfo().getPrice();
							if(price == null){
								cell.setCellValue(0.00);
							}else{
								cell.setCellValue(price.doubleValue());
							}
							break;
						case 4:
							cell.setCellValue(payment.getQuantity()); break;
						case 5:
							cell.setCellValue(payment.getSum().doubleValue()); break;
						case 6:
							cell.setCellValue(payment.getType().getDesc()); break;
						case 7:
							if(CouponPaymentType.ALIPAY_WAP.equals(payment.getType())){
								cell.setCellValue(payment.getBuyerAccount()); break;
							}else if(CouponPaymentType.UMPAY_WAP.equals(payment.getType())){
								cell.setCellValue(payment.getCardNumber()); break;
							}else{
								cell.setCellValue(""); break;
							}
						case 8:
							cell.setCellValue(payment.getSerial()); break;
					}
				}
			}
		}
		
		// 第六步，将文件存到指定位置
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
			String fileName = "CouponSalesStatistic(" + sdf.format(new Date()) + ")";
			response.setHeader("Content-disposition", "attachment; filename=" + fileName + ".xls");// 设定输出文件头
			response.setContentType("application/msexcel");// 定义输出类型
			OutputStream out = response.getOutputStream();
			wb.write(out);
			out.close();
		} catch (Exception e) {
			logger.error("优惠券销售统计导出EXCEL失败！", e);
			throw new GenericException("优惠券销售统计导出EXCEL失败");
		}
		
	}

	/** 导出EXCEL 电子券核销统计&明细 */
	@RequestMapping(value="/coupon/consume/export", method = RequestMethod.GET)
	public void exportCouponSalesList(CouponPaymentSearcher searcher,
			@RequestParam(value="queryStr", required=false)String queryStr, HttpServletResponse response){
		//1.核销列表
		searcher.setBusiness(getBusiness());
		//解析queryStr
		if(StringUtils.isNotBlank(queryStr)){
			if(getBusiness() instanceof Merchant){
				if(queryStr.contains("/")){
					if(StringUtils.startsWith(queryStr.trim(), "/")){
						throw new GenericException("优惠券核销统计导出EXCEL失败：筛选条件缺少核销商户名称");
					}
					String[] str = StringUtils.split(queryStr, "/", 2);
					searcher.setNickName(str[0]);
					searcher.setOperator(str[1]);
				}else{
					searcher.setNickName(queryStr.trim());
				}
			}else if(getBusiness() instanceof Shop){
				searcher.setOperator(queryStr.trim());
			}
		}
		Pager<Coupon> pager = new Pager<>();
		pager.setPageSize(Integer.MAX_VALUE);
		pager = couponPaymentService.consumeStatisticsList(searcher, pager);
		
		//2.加载子商户
		Map<Long, Shop> applicableShopMap = new HashMap<>();
		if(getBusiness() instanceof Merchant){
			//加载适用商户
			List<Shop> shopList = shopService.findShopListByMerchantId(getBusiness().getSelfBusinessId(), true);
			for(Shop shop : shopList){
				applicableShopMap.put(shop.getId(), shop);
			}
		}else if(getBusiness() instanceof Shop){
			Shop shop = (Shop)getBusiness();
			applicableShopMap.put(shop.getId(), shop);
		}
		
		// 第一步，创建一个webbook，对应一个Excel文件
		HSSFWorkbook wb = new HSSFWorkbook();
		// 第二步，在webbook中添加一个sheet,对应Excel文件中的sheet
		HSSFSheet sheet = wb.createSheet("优惠核销统计&明细");
		// 第三步，在sheet中添加表头第0行
		HSSFRow row = sheet.createRow(0);
		// 第四步，创建单元格，并设置值表头 设置表头居中
		HSSFCellStyle style = wb.createCellStyle();
		// 生成一个字体  
		HSSFFont font = wb.createFont();
		font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		// 把字体应用到当前的样式  
		style.setFont(font); 
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式
		
		String[] headers = new String[]{"核销时间", "电子券名称", "手机号码", "实际支付", "支付方式", "支付账号", "交易流水号", "核销商户/操作员"};
		for (int i = 0; i < headers.length; i++) {
			HSSFCell cell = row.createCell(i);
			cell.setCellStyle(style);  
			HSSFRichTextString text = new HSSFRichTextString(headers[i]);
			cell.setCellValue(text);
		}
		
		// 第五步，写入实体数据
		if(pager != null && pager.getTotalCount() > 0){
			int index = 1; //从第二行开始输出
			int headerLength = headers.length;
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			for(Coupon coupon : pager.getList()){
				row = sheet.createRow(index++);
				for(int j = 0; j < headerLength; j++){
					HSSFCell cell = row.createCell(j);
					switch(j){
						case 0:
							cell.setCellValue(sdf.format(coupon.getConsumeDate())); break;
						case 1:
							String couponInfoName = coupon.getCouponInfo().getName();
							if(CouponInfoType.CHILD.equals(coupon.getType())){
								couponInfoName = "【套票】" + coupon.getParent().getName() + "-" + couponInfoName;
							}
							cell.setCellValue(couponInfoName);break;
						case 2:
							String mobile = "-";
							if(StringUtils.isNotBlank(coupon.getMobile())){
								mobile = new StringBuilder(coupon.getMobile()).replace(3, 7, "****").toString();
							}
							cell.setCellValue(mobile); break;
						case 3:
							if(CouponInfoType.CHILD.equals(coupon.getType())){
								cell.setCellValue("未知");
							}else{
								cell.setCellValue(coupon.getPayment().getAvaragePrice().doubleValue());
							}
							break;
						case 4:
							cell.setCellValue(coupon.getPayment().getType().getDesc()); break;
						case 5:
							if(CouponPaymentType.ALIPAY_WAP.equals(coupon.getPayment().getType())){
								cell.setCellValue(coupon.getPayment().getBuyerAccount()); break;
							}else if(CouponPaymentType.UMPAY_WAP.equals(coupon.getPayment().getType())){
								cell.setCellValue(coupon.getPayment().getCardNumber()); break;
							}else{
								cell.setCellValue(""); break;
							}
						case 6:
							cell.setCellValue(coupon.getPayment().getSerial()); break;
						case 7:
							cell.setCellValue(applicableShopMap.get(coupon.getBusinessId()).getName() + "/" + coupon.getOperator().getUsername()); break;
					}
				}
			}
		}
		
		// 第六步，将文件存到指定位置
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
			String fileName = "CouponConsumeStatistic(" + sdf.format(new Date()) + ")";
			response.setHeader("Content-disposition", "attachment; filename=" + fileName + ".xls");// 设定输出文件头
			response.setContentType("application/msexcel");// 定义输出类型
			OutputStream out = response.getOutputStream();
			wb.write(out);
			out.close();
		} catch (Exception e) {
			logger.error("优惠券核销统计导出EXCEL失败！", e);
			throw new GenericException("优惠券核销统计导出EXCEL失败");
		}
	}
	
	
	
	/* =============================================财务报表（统计分析模块）START==================================================== */
	
	/*----------------------------------------------------------【点单分析】-【订单分析】--------------------------------------------------------------------*/
	/** 加载【订单分析】折线趋势页面 */
	@RequestMapping(value="order/analyze/trend", method = RequestMethod.GET)
	public ModelAndView orderTrendsAnalyze(ModelAndView mav, @RequestParam(value="date1", defaultValue="")String startDate
			, @RequestParam(value="date2", defaultValue="")String endDate, @RequestParam(value="day_block", defaultValue="")String dayBlock
			, @RequestParam(value="type", defaultValue="")String fromType){
		List<Shop> applicableShops = new ArrayList<>();
		if(getBusiness() instanceof Merchant){
			//集团账号登陆，加载所有商户
			applicableShops = shopService.findShopListByMerchantId(getBusiness().getSelfBusinessId(), true);
		}else if(getBusiness() instanceof Shop){
			applicableShops.add((Shop)getBusiness());
		}
		mav.addObject("orderTrendTypes", OrderTrendStatisticsType.values());
		mav.addObject("applicableShops", applicableShops);
		mav.addObject("startDate", startDate);
		mav.addObject("endDate", endDate);
		mav.addObject("dayBlock", dayBlock);
		mav.addObject("fromType", fromType);
		mav.setViewName("statistics/order/trend");
		return mav;
	}

	/** 点单分析--【点单量】 */
	@RequestMapping(value="/order/trend", method = RequestMethod.POST)
	@ResponseBody
	public OrderTrendStatistics getOrderAmountTrend(@RequestBody OrderTrendStatistics orderTrendStatistics){
		orderStatisticsService.orderTrendStatistics(orderTrendStatistics, getBusiness());
		return orderTrendStatistics;
	}

	/** 导出EXCEL 【点单分析】 */
	@RequestMapping(value="/order/trend/export", method = RequestMethod.GET)
	public void exportOrderTrendSummary(OrderTrendStatisticsExport orderTrendStatisticsExport, HttpServletResponse response) throws Exception{
		//generate Data
		orderTrendStatisticsExport = orderStatisticsService.orderTrendStatisticsForExport(orderTrendStatisticsExport, getBusiness());
		if(!orderTrendStatisticsExport.isIs_success()){
			throw new Exception(orderTrendStatisticsExport.getError_msg());
		}
		Map<OrderTrendStatisticsType, List<OrderTrendDateStatisticsDetail>> dailyMap = orderTrendStatisticsExport.getDaily_export();
		Map<OrderTrendStatisticsType, List<OrderTrendHourlyStatisticsDetail>> hourlyMap = orderTrendStatisticsExport.getHourly_export();
		
		// 第一步，创建一个webbook，对应一个Excel文件
		HSSFWorkbook wb = new HSSFWorkbook();
		// 第二步，在webbook中添加一个sheet,对应Excel文件中的sheet
		HSSFSheet sheet = wb.createSheet("点单分析-趋势分析");
		// 第三步，在sheet中添加表头第0行
		HSSFRow row = sheet.createRow(0);
		// 第四步，创建单元格，并设置值表头 设置表头居中
		HSSFCellStyle style = wb.createCellStyle();
		// 生成一个字体  
		HSSFFont font = wb.createFont();
		font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		// 把字体应用到当前的样式  
		style.setFont(font); 
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式
		
		//生成首行（表头）
		HSSFCell cell = row.createCell(1);
		cell.setCellStyle(style);
		StringBuilder header = new StringBuilder("点单分析-趋势分析(")
				.append(orderTrendStatisticsExport.getStart_dateTime_1().toString("yyyy/MM/dd"))
				.append("-")
				.append(orderTrendStatisticsExport.getEnd_dateTime_1().toString("yyyy/MM/dd"))
				.append(")");
		HSSFRichTextString text = new HSSFRichTextString(header.toString());
		cell.setCellValue(text);
		
		//生成第二行（标题行）
		OrderTrendStatisticsType[] typeArray = OrderTrendStatisticsType.values();
		row = sheet.createRow(1);
		for (int i = 0; i < typeArray.length + 1; i++) {
			cell = row.createCell(i);
			cell.setCellStyle(style);
			if(i == 0 && orderTrendStatisticsExport.isIs_multi_days()){
				text = new HSSFRichTextString("日期");
			}else if(i == 0 && !orderTrendStatisticsExport.isIs_multi_days()){
				text = new HSSFRichTextString("时段");
			}else if(i > 0){
				text = new HSSFRichTextString(typeArray[i-1].getDescription());
			}
			cell.setCellValue(text);
		}
		
		// 第五步，写入实体数据
		if(orderTrendStatisticsExport.isIs_multi_days() && !CollectionUtils.isEmpty(dailyMap)){
			int index = 2; //从第三行开始输出
			for(DateTime endDate = orderTrendStatisticsExport.getEnd_dateTime_1();
					!endDate.isBefore(orderTrendStatisticsExport.getStart_dateTime_1());
					endDate = endDate.minusDays(1)){
				row = sheet.createRow(index);
				for(int j = 0; j < typeArray.length + 1; j++){
					cell = row.createCell(j);
					if(j == 0){
						cell.setCellValue(endDate.toString("yyyy-MM-dd"));
					}else{
						OrderTrendStatisticsType type = typeArray[j-1];
						List<OrderTrendDateStatisticsDetail> list = dailyMap.get(type);
						cell.setCellValue(list.get(index - 2).getToday_amount().toString());
					}
				}
				index++;
			}
		}else if(!orderTrendStatisticsExport.isIs_multi_days() && !CollectionUtils.isEmpty(hourlyMap)){
			int index = 2; //从第三行开始输出
			int hr = 23;
			if(new DateTime().withTimeAtStartOfDay().equals(orderTrendStatisticsExport.getStart_dateTime_1())){//导出时间为当天，取当前时间小时数
				hr = DateTime.now().getHourOfDay();
			}
			for(int i = hr; i >= 0; i--){
				row = sheet.createRow(index);
				for(int j = 0; j < typeArray.length + 1; j++){
					cell = row.createCell(j);
					if(j == 0){
						String hour = StringUtils.leftPad(""+i, 2, '0');
						cell.setCellValue(hour + ":00-" + hour + ":59");
					}else{
						OrderTrendStatisticsType type = typeArray[j-1];
						List<OrderTrendHourlyStatisticsDetail> list = hourlyMap.get(type);
						cell.setCellValue(list.get(index - 2).getToday_amount().toString());
					}
				}
				index++;
			}
		}
		
		// 第六步，将文件存到指定位置
		try {
			String fileName = "OrderTrendStatistics(" + orderTrendStatisticsExport.getStart_dateTime_1().toString("yyyy/MM/dd") + "-"
					+ orderTrendStatisticsExport.getEnd_dateTime_1().toString("yyyy/MM/dd)");
			response.setHeader("Content-disposition", "attachment; filename=" + fileName + ".xls");// 设定输出文件头
			response.setContentType("application/msexcel");// 定义输出类型
			OutputStream out = response.getOutputStream();
			wb.write(out);
			out.close();
		} catch (Exception e) {
			logger.error("点单趋势统计导出EXCEL失败！", e);
			throw new GenericException("点单趋势统计导出EXCEL失败");
		}
	}
	
	/*-----------------------------------------------------------【点单分析】-【商户分析】-------------------------------------------------------------------*/
	/** 加载【订单分析】商户分析页面 */
	@RequestMapping(value="order/analyze/shop_ratio", method = RequestMethod.GET)
	public ModelAndView orderShopRatio(ModelAndView mav, @RequestParam(value="date1", defaultValue="")String startDate
					, @RequestParam(value="date2", defaultValue="")String endDate, @RequestParam(value="day_block", defaultValue="")String dayBlock
					, @RequestParam(value="type", defaultValue="")String fromType){
		List<Shop> applicableShops = new ArrayList<>();
		if(getBusiness() instanceof Merchant){
			//集团账号登陆，加载所有商户
			applicableShops = shopService.findShopListByMerchantId(getBusiness().getSelfBusinessId(), true);
		}else if(getBusiness() instanceof Shop){
			applicableShops.add((Shop)getBusiness());
		}
		mav.addObject("orderTrendTypes", OrderTrendStatisticsType.values());
		mav.addObject("applicableShops", applicableShops);
		mav.addObject("startDate", startDate);
		mav.addObject("endDate", endDate);
		mav.addObject("dayBlock", dayBlock);
		mav.addObject("fromType", fromType);
		mav.setViewName("statistics/order/shopRatio");
		return mav;
	}
	
	/** 【商户分析】 */
	@RequestMapping(value="/order/shop/ratio", method = RequestMethod.POST)
	@ResponseBody
	public ShopOrderRatioStatistics getOrderShopRatio(@RequestBody ShopOrderRatioStatistics shopOrderRatioStatistics){
		orderStatisticsService.orderRatioStatisticsByShop(shopOrderRatioStatistics, getBusiness());
		return shopOrderRatioStatistics;
	}

	/** 导出EXCEL 【点单分析】-【商户分析】 */
	@RequestMapping(value="/order/shop/ratio/export", method = RequestMethod.GET)
	public void exportOrderShopRatioSummary(ShopOrderRatioStatistics shopOrderRatioStatistics, HttpServletResponse response) throws Exception{
		//generate Data
		orderStatisticsService.orderRatioStatisticsByShop(shopOrderRatioStatistics, getBusiness());
		if(!shopOrderRatioStatistics.isIs_success()){
			throw new Exception(shopOrderRatioStatistics.getError_msg());
		}
		
		// 第一步，创建一个webbook，对应一个Excel文件
		HSSFWorkbook wb = new HSSFWorkbook();
		// 第二步，在webbook中添加一个sheet,对应Excel文件中的sheet
		HSSFSheet sheet = wb.createSheet("点单分析-商户分析");
		// 第三步，在sheet中添加表头第0行
		HSSFRow row = sheet.createRow(0);
		// 第四步，创建单元格，并设置值表头 设置表头居中
		HSSFCellStyle style = wb.createCellStyle();
		// 生成一个字体  
		HSSFFont font = wb.createFont();
		font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		// 把字体应用到当前的样式  
		style.setFont(font); 
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式
		
		//生成首行（表头）
		HSSFCell cell = row.createCell(1);
		cell.setCellStyle(style);
		StringBuilder header = new StringBuilder("点单分析-商户分析(")
				.append(shopOrderRatioStatistics.getStart_dateTime().toString("yyyy/MM/dd"))
				.append("-")
				.append(shopOrderRatioStatistics.getEnd_dateTime().toString("yyyy/MM/dd"))
				.append(")");
		HSSFRichTextString text = new HSSFRichTextString(header.toString());
		cell.setCellValue(text);
		
		//生成第二行（标题行）
		row = sheet.createRow(1);
		String[] headers = new String[]{"商户", "点单量", "点单总金额"};
		for (int i = 0; i < headers.length; i++) {
			cell = row.createCell(i);
			cell.setCellStyle(style);  
			text = new HSSFRichTextString(headers[i]);
			cell.setCellValue(text);
		}
		
		// 第五步，写入实体数据
		if(!CollectionUtils.isEmpty(shopOrderRatioStatistics.getShop_ratios())){
			int index = 2; //从第三行开始输出
			for(ShopOrderRatioStatisticsDetail sorsd : shopOrderRatioStatistics.getShop_ratios()){
				row = sheet.createRow(index++);
				for(int j = 0; j < 3; j++){
					cell = row.createCell(j);
					switch(j){
						case 0:
							cell.setCellValue(sorsd.getShop_name());break;
						case 1:
							cell.setCellValue(sorsd.getAmount());break;
						case 2:
							cell.setCellValue(sorsd.getSum().toString());break;
					}
				}
			}
		}
		
		// 第六步，将文件存到指定位置
		try {
			String fileName = "OrderTrendStatistics(" + shopOrderRatioStatistics.getStart_dateTime().toString("yyyy/MM/dd") + "-"
					+ shopOrderRatioStatistics.getEnd_dateTime().toString("yyyy/MM/dd)");
			response.setHeader("Content-disposition", "attachment; filename=" + fileName + ".xls");// 设定输出文件头
			response.setContentType("application/msexcel");// 定义输出类型
			OutputStream out = response.getOutputStream();
			wb.write(out);
			out.close();
		} catch (Exception e) {
			logger.error("点单分布【商户分析】导出EXCEL失败！", e);
			throw new GenericException("点单统计导出商户分析EXCEL失败");
		}
	}
	
	/*-----------------------------------------------------------【品类分析】-------------------------------------------------------------------*/
	/** 加载【点单分析】-【品类分析】页面 */
	@RequestMapping(value="item_category/analyze", method = RequestMethod.GET)
	public ModelAndView orderCategoryRatio(ModelAndView mav){
		List<Shop> applicableShops = new ArrayList<>();
		if(getBusiness() instanceof Merchant){
			//集团账号登陆，加载所有商户
			applicableShops = shopService.findShopListByMerchantId(getBusiness().getSelfBusinessId(), true);
		}else if(getBusiness() instanceof Shop){
			applicableShops.add((Shop)getBusiness());
		}
		mav.addObject("itemCategoryRatioTypes", CategoryRatioStatisticsType.values());
		mav.addObject("applicableShops", applicableShops);
		mav.setViewName("statistics/order/itemCategoryRatio");
		return mav;
	}
	
	/** 【品类分析】 */
	@RequestMapping(value="/category/ratio", method = RequestMethod.POST)
	@ResponseBody
	public CategoryRatioStatistics getOrderCategoryRatio(@RequestBody CategoryRatioStatistics categoryRatioStatistics){
		orderStatisticsService.categoryRatioStatisticsByShop(categoryRatioStatistics, getBusiness());
		return categoryRatioStatistics;
	}
	
	/*-----------------------------------------------------------【商品分析】-------------------------------------------------------------------*/
	/** 加载【点单分析】-【商品分析】页面 */
	@RequestMapping(value="item/analyze", method = RequestMethod.GET)
	public ModelAndView orderItemRatio(ModelAndView mav){
		List<Shop> applicableShops = new ArrayList<>();
		if(getBusiness() instanceof Merchant){
			//集团账号登陆，加载所有商户
			applicableShops = shopService.findShopListByMerchantId(getBusiness().getSelfBusinessId(), true);
		}else if(getBusiness() instanceof Shop){
			applicableShops.add((Shop)getBusiness());
		}
		mav.addObject("itemRatioTypes", ItemRatioStatisticsType.values());
		mav.addObject("applicableShops", applicableShops);
		mav.setViewName("statistics/order/itemRatio");
		return mav;
	}
	
	/** 商品分析--【点单量】 */
	@RequestMapping(value="/item/ratio", method = RequestMethod.POST)
	@ResponseBody
	public ItemRatioStatistics getOrderItemRatio(@RequestBody ItemRatioStatistics itemRatioStatistics){
		orderStatisticsService.itemRatioStatisticsByShop(itemRatioStatistics, getBusiness());
		return itemRatioStatistics;
	}
	
	/* =============================================财务报表（统计分析模块）END==================================================== */
}
