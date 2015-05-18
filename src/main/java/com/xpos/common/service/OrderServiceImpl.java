package com.xpos.common.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
import org.dozer.Mapper;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.xkeshi.dao.CashTransactionDAO;
import com.xkeshi.dao.PrepaidCardTransactionDAO;
import com.xkeshi.dao.WXPayTransactionDAO;
import com.xkeshi.pojo.po.OrderTransaction;
import com.xkeshi.pojo.vo.OrderDetailVO;
import com.xkeshi.pojo.vo.OrderItemDetailVO;
import com.xkeshi.pojo.vo.OrderTransactionListVO;
import com.xkeshi.pojo.vo.OrderTransactionVO;
import com.xkeshi.pojo.vo.PrintOrderItemSummaryVO;
import com.xkeshi.pojo.vo.PrintOrderSummaryVO;
import com.xkeshi.pojo.vo.Result;
import com.xkeshi.pojo.vo.SystemParam;
import com.xkeshi.pojo.vo.transaction.BaseTransactionVO;
import com.xkeshi.pojo.vo.transaction.CashTransactionVO;
import com.xkeshi.utils.CodeUtil;
import com.xpos.common.entity.Item;
import com.xpos.common.entity.Operator;
import com.xpos.common.entity.Order;
import com.xpos.common.entity.Order.Status;
import com.xpos.common.entity.Order.Type;
import com.xpos.common.entity.OrderItem;
import com.xpos.common.entity.Shop;
import com.xpos.common.entity.example.ItemInventoryExample;
import com.xpos.common.entity.example.OrderExample;
import com.xpos.common.entity.face.Business;
import com.xpos.common.entity.itemInventory.ItemInventory;
import com.xpos.common.entity.itemInventory.ItemInventoryChangeDetail;
import com.xpos.common.exception.GenericException;
import com.xpos.common.persistence.mybatis.AlipayTransactionMapper;
import com.xpos.common.persistence.mybatis.BankNFCTransactionMapper;
import com.xpos.common.persistence.mybatis.ItemInventoryMapper;
import com.xpos.common.persistence.mybatis.ItemMapper;
import com.xpos.common.persistence.mybatis.OperatorMapper;
import com.xpos.common.persistence.mybatis.OrderItemMapper;
import com.xpos.common.persistence.mybatis.OrderMapper;
import com.xpos.common.persistence.mybatis.POSTransactionMapper;
import com.xpos.common.persistence.mybatis.physicalCoupon.PhysicalCouponOrderMapper;
import com.xpos.common.searcher.OrderSearcher;
import com.xpos.common.utils.Pager;

@Service
public class OrderServiceImpl implements OrderService{
	
	@Autowired(required = false)
    private Mapper dozerMapper;

	@Autowired
	private OrderMapper orderMapper;
	
	@Autowired
	private PhysicalCouponOrderMapper physicalCouponOrderMapper;
	@Autowired
	private OrderItemMapper orderItemMapper;
	
	@Autowired
	private ItemMapper itemMapper;
	
	@Autowired
	private ItemInventoryMapper itemInventoryMapper;
	
	@Autowired
	private BankNFCTransactionMapper  bankNFCTransactionMapper   ;

	@Autowired
	private POSTransactionMapper    posTransactionMapper  ;
	
	@Autowired
	private WXPayTransactionDAO  wxPayTransactionDAO   ;
	
	@Autowired
	private AlipayTransactionMapper  alipayTransactionMapper   ;

	@Autowired
	private CashTransactionDAO   cashTransactionDAO   ;
	
	@Autowired
	private PrepaidCardTransactionDAO  prepaidCardTransactionDAO   ;
	@Autowired
	private ShopService shopService;
	@Autowired
	private OperatorMapper operatorMapper;
	
	@Override
	@Transactional
	public boolean saveOrder(Order order) {
		boolean result = true;
		order.setOrderNumber(CodeUtil.getNewCode());
		result = orderMapper.insert(order) > 0;
		if(order.getItems() != null){
			for(OrderItem orderItem:order.getItems()){
				Item persistence = itemMapper.selectByPrimaryKey(orderItem.getItem().getId());
				if(persistence == null)
					throw new GenericException("商品不存在");
				orderItem.setOrder(order);
				orderItem.setItemName(persistence.getName());
				orderItem.setPrice(persistence.getPrice());
				if(orderItem.getQuantity() == null)
					orderItem.setQuantity(1);
				result = result && orderItemMapper.insert(orderItem) > 0;
			}
		}
		return result;
	}

	@Override
	public Pager<Order> findOrders(Business business, Pager<Order> pager, OrderSearcher searcher) {
		List<Order> orders = orderMapper.queryOrderList(business, pager, searcher);
		pager.setList(orders);
	
		int totalCount = orderMapper.countOrders(business, pager, searcher);
		pager.setTotalCount(totalCount);
		
		return pager;
	}

	@Override
	public String[] getOrderStatistics(Business business, OrderSearcher searcher) {
		String[] statistic = new String[3];
		
		Pager<Order> pager = new Pager<>();
		pager.setPageSize(Integer.MAX_VALUE);
		pager = findOrders(business, pager, searcher);
		if(pager.getTotalCount() == null || pager.getTotalCount() == 0){
			statistic[0] = "0";
			statistic[2] = "0";
		}else{
			//遍历order list统计总数
			Integer orderItemCount = 0;
			for(Order order : pager.getList()){
				for(OrderItem orderItem : order.getItems()){
					orderItemCount += orderItem.getQuantity();
				}
			}
			statistic[0] = orderItemCount.toString();
			statistic[2] = pager.getTotalCount().toString();
		}
		
		statistic[1] = getTotalAmount(business, searcher);
		return statistic;
	}
	
	/** 总金额统计 */
	@Override
	public String getTotalAmount(Business business, OrderSearcher searcher) {
		BigDecimal sum = orderMapper.getOrderAmount(business, searcher);
		String totalAmount = sum !=null ? sum.toString() : "0";
		return totalAmount;
	}
	
	/**订单列表页面的交易金额合计*/
	@Override
	public String getCaculateAmount(Business business, OrderSearcher searcher) {
		BigDecimal physicalSum = physicalCouponOrderMapper.getPhysicalAmount(business, searcher);
		BigDecimal Ordersum = orderMapper.getOrderAmount(business, searcher);
		BigDecimal sum = Ordersum.add(physicalSum);
		String totalSum =  sum !=null ? sum.toString() : "0";
		return totalSum;
	}
	@Override
	public Pager<OrderItem> findOrderItems(OrderSearcher searcher, Pager<OrderItem> pager) {
		List<OrderItem> orders = orderItemMapper.selectOrderItemStatisticList(searcher, pager);
		pager.setList(orders);
		
		int totalCount = orderItemMapper.countOrderItemStatisticList(searcher);
		pager.setTotalCount(totalCount);
		
		return pager;
	}

	@Override
	public Order findOrderByPOSTransactionId(Long posTransactionId) {
		OrderExample example = new OrderExample();
		example.createCriteria()
			.addCriterion("status = ", Status.SUCCESS.name())
//			.addCriterion("type = ", Type.BANKCARD.name())
			.addCriterion("posTransaction_id = ", posTransactionId);
		return orderMapper.selectOneByExample(example);
	}

	@Override
	public boolean updateOrder(Order order) {
		return orderMapper.updateByPrimaryKey(order) == 1;
	}

	@Override
	public Order findByOrderNumber(String orderNumber) {
//		OrderExample example = new OrderExample();
//		example.createCriteria()
//			.addCriterion("orderNumber = ", orderNumber);
//		return orderMapper.selectOneByExample(example);
		/**根据ordernumber来查询点单详情页面里面的数据*/
		return orderMapper.findByOrderNumber(orderNumber);
	}

	@Override
	@Transactional
	public String createOrderAndDeductItemInventory(Order order, List<Item> failList) {
		//order.setOrderNumber(CodeUtil.getNewCode());
		order.setStatus(Status.UNPAID);
        try{
            orderMapper.insertOrder(order);
        }catch (RuntimeException e) {
            throw new RuntimeException("订单号重复，请重新下单");
        }
		boolean result = true;
		for(OrderItem orderItem : order.getItems()){
			Item item = new Item();
			item.setId(orderItem.getId());
			orderItem.setItem(item);
			//校验商品合法性、对比库存
			ItemInventoryExample example = new ItemInventoryExample();
			example.createCriteria().addCriterion("item_id=", orderItem.getItem().getId())
									.addCriterion("deleted=", false);
			ItemInventory itemInventory = itemInventoryMapper.selectOneByExample(example);
			if(itemInventory == null //库存对象不存在
					|| itemInventory.getItem() == null //库存对应的商品不存在
					|| !order.getBusinessId().equals(itemInventory.getItem().getBusinessId()) //非当前商户商品
					|| !order.getBusinessType().equals(itemInventory.getItem().getBusinessType())){ //非当前商户商品
				throw new RuntimeException("无法找到库存信息");
			}else if(orderItem.getQuantity() <= 0 //参数异常
					|| itemInventory.getInventory().compareTo(orderItem.getQuantity()) < 0 //库存不足
					|| !itemInventory.getItem().isMarketable() //商品已下架
					|| itemInventory.getItem().getDeleted()){ //商品已删除
				itemInventory.getItem().setItemInventory(itemInventory);
				failList.add(itemInventory.getItem());
                throw new RuntimeException(itemInventory.getItem().getName()+" 库存不足或已下架，下单失败");
			}
			
			try {
				Date createDate  = new Date();
				//扣除库存
				itemInventoryMapper.update(itemInventory.getId(), ItemInventoryChangeDetail.INVENTORY_TYPE_EXPORT, orderItem.getQuantity());
				orderItem.setOrder(order);
				orderItem.setItemName(itemInventory.getItem().getName());
				orderItem.setPrice(itemInventory.getItem().getPrice()); //商品快照存原价
				orderItem.setCreateDate(createDate);
				orderItem.setId(null);
				result = result && orderItemMapper.insert(orderItem) > 0; //插入orderItem记录
			} catch (Exception e) {
				itemInventory = itemInventoryMapper.selectOneByExample(example); //重新获取最新inventory数据 FIXME
				itemInventory.getItem().setItemInventory(itemInventory);
				failList.add(itemInventory.getItem());
				result = false;
			}
		}
		
		int counter = orderMapper.requireOrderCounter(order.getBusinessId());
		Order updateOrder = new Order(); //FIXME updateByPrimaryKeys时报字段不匹配（operatorSessionCode <--> operator_session_code）。建临时对象，重构时改用新方法
		updateOrder.setId(order.getId());
		updateOrder.setCounter(counter);
		result = result && orderMapper.updateByPrimaryKey(updateOrder) > 0;
		result = result && orderMapper.increaseOrderCounter(order.getBusinessId(), counter) > 0;
		
		if(!result){
			throw new RuntimeException("创建订单插入数据库异常、回滚");
		}else {
            return null;
        }
	}


	@Override
	@Transactional
	public boolean discardOrderAndReturnItemInventory(String orderNumber, Status targetStatus) {
		Order order = findByOrderNumber(orderNumber);
		if(order == null //无此订单
			|| !(Status.UNPAID.equals(order.getStatus()) || Status.SUCCESS.equals(order.getStatus())) //当前状态非待付款、已成功
			|| !(Status.CANCEL.equals(targetStatus) || Status.REFUND.equals(targetStatus) || Status.TIMEOUT.equals(targetStatus))){ //目标状态非撤销、退款、超时
			return false;
		}
		
		order.setStatus(targetStatus);
		order.setOperatorSessionCode(null);
		boolean result = orderMapper.updateByPrimaryKey(order) == 1;
		
		for(OrderItem orderItem : order.getItems()){
			//校验商品合法性、对比库存
			ItemInventoryExample example = new ItemInventoryExample();
			example.createCriteria().addCriterion("item_id=", orderItem.getItem().getId()); //ignore deleted
			ItemInventory itemInventory = itemInventoryMapper.selectOneByExample(example);
			if(itemInventory == null //库存对象不存在
					|| itemInventory.getItem() == null){ //库存对应的商品不存在
				continue; //不需要退还库存，直接跳过
			}
			
			try {
				//退还库存(商品已下架、已删除也退还)
				itemInventoryMapper.update(itemInventory.getId(),  ItemInventoryChangeDetail.INVENTORY_TYPE_IMPORT, orderItem.getQuantity());
			} catch (Exception e) {
				result = false;
			}
		}
		
		if(!result){
			throw new RuntimeException("撤销订单、退还库存异常、回滚");
		}
		return result;
	}

	@Override
	@Transactional
	public boolean addOrderItem(List<OrderItem> addList, List<Item> failList, Order order) {
		boolean result = true;
		Date createDate  = new Date();
		BigDecimal totalAmount = order.getTotalAmount();
		for(OrderItem orderItem : addList){
			//校验商品合法性、对比库存
			ItemInventoryExample example = new ItemInventoryExample();
			example.createCriteria().addCriterion("item_id=", orderItem.getItem().getId())
									.addCriterion("deleted=", false);
			ItemInventory itemInventory = itemInventoryMapper.selectOneByExample(example);
			if(itemInventory == null //库存对象不存在
					|| itemInventory.getItem() == null //库存对应的商品不存在
					|| !order.getBusinessId().equals(itemInventory.getItem().getBusinessId()) //非当前商户商品
					|| !order.getBusinessType().equals(itemInventory.getItem().getBusinessType())){ //非当前商户商品
				throw new RuntimeException("无法找到库存信息");
			}else if(orderItem.getQuantity() <= 0 //参数异常
					|| itemInventory.getItem().getDeleted()){ //商品已删除
				itemInventory.getItem().setItemInventory(itemInventory);
				failList.add(itemInventory.getItem());
				result = false;
				continue;
			}
			try {
				//减少库存
				itemInventoryMapper.update(itemInventory.getId(), ItemInventoryChangeDetail.INVENTORY_TYPE_EXPORT, orderItem.getQuantity());
				orderItem.setOrder(order);
				orderItem.setItemName(itemInventory.getItem().getName());
				orderItem.setPrice(itemInventory.getItem().getPrice()); //商品快照存原价
				orderItem.setCreateDate(createDate);
				totalAmount = totalAmount.add(itemInventory.getItem().getPrice().multiply(new BigDecimal(orderItem.getQuantity())));//订单原总价更新
				result = result && orderItemMapper.insert(orderItem) > 0; //插入orderItem记录
			} catch (Exception e) {
				itemInventory = itemInventoryMapper.selectOneByExample(example); //重新获取最新inventory数据 FIXME
				itemInventory.getItem().setItemInventory(itemInventory);
				failList.add(itemInventory.getItem());
				result = false;
			}
			order.setTotalAmount(totalAmount);
			order.setActuallyPaid(totalAmount);
			order.setOperatorSessionCode(null);
			result = result && orderMapper.updateByPrimaryKey(order)>0;
		}
		return result;
	}

	@Override
	@Transactional
	public boolean decreaseOrderItem(List<OrderItem> decreaseList,List<Item> failList, Order order) {
		boolean result = true;
		int itemsSize = 0;
		BigDecimal totalAmount = order.getTotalAmount();
		for(OrderItem orderItem : decreaseList){
			//校验商品合法性、对比库存
			int quantity = orderItem.getQuantity();
			orderItem = orderItemMapper.selectByPrimaryKey(orderItem.getId());
			ItemInventoryExample example = new ItemInventoryExample();
			example.createCriteria().addCriterion("item_id=", orderItem.getItem().getId())
									.addCriterion("deleted=", false);
			ItemInventory itemInventory = itemInventoryMapper.selectOneByExample(example);
			if(itemInventory == null //库存对象不存在
					|| itemInventory.getItem() == null //库存对应的商品不存在
					|| !order.getBusinessId().equals(itemInventory.getItem().getBusinessId()) //非当前商户商品
					|| !order.getBusinessType().equals(itemInventory.getItem().getBusinessType())){ //非当前商户商品
				throw new RuntimeException("无法找到库存信息");
			}else if(quantity <= 0 //参数异常
					|| itemInventory.getItem().getDeleted()){ //商品已删除
				itemInventory.getItem().setItemInventory(itemInventory);
				failList.add(itemInventory.getItem());
				result = false;
				continue;
			}
			
			try {
				List<OrderItem> orderItemList = order.getItems();
				itemsSize = orderItemList.size();
				for (OrderItem oi : orderItemList) {
					if(oi.getId().equals(orderItem.getId())) {
						int orquantity = oi.getQuantity();//查询已点商品数量
						itemInventoryMapper.update(itemInventory.getId(), ItemInventoryChangeDetail.INVENTORY_TYPE_IMPORT, quantity);
						totalAmount = totalAmount.subtract(itemInventory.getItem().getPrice().multiply(new BigDecimal(quantity)));//订单原总价更新
						if(orquantity == quantity) {//商品全部撤销,删除该记录
							result = result && orderItemMapper.deleteById(orderItem.getId()) > 0;
							itemsSize-- ;
						}else {
							orderItem.setQuantity(orquantity - quantity);
							result = result && orderItemMapper.updateByPrimaryKey(orderItem) > 0; //插入orderItem记录
						}
					}
				}
			} catch (Exception e) {
				itemInventory = itemInventoryMapper.selectOneByExample(example); //重新获取最新inventory数据 FIXME
				itemInventory.getItem().setItemInventory(itemInventory);
				failList.add(itemInventory.getItem());
				result = false;
			}
		}
		if(itemsSize == 0) {//订单内商品已全部取消
			return orderMapper.deleteById(order.getId()) > 0;
		}else {
			order.setTotalAmount(totalAmount);
			order.setActuallyPaid(totalAmount);
		}
		order.setOperatorSessionCode(null);
		result = result && orderMapper.updateByPrimaryKey(order)>0;
		return result;
	}

	@Override
	@Transactional
	public boolean increaseOrderItem(List<OrderItem> increaseList,List<Item> failList, Order order) {
		boolean result = true;
		BigDecimal totalAmount = order.getTotalAmount();
		for(OrderItem orderItem : increaseList){
			//校验商品合法性、对比库存
			int quantity = orderItem.getQuantity();
			orderItem = orderItemMapper.selectByPrimaryKey(orderItem.getId());
			ItemInventoryExample example = new ItemInventoryExample();
			example.createCriteria().addCriterion("item_id=", orderItem.getItem().getId())
									.addCriterion("deleted=", false);
			ItemInventory itemInventory = itemInventoryMapper.selectOneByExample(example);
			if(itemInventory == null //库存对象不存在
					|| itemInventory.getItem() == null //库存对应的商品不存在
					|| !order.getBusinessId().equals(itemInventory.getItem().getBusinessId()) //非当前商户商品
					|| !order.getBusinessType().equals(itemInventory.getItem().getBusinessType())){ //非当前商户商品
				throw new RuntimeException("无法找到库存信息");
			}else if(quantity <= 0 //参数异常
					|| itemInventory.getItem().getDeleted()){ //商品已删除
				itemInventory.getItem().setItemInventory(itemInventory);
				failList.add(itemInventory.getItem());
				result = false;
				continue;
			}
			
			try {
				List<OrderItem> orderItemList = order.getItems();
				for (OrderItem oi : orderItemList) {
					if(oi.getId().equals(orderItem.getId())) {
						int orquantity = oi.getQuantity();//查询已点商品数量
						itemInventoryMapper.update(itemInventory.getId(), ItemInventoryChangeDetail.INVENTORY_TYPE_EXPORT, quantity);
						totalAmount = totalAmount.add(itemInventory.getItem().getPrice().multiply(new BigDecimal(quantity)));//订单原总价更新
						orderItem.setQuantity(orquantity + quantity);
						result = result && orderItemMapper.updateByPrimaryKey(orderItem) > 0; //插入orderItem记录
					}
				}
			} catch (Exception e) {
				itemInventory = itemInventoryMapper.selectOneByExample(example); //重新获取最新inventory数据 FIXME
				itemInventory.getItem().setItemInventory(itemInventory);
				failList.add(itemInventory.getItem());
				result = false;
			}
		}
		order.setTotalAmount(totalAmount);
		order.setActuallyPaid(totalAmount);
		order.setOperatorSessionCode(null);
		result = result && orderMapper.updateByPrimaryKey(order)>0;
		return result;
	}

	@Override
	public List<BaseTransactionVO> getOrderTransactionList(String orderNum) {
		 List<BaseTransactionVO> transactionVOs = new ArrayList<BaseTransactionVO>();
		 //支付宝扫码支付总金额
		 BaseTransactionVO baseTransactionVO = alipayTransactionMapper.getPayTransactionGroupByOrderNumber(orderNum);
		 if (baseTransactionVO != null) 
			 transactionVOs.add(baseTransactionVO);
		 //电子钱包(NFC)支付总金额
		 baseTransactionVO = bankNFCTransactionMapper.getPayTransactionGroupByOrderNumber(orderNum);
		 if (baseTransactionVO != null) 
			 transactionVOs.add(baseTransactionVO);
		 //现金支付总金额
		 baseTransactionVO = cashTransactionDAO.getPayTransactionGroupByOrderNumber(orderNum);
		 if (baseTransactionVO != null) 
			 transactionVOs.add(baseTransactionVO);
		 //pos刷卡支付总金额
		 baseTransactionVO = posTransactionMapper.getPayTransactionGroupByOrderNumber(orderNum);
		 if (baseTransactionVO != null) 
			 transactionVOs.add(baseTransactionVO);
		 //微信扫码支付总金额
		 baseTransactionVO = wxPayTransactionDAO.getPayTransactionGroupByOrderNumber(orderNum);
		 if (baseTransactionVO != null) 
			 transactionVOs.add(baseTransactionVO);
		 //预付卡支付总金额
		 baseTransactionVO = prepaidCardTransactionDAO.getPayTransactionGroupByOrderNumber(orderNum);
		 if (baseTransactionVO != null) 
			 transactionVOs.add(baseTransactionVO);
		return transactionVOs;
	}
	
	/** 生成打印客户小票的订单信息 */
	public Result generateXPOSOrderPrintSummary(SystemParam systemParam, String orderNumber) {
		Order order = findByOrderNumber(orderNumber);
		if(order == null 
				|| !StringUtils.equals(order.getStatus().name(), "SUCCESS")
				|| !order.getBusinessId().equals(systemParam.getMid())){
			return new Result("未查到订单或订单状态异常", "1001");
		}
		Result result = new Result("订单查询成功", "0");
		PrintOrderSummaryVO printVO = new PrintOrderSummaryVO();
		Shop shop = shopService.findShopByIdIgnoreVisible(systemParam.getMid());
		Operator operator = operatorMapper.selectByPrimaryKey(systemParam.getOperatorId());
		List<OrderItem> orderItemList = order.getItems(); 
		
		printVO.setOrderNumber(orderNumber);
		printVO.setOperatorName(order.getOperator().getRealName());
		printVO.setShopName(shop.getName());
		printVO.setCounter(order.getCounter());
		printVO.setTradeTime(new DateTime(order.getModifyDate()).toString("yyyy/MM/dd HH:mm"));
		printVO.setOperatorName(operator.getRealName());
		printVO.setAddress(shop.getAddress());
		printVO.setContact(shop.getContact());
		printVO.setItemList(convertOrderItemSummaryVO(orderItemList));
		printVO.setTotalAmount(order.getTotalAmount()); //总计
		printVO.setActuallyAmount(order.getActuallyPaid());//应收
		printVO.setDiscountAmount(order.getTotalAmount().subtract(order.getActuallyPaid())); //折扣
		List<BaseTransactionVO> transactionList = getOrderTransactionList(orderNumber);
		for(BaseTransactionVO transaction : transactionList){
			if(StringUtils.equals(transaction.getTransactionType(), Type.CASH.toString())){
				CashTransactionVO cashTransaction = (CashTransactionVO)transaction;
				printVO.setCashReceived(cashTransaction.getReceived());
				printVO.setCashReturned(cashTransaction.getReturned());
			}else if(StringUtils.equals(transaction.getTransactionType(), Type.BANKCARD.toString())){
				printVO.setPosReceived(transaction.getAmount());
			}else if(StringUtils.equals(transaction.getTransactionType(), Type.BANK_NFC_CARD.toString())){
				printVO.setNfcReceived(transaction.getAmount());
			}else if(StringUtils.equals(transaction.getTransactionType(), Type.ALIPAY_QRCODE.toString())){
				printVO.setAlipayReceived(transaction.getAmount());
			}else if(StringUtils.equals(transaction.getTransactionType(), Type.WXPAY_QRCODE.toString())){
				printVO.setWxpayReceived(transaction.getAmount());
			}else if(StringUtils.equals(transaction.getTransactionType(), Type.PREPAID.toString())){
				printVO.setPrepaidReceived(transaction.getAmount());
			}
		}
		
		result.setResult(printVO);
		return result;
	}


	private List<PrintOrderItemSummaryVO> convertOrderItemSummaryVO(List<OrderItem> orderItemList) {
		List<PrintOrderItemSummaryVO> orderItemSummaryList = new ArrayList<>();
		Map<Long, OrderItem> map = new TreeMap<>();
		
		for(OrderItem orderItem : orderItemList){ //先消费后付款类型会分多笔，按商品先归类
			OrderItem _orderItem = map.get(orderItem.getId());
			if(_orderItem == null){
				map.put(orderItem.getId(), orderItem);
				continue;
			}
			_orderItem.setQuantity(orderItem.getQuantity() + _orderItem.getQuantity());
			_orderItem.setAmount(orderItem.getAmount().add(_orderItem.getAmount()));
			map.put(orderItem.getId(), _orderItem);
		}
		for(OrderItem orderItem : map.values()){
			PrintOrderItemSummaryVO printOrderItemVO = new PrintOrderItemSummaryVO();
			printOrderItemVO.setPrinterId(orderItem.getItem().getPrinterId());
			printOrderItemVO.setName(orderItem.getItemName());
			printOrderItemVO.setQuantity(orderItem.getQuantity());
			printOrderItemVO.setAmount(orderItem.getAmount());
			orderItemSummaryList.add(printOrderItemVO);
		}
		return orderItemSummaryList;
	}
	
	/** 返回订单详细信息 */
	public Result generateXPOSOrderDetail(SystemParam systemParam, String orderNumber) {
		Order order = findByOrderNumber(orderNumber);
		if(order == null || !order.getBusinessId().equals(systemParam.getMid())){
			return new Result("未查到订单", "1001");
		}
		Result result = new Result("订单查询成功", "0");
		OrderDetailVO detailVO = new OrderDetailVO();
		Operator operator = operatorMapper.selectByPrimaryKey(systemParam.getOperatorId());
		List<OrderItem> orderItemList = order.getItems(); 
		detailVO.setOrderNumber(orderNumber);
		detailVO.setOperatorName(operator.getRealName());
		detailVO.setStatus(order.getStatus().toString());
		detailVO.setTradeTime(new DateTime(order.getTradeDate()==null?order.getModifyDate():order.getTradeDate()).toString("yyyy/MM/dd HH:mm"));
		detailVO.setTotalItemCount(getItemCountByOrderItemList(orderItemList));
		detailVO.setTotalAmount(order.getTotalAmount());
		detailVO.setActuallyAmount(order.getActuallyPaid());
		detailVO.setDiscountAmount(order.getTotalAmount().subtract(order.getActuallyPaid()));
		detailVO.setItemList(convertOrderItemDetailVO(orderItemList));
		detailVO.setRefundTime(new DateTime(order.getModifyDate()).toString("yyyy/MM/dd HH:mm"));
		List<BaseTransactionVO> transactionList = getOrderTransactionList(orderNumber);
		BigDecimal refundAmount = new BigDecimal(0);
		List<String> payments = new ArrayList<>();
		//退款金额合计
		for(BaseTransactionVO transaction : transactionList){
			if(StringUtils.equals(transaction.getTransactionType(), Type.CASH.toString())){
				refundAmount = refundAmount.add(transaction.getAmount());
				payments.add("现金");
			}else if(StringUtils.equals(transaction.getTransactionType(), Type.BANKCARD.toString())){
				refundAmount = refundAmount.add(transaction.getAmount());
				payments.add("POS刷卡");
			}else if(StringUtils.equals(transaction.getTransactionType(), Type.BANK_NFC_CARD.toString())){
				//refundAmount.add(transaction.getAmount());
				payments.add("电子钱包");
			}else if(StringUtils.equals(transaction.getTransactionType(), Type.ALIPAY_QRCODE.toString())){
				refundAmount = refundAmount.add(transaction.getAmount());
				payments.add("支付宝扫码");
			}else if(StringUtils.equals(transaction.getTransactionType(), Type.WXPAY_QRCODE.toString())){
				refundAmount = refundAmount.add(transaction.getAmount());
				payments.add("微信扫码");
			}else if(StringUtils.equals(transaction.getTransactionType(), Type.PREPAID.toString())){
				refundAmount = refundAmount.add(transaction.getAmount());
				payments.add("预付卡");
			}
		}
		detailVO.setPaymentType(StringUtils.join(payments, ","));
		detailVO.setRefundAmount(refundAmount);
		result.setResult(detailVO);
		return result;
	}
	
	private int getItemCountByOrderItemList(List<OrderItem> orderItemList) {
		int count = 0;
		for (OrderItem orderItem : orderItemList) {
			if(orderItem.getQuantity()>=0) {
				count += orderItem.getQuantity();
			}
		}
		return count;
	}

	private List<OrderItemDetailVO> convertOrderItemDetailVO(List<OrderItem> orderItemList) {
		List<OrderItemDetailVO> orderItemDetailList = new ArrayList<>();
		Map<Long, OrderItem> map = new TreeMap<>();
		
		for(OrderItem orderItem : orderItemList){ //先消费后付款类型会分多笔，按商品先归类
			OrderItem _orderItem = map.get(orderItem.getId());
			if(_orderItem == null){
				map.put(orderItem.getId(), orderItem);
				continue;
			}
			_orderItem.setQuantity(orderItem.getQuantity() + _orderItem.getQuantity());
			_orderItem.setAmount(orderItem.getAmount().add(_orderItem.getAmount()));
			map.put(orderItem.getId(), _orderItem);
		}
		for(OrderItem orderItem : map.values()){
			OrderItemDetailVO orderItemVO = new OrderItemDetailVO();
			orderItemVO.setName(orderItem.getItemName());
			orderItemVO.setPrice(orderItem.getPrice());
			orderItemVO.setQuantity(orderItem.getQuantity());
			orderItemVO.setAmount(orderItem.getAmount());
			orderItemDetailList.add(orderItemVO);
		}
		return orderItemDetailList;
	}
	
	/** 返回订单支付流水的列表 */
	public Result generateXPOSOrderTransaction(SystemParam systemParam, String orderNumber) {
		Order order = findByOrderNumber(orderNumber);
		if(order == null || !order.getBusinessId().equals(systemParam.getMid()) || !Status.SUCCESS.equals(order.getStatus())){
			return new Result("未找到订单或订单状态有误", "1001");
		}
		Result result = new Result("流水状态查询成功", "0");
		OrderTransactionListVO vo = new OrderTransactionListVO();
		List<OrderTransactionVO> voList = new ArrayList<>();
		List<OrderTransaction> list = orderMapper.findTransactionListByOrderNumber(orderNumber);
		if(!CollectionUtils.isEmpty(list)) {
			for(OrderTransaction orderTransaction : list) {
				OrderTransactionVO orderTransactionVO = new OrderTransactionVO();
				dozerMapper.map(orderTransaction, orderTransactionVO);
				voList.add(orderTransactionVO);
			}
			vo.setTransactionList(voList);
			result.setResult(vo);
		}
		return result;
	}
	/**根据订单号查出订单实际付款金额*/
	public BigDecimal getActuallyPaid(String orderNum) {
		return orderMapper.getActuallyPaid(orderNum);
	}


}
