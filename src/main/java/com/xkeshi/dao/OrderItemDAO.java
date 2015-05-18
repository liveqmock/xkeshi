package com.xkeshi.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.xkeshi.pojo.po.OrderItem;

/**
 * Created by snoopy on 2015/2/11.
 */
public interface OrderItemDAO {
    
	
	List<OrderItem> getOrderItemByOrderId(@Param("orderId") Long orderId);

	/**
	 * 退还库存
	 * @param item_id
	 * @param quantity
	 */
	void returnItemInventory(@Param("itemId")Long itemId, @Param("quantity")Integer quantity);

	/** 批量插入订单商品 */
	int batchInsert(@Param("orderItemList")List<OrderItem> orderItemList);
	
}
