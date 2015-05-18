package com.xpos.api;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Post;
import org.restlet.resource.Put;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.xpos.api.param.OrderPayment;
import com.xpos.api.result.ResCode;
import com.xpos.api.result.ValidateError;
import com.xpos.common.entity.Order;
import com.xpos.common.entity.Order.Status;
import com.xpos.common.entity.Shop;
import com.xpos.common.entity.ShopInfo;
import com.xpos.common.entity.Terminal;
import com.xpos.common.entity.pos.POSGatewayAccount;
import com.xpos.common.entity.pos.POSTransaction;
import com.xpos.common.entity.pos.POSGatewayAccount.POSGatewayAccountType;
import com.xpos.common.entity.pos.POSTransaction.POSTransactionStatus;
import com.xpos.common.entity.pos.POSTransaction.POSTransactionType;
import com.xpos.common.service.OrderService;
import com.xpos.common.service.POSTransactionService;
import com.xpos.common.service.ShopService;
import com.xpos.common.service.TerminalService;
import com.xpos.common.utils.BeanUtil;
import com.xpos.common.utils.UUIDUtil;

public class OrderPaymentResource extends CacheableResource{
	private Logger logger = LoggerFactory.getLogger(OrderResource.class);

	@Autowired
	private TerminalService terminalService;
	
	@Autowired
	private POSTransactionService transactionService;
	
	@Autowired
	private ShopService shopService;
	
	@Autowired
	private OrderService orderService;
	
	@Put("json")
	public Representation updateOrderStatus(JsonRepresentation entity){
		if(entity == null){
			return new JsonRepresentation(new ValidateError("-2","post请求body的json参数不能为空"));
		}
		JsonRepresentation re = null;
		try {
			String jsonStr = entity.getText();
			OrderPayment payment = JSON.parseObject(jsonStr, OrderPayment.class);
			String validation = BeanUtil.validate(payment);
			if(validation != null){
				return new JsonRepresentation(new ValidateError("1201",validation));
			}
			//take care of device_number here...
			String deviceNumber = payment.getDeviceNumber();
			Terminal terminal = terminalService.findTerminalByDevice(deviceNumber);
			
			if(terminal == null){
				return new JsonRepresentation(new ValidateError("1206","设备未注册"));
			}else if(terminal.getShop() == null){
				return new JsonRepresentation(new ValidateError("1203","指定商户不存在/已删除"));
			}else if(!terminal.getShop().getId().equals(payment.getMidLong())){
				return new JsonRepresentation(new ValidateError("1202","商户与设备不匹配"));
			}
//			else if(!payment.getCsrfToken().equals(SecurityUtils.getSubject().getSession().getAttribute(CSRFTokenManager.CSRF_PARAM_NAME))){
//				return new JsonRepresentation(new ValidateError("1207","token码校验失败"));
//			}
			
			Shop shop = terminal.getShop();
			ShopInfo shopInfo = shopService.findShopInfoByShopId(shop.getId());
			List<POSGatewayAccount> posGatewayAccounts = shopInfo.getPosAccountList();
			POSGatewayAccount account = null;
			for(POSGatewayAccount _account : posGatewayAccounts){
				if(_account.getType().getState() == payment.getType()){
					account = _account;
				}
			}
			if(account == null || !payment.getRegisterMid().equals(account.getAccount())){
				return new JsonRepresentation(new ValidateError("1204","支付机构商户ID不匹配"));
			}
			
			//query transaction by code
			POSTransaction transaction = transactionService.findTransactionByCode(payment.getSerial());
			if(transaction == null
				|| !transaction.getBusinessId().equals(payment.getMidLong())
				|| !POSGatewayAccountType.UMPAY.equals(transaction.getGatewayType())){
				return new JsonRepresentation(new ValidateError("1205","订单不存在"));
			}
				
			//update order to DB
			POSTransactionStatus status = POSTransactionStatus.queryByState(payment.getStatus());
			if(status != null){
				transaction.setStatus(status);
				if(transactionService.updatePOSTransaction(transaction)){
					if(status.equals(POSTransactionStatus.PAID_REVOCATION) || status.equals(POSTransactionStatus.PAID_REFUND)) {
						//撤销相关点单，改为CANCEL状态
						Order order = orderService.findOrderByPOSTransactionId(transaction.getId());
						if(order != null){
							orderService.discardOrderAndReturnItemInventory(order.getOrderNumber(), Status.REFUND);
						}
					}
					re = new JsonRepresentation(ResCode.General.OK);
				}else{
					re = new JsonRepresentation(new ValidateError("1208","订单状态更新失败"));
				}
			}else{
				re = new JsonRepresentation(new ValidateError("1208","订单状态更新失败"));
			}
			return re;
		} catch (Exception e) {
			logger.error("Cannot add order due to "+ e.getMessage(), e);
			re = new JsonRepresentation(ResCode.General.SERVER_INTERNAL_ERROR);
		}
		return re;
	}
	
	
	/**
	 * 请求添加电子现金类型订单
	 */
	@Post("json")
	public Representation addElectronicCashOrder(JsonRepresentation entity){
		if(entity == null){
			return new JsonRepresentation(new ValidateError("-2","post请求body的json参数不能为空"));
		}
		JsonRepresentation re = null;
		try {
			String jsonStr = entity.getText();
			com.xpos.api.param.Order order = JSON.parseObject(jsonStr, com.xpos.api.param.Order.class);
			String validation = BeanUtil.validate(order);
			if(validation != null){
				re = new JsonRepresentation(new ValidateError("5501",validation));
			}else{
				
				if(isCached(order.getTicket())){
					logger.warn("Duplicated request, return result from cache. - " + getInfo());
					return new JsonRepresentation(getCachedEntity(order.getTicket()));
				}else{
					String deviceNumber = order.getDeviceNumber();
					Terminal terminal = terminalService.findTerminalByDevice(deviceNumber);
					if(terminal == null){
						return new JsonRepresentation(new ValidateError("5506","设备未注册"));
					}else if(terminal.getShop() == null){
						return new JsonRepresentation(new ValidateError("5503","指定商户不存在/已删除"));
					}else if(!terminal.getShop().getId().equals(order.getMidLong())){
						return new JsonRepresentation(new ValidateError("5502","商户与设备不匹配"));
					}
					
					Shop shop = terminal.getShop();
					ShopInfo shopInfo = shopService.findShopInfoByShopId(shop.getId());
					List<POSGatewayAccount> posGatewayAccounts = shopInfo.getPosAccountList();
					POSGatewayAccount account = null;
					for(POSGatewayAccount _account : posGatewayAccounts){
						if(_account.getType().getState() == order.getType()){
							account = _account;
						}
					}
					if(account == null || !order.getRegisterMid().equals(account.getAccount())){
						return new JsonRepresentation(new ValidateError("5507","支付机构商户ID不匹配"));
					}
					
					//save order to DB
					POSTransaction transaction = new POSTransaction();
					transaction.setBusiness(shop);
					transaction.setSum(BigDecimal.valueOf(0.01d).multiply(BigDecimal.valueOf(order.getSumInt())).setScale(2, RoundingMode.HALF_UP));
					transaction.setStatus(POSTransactionStatus.UNPAID);
					transaction.setType(POSTransactionType.ELECTRONIC_CASH);
					transaction.setCode(UUIDUtil.getRandomString(32));
					transaction.setGatewayAccount(account.getAccount());
					transaction.setGatewayType(account.getType());
					transaction.setOperator(order.getOperator());
					if(StringUtils.isNotBlank(order.getPhone())){
						transaction.setMobile(order.getPhone());
					}
					if(!transactionService.savePOSTransaction(transaction)){
						return new JsonRepresentation(new ValidateError("5504","创建电子现金支付流水失败"));
					}else{
						re = new JsonRepresentation(ResCode.General.OK);
						JSONObject json = re.getJsonObject();
						json.put("orderId", transaction.getId());
						json.put("orderCode", transaction.getCode());
						//只缓存保存成功的结果
						cache(order.getTicket(),re.getText());
						return re;
					}
				}
			}
			
		} catch (Exception e) {
			logger.error("Cannot add order due to "+ e.getMessage(), e);
			re = new JsonRepresentation(ResCode.General.SERVER_INTERNAL_ERROR);
		}
		return re;
		
	}
}
