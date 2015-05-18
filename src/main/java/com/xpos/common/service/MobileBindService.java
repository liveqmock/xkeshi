package com.xpos.common.service;


//TODO 考虑把这个Service 融合到其他地方去
public interface MobileBindService {

	String findBindedMobileByOpenID(String openID, String appID);

	String generateValidationCode(String appID,String openID, String mobile);

	String findVerifyCodeByMobile(String mobile,String appID);

	boolean verifySuccess(String mobile,String appID);

	String generateValidationCodewithOutopenId(String appID,String mobile);


}
