package com.xkeshi.pojo.vo;

/**
 * shop列表显示时使用
 *
 * Created by david-y on 2015/1/7.
 */
public class ShopLiteVO {
    private Long id;
    private String name;
    private String contact;
    private Long merchantId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public Long getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(Long merchantId) {
        this.merchantId = merchantId;
    }
}
