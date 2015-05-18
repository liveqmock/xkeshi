package com.xpos.common.timer;

import org.springframework.beans.factory.annotation.Autowired;

import com.xkeshi.service.OrdersService;

public class UpdateOrderStatus {

	@Autowired
	private OrdersService orderService;
	
	
	//订单支付超时
	public void updateStatus() {
		orderService.updateTimeOutOrder();
	}
	
	public void resetOrderShopCounter(){
		orderService.resetOrderShopCounter();
	}
	
}