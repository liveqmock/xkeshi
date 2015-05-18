package com.xkeshi.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xkeshi.common.em.OrderPaymentStatus;
import com.xkeshi.dao.OrderDAO;
import com.xkeshi.dao.OrderItemDAO;
import com.xkeshi.pojo.po.Order;
import com.xkeshi.pojo.po.OrderItem;
import com.xkeshi.pojo.po.UnpaidOrderList;
import com.xkeshi.pojo.vo.UnpaidItemListVO;
import com.xkeshi.pojo.vo.UnpaidOrderListVO;

/**
 * Created by snoopy on 2015/2/10.
 */
@Service
public class OrdersService {
	

	@Autowired(required = false)
	private Mapper dozerMapper;
	
    @Autowired(required=false)
    private OrderDAO orderDAO;
   
    @Autowired(required=false)
    private OrderItemDAO orderItemDAO;

    
    public int getUnpaidOrderCountByOperatorSessionCode(String operatorSessionCode) {
    	if(StringUtils.isNotBlank(operatorSessionCode)) {
    		return orderDAO.getUnpaidOrderCountByOperatorSessionCode(operatorSessionCode);
    	}else   {
    		return 0;
    	}
    }
    
    public int getUnpaidOrderCountByOperatorId(Long operatorId) {
    	return orderDAO.getUnpaidOrderCountByOperatorId(operatorId);
	}


    public List<UnpaidOrderListVO> getUnpaidOrderListByOperatorSessionCode(String operatorSessionCode) {
    	if(StringUtils.isNotBlank(operatorSessionCode)) {
    		List<UnpaidOrderList> unpaidOrderList = orderDAO.getUnpaidOrderListByOperatorSessionCode(operatorSessionCode);
    		return returnUnpaidOrderVOList(unpaidOrderList);
    	}else {
    		return null;
    	}
    }
    
    public List<UnpaidOrderListVO> getUnpaidOrderListByOperatorId(Long operatorId) {
    	List<UnpaidOrderList> unpaidOrderList = orderDAO.getUnpaidOrderListByOperatorId(operatorId);
    	return returnUnpaidOrderVOList(unpaidOrderList);
    }
    
    private List<UnpaidOrderListVO> returnUnpaidOrderVOList (List<UnpaidOrderList> unpaidOrderList) {
    	if(unpaidOrderList == null || unpaidOrderList.size()==0) {
    		return null;
    	}
    	List<UnpaidOrderListVO> unpaidOrderVOList = new ArrayList<>();
    	for (UnpaidOrderList upaidOrder : unpaidOrderList) {
    		unpaidOrderVOList.add(dozerMapper.map(upaidOrder, UnpaidOrderListVO.class));
		}
    	return unpaidOrderVOList;
    }
    
    public List<UnpaidItemListVO> getUnpaidOrderItemListByOrderNumber(String orderNumber) {
    	return orderDAO.getUnpaidOrderItemListByOrderNumber(orderNumber);
    }
    
	/**
	 * 后台定时器用于订单支付超时
	 */
	public void updateTimeOutOrder() {
		List<Order> orderList = orderDAO.getTimeOutOrderList();
		if (CollectionUtils.isNotEmpty(orderList)) {
			for(Order order:orderList) {//遍历满足条件的订单
				orderDAO.updateOrderStatus(order.getOrderNumber(), OrderPaymentStatus.TIMEOUT.toString());//更新订单状态
				List<OrderItem> orderItemList = orderItemDAO.getOrderItemByOrderId(order.getId());
				if(CollectionUtils.isNotEmpty(orderItemList)) {
					for(OrderItem orderItem : orderItemList) {//返回库存
						orderItemDAO.returnItemInventory(orderItem.getItemId(),orderItem.getQuantity());
					}
				}
			
			}
		}
	}

	


	public void resetOrderShopCounter() {
		orderDAO.resetOrderShopCounter();
	}


	


}
