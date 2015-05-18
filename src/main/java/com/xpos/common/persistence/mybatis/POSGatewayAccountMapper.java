package com.xpos.common.persistence.mybatis;

import org.apache.ibatis.annotations.Param;

import com.xpos.common.entity.pos.POSGatewayAccount;
import com.xpos.common.persistence.BaseMapper;

public interface POSGatewayAccountMapper extends BaseMapper<POSGatewayAccount>{

	POSGatewayAccount selectAlipayAccountByAccountAndShopId(@Param("account")String account, @Param("shopId")Long shopId);

	POSGatewayAccount selectAlipayAccountByShopId(@Param("shopId")Long shopId);
	
}