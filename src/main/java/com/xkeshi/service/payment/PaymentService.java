package com.xkeshi.service.payment;

import com.drongam.hermes.entity.SMS;
import com.xkeshi.common.em.OrderPaymentStatus;
import com.xkeshi.common.em.Payment;
import com.xkeshi.common.globality.GlobalSource;
import com.xkeshi.dao.*;
import com.xkeshi.pojo.po.*;
import com.xkeshi.pojo.po.Order;
import com.xkeshi.pojo.vo.param.payment.CashPaymentParam;
import com.xkeshi.pojo.vo.param.payment.PaymentParam;
import com.xkeshi.service.XMemberService;
import com.xkeshi.utils.DateUtils;
import com.xkeshi.utils.EncryptionUtil;
import com.xkeshi.utils.Tools;
import com.xpos.common.entity.*;
import com.xpos.common.entity.Order.Type;
import com.xpos.common.entity.face.Business.BusinessType;
import com.xpos.common.service.SMSService;

import org.apache.commons.lang.math.RandomUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

/**
 * Created by david-y on 2015/1/22.
 */
@SuppressWarnings({"unchecked","unused"})
@Service
public class PaymentService {

    @Autowired(required = false)
    private OrderDAO orderDAO;
    @Autowired(required = false)
    private PrepaidDAO prepaidDAO;

    @Autowired(required = false)
    private OrderMemberDiscountDAO orderMemberDiscountDAO;
    @Autowired(required = false)
    private MemberDAO memberDAO;
    @Autowired(required = false)
    private MemberTypeDAO memberTypeDAO;
    @Autowired(required = false)
    private PhysicalCouponDAO physicalCouponDAO;
    @Autowired(required = false)
    private PhysicalCouponOrderDAO physicalCouponOrderDAO;
    @Autowired(required = false)
    CashTransactionDAO cashTransactionDAO;
    @Autowired(required = false)
    PrepaidCardTransactionDAO prepaidCardTransactionDAO;
    @Autowired(required = false)
    PrepaidCardChargeRulesDAO prepaidCardChargeRulesDAO;

    @Autowired(required = false)
    private PrepaidCardChargeOrderDAO prepaidCardChargeOrderDAO;
    @Autowired(required = false)
    private ThirdOrderDAO thirdOrderDAO;

    @Autowired
    private XMemberService xMemberService;

    @Autowired
    private SMSService smsService;


    /**
     *
     * 支付成功后,更新订单付款状态
     *
     * Update order status.
     *
     * @param orderNumber the order number
     * @param orderType the order type
     */
    protected void updateOrderPaymentStatus(String orderNumber, String orderType) {
        updateOrderPaymentStatus(orderNumber, orderType, false);
    }

    /**
     * 支付成功后,更新订单付款状态
     *
     * @param orderNumber the order number
     * @param orderType the order type
     * @param paymentByPrepaidCard the payment by prepaid card
     */
    protected void updateOrderPaymentStatus(String orderNumber, String orderType, boolean paymentByPrepaidCard) {
        BigDecimal totalPaidAmount = getTotalPaidAmount(orderNumber, orderType);

        if (totalPaidAmount == null){
            return;
        }
        //是否完全支付
        BigDecimal paymentAmount = getPaymentAmount(orderNumber, orderType);

        if (StringUtils.equals(orderType, "XPOS_ORDER")){//(订单总金额*折扣-优惠金额-已付金额=提交金额)
            //会员折扣
            BigDecimal discount = orderMemberDiscountDAO.getMemberDiscountByOrderNumber(orderNumber);
            //实体券抵扣金额累计
            BigDecimal couponAmount = physicalCouponOrderDAO.sumAmountByOrderNumber(orderNumber);
            if (totalPaidAmount
                    .multiply(discount).setScale(2, RoundingMode.HALF_UP)
                    .subtract(couponAmount)
                    .subtract(paymentAmount).compareTo(BigDecimal.ZERO) <= 0) {
                updateOrderStatus(orderNumber, orderType, OrderPaymentStatus.SUCCESS,paymentByPrepaidCard);
            } else {
                updateOrderStatus(orderNumber, orderType, OrderPaymentStatus.PARTIAL_PAYMENT,paymentByPrepaidCard);
            }
        }else{//(订单总金额-已付金额=提交金额)
            if (StringUtils.equals(orderType, "XPOS_PREPAID")){
                //设置预付卡status=1
                prepaidDAO.updateStatusByChargeOrderCode(orderNumber, 1);
            }

            if (totalPaidAmount.subtract(paymentAmount).compareTo(BigDecimal.ZERO) == 0) {
                updateOrderStatus(orderNumber, orderType, OrderPaymentStatus.SUCCESS,paymentByPrepaidCard);
            } else {
                updateOrderStatus(orderNumber, orderType, OrderPaymentStatus.PARTIAL_PAYMENT,paymentByPrepaidCard);
            }
        }


    }

    /**
     * 更新订单退款状态
     *
     * Update order refund status.
     *
     * @param orderNumber the order number
     * @param orderType the order type
     */
    protected void updateOrderRefundStatus(String orderNumber, String orderType) {
        //订单是否还有成功的支付
        Boolean hasTransaction = orderDAO.hasTransactionSuccessful(orderNumber,orderType);

        if (StringUtils.equals(orderType, "XPOS_ORDER")){
            String refundStatus = hasTransaction ? "PARTIAL_REFUND" : "REFUND" ;
            orderDAO.updateOrderRefundStatus(orderNumber,refundStatus);
        } else if(StringUtils.equals(orderType,"XPOS_PREPAID")){
            Long refundStatus = hasTransaction ? GlobalSource.getIDByName(GlobalSource.metaOrderPaymentStatusList,"部分退款") : GlobalSource.getIDByName(GlobalSource.metaOrderPaymentStatusList,"退款") ;
            prepaidCardChargeOrderDAO.updateOrderRefundStatus(orderNumber,refundStatus);
        }else if(StringUtils.equals(orderType,"THIRD_ORDER")){
            Long refundStatus = hasTransaction ? GlobalSource.getIDByName(GlobalSource.metaOrderPaymentStatusList,"部分退款") : GlobalSource.getIDByName(GlobalSource.metaOrderPaymentStatusList,"退款") ;
            thirdOrderDAO.updateOrderRefundStatus(orderNumber, refundStatus);
        }
    }

    private BigDecimal getActuallyPaidAmount(String orderNumber, String orderType) {
        BigDecimal actuallyPaidAmount = null;

        if (StringUtils.equals(orderType, "XPOS_ORDER")){
            actuallyPaidAmount = orderDAO.getActuallyPaidAmount(orderNumber);
        } else if(StringUtils.equals(orderType,"XPOS_PREPAID")){
            actuallyPaidAmount = prepaidCardChargeOrderDAO.getActuallyPaidAmount(orderNumber);
        }else if(StringUtils.equals(orderType,"THIRD_ORDER")){
            actuallyPaidAmount = thirdOrderDAO.getActuallyPaidAmount(orderNumber);
        }
        return actuallyPaidAmount;
    }


    BigDecimal getTotalPaidAmount(String orderNumber, String orderType) {
        BigDecimal totalPaidAmount = null;

        if (StringUtils.equals(orderType, "XPOS_ORDER")){
            totalPaidAmount = orderDAO.getTotalPaidAmount(orderNumber);
        } else if(StringUtils.equals(orderType,"XPOS_PREPAID")){
            totalPaidAmount = prepaidCardChargeOrderDAO.getActuallyPaidAmount(orderNumber);
        }else if(StringUtils.equals(orderType,"THIRD_ORDER")){
            totalPaidAmount = thirdOrderDAO.getActuallyPaidAmount(orderNumber);
        }
        return totalPaidAmount;
    }


    /**
     *
     * 更新会员等级
     *
     * Update member type.
     *
     * @param prepaidOrder the order number
     */
    protected void updateMemberType(String prepaidOrder) {

        //获取充值规则
        PrepaidCardChargeRules rule = prepaidCardChargeRulesDAO.getByPrepaidChargeCode(prepaidOrder);
        if (rule != null && rule.getMemberTypeId() != null){
            memberDAO.updateMemberTypeIdByPrepaidChargeCode(prepaidOrder,rule.getMemberTypeId());
        }

    }


    /**
     * 添加会员折扣到订单
     * @param orderNumber
     * @param paymentParam
     * @param shopId
     */
    protected void insertMemberDiscountToOrder(String orderNumber, PaymentParam paymentParam, Long shopId) {
        BigDecimal memberDiscount = xMemberService.getMemberDiscountByMemberIdAndShopId(paymentParam.getMemberId(), shopId);
        if (memberDiscount != null) {
            insertOrderDiscount(orderNumber, paymentParam.getMemberId(), shopId, memberDiscount);
            setMemberIdInOrder(orderNumber, paymentParam.getMemberId());
        }
    }

    /**
     * 在订单中设置会员ID（如果使用会员功能）
     *
     * @param orderNumber
     * @param memberId
     */
    private void setMemberIdInOrder(String orderNumber, Long memberId) {
        orderDAO.updateMemberIdInOrder(orderNumber, memberId);
    }

    protected void clearMemberDiscountByOrderNumber(String orderNumber) {
        orderMemberDiscountDAO.clearMemberDiscountByOrderNumber(orderNumber);
    }


    /**
     * 更新预付卡充值订单的充值渠道
     * @param orderNumber
     * @param channelId
     */
    protected void updateOrderChargeChannel(String orderType, String orderNumber, Long channelId) {
    	if (StringUtils.equals(orderType, "XPOS_PREPAID")){
    		prepaidCardChargeOrderDAO.updatePrepaidCardChargeChannel(orderNumber, channelId);
    	}else if(StringUtils.equals(orderType, "XPOS_ORDER")){
    		Type type = Type.findByCode(channelId);
    		orderDAO.updateXPOSOrderPaymentChannel(orderNumber, type.name());
    	}
    }


    /**
     * 添加实体券信息到订单
     * @param orderNumber
     * @param paymentParam
     * @param shopId
     * @return
     */
    protected boolean insertPhysicalCoupons(String orderNumber, PaymentParam paymentParam, Long shopId) {
        //添加实体券使用记录
        Long[] physicalCouponIds = paymentParam.getPhysicalCouponIds();
        if (ArrayUtils.isNotEmpty(physicalCouponIds)){
            for (int i = 0; i < physicalCouponIds.length; i++) {
                //检查实体券的合法性
                if (!physicalCouponDAO.enableCoupon(physicalCouponIds[i], shopId)) {
                    return true;
                }
            }

            //添加到实体券订单
            insertPhysicalCouponOrder(orderNumber,physicalCouponIds);
        }
        return false;
    }

     /**
     * 删除订单实体券信息
     * @param orderNumber
     * @return
     */
    protected void clearPhysicalCouponsByOrderNumber(String orderNumber) {
        physicalCouponOrderDAO.clearPhysicalCouponsByOrderNumber(orderNumber);
    }

    /**
     * 支付前检查订单状态
     * @param orderNumber
     * @param paymentParam
     * @return
     */
    protected Payment checkOrderForPayment(String orderNumber, PaymentParam paymentParam){
        String orderType = StringUtils.upperCase(paymentParam.getOrderType());

        //检查订单状态
        if (StringUtils.equals(orderType,"XPOS_ORDER")){
            Order order = orderDAO.getByOrderNumber(orderNumber);
            if (order != null ) {
                if (StringUtils.equals(order.getStatus(),"UNPAID")
                        || StringUtils.equals(order.getStatus(),"FAILED")
                        || StringUtils.equals(order.getStatus(),"PARTIAL_PAYMENT")
                        ){

                    return checkPayment(orderNumber, paymentParam, orderType);

                }else {
                    return Payment.INVALID_ORDER_STATUS;
                }
            } else {
                return Payment.NON_ORDER;
            }
        } else if (StringUtils.equalsIgnoreCase(orderType, "XPOS_PREPAID")) {
            PrepaidCardChargeOrder order = prepaidCardChargeOrderDAO.getByProperty(PrepaidCardChargeOrder.class, "code",orderNumber);
            if (order != null ) {
                if (order.getChargeStatusId().intValue() == 2){
                    return checkPayment(orderNumber, paymentParam, orderType);
                }else {
                    return Payment.INVALID_ORDER_STATUS;
                }
            }else {
                return Payment.NON_ORDER;
            }
        } else if (StringUtils.equalsIgnoreCase(orderType, "THIRD_ORDER")) {
            ThirdOrder order = thirdOrderDAO.getByProperty(ThirdOrder.class, "thirdOrderCode",orderNumber);
            if (order != null ) {
                if (order.getThirdOrderPaymentStatusId().intValue() == 2){
                    return checkPayment(orderNumber, paymentParam, orderType);
                }else {
                    return Payment.INVALID_ORDER_STATUS;
                }
            }else {
                return Payment.NON_ORDER;
            }
        }
        return Payment.CLIENT_PARAM_ERROR;
    }

    private Payment checkPayment(String orderNumber, PaymentParam paymentParam, String orderType) {
        //检查订单是否有相关支付中的支付
        boolean hasPayingTransaction = hasPayingTransaction(orderNumber,orderType);
        if(hasPayingTransaction){
            return Payment.HAS_DEALING_TRANSACTION;
        }

        //获取订单已支付的支付流水的汇总金额
        BigDecimal paymentAmount = getPaymentAmount(orderNumber,orderType);
        //提交的金额
        BigDecimal submitAmount = paymentParam.getAmount();
        //订单的总金额
        BigDecimal totalPaidAmount = getTotalPaidAmount(orderNumber, orderType);
        if (totalPaidAmount == null){
            return Payment.NON_ORDER;
        }
        //会员折扣
        BigDecimal discount = BigDecimal.ONE;
        //实体券抵扣金额累计
        BigDecimal couponAmount = BigDecimal.ZERO;


        //预付卡充值时必须一次性支付
        if (StringUtils.equalsIgnoreCase(orderType, "XPOS_PREPAID")
                && submitAmount.compareTo(totalPaidAmount) != 0) {
            return Payment.PARTIAL_PREPAID_CHARGE;
        } else if (StringUtils.equalsIgnoreCase(orderType, "XPOS_ORDER")) {
            discount = orderMemberDiscountDAO.getMemberDiscountByOrderNumber(orderNumber);
            couponAmount = physicalCouponOrderDAO.sumAmountByOrderNumber(orderNumber);
        }

        if (totalPaidAmount == null){
            return Payment.NON_ORDER;
        }

        //判断提交金额是否大于可以支付金额
        if (paymentAmount.compareTo(BigDecimal.ZERO) == 0) { //首次支付
            return Payment.FIRST_PAYMENT;
        } else if(totalPaidAmount
                .multiply(discount).setScale(2, RoundingMode.HALF_UP)
                .subtract(couponAmount).compareTo(BigDecimal.ZERO) < 0){
            return Payment.NOT_FIRST_PAYMENT;
        } else if (totalPaidAmount
                .multiply(discount).setScale(2, RoundingMode.HALF_UP)
                .subtract(couponAmount)
                .subtract(paymentAmount).compareTo(submitAmount) == -1 ){
            return Payment.OVER_RANGE;
        } else {
            return Payment.NOT_FIRST_PAYMENT;
        }



    }


    /**
     *
     * 更新订单支付状态
     *
     * Update order status.
     *  @param orderNumber the order number
     * @param orderType
     * @param status the status
     */
    private void updateOrderStatus(String orderNumber, String orderType, OrderPaymentStatus status ,boolean paymentByPrepaidCard) {
        String statusStr = status.toString();
        if (StringUtils.equals(orderType,"XPOS_ORDER")){
            orderDAO.updateOrderStatus(orderNumber, statusStr);

            //如果是预付卡支付成功需要发送成功短信
            if (paymentByPrepaidCard && (status.equals(OrderPaymentStatus.SUCCESS) || status.equals(OrderPaymentStatus.PARTIAL_PAYMENT))) {
                sendPrepaidCardPaymentSuccess(orderNumber);
            }

        } else if (StringUtils.equalsIgnoreCase(orderType, "XPOS_PREPAID")) {
            orderDAO.updatePrepaidCardChargeOrderStatus(orderNumber, status.getValue());
            if(status.equals(OrderPaymentStatus.SUCCESS)){
            	//如果是首充支付成功，需要初始化预付卡密码以及短信通知
            	boolean isInitial = prepaidCardChargeOrderDAO.isInitial(orderNumber);
            	if (isInitial) {
            		initPrepaidCardPassword(orderNumber);
                    //设置预付卡status=1
                    prepaidDAO.updateStatusByChargeOrderCode(orderNumber, 1);
            	} else {
            		sendRechargeMessage(orderNumber);
            	}
            }

        } else if (StringUtils.equalsIgnoreCase(orderType, "THIRD_ORDER")) {
            orderDAO.updateThirdOrderStatus(orderNumber,  status.getValue());
        }
    }


    /**
     * 更新订单的实付金额（实付金额 = 订单总金额*折扣-优惠券金额）
     *
     * Update order actual amount.
     *
     * @param orderNumber the order number
     */
    public void updateOrderActualAmount(String orderNumber) {
        BigDecimal discount = orderMemberDiscountDAO.getMemberDiscountByOrderNumber(orderNumber);
        BigDecimal couponAmount = physicalCouponOrderDAO.sumAmountByOrderNumber(orderNumber);
        orderDAO.updateOrderActualPaid(orderNumber, discount, couponAmount);
    }


    private void sendRechargeMessage(String orderNumber) {
        PrepaidCard prepaidCard = prepaidDAO.getByPrepaidCardChargeOrderNumber(orderNumber);
        if (prepaidCard == null) {
            return;
        }
        //发送短信
        String mobileNumber = prepaidDAO.getMemberMobileNumberById(prepaidCard.getId());
        String dateStr = DateUtils.formatDate(new Date(),"MM月dd日HH时mm分");
        PrepaidCardChargeOrder order = prepaidCardChargeOrderDAO.getByCode(orderNumber);
        String shopName = prepaidCardChargeOrderDAO.getShopNameByOrderNumber(orderNumber);
        String content = StringUtils.join("您于", dateStr, "在",shopName,"充值", order.getTotalAmount(),
                "元，当前余额：", prepaidCard.getBalance(), "元。");
        SMS sms = new SMS();
		sms.setMobile(mobileNumber);
		sms.setMessage(content);
		smsService.sendSMSAndDeductions(prepaidCard.getBusinessId() ,prepaidCard.getBusinessTypeId(),sms,null,"为预付卡充值,发送成功短信" );
    }

    private void sendPrepaidCardPaymentSuccess(String orderNumber) {
        PrepaidCard prepaidCard = prepaidDAO.getByOrderNumber(orderNumber);
        if (prepaidCard == null) {
            return;
        }
        String mobileNumber = prepaidDAO.getMemberMobileNumberById(prepaidCard.getId());
        //预付卡支付的金额
        BigDecimal paidAmount = prepaidCardTransactionDAO.sumAmountByOrderNumber(orderNumber);
        String shopName = orderDAO.getShopNameByOrderNumber(orderNumber);
        String content = StringUtils.join("您在",shopName,"商户使用预付卡成功支付", paidAmount,
                "元，当前余额为", prepaidCard.getBalance(), "元。");
        //发送短信
        SMS sms = new SMS();
		sms.setMobile(mobileNumber);
		sms.setMessage(content);
		smsService.sendSMSAndDeductions(prepaidCard.getBusinessId() ,prepaidCard.getBusinessTypeId(),sms,null,"使用预付卡消费,发送短信" );
    }

    void sendPrepaidCardRefundSuccess(String orderNumber) {
        PrepaidCard prepaidCard = prepaidDAO.getByOrderNumber(orderNumber);
        if (prepaidCard == null) {
            return;
        }
        String mobileNumber = prepaidDAO.getMemberMobileNumberById(prepaidCard.getId());
        //预付卡支付的金额
        BigDecimal paidAmount = prepaidCardTransactionDAO.sumRefundAmountByOrderNumber(orderNumber);
        String shopName = orderDAO.getShopNameByOrderNumber(orderNumber);
        String dateStr = DateUtils.formatDate(new Date(), "MM月dd日HH时mm分");
        String content = StringUtils.join("您的",shopName,"预付卡于",dateStr,"收到一笔退款：", paidAmount,
                "元，当前余额为", prepaidCard.getBalance(), "元。");
        //发送短信
        SMS sms = new SMS();
        sms.setMobile(mobileNumber);
        sms.setMessage(content);
        smsService.sendSMSAndDeductions(prepaidCard.getBusinessId() ,prepaidCard.getBusinessTypeId(),sms,null,"预付卡退款,发送短信" );
    }

    //初始化预付卡密码（并发送短信）
    private void initPrepaidCardPassword(String orderNumber) {
        //初始密码
        String rawPassword = StringUtils.rightPad(String.valueOf(RandomUtils.nextInt(999999)), 6, "0");
        String salt = EncryptionUtil.getSalt();
        String password = EncryptionUtil.encodePassword(rawPassword, salt);

        PrepaidCard prepaidCard = prepaidDAO.getByPrepaidCardChargeOrderNumber(orderNumber);
        if (prepaidCard == null) {
            return;
        }
        Long prepaidCardId = prepaidCard.getId();
        prepaidDAO.initPrepaidCardPassword(prepaidCardId, password, salt);

        //发送短信
        String mobileNumber = prepaidDAO.getMemberMobileNumberById(prepaidCardId);
        String dateStr = DateUtils.formatDate(new Date(),"MM月dd日HH时mm分");
        PrepaidCardChargeOrder order = prepaidCardChargeOrderDAO.getByCode(orderNumber);
        String shopName = prepaidCardChargeOrderDAO.getShopNameByOrderNumber(orderNumber);
        
		String content = StringUtils.join("您于", dateStr, "在",shopName,"充值", order.getTotalAmount(),
                "元，成功开通预付卡。您的初始支付密码为：", rawPassword, "，当前余额：", prepaidCard.getBalance(), "元。",
                "修改密码请访问 http://member.xka.me/", prepaidCard.getBusinessTypeId().equals(1L) ? "m/" : "s/", prepaidCard.getBusinessId());

    	SMS sms = new SMS();
		sms.setMobile(mobileNumber);
		sms.setMessage(content);
		String  hiddenContent = StringUtils.join("您于", dateStr, "在",shopName,"充值", order.getTotalAmount(),
                                      "元，成功开通预付卡。您的初始支付密码为：******，当前余额：", prepaidCard.getBalance(), "元。");
		smsService.sendSMSAndDeductions(prepaidCard.getBusinessId() ,prepaidCard.getBusinessTypeId(),sms,hiddenContent,"开通预付卡,发送成功短信" );
    }

    /**
     * 添加现金支付记录
     *
     * Insert cash transaction.
     * @param orderNumber the order number
     * @param orderType
     * @param amount the amount
     * @param receivedAmount
     */
    protected void insertCashTransaction(String orderNumber, String orderType, BigDecimal amount, CashPaymentParam paymentParam) {
        CashTransaction po = new CashTransaction();
        if (StringUtils.equalsIgnoreCase(orderType, "XPOS_ORDER")) {
            po.setOrderNumber(orderNumber);
        } else if (StringUtils.equalsIgnoreCase(orderType, "XPOS_PREPAID")) {
            po.setPrepaidCardChargeOrderCode(orderNumber);
        } else if (StringUtils.equalsIgnoreCase(orderType, "THIRD_ORDER")) {
            po.setThirdOrderCode(orderNumber);
        }
        po.setAmount(amount);
        po.setReceived(paymentParam.getReceivedAmount());
        po.setReturned(amount.subtract(paymentParam.getReceivedAmount()));
        po.setCashPaymentStatusId(1L);//支付成功
        po.setSerial(paymentParam.getSerial());
        cashTransactionDAO.insert(po);
    }

    /**
     * 添加预付卡支付记录
     *
     * Insert PrepaidCard transaction.
     * @param prepaidCardId
     * @param orderNumber the order number
     * @param orderType
     * @param amount the amount
     */
    protected void insertPrepaidCardTransaction(Long prepaidCardId, String orderNumber, String orderType, BigDecimal amount,String serial) {

        PrepaidCardTransaction po = new PrepaidCardTransaction();
        if (StringUtils.equalsIgnoreCase(orderType, "XPOS_ORDER")) {
            po.setOrderNumber(orderNumber);
        } else if (StringUtils.equalsIgnoreCase(orderType, "THIRD_ORDER")) {
            po.setThirdOrderCode(orderNumber);
        }
        po.setAmount(amount);
        po.setPrepaidCardId(prepaidCardId);
        po.setPrepaidCardPaymentStatusId(1L);//支付成功
        po.setSerial(serial);
        prepaidCardTransactionDAO.insert(po);
    }

    /**
     * 添加实体券订单
     *
     * Insert physical coupon order.
     *  @param orderNumber the order number
     * @param physicalCouponIds the physical coupon id
     */
    private void insertPhysicalCouponOrder(String orderNumber, Long[] physicalCouponIds) {
        //获取实体券的价格
        for (Long id : physicalCouponIds) {
            PhysicalCoupon physicalCoupon = physicalCouponDAO.getByID(PhysicalCoupon.class, id);
            PhysicalCouponOrder po = new PhysicalCouponOrder();
            po.setOrderNumber(orderNumber);
            po.setPhysicalCouponId(id);
            po.setPhysicalCouponName(physicalCoupon.getName());
            po.setAmount(physicalCoupon.getAmount());
            physicalCouponOrderDAO.insert(po);
        }
    }

    /**
     * 添加订单会员折扣
     * @param orderNumber
     * @param memberId
     * @param shopId
     * @param memberDiscount
     */
    private void insertOrderDiscount(String orderNumber, Long memberId, Long shopId, BigDecimal memberDiscount) {
        OrderMemberDiscount po = new OrderMemberDiscount();
        po.setOrderNumber(orderNumber);
        po.setMemberTypeId(memberDAO.getMemberTypeIdById(memberId));
        //获取会员等级是否集团统一管理
        boolean centralManagementMember = memberTypeDAO.checkCentralManagementMemberByShopId(shopId);
        po.setBusinessType(centralManagementMember? "MERCHANT" :"SHOP");
        po.setDiscount(memberDiscount);
        po.setMemberId(memberId);
        orderMemberDiscountDAO.insert(po);
    }

    /**
     * 订单是否具有支付中的交易
     *
     * Has paying transaction.
     *
     * @param orderNumber the order number
     * @param orderType
     * @return the boolean
     */
    private boolean hasPayingTransaction(String orderNumber, String orderType) {
        return orderDAO.hasPayingTransaction(orderNumber,orderType);
    }


    /**
     * 获取某订单已经支付的金额汇总
     *
     * Gets payment amount.
     *
     * @param orderNumber the order number
     * @param orderType
     * @return the payment amount
     */
    BigDecimal getPaymentAmount(String orderNumber, String orderType) {
        return orderDAO.getPaymentAmountByOrderNumber(orderNumber,orderType);
    }


    /**
     * 检查订单状态是否可以退款
     * 仅支付成功、部分退款、部分支付状态的订单可以退款
     *
     * Check available refund.
     *
     * @param orderNumber the order number
     * @param orderType the order type
     * @return the boolean
     */
    public Boolean checkAvailableRefund(String orderNumber, String orderType) {
        if (StringUtils.equalsIgnoreCase(orderType, "XPOS_ORDER")) {
            return orderDAO.checkAvailableRefund(orderNumber);
        } else if (StringUtils.equalsIgnoreCase(orderType, "XPOS_PREPAID")) {
            return prepaidCardTransactionDAO.checkAvailableRefund(orderNumber);
        } else if (StringUtils.equalsIgnoreCase(orderType, "THIRD_ORDER")) {
            return thirdOrderDAO.checkAvailableRefund(orderNumber);
        }
        return false;
    }

}
