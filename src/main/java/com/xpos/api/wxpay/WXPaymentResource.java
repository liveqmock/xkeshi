package com.xpos.api.wxpay;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.ext.servlet.ServletUtils;
import org.restlet.representation.Representation;
import org.restlet.resource.Post;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wxpay.util.Sha1Util;
import com.wxpay.ResponseHandler;
import com.xpos.api.CacheableResource;
import com.xpos.common.entity.Configuration;
import com.xpos.common.entity.CouponPayment;
import com.xpos.common.entity.CouponPayment.CouponPaymentStatus;
import com.xpos.common.entity.CouponPayment.CouponPaymentType;
import com.xpos.common.service.ConfigurationService;
import com.xpos.common.service.CouponPaymentService;
import com.xpos.common.service.CouponService;
import com.xpos.common.utils.HttpUtils;


public class WXPaymentResource extends CacheableResource {
	private Logger logger = LoggerFactory.getLogger(WXPaymentResource.class);
	
	private static Configuration WXPAY_TOKEN_RECEIVE_INTERNAL = null;
	//微信公众平台全局唯一的票据
	private static Configuration WXPAY_TOKEN_ACCESS = null;
	
	private HttpServletRequest request = null;
	
	private HttpServletResponse response = null;
	
	@Autowired
	private CouponPaymentService paymentService;
	@Autowired
	private CouponService couponService;
	@Autowired
	private ConfigurationService confService;
	
	@Value("#{settings['wxpay.partner.key']}")
	private String partnerKey;
	
	@Value("#{settings['wxpay.paySignKey']}")
	private String paySignKey;
	
	@Value("#{settings['wxpay.tokenUrl']}")
	private String tokenUrl;
	
	@Value("#{settings['wxpay.deliverNotifyUrl']}")
	private String deliverNotifyUrl;
	
	@Value("#{settings['wxpay.appId']}")
	private String appId;
	
	@Value("#{settings['wxpay.appSecret']}")
	private String appSecret;
	
	/** 微信支付交易结果 */
	@Post
	public String wxPayNotify(Representation entity) {
		if(request == null){
			return "FAIL";
		}
		
		ResponseHandler responseHandler = new ResponseHandler(request, response);
		responseHandler.setPartnerKey(partnerKey);
		responseHandler.setAppKey(paySignKey);
		SortedMap<String, String> allParams = new TreeMap<>();
		allParams.putAll(responseHandler.getAllParameters());
		allParams.putAll(responseHandler.getXmlMap());
		String notifyJson = JSON.toJSONString(allParams);
		logger.info("received WeiXin Payment notify: \n【" + notifyJson + "】");
		
		if (responseHandler.isValidSign()) {
			if (responseHandler.isWXsign()) {
				//商户订单号 code
				String out_trade_no = responseHandler.getParameter("out_trade_no");
				//财付通订单号 serial
				String transaction_id = responseHandler.getParameter("transaction_id");
				//金额,以分为单位 sum
				String total_fee = responseHandler.getParameter("total_fee");
				//支付结果 responseCode
				String trade_state = responseHandler.getParameter("trade_state");
				//商户号 
				String partner_id = responseHandler.getParameter("partner");
				//通知ID traceNo
				String notify_id = responseHandler.getParameter("notify_id");
				//支付完成时间 tradeDate
				String time_end = responseHandler.getParameter("time_end");
				//postData
				String openid = responseHandler.getXmlMap().get("openid");
				
				CouponPayment payment = paymentService.findPaymentByCode(out_trade_no);
				CouponPaymentStatus curStatus = payment.getStatus();
				CouponPayment _payment = new CouponPayment();
				_payment.setCode(payment.getCode());
				_payment.setRemark(notifyJson);
				_payment.setType(CouponPaymentType.WEI_XIN);
				//判断签名及结果
				if ("0".equals(trade_state)){
					if(curStatus.equals(CouponPaymentStatus.UNPAID) || curStatus.equals(CouponPaymentStatus.PAID_FAIL)
							|| curStatus.equals(CouponPaymentStatus.PAID_TIMEOUT)) {
						//如果消费结果返回成功，且DB仍是待付款或失败，则修改DB为交易成功状态
						_payment.setStatus(CouponPaymentStatus.PAID_SUCCESS);
						_payment.setTraceNo(notify_id);
						_payment.setSerial(transaction_id);
						DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyyMMddHHmmss");
						_payment.setTradeDate(fmt.parseDateTime(time_end).toDate());
						_payment.setResponseCode(trade_state);
						if(!paymentService.updateCouponPaymentByCode(_payment)){
							logger.error("微信付款回调更新数据库状态失败。couponPayment code:"+out_trade_no);
							return "fail";
						}
					}else{
						return "success"; //重复的支付成功通知，不做处理，直接返回success
					}
				} else {
					//支付失败，数据库记录状态后返回，不再发送优惠券
					_payment.setStatus(CouponPaymentStatus.PAID_FAIL);
					_payment.setTraceNo(notify_id);
					_payment.setSerial(transaction_id);
					DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyyMMddHHmmss");
					_payment.setTradeDate(fmt.parseDateTime(time_end).toDate());
					_payment.setResponseCode(trade_state);
					if(paymentService.updateCouponPaymentByCode(_payment)){
						return "success";
					}else{
						logger.error("微信付款回调更新数据库状态失败。couponPayment code:"+out_trade_no);
						return "fail";
					}
				}
				
				paymentService.paymentByCreateCoupon(payment, false);
				
				//通知微信平台发货成功
				SortedMap<String, String> deliverParamMap = new TreeMap<>();
				deliverParamMap.put("appid", appId);
				deliverParamMap.put("appkey", paySignKey);
				deliverParamMap.put("openid", openid);
				deliverParamMap.put("transid", transaction_id);
				deliverParamMap.put("out_trade_no", out_trade_no);
				deliverParamMap.put("deliver_timestamp", ""+System.currentTimeMillis()/1000);
				deliverParamMap.put("deliver_status", "1");
				deliverParamMap.put("deliver_msg", "ok");
				String sign;
				try {
					sign = Sha1Util.createSHA1Sign(deliverParamMap);
					deliverParamMap.put("app_signature", sign);
				} catch (Exception e) {
					logger.error("调用微信通知发货接口生成postData时异常", e);
				}
				deliverParamMap.put("sign_method", "sha1");
				String postJson = JSONArray.toJSONString(deliverParamMap);
				String jsonResponse = HttpUtils.jsonHttpsPost(deliverNotifyUrl+"?access_token="+WXPAY_TOKEN_ACCESS.getValue(), postJson);
				logger.info("调用微信通知发货接口返回值："+jsonResponse);
				
				return "success";
			} else {//sha1签名失败
				logger.info("微信付款回调通知 SHA1 签名校验失败");
			}
		}else{//MD5签名失败
			logger.info("微信付款回调通知 MD5 签名校验失败");
		}
		return "FAIL";
		
	}
	
	@Override  
	public void init(Context context, Request request, Response response) {
		super.init(context, request, response); 
		this.request = ServletUtils.getRequest(request);
		this.response = ServletUtils.getResponse(response);
		
		//validate token_access
		WXPAY_TOKEN_RECEIVE_INTERNAL = confService.findByName("WXPAY_TOKEN_RECEIVE_INTERNAL");
		long internal = Long.valueOf(WXPAY_TOKEN_RECEIVE_INTERNAL.getValue());
		
		if(WXPAY_TOKEN_ACCESS == null){
			WXPAY_TOKEN_ACCESS = confService.findByName("WXPAY_TOKEN_ACCESS");
		}
		
		Date now = new Date();
		Date lastReceivedDate = WXPAY_TOKEN_ACCESS.getModifyDate() == null ? WXPAY_TOKEN_ACCESS.getCreateDate() : WXPAY_TOKEN_ACCESS.getModifyDate();
		if(StringUtils.isBlank(WXPAY_TOKEN_ACCESS.getValue()) ||
				(now.getTime() - lastReceivedDate.getTime() >= internal * 1000)){
			String newToken = getTokenReal();
			if(StringUtils.isNotBlank(newToken)){
				WXPAY_TOKEN_ACCESS.setValue(newToken);
				WXPAY_TOKEN_ACCESS.setModifyDate(now);
				confService.update(WXPAY_TOKEN_ACCESS);
			}else{
				logger.error("received WXPAY_TOKEN error!");
			}
		}
	}
	
	private String getTokenReal(){
		Map<String, String> paramMap = new HashMap<>();
		paramMap.put("grant_type", "client_credential");
		paramMap.put("appid", appId);
		paramMap.put("secret", appSecret);
		
		String jsonResponse = HttpUtils.httpsGet(tokenUrl, paramMap);
		JSONObject object = JSONObject.parseObject(jsonResponse);
		String newToken = object.getString("access_token");
		return newToken;
	}
}
