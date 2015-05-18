package com.xpos.api.partner;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Put;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.xpos.api.BaseResource;
import com.xpos.api.param.PartnerApplyCoupon;
import com.xpos.api.result.ResCode;
import com.xpos.api.result.ValidateError;
import com.xpos.common.entity.Configuration;
import com.xpos.common.entity.Coupon;
import com.xpos.common.entity.CouponInfo;
import com.xpos.common.entity.CouponInfo.CouponInfoType;
import com.xpos.common.entity.CouponPayment;
import com.xpos.common.entity.CouponPayment.CouponPaymentSource;
import com.xpos.common.entity.CouponPayment.CouponPaymentStatus;
import com.xpos.common.entity.CouponPayment.CouponPaymentType;
import com.xpos.common.service.ActivityService;
import com.xpos.common.service.ConfigurationService;
import com.xpos.common.service.CouponPaymentService;
import com.xpos.common.service.CouponService;
import com.xpos.common.utils.BeanUtil;
import com.xpos.common.utils.FileMD5;
import com.xpos.common.utils.IDUtil;
import com.xpos.common.utils.UUIDUtil;

public class PartnerCouponResource extends BaseResource {
	private Logger logger = LoggerFactory.getLogger(PartnerCouponResource.class);
	
	private final static String PARTNER_KEY_PREFFIX = "PARTNER_KEY_PREFFIX_"; //partnerKey名前缀
	
	@Autowired
	private CouponService couponService;
	@Autowired
	private ActivityService activityService;
	@Autowired
	private ConfigurationService confService;
	@Autowired
	private CouponPaymentService couponPaymentService;
	
	@Put("json")
	public Representation couponApply(JsonRepresentation entity){
		if(entity == null){
			return new JsonRepresentation(new ValidateError("-2","post请求body的json参数不能为空"));
		}
		
		try {
			String jsonStr = entity.getText();
			PartnerApplyCoupon applyCoupon = JSON.parseObject(jsonStr, PartnerApplyCoupon.class);
			String validation = BeanUtil.validate(applyCoupon);
			if(validation != null){
				return new JsonRepresentation(new ValidateError("0101",validation));
			}
			
			//find Configuration by channel name
			Configuration conf = confService.findByName(PARTNER_KEY_PREFFIX + applyCoupon.getChannel());
			if(conf == null){
				return new JsonRepresentation(new ValidateError("0101", "channel error"));
			}
			
			//validate token
			String inputTokenString = applyCoupon.getChannel() + applyCoupon.getCoupon_info_id() + conf.getValue();
			String validInputToken = FileMD5.getFileMD5String(inputTokenString.getBytes());
			if(!StringUtils.equalsIgnoreCase(validInputToken, applyCoupon.getToken())){
				return new JsonRepresentation(new ValidateError("0101","Token校验失败"));
			}
			
			//valid couponInfo
			Long cid = IDUtil.decode(applyCoupon.getCoupon_info_id());
			CouponInfo couponInfo = couponService.findCouponInfoById(cid);
			if(couponInfo == null || couponInfo.getPublished()==false){
				return new JsonRepresentation(new ValidateError("0102","优惠活动不存在或未发布"));
			}else if(!couponInfo.canBuy()){
				return new JsonRepresentation(new ValidateError("0103","优惠活动无法购买"));
			}
			
			//create Coupon
			boolean result = false;
			Coupon coupon = null;
			if(couponInfo.getType().equals(CouponInfoType.NORMAL)){
				CouponPayment payment = new CouponPayment();
				payment.setCouponInfo(couponInfo);
				payment.setSum(new BigDecimal(0));
				payment.setCode(UUIDUtil.getRandomString(32));
				payment.setStatus(CouponPaymentStatus.PAID_SUCCESS);
				payment.setType(CouponPaymentType.EXTERNAL_APPLY);
				payment.setQuantity(1);
				payment.setRemark("PARTNER APPLY, CHANNEL："+conf.getName());
				payment.setTradeDate(new Date());
				payment.setSource(CouponPaymentSource.XKESHI_WEB);
				result = couponPaymentService.saveCouponPayment(payment);
				
				coupon = new Coupon();
				coupon.setBusinessId(couponInfo.getBusinessId());
				coupon.setBusinessType(couponInfo.getBusinessType());
				coupon.setPayment(payment);
				coupon.setType(CouponInfoType.NORMAL);
				coupon.setCouponInfo(couponInfo);
				result &= couponService.saveCoupon(coupon, true);//coupon表添加记录
			}
			
			JsonRepresentation re = null;
			//consume coupon
			if(result){
				Map<String, Object> params = new HashMap<>();
				params.put("res", "0");
				String outputTokenString = applyCoupon.getChannel() + applyCoupon.getCoupon_info_id() + coupon.getCouponCode() + conf.getValue();
				String validOutputToken = FileMD5.getFileMD5String(outputTokenString.getBytes());
				params.put("code", coupon.getCouponCode());
				params.put("token", validOutputToken);
				re = new JsonRepresentation(params);
			}else {
				re = new JsonRepresentation(ResCode.General.SERVER_INTERNAL_ERROR);
			}
			return re;
		}catch(Exception e){
			logger.error("external partner apply coupon error", e);
			return new JsonRepresentation(ResCode.General.SERVER_INTERNAL_ERROR);
		}
	}
}
