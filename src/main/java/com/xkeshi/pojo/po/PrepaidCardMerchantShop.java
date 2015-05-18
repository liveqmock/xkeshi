package com.xkeshi.pojo.po;

import javax.persistence.Table;

/**
 * <br>Author: David <br>
 * 2015/1/6.
 */
@Table(name = "prepaid_card_merchant_shop")
public class PrepaidCardMerchantShop  {

    private Long merchantId;

    private Long shopId;

    public Long getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(Long merchantId) {
        this.merchantId = merchantId;
    }

    public Long getShopId() {
        return shopId;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }
}
