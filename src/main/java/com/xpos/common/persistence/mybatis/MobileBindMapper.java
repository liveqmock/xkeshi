package com.xpos.common.persistence.mybatis;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultType;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.xpos.common.entity.BaseEntity;
import com.xpos.common.persistence.BaseMapper;

public interface MobileBindMapper extends BaseMapper<BaseEntity>{

	@ResultType(value = String.class)
	@Select("select mobile from Mobile_Bind where appID=#{appID} and openID = #{openID} and deleted=false and bind=true ")
	public String selectBindedMobileByOpenId(@Param("openID")String openID,@Param("appID")String appID);
	
	/**
	 * mobile和code为必填项,openID和appID选填
	 * */
	public int save( @Param("mobile")String mobile, @Param("code")String code, @Param("openID")String openID, @Param("appID")String appID);

	@ResultType(value = String.class)
	@Select("select sms_verification_code from Mobile_Bind where appID = #{appID} and mobile = #{mobile} and deleted = false")
	public String selectVerifyCodeByMobile(@Param("mobile")String mobile, @Param("appID")String appID);
	
	@ResultType(value = Integer.class)
	@Update("update Mobile_Bind set bind = true where mobile = #{mobile} and appID=#{appID} and deleted = false")
	public int updateBindResultByMobile( @Param("mobile")String mobile, @Param("appID")String appID);
	
	
}