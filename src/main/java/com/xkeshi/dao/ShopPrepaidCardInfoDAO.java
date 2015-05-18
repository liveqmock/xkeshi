package com.xkeshi.dao;

import com.xkeshi.pojo.po.ShopPrepaidCardInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by david-y on 2015/1/21.
 */
public interface ShopPrepaidCardInfoDAO extends BaseDAO<ShopPrepaidCardInfo> {


    List<ShopPrepaidCardInfo> getListByMerchantId(@Param("merchantId") Long merchantId);


}
