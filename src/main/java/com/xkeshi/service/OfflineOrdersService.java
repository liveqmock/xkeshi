package com.xkeshi.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.dozer.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xkeshi.common.em.OfflineOrderValidationResult;
import com.xkeshi.common.em.OrderPaymentStatus;
import com.xkeshi.dao.CashTransactionDAO;
import com.xkeshi.dao.OperatorDAO;
import com.xkeshi.dao.OrderDAO;
import com.xkeshi.dao.OrderItemDAO;
import com.xkeshi.dao.PhysicalCouponDAO;
import com.xkeshi.dao.ShopDAO;
import com.xkeshi.pojo.po.CashTransaction;
import com.xkeshi.pojo.po.Order;
import com.xkeshi.pojo.po.OrderItem;
import com.xkeshi.pojo.po.PhysicalCoupon;
import com.xkeshi.pojo.po.PhysicalCouponOrder;
import com.xkeshi.pojo.vo.Result;
import com.xkeshi.pojo.vo.SystemParam;
import com.xkeshi.pojo.vo.offline.CashTransactionDetailVO;
import com.xkeshi.pojo.vo.offline.FailOrderVO;
import com.xkeshi.pojo.vo.offline.OfflineFailOrderListVO;
import com.xkeshi.pojo.vo.offline.OfflineOrderDetailVO;
import com.xkeshi.pojo.vo.offline.OfflineOrderVO;
import com.xkeshi.pojo.vo.offline.OfflineOrderValidator;
import com.xkeshi.pojo.vo.offline.OrderDiscount;
import com.xkeshi.pojo.vo.offline.OrderItemVO;
import com.xkeshi.pojo.vo.offline.TransactionList;
import com.xpos.common.entity.face.Business.BusinessType;
import com.xpos.common.persistence.mybatis.ItemInventoryMapper;

/**
 * Created by snoopy on 2015/4/13.
 */
@Service("OfflineOrdersService")
public class OfflineOrdersService {
	private final OfflineOrderValidator validator = new OfflineOrderValidator();

    private static final Logger LOG = LoggerFactory.getLogger(OfflineOrdersService.class);
	
	@Autowired(required = false)
	private Mapper dozerMapper;
	
    @Autowired(required=false)
    private OrderDAO orderDAO;
   
    @Autowired(required=false)
    private OrderItemDAO orderItemDAO;
    
    @Autowired(required = false)
    private PhysicalCouponDAO physicalCouponDAO;
    
    @Autowired(required = false)
    private CashTransactionDAO cashTransactionDAO;
    
    @Autowired(required = false)
    private OperatorDAO operatorDAO;
    
    @Autowired(required = false)
    private ShopDAO shopDAO;
    
    @Autowired(required = false)
    private ItemInventoryMapper itemInventoryMapper;
    
    @Resource(name="OfflineOrdersService")
    private OfflineOrdersService thisService;
    
    public Result uploadOfflineOrder(SystemParam systemParam , List<OfflineOrderVO> offlineOrderList) {
		OfflineFailOrderListVO failList = new OfflineFailOrderListVO();
		List<FailOrderVO> failOrderList = new ArrayList<>();
		if(offlineOrderList != null && offlineOrderList.size()>0) {
			OfflineOrderValidationResult validationResult = null;
			List<PhysicalCoupon> physicalCouponList = physicalCouponDAO.getAllByShopIdIgnoreStatus(systemParam.getMid());
			Map<Long, PhysicalCoupon> physicalCouponMap = new HashMap<>();
			for(PhysicalCoupon physicalCoupon : physicalCouponList){
				physicalCouponMap.put(physicalCoupon.getId(), physicalCoupon);
			}
			Long[] physicalCouponIds = physicalCouponMap.keySet().toArray(new Long[]{});
			boolean isShiftEnabled = shopDAO.getShopByShopId(systemParam.getMid()).getEnableShift();
			for(OfflineOrderVO offlineOrderVO : offlineOrderList ) {
				validationResult = validator.validate(offlineOrderVO, physicalCouponIds,isShiftEnabled);
				String orderNumber = offlineOrderVO.getOfflineOrderDetail().getOrderNumber();
				if(validationResult == null) {
					if(orderDAO.getByOrderNumber(orderNumber)!=null) {
						failOrderList.add(new FailOrderVO(orderNumber, OfflineOrderValidationResult.ORDER_ALREADY_EXISTS.getErrorCode()));
					}else{
						try {
							thisService.saveOfflineOrder(offlineOrderVO,systemParam.getMid(),systemParam.getDeviceNumber(), physicalCouponMap);
						} catch (Exception e) {
							LOG.debug("insert error", e);;
							failOrderList.add(new FailOrderVO(orderNumber, OfflineOrderValidationResult.FAIL_TO_SAVE_ORDER.getErrorCode()));
						}
					}
				}else{
					failOrderList.add(new FailOrderVO(orderNumber, validationResult.getErrorCode()));
				}
			}
		}
		failList.setFailOrderList(failOrderList);
		if(failOrderList.size() > 0 ) {
			return new Result("0", "离线订单上传完成", failList);
		}
		
		return new Result("离线订单上传完成","0");
	}
	
	@Transactional
	public boolean saveOfflineOrder(OfflineOrderVO offlineOrderVO, Long shopId,String deviceNumber, Map<Long, PhysicalCoupon> physicalCouponMap) {
		Order orderPO = saveOrderByOfflineOrder(offlineOrderVO.getOfflineOrderDetail(),shopId,deviceNumber);
		boolean result = orderPO.getId() != null;
		result = result && saveOrderItemByOfflineOrder(offlineOrderVO, orderPO);
		result = result && savePhysicalCouponOrder(offlineOrderVO, physicalCouponMap);
		result = result && saveTransactionList(offlineOrderVO);
		if(!result){
			throw new RuntimeException();
		}
		return true;
	}
	public Order saveOrderByOfflineOrder(OfflineOrderDetailVO offlineOrderVO,Long shopId,String deviceNumber) {
		Order orderPO = dozerMapper.map(offlineOrderVO, Order.class);
		orderPO.setDeviceNumber(deviceNumber);
		orderPO.setBusinessId(shopId);
		orderPO.setBusinessType(BusinessType.SHOP.name());
		orderDAO.insert(orderPO);
		return orderPO;
	}
	public boolean saveOrderItemByOfflineOrder(OfflineOrderVO offlineOrderVO, Order orderPO) {
		List<OrderItemVO> list = offlineOrderVO.getOrderItemList();
		List<OrderItem> orderItemList = new ArrayList<>();
		OrderPaymentStatus status = OrderPaymentStatus.valueOf(orderPO.getStatus());
		boolean requireInventoryUpdate = OrderPaymentStatus.SUCCESS.equals(status) || OrderPaymentStatus.UNPAID.equals(status) 
											|| OrderPaymentStatus.PARTIAL_PAYMENT.equals(status);
		boolean result = true;
		for(OrderItemVO vo : list){
			OrderItem orderItemPO = dozerMapper.map(vo, OrderItem.class);
			orderItemPO.setOrderId(orderPO.getId());
			orderItemList.add(orderItemPO);
			if(requireInventoryUpdate){
				result = result && itemInventoryMapper.updateByItemId(orderItemPO.getItemId(), orderItemPO.getQuantity()) > 0;
			}
		}
		return result && orderItemDAO.batchInsert(orderItemList) == orderItemList.size();
	}
	public boolean savePhysicalCouponOrder(OfflineOrderVO offlineOrderVO, Map<Long, PhysicalCoupon> physicalCouponMap) {
		OrderDiscount orderDiscount = offlineOrderVO.getDiscount();
		if(orderDiscount != null && ArrayUtils.isNotEmpty(orderDiscount.getPhysicalCouponIds())){
			List<PhysicalCouponOrder> couponList = new ArrayList<>();
			for(Long id : orderDiscount.getPhysicalCouponIds()){
				PhysicalCouponOrder physicalCouponOrder = new PhysicalCouponOrder();
				physicalCouponOrder.setOrderNumber(offlineOrderVO.getOfflineOrderDetail().getOrderNumber());
				physicalCouponOrder.setPhysicalCouponId(id);
				physicalCouponOrder.setPhysicalCouponName(physicalCouponMap.get(id).getName());
				physicalCouponOrder.setAmount(physicalCouponMap.get(id).getAmount());
				couponList.add(physicalCouponOrder);
			}
			return physicalCouponDAO.batchInsertXkeshiOrder(couponList) == couponList.size(); 
		}
		return true;
	}
	public boolean saveTransactionList(OfflineOrderVO offlineOrderVO) {
		boolean result = true;
		TransactionList transactionList = offlineOrderVO.getTransactionList();
		if(transactionList != null && CollectionUtils.isNotEmpty(transactionList.getCashTransaction())){
			for(CashTransactionDetailVO vo : transactionList.getCashTransaction()){
				CashTransaction cashTransaction = dozerMapper.map(vo, CashTransaction.class);
				cashTransaction.setOrderNumber(offlineOrderVO.getOfflineOrderDetail().getOrderNumber());
				if(result){
					result = result && cashTransactionDAO.insertOrUpdate(cashTransaction) > 0;
				}else{
					return false;
				}
			}
			return result;
		}
		return true;
	}
}
