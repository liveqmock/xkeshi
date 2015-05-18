package com.xpos.api;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.joda.time.DateTime;
import org.json.JSONObject;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.xpos.api.result.ResCode;
import com.xpos.api.result.ValidateError;
import com.xpos.common.entity.Coupon.CouponStatus;
import com.xpos.common.entity.Order.Status;
import com.xpos.common.entity.Terminal;
import com.xpos.common.entity.example.CouponExample;
import com.xpos.common.entity.member.Member;
import com.xpos.common.entity.pos.POSGatewayAccount.POSGatewayAccountType;
import com.xpos.common.entity.pos.POSTransaction.POSTransactionStatus;
import com.xpos.common.entity.pos.POSTransaction.POSTransactionType;
import com.xpos.common.searcher.CouponSearcher;
import com.xpos.common.searcher.OrderSearcher;
import com.xpos.common.searcher.POSTransactionSearcher;
import com.xpos.common.searcher.member.MemberSearcher;
import com.xpos.common.service.CouponService;
import com.xpos.common.service.OrderService;
import com.xpos.common.service.POSTransactionService;
import com.xpos.common.service.TerminalService;
import com.xpos.common.service.member.MemberService;
import com.xpos.common.utils.Pager;

public class StatisticsResource extends BaseResource{
	private Logger logger = LoggerFactory.getLogger(StatisticsResource.class);

	@Autowired
	private TerminalService terminalService;
	@Autowired
	private POSTransactionService transactionService;
	@Autowired
	private OrderService orderService;
	@Autowired
	private CouponService couponService;
	@Autowired
	private MemberService memberService;
	
	/**
	 * /statistics/{mid}/summary?deviceNumber=xxx
	 */
	@Get
	public Representation statisticsSummary(){
		Long mid = NumberUtils.toLong((String) getRequestAttributes().get("mid"));
		String deviceNumber = getQuery().getFirstValue("deviceNumber");
		Integer channel = NumberUtils.toInt(getQuery().getFirstValue("channel"), 0); //第三方支付机构类型，1:联动优势，2:中国银行，3:盛付通
		String date = getQuery().getFirstValue("date");
		
		if(StringUtils.isBlank(deviceNumber)){
			return new JsonRepresentation(new ValidateError("3401", "deviceNumber不能为空"));
		}

		//take care of device_number here...
		Terminal terminal = terminalService.findTerminalByDevice(deviceNumber);
		if(terminal == null){
			return new JsonRepresentation(new ValidateError("3402","设备未注册"));
		}else if(terminal.getShop() == null){
			return new JsonRepresentation(new ValidateError("3403","指定商户不存在/已删除"));
		}else if(!terminal.getShop().getId().equals(mid)){
			return new JsonRepresentation(new ValidateError("3404","商户与设备不匹配"));
		}
		
		//verify channel
		POSGatewayAccountType gatewayAccountType = POSGatewayAccountType.queryByState(channel);
		
		//parse date
		Date today = null;
		if(StringUtils.isBlank(date)){
			today = new Date(); //默认查询今日;
		}else{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); 
			try{
				today = sdf.parse(date);
				if(today.after(new Date())){
					return new JsonRepresentation(new ValidateError("3405","日期范围错误"));
				}
			}catch(Exception e){
				return new JsonRepresentation(new ValidateError("3405","日期格式错误"));
			}
		}
		Date yesterday = new DateTime(today).minusDays(1).toDate();
		
		JsonRepresentation re = null;
		
		try{
			//销售商品统计
			OrderSearcher orderSearcher = new OrderSearcher();
			orderSearcher.setStartDate(today);
			orderSearcher.setEndDate(today);
			orderSearcher.setStatus(Status.SUCCESS);
			String[] todayOrderStatistic = orderService.getOrderStatistics(terminal.getShop(), orderSearcher);
			orderSearcher.setStartDate(yesterday);
			orderSearcher.setEndDate(yesterday);
			String[] yesterdayOrderStatistic = orderService.getOrderStatistics(terminal.getShop(), orderSearcher);
			
			//POS刷卡、电子券统计
			POSTransactionSearcher posTransactionSearcher = new POSTransactionSearcher();
			Set<POSTransactionStatus> statusSet = new HashSet<>();
			statusSet.add(POSTransactionStatus.PAID_SUCCESS);
			posTransactionSearcher.setStatusSet(statusSet);
			
			posTransactionSearcher.setStartDate(today);//当天
			posTransactionSearcher.setEndDate(today);//当天
			posTransactionSearcher.setGatewayAccountType(gatewayAccountType);
			posTransactionSearcher.setType(POSTransactionType.BANK_CARD);//刷卡
			String[] todayPOSStatistic = transactionService.getBankCardStatistic(terminal.getShop(), posTransactionSearcher);//当天刷卡统计
			posTransactionSearcher.setGatewayAccountType(null);
			posTransactionSearcher.setType(POSTransactionType.ELECTRONIC_CASH);//电子现金
			String[] todayElectronicCashStatistic = transactionService.getElectronicCashStatistic(terminal.getShop(), posTransactionSearcher);//当天电子券统计
			posTransactionSearcher.setType(POSTransactionType.ALIPAY);//支付宝当面付
			String[] todayAlipayQRCodeStatistic = transactionService.getAlipayQRCodeStatistic(terminal.getShop(), posTransactionSearcher);//当天支付宝当面付统计
			
			posTransactionSearcher.setStartDate(yesterday);//昨日
			posTransactionSearcher.setEndDate(yesterday);//昨日
			posTransactionSearcher.setGatewayAccountType(gatewayAccountType);
			posTransactionSearcher.setType(POSTransactionType.BANK_CARD);//刷卡
			String[] yesterdayPOSStatistic = transactionService.getBankCardStatistic(terminal.getShop(), posTransactionSearcher);//昨日刷卡统计
			posTransactionSearcher.setGatewayAccountType(null);
			posTransactionSearcher.setType(POSTransactionType.ELECTRONIC_CASH);//电子现金
			String[] yesterdayElectronicCashStatistic = transactionService.getElectronicCashStatistic(terminal.getShop(), posTransactionSearcher);//昨日电子券统计
			posTransactionSearcher.setType(POSTransactionType.ALIPAY);//支付宝当面付
			String[] yesterdayAlipayQRCodeStatistic = transactionService.getAlipayQRCodeStatistic(terminal.getShop(), posTransactionSearcher);//昨日支付宝当面付统计
			
			//核销数量统计
			CouponSearcher couponSearcher = new CouponSearcher();
			Set<CouponStatus> couponStatus = new HashSet<>();
			couponStatus.add(CouponStatus.USED);
			couponSearcher.setBusiness(terminal.getShop());
			couponSearcher.setStatus(couponStatus);
			couponSearcher.setStartDate(today);
			couponSearcher.setEndDate(today);
			int todayConsumedCouponCount = couponService.countCoupons((CouponExample)couponSearcher.getExample());
			couponSearcher.setStartDate(yesterday);
			couponSearcher.setEndDate(yesterday);
			int yesterdayConsumedCouponCount = couponService.countCoupons((CouponExample)couponSearcher.getExample());
			
			//新增会员数统计
			MemberSearcher searcher = new MemberSearcher();
			searcher.setCreateStartDate(new DateTime(today).withTimeAtStartOfDay().toDate());
			Pager<Member> pager = new Pager<>();
			pager.setPageSize(Integer.MAX_VALUE);
			int todayMemberCount = memberService.findMembersByBusiness(terminal.getShop(), pager, searcher).getTotalCount();
	
			MemberSearcher searcher2 = new MemberSearcher();
			searcher2.setCreateStartDate(new DateTime(yesterday).withTimeAtStartOfDay().toDate());
			searcher.setCreateEndDate(new DateTime().withTimeAtStartOfDay().toDate());
			Pager<Member> pager2 = new Pager<>();
			pager2.setPageSize(Integer.MAX_VALUE);
			int yesterdayMemberCount = memberService.findMembersByBusiness(terminal.getShop(), pager2, searcher2).getTotalCount();
		
			re = new JsonRepresentation(ResCode.General.OK);
			JSONObject json = re.getJsonObject();
			json.put("todayOrderItemCount", todayOrderStatistic[0]);
			json.put("todayOrderAmount", todayOrderStatistic[1]);
			json.put("todayOrderCount", todayOrderStatistic[2]);
			json.put("yesterdayOrderItemCount", yesterdayOrderStatistic[0]);
			json.put("yesterdayOrderAmount", yesterdayOrderStatistic[1]);
			json.put("yesterdayOrderCount", yesterdayOrderStatistic[2]);
			json.put("todayPOSCount", todayPOSStatistic[0]);
			json.put("todayPOSAmount", todayPOSStatistic[1]);
			json.put("yesterdayPOSCount", yesterdayPOSStatistic[0]);
			json.put("yesterdayPOSAmount", yesterdayPOSStatistic[1]);
			json.put("todayElectronicCashCount", todayElectronicCashStatistic[0]);
			json.put("todayElectronicCashAmount", todayElectronicCashStatistic[1]);
			json.put("yesterdayElectronicCashCount", yesterdayElectronicCashStatistic[0]);
			json.put("yesterdayElectronicCashAmount", yesterdayElectronicCashStatistic[1]);
			json.put("todayAlipayQRCodeCount", todayAlipayQRCodeStatistic[0]);
			json.put("todayAlipayQRCodeAmount", todayAlipayQRCodeStatistic[1]);
			json.put("yesterdayAlipayQRCodeCount", yesterdayAlipayQRCodeStatistic[0]);
			json.put("yesterdayAlipayQRCodeAmount", yesterdayAlipayQRCodeStatistic[1]);
			json.put("todayCouponCount", todayConsumedCouponCount);
			json.put("yesterdayCouponCount", yesterdayConsumedCouponCount);
			json.put("todayMemberCount", todayMemberCount);
			json.put("yesterdayMemberCount", yesterdayMemberCount);
			
			json.put("todayCMCCTicketCount", "0"); //TODO 为兼容老版本APP的统计左侧列表。待删除
			json.put("todayCMCCTicketAmount", "0");
			json.put("yesterdayCMCCTicketCount", "0");
			json.put("yesterdayCMCCTicketAmount", "0");
			
		}catch(Exception e){
			logger.error("error happens when count daily statistics summary due to "+ e.getMessage(), e);
			re = new JsonRepresentation(ResCode.General.SERVER_INTERNAL_ERROR);
		}
		return re;
	}
	
}
