package com.xkeshi.dao;

import com.xkeshi.pojo.po.Shop;
import com.xkeshi.pojo.vo.ShopLiteVO;

import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by david-y on 2015/1/8.
 */
public interface ShopDAO  {
    boolean hasMerchant(@Param("shopId") Long shopId);

    Long getMerchantId(@Param("shopId") Long shopId);

    List<ShopLiteVO> getShopsByMerchantId(@Param("merchantId") Long merchantId);

	Shop getShopByShopId(@Param("shopId") Long shopId);
	
	int updateShopByShift(@Param("shop") Shop shop);

    int updateShopByMultiplePayment(@Param("shop") Shop shop);

    String getNameById(@Param("shopId") Long shopId);

    Shop getShopLiteById(@Param("shopId") Long shopId);

	Shop selectShopByShopId(@Param("businessId")Long businessId);
}