package com.xpos.api;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.CollectionUtils;

import com.xpos.api.param.OrderListItem;
import com.xpos.api.result.ResCode;
import com.xpos.api.result.ValidateError;
import com.xpos.common.entity.Configuration;
import com.xpos.common.entity.Terminal;
import com.xpos.common.entity.pos.POSGatewayAccount.POSGatewayAccountType;
import com.xpos.common.entity.pos.POSTransaction;
import com.xpos.common.entity.pos.POSTransaction.POSTransactionStatus;
import com.xpos.common.entity.pos.POSTransaction.POSTransactionType;
import com.xpos.common.searcher.POSTransactionSearcher;
import com.xpos.common.service.ConfigurationService;
import com.xpos.common.service.POSTransactionService;
import com.xpos.common.service.TerminalService;
import com.xpos.common.utils.Pager;
import com.xpos.common.utils.SignEncodeUtil;

public class OrderListResource extends BaseResource{
	private Logger logger = LoggerFactory.getLogger(OrderListResource.class);
	private final String MOBILE_NUM_REGEX = "MOBILE_NUMBER_REGEX";
	
	@Autowired
	private TerminalService terminalService;
	@Autowired
	private POSTransactionService transactionService;
	@Autowired
	private ConfigurationService confService;
	
	@Value("#{settings['umpay.keyFilePath']}")
	private String keyPath;
	
	/**
	 * /merchant/{mid}/order/list?deviceNumber=xxx&startDate =2013-09-01&endDate=2013-10-1&minSum=100&maxSum=1000&status=1&page=1&pagesize=20
	 * @return
	 */
	@Get("json")
	public Representation getOrderList(){
		Long mid = NumberUtils.toLong((String) getRequestAttributes().get("mid"));
		String deviceNumber = getQuery().getFirstValue("deviceNumber");
		String startDate = getQuery().getFirstValue("startDate");
		String endDate = getQuery().getFirstValue("endDate");
		String minSumStr = getQuery().getFirstValue("minSum");
		String maxSumStr = getQuery().getFirstValue("maxSum");
		String[] statusArray = getQuery().getValuesArray("status");
		String pageTo = getQuery().getFirstValue("page");
		String pagesize = getQuery().getFirstValue("pagesize");
		String operator = null; // 从session中获取
		Integer channel = NumberUtils.toInt(getQuery().getFirstValue("channel"), 0); //第三方支付机构类型，1:联动优势，2:中国银行，3:盛付通，不传默认查询所有类型
		Integer type = NumberUtils.toInt(getQuery().getFirstValue("type"), 0); //订单支付手段，1-银行卡, 2-充值卡, 3-会员积分消费, 4-移动电子券
		String phone = getQuery().getFirstValue("phone"); //指定用户手机号

		if(StringUtils.isBlank(deviceNumber)){
			return new JsonRepresentation(new ValidateError("2301", "deviceNumber不能为空"));
		}
		
		//else if(StringUtils.isBlank(operator)){
		//	return new JsonRepresentation(new ValidateError("2308", "当前用户未登录"));
		//}

		//take care of device_number here...
		Terminal terminal = terminalService.findTerminalByDevice(deviceNumber);
		if(terminal == null){
			return new JsonRepresentation(new ValidateError("2302","设备未注册"));
		}else if(terminal.getShop() == null){
			return new JsonRepresentation(new ValidateError("2303","指定商户不存在/已删除"));
		}else if(!terminal.getShop().getId().equals(mid)){
			return new JsonRepresentation(new ValidateError("2304","商户与设备不匹配"));
		}
		
		BigDecimal minSum = getMoneySum(minSumStr);
		BigDecimal maxSum = getMoneySum(maxSumStr);
		if(minSum != null && minSum.compareTo(new BigDecimal(0)) < 0){
			return new JsonRepresentation(new ValidateError("2305","交易金额范围错误"));
		}else if(maxSum != null && maxSum.compareTo(minSum) < 0 ){
			return new JsonRepresentation(new ValidateError("2305","交易金额范围错误"));
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
		
		//verify status
		Set<POSTransactionStatus> statusSet = new HashSet<>();
		if(statusArray == null || statusArray.length == 0){ //URL传入参数为空
			for(POSTransactionStatus transactionStatus : POSTransactionStatus.values()){
				statusSet.add(transactionStatus);
			}
		}else{
			for(int i = 0; i < statusArray.length; i++){
				Integer state = NumberUtils.toInt(statusArray[i], Integer.MIN_VALUE);
				POSTransactionStatus _status = POSTransactionStatus.queryByState(state);
				if(_status != null){
					statusSet.add(_status);
				}
			}
		}
		
		DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd");
		// parse end date
		DateTime eDate = null;
		try {
			if(StringUtils.isBlank(endDate)){
				eDate = new DateTime();
			}else{
				eDate = fmt.parseDateTime(endDate);
			}
		} catch (Exception e) {
			return new JsonRepresentation(new ValidateError("2307","结束日期格式错误"));
		}
		
		//parse start date
		DateTime sDate = null;
		try{
			if(StringUtils.isBlank(startDate)){
				sDate = new DateTime().withTimeAtStartOfDay().minusDays(7).toDateTime(); //默认查询最近一周
			}else{
				sDate = fmt.parseDateTime(startDate);
			}
		}catch(Exception e){
			return new JsonRepresentation(new ValidateError("2307","开始日期格式错误"));
		}
		
		
		if(sDate != null && eDate != null && sDate.isAfter(eDate.getMillis())){
			return new JsonRepresentation(new ValidateError("2307","时间范围不能前后颠倒"));
		}
		
		//verify channel
		POSGatewayAccountType gatewayAccountType = POSGatewayAccountType.queryByState(channel);
		
		//verify type
		POSTransactionType transactionType = null;
		if(type > 0){
			for(POSTransactionType _type : POSTransactionType.values()){
				if(_type.getType() == type){
					transactionType = _type;
				}
			}
		}
		
		//verify phone
		String mobile = null;
		Configuration conf = confService.findByName(MOBILE_NUM_REGEX);
		if(StringUtils.isNotBlank(phone) && Pattern.matches(conf.getValue(), phone)){
			mobile = phone;
		}
		
		POSTransactionSearcher searcher = new POSTransactionSearcher();
		searcher.setStartDate(sDate.toDate());
		searcher.setEndDate(eDate.toDate());
		searcher.setMobile(mobile);
		searcher.setMinSum(minSum);
		searcher.setMaxSum(maxSum);
		searcher.setGatewayAccountType(gatewayAccountType);
		searcher.setStatusSet(statusSet);
		searcher.setType(transactionType);
		searcher.setOrder(3);//默认按交易时间倒序
//		searcher.setOperator(channel == ExternalMidTypeEnum.BANK_OF_CHINA.getType()? "boc-pos":operator);
		
		JsonRepresentation re = null;
		Pager<POSTransaction> pager = new Pager<POSTransaction>();
		pager.setPageSize(size);
		pager.setPageNumber(currentPage);
		
		List<OrderListItem> orderList = new ArrayList<>();
		try{
			pager = transactionService.findTransactions(terminal.getShop(), searcher, pager);
			if(!CollectionUtils.isEmpty(pager.getList())){
				for(POSTransaction transaction : pager.getList()){
					OrderListItem orderItem = new OrderListItem();
					orderItem.setId(transaction.getId());
					orderItem.setMid(transaction.getBusinessId());
					orderItem.setRegisterMid(transaction.getGatewayAccount());
					orderItem.setRegisterType(transaction.getGatewayType().getState());
					orderItem.setMobile(transaction.getMaskedMobile());
					orderItem.setSum(BigDecimal.valueOf(100).multiply(transaction.getSum()).setScale(2, RoundingMode.HALF_UP).intValue());//转换成以分为单位
					String signature = null;
					if(transaction.getGatewayType().getState() == POSGatewayAccountType.UMPAY.getState()
							&& transaction.getType().equals(POSTransactionType.BANK_CARD)){ //联动优势刷卡交易列表，返回撤销签名
						StringBuffer bf = new StringBuffer();
						bf.append(transaction.getGatewayAccount()).append(transaction.getCode());//用户撤销交易的签名，对“商户编号（registerMid）+订单编号（orderId）”加签
						signature = SignEncodeUtil.umpaySignData(bf.toString(), keyPath);
					}
					orderItem.setSignature(signature);
					orderItem.setOrderId(transaction.getCode());
					orderItem.setCardNum(StringUtils.isBlank(transaction.getCardNumber())?"":transaction.getCardNumber());
					orderItem.setTradeTime(transaction.getTradeDate()==null?"":new DateTime(transaction.getTradeDate()).toString("yyyy-MM-dd HH:mm:ss"));
					orderItem.setStatus(transaction.getStatus().getState());
					orderItem.setTraceNo(StringUtils.defaultIfBlank(transaction.getTraceNo(),""));
					orderItem.setRefNo(transaction.getRefNo());
					orderItem.setType(transaction.getType()!=null?transaction.getType().getType():-1);
					orderItem.setBatchNo(StringUtils.defaultIfBlank(transaction.getBatchNo(), ""));
					orderItem.setAuthCode(StringUtils.defaultIfBlank(transaction.getAuthCode(), ""));
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
			
		}catch(Exception e){
			logger.error("Cannot find order list due to "+ e.getMessage(), e);
			re = new JsonRepresentation(ResCode.General.SERVER_INTERNAL_ERROR);
		}
		return re;
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
