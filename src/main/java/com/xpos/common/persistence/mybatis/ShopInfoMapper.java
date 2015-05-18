package com.xpos.common.persistence.mybatis;

import com.xpos.common.entity.ShopInfo;
import com.xpos.common.persistence.BaseMapper;
import org.apache.ibatis.annotations.Param;

public interface ShopInfoMapper extends BaseMapper<ShopInfo> {

	int updateByShopId(@Param("shopInfo") ShopInfo shopInfo);

    int updateShopInfoCash(@Param("shopInfo") ShopInfo shopInfo);
}
