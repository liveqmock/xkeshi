package com.xpos.api.alipay;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.json.JSONObject;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.Put;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alipay.service.AlipayQRCodePaymentService;
import com.xpos.api.BaseResource;
import com.xpos.api.result.ValidateError;
import com.xpos.common.entity.Shop;
import com.xpos.common.entity.ShopInfo;
import com.xpos.common.entity.Terminal;
import com.xpos.common.entity.pos.POSGatewayAccount;
import com.xpos.common.entity.pos.POSGatewayAccount.POSGatewayAccountType;
import com.xpos.common.entity.pos.POSTransaction;
import com.xpos.common.entity.pos.POSTransaction.POSTransactionStatus;
import com.xpos.common.entity.pos.POSTransaction.POSTransactionType;
import com.xpos.common.service.POSGatewayAccountService;
import com.xpos.common.service.POSTransactionService;
import com.xpos.common.service.ShopService;
import com.xpos.common.service.TerminalService;
import com.xpos.common.utils.UUIDUtil;

/** 支付宝线下扫码支付 */
public class AlipayQRCodePaymentOrderResource extends BaseResource {
	private Logger logger = LoggerFactory.getLogger(AlipayQRCodePaymentOrderResource.class);
	
	//支付宝
	
	@Autowired
	private TerminalService terminalService;
	@Autowired
	private POSTransactionService transactionService;
	@Autowired
	private ShopService shopService;
	@Autowired
	private AlipayQRCodePaymentService alipayQRCodePaymentService;
	@Autowired
	private POSGatewayAccountService gatewayAccountService;
	
	/** 统一下单并支付 */
	@Post("json")
	public Representation createAndPayByQRCode(JsonRepresentation entity){
		if(entity == null){
			return new JsonRepresentation(new ValidateError("-2","post请求body的json参数不能为空"));
		}
		JsonRepresentation re = null;
		try {
			String jsonStr = entity.getText();
			JSONObject obj = new JSONObject(jsonStr);
			
			String deviceNumber = obj.getString("deviceNumber"); //设备号
			BigDecimal totalFee = new BigDecimal(obj.getString("totalFee")); //支付金额。单位:元
			Long shopId = obj.getLong("mid"); //商户号(shopId)
			String partner = obj.getString("registerMid"); //第三方支付平台注册账号，此处为支付宝PID
			String operator = obj.getString("operator"); //POS端操作员账号
			String dynamicId = obj.getString("dynaId");
			
			Terminal terminal = terminalService.findTerminalByDevice(deviceNumber);
			if(terminal == null){
				return new JsonRepresentation(new ValidateError("4301","设备未注册"));
			}else if(terminal.getShop() == null){
				return new JsonRepresentation(new ValidateError("4303","指定商户不存在/已删除"));
			}else if(!terminal.getShop().getId().equals(shopId)){
				return new JsonRepresentation(new ValidateError("4302","商户与设备不匹配"));
			}
			
			Shop shop = terminal.getShop();
			ShopInfo shopInfo = shopService.findShopInfoByShopId(shop.getId());
			List<POSGatewayAccount> posGatewayAccounts = shopInfo.getPosAccountList();
			POSGatewayAccount account = null;
			for(POSGatewayAccount _account : posGatewayAccounts){
				if(POSGatewayAccountType.ALIPAY.equals(_account.getType())){
					account = _account;
					break;
				}
			}
			if(account == null || !partner.equals(account.getAccount())){
				return new JsonRepresentation(new ValidateError("4304","支付机构商户ID不匹配"));
			}
			
			//创建POSTransaction订单，状态为UNPAID
			POSTransaction transaction = new POSTransaction();
			transaction.setBusiness(shop);
			transaction.setSum(totalFee);
			transaction.setStatus(POSTransactionStatus.UNPAID);
			transaction.setType(POSTransactionType.ALIPAY);
			transaction.setCode(UUIDUtil.getRandomString(32));
			transaction.setGatewayAccount(account.getAccount());
			transaction.setGatewayType(account.getType());
			transaction.setOperator(operator);
			transaction.setTerminal(deviceNumber); //支付宝没有器具，交易终端号以PAD序列号代替
			
			if(!transactionService.savePOSTransaction(transaction)){
				return new JsonRepresentation(new ValidateError("4305","系统创建订单失败"));
			}
			
			//调用支付宝接口下单&支付
			String responseText = alipayQRCodePaymentService.submitAndPay(shop, dynamicId, account.getSignKey(), transaction);
			transaction = alipayQRCodePaymentService.processSubmitAndPayCallback(responseText, account.getSignKey(), transaction);
			
			JSONObject json = new JSONObject();
			json.put("orderId", transaction.getId());
			json.put("orderCode", transaction.getCode());
			if(POSTransactionStatus.PAID_FAIL.equals(transaction.getStatus())){
				json.put("res", "4306");
				if(StringUtils.isNotBlank(transaction.getRemark())){
					json.put("description", transaction.getRemark());
				}else{
					json.put("description", "支付宝返回付款失败");
				}
				return new JsonRepresentation(json);
			}else if(POSTransactionStatus.PAID_SUCCESS.equals(transaction.getStatus())){
				json.put("res", "0");
				json.put("description", "付款成功");
				return new JsonRepresentation(json);
			}else if(POSTransactionStatus.UNPAID.equals(transaction.getStatus())){
				json.put("res", "4307");
				json.put("description", "订单创建成功，等待支付");
				return new JsonRepresentation(json);
			}
			
			return re;
		} catch (Exception e) {
			logger.error("创建支付宝线下扫码支付订单失败！！", e);
			JSONObject obj = new JSONObject();
			obj.put("res", "-1");
			obj.put("description", e.getMessage());
			re = new JsonRepresentation(obj);
		}
		return re;
		
	}
	
	/** 支付结果查询 */
	@Get
	public Representation queryQRCodeResult(){
		Long mid = NumberUtils.toLong(getQueryValue("mid"));
		String deviceNumber = getQueryValue("deviceNumber");
		String orderCode = getQueryValue("orderCode");
		
		if(StringUtils.isBlank(orderCode)){
			return new JsonRepresentation(new ValidateError("4404", "订单号不能为空"));
		}else if(StringUtils.isBlank(deviceNumber)){
			return new JsonRepresentation(new ValidateError("4405", "deviceNumber不能为空"));
		}
		
		//take care of device_number here...
		Terminal terminal = terminalService.findTerminalByDevice(deviceNumber);
		if(terminal == null){
			return new JsonRepresentation(new ValidateError("4401","设备未注册"));
		}else if(terminal.getShop() == null){
			return new JsonRepresentation(new ValidateError("4403","指定商户不存在/已删除"));
		}else if(!terminal.getShop().getId().equals(mid)){
			return new JsonRepresentation(new ValidateError("4402","商户与设备不匹配"));
		}
		
		
		JsonRepresentation re = null;
		try {
			POSTransaction transaction = transactionService.findTransactionByCode(orderCode);
			if(transaction == null || !transaction.getBusinessId().equals(mid)){ //未找到订单 或者 商户号不匹配
				return new JsonRepresentation(new ValidateError("4406","订单不存在"));
			}else if(!transaction.getGatewayType().equals(POSGatewayAccountType.ALIPAY)
					|| !transaction.getType().equals(POSTransactionType.ALIPAY)) {
				return new JsonRepresentation(new ValidateError("4407","订单类型错误"));
			}
			
			String signKey = null;
			POSGatewayAccount account = gatewayAccountService.findByAccountAndType(transaction.getGatewayAccount(), transaction.getGatewayType());
			if(account == null){
				logger.error("支付宝线下扫码支付【收单查询】接口异常，gatewayAccount = [" + transaction.getGatewayAccount() + "]未找到对应的POSGatewayAccount");
				throw new Exception("商户支付宝账户异常，请与管理员联系");
			}else{
				signKey = account.getSignKey();
			}
			
			String responseText = alipayQRCodePaymentService.query(signKey, transaction);
			String message = alipayQRCodePaymentService.processQueryCallback(responseText, signKey, transaction);
			JSONObject obj = new JSONObject();
			obj.put("res", "0");
			obj.put("description", message);
			re = new JsonRepresentation(obj);
		} catch (Exception e) {
			logger.error("查询支付宝线下扫码支付交易结果失败！！", e);
			JSONObject obj = new JSONObject();
			obj.put("res", "-1");
			obj.put("description", e.getMessage());
			re = new JsonRepresentation(obj);
		}
		return re;
		
	}

	/** 撤销订单
	 * 如果订单支付成功，调用撤销接口则进行全额退款，如果订单未支付成功，调用则关闭订单
	 */
	@Put("json")
	public Representation cancelQRCodeOrder(JsonRepresentation entity){
		if(entity == null){
			return new JsonRepresentation(new ValidateError("-2","请求body的json参数不能为空"));
		}
		JsonRepresentation re = null;
		try {
			String jsonStr = entity.getText();
			JSONObject obj = new JSONObject(jsonStr);
			
			String deviceNumber = obj.getString("deviceNumber"); //设备号
			Long shopId = obj.getLong("mid"); //商户号(shopId)
			String orderCode = obj.getString("orderCode"); //订单号
			
			Terminal terminal = terminalService.findTerminalByDevice(deviceNumber);
			if(terminal == null){
				return new JsonRepresentation(new ValidateError("4501","设备未注册"));
			}else if(terminal.getShop() == null){
				return new JsonRepresentation(new ValidateError("4503","指定商户不存在/已删除"));
			}else if(!terminal.getShop().getId().equals(shopId)){
				return new JsonRepresentation(new ValidateError("4502","商户与设备不匹配"));
			}
			
			
			//查找订单
			POSTransaction transaction = transactionService.findTransactionByCode(orderCode);
			if(transaction == null){
				return new JsonRepresentation(new ValidateError("4504","未找到订单"));
			}
			/*
			else if(POSTransactionStatus.PAID_FAIL.equals(transaction.getStatus())){
				//订单已是付款失败状态
				return new JsonRepresentation(new ValidateError("4505","订单已关闭"));
			}else if(POSTransactionStatus.PAID_REVOCATION.equals(transaction.getStatus())
				|| POSTransactionStatus.PAID_REVOCATION.equals(transaction.getStatus())){
				//订单已是撤销、退款状态
				return new JsonRepresentation(new ValidateError("4506","商户与设备不匹配"));
			}
			*/
			
			String signKey = null;
			POSGatewayAccount account = gatewayAccountService.findByAccountAndType(transaction.getGatewayAccount(), transaction.getGatewayType());
			if(account == null){
				logger.error("支付宝线下扫码支付【撤销订单】接口异常，gatewayAccount = [" + transaction.getGatewayAccount() + "]未找到对应的POSGatewayAccount");
				throw new Exception("商户支付宝账户异常，请与管理员联系");
			}else{
				signKey = account.getSignKey();
			}
			
			//调用支付宝撤销接口
			String responseText = alipayQRCodePaymentService.cancel(signKey, transaction);
			String message = alipayQRCodePaymentService.processCancelCallback(responseText, signKey, transaction);
			
			JSONObject obj2 = new JSONObject();
			obj2.put("res", "0");
			obj2.put("description", message);
			re = new JsonRepresentation(obj2);
			return re;
		} catch (Exception e) {
			logger.error("撤销支付宝线下扫码支付订单失败！！", e);
			JSONObject obj = new JSONObject();
			obj.put("res", "-1");
			obj.put("description", e.getMessage());
			re = new JsonRepresentation(obj);
		}
		return re;
		
	}
	
	/** 订单退款 */
	@Delete("json")
	public Representation refundQRCodeOrder(JsonRepresentation entity){
		if(entity == null){
			return new JsonRepresentation(new ValidateError("-2","请求body的json参数不能为空"));
		}
		JsonRepresentation re = null;
		try {
			String jsonStr = entity.getText();
			JSONObject obj = new JSONObject(jsonStr);
			
			String deviceNumber = obj.getString("deviceNumber"); //设备号
			Long shopId = obj.getLong("mid"); //商户号(shopId)
			String orderCode = obj.getString("orderCode"); //订单号
			
			Terminal terminal = terminalService.findTerminalByDevice(deviceNumber);
			if(terminal == null){
				return new JsonRepresentation(new ValidateError("4601","设备未注册"));
			}else if(terminal.getShop() == null){
				return new JsonRepresentation(new ValidateError("4603","指定商户不存在/已删除"));
			}else if(!terminal.getShop().getId().equals(shopId)){
				return new JsonRepresentation(new ValidateError("4602","商户与设备不匹配"));
			}
			
			
			//查找订单
			POSTransaction transaction = transactionService.findTransactionByCode(orderCode);
			if(transaction == null){
				return new JsonRepresentation(new ValidateError("4604","未找到订单"));
			}
			/*
			else if(POSTransactionStatus.PAID_FAIL.equals(transaction.getStatus())){
				//订单已是付款失败状态
				return new JsonRepresentation(new ValidateError("4605","订单已关闭"));
			}else if(POSTransactionStatus.PAID_REFUND.equals(transaction.getStatus())
			|| POSTransactionStatus.PAID_REVOCATION.equals(transaction.getStatus())){
				//订单已是撤销、退款状态
				return new JsonRepresentation(new ValidateError("4606","商户与设备不匹配"));
			}
			 */
			
			String signKey = null;
			POSGatewayAccount account = gatewayAccountService.findByAccountAndType(transaction.getGatewayAccount(), transaction.getGatewayType());
			if(account == null){
				logger.error("支付宝线下扫码支付【收单退款】接口异常，gatewayAccount = [" + transaction.getGatewayAccount() + "]未找到对应的POSGatewayAccount");
				throw new Exception("商户支付宝账户异常，请与管理员联系");
			}else{
				signKey = account.getSignKey();
			}
			
			//调用支付宝撤销接口
			String responseText = alipayQRCodePaymentService.refund(signKey, transaction);
			String message = alipayQRCodePaymentService.processRefundCallback(responseText, signKey, transaction);
			
			JSONObject obj2 = new JSONObject();
			obj2.put("res", "0");
			obj2.put("description", message);
			re = new JsonRepresentation(obj2);
			return re;
		} catch (Exception e) {
			logger.error("支付宝线下扫码支付订单退款失败！！", e);
			JSONObject obj = new JSONObject();
			obj.put("res", "-1");
			obj.put("description", e.getMessage());
			re = new JsonRepresentation(obj);
		}
		return re;
		
	}

}
