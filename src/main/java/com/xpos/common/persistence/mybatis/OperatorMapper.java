package com.xpos.common.persistence.mybatis;

import org.apache.ibatis.annotations.Param;

import com.xpos.common.entity.Operator;
import com.xpos.common.persistence.BaseMapper;

public interface OperatorMapper extends BaseMapper<Operator>{
	
	Operator findManagerByNameAndShopId(@Param("userName")String userName,@Param("shopId")Long shopId);
}
