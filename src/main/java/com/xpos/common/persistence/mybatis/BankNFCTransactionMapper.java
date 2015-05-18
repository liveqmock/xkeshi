package com.xpos.common.persistence.mybatis;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.ibatis.annotations.Param;

import com.xkeshi.pojo.po.BankNFCTransaction;
import com.xkeshi.pojo.vo.shift.BankNFCPayTransactionVO;
import com.xkeshi.pojo.vo.transaction.BaseTransactionVO;
import com.xpos.common.entity.Order.Status;

public interface BankNFCTransactionMapper {
	
	int insert(BankNFCTransaction bankNFCTransaction);

	BankNFCTransaction selectById(long id);

	BankNFCTransaction selectBySerial(String serial);

	int updateBySerial(BankNFCTransaction transaction);
	/**官方订单NFC支付*/
	BankNFCPayTransactionVO getOrderTotalAmountByOperatorSessionCode(@Param("operatorSessionCode") String operatorSessionCode);
	
	/**第三方订单NFC支付*/
	BankNFCPayTransactionVO getThirdOrderTotalAmountByOperatorSessionCode(@Param("operatorSessionCode") String operatorSessionCode);

	public BaseTransactionVO getPayTransactionGroupByOrderNumber(@Param("orderNumber") String orderNum);


	/**统计爱客仕或第三方订单在一定时间范围内的NFC统计总笔数*/
	public int countByOperatorAndType(@Param("operatorId")Long operatorId, @Param("orderType")String orderType, 
			@Param("startTime")Date startTime, @Param("endTime")Date endTime, @Param("orderStatus") Status orderStatus);

	/**统计爱客仕或第三方订单在一定时间范围内的NFC统计总金额*/
	public BigDecimal getAmountByOperatorAndType(@Param("operatorId")Long operatorId, @Param("orderType")String orderType, 
			@Param("startTime")Date startTime, @Param("endTime")Date endTime, @Param("orderStatus") Status orderStatus);

	int insertOrUpdate(BankNFCTransaction bankNFCTransaction);
	
}