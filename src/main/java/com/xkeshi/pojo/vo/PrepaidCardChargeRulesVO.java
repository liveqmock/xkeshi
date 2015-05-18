package com.xkeshi.pojo.vo;

import com.xkeshi.utils.Tools;

/**
 * Created by david-y on 2015/1/7.
 */
public class PrepaidCardChargeRulesVO {

    private Long ruleId;
    private String chargeAmount;
    private Integer chargeGiftTypeId;
    private String chargeGiftAmount;
    private Boolean isInitial;
    private Long memberTypeId;

    public Long getRuleId() {
        return ruleId;
    }

    public void setRuleId(Long ruleId) {
        this.ruleId = ruleId;
    }

    public String getChargeAmount() {
        return chargeAmount;
    }

    public void setChargeAmount(String chargeAmount) {
        this.chargeAmount = Tools.trimZero(chargeAmount);
    }

    public String getChargeGiftAmount() {
        return chargeGiftAmount;
    }

    public void setChargeGiftAmount(String chargeGiftAmount) {
        this.chargeGiftAmount = Tools.trimZero(chargeGiftAmount);
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

    public Integer getChargeGiftTypeId() {
        return chargeGiftTypeId;
    }

    public void setChargeGiftTypeId(Integer chargeGiftTypeId) {
        this.chargeGiftTypeId = chargeGiftTypeId;
    }
}
