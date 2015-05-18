package com.xkeshi.dao;

import com.xkeshi.pojo.po.CashTransaction;
import com.xkeshi.pojo.vo.shift.CashPayTransactionVO;
import com.xkeshi.pojo.vo.transaction.BaseTransactionVO;
import com.xpos.common.entity.Order.Status;

import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by david-y on 2015/1/23.
 */
public interface CashTransactionDAO extends BaseDAO<CashTransaction> {

	public BaseTransactionVO getPayTransactionGroupByOrderNumber(@Param("orderNumber") String orderNum);

	/**官方订单现金支付*/
	public CashPayTransactionVO getOrderTotalAmountByOperatorSessionCode(@Param("operatorSessionCode")String operatorSessionCode);
	
	/**第三方订单现金支付*/
	public CashPayTransactionVO getThirdOrderTotalAmountByOperatorSessionCode(@Param("operatorSessionCode")String operatorSessionCode);

	/**统计爱客仕或第三方订单在一定时间范围内的现金统计总笔数*/
	public int countByOperatorAndType(@Param("operatorId")Long operatorId, @Param("orderType")String orderType, 
			@Param("startTime")Date startTime, @Param("endTime")Date endTime, @Param("orderStatus") Status orderStatus);

	/**统计爱客仕或第三方订单在一定时间范围内的现金统计总金额*/
	public BigDecimal getAmountByOperatorAndType(@Param("operatorId")Long operatorId, @Param("orderType")String orderType, 
			@Param("startTime")Date startTime, @Param("endTime")Date endTime, @Param("orderStatus") Status orderStatus);

    CashTransaction getBySerial(@Param("serial") String serial);

    /**
     * 现金退款
     *
     * Refund cash transaction.
     *
     * @param serial the serial
     * @return the boolean
     */
    int refundCashTransaction(@Param("serial") String serial);

	public int insertOrUpdate(CashTransaction cashTransaction);
}
