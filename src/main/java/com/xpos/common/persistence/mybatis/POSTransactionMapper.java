package com.xpos.common.persistence.mybatis;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.ibatis.annotations.Param;

import com.xkeshi.pojo.vo.shift.POSPayTransactionVO;
import com.xkeshi.pojo.vo.transaction.BaseTransactionVO;
import com.xpos.common.entity.Order.Status;
import com.xpos.common.entity.pos.POSTransaction;
import com.xpos.common.entity.pos.POSTransaction.POSTransactionType;
import com.xpos.common.persistence.BaseMapper;

public interface POSTransactionMapper extends BaseMapper<POSTransaction>{

	BigDecimal countTotalAmount(@Param("whereClause")String whereClause);
	
	POSTransaction selectById(long id);

	POSTransaction selectByCode(String code);

	int save(POSTransaction posTransaction);

	int updateById(POSTransaction posTransaction);

	public BaseTransactionVO getPayTransactionGroupByOrderNumber(@Param("orderNumber") String orderNum);

	/**
	 * 官方POS刷卡支付
	 * @param operatorSessionCode
	 */
	POSPayTransactionVO getOrderTotalAmountByOperatorSessionCode(@Param("operatorSessionCode")String operatorSessionCode ,  @Param("type") POSTransactionType  type );
	
	/**
	 * 第三方POS刷卡支付
	 * @param operatorSessionCode
	 */
	POSPayTransactionVO getThirdOrderTotalAmountByOperatorSessionCode(@Param("operatorSessionCode")String operatorSessionCode ,  @Param("type") POSTransactionType  type );
	
	/**统计爱客仕或第三方订单在一定时间范围内的刷卡统计总笔数*/
	public int countByOperatorAndType(@Param("operatorId")Long operatorId, @Param("orderType")String orderType, 
			@Param("startTime")Date startTime, @Param("endTime")Date endTime, @Param("orderStatus") Status orderStatus);

	/**统计爱客仕或第三方订单在一定时间范围内的刷卡统计总金额*/
	public BigDecimal getAmountByOperatorAndType(@Param("operatorId")Long operatorId, @Param("orderType")String orderType, 
			@Param("startTime")Date startTime, @Param("endTime")Date endTime, @Param("orderStatus") Status orderStatus);

	int insertOrUpdate(POSTransaction posTransaction);

}