package com.xkeshi.dao;

import com.xkeshi.common.db.Query;
import com.xkeshi.pojo.po.PrepaidCard;
import com.xkeshi.pojo.vo.param.PrepaidListParam;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * <br>Author: David <br>
 * 2015/1/6.
 */
public interface PrepaidDAO extends BaseDAO<PrepaidCard> {

    int countAllPrepaidList(@Param("param") PrepaidListParam param);


    List<PrepaidCard> queryMemberCentralPrepaidList(@Param("query") Query query, @Param("param") PrepaidListParam param);

    List<PrepaidCard> queryNotMemberCentralPrepaidList(@Param("query") Query query, @Param("param") PrepaidListParam param);


    BigDecimal getBalanceByMobileNumberAndShopId(@Param("mobileNumber") String mobileNumber, @Param("shopId") Long shopId);
    BigDecimal getBalanceByMobileNumberAndMerchantId(@Param("mobileNumber") String mobileNumber, @Param("merchantId") Long merchantId);


    void updatePasswordByShopId(@Param("memberId") Long memberId, @Param("shopId") Long shopId, @Param("password") String password, @Param("salt") String salt);
    void updatePasswordByMerchantId(@Param("memberId") Long memberId, @Param("merchantId") Long merchantId, @Param("password") String password, @Param("salt") String salt);

    int countPrepaidCardChargeRecordList(@Param("date") Date date, @Param("shopId") Long shopId);


    BigDecimal sumPrepaidCardChargeRecordList(@Param("date") Date date, @Param("shopId") Long shopId);

    PrepaidCard getPrepaidCardInfoByMemberIdAndShopId(@Param("id") Long id, @Param("shopId") Long shopId);
    PrepaidCard getPrepaidCardInfoByMemberIdAndMerchantId(@Param("id") Long id, @Param("merchantId") Long merchantId);

    int countAllMemberCentralPrepaidList(@Param("param") PrepaidListParam param);

    int countAllNotMemberCentralPrepaidList(@Param("param") PrepaidListParam param);

    void updatePrepaidCardEnable(@Param("prepaidCardId") Long prepaidCardId, @Param("enable") Boolean enable);

    Long getIdByMemberIdAndShopId(@Param("memberId") Long memberId, @Param("shopId") Long shopId);

    Long getIdByMemberIdAndMerchantId(@Param("memberId") Long memberId, @Param("merchantId") Long merchantId);

    PrepaidCard getByMemberId(@Param("memberId") Long memberId);

    String getSaltById(@Param("prepaidCardId") Long prepaidCardId);

    Long getIdByPrepaidCardChargeOrderNumber(@Param("orderNumber") String orderNumber);

    void initPrepaidCardPassword(@Param("prepaidCardId") Long prepaidCardId, @Param("password") String password, @Param("salt") String salt);

    String getMemberMobileNumberById(@Param("prepaidCardId") Long prepaidCardId);

    PrepaidCard getByPrepaidCardChargeOrderNumber(@Param("orderNumber") String orderNumber);

    PrepaidCard getByOrderNumber(@Param("orderNumber") String orderNumber);

    BigDecimal sumChargeAmountByMobileNumberAndShopId(@Param("mobileNumber") String mobileNumber, @Param("shopId") Long shopId);
    BigDecimal sumChargeAmountByMobileNumberAndMerchantId(@Param("mobileNumber") String mobileNumber, @Param("merchantId") Long merchantId);

    void updateStatusByChargeOrderCode(@Param("orderNumber") String orderNumber, @Param("status") int status);
}
