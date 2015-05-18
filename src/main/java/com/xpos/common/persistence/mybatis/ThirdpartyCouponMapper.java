package com.xpos.common.persistence.mybatis;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import com.xpos.common.entity.ThirdpartyCoupon;
import com.xpos.common.persistence.BaseMapper;

public interface ThirdpartyCouponMapper extends BaseMapper<ThirdpartyCoupon>{
	
	@Update("update ThirdpartyCoupon set mobile=#{newmobile} where mobile=#{oldmobile}")
	public int updateMobile(@Param(value = "oldmobile")String oldmobile, @Param(value ="newmobile") String newmobile);
   
}