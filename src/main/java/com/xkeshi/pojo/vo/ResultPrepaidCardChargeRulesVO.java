package com.xkeshi.pojo.vo;

import java.util.List;

/**
 * 预付卡充值规则返回结果VO
 * <p/>
 * Created by david-y on 2015/1/20.
 */
public class ResultPrepaidCardChargeRulesVO {
    private List<ResultPrepaidCardChargeRuleVO> rules;

    public List<ResultPrepaidCardChargeRuleVO> getRules() {
        return rules;
    }

    public void setRules(List<ResultPrepaidCardChargeRuleVO> rules) {
        this.rules = rules;
    }
}
