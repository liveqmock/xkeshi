package com.xkeshi.dao;

import com.xkeshi.pojo.vo.ShopLiteVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by david-y on 2015/1/7.
 */
public interface PrepaidCardMerchantShopDAO {

    boolean hasShopsByMerchantId(@Param("merchantId") Long merchantId);

    List<ShopLiteVO> getShopListByMerchantId(@Param("merchantId") Long merchantId);

    void deleteShopsByMerchantId(@Param("merchantId") Long merchantId);

    void insert(@Param("merchantId") Long merchantId, @Param("shopId") Long shopId);

    Boolean checkMerchantShop(@Param("merchantId") Long merchantId, @Param("shopId") Long shopId);
}
