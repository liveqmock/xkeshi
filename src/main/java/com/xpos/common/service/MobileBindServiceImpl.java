package com.xpos.common.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.xpos.common.persistence.mybatis.MobileBindMapper;
import com.xpos.common.utils.CouponUtil;

@Service
public class MobileBindServiceImpl implements MobileBindService{
	
	@Resource
	private MobileBindMapper mobileBindMapper;
	
	@Override
	public String findBindedMobileByOpenID(String openID,String appID) {
		return mobileBindMapper.selectBindedMobileByOpenId(openID,appID);
	}
	
	@Override
	public String findVerifyCodeByMobile(String mobile,String appID){
		return mobileBindMapper.selectVerifyCodeByMobile(mobile,appID);
	}

	@Override
	public String generateValidationCode(String appID,String openID, String mobile) {
		String code = CouponUtil.newCode(3);
		mobileBindMapper.save(mobile,code,openID,appID);
		return code;
	}
	
	@Override
	public String generateValidationCodewithOutopenId(String appID,String mobile) {
		String code = CouponUtil.newCode(5);
		mobileBindMapper.save(mobile,code,null,appID);
		return code;
	}
	
	@Override
	public boolean verifySuccess(String mobile,String appID) {
		return mobileBindMapper.updateBindResultByMobile(mobile,appID) == 1;
	}

}
