package com.xkeshi.pojo.vo.param.payment;

/**
 * 微信支付请求参数
 *
 * Created by david-y on 2015/1/22.
 */
public class WxpayPaymentParam extends PaymentParam {

    private String registerMid;
    private String dynamicCode;


    public String getRegisterMid() {
        return registerMid;
    }

    public void setRegisterMid(String registerMid) {
        this.registerMid = registerMid;
    }

    public String getDynamicCode() {
        return dynamicCode;
    }

    public void setDynamicCode(String dynamicCode) {
        this.dynamicCode = dynamicCode;
    }
}
