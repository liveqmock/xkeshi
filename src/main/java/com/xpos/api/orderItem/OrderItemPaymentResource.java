package com.xpos.api.orderItem;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Post;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xpos.api.BaseResource;
import com.xpos.api.result.ValidateError;
import com.xpos.common.entity.Item;
import com.xpos.common.entity.Order;
import com.xpos.common.entity.Order.Status;
import com.xpos.common.entity.Order.Type;
import com.xpos.common.entity.OrderItem;
import com.xpos.common.entity.Terminal;
import com.xpos.common.service.OrderService;
import com.xpos.common.service.TerminalService;

public class OrderItemPaymentResource extends BaseResource{
	private Logger logger = LoggerFactory.getLogger(OrderItemPaymentResource.class);

	@Autowired
	private TerminalService terminalService;

	@Autowired
	private OrderService orderService;
	
	/**
	 * /orderItem/updatePayment
	 */
	@Post("json")
	public Representation updatePayment(JsonRepresentation entity){
		if(entity == null){
			return new JsonRepresentation(new ValidateError("-2","post请求body的json参数不能为空"));
		}
		JsonRepresentation re = null;
		try {
			String jsonStr = entity.getText();
			JSONObject obj = new JSONObject(jsonStr);
			
			Long shopId = obj.optLong("mid"); //商户号(shopId)
			String deviceNumber = obj.optString("deviceNumber"); //设备号
			Order order = new ObjectMapper().readValue(obj.optString("order"), Order.class);
			
			if(StringUtils.isBlank(deviceNumber)){
				return new JsonRepresentation(new ValidateError("5301","deviceNumber不能为空"));
			}else if(shopId <= 0){
				return new JsonRepresentation(new ValidateError("5302","商户Id不能为空"));
			}else if(StringUtils.isBlank(order.getOrderNumber())){
				return new JsonRepresentation(new ValidateError("5306", "订单流水号不能为空"));
			}
			
			//take care of device_number here...
			Terminal terminal = terminalService.findTerminalByDevice(deviceNumber);
			if(terminal == null){
				return new JsonRepresentation(new ValidateError("5303","设备未注册"));
			}else if(terminal.getShop() == null){
				return new JsonRepresentation(new ValidateError("5304","指定商户不存在/已删除"));
			}else if(!terminal.getShop().getId().equals(Long.valueOf(shopId))){
				return new JsonRepresentation(new ValidateError("5305","商户与设备不匹配"));
			}
			
			
			Order current_order = orderService.findByOrderNumber(order.getOrderNumber());
			if(current_order == null || !current_order.getBusinessId().equals(shopId)){
				return new JsonRepresentation(new ValidateError("5307", "未找到指定订单"));
			}else if(!(Status.UNPAID.equals(current_order.getStatus())//未付款、取消、支付失败、超时4种类型订单才能更新支付状态
					|| Status.CANCEL.equals(current_order.getStatus())
					|| Status.FAILED.equals(current_order.getStatus())
					|| Status.TIMEOUT.equals(current_order.getStatus()))) {
				return new JsonRepresentation(new ValidateError("5308", "当前订单状态不符"));
			}
			
			if(!(Status.SUCCESS.equals(order.getStatus()) || Status.FAILED.equals(order.getStatus()))) {
				return new JsonRepresentation(new ValidateError("5309", "更新订单状态不符"));
			}else if(order.getActuallyPaid() == null 
					|| order.getActuallyPaid().compareTo(new BigDecimal(0))<0//实付金额小于0
					|| order.getActuallyPaid().compareTo(current_order.getTotalAmount())>0) {//实付金额大于应收金额
				return new JsonRepresentation(new ValidateError("5310", "实付金额数目有误"));
			}else if(order.getType()==null) {
				return new JsonRepresentation(new ValidateError("5311", "支付方式为空"));
			}else if(order.getDiscount() == null 
					|| order.getDiscount().compareTo(new BigDecimal(0))<0
					|| order.getDiscount().compareTo(new BigDecimal(10))>0) {
				return new JsonRepresentation(new ValidateError("5312", "订单折扣有误"));
			}else if((Type.BANKCARD.equals(order.getType()) || Type.ALIPAY_QRCODE.equals(order.getType())) && order.getPosTransaction() == null) {
				return new JsonRepresentation(new ValidateError("5313", "支付信息不完整"));
			}else if(Status.SUCCESS.equals(order.getStatus()) && order.getTradeDate() == null) {
				return new JsonRepresentation(new ValidateError("5314", "交易时间不能为空"));
			}
			order.setId(current_order.getId());	
			JSONObject json = new JSONObject();
			if(orderService.updateOrder(order)) {
				json.put("res", "0");
			}else {
				json.put("res", "-1");
				json.put("description", "支付信息更新失败");
			}
			return new JsonRepresentation(json);
		} catch (JsonParseException|JsonMappingException|JSONException e) {
			logger.error("支付信息更新失败，解析JSON格式失败！！", e);
			JSONObject obj = new JSONObject();
			obj.put("res", "-3");
			obj.put("description", "参数解析失败");
			re = new JsonRepresentation(obj);
		} catch (Exception e){
			logger.error("支付信息更新失败！！", e);
			JSONObject obj = new JSONObject();
			obj.put("res", "-1");
			obj.put("description", "支付信息更新失败");
			re = new JsonRepresentation(obj);
		}
		return re;
	}

}
