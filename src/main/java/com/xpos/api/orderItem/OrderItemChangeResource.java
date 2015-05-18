package com.xpos.api.orderItem;

import java.io.IOException;
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
import com.xpos.common.entity.OrderItem;
import com.xpos.common.entity.Terminal;
import com.xpos.common.service.OrderService;
import com.xpos.common.service.TerminalService;

public class OrderItemChangeResource extends BaseResource{
	private Logger logger = LoggerFactory.getLogger(OrderItemChangeResource.class);

	@Autowired
	private TerminalService terminalService;

	@Autowired
	private OrderService orderService;
	
	/**
	 * /orderItem/changeItem
	 */
	@Post("json")
	public Representation changeItem(JsonRepresentation entity){
		if(entity == null){
			return new JsonRepresentation(new ValidateError("-2","post请求body的json参数不能为空"));
		}
		JsonRepresentation re = null;
		try {
			String jsonStr = entity.getText();
			JSONObject obj = new JSONObject(jsonStr);
			
			Long shopId = obj.optLong("mid"); //商户号(shopId)
			String deviceNumber = obj.optString("deviceNumber"); //设备号
			String serial = obj.optString("serial");//订单号
			
			if(StringUtils.isBlank(deviceNumber)){
				return new JsonRepresentation(new ValidateError("5201","deviceNumber不能为空"));
			}else if(shopId <= 0){
				return new JsonRepresentation(new ValidateError("5202","商户Id不能为空"));
			}else if(StringUtils.isBlank(serial)){
				return new JsonRepresentation(new ValidateError("5206", "订单流水号不能为空"));
			}
			
			//take care of device_number here...
			Terminal terminal = terminalService.findTerminalByDevice(deviceNumber);
			if(terminal == null){
				return new JsonRepresentation(new ValidateError("5203","设备未注册"));
			}else if(terminal.getShop() == null){
				return new JsonRepresentation(new ValidateError("5204","指定商户不存在/已删除"));
			}else if(!terminal.getShop().getId().equals(Long.valueOf(shopId))){
				return new JsonRepresentation(new ValidateError("5205","商户与设备不匹配"));
			}
			
			
			Order order = orderService.findByOrderNumber(serial);
			if(order == null || !order.getBusinessId().equals(shopId)){
				return new JsonRepresentation(new ValidateError("5207", "未找到指定订单"));
			}else if(!Status.UNPAID.equals(order.getStatus())) {
				return new JsonRepresentation(new ValidateError("5208", "只有未付款的订单才能更新商品"));
			}
			
			List<Item> failList = new ArrayList<Item>();
			boolean result = true;
			try{
				List<OrderItem> addList = parseOrderItemArray(obj, "new");//追加点单商品
				if(addList.size()>0) {
					result = result && orderService.addOrderItem(addList,failList,order);
				}
				List<OrderItem> increaseList = parseOrderItemArray(obj, "increase");//更新已点商品
				if(increaseList.size()>0) {
					result = result && orderService.increaseOrderItem(increaseList,failList,order);
				}
				List<OrderItem> decreaseList = parseOrderItemArray(obj, "decrease");//取消已点商品
				if(decreaseList.size()>0) {
					order = orderService.findByOrderNumber(serial);
					result = result && orderService.decreaseOrderItem(decreaseList,failList,order);
				}
			}catch(RuntimeException e){
				result = false;
			}
			
			if(result){
				JSONObject json = new JSONObject();
				json.put("res", "0");
				return new JsonRepresentation(json);
			}else{
				JSONObject json = new JSONObject();
				json.put("res", "-1");
				json.put("description", "库存锁定失败，请返回重新下单");
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
			logger.error("商品点单修改失败，解析JSON格式失败！！", e);
			JSONObject obj = new JSONObject();
			obj.put("res", "-3");
			obj.put("description", "商品点单修改失败，请返回重新操作");
			re = new JsonRepresentation(obj);
		} catch (Exception e){
			logger.error("商品点单修改，库存更新失败！！", e);
			JSONObject obj = new JSONObject();
			obj.put("res", "-1");
			obj.put("description", "库存更新失败，请返回重新操作");
			re = new JsonRepresentation(obj);
		}
		return re;
	}

	private List<OrderItem> parseOrderItemArray(JSONObject obj, String key) throws IOException {
		List<OrderItem> list = new ArrayList<>();
		if(obj.has(key) && obj.getJSONArray(key)!=null ) {
			JSONArray jsonArray = obj.getJSONArray(key);
			if(jsonArray.length() > 0){
				for(int i = 0; i < jsonArray.length(); i++){
					JSONObject jsonObj = jsonArray.getJSONObject(i);
					try {
						OrderItem orderItem = new ObjectMapper().readValue(jsonObj.toString(), OrderItem.class);
						list.add(orderItem);
					} catch (IOException e) {
						throw e;
					}
				}
			}
		}
		return list;
	}
	

	
}
