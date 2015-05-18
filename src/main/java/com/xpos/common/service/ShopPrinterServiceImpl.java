package com.xpos.common.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xpos.common.entity.ShopPrinter;
import com.xpos.common.persistence.mybatis.ShopPrinterMapper;
import com.xpos.common.utils.Pager;

@Service
public class ShopPrinterServiceImpl implements ShopPrinterService{

	@Autowired
	private ShopPrinterMapper shopPrinterMapper;
	
	@Override
	public Pager<ShopPrinter> findShopPrintersByShopId(Long shopId,Pager<ShopPrinter> pager) {
		if(shopId == null) {
			return null;
		}else if(pager == null) {
			pager = new Pager<ShopPrinter>();
			pager.setPageSize(Integer.MAX_VALUE);
		}
		pager.setTotalCount(shopPrinterMapper.findShopPrintersByShopId(shopId, null).size());
		pager.setList(shopPrinterMapper.findShopPrintersByShopId(shopId, pager));
		return pager;
	}

	@Override
	public boolean save(ShopPrinter shopPrinter) {
		return shopPrinterMapper.insert(shopPrinter)>0;
	}

	@Override
	public boolean update(ShopPrinter shopPrinter) {
		if(shopPrinter == null || shopPrinter.getId() == null) {
			return false;
		}
		return shopPrinterMapper.update(shopPrinter)>0;
	}

	@Override
	public ShopPrinter findShopPrinterById(Long id) {
		return shopPrinterMapper.selectById(id);
	}
	
	
}
