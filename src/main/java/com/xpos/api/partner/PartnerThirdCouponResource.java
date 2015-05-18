package com.xpos.api.partner;

import java.io.IOException;
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
import com.xpos.common.entity.ThirdpartyCoupon;
import com.xpos.common.entity.ThirdpartyCouponInfo;
import com.xpos.common.service.ConfigurationService;
import com.xpos.common.service.CouponService;
import com.xpos.common.utils.BeanUtil;
import com.xpos.common.utils.FileMD5;
import com.xpos.common.utils.IDUtil;

public class PartnerThirdCouponResource extends BaseResource {
	private Logger logger = LoggerFactory.getLogger(PartnerThirdCouponResource.class);
	
	private final static String PARTNER_KEY_PREFFIX = "PARTNER_KEY_PREFFIX_"; //partnerKey名前缀
	
	@Autowired
	private CouponService couponService;
	@Autowired
	private ConfigurationService confService;
	
	@Put("json")
	public Representation thirdcouponApply(JsonRepresentation entity){
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
			ThirdpartyCouponInfo thirdCouponInfo = couponService.findThirdpartyCouponInfoById(cid);
			if(thirdCouponInfo == null || thirdCouponInfo.getPublished()==false){
				return new JsonRepresentation(new ValidateError("0102","优惠活动不存在或未发布"));
			}else if(!thirdCouponInfo.canBuy()){
				return new JsonRepresentation(new ValidateError("0103","优惠活动无法购买"));
			}
			
			//create Coupon
			boolean result = false;
			ThirdpartyCoupon thirdcoupon = new ThirdpartyCoupon();
			thirdcoupon.setCouponInfo(thirdCouponInfo);
			thirdcoupon.setMobile("");
			result = couponService.saveThirdpatyCoupon(thirdcoupon);
			thirdcoupon = couponService.findThirdCouponById(thirdcoupon.getId());
			
			JsonRepresentation re = null;
			//consume coupon
			if(result){
				Map<String, Object> params = new HashMap<>();
				params.put("res", "0");
				String outputTokenString = applyCoupon.getChannel() + applyCoupon.getCoupon_info_id() + thirdcoupon.getCouponCode() +  thirdcoupon.getPassword() + conf.getValue();
				String validOutputToken = FileMD5.getFileMD5String(outputTokenString.getBytes());
				params.put("code", thirdcoupon.getCouponCode());
				params.put("password", thirdcoupon.getPassword());
				params.put("token", validOutputToken);
				re = new JsonRepresentation(params);
			}else {
				re = new JsonRepresentation(ResCode.General.SERVER_INTERNAL_ERROR);
			}
			return re;
		}catch(Exception e){
			logger.error("external partner apply thirdcoupon error", e);
			return new JsonRepresentation(ResCode.General.SERVER_INTERNAL_ERROR);
		}
	}
	
	public static void main(String[] arges) throws IOException {
		
		System.out.println(IDUtil.encode(465L));
		//10274794
		System.out.println(FileMD5.getFileMD5String("dzcm".getBytes()));
		//d41d8cd98f00b204e9800998ecf8427e
	}
}
