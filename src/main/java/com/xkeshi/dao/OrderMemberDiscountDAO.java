package com.xkeshi.dao;

import com.xkeshi.pojo.po.OrderMemberDiscount;
import com.xkeshi.pojo.vo.shift.MemberDiscountVO;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;

/**
 * Created by david-y on 2015/1/23.
 */
public interface OrderMemberDiscountDAO extends BaseDAO<OrderMemberDiscount> {

	/**官方订单会员折扣 */
	MemberDiscountVO getOrderUseMemberDiscountByOperatorSessionCode( String operatorSessionCode);

    BigDecimal getMemberDiscountByOrderNumber(@Param("orderNumber") String orderNumber);

    void clearMemberDiscountByOrderNumber(@Param("orderNumber") String orderNumber);

}
