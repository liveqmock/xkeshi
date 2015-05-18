package com.xkeshi.dao;

import com.xkeshi.pojo.po.Member;
import org.apache.ibatis.annotations.Param;

/**
 * Created by david-y on 2015/1/19.
 */
public interface MemberDAO {


    Member getInfoByMobileNumberAndShopId(@Param("mobileNumber") String mobileNumber, @Param("shopId") Long shopId);

    Member getInfoByMobileNumberAndMerchantId(@Param("mobileNumber") String mobileNumber, @Param("merchantId") Long merchantId);

    Long getMemberTypeIdById(@Param("memberId") Long memberId);

    String getBusinessTypeById(@Param("memberId") Long memberId);

    Long getBusinessIdById(@Param("memberId") Long memberId);

    Long getMemberShopIdById(@Param("memberId") Long memberId);



    Member getById(@Param("memberId") Long memberId);

    void updateMemberTypeIdByMemberId(@Param("memberId") Long memberId, @Param("memberTypeId") Long memberTypeId);

    void updateMemberTypeIdByPrepaidChargeCode(@Param("prepaidOrder") String prepaidOrder, @Param("memberTypeId") Long memberTypeId);
}
