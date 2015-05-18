package com.xkeshi.dao;

import org.apache.ibatis.annotations.Param;

import com.xkeshi.pojo.po.ShopInfo;

public interface ShopInfoDAO {

	ShopInfo  getShopInfoByShopId(@Param("shopId") Long shopId);
	
}
