package com.xkeshi.pojo.vo.param.payment;

/**
 * 预付卡支付请求参数
 * <p/>
 * Created by david-y on 2015/1/22.
 */
public class PrepaidCardPaymentParam extends PaymentParam {

    private String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
