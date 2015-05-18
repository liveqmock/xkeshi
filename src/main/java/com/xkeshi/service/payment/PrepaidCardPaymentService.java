package com.xkeshi.service.payment;

import java.math.BigDecimal;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xkeshi.common.em.Payment;
import com.xkeshi.common.em.Refund;
import com.xkeshi.common.globality.GlobalSource;
import com.xkeshi.dao.OrderDAO;
import com.xkeshi.dao.PhysicalCouponDAO;
import com.xkeshi.pojo.po.PrepaidCard;
import com.xkeshi.pojo.po.PrepaidCardTransaction;
import com.xkeshi.pojo.vo.param.payment.PaymentRefundParam;
import com.xkeshi.pojo.vo.param.payment.PrepaidCardPaymentParam;
import com.xkeshi.service.PrepaidService;
import com.xkeshi.service.XMemberService;
import com.xpos.common.utils.TokenUtil;

/**
 * 预付卡支付service
 * Created by david-y on 2015/1/22.
 */
@Service
public class PrepaidCardPaymentService extends PaymentService {

    @Autowired(required = false)
    private OrderDAO orderDAO;

    @Autowired
    private PrepaidService prepaidService;
    @Autowired
    private XMemberService xMemberService;

    @Autowired(required = false)
    private PhysicalCouponDAO physicalCouponDAO;


    /**
     * 预付卡支付
     *
     * Payment for prepaid card.
     *
     * @param orderNumber the order number
     * @param paymentParam the payment param
     * @param shopId the shop id
     * @return the payment
     *
     */
    @Transactional
    public Payment paymentForPrepaidCard(String orderNumber, PrepaidCardPaymentParam paymentParam ,Long shopId) {
        String orderType = StringUtils.upperCase(paymentParam.getOrderType());
        BigDecimal submitAmount = paymentParam.getAmount();
        Long memberId = paymentParam.getMemberId();
        String serial = paymentParam.getSerial();


        //获取预付卡
        PrepaidCard prepaidCard = prepaidService.getByMemberId(memberId);

        if (prepaidCard == null) {
            return Payment.NOT_EXIST_PREPAID_CARD;
        }

        //密码解码
        try {
            String rawPassword = TokenUtil.decrypt(paymentParam.getPassword());
            //校验支付密码
            boolean checkPassword = prepaidService.checkPassword(prepaidCard.getId(),rawPassword);
            if (!checkPassword) {
                return Payment.PASSWORD_ERROR;
            }
        } catch (Exception e) {
            return Payment.PREPAID_CARD_PASSWORD_DECODE_ERROR;
        }


        //支付前检查订单状态
        Payment paymentStatus = checkOrderForPayment(orderNumber, paymentParam);



        //获取预付卡实际需支付金额（取决于余额是否足够）
        BigDecimal needPaymentAmount = getPrepaidCardNeedPaymentAmount(orderNumber,memberId,shopId,paymentParam.getPhysicalCouponIds(), prepaidCard.getBalance());

        if (StringUtils.equals(paymentStatus.getName(),"首次支付")){

            if (StringUtils.equals(orderType, "XPOS_ORDER")){
                //添加实体券优惠
                if (insertPhysicalCoupons(orderNumber, paymentParam, shopId)){
                    return Payment.INVALID_PHYSICAL_COUPON; //实体券如果不在商户可用则终止此次支付
                }
                //添加会员折扣
                insertMemberDiscountToOrder(orderNumber, paymentParam, shopId);
            }

            //添加预付卡支付流水记录
            insertPrepaidCardTransaction(prepaidCard.getId(),orderNumber, orderType, needPaymentAmount,serial);
            
            //更新支付方式
            updateOrderChargeChannel(orderType, orderNumber,
                    GlobalSource.getIDByName(GlobalSource.metaPrepaidCardChargeChannelList, "预付卡"));

            //更新订单实付金额
            updateOrderActualPaid(orderNumber,orderType, paymentParam.getMemberId(),shopId, paymentParam.getPhysicalCouponIds());

            //更新订单状态
            updateOrderPaymentStatus(orderNumber, orderType, true);

            return Payment.SUCCESS;
        } else if(StringUtils.equals(paymentStatus.getName(),"非首次支付")){

            //检查是否有使用预付卡支付记录(同一订单中不允许预付卡支付多次)
            if (hasPrepaidCardTransaction(orderNumber,orderType)){
                return Payment.EXIST_PREPAID_CARD_PAYMENT_RECORD;
            }

            //添加预付卡支付流水记录
            insertPrepaidCardTransaction(prepaidCard.getId(), orderNumber, orderType, submitAmount,serial);

            //更新订单实付金额
            updateOrderActualPaid(orderNumber,orderType, paymentParam.getMemberId(),shopId, paymentParam.getPhysicalCouponIds());


            //更新订单状态
            updateOrderPaymentStatus(orderNumber, orderType, true);

            return Payment.SUCCESS;
        } else {
            return paymentStatus; //非成功
        }
    }

    /**
     * 检查预付卡余额是否足够支付订单
     * @param orderNumber
     * @param shopId
     * @param physicalCouponIds
     * @param balance
     */
    private boolean checkPrepaidCardBalanceEnough(String orderNumber, Long memberId, Long shopId, Long[] physicalCouponIds, BigDecimal balance) {
        //会员折扣
        BigDecimal discount = BigDecimal.ONE;
        if (memberId != null) {
            discount = xMemberService.getMemberDiscountByMemberIdAndShopId(memberId, shopId);
        }
        //实体券抵扣金额累计
        BigDecimal couponAmount = BigDecimal.ZERO;
        if (ArrayUtils.isNotEmpty(physicalCouponIds)) {
            couponAmount = physicalCouponDAO.sumCouponAmount(physicalCouponIds, shopId);
        }
        BigDecimal totalPaidAmount = getTotalPaidAmount(orderNumber, "XPOS_ORDER");
        BigDecimal paymentAmount = getPaymentAmount(orderNumber, "XPOS_ORDER");

        if (totalPaidAmount
                .multiply(discount)
                .subtract(couponAmount)
                .subtract(paymentAmount).compareTo(balance) == 1) {
            return false;
        }
        return true;
    }

    /**
     * 预付卡实际需支付金额
     *
     * @param orderNumber
     * @param memberId
     * @param shopId
     * @param physicalCouponIds
     * @param balance
     * @return
     */
    private BigDecimal getPrepaidCardNeedPaymentAmount(String orderNumber, Long memberId, Long shopId, Long[] physicalCouponIds, BigDecimal balance) {
        //会员折扣
        BigDecimal discount = BigDecimal.ONE;
        if (memberId != null) {
            discount = xMemberService.getMemberDiscountByMemberIdAndShopId(memberId, shopId);
        }
        //实体券抵扣金额累计
        BigDecimal couponAmount = BigDecimal.ZERO;
        if (ArrayUtils.isNotEmpty(physicalCouponIds)) {
            couponAmount = physicalCouponDAO.sumCouponAmount(physicalCouponIds, shopId);
        }
        BigDecimal totalPaidAmount = getTotalPaidAmount(orderNumber, "XPOS_ORDER");
        BigDecimal paymentAmount = getPaymentAmount(orderNumber, "XPOS_ORDER");

        BigDecimal needPaymentAmount = totalPaidAmount
                .multiply(discount)
                .subtract(couponAmount)
                .subtract(paymentAmount);
        //需付款金额不能为负数
        if (needPaymentAmount.compareTo(BigDecimal.ZERO) == -1){
            return BigDecimal.ZERO;
        }

        if (needPaymentAmount.compareTo(balance) == 1) {
            return balance;
        }
        return needPaymentAmount;

    }

    /**
     * 更新订单实际支付金额
     * @param orderNumber
     * @param orderType
     * @param memberId
     * @param shopId
     * @param physicalCouponIds
     */
    private void updateOrderActualPaid(String orderNumber, String orderType, Long memberId, Long shopId, Long[] physicalCouponIds) {
        //如果是爱客仕订单类型，则需要处理订单中的实付金额
        if (StringUtils.equals(orderType,"XPOS_ORDER")){
            //会员折扣
            BigDecimal discount = BigDecimal.ONE;
            if (memberId != null) {
                discount = xMemberService.getMemberDiscountByMemberIdAndShopId(memberId, shopId);
            }
            //实体券抵扣金额累计
            BigDecimal couponAmount = BigDecimal.ZERO;
            if (ArrayUtils.isNotEmpty(physicalCouponIds)) {
                couponAmount = physicalCouponDAO.sumCouponAmount(physicalCouponIds, shopId);
            }
            //更新xpos订单中的实付金额
            orderDAO.updateOrderActualPaid(orderNumber,discount,couponAmount);

        }
    }


    /**
     * 检查是否有使用预付卡支付记录
     *
     */
    private boolean hasPrepaidCardTransaction(String orderNumber, String orderType) {
        return orderDAO.hasPrepaidCardTransaction(orderNumber, orderType);
    }


    /**
     * 预付卡支付退款
     *
     *
     * @param serial the serial
     * @param orderNumber the order number
     * @param refundParam the refund param
     * @return the refund
     */
    @Transactional
    public Refund refundForPrepaidCard(String serial, String orderNumber, PaymentRefundParam refundParam) {

        //获取支付
        PrepaidCardTransaction transaction = prepaidCardTransactionDAO.getBySerial(serial);
        if (transaction == null) {
            return Refund.NON_TRANSACTION;
        }

        String orderType = null;
        if (transaction.getOrderNumber() != null) {
            orderType = "XPOS_ORDER";
        } else if (transaction.getThirdOrderCode() != null) {
            orderType = "THIRD_ORDER";
        }

        if (!(StringUtils.equalsIgnoreCase(refundParam.getOrderType(), orderType) &&
                StringUtils.equalsIgnoreCase(orderNumber, transaction.getOrderNumber()))) {
            return Refund.TRANSACTION_UNMATCHED; //订单和支付不匹配
        }

        //检查订单状态是否可退款
        Boolean availableOrderRefund = checkAvailableRefund(orderNumber,orderType);
        if (!availableOrderRefund){
            return Refund.ORDER_UNABLE_REFUND;
        }


        //退款操作
        Boolean refundTransactionSuccess = refundPrepaidTransaction(serial, orderNumber);
        if (!refundTransactionSuccess){
            return Refund.REFUND_TRANSACTION_FAILED;
        }else{
        	//发送退款成功后发送短信
            sendPrepaidCardRefundSuccess(orderNumber);
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
     * 离线订单的预付卡支付流水退款
     */
    @Transactional
    public boolean refundForOfflinePrepaidCard(PrepaidCardTransaction transaction) {
    	if (transaction == null) {
    		return false;
    	}
    	
    	
    	//退款操作
    	Boolean refundTransactionSuccess = refundPrepaidTransaction(transaction.getSerial(), transaction.getOrderNumber());
    	if (!refundTransactionSuccess){
    		return false;
    	}else{
    		//发送退款成功后发送短信
    		sendPrepaidCardRefundSuccess(transaction.getOrderNumber());
    	}
    	
    	return true;
    	
    }

    /**
     * 退预付卡支付
     * @param serial
     * @return
     */
    private Boolean refundPrepaidTransaction(String serial, String orderNumber) {
        if (prepaidCardTransactionDAO.refundTransaction(serial, orderNumber) > 0) {
            return true;
        }
        return false;
    }

}
