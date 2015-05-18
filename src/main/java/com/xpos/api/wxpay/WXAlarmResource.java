package com.xpos.api.wxpay;

import java.util.Map;
import java.util.Properties;
import java.util.SortedMap;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.ext.servlet.ServletUtils;
import org.restlet.representation.Representation;
import org.restlet.resource.Post;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import com.alibaba.fastjson.JSON;
import com.wxpay.ResponseHandler;
import com.xpos.api.CacheableResource;
import com.xpos.common.entity.Configuration;
import com.xpos.common.service.ConfigurationService;


public class WXAlarmResource extends CacheableResource {
	private Logger logger = LoggerFactory.getLogger(WXAlarmResource.class);
	
	private static Configuration WXPAY_ALARM_MAIL_ENABLED = null;
	private static Configuration WXPAY_ALARM_MAIL_HOST = null;
	private static Configuration WXPAY_ALARM_MAIL_USERNAME = null;
	private static Configuration WXPAY_ALARM_MAIL_PASSWORD = null;
	private static Configuration WXPAY_ALARM_MAIL_TO_LIST = null;
	
	private HttpServletRequest request = null;
	
	private HttpServletResponse response = null;
	
	@Value("#{settings['wxpay.partner.key']}")
	private String partnerKey;
	
	@Value("#{settings['wxpay.paySignKey']}")
	private String paySignKey;
	
	@Resource
	private ConfigurationService confService;
	
	/** 微信告警通知 */
	@Post
	public String wxPayAlarm(Representation entity) {
		if(request == null){
			return "fail";
		}
		
		ResponseHandler responseHandler = new ResponseHandler(request, response);
		responseHandler.setPartnerKey(partnerKey);
		responseHandler.setAppKey(paySignKey);
		SortedMap<String, String> postDataMap = responseHandler.getXmlMap();
		if(postDataMap == null || postDataMap.isEmpty()){
			return "fail";
		}
		
		if(responseHandler.isWXSignAlarm()) {
			String notifyJson = JSON.toJSONString(postDataMap);
			logger.error("WeiXin Alarm notify received: \n【" + notifyJson + "】");
			
			WXPAY_ALARM_MAIL_ENABLED = confService.findByName("WXPAY_ALARM_MAIL_ENABLED");
			Boolean isEnabled = WXPAY_ALARM_MAIL_ENABLED != null ? Boolean.valueOf(WXPAY_ALARM_MAIL_ENABLED.getValue()) : false;
			if(isEnabled){
				sendAlarmEmail(postDataMap);
			}
			
			return "success";
		} else {//sha1签名失败
			logger.info("微信告警通知 SHA1 签名校验失败");
			String notifyJson = JSON.toJSONString(postDataMap);
			logger.error("WeiXin Alarm notify received: \n【" + notifyJson + "】");
		}
		return "fail";
		
	}
	
	@Override
	public void init(Context context, Request request, Response response) {
		super.init(context, request, response);
		this.request = ServletUtils.getRequest(request);
		this.response = ServletUtils.getResponse(response);
	}
	
	public void sendAlarmEmail(Map<String, String> paramMap){
		WXPAY_ALARM_MAIL_HOST= confService.findByName("WXPAY_ALARM_MAIL_HOST");
		WXPAY_ALARM_MAIL_USERNAME = confService.findByName("WXPAY_ALARM_MAIL_USERNAME");
		WXPAY_ALARM_MAIL_PASSWORD = confService.findByName("WXPAY_ALARM_MAIL_PASSWORD");
		WXPAY_ALARM_MAIL_TO_LIST = confService.findByName("WXPAY_ALARM_MAIL_TO_LIST");
		
		JavaMailSenderImpl senderImpl = new JavaMailSenderImpl();
		senderImpl.setDefaultEncoding("GB2312");
		SimpleMailMessage mailMessage = new SimpleMailMessage();
		mailMessage.setTo(WXPAY_ALARM_MAIL_TO_LIST.getValue());
		mailMessage.setFrom(WXPAY_ALARM_MAIL_USERNAME.getValue());
		mailMessage.setSubject("微信支付告警通知");
		mailMessage.setText("错误类型：" + paramMap.get("errortype") + " \n"
							+ "错误描述： " + paramMap.get("description") + " \n"
							+ "错误详情： " + paramMap.get("alarmcontent"));
		
		senderImpl.setHost(WXPAY_ALARM_MAIL_HOST.getValue());
		senderImpl.setUsername(WXPAY_ALARM_MAIL_USERNAME.getValue());
		senderImpl.setPassword(WXPAY_ALARM_MAIL_PASSWORD.getValue());
		
		Properties prop = new Properties();
		prop.put("mail.smtp.auth", "true"); // 将这个参数设为true，让服务器进行认证,认证用户名和密码是否正确
		prop.put("mail.smtp.timeout", "25000");
		senderImpl.setJavaMailProperties(prop);
		// 发送邮件  
		try{
			senderImpl.send(mailMessage);
			logger.info("微信告警通知邮件发送成功，【" + WXPAY_ALARM_MAIL_TO_LIST + "】");
		}catch(MailException me){
			logger.error("微信告警通知邮件发送失败", me);
		}
	}
	
}
