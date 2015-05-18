package com.xkeshi.pojo.vo;

import java.math.BigDecimal;

/**
 * 预付卡充值记录vo
 * Created by david-y on 2015/1/20.
 */
public class PrepaidCardChargeVO {
    private String prepaidCardChargeCode;
    private BigDecimal amount;
    private String type;
    private String tradeTime;
    private String status;

    public String getPrepaidCardChargeCode() {
        return prepaidCardChargeCode;
    }

    public void setPrepaidCardChargeCode(String prepaidCardChargeCode) {
        this.prepaidCardChargeCode = prepaidCardChargeCode;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTradeTime() {
        return tradeTime;
    }

    public void setTradeTime(String tradeTime) {
        this.tradeTime = tradeTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
