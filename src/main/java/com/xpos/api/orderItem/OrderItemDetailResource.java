package com.xpos.api.orderItem;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONObject;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.xpos.api.BaseResource;
import com.xpos.api.result.ResCode;
import com.xpos.api.result.ValidateError;
import com.xpos.common.entity.Order;
import com.xpos.common.entity.OrderItem;
import com.xpos.common.service.OrderService;

public class OrderItemDetailResource extends BaseResource{
	private Logger logger = LoggerFactory.getLogger(OrderItemDetailResource.class);

	@Autowired
	private OrderService orderService;
	
	/**
	 * /orderItem/{mid}/detail?serial=xxx&status=SUCCESS
	 */
	@Get
	public Representation orderItemDetail(){
		Long mid = NumberUtils.toLong((String) getRequestAttributes().get("mid"));
		String serial = getQuery().getFirstValue("serial");
		String status = getQuery().getFirstValue("status");
		
		if(StringUtils.isBlank(serial)){
			return new JsonRepresentation(new ValidateError("4701", "订单流水号不能为空"));
		}
		
		
		JsonRepresentation re = null;
		try{
			Order order = orderService.findByOrderNumber(serial);
			if(order == null || !order.getBusinessId().equals(mid)){
				return new JsonRepresentation(new ValidateError("4702", "未找到指定订单"));
			}else if(StringUtils.isNotBlank(status) && !StringUtils.equalsIgnoreCase(status, order.getStatus().toString())){
				return new JsonRepresentation(new ValidateError("4703", "未找到指定状态的订单"));
			}
			re = new JsonRepresentation(ResCode.General.OK);
			JSONObject json = re.getJsonObject();
			json.put("serial", serial);
			json.put("amount", order.getTotalAmount() != null ? order.getTotalAmount() : 0);
			json.put("paid", order.getActuallyPaid() != null ? order.getActuallyPaid() : 0);
			json.put("type", order.getType() != null ? order.getType().getDesc() : "");
			json.put("status", order.getStatus() != null ? order.getStatus().toString() : "");
			json.put("operatorAccount", order.getOperator() != null ? order.getOperator().getUsername() : "");
			json.put("operatorName", order.getOperator() != null ? order.getOperator().getRealName() : "");
			if(!CollectionUtils.isEmpty(order.getItems())){
				JSONArray array = new JSONArray();
				for(OrderItem item : order.getItems()){
					JSONObject jsonObj = new JSONObject();
					jsonObj.put("id", item.getId());
					jsonObj.put("name", item.getItemName());
					jsonObj.put("price", item.getPrice() != null ? item.getPrice() : "");
					jsonObj.put("quantity", item.getQuantity() != null ? item.getQuantity() : "");
					jsonObj.put("createDate", new DateTime(item.getCreateDate()).toString("yyyy-MM-dd HH:mm:ss"));
					array.put(jsonObj);
				}
				json.put("list", array);
			}
		}catch(Exception e){
			logger.error("Cannot find order detail due to "+ e.getMessage(), e);
			re = new JsonRepresentation(ResCode.General.SERVER_INTERNAL_ERROR);
		}
		return re;
	}
	
}
