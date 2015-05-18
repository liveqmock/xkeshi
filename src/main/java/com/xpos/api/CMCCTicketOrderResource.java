package com.xpos.api;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.Put;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.xpos.api.param.Order;
import com.xpos.api.param.OrderPayment;
import com.xpos.api.result.ResCode;
import com.xpos.api.result.ValidateError;
import com.xpos.common.entity.Shop;
import com.xpos.common.entity.ShopInfo;
import com.xpos.common.entity.Terminal;
import com.xpos.common.entity.pos.POSGatewayAccount;
import com.xpos.common.entity.pos.POSGatewayAccount.POSGatewayAccountType;
import com.xpos.common.entity.pos.POSTransaction;
import com.xpos.common.entity.pos.POSTransaction.POSTransactionStatus;
import com.xpos.common.entity.pos.POSTransaction.POSTransactionType;
import com.xpos.common.service.ExternalHttpInvokeService;
import com.xpos.common.service.POSTransactionService;
import com.xpos.common.service.ShopService;
import com.xpos.common.service.TerminalService;
import com.xpos.common.utils.BeanUtil;
import com.xpos.common.utils.TokenUtil;
import com.xpos.common.utils.UUIDUtil;

public class CMCCTicketOrderResource extends CacheableResource {
	private Logger logger = LoggerFactory.getLogger(CMCCTicketOrderResource.class);
	
	@Autowired
	private TerminalService terminalService;
	
	@Autowired
	private POSTransactionService posTransactionService;

	@Autowired
	private ExternalHttpInvokeService externalHttpInvokeService;

	@Autowired
	private ShopService shopService;
	
	/** 创建并支付提交的电子券订单 */
	@Post("json")
	public Representation payTicketOrder(JsonRepresentation entity){
		if(entity == null){
			return new JsonRepresentation(new ValidateError("-2","post请求body的json参数不能为空"));
		}
		JsonRepresentation re = null;
		try {
			String jsonStr = entity.getText();
			Order order = JSON.parseObject(jsonStr, Order.class);
			String validation = BeanUtil.validate(order);
			if(validation != null){
				re = new JsonRepresentation(new ValidateError("2701",validation));
			}else{
				
				if(isCached(order.getTicket())){
					logger.warn("Duplicated request, return result from cache. - " + getInfo());
					return new JsonRepresentation(getCachedEntity(order.getTicket()));
				}else{
					String deviceNumber = order.getDeviceNumber();
					Terminal terminal = terminalService.findTerminalByDevice(deviceNumber);
					if(terminal == null){
						return new JsonRepresentation(new ValidateError("2706","设备未注册"));
					}else if(terminal.getShop() == null){
						return new JsonRepresentation(new ValidateError("2703","指定商户不存在/已删除"));
					}else if(!terminal.getShop().getId().equals(order.getMidLong())){
						return new JsonRepresentation(new ValidateError("2702","商户与设备不匹配"));
					}
//					else if(!order.getCsrfToken().equals(SecurityUtils.getSubject().getSession().getAttribute(CSRFTokenManager.CSRF_PARAM_NAME))){
//						return new JsonRepresentation(new ValidateError("2705","token码校验失败"));
//					}
					
					
					String rawPassword = TokenUtil.decrypt(order.getToken());
					POSTransaction transaction = new POSTransaction();
					transaction.setBusiness(terminal.getShop());
					transaction.setSum(BigDecimal.valueOf(0.01d).multiply(BigDecimal.valueOf(order.getSumInt())).setScale(2, RoundingMode.HALF_UP));
					transaction.setStatus(POSTransactionStatus.UNPAID);
					transaction.setType(POSTransactionType.CMCC_TICKET);
					transaction.setCode(UUIDUtil.getRandomString(20));
					transaction.setGatewayAccount(order.getRegisterMid());
					transaction.setGatewayType(POSGatewayAccountType.UMPAY); //目前只有联动优势通道支持
					transaction.setPassword(rawPassword);
					transaction.setOperator(order.getOperator());
					transaction.setMobile(order.getPhone());
					
					String errorMessage = posTransactionService.createCMCCTicketOrder(transaction, deviceNumber);
					if(StringUtils.isNotBlank(errorMessage)){
						return new JsonRepresentation(new ValidateError("2704", errorMessage));
					}else{
						re = new JsonRepresentation(ResCode.General.OK);
						//只缓存保存成功的结果
						cache(order.getTicket(),re.getText());
						return re;
					}
				}
			}
			
		} catch (Exception e) {
			logger.error("创建、支付移动电子券订单失败。", e);
			re = new JsonRepresentation(ResCode.General.SERVER_INTERNAL_ERROR);
		}
		return re;
		
	}
	
	/** 查询电子券余额 */
	@Get("json")
	public Representation getTicketBalanceByPhone(){
		JsonRepresentation re = null;
		try {
			String phone = getQuery().getFirstValue("phone");
			int balance = externalHttpInvokeService.getTicketBalanceByPhone(phone);
			if(balance == -1){
				return new JsonRepresentation(new ValidateError("2601","手机号格式错误"));
			}else if(balance == -2){
				return new JsonRepresentation(new ValidateError("2603","查询余额失败，请确认已开通电子钱包"));
			}else if(balance == -3){
				return new JsonRepresentation(new ValidateError("2604","查询余额失败，请稍后再试"));
			}else{
				Map<String, Object> map = new HashMap<>();
				map.put("res", "0");
				map.put("phone", phone);
				map.put("balance", BigDecimal.valueOf(0.01d).multiply(BigDecimal.valueOf(balance)).setScale(2, RoundingMode.HALF_UP));
				re = new JsonRepresentation(map);
			}
			return re;
		} catch (Exception e) {
			logger.error("查询用户电子券余额异常！！", e);
			re = new JsonRepresentation(ResCode.General.SERVER_INTERNAL_ERROR);
		}
		return re;
	}
	
	/** 移动电子券订单冲正（撤销） */
	@Put("json")
	public Representation cancelTicketOrder(JsonRepresentation entity){
		if(entity == null){
			return new JsonRepresentation(new ValidateError("-2","post请求body的json参数不能为空"));
		}
		JsonRepresentation re = null;
		try {
			String jsonStr = entity.getText();
			OrderPayment payment = JSON.parseObject(jsonStr, OrderPayment.class);
			String validation = BeanUtil.validate(payment);
			if(validation != null){
				return new JsonRepresentation(new ValidateError("2801",validation));
			}
			
			String deviceNumber = payment.getDeviceNumber();
			Terminal terminal = terminalService.findTerminalByDevice(deviceNumber);
			if(terminal == null){
				return new JsonRepresentation(new ValidateError("2806","设备未注册"));
			}else if(terminal.getShop() == null){
				return new JsonRepresentation(new ValidateError("2803","指定商户不存在/已删除"));
			}else if(!terminal.getShop().getId().equals(payment.getMidLong())){
				return new JsonRepresentation(new ValidateError("2802","商户与设备不匹配"));
			}
//			else if(!payment.getCsrfToken().equals(SecurityUtils.getSubject().getSession().getAttribute(CSRFTokenManager.CSRF_PARAM_NAME))){
//				return new JsonRepresentation(new ValidateError("2807","token码校验失败"));
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
				return new JsonRepresentation(new ValidateError("2804","支付机构商户ID不匹配"));
			}
			
			//query pos transaction by orderId
			POSTransaction transaction = posTransactionService.findTransactionByCode(payment.getSerial());
			if(transaction == null || transaction.getStatus() != POSTransactionStatus.PAID_SUCCESS){
				return new JsonRepresentation(new ValidateError("2805","订单不存在"));
			}
			
			String errorMessage = posTransactionService.revocationCMCCTicketOrder(transaction);
			if(StringUtils.isNotBlank(errorMessage)){
				return new JsonRepresentation(new ValidateError("2808", errorMessage));
			}else{
				re = new JsonRepresentation(ResCode.General.OK);
				return re;
			}
		} catch (Exception e) {
			logger.error("移动电子券订单冲正（撤销）失败。", e);
			re = new JsonRepresentation(ResCode.General.SERVER_INTERNAL_ERROR);
		}
		return re;
		
	}
}
