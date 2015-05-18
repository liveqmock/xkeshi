package com.xpos.controller.api;

import com.xkeshi.common.db.OrderType;
import com.xkeshi.common.db.Query;
import com.xkeshi.common.em.result.PrepaidCardChargeOrderResult;
import com.xkeshi.pojo.vo.*;
import com.xkeshi.pojo.vo.param.PasswordParam;
import com.xkeshi.pojo.vo.param.PrepaidCardChargeParam;
import com.xkeshi.service.PrepaidService;
import com.xkeshi.service.XMemberService;
import com.xkeshi.service.XMerchantService;
import com.xkeshi.service.XShopService;
import com.xkeshi.utils.Tools;
import com.xpos.common.utils.TokenUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


/**
 * 预付卡API controller
 * The type API member controller.
 */
@Controller
@RequestMapping("api/")
public class APIPrepaidController extends BaseAPIController {


	@Autowired
	private PrepaidService prepaidService;


    @Autowired
    private XMerchantService xMerchantService;

    @Autowired
    private XShopService xShopService;

    @Autowired
    private XMemberService xMemberService;


    /**
     *
     * 获取预付卡首充规则
     *
     * Gets first charge rules by member type id.
     *
     * @param param the param
     * @param memberTypeId the member type id
     * @return the first charge rules by member type id
     */
    @ResponseBody
    @RequestMapping(value = "member_type/{memberTypeId}/prepaid_card/first_charge_rule/list",method = RequestMethod.GET)
    public Result getFirstChargeRulesByMemberTypeId(@ModelAttribute SystemParam param,
                                                    @PathVariable("memberTypeId") Long memberTypeId) {

        Result result = new Result("预付卡首充规则","0");

        ResultPrepaidCardChargeRulesVO rulesVO = prepaidService.getFirstChargeRules(memberTypeId, param.getMid());

        if (rulesVO != null) {
            result.setResult(rulesVO);
        } else {
            result = new Result("该商户没有适用的充值规则", "1001");
        }

        return result;
    }


    /**
     *
     * 获取会员预付卡续充规则
     */
    @ResponseBody
    @RequestMapping(value = "member/{mobileNumber}/prepaid_card/recharge_rule/list", method = RequestMethod.GET)
    public Result getRechargeRulesByMemberId(@ModelAttribute SystemParam param,
                                                    @PathVariable("mobileNumber") String mobileNumber) {

        Result result = new Result("预付卡续充规则","0");
        ResultPrepaidCardChargeRulesVO rulesVO = prepaidService.getRechargeRules(mobileNumber, param.getMid());

        if (rulesVO != null) {
            result.setResult(rulesVO);
        } else {
            result = new Result("该商户没有适用的充值规则", "1001");
        }


        return result;
    }



    /**
     *
     * 修改预付卡支付密码
     *
     */
    @ResponseBody
    @RequestMapping(value = "member/{mobileNumber}/prepaid_card/password", method = RequestMethod.PUT)
    public Result changePayPassword(@ModelAttribute SystemParam param,
                                    @RequestBody PasswordParam password ,
                                             @PathVariable("mobileNumber") String mobileNumber) {

        Result result = new Result("修改预付卡支付密码","0");
        //还原密码文本
        String plainPassword = null;
        try {
            plainPassword = TokenUtil.decrypt(password.getNewPassword());
        } catch (Exception e) {
            result = new Result("密码没有按规定加密","1001");
            return result;
        }
        //设置密码
        if(!prepaidService.setPasswordByMobileNumberAndShopId(mobileNumber, param.getMid(), plainPassword)) {
        	return new Result("修改预付卡支付密码失败","1001");
        }
        return result;
    }



    /**
     *
     * 查询预付卡充值列表
     *
     */
    @ResponseBody
    @RequestMapping(value = "prepaid_card/charge/list", method = RequestMethod.GET)
    public Result changePayPassword(@ModelAttribute SystemParam param,
                                    @RequestParam("date") String dateStr,
                                    @RequestParam(defaultValue = "1") Integer page,
                                    @RequestParam(defaultValue = "30") Integer pageSize
                                    ) {




        Date date = new Date();
        if (StringUtils.isNotBlank(dateStr)) {
            date = Tools.setDate(dateStr);
        }

        PrepaidCardChargeRecordVO recordVO = new PrepaidCardChargeRecordVO();

        int listCount = prepaidService.countPrepaidCardChargeRecordList(date, param.getMid());

        BigDecimal totalAmount = prepaidService.sumPrepaidCardChargeRecordList(date, param.getMid());

        Query query = new Query(listCount, pageSize, page);
        query.setOrderColumns("created_time");
        query.setOrderType(OrderType.DESC);
        List<PrepaidCardChargeVO> voList = prepaidService.queryPrepaidCardChargeRecordList(query, date, param.getMid());
        query.calculatePage(voList.size());

        recordVO.setChargeList(voList);
        recordVO.setTotalCount(listCount);
        recordVO.setTotalAmount(totalAmount);
        recordVO.setPage(page);
        recordVO.setPageSize(pageSize);
        recordVO.setHasPrefix(query.isPrevious());
        recordVO.setHasNext(query.isNext());

        Result result = new Result("0", "查询预付卡充值列表", recordVO);

        return result;
    }


    /**
     *
     * 查询预付卡信息
     */
    @ResponseBody
    @RequestMapping(value = "member/{mobileNumber}/prepaid_card/info", method = RequestMethod.GET)
    public Result getPrepaidCardInfo(@ModelAttribute SystemParam param,
                                             @PathVariable("mobileNumber") String mobileNumber) {

        Result result = new Result("查询预付卡信息","0");
        PrepaidCardResultVO rulesVO = new PrepaidCardResultVO();
        rulesVO.setPrepaidCardInfo(prepaidService.getPrepaidCardInfo(mobileNumber, param.getMid()));

        result.setResult(rulesVO);

        return result;
    }



    /**
     *
     * 创建预付卡充值订单
     *
     */
    @ResponseBody
    @RequestMapping(value = "prepaid_card/charge", method = RequestMethod.POST)
    public Result chargePrepaidCard(@ModelAttribute SystemParam param,
                                    @RequestBody PrepaidCardChargeParam chargeParam
                                    ) {
    	com.xkeshi.pojo.po.Shop shopPO = xShopService.findShopByShopId(param.getMid());
    	String operatorSession = null;
		if (shopPO.getEnableShift()) {
			operatorSession = super.getOperatorSession(param);
			if (StringUtils.isBlank(operatorSession)) {
				return new Result("-1", "未获取当前操作员的当班会话，请退出后重新登录", null);
			}
		}
		//创建预付卡充值订单
		PrepaidCardChargeOrderResult res = prepaidService.insertChargeOrder(chargeParam, param.getMid(), param.getOperatorId(),operatorSession);
		
		Result result = new Result(res.getName(), res.getCode());
		if (res.getResult() != null){
			result.setResult(res.getResult());
		}
		return result;
    }

    /**
     * 获取预付卡充值订单的小票打印信息
     */
    @ResponseBody
    @RequestMapping(value="prepaid_card/charge/{orderNumber}/print_summary", method = RequestMethod.GET)
    public Result getOrderPrintSummary(@ModelAttribute SystemParam systemParam,
                                       @PathVariable(value="orderNumber")String orderNumber){


        PrepaidChargeOrderSummaryVO summaryVO = prepaidService.getPrepaidChargeOrderSummary(orderNumber);
        if (summaryVO == null) {
            return new Result("1001","获取充值订单信息失败",  null);
        }
        return new Result("0","充值订单查询成功",  summaryVO);
    }



}
