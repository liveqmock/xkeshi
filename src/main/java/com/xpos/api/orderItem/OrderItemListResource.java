package com.xpos.api.orderItem;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONObject;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import com.xkeshi.dao.CashTransactionDAO;
import com.xkeshi.dao.PrepaidCardTransactionDAO;
import com.xkeshi.dao.WXPayTransactionDAO;
import com.xkeshi.pojo.po.Shop;
import com.xkeshi.service.XShopService;
import com.xpos.api.BaseResource;
import com.xpos.api.param.OrderItem;
import com.xpos.api.result.ResCode;
import com.xpos.api.result.ValidateError;
import com.xpos.common.entity.Order;
import com.xpos.common.entity.Order.Status;
import com.xpos.common.entity.Terminal;
import com.xpos.common.entity.pos.POSOperationLog;
import com.xpos.common.persistence.mybatis.AlipayTransactionMapper;
import com.xpos.common.persistence.mybatis.BankNFCTransactionMapper;
import com.xpos.common.persistence.mybatis.POSTransactionMapper;
import com.xpos.common.searcher.OrderSearcher;
import com.xpos.common.service.OperatorShiftService;
import com.xpos.common.service.OrderService;
import com.xpos.common.service.TerminalService;
import com.xpos.common.utils.Pager;

public class OrderItemListResource extends BaseResource{
	private Logger logger = LoggerFactory.getLogger(OrderItemListResource.class);

	@Autowired
	private TerminalService terminalService;
	@Autowired
	private OrderService orderService;
	
	@Autowired
	private BankNFCTransactionMapper  bankNFCTransactionMapper   ;

	@Autowired
	private POSTransactionMapper    posTransactionMapper  ;
	
	@Autowired
	private WXPayTransactionDAO  wxPayTransactionDAO   ;
	
	@Autowired
	private AlipayTransactionMapper  alipayTransactionMapper   ;
	
	@Autowired
	private CashTransactionDAO   cashTransactionDAO   ;
	
	@Autowired
	private PrepaidCardTransactionDAO  prepaidCardTransactionDAO   ;
	
	@Autowired
	private OperatorShiftService  operatorShiftService   ;
	
	@Autowired
	private XShopService   xShopService  ;
	
	/**
	 * /orderItem/{mid}/list?deviceNumber=xxx&startDate=2013-09-01&endDate=2013-10-1&minSum=100&maxSum=1000&page=1&pagesize=20&status=SUCCESS
	 */
	@Get
	public Representation getOrderList(){
		Long mid = NumberUtils.toLong((String) getRequestAttributes().get("mid"));
		String deviceNumber = getQuery().getFirstValue("deviceNumber");
		Long operatorId = Long.valueOf(getQuery().getFirstValue("operatorId"));
		String startDate = getQuery().getFirstValue("startDate");
		String endDate = getQuery().getFirstValue("endDate");
		String minSumStr = getQuery().getFirstValue("minSum");
		String maxSumStr = getQuery().getFirstValue("maxSum");
		String pageTo = getQuery().getFirstValue("page");
		String pagesize = getQuery().getFirstValue("pagesize");
		String statusStr = getQuery().getFirstValue("status");
		
		
		if(StringUtils.isBlank(deviceNumber)){
			return new JsonRepresentation(new ValidateError("3501", "deviceNumber不能为空"));
		}
		
		else if(operatorId == null){
			return new JsonRepresentation(new ValidateError("2308", "当前用户未登录"));
		}
		String operatorSessionCode =  null;
		Shop shopPO = xShopService.findShopByShopId(mid);
		if (shopPO.getEnableShift()) {
			POSOperationLog posOperationLog = operatorShiftService.getLastOperatorSession(deviceNumber, operatorId);
			operatorSessionCode = posOperationLog.getOperatorSessionCode();
		}
		//take care of device_number here...
		Terminal terminal = terminalService.findTerminalByDevice(deviceNumber);
		if(terminal == null){
			return new JsonRepresentation(new ValidateError("3502","设备未注册"));
		}else if(terminal.getShop() == null){
			return new JsonRepresentation(new ValidateError("3503","指定商户不存在/已删除"));
		}else if(!terminal.getShop().getId().equals(mid)){
			return new JsonRepresentation(new ValidateError("3504","商户与设备不匹配"));
		}
		
		BigDecimal minSum = getMoneySum(minSumStr);
		BigDecimal maxSum = getMoneySum(maxSumStr);
		if(minSum != null && minSum.compareTo(new BigDecimal(0)) < 0){
			return new JsonRepresentation(new ValidateError("3505","交易金额范围错误"));
		}else if(maxSum != null && maxSum.compareTo(minSum) < 0 ){
			return new JsonRepresentation(new ValidateError("3505","交易金额范围错误"));
		}
		
		//Default display the first page
		Integer currentPage = (StringUtils.isBlank(pageTo) || !StringUtils.isNumeric(pageTo))? 1:Integer.parseInt(pageTo);
		if(currentPage <= 0){
			currentPage = 1;
		}
		//Default page size is 20
		Integer size = (StringUtils.isBlank(pagesize) || !StringUtils.isNumeric(pagesize))? 20:Integer.parseInt(pagesize);
		if(size <= 0){
			size = 20;
		}
		
		DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd");
		// parse end date
		DateTime eDate = null;
		try {
			if(StringUtils.isNotBlank(endDate)){
				eDate = fmt.parseDateTime(endDate).plusDays(1);
			}
		} catch (Exception e) {
			return new JsonRepresentation(new ValidateError("3507","结束日期格式错误"));
		}
		
		//parse start date
		DateTime sDate = null;
		try{
			if(StringUtils.isBlank(startDate)){
				sDate = new DateTime().withTimeAtStartOfDay(); //默认查询今日流水
			}else{
				sDate = fmt.parseDateTime(startDate);
			}
		}catch(Exception e){
			return new JsonRepresentation(new ValidateError("3507","开始日期格式错误"));
		}
		
		
		if(sDate != null && eDate != null && sDate.isAfter(eDate.getMillis())){
			return new JsonRepresentation(new ValidateError("3507","时间范围错误"));
		}
		
		//validate status
		Status status = null;
		if(StringUtils.isNotBlank(statusStr) && Status.findByName(statusStr) != null){ //不传默认SUCCESS的订单
			status = Status.findByName(statusStr);
		}
		
		OrderSearcher searcher = new OrderSearcher();
		if(operatorSessionCode == null) {
			searcher.setOperatorId(operatorId);
		}else {
			searcher.setOperatorSessionCode(operatorSessionCode);
		}
		searcher.setStartDateTime(sDate != null ? sDate.toDate() : null);
		searcher.setEndDateTime(eDate != null ? eDate.toDate() : null);
		searcher.setOrder(1);//默认按ID倒序
		searcher.setMinAmount(minSum);
		searcher.setMaxAmount(maxSum);
		if(status != null) {
			searcher.setStatus(status);
		}
		
		JsonRepresentation re = null;
		Pager<Order> pager = new Pager<>();
		pager.setPageSize(size);
		pager.setPageNumber(currentPage);
		
		List<OrderItem> orderList = new ArrayList<>();
		DateTime today = new DateTime();
		try{
			//今日销售商品统计(订单数、总金额)
			OrderSearcher orderSearcher = new OrderSearcher();
			orderSearcher.setStartDateTime(today.withTimeAtStartOfDay().toDate());
			orderSearcher.setEndDateTime(today.plusDays(1).withTimeAtStartOfDay().toDate());
			orderSearcher.setStatus(Status.SUCCESS); //仅统计成功的
			if(operatorSessionCode == null) {
				orderSearcher.setOperatorId(operatorId);
			}else {
				orderSearcher.setOperatorSessionCode(operatorSessionCode);
			}
			String[] todayOrderStatistic = orderService.getOrderStatistics(terminal.getShop(), orderSearcher);
			
			orderService.findOrders(terminal.getShop(), pager, searcher);
			if(!CollectionUtils.isEmpty(pager.getList())){
				for(Order order : pager.getList()){
					OrderItem orderItem = new OrderItem();
					orderItem.setSerial(order.getOrderNumber());
					orderItem.setAmount(order.getActuallyPaid());
					orderItem.setType(getPaymentsByOrderNumber(order.getOrderNumber()));
					orderItem.setStatus(order.getStatus().toString());
					orderItem.setTradeTime(new DateTime(order.getCreateDate()).toString("HH:mm"));
					orderList.add(orderItem);
				}
			}
			re = new JsonRepresentation(ResCode.General.OK);
			JSONObject json = re.getJsonObject();
			json.put("total", pager.getTotalCount());
			json.put("page", currentPage);
			json.put("pagesize", size);
			json.put("hasPrefix", pager.isForward());
			json.put("hasNext", pager.isNext());
			json.put("list", orderList);
			json.put("todayOrderCount", todayOrderStatistic[2]);
			json.put("todayOrderAmount", todayOrderStatistic[1]);
			
		}catch(Exception e){
			logger.error("Cannot find order list due to "+ e.getMessage(), e);
			re = new JsonRepresentation(ResCode.General.SERVER_INTERNAL_ERROR);
		}
		return re;
	}
	
	private String getPaymentsByOrderNumber(String orderNumber) {
		List<String> payments = new ArrayList<>();
		 //预付卡支付
		 if (prepaidCardTransactionDAO.getPayTransactionGroupByOrderNumber(orderNumber) != null) {
			 payments.add("预付卡");
		 }
		 //支付宝扫码支付
		 if (alipayTransactionMapper.getPayTransactionGroupByOrderNumber(orderNumber) != null) {
			 payments.add("支付宝扫码");
		 }
		 //电子钱包(NFC)支付
		 if (bankNFCTransactionMapper.getPayTransactionGroupByOrderNumber(orderNumber) != null) {
			 payments.add("电子钱包");
		 }
		 //现金支付
		 if (cashTransactionDAO.getPayTransactionGroupByOrderNumber(orderNumber) != null) {
			 payments.add("现金");
		 }
		 //pos刷卡支付
		 if (posTransactionMapper.getPayTransactionGroupByOrderNumber(orderNumber) != null) {
			 payments.add("POS刷卡");
		 }
		 //微信扫码支付
		 if (wxPayTransactionDAO.getPayTransactionGroupByOrderNumber(orderNumber) != null) {
			 payments.add("微信扫码");
		 }
		 return StringUtils.join(payments, ",");
	}
	
	private BigDecimal getMoneySum(String sumStr){
		if(StringUtils.isNotBlank(sumStr)){
			if(sumStr.matches("^[0-9]*$")){
				return BigDecimal.valueOf(0.01d).multiply(BigDecimal.valueOf(Integer.valueOf(sumStr))).setScale(2, RoundingMode.HALF_UP);
			}else{
				return null;
			}
		}else return null;
	}
	
}
