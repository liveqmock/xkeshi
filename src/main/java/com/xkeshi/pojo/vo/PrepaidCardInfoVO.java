package com.xkeshi.pojo.vo;

import java.math.BigDecimal;

/**
 * 预付卡信息VO
 * Created by david-y on 2015/1/20.
 */
public class PrepaidCardInfoVO {
    private Long id;
    private BigDecimal balance;
    private String expireDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public String getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(String expireDate) {
        this.expireDate = expireDate;
    }
}
