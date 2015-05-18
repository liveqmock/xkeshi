package com.xpos.api.orderItem;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xkeshi.pojo.po.Shop;
import com.xkeshi.service.XShopService;
import com.xpos.api.BaseResource;
import com.xpos.api.result.ResCode;
import com.xpos.api.result.ValidateError;
import com.xpos.common.entity.*;
import com.xpos.common.entity.Order.Status;
import com.xpos.common.entity.Order.Type;
import com.xpos.common.entity.pos.POSOperationLog;
import com.xpos.common.service.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Delete;
import org.restlet.resource.Post;
import org.restlet.resource.Put;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class OrderItemTransactionResource extends BaseResource{
	private Logger logger = LoggerFactory.getLogger(OrderItemTransactionResource.class);
	private BigDecimal zeroBigDecimal = new BigDecimal(0);

	@Autowired
	private TerminalService terminalService;

	@Autowired
	private OperatorService operatorService;
	
	@Autowired
	private ShopService shopService;

	@Autowired
	private OrderService orderService;

	@Autowired
	private OperatorShiftService  operatorShiftService;
	
	@Autowired
	private XShopService  xShopService   ;
	
	/**
	 * /orderItem/purchaseOrder
	 */
	@Post("json")
	public Representation purchaseOrder(JsonRepresentation entity){
		if(entity == null){
			return new JsonRepresentation(new ValidateError("-2","post请求body的json参数不能为空"));
		}
		JsonRepresentation re = null;
		try {
			String jsonStr = entity.getText();
			JSONObject obj = new JSONObject(jsonStr);
			
			Long shopId = obj.optLong("mid"); //商户号(shopId)
			String deviceNumber = obj.optString("deviceNumber"); //设备号
			String consumeType = obj.optString("consumeType"); //消费类型，如消费后付款
			Long operatorId = obj.optLong("operatorId"); //操作员ID
			String orderNumber = obj.optString("orderNumber");//订单号
			Order order = new ObjectMapper().readValue(obj.optString("order"), Order.class);
			
			//validate terminal with mid/deviceNumber
			Terminal terminal = terminalService.findTerminalByDevice(deviceNumber);
			if(terminal == null){
				return new JsonRepresentation(new ValidateError("4901","设备未注册"));
			}else if(terminal.getShop() == null){
				return new JsonRepresentation(new ValidateError("4903","指定商户不存在/已删除"));
			}else if(!terminal.getShop().getId().equals(shopId)){
				return new JsonRepresentation(new ValidateError("4902","商户与设备不匹配"));
			}
			
			//validate consumeType
			ShopInfo shopInfo = shopService.findShopInfoByShopId(shopId);
			if(shopInfo == null || shopInfo.getConsumeType() == null || !StringUtils.equalsIgnoreCase(shopInfo.getConsumeType().toString(), consumeType)){
				return new JsonRepresentation(new ValidateError("4907","商户消费类型不匹配，请重新登录"));
			}
			
			//validate order
			Operator operator = operatorService.findById(operatorId);
			if(CollectionUtils.isEmpty(order.getItems())){
				return new JsonRepresentation(new ValidateError("4904","订单未包含任何商品"));
			}else if(operator == null || operator.getShop() == null ||!operator.getShop().getId().equals(shopId)){ //validate operator
				return new JsonRepresentation(new ValidateError("4905","操作员账号异常，请重新登录"));
			}else if(order.getTotalAmount().compareTo(zeroBigDecimal) < 0){
				return new JsonRepresentation(new ValidateError("4906","订单总金额异常"));
			}
			
			Order _order = new Order(); //建新对象，防止json提交过程中加入其它参数
			Shop shopPO = xShopService.findShopByShopId(shopId);
			if (shopPO.getEnableShift()) {
				POSOperationLog operatorLog = operatorShiftService.getLastOperatorSession(deviceNumber, operatorId);
				_order.setOperatorSessionCode(operatorLog.getOperatorSessionCode());
			}
			_order.setOrderNumber(orderNumber);
			_order.setBusiness(terminal.getShop());
			_order.setTotalAmount(order.getTotalAmount());
			_order.setActuallyPaid(order.getTotalAmount()); //下单时还未使用优惠，默认实收等于应付
			_order.setOperator(operator);
			_order.setMember(order.getMember());
			_order.setItems(order.getItems());
			_order.setDeviceNumber(deviceNumber);
			
			List<Item> failList = new ArrayList<Item>();
			String message = null;
			try{
                message = orderService.createOrderAndDeductItemInventory(_order, failList);
			}catch(RuntimeException e){
                JSONObject json = new JSONObject();
                json.put("res", "-1");
                json.put("description", e.getMessage());
                return new JsonRepresentation(json);
			}
			
			if(StringUtils.isBlank(message)){
				JSONObject json = new JSONObject();
				json.put("res", "0");
				json.put("orderNumber", _order.getOrderNumber());
				return new JsonRepresentation(json);
			}else{
				JSONObject json = new JSONObject();
				json.put("res", "-1");
				json.put("description", message);
				JSONArray failItems = new JSONArray();
				for(Item item : failList){
					JSONObject failItem = new JSONObject();
					failItem.put("name", item.getName());
					failItem.put("marketable", item.isMarketable());
					failItem.put("deleted", item.getDeleted());
					failItem.put("inventory", item.getItemInventory().getInventory());
					failItems.put(failItem);
				}
				json.put("failItems", failItems);
				return new JsonRepresentation(json);
			}
			
		} catch (JsonParseException|JsonMappingException|JSONException e) {
			logger.error("点单确定后创建收银订单，解析JSON格式失败！！", e);
			JSONObject obj = new JSONObject();
			obj.put("res", "-3");
			obj.put("description", "订单解析失败，请返回重新下单");
			re = new JsonRepresentation(obj);
		} catch (Exception e){
			logger.error("点单确定后创建收银订单，锁定库存失败！！", e);
			JSONObject obj = new JSONObject();
			obj.put("res", "-1");
			obj.put("description", "库存锁定失败，请返回重新下单");
			re = new JsonRepresentation(obj);
		}
		return re;
	}
	
	/**
	 * /orderItem/{mid}/discardOrder?deviceNumber=xxxx&serial=xxxx
	 */
	@Delete("json")
	public Representation cancelOrder(){
		Long mid = NumberUtils.toLong((String) getRequestAttributes().get("mid"));
		String deviceNumber = getQuery().getFirstValue("deviceNumber");
		String serial = getQuery().getFirstValue("serial");
		
		if(StringUtils.isBlank(deviceNumber)){
			return new JsonRepresentation(new ValidateError("5001","deviceNumber不能为空"));
		}else if(mid <= 0){
			return new JsonRepresentation(new ValidateError("5002","商户Id不能为空"));
		}else if(StringUtils.isBlank(serial)){
			return new JsonRepresentation(new ValidateError("5006", "订单流水号不能为空"));
		}
		
		//take care of device_number here...
		Terminal terminal = terminalService.findTerminalByDevice(deviceNumber);
		if(terminal == null){
			return new JsonRepresentation(new ValidateError("5003","设备未注册"));
		}else if(terminal.getShop() == null){
			return new JsonRepresentation(new ValidateError("5004","指定商户不存在/已删除"));
		}else if(!terminal.getShop().getId().equals(Long.valueOf(mid))){
			return new JsonRepresentation(new ValidateError("5005","商户与设备不匹配"));
		}
		
		JsonRepresentation re = null;
		try{
			Order order = orderService.findByOrderNumber(serial);
			if(order == null || !order.getBusinessId().equals(mid)){
				return new JsonRepresentation(new ValidateError("5007", "未找到指定订单"));
			}else if(!Status.UNPAID.equals(order.getStatus())){ //只有未付款状态才能执行撤销订单
				return new JsonRepresentation(new ValidateError("5008", "订单状态已更改，无法撤销订单"));
			}
			
			boolean result = false;
			try{
				result = orderService.discardOrderAndReturnItemInventory(order.getOrderNumber(), Status.CANCEL);
			}catch(RuntimeException e){
				result = false;
			}
			
			if(result){
				re = new JsonRepresentation(ResCode.General.OK);
			}else{
				JSONObject json = new JSONObject();
				json.put("res", "-1");
				json.put("description", "订单撤销失败");
				re = new JsonRepresentation(json);
			}
		}catch(Exception e){
			logger.error("Cannot find order detail due to "+ e.getMessage(), e);
			JSONObject json = new JSONObject();
			json.put("res", -1);
			json.put("description", "订单撤销失败");
			re = new JsonRepresentation(json);
		}
		return re;
	}
	
	/**
	 * 专门用于现金支付订单的退货
	 * /orderItem/{mid}/refundCashOrder?deviceNumber=xxxx&serial=xxxx
	 */
	@Put("json")
	public Representation refundCashPaidOrder(){
		Long mid = NumberUtils.toLong((String) getRequestAttributes().get("mid"));
		String deviceNumber = getQuery().getFirstValue("deviceNumber");
		String serial = getQuery().getFirstValue("serial");
		
		if(StringUtils.isBlank(deviceNumber)){
			return new JsonRepresentation(new ValidateError("5101","deviceNumber不能为空"));
		}else if(mid <= 0){
			return new JsonRepresentation(new ValidateError("5102","商户Id不能为空"));
		}else if(StringUtils.isBlank(serial)){
			return new JsonRepresentation(new ValidateError("5106", "订单流水号不能为空"));
		}
		
		//take care of device_number here...
		Terminal terminal = terminalService.findTerminalByDevice(deviceNumber);
		if(terminal == null){
			return new JsonRepresentation(new ValidateError("5103","设备未注册"));
		}else if(terminal.getShop() == null){
			return new JsonRepresentation(new ValidateError("5104","指定商户不存在/已删除"));
		}else if(!terminal.getShop().getId().equals(Long.valueOf(mid))){
			return new JsonRepresentation(new ValidateError("5105","商户与设备不匹配"));
		}
		
		JsonRepresentation re = null;
		try{
			Order order = orderService.findByOrderNumber(serial);
			if(order == null || !order.getBusinessId().equals(mid)){
				return new JsonRepresentation(new ValidateError("5107", "未找到指定订单"));
			}else if(!Status.SUCCESS.equals(order.getStatus())){ //只有付款成功状态才能执行退货
				return new JsonRepresentation(new ValidateError("5108", "订单状态已更改，无法撤销订单"));
			}else if(!Type.CASH.equals(order.getType())){
				return new JsonRepresentation(new ValidateError("-2", "接口调用错误"));
			}
			
			boolean result = false;
			try{
				result = orderService.discardOrderAndReturnItemInventory(order.getOrderNumber(), Status.REFUND);
			}catch(RuntimeException e){
				result = false;
			}
			
			if(result){
				re = new JsonRepresentation(ResCode.General.OK);
			}else{
				JSONObject json = new JSONObject();
				json.put("res", "-1");
				json.put("description", "订单退货失败");
				re = new JsonRepresentation(json);
			}
		}catch(Exception e){
			logger.error("Cannot find order detail due to "+ e.getMessage(), e);
			JSONObject json = new JSONObject();
			json.put("res", -1);
			json.put("description", "订单退货失败");
			re = new JsonRepresentation(json);
		}
		return re;
	}
	
}
