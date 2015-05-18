package com.xkeshi.dao;

import com.xkeshi.pojo.po.ShopDiscountSetting;
import com.xkeshi.pojo.vo.param.ShopDiscountSettingParam;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by nt on 2015-04-01.
 */
public interface ShopDiscountSettingDAO extends BaseDAO<ShopDiscountSetting> {

    List<ShopDiscountSetting> getDiscountWayById(@Param("id") Long id);

    Integer getDiscountWayCountById(@Param("id") Long id);

    int discountUpdate(@Param("param") ShopDiscountSettingParam param);
}
