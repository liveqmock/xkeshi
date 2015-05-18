package com.xkeshi.service;

import com.drongam.hermes.entity.SMS;
import com.xkeshi.common.db.Query;
import com.xkeshi.common.em.OrderPaymentStatus;
import com.xkeshi.common.em.result.PrepaidCardChargeOrderResult;
import com.xkeshi.common.globality.GlobalSource;
import com.xkeshi.dao.*;
import com.xkeshi.pojo.po.*;
import com.xkeshi.pojo.vo.*;
import com.xkeshi.pojo.vo.param.PrepaidCardChargeParam;
import com.xkeshi.pojo.vo.param.PrepaidChargeListParam;
import com.xkeshi.pojo.vo.param.PrepaidListParam;
import com.xkeshi.pojo.vo.result.CreateChargeOrderResultVO;
import com.xkeshi.utils.CodeUtil;
import com.xkeshi.utils.DateUtils;
import com.xkeshi.utils.EncryptionUtil;
import com.xkeshi.utils.Tools;
import com.xpos.common.entity.face.Business;
import com.xpos.common.entity.face.Business.BusinessType;
import com.xpos.common.service.SMSService;
import org.apache.commons.lang3.StringUtils;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <br>Author: David <br>
 * 2015/1/6.
 */
@Service
public class PrepaidService {


    @Autowired(required = false)
    private Mapper dozerMapper;

    @Autowired(required = false)
    private PrepaidDAO prepaidDAO;
    @Autowired(required = false)
    private MerchantDAO merchantDAO;
    @Autowired(required = false)
    private ShopDAO shopDAO;

    @Autowired(required = false)
    private PrepaidCardChargeOrderDAO prepaidCardChargeOrderDAO;
    @Autowired(required = false)
    private PrepaidCardTransactionDAO prepaidCardTransactionDAO;

    @Autowired
    private SMSService smsService;



    @Autowired(required = false)
    private ShopPrepaidCardInfoDAO shopPrepaidCardInfoDAO;


    @Autowired(required = false)
    private MemberDAO memberDAO;
    @Autowired(required = false)
    private MemberTypeDAO memberTypeDAO;
    @Autowired
    private XShopService shopService;

    @Autowired(required = false)
    private PrepaidCardMerchantShopDAO prepaidCardMerchantShopDAO;

    @Autowired(required = false)
    private PrepaidCardChargeRulesDAO prepaidCardChargeRulesDAO;

    @Autowired
    private XMerchantService xMerchantService;

    @Autowired
    private XMemberService xMemberService;

    @Autowired
    private XShopService xShopService;



    public List<PrepaidCardListVO> queryMemberCentralPrepaidList(Query query, PrepaidListParam param) {
        List<PrepaidCard> poList = prepaidDAO.queryMemberCentralPrepaidList(query, param);
        List<PrepaidCardListVO> voList = new ArrayList<>();
        for (PrepaidCard po : poList) {
            PrepaidCardListVO vo = new PrepaidCardListVO();
            dozerMapper.map(po, vo);
            voList.add(vo);
        }
        return voList;
    }

    public List<PrepaidCardListVO> queryNotMemberCentralPrepaidList(Query query, PrepaidListParam param) {
        List<PrepaidCard> poList = prepaidDAO.queryNotMemberCentralPrepaidList(query, param);
        List<PrepaidCardListVO> voList = new ArrayList<>();
        for (PrepaidCard po : poList) {
            PrepaidCardListVO vo = new PrepaidCardListVO();
            dozerMapper.map(po, vo);
            voList.add(vo);
        }
        return voList;
    }


    /**
     * 检查是否具有适用商户
     * Has shops by merchant id.
     *
     * @param merchantId the merchant id
     * @return the boolean
     */
    public boolean hasShopsByMerchantId(Long merchantId) {
        return prepaidCardMerchantShopDAO.hasShopsByMerchantId(merchantId);
    }

    /**
     * 检查是否有设置预付卡规则
     *
     * @param businessTypeId
     * @param businessId
     * @return
     */
    public boolean hasRules(Long businessTypeId, Long businessId) {
        return prepaidCardChargeRulesDAO.hasRules(businessTypeId, businessId);
    }

    public List<ShopLiteVO> getShopListByMerchantId(Long merchantId) {
        return prepaidCardMerchantShopDAO.getShopListByMerchantId(merchantId);
    }

    /**
     * 获取预付卡充值规则
     * <p/>
     * Gets prepaid card charge rules list.
     *
     * @param businessTypeId the business type id
     * @param businessId     the business id
     * @return the prepaid card charge rules list
     */
    public List<PrepaidCardChargeRulesListVO> getPrepaidCardChargeRulesList(Long businessTypeId, Long businessId) {
        List<PrepaidCardChargeRulesListVO> ruleList = new ArrayList<>();
        //集团
        if (GlobalSource.getNameByID(GlobalSource.metaBusinessTypeList, businessTypeId).equals("集团")) {
            //获取会员等级列表
            List<MemberTypeVO> memberTypeList = xMemberService.getMemberTypeList(businessId, Business.BusinessType.MERCHANT);
            if (memberTypeList != null) {
                for (MemberTypeVO memberTypeVO : memberTypeList) {
                    ruleList.add(dozerMapper.map(memberTypeVO, PrepaidCardChargeRulesListVO.class));
                }
            }

//                ruleList = prepaidCardChargeRulesDAO.getMemberTypeListByMerchantId(businessId);
            for (PrepaidCardChargeRulesListVO rule : ruleList) {
                //获取首充规则列表
                List<PrepaidCardChargeRulesVO> firstChargeRuleList = prepaidCardChargeRulesDAO.getFirstChargeRuleList(businessTypeId, businessId, rule.getMemberTypeId());
                //获取续充规则列表
                List<PrepaidCardChargeRulesVO> rechargeRuleList = prepaidCardChargeRulesDAO.getRechargeRuleList(businessTypeId, businessId, rule.getMemberTypeId());
                rule.setFirstChargeRuleList(firstChargeRuleList);
                rule.setRechargeRuleList(rechargeRuleList);
            }

        } else { //商户
            if (xShopService.hasMerchant(businessId)) { //非独立商户
                //统一管理会员
                if (xMerchantService.checkMemberCentralManagementByShopId(businessId)) {
                    //获取关联集团ID
                    Long merchantId = xShopService.getMerchantId(businessId);

                    //统一管理会员折扣
                    if (xMerchantService.checkDiscountCentralManagementByShopId(businessId)) {
                        //获取会员等级列表
                        List<MemberTypeVO> memberTypeList = xMemberService.getMemberTypeList(businessId, Business.BusinessType.SHOP);
                        if (memberTypeList != null) {
                            for (MemberTypeVO memberTypeVO : memberTypeList) {
                                ruleList.add(dozerMapper.map(memberTypeVO, PrepaidCardChargeRulesListVO.class));
                            }
                        }
//                        ruleList = prepaidCardChargeRulesDAO.getMemberTypeListByMerchantId(param);
                        for (PrepaidCardChargeRulesListVO rule : ruleList) {
                            //获取首充规则列表
                            List<PrepaidCardChargeRulesVO> firstChargeRuleList = prepaidCardChargeRulesDAO.getFirstChargeRuleList(1L, merchantId, rule.getMemberTypeId());
                            //获取续充规则列表
                            List<PrepaidCardChargeRulesVO> rechargeRuleList = prepaidCardChargeRulesDAO.getRechargeRuleList(1L, merchantId, rule.getMemberTypeId());
                            rule.setFirstChargeRuleList(firstChargeRuleList);
                            rule.setRechargeRuleList(rechargeRuleList);
                        }

                    } else {
                        //获取会员等级列表
                        List<MemberTypeVO> memberTypeList = xMemberService.getMemberTypeList(businessId, Business.BusinessType.SHOP);
                        if (memberTypeList != null) {
                            for (MemberTypeVO memberTypeVO : memberTypeList) {
                                ruleList.add(dozerMapper.map(memberTypeVO, PrepaidCardChargeRulesListVO.class));
                            }
                        }
//                        ruleList = prepaidCardChargeRulesDAO.getMemberTypeListByMerchantIdAndDisCountShopId(param,businessId);
                        for (PrepaidCardChargeRulesListVO rule : ruleList) {
                            //获取首充规则列表
                            List<PrepaidCardChargeRulesVO> firstChargeRuleList = prepaidCardChargeRulesDAO.getFirstChargeRuleList(1L, merchantId, rule.getMemberTypeId());
                            //获取续充规则列表
                            List<PrepaidCardChargeRulesVO> rechargeRuleList = prepaidCardChargeRulesDAO.getRechargeRuleList(1L, merchantId, rule.getMemberTypeId());
                            rule.setFirstChargeRuleList(firstChargeRuleList);
                            rule.setRechargeRuleList(rechargeRuleList);
                        }
                    }

                } else {
                    //获取会员等级列表
//                    ruleList = prepaidCardChargeRulesDAO.getMemberTypeListByShopId(businessId);
                    List<MemberTypeVO> memberTypeList = xMemberService.getMemberTypeList(businessId, Business.BusinessType.SHOP);
                    if (memberTypeList != null) {
                        for (MemberTypeVO memberTypeVO : memberTypeList) {
                            ruleList.add(dozerMapper.map(memberTypeVO, PrepaidCardChargeRulesListVO.class));
                        }
                    }
                    for (PrepaidCardChargeRulesListVO rule : ruleList) {
                        //获取首充规则列表
                        List<PrepaidCardChargeRulesVO> firstChargeRuleList = prepaidCardChargeRulesDAO.getFirstChargeRuleList(businessTypeId, businessId, rule.getMemberTypeId());
                        //获取续充规则列表
                        List<PrepaidCardChargeRulesVO> rechargeRuleList = prepaidCardChargeRulesDAO.getRechargeRuleList(businessTypeId, businessId, rule.getMemberTypeId());
                        rule.setFirstChargeRuleList(firstChargeRuleList);
                        rule.setRechargeRuleList(rechargeRuleList);
                    }
                }
            } else { //独立商户
                //获取会员等级列表
//                ruleList = prepaidCardChargeRulesDAO.getMemberTypeListByShopId(businessId);
                List<MemberTypeVO> memberTypeList = xMemberService.getMemberTypeList(businessId, Business.BusinessType.SHOP);
                if (memberTypeList != null) {
                    for (MemberTypeVO memberTypeVO : memberTypeList) {
                        ruleList.add(dozerMapper.map(memberTypeVO, PrepaidCardChargeRulesListVO.class));
                    }
                }
                for (PrepaidCardChargeRulesListVO rule : ruleList) {
                    //获取首充规则列表
                    List<PrepaidCardChargeRulesVO> firstChargeRuleList = prepaidCardChargeRulesDAO.getFirstChargeRuleList(businessTypeId, businessId, rule.getMemberTypeId());
                    //获取续充规则列表
                    List<PrepaidCardChargeRulesVO> rechargeRuleList = prepaidCardChargeRulesDAO.getRechargeRuleList(businessTypeId, businessId, rule.getMemberTypeId());
                    rule.setFirstChargeRuleList(firstChargeRuleList);
                    rule.setRechargeRuleList(rechargeRuleList);
                }
            }
        }

        return ruleList;
    }

    /**
     * 检查是否有预付卡充值规则
     * <p/>
     *
     * @param businessTypeId the business type id
     * @param businessId     the business id
     * @return the prepaid card charge rules list
     */
    public Boolean hasPrepaidCardChargeRules(Long businessTypeId, Long businessId) {
        //集团
        if (GlobalSource.getNameByID(GlobalSource.metaBusinessTypeList, businessTypeId).equals("集团")) {
            //获取会员等级列表
            List<MemberTypeVO> memberTypeList = xMemberService.getMemberTypeList(businessId, Business.BusinessType.MERCHANT);
            for (MemberTypeVO vo : memberTypeList) {
                if (prepaidCardChargeRulesDAO.hasFirstChargeRuleList(businessTypeId, businessId, vo.getId())) {
                    return true;
                }
                if (prepaidCardChargeRulesDAO.hasRechargeRuleList(businessTypeId, businessId, vo.getId())) {
                    return true;
                }
            }
        } else { //商户
            if (xShopService.hasMerchant(businessId) && xMerchantService.checkMemberCentralManagementByShopId(businessId)) { //非独立商户
                //获取关联集团ID
                Long merchantId = xShopService.getMerchantId(businessId);

                //统一管理会员折扣
                if (xMerchantService.checkDiscountCentralManagementByShopId(businessId)) {
                    //获取会员等级列表
                    List<MemberTypeVO> memberTypeList = xMemberService.getMemberTypeList(businessId, Business.BusinessType.SHOP);

                    for (MemberTypeVO vo : memberTypeList) {
                        if (prepaidCardChargeRulesDAO.hasFirstChargeRuleList(1L, merchantId, vo.getId())) {
                            return true;
                        }
                        if (prepaidCardChargeRulesDAO.hasRechargeRuleList(1L, merchantId, vo.getId())) {
                            return true;
                        }
                    }

                } else {
                    //获取会员等级列表
                    List<MemberTypeVO> memberTypeList = xMemberService.getMemberTypeList(businessId, Business.BusinessType.SHOP);
                    for (MemberTypeVO vo : memberTypeList) {
                        if (prepaidCardChargeRulesDAO.hasFirstChargeRuleList(1L, merchantId, vo.getId())) {
                            return true;
                        }
                        if (prepaidCardChargeRulesDAO.hasRechargeRuleList(1L, merchantId, vo.getId())) {
                            return true;
                        }
                    }
                }
            } else { //独立商户或集团下非统一管理商户
                //获取会员等级列表
                List<MemberTypeVO> memberTypeList = xMemberService.getMemberTypeList(businessId, Business.BusinessType.SHOP);
                for (MemberTypeVO vo : memberTypeList) {
                    if (prepaidCardChargeRulesDAO.hasFirstChargeRuleList(businessTypeId, businessId, vo.getId())) {
                        return true;
                    }
                    if (prepaidCardChargeRulesDAO.hasRechargeRuleList(businessTypeId, businessId, vo.getId())) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * 设置预付卡集团适用的商户
     * Sets shops in merchant.
     *
     * @param merchantId the merchant id
     * @param shopIds    the shop ids
     */
    public void setShopsInMerchant(Long merchantId, Long[] shopIds) {
        //删除所有的适用商户信息
        prepaidCardMerchantShopDAO.deleteShopsByMerchantId(merchantId);
        if (shopIds != null) {
            //重新添加适用的商户信息
            for (Long shopId : shopIds) {
                prepaidCardMerchantShopDAO.insert(merchantId, shopId);
            }
        }
    }

    /**
     * 添加预付卡规则
     * <p/>
     * Insert charge rules.
     *
     * @param businessId   the business id
     * @param businessType the business type
     * @param isInitial    the is initial
     * @param memberTypeId the member type id
     * @param rules        the rules
     */
    public void insertChargeRules(Long businessId, String businessType, boolean isInitial, Long memberTypeId, String[] rules) {
        //删除老数据
        clearChargeRules(businessId, businessType, isInitial, memberTypeId);

        for (String rule : rules) {
            PrepaidCardChargeRules po = new PrepaidCardChargeRules();

            String[] ruleInfo = StringUtils.splitByWholeSeparator(rule, "_");
            BigDecimal chartAmount = Tools.getDecimal(ruleInfo[0]);
            BigDecimal chargeGiftAmount = Tools.getDecimal(ruleInfo[2]);

            po.setBusinessId(businessId);
            po.setBusinessTypeId(GlobalSource.getIDByCode(GlobalSource.metaBusinessTypeList, StringUtils.upperCase(businessType)));
            po.setChargeAmount(chartAmount);
            po.setChargeGiftTypeId(chargeGiftAmount == null ? 1L : 2L); //1无2金额
            po.setChargeGiftAmount(chargeGiftAmount);
            po.setIsInitial(isInitial);
            po.setMemberTypeId(memberTypeId);
            prepaidCardChargeRulesDAO.insert(po);
        }
    }

    public void clearChargeRules(Long businessId, String businessType, boolean isInitial, Long memberTypeId) {
        prepaidCardChargeRulesDAO.clearMemberRules(businessId,
                GlobalSource.getIDByCode(GlobalSource.metaBusinessTypeList, StringUtils.upperCase(businessType)),
                memberTypeId,
                isInitial);
    }

    /**
     * 通过手机号和商户ID获取预付卡余额
     * <p/>
     * Gets prepaid card balance by mobile number and shop id.
     *
     * @param mobileNumber the mobile number
     * @param shopId       the shop id
     * @return the prepaid card balance by mobile number and shop id
     */
    public BigDecimal getBalanceByMobileNumberAndShopId(String mobileNumber, Long shopId) {
        return prepaidDAO.getBalanceByMobileNumberAndShopId(mobileNumber, shopId);
    }

    /**
     * 通过手机号和集团ID获取预付卡余额
     * <p/>
     * Gets prepaid card balance by mobile number and merchant id.
     *
     * @param mobileNumber the mobile number
     * @param merchantId       the merchant id
     * @return the prepaid card balance by mobile number and shop id
     */
    public BigDecimal getBalanceByMobileNumberAndMerchantId(String mobileNumber, Long merchantId) {
        return prepaidDAO.getBalanceByMobileNumberAndMerchantId(mobileNumber, merchantId);
    }

    /**
     * 获取首充规则
     * <p/>
     * Gets first charge rules.
     *
     * @param memberTypeId the member type id
     * @param shopId       the param
     * @return the first charge rules
     */
    public ResultPrepaidCardChargeRulesVO getFirstChargeRules(Long memberTypeId, Long shopId) {
        //是否会员统一管理
        if (checkEnablePrepaidCardChargeRules(shopId)) return null;

        ResultPrepaidCardChargeRulesVO resultPrepaidCardChargeRulesVO = new ResultPrepaidCardChargeRulesVO();
        List<PrepaidCardChargeRulesVO> poList = null;
        if(!memberTypeDAO.checkCentralManagementMemberByShopId(shopId)){
            Long businessTypeId = GlobalSource.getIDByCode(GlobalSource.metaBusinessTypeList, BusinessType.SHOP.name());
            poList = prepaidCardChargeRulesDAO.getFirstChargeRuleList(businessTypeId, shopId, memberTypeId);
        }else{
        	Long businessTypeId = GlobalSource.getIDByCode(GlobalSource.metaBusinessTypeList, BusinessType.MERCHANT.name());
        	Long merchantId = shopService.getMerchantId(shopId);
            poList = prepaidCardChargeRulesDAO.getFirstChargeRuleList(businessTypeId, merchantId, memberTypeId);
        }
        List<ResultPrepaidCardChargeRuleVO> voList = new ArrayList<>();
        for (PrepaidCardChargeRulesVO vo : poList) {
            voList.add(dozerMapper.map(vo, ResultPrepaidCardChargeRuleVO.class));
        }
        resultPrepaidCardChargeRulesVO.setRules(voList);
        return resultPrepaidCardChargeRulesVO;
    }

    /**
     * 获取续充规则
     * <p/>
     * Gets recharge rules.
     *
     * @param mobileNumber the mobile number
     * @param shopId       the shop id
     * @return the recharge rules
     */
    public ResultPrepaidCardChargeRulesVO getRechargeRules(String mobileNumber, Long shopId) {
        if (checkEnablePrepaidCardChargeRules(shopId)) {
            return null;
        }


        //获取会员等级信息
        MemberTypeVO memberTypeVO = xMemberService.getMemberTypeByMobileNumberAndShopId(mobileNumber, shopId);
        if (memberTypeVO == null){
            return null;
        }

        ResultPrepaidCardChargeRulesVO resultPrepaidCardChargeRulesVO = new ResultPrepaidCardChargeRulesVO();
        List<PrepaidCardChargeRulesVO> poList = null;
        if(!memberTypeDAO.checkCentralManagementMemberByShopId(shopId)){
            Long businessTypeId = GlobalSource.getIDByCode(GlobalSource.metaBusinessTypeList, BusinessType.SHOP.name());
            poList = prepaidCardChargeRulesDAO.getRechargeRuleList(businessTypeId, shopId, memberTypeVO.getId());
        }else{
        	Long businessTypeId = GlobalSource.getIDByCode(GlobalSource.metaBusinessTypeList, BusinessType.MERCHANT.name());
        	Long merchantId = shopService.getMerchantId(shopId);
            poList = prepaidCardChargeRulesDAO.getRechargeRuleList(businessTypeId, merchantId, memberTypeVO.getId());
        }
        if (poList != null && poList.size() == 0){//没有续充规则，获取首充规则
            return getFirstChargeRules(memberTypeVO.getId(), shopId);
        }
        List<ResultPrepaidCardChargeRuleVO> voList = new ArrayList<>();
        for (PrepaidCardChargeRulesVO vo : poList) {
            voList.add(dozerMapper.map(vo, ResultPrepaidCardChargeRuleVO.class));
        }
        resultPrepaidCardChargeRulesVO.setRules(voList);
        return resultPrepaidCardChargeRulesVO;
    }

    /**
     * 检查商户是否具有适用的预付卡充值规则
     * @param shopId
     * @return
     */
    private boolean checkEnablePrepaidCardChargeRules(Long shopId) {
        //是否会员统一管理
        boolean centralManagementMember = memberTypeDAO.checkCentralManagementMemberByShopId(shopId);
        Long merchantId = shopService.getMerchantId(shopId);
        if (centralManagementMember) {
            //是否是集团统一管理适用商户
            Boolean checkEnableRule = prepaidCardMerchantShopDAO.checkMerchantShop(merchantId, shopId);
            if (!checkEnableRule) {
                return true;
            }
        }
        return false;
    }

    /**
     * 设置预付卡支付密码
     * Sets password by mobile number and shop id.
     *
     * @param mobileNumber  the mobile number
     * @param shopId        the param
     * @param plainPassword
     */
    public boolean setPasswordByMobileNumberAndShopId(String mobileNumber, Long shopId, String plainPassword) {
        Member member;
        String messageName; //短信用的商户名称（统一管理时，使用集团名称）
        Long businessId;
        Long businessTypeId;
        //是否会员统一管理
        boolean centralManagementMember = memberTypeDAO.checkCentralManagementMemberByShopId(shopId);
        Long merchantId = shopService.getMerchantId(shopId);
        ShopLiteVO shopLiteVO = shopService.getShopLiteById(shopId);
        if (centralManagementMember) {
            member = memberDAO.getInfoByMobileNumberAndMerchantId(mobileNumber, merchantId);
            messageName = merchantDAO.getNameById(merchantId);
            businessId = merchantId;
            businessTypeId = 1L;
        } else {
            member = memberDAO.getInfoByMobileNumberAndShopId(mobileNumber, shopId);
            messageName = shopLiteVO.getName();
            businessId = shopId;
            businessTypeId = 2L;
        }
        if (member == null) {
            return false;
        }


        //生成盐值
        String salt = EncryptionUtil.getSalt();
        String password = EncryptionUtil.encodePassword(plainPassword, salt);

        if (centralManagementMember){
            prepaidDAO.updatePasswordByMerchantId(member.getId(), merchantId, password, salt);
        }else{
            prepaidDAO.updatePasswordByShopId(member.getId(), shopId, password, salt);
        }

        String timeStr = DateUtils.formatDate(new Date(),"yyyy年MM月dd日HH时mm分");

        //发送修改成功短信
        String contact = shopLiteVO == null ? "" : shopLiteVO.getContact();
        String content = StringUtils.join("您的", messageName, "会员预付卡支付密码于", timeStr, "重设成功，如非本人操作，请联系", contact);

        //发送短信
        SMS sms = new SMS();
        sms.setMobile(mobileNumber);
        sms.setMessage(content);
        smsService.sendSMSAndDeductions(businessId ,businessTypeId,sms,null,"预付卡修改密码,发送短信" );
        return true;
    }

    /**
     * 统计预付卡充值记录条数
     * Count prepaid card charge record list.
     *
     * @param date   the date
     * @param shopId the param
     * @return the int
     */
    public int countPrepaidCardChargeRecordList(Date date, Long shopId) {
        return prepaidDAO.countPrepaidCardChargeRecordList(date, shopId);
    }

    /**
     *  统计预付卡充值总金额
     *
     * Sum prepaid card charge record list.
     *
     * @param date the date
     * @param shopId the shop id
     * @return the big decimal
     */
    public BigDecimal sumPrepaidCardChargeRecordList(Date date, Long shopId) {
        return prepaidDAO.sumPrepaidCardChargeRecordList(date, shopId);
    }

    /**
     * 获取预付卡基本信息
     * Gets prepaid card info.
     *
     * @param mobileNumber the mobile number
     * @param shopId       the shop id
     * @return the prepaid card info
     */
    public PrepaidCardInfoVO getPrepaidCardInfo(String mobileNumber, Long shopId) {
        //获取会员
        Member member;
        PrepaidCard po;
        //是否是集团统一管理会员
        boolean centralManagementMember = memberTypeDAO.checkCentralManagementMemberByShopId(shopId);
        if (centralManagementMember) {
            Long merchantId = shopService.getMerchantId(shopId);
            member = memberDAO.getInfoByMobileNumberAndMerchantId(mobileNumber, merchantId);
            po = prepaidDAO.getPrepaidCardInfoByMemberIdAndMerchantId(member.getId(), merchantId);
        }else{
            member = memberDAO.getInfoByMobileNumberAndShopId(mobileNumber,shopId);
            po = prepaidDAO.getPrepaidCardInfoByMemberIdAndShopId(member.getId(), shopId);
        }

        if (po == null) {
            return null;
        }
        return dozerMapper.map(po, PrepaidCardInfoVO.class);
    }

    /**
     * 查询预付卡充值记录
     * Query prepaid card charge record list.
     *
     * @param query  the query
     * @param date   the date
     * @param shopId the shop id
     * @return the list
     */
    public List<PrepaidCardChargeVO> queryPrepaidCardChargeRecordList(Query query, Date date, Long shopId) {
        List<PrepaidCardChargeOrder> poList = prepaidCardChargeOrderDAO.queryAPIPrepaidCardChargeRecordList(query, date, shopId);
        List<PrepaidCardChargeVO> voList = new ArrayList<PrepaidCardChargeVO>();
        for (PrepaidCardChargeOrder po : poList) {
            voList.add(dozerMapper.map(po, PrepaidCardChargeVO.class));
        }
        return voList;
    }

    /**
     * 创建预付卡充值订单
     * <p/>
     * Insert charge order.
     *
     * @param chargeParam the charge param
     */
    public PrepaidCardChargeOrderResult insertChargeOrder(PrepaidCardChargeParam chargeParam, Long shopId, Long operatorId  ,String operatorSessionCode) {
        if (chargeParam == null) {
            return PrepaidCardChargeOrderResult.CLIENT_PARAM_ERROR;
        }

        //获取规则信息
        PrepaidCardChargeRules rule = prepaidCardChargeRulesDAO.getByID(PrepaidCardChargeRules.class, chargeParam.getRuleId());
        if (rule == null) {
            return PrepaidCardChargeOrderResult.NOT_FOUND_RULE;
        }

        //是否有充值成功记录的订单
        boolean hasOrder ;
        boolean central = xMerchantService.checkMemberCentralManagementByShopId(shopId);
        Long merchantId = xShopService.getMerchantId(shopId);
        if (central) { //统一管理
            hasOrder = prepaidCardChargeOrderDAO.hasChargeSuccessOrderByMerchantId(chargeParam.getMemberId(), merchantId);
        }else {
            hasOrder = prepaidCardChargeOrderDAO.hasChargeSuccessOrderByShopId(chargeParam.getMemberId(), shopId);
        }
        if (hasOrder) {//续充
            if (rule.getIsInitial()){
                //验证该充值规则是否是“同首充”规则
                boolean hasRechargeRuleList = prepaidCardChargeRulesDAO.hasRechargeRuleListByRuleId(chargeParam.getRuleId());
                if (hasRechargeRuleList){
                    return PrepaidCardChargeOrderResult.EXIST_ORDER; //已存在充值成功的订单，不能首充
                }
            }
        } else {//首充
            if (!rule.getIsInitial()){
                return PrepaidCardChargeOrderResult.NOT_EXIST_ORDER; //不存在首充记录不能续充
            }
        }

        PrepaidCardChargeOrder po = new PrepaidCardChargeOrder();
        String code = CodeUtil.getNewCode();
        po.setCode(code);
        po.setIsInitial(!hasOrder);
        po.setShopId(shopId);
        po.setTotalAmount(rule.getChargeAmount().add(rule.getChargeGiftAmount()));
        po.setActualAmount(rule.getChargeAmount());
        po.setOperatorId(operatorId);
        po.setOperatorSessionCode(operatorSessionCode);
        PrepaidCard prepaidCard;
        if (central){
            prepaidCard = prepaidDAO.getPrepaidCardInfoByMemberIdAndMerchantId(chargeParam.getMemberId(), merchantId);
        }else {
            prepaidCard = prepaidDAO.getPrepaidCardInfoByMemberIdAndShopId(chargeParam.getMemberId(),shopId);
        }
        if (prepaidCard == null) {
            prepaidCard = new PrepaidCard();
            //创建预付卡
            prepaidCard.setMemberId(chargeParam.getMemberId());
            prepaidCard.setInitialRuleId(chargeParam.getRuleId());

            if (central) { //统一管理
                prepaidCard.setBusinessId(merchantId);
                prepaidCard.setBusinessTypeId(1L);
            } else { //非统一管理
                prepaidCard.setBusinessId(shopId);
                prepaidCard.setBusinessTypeId(2L);
            }
            prepaidCard.setStatus(0); //默认为0
            prepaidDAO.insert(prepaidCard);
        }
        if (prepaidCard.getId() == null) {
            return PrepaidCardChargeOrderResult.OTHER;
        }

        po.setPrepaidCardId(prepaidCard.getId());

        //生成编号规则
        po.setCode(CodeUtil.getNewCode());

        //根据预付卡ID获取会员ID
        po.setMemberId(chargeParam.getMemberId());
        po.setChargeStatusId((long) OrderPaymentStatus.UNPAID.getValue());//未支付
        prepaidCardChargeOrderDAO.insert(po);

        if (po.getId() != null) {
            //更新用户会员等级
            Long memberTypeId = rule.getMemberTypeId();
            memberDAO.updateMemberTypeIdByMemberId(chargeParam.getMemberId(),memberTypeId);

            PrepaidCardChargeOrderResult success = PrepaidCardChargeOrderResult.SUCCESS;
            CreateChargeOrderResultVO result = new CreateChargeOrderResultVO();
            result.setOrderNumber(po.getCode());
            success.setResult(result);
            return success;
        }
        return PrepaidCardChargeOrderResult.OTHER;
    }


    /**
     *
     * 获取集团下商户预付卡相关列表
     * Gets prepaid card shop list by merchant.
     *
     * @param merchantId the merchant id
     * @return the prepaid card shop list by merchant
     */
    public List<ShopPrepaidCardVO> getPrepaidCardShopListByMerchant(Long merchantId) {
        List<ShopPrepaidCardInfo> poList = shopPrepaidCardInfoDAO.getListByMerchantId(merchantId);
        List<ShopPrepaidCardVO> voList = new ArrayList<>();
        for (ShopPrepaidCardInfo po : poList) {
            voList.add(dozerMapper.map(po, ShopPrepaidCardVO.class));
        }
        return voList;

    }

    /**
     * 计算会员统一管理的预付卡列表计数
     *
     * Count all member central prepaid list.
     *
     * @return the int
     * @param param
     */
    public int countAllMemberCentralPrepaidList(PrepaidListParam param) {
        return prepaidDAO.countAllMemberCentralPrepaidList(param);
    }


    /**
     * 计算会员非统一管理的预付卡列表计数
     *
     * Count all not member central prepaid list.
     *
     * @param param the shop id
     * @return the int
     */
    public int countAllNotMemberCentralPrepaidList(PrepaidListParam param) {
        return prepaidDAO.countAllNotMemberCentralPrepaidList(param);
    }


    public PrepaidCardVO getById(Long prepaidCardId) {
        PrepaidCard po = prepaidDAO.getByID(PrepaidCard.class, prepaidCardId);
        PrepaidCardVO vo;
        if (po != null) {
            vo = dozerMapper.map(po, PrepaidCardVO.class);
            Long memberId = po.getMemberId();
            Member member = memberDAO.getById(memberId);
            if (member == null){
                return null;
            }
//            Long memberShopId = memberDAO.getMemberShopIdById(memberId);
            vo.setMobileNumber(member.getMobileNumber());
            MemberTypeVO memberTypeVO = xMemberService.getMemberTypeByMemberIdAndShopId(memberId, member.getShopId());
            if (memberTypeVO == null) {
                return null;
            }
            vo.setMemberTypeName(memberTypeVO.getName());
            vo.setDiscount(Tools.trimZero(memberTypeVO.getDiscount()));
            return vo;
        }

        return null;
    }

    public String getTotalChargeAmount(Long prepaidCardId) {
        return Tools.trimZero(prepaidCardChargeOrderDAO.getTotalChargeAmountByPrepaidCardId(prepaidCardId));
    }


    /**
     * 获取预付卡消费记录次数
     * Gets consume count by prepaid card id.
     *
     * @param prepaidCardId the prepaid card id
     * @return the consume count by prepaid card id
     */
    public int getConsumeCountByPrepaidCardId(Long prepaidCardId) {
        return prepaidCardTransactionDAO.getConsumeCountByPrepaidCardId(prepaidCardId);

    }

    /**
     * 更新预付卡可用状态
     *
     * Update prepaid card enable.
     *
     * @param prepaidCardId the prepaid card id
     * @param enable the enable
     */
    public void updatePrepaidCardEnable(Long prepaidCardId, Boolean enable) {
        prepaidDAO.updatePrepaidCardEnable(prepaidCardId, enable);
    }

    public int countPrepaidCardChargeOrderListByShop(PrepaidChargeListParam param) {
        return prepaidCardChargeOrderDAO.countPrepaidCardChargeOrderListByShop(param);
    }

    public int countPrepaidCardChargeOrderListByMerchant(PrepaidChargeListParam param) {
        return prepaidCardChargeOrderDAO.countPrepaidCardChargeOrderListByMerchant(param);
    }

    public List<PrepaidCardChargeListVO> queryPrepaidCardChargeOrderListByShop(Query query, PrepaidChargeListParam param) {
        String businessType = param.getBusinessType();
        Long businessId = param.getBusinessId();
        List<MemberTypeVO> memberTypeList;
        //会员等级信息
        if (StringUtils.equalsIgnoreCase(businessType, "MERCHANT")) {
            memberTypeList = xMemberService.getMemberTypeList(businessId, Business.BusinessType.MERCHANT);
        } else {
            memberTypeList = xMemberService.getMemberTypeList(businessId, Business.BusinessType.SHOP);
        }

        List<PrepaidCardChargeListVO> voList = new ArrayList<>();
        List<PrepaidCardChargeOrder> poList = prepaidCardChargeOrderDAO.queryPrepaidCardChargeOrderListByShop(query, param);
        for (PrepaidCardChargeOrder po : poList) {
            PrepaidCardChargeListVO vo = dozerMapper.map(po, PrepaidCardChargeListVO.class);
            //手机号掩码
            vo.setMobileNumber(StringUtils.overlay(po.getMobileNumber(),"****",3,7));

            //充值规则名称
            for (MemberTypeVO type : memberTypeList) {
                if (po.getMemberTypeId() == null) continue;
                if (po.getMemberTypeId().equals(type.getId())) {
                    vo.setChargeRuleName(StringUtils.join(type.getName(),
                            "(", Tools.trimZero(type.getDiscount().multiply(BigDecimal.TEN)),"折)",
                            "赠送", Tools.trimZero(po.getTotalAmount().subtract(po.getActualAmount())),"元"));
                    break;
                }
            }

            voList.add(vo);
        }
        return voList;
    }

    public List<PrepaidCardChargeListVO> queryPrepaidCardChargeOrderListByMerchant(Query query, PrepaidChargeListParam param) {
        String businessType = param.getBusinessType();
        Long businessId = param.getBusinessId();
        List<MemberTypeVO> memberTypeList;
        //会员等级信息
        if (StringUtils.equalsIgnoreCase(businessType, "MERCHANT")) {
            memberTypeList = xMemberService.getMemberTypeList(businessId, Business.BusinessType.MERCHANT);
        } else {
            memberTypeList = xMemberService.getMemberTypeList(businessId, Business.BusinessType.SHOP);
        }

        List<PrepaidCardChargeListVO> voList = new ArrayList<>();
        List<PrepaidCardChargeOrder> poList = prepaidCardChargeOrderDAO.queryPrepaidCardChargeOrderListByMerchant(query, param);
        for (PrepaidCardChargeOrder po : poList) {
            PrepaidCardChargeListVO vo = dozerMapper.map(po, PrepaidCardChargeListVO.class);
            //手机号掩码
            vo.setMobileNumber(StringUtils.overlay(po.getMobileNumber(),"****",3,7));

            //充值规则名称
            for (MemberTypeVO type : memberTypeList) {
                if (type.getId().equals(po.getMemberTypeId())) {
                    vo.setChargeRuleName(StringUtils.join(type.getName(),
                            "(", Tools.trimZero(type.getDiscount().multiply(BigDecimal.TEN)),"折)",
                            "赠送", Tools.trimZero(po.getTotalAmount().subtract(po.getActualAmount())),"元"));
                    break;
                }
            }
            voList.add(vo);
        }
        return voList;
    }

    /**
     * 预付卡充值订单金额总计
     *
     * Sum prepaid card charge order amount.
     *
     * @param param the param
     * @return the big decimal
     */
    public BigDecimal sumPrepaidCardChargeOrderAmountByMerchant(PrepaidChargeListParam param) {
        return prepaidCardChargeOrderDAO.sumPrepaidCardChargeOrderAmountByMerchant(param);
    }

    /**
     * 预付卡充值订单金额总计
     *
     * Sum prepaid card charge order amount.
     *
     * @param param the param
     * @return the big decimal
     */
    public BigDecimal sumPrepaidCardChargeOrderAmountByShop(PrepaidChargeListParam param) {
        return prepaidCardChargeOrderDAO.sumPrepaidCardChargeOrderAmountByShop(param);
    }


    /**
     *
     * 获取预付卡ID（shop为当前商户ID，该方法会自动判断是否会员统一管理，并返回相应的预付卡Id）
     *
     * Gets id by member id and shop id.
     *
     * @param memberId the member id
     * @param shopId the shop id 当前商户的ID
     * @return the id by member id and shop id
     */
    public Long getIdByMemberIdAndShopId(Long memberId, Long shopId) {
        //是否集团统一管理会员
        Boolean central = merchantDAO.checkMemberCentralManagementByShopId(shopId);
        if (central) {
            //集团ID
            Long merchantId = shopDAO.getMerchantId(shopId);
            return prepaidDAO.getIdByMemberIdAndMerchantId(memberId, merchantId);
        }else{
            return prepaidDAO.getIdByMemberIdAndShopId(memberId,shopId);
        }
    }

    /**
     * 检查预付卡支付密码
     *
     * Check password.
     *
     * @param prepaidCardId the prepaid card id
     * @param rawPassword the raw password
     * @return the boolean
     */
    public boolean checkPassword(Long prepaidCardId, String rawPassword) {
        PrepaidCard po = prepaidDAO.getByID(PrepaidCard.class, prepaidCardId);
        if (po != null) {
            String encryptPassword = EncryptionUtil.encodePassword(rawPassword, po.getSalt());
            if (StringUtils.equalsIgnoreCase(encryptPassword, po.getPassword())) {
                return true;
            }
        }
        return false;
    }

    public PrepaidCard getByMemberId(Long memberId) {
        return prepaidDAO.getByMemberId(memberId);
    }

    public BigDecimal sumChargeAmountByMobileNumberAndShopId(String mobileNumber, Long shopId) {
        return prepaidDAO.sumChargeAmountByMobileNumberAndShopId(mobileNumber,shopId);
    }

    public BigDecimal sumChargeAmountByMobileNumberAndMerchantId(String mobileNumber, Long merchantId) {
        return prepaidDAO.sumChargeAmountByMobileNumberAndMerchantId(mobileNumber,merchantId);
    }

    public PrepaidChargeOrderSummaryVO getPrepaidChargeOrderSummary(String orderNumber) {
        PrepaidChargeOrderSummary po = prepaidCardChargeOrderDAO.getPrepaidChargeOrderSummary(orderNumber);
        if (po == null) {
            return null;
        }

        //获取会员等级信息
        MemberTypeVO memberTypeVO = xMemberService.getMemberTypeByMobileNumberAndShopId(po.getMobile(), po.getShopId());
        if (memberTypeVO == null) {
            return null;
        }

        PrepaidChargeOrderSummaryVO vo = dozerMapper.map(po, PrepaidChargeOrderSummaryVO.class);
        vo.setMemberTypeName(memberTypeVO.getName());
        vo.setMobileNumber(StringUtils.overlay(po.getMobile(),"****",0,7));
        return vo;
    }
}
