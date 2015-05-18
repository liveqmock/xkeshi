package com.xpos.controller.api;

import com.xkeshi.common.em.Payment;
import com.xkeshi.common.em.Refund;
import com.xkeshi.pojo.vo.Result;
import com.xkeshi.pojo.vo.SystemParam;
import com.xkeshi.pojo.vo.param.payment.CashPaymentParam;
import com.xkeshi.pojo.vo.param.payment.PaymentRefundParam;
import com.xkeshi.pojo.vo.param.payment.PrepaidCardPaymentParam;
import com.xkeshi.service.PrepaidService;
import com.xkeshi.service.XMemberService;
import com.xkeshi.service.XMerchantService;
import com.xkeshi.service.XShopService;
import com.xkeshi.service.payment.CashPaymentService;
import com.xkeshi.service.payment.PrepaidCardPaymentService;
import com.xpos.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


/**
 * 支付API controller
 */
@Controller
@RequestMapping("api/")
public class APIPaymentController extends BaseController {


    @Autowired
    private PrepaidService prepaidService;


    @Autowired
    private XMerchantService xMerchantService;

    @Autowired
    private XShopService xShopService;

    @Autowired
    private XMemberService xMemberService;

    @Autowired
    private CashPaymentService cashPaymentService;

    @Autowired
    private PrepaidCardPaymentService prepaidCardPaymentService;


    /**
     * 现金支付
     */
    @ResponseBody
    @RequestMapping(value = "order/{orderNumber}/cash/transaction", method = RequestMethod.POST)
    public Result cashPayment(@ModelAttribute SystemParam param,
                              @RequestBody CashPaymentParam paymentParam,
                              @PathVariable("orderNumber") String orderNumber) {

        Payment res = cashPaymentService.paymentForCash(orderNumber, paymentParam, param.getMid());

        Result result = new Result(res.getName(), res.getCode());

        return result;
    }

    /**
     * 现金支付(撤销)
     */
    @ResponseBody
    @RequestMapping(value = "order/{orderNumber}/cash/transaction/{serial}/refund", method = RequestMethod.POST)
    public Result cashRefund(@ModelAttribute SystemParam param,
                              @RequestBody PaymentRefundParam refundParam,
                              @PathVariable("orderNumber") String orderNumber,
                              @PathVariable("serial") String serial) {

        Refund res = cashPaymentService.refundForCash(serial, orderNumber, refundParam);

        Result result = new Result(res.getName(), res.getCode());

        return result;
    }


    /**
     * 预付卡支付
     */
    @ResponseBody
    @RequestMapping(value = "order/{orderNumber}/prepaid_card/transaction", method = RequestMethod.POST)
    public Result prepaidCardPayment(@ModelAttribute SystemParam param,
                              @RequestBody PrepaidCardPaymentParam paymentParam,
                              @PathVariable("orderNumber") String orderNumber) {

        Payment res = prepaidCardPaymentService.paymentForPrepaidCard(orderNumber, paymentParam, param.getMid());

        Result result = new Result(res.getName(), res.getCode());

        return result;
    }

    /**
     * 预付卡支付(撤销)
     */
    @ResponseBody
    @RequestMapping(value = "order/{orderNumber}/prepaid_card/transaction/{serial}/refund", method = RequestMethod.POST)
    public Result prepaidCardRefund(@ModelAttribute SystemParam param,
                              @RequestBody PaymentRefundParam refundParam,
                              @PathVariable("orderNumber") String orderNumber,
                              @PathVariable("serial") String serial) {

        Refund res = prepaidCardPaymentService.refundForPrepaidCard(serial, orderNumber, refundParam);

        Result result = new Result(res.getName(), res.getCode());

        return result;
    }


}
