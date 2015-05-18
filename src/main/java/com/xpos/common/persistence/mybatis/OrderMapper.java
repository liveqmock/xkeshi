package com.xpos.common.persistence.mybatis;

import java.math.BigDecimal;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.xkeshi.pojo.po.OrderTransaction;
import com.xpos.common.entity.Order;
import com.xpos.common.entity.face.Business;
import com.xpos.common.persistence.BaseMapper;
import com.xpos.common.searcher.OrderSearcher;
import com.xpos.common.utils.Pager;

public interface OrderMapper extends BaseMapper<Order>{

	/** 统计订单消费金额 */
	BigDecimal getOrderAmount(@Param("business")Business business, @Param("searcher")OrderSearcher searcher);

	List<Order> queryOrderList(@Param("business")Business business, @Param("pager")Pager<Order> pager,
			@Param("searcher")OrderSearcher searcher);

	int countOrders(@Param("business")Business business, @Param("pager")Pager<Order> pager,
			@Param("searcher")OrderSearcher searcher);
	
	Order getOrderByOrderNumber(@Param("orderNumber") String orderNumber);
	
	int insertOrder(Order order);

	int initOrderShopCounter(Long shopId);
	
	int requireOrderCounter(Long shopId);

	int increaseOrderCounter(@Param("shopId")Long shopId, @Param("currentCounter")int currentCounter);

	List<OrderTransaction> findTransactionListByOrderNumber(String orderNumber);

	int deleteById(@Param("id") Long id);
	/**根据ordernumber来查询实际支付金额*/
	BigDecimal getActuallyPaid(String orderNum);
		/**根据ordernumber来查询点单详情页面里面的数据*/
	Order findByOrderNumber(String orderNumber);
}