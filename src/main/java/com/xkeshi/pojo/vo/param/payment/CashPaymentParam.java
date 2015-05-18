package com.xkeshi.pojo.vo.param.payment;

import java.math.BigDecimal;

/**
 * 现金支付请求参数
 *
 * Created by david-y on 2015/1/22.
 */
public class CashPaymentParam extends PaymentParam {

    private BigDecimal receivedAmount;


    public BigDecimal getReceivedAmount() {
        return receivedAmount;
    }

    public void setReceivedAmount(BigDecimal receivedAmount) {
        this.receivedAmount = receivedAmount;
    }
}
