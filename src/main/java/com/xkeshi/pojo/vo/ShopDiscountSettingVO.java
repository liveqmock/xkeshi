package com.xkeshi.pojo.vo;

import com.xkeshi.pojo.meta.MetaDiscountWayName;

/**
 * Created by nt on 2015-04-01.
 */
public class ShopDiscountSettingVO {

    private Long id;

    private MetaDiscountWayName discountWayName;

    private Integer enable; //是否启用

    private Integer enablePrepaidCard;  //是否开通预付卡

    private Long shopId;

    public Long getShopId() {
        return shopId;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MetaDiscountWayName getDiscountWayName() {
        return discountWayName;
    }

    public void setDiscountWayName(MetaDiscountWayName discountWayName) {
        this.discountWayName = discountWayName;
    }

    public Integer getEnable() {
        return enable;
    }

    public void setEnable(Integer enable) {
        this.enable = enable;
    }

    public Integer getEnablePrepaidCard() {
        return enablePrepaidCard;
    }

    public void setEnablePrepaidCard(Integer enablePrepaidCard) {
        this.enablePrepaidCard = enablePrepaidCard;
    }
}
