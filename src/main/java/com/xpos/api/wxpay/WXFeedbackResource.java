package com.xpos.api.wxpay;

import java.util.SortedMap;

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

import com.alibaba.fastjson.JSON;
import com.wxpay.ResponseHandler;
import com.xpos.api.CacheableResource;


public class WXFeedbackResource extends CacheableResource {
	private Logger logger = LoggerFactory.getLogger(WXFeedbackResource.class);
	
	private HttpServletRequest request = null;
	
	private HttpServletResponse response = null;
	
	@Value("#{settings['wxpay.partner.key']}")
	private String partnerKey;
	
	@Value("#{settings['wxpay.paySignKey']}")
	private String paySignKey;
	
	/** 微信用户维权通知 */
	@Post
	public String wxPayNotify(Representation entity) {
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

		
		if(responseHandler.isWXsignfeedback()) {
			String notifyJson = JSON.toJSONString(postDataMap);
			logger.info("WeiXin Feedback notify received: \n【" + notifyJson + "】");
			return "ok";
		} else {//sha1签名失败
			logger.info("微信用户维权通知 SHA1 签名校验失败");
		}
		return "fail";
		
	}
	
	@Override
	public void init(Context context, Request request, Response response) {
		super.init(context, request, response);
		this.request = ServletUtils.getRequest(request);
		this.response = ServletUtils.getResponse(response);
	}
	
}
