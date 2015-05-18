package com.xpos.common.service;

import com.xkeshi.pojo.vo.Result;
import com.xkeshi.pojo.vo.SystemParam;
import com.xkeshi.pojo.vo.transaction.BaseTransactionVO;
import com.xpos.common.entity.Item;
import com.xpos.common.entity.Order;
import com.xpos.common.entity.Order.Status;
import com.xpos.common.entity.OrderItem;
import com.xpos.common.entity.face.Business;
import com.xpos.common.searcher.OrderSearcher;
import com.xpos.common.utils.Pager;

import java.math.BigDecimal;
import java.util.List;

public interface OrderService {
	
	public boolean saveOrder(Order order);
	
	public Pager<Order> findOrders(Business business, Pager<Order> pager, OrderSearcher searcher);
	
	/** 点单统计(orderItem总数 + 总金额 + order总数) 
	 * @return String[0]:销售商品总计，String[1]:销售金额总计，String[2]：订单总计
	 */
	public String[] getOrderStatistics(Business business, OrderSearcher searcher);
	
	/** 总金额统计 */
	public String getTotalAmount(Business business, OrderSearcher searcher);
	/**订单列表页面的交易金额合计*/
	public String getCaculateAmount(Business business, OrderSearcher searcher);
	
	/**根据订单号查出订单实际付款金额*/
	public BigDecimal getActuallyPaid(String orderNum);
	
	/**
	 * 商品统计
	 */
	public Pager<OrderItem> findOrderItems(OrderSearcher searcher, Pager<OrderItem> pager);
	
	public Order findOrderByPOSTransactionId(Long posTransactionId);
	
	public boolean updateOrder(Order order);

	public Order findByOrderNumber(String orderNumber);

	/* =========================================库存相关操作========================================= */
	/** 创建订单并扣除库存
	 * @param order 订单信息
	 * @param failList 返回库存扣除失败的商品列表
	 */
	public String createOrderAndDeductItemInventory(Order order, List<Item> failList);

	/** 撤销指定的未付款订单，并退还已扣除的库存 */
	public boolean discardOrderAndReturnItemInventory(String orderNumber, Status targetStatus);

	/**
	 *  追加订单商品
	 */
	public boolean addOrderItem(List<OrderItem> addList, List<Item> failList,Order order);

	/**
	 *	取消已点商品
	 */
	public boolean decreaseOrderItem(List<OrderItem> decreaseList,List<Item> failList, Order order);

	/**
	 *	追加已点商品
	 */
	public boolean increaseOrderItem(List<OrderItem> increaseList,List<Item> failList, Order order);

	/**获取订单的多种支付*/
	public List<BaseTransactionVO> getOrderTransactionList(String orderNum);
	
	/** 生成打印客户小票的订单信息 */
	public Result generateXPOSOrderPrintSummary(SystemParam systemParam, String orderNumber);
	
	/** 生成订单详细信息 */
	public Result generateXPOSOrderDetail(SystemParam systemParam, String orderNumber);

	/** 根据订单查询流水 */
	public Result generateXPOSOrderTransaction(SystemParam systemParam,String orderNumber);
	
	
}
