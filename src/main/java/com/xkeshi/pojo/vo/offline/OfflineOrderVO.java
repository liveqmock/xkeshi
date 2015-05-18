package com.xkeshi.pojo.vo.offline;

import java.util.List;


/**
 * 离线订单VO对象
 */
public class OfflineOrderVO {
	
	private OfflineOrderDetailVO offlineOrderDetail;
	
	private List<OrderItemVO> orderItemList;//商品列表
	
	private OrderDiscount discount;//优惠信息
	
	private TransactionList transactionList;//支付方式

	public OfflineOrderDetailVO getOfflineOrderDetail() {
		return offlineOrderDetail;
	}

	public void setOfflineOrderDetail(OfflineOrderDetailVO offlineOrderDetail) {
		this.offlineOrderDetail = offlineOrderDetail;
	}

	public List<OrderItemVO> getOrderItemList() {
		return orderItemList;
	}

	public void setOrderItemList(List<OrderItemVO> orderItemList) {
		this.orderItemList = orderItemList;
	}

	public OrderDiscount getDiscount() {
		return discount;
	}

	public void setDiscount(OrderDiscount discount) {
		this.discount = discount;
	}

	public TransactionList getTransactionList() {
		return transactionList;
	}

	public void setTransactionList(TransactionList transactionList) {
		this.transactionList = transactionList;
	}
	
	
	
}
