package com.xpos.api;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONObject;
import org.restlet.data.Form;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.Put;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.drongam.hermes.entity.SMS;
import com.xpos.api.result.ResCode;
import com.xpos.api.result.ValidateError;
import com.xpos.common.entity.Order;
import com.xpos.common.entity.Order.Status;
import com.xpos.common.entity.Shop;
import com.xpos.common.entity.Terminal;
import com.xpos.common.entity.face.Business.BusinessType;
import com.xpos.common.entity.pos.POSGatewayAccount.POSGatewayAccountType;
import com.xpos.common.entity.pos.POSTransaction;
import com.xpos.common.entity.pos.POSTransaction.POSTransactionStatus;
import com.xpos.common.entity.pos.POSTransaction.POSTransactionType;
import com.xpos.common.exception.SignEncException;
import com.xpos.common.service.ExternalHttpInvokeService;
import com.xpos.common.service.OrderService;
import com.xpos.common.service.POSTransactionService;
import com.xpos.common.service.SMSService;
import com.xpos.common.service.ShopService;
import com.xpos.common.service.TerminalService;
import com.xpos.common.service.UpYunServiceImpl;
import com.xpos.common.utils.ShengPayUtil;


public class ShengPayOrderResource extends CacheableResource {
	private Logger logger = LoggerFactory.getLogger(ShengPayOrderResource.class);
	
	@Autowired
	private TerminalService terminalService;
	@Autowired
	private POSTransactionService transactionService;
	@Autowired
	private ExternalHttpInvokeService externalHttpInvokeService;
	@Autowired
	private UpYunServiceImpl upYunService;
	@Autowired
	private ShopService shopService;
	@Autowired
	private SMSService smsService;
	@Autowired
	private OrderService orderService;
	
	@Value("#{settings['shengPay.publicKeyFilePath']}")
	private String shengPayPublicKeyPath;
	
	/** 盛付通交易结果 */
	@Post
	public Representation shengPayNotify(Representation entity){
		if(entity == null){
			return new StringRepresentation("FAIL");
		}
		
		try {
			Map<String, String> responseMap = new LinkedHashMap<>();
			//1.提取参数
			Form form = new Form(entity);
			responseMap.put("txnType", form.getFirstValue("txnType"));
			responseMap.put("cur", form.getFirstValue("cur"));
			responseMap.put("amt", form.getFirstValue("amt"));
			responseMap.put("merchantId", form.getFirstValue("merchantId"));
			responseMap.put("terminalId", form.getFirstValue("terminalId"));
			responseMap.put("traceNo", form.getFirstValue("traceNo"));
			responseMap.put("batchNo", form.getFirstValue("batchNo"));
			responseMap.put("orderId", form.getFirstValue("orderId"));
			responseMap.put("txnTime", form.getFirstValue("txnTime"));
			responseMap.put("txnRef", form.getFirstValue("txnRef"));
			responseMap.put("respCode", form.getFirstValue("respCode"));
			responseMap.put("merOrderId", form.getFirstValue("merOrderId"));
			responseMap.put("shortPan", form.getFirstValue("shortPan"));
			responseMap.put("extData", form.getFirstValue("extData"));
			responseMap.put("sign", form.getFirstValue("sign"));
			
			logger.info("received ShengPay notify: \n【" + StringUtils.join(responseMap.entrySet(), ",") + "】");
			
			//2.校验签名
			ShengPayUtil.verifySignature(responseMap, shengPayPublicKeyPath);
			
			//3.对比DB现有数据并更新
			boolean result = false;
			POSTransaction transaction = transactionService.findTransactionByCode(responseMap.get("merOrderId"));
			POSTransactionStatus curStatus = transaction.getStatus();
			POSTransaction _transaction = new POSTransaction();
			_transaction.setCode(transaction.getCode());
			if(StringUtils.equalsIgnoreCase(responseMap.get("respCode"), "00")){
				//对比返回的交易状态和DB的交易状态，做必要更新
				if(StringUtils.equalsIgnoreCase("PUR", responseMap.get("txnType"))
						&& (curStatus.equals(POSTransactionStatus.UNPAID) || curStatus.equals(POSTransactionStatus.PAID_FAIL))){
					//如果消费结果返回成功，且DB仍是待付款或失败，则修改DB为交易成功状态
					_transaction.setStatus(POSTransactionStatus.PAID_SUCCESS);
					_transaction.setTerminal(responseMap.get("terminalId"));
					_transaction.setTraceNo(responseMap.get("traceNo"));
					_transaction.setBatchNo(responseMap.get("batchNo"));
					_transaction.setSerial(responseMap.get("orderId"));
					DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyyMMddHHmmss");
					_transaction.setTradeDate(fmt.parseDateTime(responseMap.get("txnTime")).toDate());
					_transaction.setRefNo(responseMap.get("txnRef"));
					_transaction.setCardNumber(responseMap.get("shortPan"));
					_transaction.setResponseCode(responseMap.get("respCode"));
					result = transactionService.updatePOSTransactionByCode(_transaction);
					if(StringUtils.isNotBlank(transaction.getMobile())){
						_transaction.setMobile(transaction.getMobile());
						sendSMS(_transaction);
					}
				}else if(StringUtils.equalsIgnoreCase("VID", responseMap.get("txnType")) 
						&& (curStatus.equals(POSTransactionStatus.UNPAID) || curStatus.equals(POSTransactionStatus.PAID_FAIL) || curStatus.equals(POSTransactionStatus.PAID_SUCCESS))) {
					//如果撤销结果返回成功，且DB仍是待付款、失败、成功，则修改DB为撤销成功状态
					_transaction.setStatus(POSTransactionStatus.PAID_REVOCATION);
					result = transactionService.updatePOSTransactionByCode(_transaction);
					//撤销相关点单，改为REFUND状态
					Order order = orderService.findOrderByPOSTransactionId(transaction.getId());
					if(order != null){
						orderService.discardOrderAndReturnItemInventory(order.getOrderNumber(), Status.REFUND);
					}
				}else if(StringUtils.equalsIgnoreCase("RFD", responseMap.get("txnType")) 
						&& (curStatus.equals(POSTransactionStatus.UNPAID) || curStatus.equals(POSTransactionStatus.PAID_FAIL) || curStatus.equals(POSTransactionStatus.PAID_SUCCESS))) {
					//如果退货结果返回成功，且DB仍是待付款、失败、成功，则修改DB为撤销成功状态
					_transaction.setStatus(POSTransactionStatus.PAID_REFUND);
					result = transactionService.updatePOSTransactionByCode(_transaction);
					//撤销相关点单，改为REFUND状态
					Order order = orderService.findOrderByPOSTransactionId(transaction.getId());
					if(order != null){
						orderService.discardOrderAndReturnItemInventory(order.getOrderNumber(), Status.REFUND);
					}
				}else{
					result = true;
				}
				
			} else {
				logger.error("盛付通回调响应码返回错误，desc：【" + responseMap.get("respCodeDesc") + "】");
				if(StringUtils.equalsIgnoreCase("PUR", responseMap.get("txnType"))){ //刷卡交易返回非00类型的应答码，说明交易失败，修改DB为消费失败类型
					_transaction.setStatus(POSTransactionStatus.PAID_FAIL);
					_transaction.setTerminal(responseMap.get("terminalId"));
					_transaction.setTraceNo(responseMap.get("traceNo"));
					_transaction.setBatchNo(responseMap.get("batchNo"));
					_transaction.setSerial(responseMap.get("orderId"));
					DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyyMMddHHmmss");
					_transaction.setTradeDate(DateTime.parse(responseMap.get("txnTime"),fmt).toDate());
					_transaction.setRefNo(responseMap.get("txnRef"));
					_transaction.setCardNumber(responseMap.get("shortPan"));
					_transaction.setResponseCode(responseMap.get("respCode"));
					result = transactionService.updatePOSTransactionByCode(_transaction);
				}else{
					result = true;
				}
			}
			
			if(result){
				return new StringRepresentation("SUCCESS");
			}else{
				return new StringRepresentation("FAIL");
			}
		} catch (SignEncException e) {
			logger.error("校验签名异常！", e);
			return new StringRepresentation("FAIL");
		} catch (Exception e) {
			logger.error("盛付通回调接口异常：", e);
			return new StringRepresentation("FAIL");
		}
	}
	
	
	/** 盛付通单笔交易结果 */
	@Get("json")
	public Representation shengPayQuery(){
		String mid = (String) getRequestAttributes().get("mid");
		String deviceNumber = getQuery().getFirstValue("deviceNumber");
		String orderCode = getQuery().getFirstValue("orderCode");
		Long merchantId = NumberUtils.toLong(mid);
		String type = getQuery().getFirstValue("type");
		
		if(StringUtils.isBlank(orderCode)){
			return new JsonRepresentation(new ValidateError("3104", "订单号不能为空"));	
		}else if(StringUtils.isBlank(deviceNumber)){
			return new JsonRepresentation(new ValidateError("3105", "deviceNumber不能为空"));
		}
		
		//take care of device_number here...
		Terminal terminal = terminalService.findTerminalByDevice(deviceNumber);
		if(terminal == null){
			return new JsonRepresentation(new ValidateError("3101","设备未注册"));
		}else if(terminal.getShop() == null){
			return new JsonRepresentation(new ValidateError("3103","指定商户不存在/已删除"));
		}else if(!terminal.getShop().getId().equals(merchantId)){
			return new JsonRepresentation(new ValidateError("3102","商户与设备不匹配"));
		}
		
		JsonRepresentation re = null;
		try{
			POSTransaction transaction = transactionService.findTransactionByCode(orderCode);
			if(transaction == null || !transaction.getBusinessId().equals(merchantId)){ //未找到订单 或者 商户号不匹配
				return new JsonRepresentation(new ValidateError("3106","订单不存在"));
			}else if(!transaction.getGatewayType().equals(POSGatewayAccountType.SHENGPAY)
					|| !transaction.getType().equals(POSTransactionType.BANK_CARD)) {
				return new JsonRepresentation(new ValidateError("3107","订单类型错误"));
			}
			
			//首先把查询类型和DB的交易状态对比，如果匹配就直接返回true，否则再调用盛付通接口发起查询请求
			boolean result = false;
			String message = null;
			if(StringUtils.equalsIgnoreCase("PUR", type) && transaction.getStatus().equals(POSTransactionStatus.PAID_SUCCESS)){
				result = true;
			}else if(StringUtils.equalsIgnoreCase("VID", type) && transaction.getStatus().equals(POSTransactionStatus.PAID_REVOCATION)){
				result = true;
			}else if(StringUtils.equalsIgnoreCase("RFD", type) && transaction.getStatus().equals(POSTransactionStatus.PAID_REFUND)){
				result = true;
			}else{
				message = externalHttpInvokeService.queryShengPayOrderDetail(transaction, type);
				result = StringUtils.equalsIgnoreCase(message, "SUCCESS");
			}
			
			if(!result){
				re = new JsonRepresentation(new ValidateError("3108","查询失败，请稍后再试"));
				JSONObject json = re.getJsonObject();
				json.put("message", message);
			}else{
				re = new JsonRepresentation(ResCode.General.OK);
				JSONObject json = re.getJsonObject();
				transaction = transactionService.findTransactionByCode(orderCode);
				json.put("status", transaction.getStatus().getState());
				json.put("tracenumber", transaction.getTraceNo());
				json.put("batchnumber", transaction.getBatchNo());
				json.put("transdate", new DateTime(transaction.getTradeDate()).toString("yyyy-MM-dd HH:mm:ss"));
				json.put("refernumber", transaction.getRefNo());
				json.put("orderid", orderCode);
			}
		} catch (Exception e) {
			logger.error("Failed to query ShengPay order by code. ", e);
			re = new JsonRepresentation(ResCode.General.SERVER_INTERNAL_ERROR);
		}
		return re;
	}
	
	
	/** 盛付通订单提交后，客户签字图片回调 */
	@Put("json")
	public Representation signatureUpload(JsonRepresentation entity) throws Exception {
		if(entity ==null){
			return new JsonRepresentation(new ValidateError("-2","请求body的json参数不能为空"));
		}
		JsonRepresentation re = null;
		try {
			JSONObject jsonObj = entity.getJsonObject();
			Long shopId = jsonObj.getLong("mid"); //商户号
			String registerMid = jsonObj.getString("registerMid");
			String orderCode = jsonObj.getString("orderCode");
			String picContent = jsonObj.getString("content");
			
			//verify params
			POSTransaction transaction = transactionService.findTransactionByCode(orderCode);
			if(transaction == null 
					|| !transaction.getBusinessId().equals(shopId) 
					|| !transaction.getGatewayAccount().equals(registerMid)){
				return new JsonRepresentation(new ValidateError("3201","订单不存在"));
			}else if(StringUtils.isBlank(picContent)){
				return new JsonRepresentation(new ValidateError("3202","图片内容为空"));
			}
			
			byte[] picBinary = Base64.decodeBase64(picContent);
			boolean result = upYunService.uploadImg(StringUtils.join("/order/shengPay_signature/",transaction.getGatewayAccount(),"/",transaction.getCode(),".png"), picBinary);
			if(result){
				re = new JsonRepresentation(ResCode.General.OK);
			}else{
				logger.error("fail to save shengPay signature to upyun, order code:"+transaction.getCode());
				re = new JsonRepresentation(new ValidateError("3203","图片上传失败"));
			}
			return re;
		} catch (Exception e) {
			logger.error("Cannot save signature to upyun due to "+ e.getMessage(), e);
			return new JsonRepresentation(ResCode.General.SERVER_INTERNAL_ERROR);
		}
	}
	
	private void sendSMS(POSTransaction transaction){
		String url = "http://xka.me/ebill/"+transaction.getCode(); //电子账单地址
		Shop shop = shopService.findShopByIdIgnoreVisible(transaction.getBusinessId());
		StringBuffer content = new StringBuffer();
		content.append("您于").append(new DateTime(transaction.getTradeDate()).toString("MM月dd日HH时mm分")).append("在 ").append(shop.getName())
		.append(" 消费人民币：").append(transaction.getSum()).append("元，查看账单详情：").append(url);
		
		SMS sms = new SMS();
		sms.setMobile(transaction.getMobile());
		sms.setMessage(content.toString());
		smsService.sendSMSAndDeductions(shop.getId() ,BusinessType.SHOP,sms,null,"使用盛付通刷卡消费,发送成功短信" );
	}
}
