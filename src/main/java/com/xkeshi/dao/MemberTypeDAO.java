package com.xkeshi.dao;

import com.xkeshi.pojo.po.MemberType;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by david-y on 2015/1/10.
 */
public interface MemberTypeDAO {
    List<MemberType> getMemberTypeListByMerchantId(@Param("merchantId") Long merchantId);

    /**
     * 获取会员会员折扣的会员等级列表
     */
    List<MemberType> getCustomDiscountMemberTypeListByShopId(@Param("shopId") Long shopId);

    List<MemberType> getMemberTypeListByShopId(@Param("shopId") Long shopId);


    /**
     *
     * 获取集团是否统一管理会员等级
     * Check central management member by shop id.
     *
     * @param shopId the shop id
     * @return the boolean
     */
    boolean checkCentralManagementMemberByShopId(@Param("shopId") Long shopId);

    /**
     *
     * 获取集团是否统一管理会员折扣
     * Check central management discount by shop id.
     *
     * @param shopId the shop id
     * @return the boolean
     */
    boolean checkCentralManagementDiscountByShopId(@Param("shopId") Long shopId);

    /**
     * 通过会员等级ID查找集团统一管理的会员等级信息
     *
     */
    MemberType getCentralManagementMemberTypeByMemberTypeId(@Param("memberTypeId") Long memberTypeId);

    /**
     * 通过会员等级ID查找集团非统一管理的会员等级信息
     *
     */
    MemberType getNotCentralManagementMemberTypeByMemberTypeId(@Param("memberTypeId") Long memberTypeId);

    BigDecimal getMemberShopDiscount(@Param("memberTypeId") Long memberTypeId, @Param("shopId") Long shopId);

    /**
     * 获取独立商户的折扣
     * Gets discount by shop id.
     *
     *
     * @param memberTypeId
     * @return the discount by shop id
     */
    BigDecimal getDiscountByShopMemberTypeId(@Param("memberTypeId") Long memberTypeId);

    /**
     *
     * 获取非统一管理会员折扣时的商户会员折扣
     * Gets un central management discount.
     *
     * @param memberId the member id
     * @param shopId the shop id
     * @return the un central management discount
     */
    BigDecimal getUnCentralManagementDiscount(@Param("memberId") Long memberId, @Param("shopId") Long shopId);

    /**
     *
     * 获取统一管理会员折扣的折扣
     *
     * Gets central management discount.
     *
     * @param memberId the member id
     * @param merchantId the merchant id
     * @return the central management discount
     */
    BigDecimal getCentralManagementDiscount(@Param("memberId") Long memberId, @Param("merchantId") Long merchantId);

    /**
     * 获取集团统一管理的折扣
     *
     * Gets discount by merchant id.
     *
     * @param merchantId the merchant id
     * @return the discount by merchant id
     */
    BigDecimal getDiscountByMerchantMemberTypeId(@Param("merchantMemberTypeId") Long merchantMemberTypeId);


    Long getIdByMerchantId(@Param("merchantId") Long merchantId);

    /**
     * 获取集团子商户的折扣
     *
     * Gets merchant shop discount by member type id and shop id.
     *
     * @param merchantMemberTypeId the merchant member type id
     * @param shopId the shop id
     * @return the merchant shop discount by member type id and shop id
     */
    BigDecimal getMerchantShopDiscountByMemberTypeIdAndShopId(@Param("merchantMemberTypeId") Long merchantMemberTypeId, @Param("shopId") Long shopId);

    Long getMemberTypeIdByMemberId(@Param("memberId") Long memberId);
}
