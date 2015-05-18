package com.xkeshi.dao;

import com.xkeshi.pojo.po.PhysicalCouponOrder;
import com.xkeshi.pojo.vo.shift.OrderPhysicalCouponVO;
import com.xpos.common.entity.Order.Status;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created by david-y on 2015/1/23.
 */
public interface PhysicalCouponOrderDAO extends BaseDAO<PhysicalCouponOrder> {

	/**
	 * 获取官方实体券数
	 * @param operatorSessionCode
	 */
	List<OrderPhysicalCouponVO> getOrderUsePhysicalCouponByOperatorSessionCode(@Param("operatorSessionCode")String operatorSessionCode);
	
	/**
	 * 获取第三方实体券数
	 * @param operatorSessionCode
	 */
	List<OrderPhysicalCouponVO> getThirdOrderUsePhysicalCouponByOperatorSessionCode(@Param("operatorSessionCode") String operatorSessionCode);

    /**
     * 累计的实体券金额
     *
     * Sum amount by order number.
     *
     * @param orderNumber the order number
     * @return the big decimal
     */
    BigDecimal sumAmountByOrderNumber(@Param("orderNumber") String orderNumber);
    
    /**统计爱客仕或第三方订单在一定时间范围内的实体券支付统计总笔数*/
	public int countByOperatorAndType(@Param("operatorId")Long operatorId, @Param("orderType")String orderType, 
			@Param("startTime")Date startTime, @Param("endTime")Date endTime, @Param("orderStatus") Status orderStatus);

	/**统计爱客仕或第三方订单在一定时间范围内的实体券支付统计总金额*/
	public BigDecimal getAmountByOperatorAndType(@Param("operatorId")Long operatorId, @Param("orderType")String orderType, 
			@Param("startTime")Date startTime, @Param("endTime")Date endTime, @Param("orderStatus") Status orderStatus);

    void clearPhysicalCouponsByOrderNumber(@Param("orderNumber") String orderNumber);

}
