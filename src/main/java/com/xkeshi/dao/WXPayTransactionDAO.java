package com.xkeshi.dao;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.ibatis.annotations.Param;

import com.xkeshi.pojo.vo.shift.WXPayTransactionVO;
import com.xkeshi.pojo.vo.transaction.BaseTransactionVO;
import com.xpos.common.entity.Order.Status;

/**
 * 
 * @author xk
 * 微信支付
 */
public interface WXPayTransactionDAO {

	
	WXPayTransactionVO getOrderTotalAmountByOperatorSessionCode(@Param("operatorSessionCode")  String operatorSessionCode);

	WXPayTransactionVO getThirdOrderTotalAmountByOperatorSessionCode(@Param("operatorSessionCode") String operatorSessionCode);

	public BaseTransactionVO getPayTransactionGroupByOrderNumber(@Param("orderNumber") String orderNum);
	
	/**统计爱客仕或第三方订单在一定时间范围内的微信支付统计总笔数*/
	public int countByOperatorAndType(@Param("operatorId")Long operatorId, @Param("orderType")String orderType, 
			@Param("startTime")Date startTime, @Param("endTime")Date endTime, @Param("orderStatus") Status orderStatus);

	/**统计爱客仕或第三方订单在一定时间范围内的微信支付统计总金额*/
	public BigDecimal getAmountByOperatorAndType(@Param("operatorId")Long operatorId, @Param("orderType")String orderType, 
			@Param("startTime")Date startTime, @Param("endTime")Date endTime, @Param("orderStatus") Status orderStatus);
}
