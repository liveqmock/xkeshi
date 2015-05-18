package com.xpos.controller;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.xkeshi.common.db.OrderType;
import com.xkeshi.common.db.Query;
import com.xkeshi.common.globality.GlobalSource;
import com.xkeshi.interceptor.form.AvoidDuplicateSubmission;
import com.xkeshi.pojo.meta.MetaPrepaidCardChargeChannel;
import com.xkeshi.pojo.vo.MemberTypeVO;
import com.xkeshi.pojo.vo.PrepaidCardChargeListVO;
import com.xkeshi.pojo.vo.PrepaidCardChargeRulesListVO;
import com.xkeshi.pojo.vo.PrepaidCardListVO;
import com.xkeshi.pojo.vo.PrepaidCardVO;
import com.xkeshi.pojo.vo.ShopLiteVO;
import com.xkeshi.pojo.vo.ShopPrepaidCardVO;
import com.xkeshi.pojo.vo.param.PrepaidChargeListParam;
import com.xkeshi.pojo.vo.param.PrepaidListParam;
import com.xkeshi.service.PrepaidService;
import com.xkeshi.service.XMemberService;
import com.xkeshi.service.XMerchantService;
import com.xkeshi.service.XShopService;
import com.xpos.common.entity.face.Business;

@Controller
public class PrepaidController extends BaseController {


	@Autowired
	private PrepaidService prepaidService;


    @Autowired
    private XMerchantService xMerchantService;

    @Autowired
    private XShopService xShopService;

    @Autowired
    private XMemberService xMemberService;



	/**
     * 预付卡列表页面
	 * Product target list.
	 *
	 * @param mav the mav
	 * @param param the param
	 * @return the model and view
	 */
	@RequestMapping(method = RequestMethod.GET,value = "/prepaid/card/list")
	public ModelAndView prepaidList(ModelAndView mav,
                                    @ModelAttribute PrepaidListParam param){

        String businessType = getBusinessType();
        param.setBusinessType(businessType);
		param.setBusinessId(getBusinessId());
        List<PrepaidCardListVO> voList;
        Query query = null;
        if (StringUtils.equalsIgnoreCase(businessType, "MERCHANT")) {
            //集团是否统一管理
            boolean central = xMerchantService.checkMemberCentralManagementByMerchantId(getBusinessId());
            if (!central){//非统一管理
                mav.setViewName("redirect:/merchant/"+ getBusinessId() +"/prepaid/shop/list");
                return mav;
            } else { //统一管理
                int listCount = prepaidService.countAllMemberCentralPrepaidList(param);
                query = new Query(listCount, 50, param.getPageTo());
                voList = prepaidService.queryMemberCentralPrepaidList(query, param);
            }
        } else if(StringUtils.equalsIgnoreCase(businessType, "SHOP")){ //独立商户或者非统一管理商户
            int listCount = prepaidService.countAllNotMemberCentralPrepaidList(param);
            query = new Query(listCount, 50, param.getPageTo());
            voList = prepaidService.queryNotMemberCentralPrepaidList(query, param);
        } else {
            mav.setViewName("redirect:/");
            return mav;
        }

        query.setOrderColumns("t.id");
        query.setOrderType(OrderType.ASC);
		query.calculatePage(voList.size());

        List<MemberTypeVO> memberTypeList;
        //会员等级信息
        if (StringUtils.equalsIgnoreCase(businessType,"MERCHANT")){
            memberTypeList = xMemberService.getMemberTypeList(getBusinessId(), Business.BusinessType.MERCHANT);
        }else{
            memberTypeList = xMemberService.getMemberTypeList(getBusinessId(), Business.BusinessType.SHOP);
        }


		mav.addObject("memberTypeList",memberTypeList);
		mav.addObject("query",query);
		mav.addObject("voList",voList);
		mav.addObject("param", param);




		mav.setViewName("prepaid/prepaid_list");
		return mav;
	}




    //预付卡详情
    @RequestMapping(method = RequestMethod.GET,value = "/prepaid/{prepaidCardId}")
    public ModelAndView prepaidCardDetail(ModelAndView mav, @PathVariable("prepaidCardId") Long prepaidCardId){

        //基本信息
        PrepaidCardVO vo = prepaidService.getById(prepaidCardId);
        if (vo == null) {
            mav.setViewName("redirect:/prepaid/card/list");
            return mav;
        }
        mav.addObject("vo", vo);

        //充值信息
        String totalChargeAmount = prepaidService.getTotalChargeAmount(prepaidCardId);
        mav.addObject("totalChargeAmount", totalChargeAmount);


        //消费信息
        int consumeCount = prepaidService.getConsumeCountByPrepaidCardId(prepaidCardId);
        mav.addObject("consumeCount", consumeCount);

        mav.setViewName("prepaid/detail");
        return mav;
    }

    //预付卡状态更新
    @RequestMapping(method = RequestMethod.POST,value = "/prepaid/{prepaidCardId}/enable/update")
    public ModelAndView updatePrepaidCardEnable(ModelAndView mav, @PathVariable("prepaidCardId") Long prepaidCardId
            ,@RequestParam("enable") Boolean enable) {

        prepaidService.updatePrepaidCardEnable(prepaidCardId,enable);

        mav.setViewName("redirect:/prepaid/"+prepaidCardId);
        return mav;
    }



    //预付卡商户列表
    @RequestMapping(method = RequestMethod.GET,value = "/merchant/{merchantId}/prepaid/shop/list")
    public ModelAndView prepaidShopList(ModelAndView mav, @PathVariable("merchantId") Long merchantId){

        if (!getBusinessId().equals(merchantId)){
            return mav;
        }

        //集团下所有的商户列表
        List<ShopPrepaidCardVO> voList = prepaidService.getPrepaidCardShopListByMerchant(merchantId);

        mav.addObject("voList", voList);

        mav.setViewName("prepaid/prepaid_shop_list");
        return mav;
    }



    //充值规则列表
    @AvoidDuplicateSubmission(addToken= true)
    @RequestMapping(method = RequestMethod.GET,value = "/prepaid/rule")
    public ModelAndView viewPrepaidRule(ModelAndView mav) {
        Long businessTypeId = GlobalSource.getIDByCode(GlobalSource.metaBusinessTypeList, getBusinessType());
        boolean editable = false; //可编辑
        final Long businessId = getBusinessId();

        //检查是否是集团
        if (StringUtils.equals(getBusinessType(), "MERCHANT") ) {

            //是否统一管理
            if (xMerchantService.checkMemberCentralManagementByMerchantId(businessId)){
                editable = true;

                //是否已经设置过商户
                if(!prepaidService.hasShopsByMerchantId(businessId)){
                    mav.setViewName("redirect:/merchant/"+ businessId +"/prepaid/shop/update");
                    return mav;
                }
                //是否已经设置过规则
                if (!prepaidService.hasRules(businessTypeId, businessId)) {
                    mav.setViewName("redirect:/prepaid/rule/update?bId="+businessId+"&bType=merchant");
                    return mav;
                }

                //获取子商户列表
                List<ShopLiteVO> shopList = xShopService.getShopsByMerchantId(businessId);
                mav.addObject("shopList", shopList);
                //查询已设置的商户列表
                List<ShopLiteVO> checkedShopList = prepaidService.getShopListByMerchantId(businessId);
                mav.addObject("checkedShopList", checkedShopList);
            } else {
                //跳转到商户列表页面
                mav.setViewName("redirect:/merchant/"+ businessId +"/prepaid/shop/list");
                return mav;
            }
        } else { //商户
            //是否是独立商户
            if (!xShopService.hasMerchant(businessId)) { //独立商户
                //是否已经设置过规则
                if (!prepaidService.hasRules(businessTypeId, businessId)) {
                    mav.setViewName("redirect:/prepaid/rule/update?bId="+businessId+"&bType=shop");
                    return mav;
                }
                editable = true;
            } else {
                //是否被集团统一管理会员
                if (xMerchantService.checkMemberCentralManagementByShopId(businessId)){ //统一管理

                } else { //非统一管理
                    editable = true;
                    //是否已经设置过规则
                    if (!prepaidService.hasRules(businessTypeId, businessId)) {
                        mav.setViewName("redirect:/prepaid/rule/update?bId="+businessId+"&bType=shop");
                        return mav;
                    }
                }
            }
        }


        //规则列表
        List<PrepaidCardChargeRulesListVO> ruleList = prepaidService.getPrepaidCardChargeRulesList(businessTypeId,businessId);
        mav.addObject("ruleList", ruleList);
        mav.addObject("businessId", businessId);
        mav.addObject("businessType", getBusinessType());

        mav.addObject("editable", editable);
        mav.setViewName("prepaid/rule_detail");
        return mav;
    }




    //添加充值规则（选商户）（仅统一管理会员的集团）
    @RequestMapping(method = RequestMethod.GET,value = "/merchant/{merchantId}/prepaid/shop/update")
    public ModelAndView setPrepaidRuleShopsView(ModelAndView mav, @PathVariable("merchantId") Long merchantId) {

        //获取子商户列表
        List<ShopLiteVO> shopList = xShopService.getShopsByMerchantId(merchantId);
        mav.addObject("shopList", shopList);
        mav.addObject("merchantId", merchantId);
        mav.setViewName("prepaid/shop_list");
        return mav;
    }


    //添加充值规则（选商户）（仅统一管理会员的集团）
    @RequestMapping(method = RequestMethod.POST,value = "/merchant/{merchantId}/prepaid/shop/update")
    public ModelAndView setPrepaidRuleShops(ModelAndView mav,
                                            @PathVariable("merchantId") Long merchantId,
                                            @RequestParam(value = "shopIds",required = false) Long[] shopIds) {

        //设置预付卡集团适用的商户
        prepaidService.setShopsInMerchant(merchantId, shopIds);
        mav.setViewName("redirect:/prepaid/rule");
        return mav;
    }




    //添加充值规则（设置规则）(未设置过充值规则时才会访问)
    @AvoidDuplicateSubmission(addToken= true)
    @RequestMapping(method = RequestMethod.GET,value = "/prepaid/rule/update")
    public ModelAndView setPrepaidRulesView(ModelAndView mav,
                                            @RequestParam("bId") Long businessId,
                                            @RequestParam("bType") String businessType) {

        //获取会员等级折扣信息
        List<MemberTypeVO> memberTypeList = null;
        if (StringUtils.equals("merchant",businessType)) {
            memberTypeList = xMemberService.getMemberTypeList(businessId, Business.BusinessType.MERCHANT);
        } else if (StringUtils.equals("shop",businessType)) {
            memberTypeList = xMemberService.getMemberTypeList(businessId, Business.BusinessType.SHOP);
        }

        mav.addObject("businessId", businessId);
        mav.addObject("businessType", businessType);
        mav.addObject("memberTypeList", memberTypeList);
        mav.setViewName("prepaid/rule_update");
        return mav;
    }


    /**
     *
     * 保存规则信息
     *
     * 规则格式
     * 会员卡等级ID||首充数据1|首充数据2|首充数据3...||续充数据1|续充数据2|续充数据3...
     * 首充数据说明：（赠送类型----0无1金额）
     * 充值金额_赠送类型_赠送金额
     *
     * 续充数据说明：（同首充）
     *
     *
     * 例子：
     * 001||1000_1_100|500_0_||1000_1_80|500_0_
     * 002||1000_1_100|500_0_||
     *
     *
     * Save prepaid rules view.
     *
     * @param mav the mav
     * @param businessId the business id
     * @param businessType the business type
     * @param rules the rules
     * @return the model and view
     */
    //保存充值规则（设置规则）
    @AvoidDuplicateSubmission(removeToken=true,errorRedirectURL="/prepaid/rule")
    @RequestMapping(method = RequestMethod.POST,value = "/prepaid/rule/update")
    public ModelAndView savePrepaidRules(ModelAndView mav,
                                             @RequestParam("bId") Long businessId,
                                             @RequestParam("bType") String businessType,
                                             @RequestParam("rule") String[] rules
    ) {

        //分析规则数据
        for (String rule : rules) {
            //分解单个会员等级下的
            //信息格式为 001||1000_1_100|500_0_||1000_1_80|500_0_
            String[] ruleInfo = StringUtils.splitByWholeSeparator(rule, "||");
            if (ruleInfo.length == 3) {
                Long memberTypeId = Long.valueOf(ruleInfo[0]);
                String[] firstChargeRules = StringUtils.splitByWholeSeparator(ruleInfo[1], "|");
                prepaidService.insertChargeRules(businessId,businessType,true,memberTypeId,firstChargeRules);
                String[] rechargeRules = StringUtils.splitByWholeSeparator(ruleInfo[2], "|");
                prepaidService.insertChargeRules(businessId,businessType,false,memberTypeId,rechargeRules);
            }
        }

        mav.setViewName("redirect:/prepaid/rule");
        return mav;
    }


    /**
     *
     * 保存规则信息
     *
     * 规则格式
     * 会员卡等级ID||首充数据1|首充数据2|首充数据3...||续充数据1|续充数据2|续充数据3...
     * 首充数据说明：（赠送类型----0无1金额）
     * 充值金额_赠送类型_赠送金额
     *
     * 续充数据说明：（同首充）
     *
     *
     * 例子：
     * 001||1000_1_100|500_0_||1000_1_80|500_0_
     * 002||1000_1_100|500_0_||
     *
     *
     * Save prepaid rules view.
     *
     * @param mav the mav
     * @param businessId the business id
     * @param businessType the business type
     * @return the model and view
     */
    //保存充值规则（设置规则）
    @AvoidDuplicateSubmission(removeToken=true,errorRedirectURL="/prepaid/rule")
    @RequestMapping(value = "/prepaid/member_type/{memberTypeId}/rule/update")
    public ModelAndView savePrepaidRulesForMemberTypeID(ModelAndView mav,
                                                        @RequestParam("bId") Long businessId,
                                                        @RequestParam("bType") String businessType,
                                                        @RequestParam("rule") String rule,
                                                        @PathVariable("memberTypeId") Long memberTypeId) {
        //信息格式为 1000_1_100|500_0_||1000_1_80|500_0_
        String[] ruleInfo = StringUtils.splitByWholeSeparator(rule, "||");
        if (ruleInfo.length == 2) {
            String[] firstChargeRules = StringUtils.splitByWholeSeparator(ruleInfo[0], "|");
            prepaidService.insertChargeRules(businessId,businessType,true,memberTypeId,firstChargeRules);
            String[] rechargeRules = StringUtils.splitByWholeSeparator(ruleInfo[1], "|");
            prepaidService.insertChargeRules(businessId,businessType,false,memberTypeId,rechargeRules);
        }else if (StringUtils.equals(rule,"||")) {
            //删除所有规则
            prepaidService.clearChargeRules(businessId,businessType,true,memberTypeId);
            prepaidService.clearChargeRules(businessId,businessType,false,memberTypeId);
        }
        mav.setViewName("redirect:/prepaid/rule");
        return mav;
    }



    //充值规则列表
    @RequestMapping(method = RequestMethod.GET,value = "/prepaid/charge/list")
    public ModelAndView prepaidCardChargeList(ModelAndView mav,@ModelAttribute PrepaidChargeListParam param) {
        String businessType = getBusinessType();
        param.setBusinessType(businessType);
        param.setBusinessId(getBusinessId());

        Query query = null;
        int listCount;
        BigDecimal totalChargeAmount;
        List<PrepaidCardChargeListVO> voList;
        if (StringUtils.equalsIgnoreCase(businessType, "MERCHANT")) {
            //集团是否统一管理
            boolean central = xMerchantService.checkMemberCentralManagementByMerchantId(getBusinessId());
            if (!central){//非统一管理
                listCount = prepaidService.countPrepaidCardChargeOrderListByShop(param);
                totalChargeAmount = prepaidService.sumPrepaidCardChargeOrderAmountByShop(param);
                query = new Query(listCount, 50, param.getPageTo());
                query.setOrderColumns("t.id");
                query.setOrderType(OrderType.DESC);
                voList = prepaidService.queryPrepaidCardChargeOrderListByShop(query, param);
            } else { //统一管理
                listCount = prepaidService.countPrepaidCardChargeOrderListByMerchant(param);
                totalChargeAmount = prepaidService.sumPrepaidCardChargeOrderAmountByMerchant(param);
                query = new Query(listCount, 50, param.getPageTo());
                query.setOrderColumns("t.id");
                query.setOrderType(OrderType.DESC);
                voList = prepaidService.queryPrepaidCardChargeOrderListByMerchant(query, param);
            }
        } else if(StringUtils.equalsIgnoreCase(businessType, "SHOP")) { //独立商户或者非统一管理商户
            listCount = prepaidService.countPrepaidCardChargeOrderListByShop(param);
            totalChargeAmount = prepaidService.sumPrepaidCardChargeOrderAmountByShop(param);
            query = new Query(listCount, 50, param.getPageTo());
            query.setOrderColumns("t.id");
            query.setOrderType(OrderType.DESC);
            voList = prepaidService.queryPrepaidCardChargeOrderListByShop(query, param);
        } else {
            mav.setViewName("redirect:/");
            return mav;
        }
        query.calculatePage(voList.size());

        //总充值金额
        mav.addObject("totalChargeAmount",totalChargeAmount);

        //充值方式
        List<MetaPrepaidCardChargeChannel> channelList = GlobalSource.metaPrepaidCardChargeChannelList;
        mav.addObject("channelList",channelList);

        mav.addObject("query",query);
        mav.addObject("voList",voList);
        mav.addObject("param", param);
        mav.setViewName("prepaid/order_list");
        return mav;
    }




}
