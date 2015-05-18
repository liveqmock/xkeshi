package com.xkeshi.pojo.po;

import javax.persistence.Column;
import javax.persistence.Table;
import java.math.BigDecimal;

/**
 *
 * Created by david-y on 2015/1/29.
 */
@Table(name = "shop_prepaid_card_info")
public class ShopPrepaidCardInfo extends Base {


    @Column(name = "shop_id")
    private Long shopId;
    @Column(name = "has_prepaid_card_rules")
    private Boolean hasPrepaidCardRules;
    @Column(name = "prepaid_card_count")
    private Integer prepaidCardCount;
    @Column(name = "prepaid_card_charge_amount")
    private BigDecimal prepaidCardChargeAmount;

    private String shopName;

    public Long getShopId() {
        return shopId;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }

    public Boolean getHasPrepaidCardRules() {
        return hasPrepaidCardRules;
    }

    public void setHasPrepaidCardRules(Boolean hasPrepaidCardRules) {
        this.hasPrepaidCardRules = hasPrepaidCardRules;
    }

    public Integer getPrepaidCardCount() {
        return prepaidCardCount;
    }

    public void setPrepaidCardCount(Integer prepaidCardCount) {
        this.prepaidCardCount = prepaidCardCount;
    }

    public BigDecimal getPrepaidCardChargeAmount() {
        return prepaidCardChargeAmount;
    }

    public void setPrepaidCardChargeAmount(BigDecimal prepaidCardChargeAmount) {
        this.prepaidCardChargeAmount = prepaidCardChargeAmount;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }
}
