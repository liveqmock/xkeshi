package com.xkeshi.pojo.po;

import java.math.BigDecimal;

/**
 * Created by david-y on 2015/1/8.
 */
public class MemberType {
    private Long id;
    private String name;
    private BigDecimal discount;

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

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }
}
