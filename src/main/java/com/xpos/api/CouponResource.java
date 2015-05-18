package com.xpos.api;

import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.joda.time.DateTime;
import org.json.JSONObject;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSON;
import com.xpos.api.param.UsedCoupon;
import com.xpos.api.result.ResCode;
import com.xpos.api.result.ValidateError;
import com.xpos.common.entity.Configuration;
import com.xpos.common.entity.Coupon;
import com.xpos.common.entity.Coupon.CouponStatus;
import com.xpos.common.entity.CouponInfo;
import com.xpos.common.entity.CouponInfo.CouponInfoStatus;
import com.xpos.common.entity.Operator;
import com.xpos.common.entity.Terminal;
import com.xpos.common.entity.face.Business.BusinessType;
import com.xpos.common.service.ActivityService;
import com.xpos.common.service.ConfigurationService;
import com.xpos.common.service.CouponService;
import com.xpos.common.service.OperatorService;
import com.xpos.common.service.TerminalService;
import com.xpos.common.utils.BeanUtil;

public class CouponResource extends BaseResource {
	private Logger logger = LoggerFactory.getLogger(CouponResource.class);
	private final String MOBILE_NUM_REGEX = "MOBILE_NUMBER_REGEX";
	
	@Autowired
	private TerminalService terminalService;
	@Autowired
	private CouponService couponService;
	@Autowired
	private ActivityService activityService;
//	@Autowired
//	private SMSQueueService smsQueueService;
	@Autowired
	private OperatorService operatorService;
	@Autowired
	private ConfigurationService confService;
	
	/**
	 * /coupon/{couponId}/resend?phone=13566256326&deviceNumber=xxx&activitySerial=xxx
	 * @return
	 */
	@Get("json")
	public Representation resend(){
		Long couponId = NumberUtils.toLong((String)getRequestAttributes().get("couponId"));
		String phone = getQuery().getFirstValue("phone");
		String deviceNumber = getQuery().getFirstValue("deviceNumber");
		String activitySerial = getQuery().getFirstValue("activitySerial");
		Configuration conf = confService.findByName(MOBILE_NUM_REGEX);
		
		if(StringUtils.isBlank(deviceNumber)){
			return new JsonRepresentation(new ValidateError("2101", "deviceNumber不能为空"));
		}else if(StringUtils.isBlank(phone)){
			return new JsonRepresentation(new ValidateError("2103", "手机号不存在"));
		}else if(!Pattern.matches(conf.getValue(), phone)){
			return new JsonRepresentation(new ValidateError("2103", "手机号不存在"));
		}else if(couponId <= 0){
			return new JsonRepresentation(new ValidateError("2102", "couponId不存在"));
		}
		
		//take care of device_number here...
		Terminal terminal = terminalService.findTerminalByDevice(deviceNumber);
		if(terminal == null){
			return new JsonRepresentation(new ValidateError("2101","设备未注册"));
		}else if(terminal.getShop() == null){
			return new JsonRepresentation(new ValidateError("2101","指定商户不存在/已删除"));
		}
		
		//check coupon status
		Coupon coupon = couponService.findCouponById(couponId);
		if(coupon == null){
			return new JsonRepresentation(new ValidateError("2102","活动不存在"));
		}else if(coupon.getStatus().equals(CouponStatus.USED)){
			return new JsonRepresentation(new ValidateError("2102","优惠码已使用"));
		}else if(!StringUtils.equals(phone, coupon.getMobile())){
			return new JsonRepresentation(new ValidateError("2102","手机号码不匹配"));
		}
		
		if(StringUtils.isNotBlank(activitySerial) && 
				activityService.findRelatedActivityByCouponInfoId(coupon.getCouponInfo().getBusinessId())!= null){
			return new JsonRepresentation(new ValidateError("2102","优惠码与活动不匹配"));
		}
		
//		smsQueueService.resendCoupon(coupon);
		return new JsonRepresentation(ResCode.General.OK);
		
	}
	
	@Delete("json")
	public Representation consume(JsonRepresentation entity){
		if(entity == null){
			return new JsonRepresentation(new ValidateError("-2","post请求body的json参数不能为空"));
		}
		
		try {
			String jsonStr = entity.getText();
			UsedCoupon usedCoupon = JSON.parseObject(jsonStr, UsedCoupon.class);
			String validation = BeanUtil.validate(usedCoupon);
			if(validation != null){
				return new JsonRepresentation(new ValidateError("1001",validation));
			}
			
			Configuration conf = confService.findByName(MOBILE_NUM_REGEX);
			if(StringUtils.isBlank(usedCoupon.getDeviceNumber())){
				return new JsonRepresentation(new ValidateError("1002", "deviceNumber不能为空"));
			}else if(StringUtils.isNotBlank(usedCoupon.getPhone()) && !Pattern.matches(conf.getValue(), usedCoupon.getPhone())){
				return new JsonRepresentation(new ValidateError("1005", "手机号格式错误"));
			}else if(StringUtils.isBlank(usedCoupon.getCoupon())){
				return new JsonRepresentation(new ValidateError("1006", "优惠码不能为空"));
			}else if(usedCoupon.getOperatorId() == null || usedCoupon.getOperatorId() <= 0){
				return new JsonRepresentation(new ValidateError("1001", "参数校验失败"));
			}
			
			//take care of device_number here...
			Terminal terminal = terminalService.findTerminalByDevice(usedCoupon.getDeviceNumber());
			Operator operator = operatorService.findById(usedCoupon.getOperatorId());
			if(terminal == null){
				return new JsonRepresentation(new ValidateError("1007","设备未注册"));
			}else if(terminal.getShop() == null){
				return new JsonRepresentation(new ValidateError("1003","指定商户不存在/已删除"));
			}else if(!terminal.getShop().getId().equals(Long.valueOf(usedCoupon.getMid()))){
				return new JsonRepresentation(new ValidateError("1013","商户与设备不匹配"));
			}else if(operator == null || !operator.getShop().getId().equals(terminal.getShop().getSelfBusinessId())){
				return new JsonRepresentation(new ValidateError("1014","操作员信息错误"));
			}
//			else if(!usedCoupon.getCsrfToken().equals(SecurityUtils.getSubject().getSession().getAttribute(CSRFTokenManager.CSRF_PARAM_NAME))){
//				return new JsonRepresentation(new ValidateError("1012","token码校验失败"));
//			}
			
			//check coupon status
			Coupon coupon = couponService.findCouponByCode(usedCoupon.getCoupon());
			if(coupon == null){
				return new JsonRepresentation(new ValidateError("1008","活动不存在"));
			}
			Set<Long> allowedShops = coupon.getCouponInfo().getScope();
			if(CollectionUtils.isEmpty(allowedShops) || !allowedShops.contains(Long.valueOf(usedCoupon.getMid()))){
				//核销的商户不在指定消费商户列表中
				return new JsonRepresentation(new ValidateError("1008","活动不存在"));
			}else if(coupon.getStatus().equals(CouponStatus.USED)){
				return new JsonRepresentation(new ValidateError("1011","优惠码已使用"));
			}else if(StringUtils.isNotBlank(coupon.getMobile()) 
					&& !StringUtils.equals(usedCoupon.getPhone(), coupon.getMobile())){
				return new JsonRepresentation(new ValidateError("1005", "手机号错误"));
			}
			
			CouponInfo couponInfo = coupon.getCouponInfo();
			//check activity status
			if(couponInfo == null){
				return new JsonRepresentation(new ValidateError("1008","活动不存在"));
			}else if(couponInfo.getStatus().equals(CouponInfoStatus.PENDING) || couponInfo.getStatus().equals(CouponInfoStatus.UNPUBLISHED)
					|| !couponInfo.getPublished()){
				return new JsonRepresentation(new ValidateError("1009","活动未开始"));
			}else if(couponInfo.getStatus().equals(CouponInfoStatus.EXPIRE)){
				return new JsonRepresentation(new ValidateError("1010","活动已结束"));
			}
			
			JsonRepresentation re = null;
			//consume coupon
			DateTime consumeTime = new DateTime();
			Coupon updateCoupon = new Coupon();
			updateCoupon.setBusinessId(Long.valueOf(usedCoupon.getMid()));
			updateCoupon.setBusinessType(BusinessType.SHOP);
			updateCoupon.setCouponCode(coupon.getCouponCode());
			updateCoupon.setOperator(operator);
			updateCoupon.setConsumeDate(consumeTime.toDate());//核销时间
			if(StringUtils.isBlank(coupon.getMobile()) && StringUtils.isNotBlank(usedCoupon.getPhone())){
				updateCoupon.setMobile(usedCoupon.getPhone());
			}
			
			if(couponService.consumeCoupon(updateCoupon)){
				re = new JsonRepresentation(ResCode.General.OK);
				JSONObject json = re.getJsonObject();
				json.put("phone", usedCoupon.getPhone());
				json.put("activityName", couponInfo.getName());
				json.put("intro", couponInfo.getIntro());
				json.put("consumeTime", consumeTime.toString("yyyy-MM-dd HH:mm:ss"));
			}else {
				logger.error("优惠码核销数据库更新失败。");
				re = new JsonRepresentation(new ValidateError("1015","核销失败，请确认手机号、优惠码是否正确"));
			}
			return re;
		}catch(Exception e){
			logger.error("verify & consume coupon error, due to "+ e.getMessage(), e);
			return new JsonRepresentation(ResCode.General.SERVER_INTERNAL_ERROR);
		}
	}
}
