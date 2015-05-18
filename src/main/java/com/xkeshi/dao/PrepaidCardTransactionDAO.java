package com.xkeshi.dao;

import org.apache.ibatis.annotations.Param;

import com.xkeshi.pojo.po.PrepaidCardTransaction;
import com.xkeshi.pojo.vo.transaction.PrepaidCardTransactionVO;

import java.math.BigDecimal;

/**
 * Created by david-y on 2015/1/23.
 */
public interface PrepaidCardTransactionDAO extends BaseDAO<PrepaidCardTransaction> {
    int getConsumeCountByPrepaidCardId(@Param("prepaidCardId") Long prepaidCardId);

    PrepaidCardTransactionVO getPayTransactionGroupByOrderNumber(@Param("orderNumber")String orderNum);

    BigDecimal sumAmountByOrderNumber(@Param("orderNumber") String orderNumber);

    BigDecimal sumRefundAmountByOrderNumber(@Param("orderNumber") String orderNumber);

    /**
     * 检查是否支持退款（仅支付成功的充值订单允许退款）
     *
     * Check available refund.
     *
     * @param orderNumber the order number
     * @return the boolean
     *
     */
    Boolean checkAvailableRefund(@Param("orderNumber") String orderNumber);

    PrepaidCardTransaction getBySerial(@Param("serial") String serial);

    int refundTransaction(@Param("serial") String serial, @Param("orderNumber")String orderNumber);

	PrepaidCardTransaction getByOrderNumberAndSerial(@Param("orderNumber")String orderNumber, @Param("serial")String serial);
}
