package com.xkeshi.dao;

import com.xkeshi.pojo.po.ThirdOrder;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;

/**
 * Created by david-y on 2015/1/21.
 */
public interface ThirdOrderDAO extends BaseDAO<ThirdOrder> {


    BigDecimal getActuallyPaidAmount(@Param("orderNumber") String orderNumber);

    /**
     * 检查第三方订单是否可退款（仅成功支付的订单可退款）
     *
     * Check available refund.
     *
     * @param orderNumber the order number
     * @return the boolean
     */
    Boolean checkAvailableRefund(@Param("orderNumber") String orderNumber);

    void updateOrderRefundStatus(@Param("orderNumber") String orderNumber, @Param("refundStatus") Long refundStatus);
}
