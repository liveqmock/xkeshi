package com.xkeshi.service.payment;

import com.xkeshi.common.em.Payment;
import com.xkeshi.common.em.Refund;
import com.xkeshi.common.globality.GlobalSource;
import com.xkeshi.pojo.po.CashTransaction;
import com.xkeshi.pojo.vo.param.payment.CashPaymentParam;
import com.xkeshi.pojo.vo.param.payment.PaymentRefundParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * Created by david-y on 2015/1/22.
 */
@Service
public class CashPaymentService extends PaymentService {



    /**
     * 现金支付
     *
     * Payment for cash.
     *
     * @param orderNumber the order number
     * @param paymentParam the payment param
     * @param shopId the shop id
     * @return the payment
     */
	@Transactional
    public Payment paymentForCash(String orderNumber, CashPaymentParam paymentParam ,Long shopId) {
        String orderType = StringUtils.upperCase(paymentParam.getOrderType());
        BigDecimal submitAmount = paymentParam.getAmount();

        //支付前检查订单状态
        Payment paymentStatus = checkOrderForPayment(orderNumber, paymentParam);
        if (paymentStatus == Payment.FIRST_PAYMENT){ //首次支付

            if (StringUtils.equals(orderType, "XPOS_ORDER")){
                //删除实体券
                clearPhysicalCouponsByOrderNumber(orderNumber);
                //添加实体券优惠
                if (insertPhysicalCoupons(orderNumber, paymentParam, shopId)){
                    return Payment.INVALID_PHYSICAL_COUPON; //实体券如果不在商户可用则终止此次支付
                }
                //清空会员折扣
                clearMemberDiscountByOrderNumber(orderNumber);
                //添加会员折扣
                insertMemberDiscountToOrder(orderNumber, paymentParam, shopId);


                //更新订单的实付金额（订单总金额*折扣-实体优惠券金额）
                updateOrderActualAmount(orderNumber);
            }

            //添加现金支付流水记录
            insertCashTransaction(orderNumber, orderType, submitAmount, paymentParam);

            //更新支付方式
            updateOrderChargeChannel(orderType, orderNumber,
                    GlobalSource.getIDByName(GlobalSource.metaPrepaidCardChargeChannelList,"现金"));

            //更新订单状态
            updateOrderPaymentStatus(orderNumber, orderType);

            if (StringUtils.equals(orderType, "XPOS_PREPAID")){
                updateMemberType(orderNumber); //覆盖会员等级
            }

            return Payment.SUCCESS;
        } else if(paymentStatus == Payment.NOT_FIRST_PAYMENT){ //非首次支付

            //添加现金支付流水记录
            insertCashTransaction(orderNumber, orderType, submitAmount, paymentParam);

            //更新订单状态
            updateOrderPaymentStatus(orderNumber, orderType);


            return Payment.SUCCESS;
        } else {
            return paymentStatus; //非成功
        }
    }




    /**
     * 现金支付退款
     *
     * Refund for cash.
     *
     * @param serial the serial
     * @param orderNumber the order number
     * @param refundParam the refund param
     * @return the refund
     */
    @Transactional
    public Refund refundForCash(String serial, String orderNumber, PaymentRefundParam refundParam) {

        //获取支付
        CashTransaction transaction = cashTransactionDAO.getBySerial(serial);
        if (transaction == null) {
            return Refund.NON_TRANSACTION;
        }

        String orderType = null;
        if (transaction.getOrderNumber() != null) {
            orderType = "XPOS_ORDER";
        } else if (transaction.getThirdOrderCode() != null) {
            orderType = "THIRD_ORDER";
        } else if (transaction.getPrepaidCardChargeOrderCode() != null) {
            orderType = "XPOS_PREPAID";
        }

        if (!(StringUtils.equalsIgnoreCase(refundParam.getOrderType(), orderType) &&
                StringUtils.equalsIgnoreCase(orderNumber, transaction.getOrderNumber()))) {
            return Refund.TRANSACTION_UNMATCHED; //订单和支付不匹配
        }

        //检查订单状态是否可退
        Boolean availableOrderRefund = checkAvailableRefund(orderNumber,orderType);
        if (!availableOrderRefund){
            return Refund.ORDER_UNABLE_REFUND;
        }


        //退款操作
        Boolean refundTransactionSuccess = refundCashTransaction(serial);
        if (!refundTransactionSuccess){
            return Refund.REFUND_TRANSACTION_FAILED;
        }


        if (StringUtils.equals(orderType, "XPOS_ORDER")){
            //更新订单的实付金额（订单总金额*折扣-实体优惠券金额）
            updateOrderActualAmount(orderNumber);
        }

        //更新订单退款状态
        updateOrderRefundStatus(orderNumber, orderType);

        return Refund.SUCCESS;

    }




    /**
     * 退现金支付
     *
     * @param serial
     */
    private Boolean refundCashTransaction(String serial) {
        if (cashTransactionDAO.refundCashTransaction(serial) > 0) {
            return true;
        }
        return false;
    }


}
