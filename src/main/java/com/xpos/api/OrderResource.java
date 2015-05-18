package com.xpos.api;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.json.JSONObject;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Post;
import org.restlet.resource.Put;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.alibaba.fastjson.JSON;
import com.drongam.hermes.entity.SMS;
import com.xpos.api.param.Order;
import com.xpos.api.result.ResCode;
import com.xpos.api.result.ValidateError;
import com.xpos.common.entity.Configuration;
import com.xpos.common.entity.Shop;
import com.xpos.common.entity.ShopInfo;
import com.xpos.common.entity.Terminal;
import com.xpos.common.entity.face.Business.BusinessType;
import com.xpos.common.entity.pos.POSGatewayAccount;
import com.xpos.common.entity.pos.POSGatewayAccount.POSGatewayAccountType;
import com.xpos.common.entity.pos.POSTransaction;
import com.xpos.common.entity.pos.POSTransaction.POSTransactionStatus;
import com.xpos.common.entity.pos.POSTransaction.POSTransactionType;
import com.xpos.common.service.ConfigurationService;
import com.xpos.common.service.POSTransactionService;
import com.xpos.common.service.SMSService;
import com.xpos.common.service.ShopService;
import com.xpos.common.service.TerminalService;
import com.xpos.common.utils.BeanUtil;
import com.xpos.common.utils.SignEncodeUtil;
import com.xpos.common.utils.UUIDUtil;

public class OrderResource extends CacheableResource {
	private Logger logger = LoggerFactory.getLogger(OrderResource.class);
	private final String MOBILE_NUM_REGEX = "MOBILE_NUMBER_REGEX";
	
	@Autowired
	private TerminalService terminalService;
	
	@Autowired
	private POSTransactionService transactionService;
	
	@Value("#{settings['umpay.keyFilePath']}")
	private String keyPath;
	
	@Autowired
	private SMSService smsService;
	
//	@Autowired
//	private ShortUrlService shortUrlService;

	@Autowired
	private ShopService shopService;
	
	@Autowired
	private ConfigurationService confService;
	
	/**
	 * 请求添加订单
	 * @param entity
	 * @return
	 */
	@Post("json")
	public Representation addOrder(JsonRepresentation entity){
		if(entity == null){
			return new JsonRepresentation(new ValidateError("-2","post请求body的json参数不能为空"));
		}
		JsonRepresentation re = null;
		try {
			String jsonStr = entity.getText();
			Order order = JSON.parseObject(jsonStr, Order.class);
			String validation = BeanUtil.validate(order);
			if(validation != null){
				re = new JsonRepresentation(new ValidateError("0301",validation));
			}else{
				
				if(isCached(order.getTicket())){
					logger.warn("Duplicated request, return result from cache. - " + getInfo());
					return new JsonRepresentation(getCachedEntity(order.getTicket()));
				}else{
					String deviceNumber = order.getDeviceNumber();
					Terminal terminal = terminalService.findTerminalByDevice(deviceNumber);
					if(terminal == null){
						return new JsonRepresentation(new ValidateError("0306","设备未注册"));
					}else if(terminal.getShop() == null){
						return new JsonRepresentation(new ValidateError("0303","指定商户不存在/已删除"));
					}else if(!terminal.getShop().getId().equals(order.getMidLong())){
						return new JsonRepresentation(new ValidateError("0302","商户与设备不匹配"));
					}
//					else if(!order.getCsrfToken().equals(SecurityUtils.getSubject().getSession().getAttribute(CSRFTokenManager.CSRF_PARAM_NAME))){
//						return new JsonRepresentation(new ValidateError("0305","token码校验失败"));
//					}
					
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
						return new JsonRepresentation(new ValidateError("0307","支付机构商户ID不匹配"));
					}
					
					//save order to DB
					POSTransaction transaction = new POSTransaction();
					transaction.setBusiness(shop);
					transaction.setSum(BigDecimal.valueOf(0.01d).multiply(BigDecimal.valueOf(order.getSumInt())).setScale(2, RoundingMode.HALF_UP));
					transaction.setStatus(POSTransactionStatus.UNPAID);
					transaction.setType(POSTransactionType.BANK_CARD);
					transaction.setCode(UUIDUtil.getRandomString(32));
					transaction.setGatewayAccount(account.getAccount());
					transaction.setGatewayType(account.getType());
					
					
					String signature = "";
					if(account.getType().equals(POSGatewayAccountType.UMPAY)){ //联动优势需生成签名
						StringBuffer bf = new StringBuffer();
						bf.append(order.getSum()).append(order.getRegisterMid()).append(transaction.getCode());
						signature = SignEncodeUtil.umpaySignData(bf.toString(), keyPath);
					}
					transaction.setOperator(order.getOperator());
					if(StringUtils.isNotBlank(order.getPhone())){
						transaction.setMobile(order.getPhone());
					}
					if(!transactionService.savePOSTransaction(transaction)){
						return new JsonRepresentation(new ValidateError("0304","创建订单失败"));
					}else{
						re = new JsonRepresentation(ResCode.General.OK);
						JSONObject json = re.getJsonObject();
						json.put("orderId", transaction.getId());
						json.put("orderCode", transaction.getCode());
						json.put("signature", signature);
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
	
	/**
	 * 更新订单
	 * @return
	 */
	@Put("json")
	public Representation updateOrderPhone(JsonRepresentation entity){
		JsonRepresentation re = null;
		try {
			JSONObject json = entity.getJsonObject();
			String phone = json.getString("phone");
			String code = json.getString("orderId");
			
			Configuration conf = confService.findByName(MOBILE_NUM_REGEX);
			if(StringUtils.isBlank(phone)){
				re = new JsonRepresentation(new ValidateError("1901","请填写手机号"));
			}else if(!Pattern.matches(conf.getValue(), phone)){
				re = new JsonRepresentation(new ValidateError("1902", "手机号格式错误"));
			}else if(StringUtils.isEmpty(code)){
				re = new JsonRepresentation(new ValidateError("1903","订单号为空"));
			}else{
				//query transaction by code
				POSTransaction transaction = transactionService.findTransactionByCode(code);
				
				//verify transaction status
//				if(order.getStatus() != POSOrder.PAID_SUCCESS){
//					...
//				}
				
				//compare to current phone
				if(StringUtils.equals(phone, transaction.getMobile())){
					return new JsonRepresentation(ResCode.General.OK);
				}else{
					//update order by orderId here
					POSTransaction _transaction = new POSTransaction();
					_transaction.setId(transaction.getId());
					_transaction.setMobile(phone);
					if(transactionService.updatePOSTransaction(_transaction)){
						re = new JsonRepresentation(ResCode.General.OK);
						String url = "http://xka.me/ebill/"+code; //电子账单地址
						Shop shop = shopService.findShopByIdIgnoreVisible(transaction.getBusinessId());
						StringBuffer content = new StringBuffer();
						content.append("您于").append(new DateTime(transaction.getTradeDate()).toString("MM月dd日HH时mm分")).append("在 ").append(shop.getName())
						.append(" 消费人民币：").append(transaction.getSum()).append("元，查看账单详情：").append(url);
						 
						SMS sms = new SMS();
						sms.setMobile(phone);
						sms.setMessage(content.toString());
						smsService.sendSMSAndDeductions(shop.getId() ,BusinessType.SHOP,sms,null,"发送电子账单短信" );
					}else{
						re = new JsonRepresentation(new ValidateError("1904","手机号更新失败"));
					}
				}
			}
			
		} catch (Exception e) {
			logger.error("Cannot update phone of order due to "+ e.getMessage(), e);
			re = new JsonRepresentation(ResCode.General.SERVER_INTERNAL_ERROR);
		}
		return re;
	}

}
