package com.xkeshi.pojo.vo.param;

/**
 *
 * 预付卡充值订单参数
 * Created by david-y on 2015/1/21.
 */
public class PrepaidCardChargeParam {
    Long memberId;
    Long ruleId;

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public Long getRuleId() {
        return ruleId;
    }

    public void setRuleId(Long ruleId) {
        this.ruleId = ruleId;
    }
}
