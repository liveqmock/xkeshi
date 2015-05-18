package com.xkeshi.dao;

import java.math.BigDecimal;

import org.apache.ibatis.annotations.Param;

/**
 * 电子券DAO
 *
 * Created by david-y on 2015/1/19.
 */
public interface ElectronicCouponDAO {

    Integer getElectronicCouponCountByMobileNumberAndShopId(@Param("mobileNumber") String mobileNumber, @Param("shopId") Long shopId);

	BigDecimal getOrderTotalAmountByOperatorSessionCode(@Param("operatorSessionCode") String operatorSessionCode);
}
