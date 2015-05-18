package com.xkeshi.service;

import com.xkeshi.dao.ElectronicCouponDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by david-y on 2015/1/19.
 */
@Service
public class ElectronicCouponService {

    @Autowired(required = false)
    private ElectronicCouponDAO electronicCouponDAO;

    /**
     *
     * 获取电子券可用数量
     *
     * Gets electronic coupon count by mobile number and shop id.
     *
     * @param mobileNumber the mobile number
     * @param shopId the shop id
     * @return the electronic coupon count by mobile number and shop id
     */
    public Integer getElectronicCouponCountByMobileNumberAndShopId(String mobileNumber, Long shopId) {
        return electronicCouponDAO.getElectronicCouponCountByMobileNumberAndShopId(mobileNumber, shopId);
    }
}
