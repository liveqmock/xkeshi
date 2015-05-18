package com.xkeshi.pojo.vo;


import java.util.List;

/**
 * Created by david-y on 2015/1/7.
 */
public class PrepaidCardChargeRulesListVO {

    private Long memberTypeId;
    private String memberTypeName;
    private String discount;
    private List<PrepaidCardChargeRulesVO> firstChargeRuleList;
    private List<PrepaidCardChargeRulesVO> rechargeRuleList;

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public Long getMemberTypeId() {
        return memberTypeId;
    }

    public void setMemberTypeId(Long memberTypeId) {
        this.memberTypeId = memberTypeId;
    }

    public String getMemberTypeName() {
        return memberTypeName;
    }

    public void setMemberTypeName(String memberTypeName) {
        this.memberTypeName = memberTypeName;
    }

    public List<PrepaidCardChargeRulesVO> getFirstChargeRuleList() {
        return firstChargeRuleList;
    }

    public void setFirstChargeRuleList(List<PrepaidCardChargeRulesVO> firstChargeRuleList) {
        this.firstChargeRuleList = firstChargeRuleList;
    }

    public List<PrepaidCardChargeRulesVO> getRechargeRuleList() {
        return rechargeRuleList;
    }

    public void setRechargeRuleList(List<PrepaidCardChargeRulesVO> rechargeRuleList) {
        this.rechargeRuleList = rechargeRuleList;
    }
}
