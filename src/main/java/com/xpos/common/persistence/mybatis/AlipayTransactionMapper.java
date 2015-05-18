package com.xpos.common.persistence.mybatis;

import com.xkeshi.pojo.po.alipay.AlipayTransaction;
import com.xkeshi.pojo.po.AlipayTransactionDetail;
import com.xkeshi.pojo.po.AlipayTransactionList;
import com.xkeshi.pojo.vo.shift.AlipayTransactionVO;
import com.xkeshi.pojo.vo.transaction.BaseTransactionVO;
import com.xpos.common.entity.Order.Status;
import com.xpos.common.utils.Pager;

import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public interface AlipayTransactionMapper{
	
	public int insert(AlipayTransaction po);

	public AlipayTransaction selectById(long id);

	public int updateById(AlipayTransaction po);

	public AlipayTransaction selectBySerial(@Param("serial")String serial);

    public List<AlipayTransactionList> AlipayQRCodeList(@Param("key")String key,@Param("businessType")String businessType, @Param("alipayTransactionVo")AlipayTransactionList alipayTransactionList, @Param("pager")Pager<AlipayTransactionList> pager);
    
    public int AlipayQRCodeListSize(@Param("key")String key,@Param("businessType")String businessType, @Param("alipayTransactionVo")AlipayTransactionList alipayTransactionList);

    public AlipayTransactionDetail findAlipayTransactionById(Long id);
	/**
	 * 获取爱客仕(系统订单)的支付宝支付成功的总金额
	 * @param operatorSessionCode 操作员当班回话
	 */
	public AlipayTransactionVO getOrderTotalAmountByOperatorSessionCode(@Param("operatorSessionCode")String operatorSessionCode);

	/**获取第三方订单支付支付成功的总金额
	 * @param operatorSessionCode 操作员当班回话
	 * */
	public AlipayTransactionVO getThirdOrderTotalAmountByOperatorSessionCode(@Param("operatorSessionCode")String operatorSessionCode);

	public BaseTransactionVO getPayTransactionGroupByOrderNumber(@Param("orderNumber") String orderNum);

	/**统计爱客仕或第三方订单在一定时间范围内的支付宝统计总笔数*/
	public int countByOperatorAndType(@Param("operatorId")Long operatorId, @Param("orderType")String orderType, 
			@Param("startTime")Date startTime, @Param("endTime")Date endTime, @Param("orderStatus") Status orderStatus);

	/**统计爱客仕或第三方订单在一定时间范围内的支付宝统计总金额*/
	public BigDecimal getAmountByOperatorAndType(@Param("operatorId")Long operatorId, @Param("orderType")String orderType, 
			@Param("startTime")Date startTime, @Param("endTime")Date endTime, @Param("orderStatus") Status orderStatus);

	public AlipayTransaction getByOrderNumberAndSerial(@Param("orderNumber")String orderNumber,@Param("serial")String serial);

	public boolean updateStatusById(@Param("id")Long id, @Param("alipayPaymentStatus")int alipayPaymentStatus);

}
