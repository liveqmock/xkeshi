package com.xkeshi.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.xkeshi.dao.ShopInfoDAO;
import com.xkeshi.pojo.po.ShopInfo;

/**
 * 
 * @author xk
 * @createDate 2015/03/01
 */
@Service
public class XShopInfoService {
  
	@Resource
	private ShopInfoDAO  shopInfoDAO  ;
	
	
	public ShopInfo  getShopInfoByShopId(Long shopId){
		return shopInfoDAO.getShopInfoByShopId(shopId);
	}
	
	
}
