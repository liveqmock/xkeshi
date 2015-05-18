package com.xpos.common.persistence.mybatis;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.xpos.common.entity.ShopPrinter;
import com.xpos.common.utils.Pager;


public interface ShopPrinterMapper{
	
	ShopPrinter selectById(Long id);

	List <ShopPrinter> findShopPrintersByShopId(@Param("shopId")Long shopId,@Param("pager")Pager<ShopPrinter> pager);
	
	int insert(ShopPrinter shopPrinter);
	
	int update(ShopPrinter shopPrinter);

}