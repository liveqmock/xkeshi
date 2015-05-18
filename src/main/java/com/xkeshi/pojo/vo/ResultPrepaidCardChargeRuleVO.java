package com.xkeshi.pojo.vo;

import java.math.BigDecimal;

/**
 * 预付卡充值规则VO
 * Created by david-y on 2015/1/20.
 */
public class ResultPrepaidCardChargeRuleVO {
    private Long id;
    private BigDecimal chargeAmount;
    private BigDecimal chargeGiftAmount;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getChargeAmount() {
        return chargeAmount;
    }

    public void setChargeAmount(BigDecimal chargeAmount) {
        this.chargeAmount = chargeAmount;
    }

    public BigDecimal getChargeGiftAmount() {
        return chargeGiftAmount;
    }

    public void setChargeGiftAmount(BigDecimal chargeGiftAmount) {
        this.chargeGiftAmount = chargeGiftAmount;
    }
}
