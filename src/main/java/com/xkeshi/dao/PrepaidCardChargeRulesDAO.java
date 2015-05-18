package com.xkeshi.dao;

import com.xkeshi.pojo.po.PrepaidCardChargeRules;
import com.xkeshi.pojo.vo.PrepaidCardChargeRulesListVO;
import com.xkeshi.pojo.vo.PrepaidCardChargeRulesVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <br>Author: David <br>
 * 2015/1/6.
 */
public interface PrepaidCardChargeRulesDAO extends BaseDAO<PrepaidCardChargeRules> {


    boolean hasRules(@Param("businessTypeId") Long businessTypeId, @Param("businessId") Long businessId);

    List<PrepaidCardChargeRules> getRules(@Param("businessTypeId") Long businessTypeId, @Param("businessId") Long businessId);

    List<PrepaidCardChargeRulesListVO> getMemberTypeListByMerchantId(@Param("businessId") Long businessId);

    List<PrepaidCardChargeRulesVO> getFirstChargeRuleList(@Param("businessTypeId") Long businessTypeId, @Param("businessId") Long businessId, @Param("memberTypeId") Long memberTypeId);
    boolean hasFirstChargeRuleList(@Param("businessTypeId") Long businessTypeId, @Param("businessId") Long businessId, @Param("memberTypeId") Long memberTypeId);

    List<PrepaidCardChargeRulesVO> getRechargeRuleList(@Param("businessTypeId") Long businessTypeId, @Param("businessId") Long businessId, @Param("memberTypeId") Long memberTypeId);
    boolean hasRechargeRuleList(@Param("businessTypeId") Long businessTypeId, @Param("businessId") Long businessId, @Param("memberTypeId") Long memberTypeId);

    List<PrepaidCardChargeRulesListVO> getMemberTypeListByShopId(@Param("businessId") Long businessId);

    List<PrepaidCardChargeRulesListVO> getMemberTypeListByMerchantIdAndDisCountShopId(@Param("merchantId") Long merchantId, @Param("businessId") Long businessId);

    void clearMemberRules(@Param("businessId") Long businessId, @Param("businessType") Long businessType, @Param("memberTypeId") Long memberTypeId, @Param("isInitial") boolean isInitial);

    List<PrepaidCardChargeRules> getFirstChargeRulesByMemberTypeId(@Param("memberTypeId") Long memberTypeId);

    List<PrepaidCardChargeRules> getRechargeRulesByMemberTypeId(@Param("memberTypeId") Long memberTypeId);


    /**
     * 是否有续充规则
     *
     * Has recharge rule list by rule id.
     *
     * @param ruleId the rule id
     * @return the boolean
     */
    boolean hasRechargeRuleListByRuleId(@Param("ruleId") Long ruleId);

    PrepaidCardChargeRules getByPrepaidChargeCode(@Param("prepaidOrder") String prepaidOrder);

}
