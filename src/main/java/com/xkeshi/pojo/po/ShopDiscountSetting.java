package com.xkeshi.pojo.po;

import javax.persistence.Column;
import javax.persistence.Table;

/**
 * Created by nt on 2015-04-01.
 */
@Table(name = "shop_discount_setting")
public class ShopDiscountSetting extends Base {

    @Column(name = "discount_way_name_id")
    private Long discountWayNameId;

    @Column(name = "enable")
    private Integer enable; //是否启用

    @Column(name = "enable_prepaid_card")
    private Integer enablePrepaidCard;  //是否开通预付卡

    @Column(name = "shop_id")
    private Long shopId;

    public Long getShopId() {
        return shopId;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }

    public Long getDiscountWayNameId() {
        return discountWayNameId;
    }

    public void setDiscountWayNameId(Long discountWayNameId) {
        this.discountWayNameId = discountWayNameId;
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
