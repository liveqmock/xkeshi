package com.xkeshi.dao;

import com.xkeshi.pojo.po.Order;
import com.xkeshi.pojo.po.UnpaidOrderList;
import com.xkeshi.pojo.vo.UnpaidItemListVO;

import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by david-y on 2015/1/22.
 */
public interface OrderDAO {
    Order getByOrderNumber(@Param("orderNumber") String orderNumber);

    BigDecimal getPaymentAmountByOrderNumber(@Param("orderNumber") String orderNumber, @Param("orderType") String orderType);

    boolean hasPayingTransaction(@Param("orderNumber") String orderNumber, @Param("orderType") String orderType);

    /**
     * 更新订单支付状态
     *
     * Update order status.
     *
     * @param orderNumber the order number
     * @param status the status
     */
    void updateOrderStatus(@Param("orderNumber") String orderNumber, @Param("status") String status);

    /**
     * 更新预付卡充值订单支付状态
     *
     * Update prepaid card charge order status.
     *  @param orderNumber the order number
     * @param status the status
     */
    void updatePrepaidCardChargeOrderStatus(@Param("orderNumber") String orderNumber, @Param("status") int status);

    /**
     * 更新第三方订单状态
     *
     * Update third order status.
     *
     * @param orderNumber the order number
     * @param status the status
     */
    void updateThirdOrderStatus(@Param("orderNumber") String orderNumber, @Param("status") int status);

    boolean hasPrepaidCardTransaction(@Param("orderNumber") String orderNumber, @Param("orderType") String orderType);

    BigDecimal getActuallyPaidAmount(@Param("orderNumber") String orderNumber);
    BigDecimal getTotalPaidAmount(@Param("orderNumber") String orderNumber);

	/**
	 * 通过operatorSessionCode查询未支付订单的数量
	 * @param operatorSessionCode
	 * @return
	 */
	int getUnpaidOrderCountByOperatorSessionCode(@Param("operatorSessionCode") String operatorSessionCode);
	
	/**
	 * 通过opertorId查询未支付订单数量
	 * @param operatorId
	 * @return
	 */
	int getUnpaidOrderCountByOperatorId(@Param("operatorId") Long operatorId);
	
	/**
	 * 获取超时未支付的订单
	 */
	List<Order> getTimeOutOrderList();

	/**
	 * 通过operatorSessionCode查询未支付订单列表
	 * @param operatorSessionCode
	 * @return
	 */
	List<UnpaidOrderList> getUnpaidOrderListByOperatorSessionCode(@Param("operatorSessionCode")String operatorSessionCode);
	
	/**
	 * 通过operatorId查询未支付订单列表
	 * @param operatorId
	 * @return
	 */
	List<UnpaidOrderList> getUnpaidOrderListByOperatorId(@Param("operatorId")Long operatorId);

	/**
	 * 通过订单编号查询下单的商品
	 * @param orderNumber
	 * @return
	 */
	List<UnpaidItemListVO> getUnpaidOrderItemListByOrderNumber(String orderNumber);

	/** 更新爱客仕订单的支付方式 */
	int updateXPOSOrderPaymentChannel(@Param("orderNumber")String orderNumber, @Param("type")String type);
	
	void updateOrderActualPaid(@Param("orderNumber") String orderNumber, @Param("discount") BigDecimal discount, @Param("couponAmount") BigDecimal couponAmount);

	int resetOrderShopCounter();

    /**
     * 是否可退款（支付成功、部分支付成功、部分退款状态可以退款）
     *
     * Check available refund.
     *
     * @param orderNumber the order number
     * @return the boolean
     */
    Boolean checkAvailableRefund(@Param("orderNumber") String orderNumber);

    /**
     * 更新退款状态
     *
     * Update order refund status.
     *
     * @param orderNumber the order number
     * @param refundStatus
     */
    void updateOrderRefundStatus(@Param("orderNumber") String orderNumber, @Param("refundStatus") String refundStatus);

    /**
     * 检查是否包含成功的支付
     *
     * Has transaction successful.
     *
     * @param orderNumber the order number
     * @param orderType
     * @return the boolean
     */
    Boolean hasTransactionSuccessful(@Param("orderNumber") String orderNumber, @Param("orderType") String orderType);

    String getShopNameByOrderNumber(@Param("orderNumber") String orderNumber);

	void updateOrderManager(@Param("orderNumber")String orderNumber, @Param("operatorId")Long operatorId);

    void updateMemberIdInOrder(@Param("orderNumber") String orderNumber, @Param("memberId") Long memberId);

	int insert(Order orderPO);

	int updateOfflineOrder(Order orderPO);
}
