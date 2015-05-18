package com.xkeshi.pojo.vo;

/**
 *
 * 集团非统一管理商户会员的商户列表
 *
 * Created by david-y on 2015/1/29.
 */
public class ShopPrepaidCardVO {
    private Long shopId;
    private String shopName;
    private Boolean hasChargeRules;
    private Integer prepaidCardCount;
    private String chargeAmount;

    public Long getShopId() {
        return shopId;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public Boolean getHasChargeRules() {
        return hasChargeRules;
    }

    public void setHasChargeRules(Boolean hasChargeRules) {
        this.hasChargeRules = hasChargeRules;
    }

    public Integer getPrepaidCardCount() {
        return prepaidCardCount;
    }

    public void setPrepaidCardCount(Integer prepaidCardCount) {
        this.prepaidCardCount = prepaidCardCount;
    }

    public String getChargeAmount() {
        return chargeAmount;
    }

    public void setChargeAmount(String chargeAmount) {
        this.chargeAmount = chargeAmount;
    }
}
