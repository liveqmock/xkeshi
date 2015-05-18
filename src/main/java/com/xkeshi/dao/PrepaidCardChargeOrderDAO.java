package com.xkeshi.dao;

import com.xkeshi.common.db.Query;
import com.xkeshi.pojo.po.PrepaidCardChargeOrder;
import com.xkeshi.pojo.po.PrepaidChargeOrderSummary;
import com.xkeshi.pojo.vo.PrepaidChargeOrderSummaryVO;
import com.xkeshi.pojo.vo.param.PrepaidChargeListParam;
import com.xkeshi.pojo.vo.shift.PrepaidCardPayTransactionVO;
import com.xpos.common.entity.Order.Status;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created by david-y on 2015/1/21.
 */
public interface PrepaidCardChargeOrderDAO extends BaseDAO<PrepaidCardChargeOrder> {

    List<PrepaidCardChargeOrder> queryAPIPrepaidCardChargeRecordList(@Param("query") Query query, @Param("date") Date date, @Param("shopId") Long shopId);

    boolean hasChargeSuccessOrderByShopId(@Param("memberId") Long memberId, @Param("shopId") Long shopId);
    boolean hasChargeSuccessOrderByMerchantId(@Param("memberId") Long memberId, @Param("merchantId") Long merchantId);

    BigDecimal getActuallyPaidAmount(@Param("orderNumber") String orderNumber);
    BigDecimal getTotalPaidAmount(@Param("orderNumber") String orderNumber);

    void updatePrepaidCardChargeChannel(@Param("orderNumber") String orderNumber, @Param("channelId") Long channelId);

    BigDecimal getTotalChargeAmountByPrepaidCardId(@Param("prepaidCardId") Long prepaidCardId);

    int countPrepaidCardChargeOrderListByShop(@Param("param") PrepaidChargeListParam param);

    int countPrepaidCardChargeOrderListByMerchant(@Param("param") PrepaidChargeListParam param);

    List<PrepaidCardChargeOrder> queryPrepaidCardChargeOrderListByShop(@Param("query") Query query, @Param("param") PrepaidChargeListParam param);

    List<PrepaidCardChargeOrder> queryPrepaidCardChargeOrderListByMerchant(@Param("query") Query query, @Param("param") PrepaidChargeListParam param);

    BigDecimal sumPrepaidCardChargeOrderAmountByMerchant(@Param("param") PrepaidChargeListParam param);
    BigDecimal sumPrepaidCardChargeOrderAmountByShop(@Param("param") PrepaidChargeListParam param);

    /**官方订单支付总金额统计*/
	PrepaidCardPayTransactionVO getOrderTotalAmountByOperatorSessionCode(@Param("operatorSessionCode") String operatorSessionCode);
	
	/**第三方订单支付总金额统计*/
	PrepaidCardPayTransactionVO getThirdOrderTotalAmountByOperatorSessionCode(@Param("operatorSessionCode") String operatorSessionCode);

    boolean isInitial(@Param("orderNumber") String orderNumber);


    PrepaidCardChargeOrder getByCode(@Param("orderNumber") String orderNumber);

    String getShopNameByOrderNumber(@Param("orderNumber") String orderNumber);

	/**统计爱客仕或第三方订单在一定时间范围内的预付卡支付统计总笔数*/
	public int countByOperatorAndType(@Param("operatorId")Long operatorId, @Param("orderType")String orderType, 
			@Param("startTime")Date startTime, @Param("endTime")Date endTime, @Param("orderStatus") Status orderStatus);

	/**统计爱客仕或第三方订单在一定时间范围内的预付卡支付统计总金额*/
	public BigDecimal getAmountByOperatorAndType(@Param("operatorId")Long operatorId, @Param("orderType")String orderType, 
			@Param("startTime")Date startTime, @Param("endTime")Date endTime, @Param("orderStatus") Status orderStatus);

    /**
     * 更新订单退款状态
     *
     * Update order refund status.
     *
     * @param orderNumber the order number
     * @param refundStatus the refund status
     */
    void updateOrderRefundStatus(@Param("orderNumber") String orderNumber, @Param("refundStatus") Long refundStatus);


    /**
     * 获取预付卡充值订单的打印信息
     *
     * @param orderNumber
     * @return
     */
    PrepaidChargeOrderSummary getPrepaidChargeOrderSummary(@Param("orderNumber") String orderNumber);
}
