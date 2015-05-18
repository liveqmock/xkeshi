package com.xpos.common.service;

import com.xpos.common.entity.ShopPrinter;
import com.xpos.common.utils.Pager;

public interface ShopPrinterService {
	
	/**
	 * 通过商户ID查询打印档口
	 */
	Pager<ShopPrinter> findShopPrintersByShopId(Long shopId,Pager<ShopPrinter> pager);
	
	boolean save(ShopPrinter shopPrinter);
	
	boolean update(ShopPrinter shopPrinter);

	/**
	 * 通过ID查询
	 */
	ShopPrinter findShopPrinterById(Long id);
}
