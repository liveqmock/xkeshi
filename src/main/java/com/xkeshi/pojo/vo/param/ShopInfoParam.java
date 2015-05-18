package com.xkeshi.pojo.vo.param;

/**
 * Created by nt on 2015-04-03.
 */
public class ShopInfoParam {

    private Integer enableCash; //是否支持现金支付

    private Long shopId;

    public Long getShopId() {
        return shopId;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }

    public Integer getEnableCash() {
        return enableCash;
    }

    public void setEnableCash(Integer enableCash) {
        this.enableCash = enableCash;
    }
}
