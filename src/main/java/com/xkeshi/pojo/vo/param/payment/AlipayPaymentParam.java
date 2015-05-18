package com.xkeshi.pojo.vo.param.payment;

/**
 * 支付宝支付请求参数
 *
 * Created by david-y on 2015/1/22.
 */
public class AlipayPaymentParam extends PaymentParam {

    private String sellerAccount;
    private String dynamicCode;

    public String getSellerAccount() {
        return sellerAccount;
    }

    public void setSellerAccount(String sellerAccount) {
        this.sellerAccount = sellerAccount;
    }

    public String getDynamicCode() {
        return dynamicCode;
    }

    public void setDynamicCode(String dynamicCode) {
        this.dynamicCode = dynamicCode;
    }
}
