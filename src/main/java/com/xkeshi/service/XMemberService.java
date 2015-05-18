package com.xkeshi.service;

import com.xkeshi.dao.MemberDAO;
import com.xkeshi.dao.MemberTypeDAO;
import com.xkeshi.dao.MerchantDAO;
import com.xkeshi.dao.ShopDAO;
import com.xkeshi.pojo.po.Member;
import com.xkeshi.pojo.po.MemberType;
import com.xkeshi.pojo.vo.MemberInfoVO;
import com.xkeshi.pojo.vo.MemberTofuVO;
import com.xkeshi.pojo.vo.MemberTypeVO;
import com.xpos.common.entity.face.Business;
import org.apache.commons.lang3.StringUtils;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by david-y on 2015/1/8.
 */
@Service
public class XMemberService {

    @Autowired(required = false)
    private MemberTypeDAO memberTypeDAO;
    @Autowired(required = false)
    private MerchantDAO merchantDAO;
    @Autowired(required = false)
    private MemberDAO memberDAO;


    @Autowired(required = false)
    private ShopDAO shopDAO;

    @Autowired
    private ElectronicCouponService electronicCouponService;

    @Autowired
    private PrepaidService prepaidService;

    @Autowired
    private XMerchantService xMerchantService;

    @Autowired
    private XShopService shopService;



    @Autowired
    private Mapper dozerMapper;


    public List<MemberTypeVO> getMemberTypeList(Long businessId, Business.BusinessType businessType) {
        List<MemberType> memberTypeList = null;
        if (businessType == Business.BusinessType.MERCHANT) {
            memberTypeList = memberTypeDAO.getMemberTypeListByMerchantId(businessId);
        } else if (businessType == Business.BusinessType.SHOP) {
            //是否是独立商户
            if (shopDAO.hasMerchant(businessId)) {
                //检查是否集团统一管理
                if (merchantDAO.checkMemberCentralManagementByShopId(businessId)) {
                    Long merchantId = shopDAO.getMerchantId(businessId);
                    //折扣统一管理
                    if (merchantDAO.checkDiscountCentralManagementByShopId(businessId)) {
                        memberTypeList = memberTypeDAO.getMemberTypeListByMerchantId(merchantId);
                    } else { //折扣非统一管理
                        memberTypeList = memberTypeDAO.getCustomDiscountMemberTypeListByShopId(businessId);
                    }
                } else {
                    memberTypeList = memberTypeDAO.getMemberTypeListByShopId(businessId);
                }
            } else {
                memberTypeList = memberTypeDAO.getMemberTypeListByShopId(businessId);
            }
        }
        List<MemberTypeVO> voList = new ArrayList<>();
        if (memberTypeList != null) {
            for (MemberType po : memberTypeList) {
                voList.add(dozerMapper.map(po, MemberTypeVO.class));
            }
        }
        return voList;
    }

    /**
     * 获取会员豆腐干信息
     * <p/>
     * Gets member tofu info.
     *
     * @param mobileNumber the mobile number
     * @param shopId       the shop id
     * @return the member tofu info
     */
    public MemberTofuVO getMemberTofuInfo(String mobileNumber, Long shopId) {


    	//获取会员基本信息
    	MemberInfoVO memberInfoVO = getMemberInfoByMobileNumber(mobileNumber, shopId);
    	if (memberInfoVO == null) {
			return null;
		}

        //获取会员等级信息
        MemberTypeVO memberTypeVO = getMemberTypeByMobileNumberAndShopId(mobileNumber, shopId);

        //获取电子券数量
        Integer electronicCouponCount = electronicCouponService.getElectronicCouponCountByMobileNumberAndShopId(mobileNumber, shopId);

        //获取预付卡余额
        BigDecimal prepaidCardBalance = getPrepaidCardBalance(mobileNumber, shopId);

        MemberTofuVO vo = new MemberTofuVO();
        vo.setMemberInfo(memberInfoVO);
        vo.setMemberType(memberTypeVO);
        vo.setElectronicCouponCount(electronicCouponCount);
        vo.setPrepaidCardBalance(prepaidCardBalance);
        return vo;
    }

    private BigDecimal getPrepaidCardBalance(String mobileNumber, Long shopId) {
        BigDecimal prepaidCardBalance;
        //是否集团统一管理会员
        Boolean central = merchantDAO.checkMemberCentralManagementByShopId(shopId);
        Long merchantId = shopService.getMerchantId(shopId);
        if (central) {
            prepaidCardBalance = prepaidService.getBalanceByMobileNumberAndMerchantId(mobileNumber, merchantId);
        }else{
            prepaidCardBalance = prepaidService.getBalanceByMobileNumberAndShopId(mobileNumber, shopId);
        }

        if (prepaidCardBalance != null && prepaidCardBalance.compareTo(BigDecimal.ZERO) == 0) {
            BigDecimal chargeAmount;
            if (central) {
                chargeAmount = prepaidService.sumChargeAmountByMobileNumberAndMerchantId(mobileNumber, merchantId);
            }else{
                chargeAmount = prepaidService.sumChargeAmountByMobileNumberAndShopId(mobileNumber, shopId);
            }

            if (chargeAmount != null && chargeAmount.compareTo(BigDecimal.ZERO) > 0) {
                prepaidCardBalance = BigDecimal.ZERO;
            } else {
                prepaidCardBalance = null;
            }
        }
        return prepaidCardBalance;
    }


    public MemberInfoVO getMemberInfoByMobileNumber(String mobileNumber,Long shopId) {
        //是否集团统一管理会员
        Boolean central = merchantDAO.checkMemberCentralManagementByShopId(shopId);
        Member po;
        if (central) {
            Long merchantId = shopService.getMerchantId(shopId);
            po = memberDAO.getInfoByMobileNumberAndMerchantId(mobileNumber, merchantId);
        }else {
            po = memberDAO.getInfoByMobileNumberAndShopId(mobileNumber,shopId);
        }
        if (po != null){
            return dozerMapper.map(po, MemberInfoVO.class);
        }
        return null;
    }

    /**
     *
     * 获取会员等级信息
     *
     * Gets member type by mobile number and shop id.
     *
     * @param mobileNumber the mobile number
     *@param shopId the shop id  @return the member type by mobile number and shop id
     */
    public MemberTypeVO getMemberTypeByMobileNumberAndShopId(String mobileNumber,  Long shopId) {

        Member memberPO;

        //是否集团统一管理会员
        Boolean central = merchantDAO.checkMemberCentralManagementByShopId(shopId);
        if (central) {
            Long merchantId = shopService.getMerchantId(shopId);
            memberPO = memberDAO.getInfoByMobileNumberAndMerchantId(mobileNumber, merchantId);
        } else {
            memberPO = memberDAO.getInfoByMobileNumberAndShopId(mobileNumber, shopId);

        }


        if (memberPO == null) {
            return null;
        }
        Long memberTypeId = memberPO.getMemberTypeId();

        MemberType po;
        //是否是集团统一管理会员
        boolean centralManagementMember = memberTypeDAO.checkCentralManagementMemberByShopId(shopId);
        if (centralManagementMember) {
            po = memberTypeDAO.getCentralManagementMemberTypeByMemberTypeId(memberTypeId);
            //是否是集团统一管理折扣
            boolean centralManagementDiscount = memberTypeDAO.checkCentralManagementDiscountByShopId(shopId);
            if (!centralManagementDiscount) {
                //获取集团统一管理会员折扣时的折扣
                BigDecimal discount = memberTypeDAO.getMemberShopDiscount(memberTypeId, shopId);
                po.setDiscount(discount);
            }
        } else {
            po = memberTypeDAO.getNotCentralManagementMemberTypeByMemberTypeId(memberTypeId);
        }
        if (po != null) {
            MemberTypeVO vo = dozerMapper.map(po, MemberTypeVO.class);
            vo.setDiscount(po.getDiscount() != null ? po.getDiscount().multiply(BigDecimal.valueOf(10)) : null);
            return vo;
        }
        return null;
    }


    /**
     *
     * 获取会员等级信息
     *
     * Gets member type by mobile number and shop id.
     *
     * @param memberId the mobile number
     * @param shopId the shop id
     * @return the member type by mobile number and shop id
     */
    public MemberTypeVO getMemberTypeByMemberIdAndShopId(Long memberId, Long shopId) {


        Long memberTypeId = memberTypeDAO.getMemberTypeIdByMemberId(memberId);

        MemberType po;
        //是否是集团统一管理会员
        boolean centralManagementMember = memberTypeDAO.checkCentralManagementMemberByShopId(shopId);
        if (centralManagementMember) {
            po = memberTypeDAO.getCentralManagementMemberTypeByMemberTypeId(memberTypeId);
            //是否是集团统一管理折扣
            boolean centralManagementDiscount = memberTypeDAO.checkCentralManagementDiscountByShopId(shopId);
            if (!centralManagementDiscount) {
                //获取集团统一管理会员折扣时的折扣
                BigDecimal discount = memberTypeDAO.getMemberShopDiscount(memberTypeId, shopId);
                po.setDiscount(discount);
            }
        } else {
            po = memberTypeDAO.getNotCentralManagementMemberTypeByMemberTypeId(memberTypeId);
        }
        if (po != null) {
            MemberTypeVO vo = dozerMapper.map(po, MemberTypeVO.class);
            vo.setDiscount(po.getDiscount() != null? po.getDiscount().multiply(BigDecimal.valueOf(10)) : null);
            return vo;
        }
        return null;
    }

    /**
     *
     * 获取会员折扣
     *
     */
    public BigDecimal getMemberDiscountByMemberIdAndShopId(Long memberId,Long shopId) {
        //获取会员的businessType
        String businessType = memberDAO.getBusinessTypeById(memberId);

        if (StringUtils.equals(businessType, "MERCHANT")) {
            Long merchantId = shopService.getMerchantId(shopId);
            Long businessId = memberDAO.getBusinessIdById(memberId);
            if (!merchantId.equals(businessId)) {
                return null;
            }

            //是否统一管理折扣
            boolean centralManagementDiscount = memberTypeDAO.checkCentralManagementDiscountByShopId(shopId);
            Long merchantMemberTypeId = memberDAO.getMemberTypeIdById(memberId);
            if (centralManagementDiscount){
                return memberTypeDAO.getDiscountByMerchantMemberTypeId(merchantMemberTypeId);
            }else {
                //集团会员类型ID
                return memberTypeDAO.getMerchantShopDiscountByMemberTypeIdAndShopId(merchantMemberTypeId, shopId);
            }
        } else if (StringUtils.equals(businessType, "SHOP")) {
            Long memberTypeId = memberTypeDAO.getMemberTypeIdByMemberId(memberId);
            return memberTypeDAO.getDiscountByShopMemberTypeId(memberTypeId);
        }
        return null;

    }


}
