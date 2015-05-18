package com.xkeshi.pojo.po;

import javax.persistence.Column;
import javax.persistence.Table;
import java.math.BigDecimal;

/**
 *
 * 预付卡规则
 *
 * <br>Author: David <br>
 * 2015/1/6.
 */
@Table(name = "prepaid_card_charge_rules")
public class PrepaidCardChargeRules extends Base {

    @Column(name = "business_id")
    private Long businessId;
    @Column(name = "business_type_id")
    private Long businessTypeId;
    @Column(name = "is_initial")
    private Boolean isInitial;
    @Column(name = "member_type_id")
    private Long memberTypeId;
    @Column(name = "charge_amount")
    private BigDecimal chargeAmount;
    @Column(name = "charge_gift_type_id")
    private Long chargeGiftTypeId;
    @Column(name = "charge_gift_amount")
    private BigDecimal chargeGiftAmount;

    private String memberTypeName;

    private BigDecimal discount;

    public String getMemberTypeName() {
        return memberTypeName;
    }

    public void setMemberTypeName(String memberTypeName) {
        this.memberTypeName = memberTypeName;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public Long getBusinessId() {
        return businessId;
    }

    public void setBusinessId(Long businessId) {
        this.businessId = businessId;
    }

    public Long getBusinessTypeId() {
        return businessTypeId;
    }

    public void setBusinessTypeId(Long businessTypeId) {
        this.businessTypeId = businessTypeId;
    }

    public Boolean getIsInitial() {
        return isInitial;
    }

    public void setIsInitial(Boolean isInitial) {
        this.isInitial = isInitial;
    }

    public Long getMemberTypeId() {
        return memberTypeId;
    }

    public void setMemberTypeId(Long memberTypeId) {
        this.memberTypeId = memberTypeId;
    }

    public BigDecimal getChargeAmount() {
        return chargeAmount;
    }

    public void setChargeAmount(BigDecimal chargeAmount) {
        this.chargeAmount = chargeAmount;
    }

    public Long getChargeGiftTypeId() {
        return chargeGiftTypeId;
    }

    public void setChargeGiftTypeId(Long chargeGiftTypeId) {
        this.chargeGiftTypeId = chargeGiftTypeId;
    }

    public BigDecimal getChargeGiftAmount() {
        return chargeGiftAmount;
    }

    public void setChargeGiftAmount(BigDecimal chargeGiftAmount) {
        this.chargeGiftAmount = chargeGiftAmount;
    }
}
