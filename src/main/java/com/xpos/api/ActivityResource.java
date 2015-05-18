package com.xpos.api;

import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.json.JSONObject;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import com.xpos.api.result.ResCode;
import com.xpos.api.result.ValidateError;
import com.xpos.common.entity.Configuration;
import com.xpos.common.entity.Coupon;
import com.xpos.common.entity.CouponInfo;
import com.xpos.common.entity.Terminal;
import com.xpos.common.entity.Coupon.CouponStatus;
import com.xpos.common.service.ActivityService;
import com.xpos.common.service.ConfigurationService;
import com.xpos.common.service.CouponService;
import com.xpos.common.service.TerminalService;

public class ActivityResource extends BaseResource {
	private Logger logger = LoggerFactory.getLogger(ActivityResource.class);
	private final String MOBILE_NUM_REGEX = "MOBILE_NUMBER_REGEX";
	
	@Autowired
	private TerminalService terminalService;
	@Autowired
	private ActivityService activityService;
	@Autowired
	private CouponService couponService;
	@Autowired
	private ConfigurationService confService;
	
	/**
	 * /merchant/{mid}/activity?deviceNumber=xxx&phone=18888888888&coupon=xxxxxx&cid=xxxx
	 * @return
	 */
	@Get("json")
	public Representation queryActivity(){
		String mid = (String) getRequestAttributes().get("mid");
		String deviceNumber = getQuery().getFirstValue("deviceNumber");
		String phone = getQuery().getFirstValue("phone");
		String couponStr = getQuery().getFirstValue("coupon");
		String couponId = getQuery().getFirstValue("cid");
		
		if(StringUtils.isBlank(couponStr)&&StringUtils.isBlank(couponId)){
			return new JsonRepresentation(new ValidateError("0502", "优惠码和优惠码ID必须传一个"));
		}
		
		if(StringUtils.isBlank(deviceNumber)){
			return new JsonRepresentation(new ValidateError("0508", "deviceNumber不能为空"));
		}
		
		Configuration conf = confService.findByName(MOBILE_NUM_REGEX);
		if(StringUtils.isNotBlank(phone) && !Pattern.matches(conf.getValue(), phone)){
			return new JsonRepresentation(new ValidateError("0504", "手机号格式错误"));
		}
		
		//take care of device_number here...
		Terminal terminal = terminalService.findTerminalByDevice(deviceNumber);
		if(terminal == null){
			return new JsonRepresentation(new ValidateError("0501","设备未注册"));
		}else if(terminal.getShop() == null){
			return new JsonRepresentation(new ValidateError("0503","指定商户不存在/已删除"));
		}else if(!terminal.getShop().getId().equals(Long.valueOf(mid))){
			return new JsonRepresentation(new ValidateError("0502","商户与设备不匹配"));
		}
		
		JsonRepresentation re = null;
		try{
			Coupon coupon = null;
			if(StringUtils.isNotBlank(couponId)){
				coupon = couponService.findCouponById(Long.valueOf(couponId));
			}
			if(coupon == null && StringUtils.isNotBlank(couponStr)){
				coupon = couponService.findCouponByCode(couponStr);
			}
			if(coupon == null){
				// coupon不存在
				return new JsonRepresentation(new ValidateError("0505","优惠码错误"));
			}else{
				Set<Long> allowedShops = coupon.getCouponInfo().getScope();
				if(CollectionUtils.isEmpty(allowedShops) || !allowedShops.contains(Long.valueOf(mid))){
					//核销的商户不在指定消费商户列表中
					return new JsonRepresentation(new ValidateError("0505","对不起，本商户不在指定消费商户范围内"));
				}
			}
			if(StringUtils.isNotBlank(coupon.getMobile()) && !StringUtils.equals(phone, coupon.getMobile())){
				return new JsonRepresentation(new ValidateError("0504", "手机号不正确"));
			}
			
			CouponInfo couponInfo = coupon.getCouponInfo();
			
			if(couponInfo == null){
				re = new JsonRepresentation(new ValidateError("0507","活动不存在"));
			}else{
				String startDate = couponInfo.getStartDate() != null ? new DateTime(couponInfo.getStartDate()).toString("yyyy-MM-dd") : "";
				String endDate = couponInfo.getEndDate() != null ? new DateTime(couponInfo.getEndDate()).toString("yyyy-MM-dd") : "";
				re = new JsonRepresentation(ResCode.General.OK);
				JSONObject json = re.getJsonObject();
				json.put("phone", phone);
				json.put("name", couponInfo.getName());
				json.put("intro", couponInfo.getIntro());
				json.put("startDate", startDate);
				json.put("endDate", endDate);
				json.put("activityStatus", couponInfo.getStatus().getState());
				json.put("couponStatus", coupon.getStatus().getState());
				json.put("thumb", "http://xpos-img.b0.upaiyun.com"+couponInfo.getThumb());
				if(CouponStatus.USED.equals(coupon.getStatus())){
					json.put("usedDate", coupon.getConsumeDate() != null ? new DateTime(coupon.getConsumeDate()).toString("yyyy-MM-dd HH:mm:ss") : "");
				}
			}
		} catch (Exception e) {
			logger.error("Cannot find coupon by code & phone due to "+ e.getMessage(), e);
			re = new JsonRepresentation(ResCode.General.SERVER_INTERNAL_ERROR);
		}
		return re;
	}
}
