package com.xpos.controller;

import java.util.HashMap;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;

import com.xpos.common.exception.UpYunException;
import com.xpos.common.utils.UpYunUtils;

@Controller
@RequestMapping("/upyun")
public class UpYunController extends BaseController {
	@Value("#{settings['context']}")
	private String context;
	
	@Value("#{settings['upyun.image.bucketName']}")
	private String bucketName;
	
	@Value("#{settings['upyun.form.apiKey']}")
	private String apiKey;


	
	@RequestMapping(value="upload", method = RequestMethod.GET)
	@ResponseBody
	public UpYunEntity up(@RequestParam("type") Integer type,
			@RequestParam(value="shopId", required=false, defaultValue="") Long shopId,
			@RequestParam(value="domain", required = false, defaultValue = "") String domain) throws UpYunException{
		String upYunSaveFolder;
		switch(type){
			case 1: upYunSaveFolder = "joint/UEditor/"+shopId; break; //JOINT 创建商家互通活动时的富文本内容
			case 2: upYunSaveFolder = "merchant/" + shopId + "/promotion/UEditor"; break; //PROMOTION 创建商家促销活动时的富文本内容
			case 3: upYunSaveFolder = "merchant/" + shopId + "/COUPON/UEditor"; break; //COUPON 创建商家优惠券时的富文本内容
			case 4: upYunSaveFolder = "merchant/" + shopId + "/ARTICLE/UEditor"; break; //ARTICLE “关于商户”的富文本内容
			case 5: upYunSaveFolder = "pager/UEditor"; break; //文章模板
			default: upYunSaveFolder = null; return new UpYunEntity();
		}
		long expiration = System.currentTimeMillis() / 1000 + 6 * 10 * 30; //以秒为单位，过期时间默认30分钟
		String saveKey = getSaveKey(upYunSaveFolder, "{random32}{.suffix}");
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("return-url", StringUtils.defaultIfBlank("http://"+domain, context) + "/upyun/notify");
		String policy = UpYunUtils.makePolicy(saveKey, expiration, bucketName, params);
		String signature = getSignature(policy, apiKey);
		return new UpYunEntity(bucketName, policy, signature);
	}
	
	@RequestMapping(value="notify")
	@ResponseBody
	public UeditorCallBackEntity notifyUrl(UpYunCallBackEntity upyunImage, WebRequest request) throws Exception {
		return new UeditorCallBackEntity(upyunImage);
	}

	public String getSaveKey(String saveFloder, String pattern) {
		return "/" + saveFloder + "/" + pattern;
	}

	public String getSignature(String policy, String API_KEY) {
		return UpYunUtils.signature(policy + "&" + API_KEY);
	}
	
}

class UpYunEntity {
	private boolean success;
	private String bucket;
	private String policy;
	private String signature;
	
	public UpYunEntity(){
	}
	
	public UpYunEntity(String bucket, String policy, String signature){
		this.bucket = bucket;
		this.policy = policy;
		this.signature = signature;
	}

	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public String getBucket() {
		return bucket;
	}
	public void setBucket(String bucket) {
		this.bucket = bucket;
	}
	public String getPolicy() {
		return policy;
	}
	public void setPolicy(String policy) {
		this.policy = policy;
	}
	public String getSignature() {
		return signature;
	}
	public void setSignature(String signature) {
		this.signature = signature;
	}
	
}

class UpYunCallBackEntity{
	private String code;
	private String message;
	private String url;
	private String time;
	private String sign;
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getSign() {
		return sign;
	}
	public void setSign(String sign) {
		this.sign = sign;
	}
}

class UeditorCallBackEntity{
	private String original;
	private String url;
	private String title;
	private String state;

	public UeditorCallBackEntity(UpYunCallBackEntity upyunImage) {
		url = upyunImage.getUrl();
		state = transUpYunCodeToUeState(upyunImage.getCode(), upyunImage.getMessage());
	}

	private String transUpYunCodeToUeState(String code, String msg) {
		String defaultState = "未知错误";
		if (code == null) {
			return defaultState;
		}
		if (code.equals("200")) {
			return "SUCCESS";
		}
		return msg;
	}

	public String getOriginal() {
		return original;
	}
	public void setOriginal(String original) {
		this.original = original;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
}
