package com.xkeshi.service;

import com.xkeshi.common.em.OfflineOrderValidationResult;
import com.xkeshi.common.em.OrderPaymentStatus;
import com.xkeshi.common.em.TransactionPaymentStatus;
import com.xkeshi.dao.*;
import com.xkeshi.pojo.po.*;
import com.xkeshi.pojo.po.alipay.AlipayTransaction;
import com.xkeshi.pojo.vo.Result;
import com.xkeshi.pojo.vo.SystemParam;
import com.xkeshi.pojo.vo.offline.*;
import com.xkeshi.service.payment.AlipayQRCodePaymentService;
import com.xkeshi.service.payment.PrepaidCardPaymentService;
import com.xpos.common.entity.face.Business.BusinessType;
import com.xpos.common.entity.pos.POSGatewayAccount;
import com.xpos.common.entity.pos.POSTransaction;
import com.xpos.common.entity.pos.POSTransaction.POSTransactionType;
import com.xpos.common.persistence.mybatis.*;
import com.xpos.common.utils.DateUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.dozer.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.ParseException;
import java.util.*;

/**
 * Created by snoopy on 2015/4/13.
 */
@Service("InterruptedOrdersService")
public class InterruptedOrdersService {
	private final OfflineOrderValidator validator = new OfflineOrderValidator();

    private static final Logger LOG = LoggerFactory.getLogger(InterruptedOrdersService.class);
	
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
    private OrderMemberDiscountDAO orderMemberDiscountDAO;
    
    @Autowired(required = false)
    private MemberTypeDAO memberTypeDAO;
    
    @Autowired(required = false)
    private PrepaidCardTransactionDAO prepaidCardTransactionDAO;
    
    @Autowired(required = false)
    private POSTransactionMapper posTransactionMapper;

    @Autowired(required = false)
    private BankNFCTransactionMapper bankNfcTransactionMapper;

    @Autowired(required = false)
    private AlipayTransactionMapper alipayTransactionMapper;
    
    @Autowired(required = false)
    private POSGatewayAccountMapper posGatewayAccountMapper;
    
    @Autowired
    private AlipayQRCodePaymentService alipayQRCodePaymentService;
    
    @Autowired(required = false)
    private ItemInventoryMapper itemInventoryMapper;
    
    @Autowired
    private PrepaidService prepaidService;
    
    @Autowired
    private PrepaidCardPaymentService prepaidCardPaymentService;
    
    @Resource(name="InterruptedOrdersService")
    private InterruptedOrdersService thisService;
    
    public Result uploadInterruptedOrder(SystemParam systemParam , List<OfflineOrderVO> offlineOrderList) {
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
					try {
						thisService.saveOfflineOrder(offlineOrderVO,systemParam.getMid(),systemParam.getDeviceNumber(), physicalCouponMap);
					} catch (Exception e) {
						LOG.debug("insert error", e);;
						failOrderList.add(new FailOrderVO(orderNumber, OfflineOrderValidationResult.FAIL_TO_SAVE_ORDER.getErrorCode()));
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
		Order orderPO = saveOrderByOfflineOrder(offlineOrderVO,shopId,deviceNumber);
		boolean result = orderPO.getId() != null;
		result = result && savePhysicalCouponOrder(offlineOrderVO, physicalCouponMap);
		result = result && saveOrderMemberDiscount(offlineOrderVO, shopId);
		result = result && saveTransactionList(offlineOrderVO, shopId,deviceNumber);
		if(!result){
			throw new RuntimeException();
		}
		return true;
	}
    
    
	public Order saveOrderByOfflineOrder(OfflineOrderVO offlineOrderVO,Long shopId,String deviceNumber) {
		OfflineOrderDetailVO detailVO = offlineOrderVO.getOfflineOrderDetail();
		String orderNumber = detailVO.getOrderNumber();
		Order orderPO = orderDAO.getByOrderNumber(orderNumber);
		if(orderPO == null){
			orderPO = dozerMapper.map(detailVO, Order.class);
			orderPO.setDeviceNumber(deviceNumber);
			orderPO.setBusinessId(shopId);
			orderPO.setBusinessType(BusinessType.SHOP.name());
			orderDAO.insert(orderPO);
			saveOrderItemByOfflineOrder(offlineOrderVO,orderPO);
		}else{
			//第一步进行库存校验
			OrderPaymentStatus status = OrderPaymentStatus.getByValue(detailVO.getStatus());
			List<OrderItemVO> list = offlineOrderVO.getOrderItemList();
			if ((StringUtils.equals(orderPO.getStatus(),"TIMEOUT")
                   || StringUtils.equals(orderPO.getStatus(),"FAILED")
                   || StringUtils.equals(orderPO.getStatus(),"REFUND"))
                   && 
                   (OrderPaymentStatus.SUCCESS.equals(status) 
                	|| OrderPaymentStatus.UNPAID.equals(status) 
                	|| OrderPaymentStatus.PARTIAL_PAYMENT.equals(status))
                   ){//服务器已还原库存，客户端返回成功锁定状态，则扣除库存
				for(OrderItemVO vo : list){
					OrderItem orderItemPO = dozerMapper.map(vo, OrderItem.class);
					itemInventoryMapper.updateByItemId(orderItemPO.getItemId(), orderItemPO.getQuantity());
				}
			}else if((StringUtils.equals(orderPO.getStatus(),"SUCCESS")
		                   || StringUtils.equals(orderPO.getStatus(),"UNPAID")
		                   || StringUtils.equals(orderPO.getStatus(),"PARTIAL_PAYMENT"))
		                   && 
		                   (OrderPaymentStatus.FAILED.equals(status) 
		                	|| OrderPaymentStatus.TIMEOUT.equals(status) 
		                	|| OrderPaymentStatus.CANCEL.equals(status)
		                	|| OrderPaymentStatus.REFUND.equals(status)
		                	)
				) {//服务器已锁定库存，客户端返回失败状态，则还原库存
				for(OrderItemVO vo : list){
					OrderItem orderItemPO = dozerMapper.map(vo, OrderItem.class);
					itemInventoryMapper.updateAddByItemId(orderItemPO.getItemId(), orderItemPO.getQuantity());
				}
			}
			//第二步进行订单状态更新
			orderPO.setActuallyPaid(detailVO.getActuallyPaid());
			orderPO.setType(detailVO.getType());
			orderPO.setStatus(status.name());
			orderPO.setManagerId(detailVO.getManagerId());
			try {
				orderPO.setModifyDate(DateUtil.getDateFormatter(detailVO.getUpdatedTime()));
			} catch (ParseException e) {
			}
			if(orderDAO.updateOfflineOrder(orderPO) <= 0){;
				orderPO.setId(null);
			}
		}
		return orderPO;
	}
	
	private boolean saveOrderItemByOfflineOrder(OfflineOrderVO offlineOrderVO, Order orderPO) {
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
			physicalCouponDAO.deleteByOrderNumber(offlineOrderVO.getOfflineOrderDetail().getOrderNumber());
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
    
    
	public boolean saveOrderMemberDiscount(OfflineOrderVO offlineOrderVO, Long shopId) {
		OrderDiscount orderDiscount = offlineOrderVO.getDiscount();
		if(orderDiscount != null && orderDiscount.getMemberDiscount() != null){
			MemberDiscountDetailVO memberDiscountDetailVO = orderDiscount.getMemberDiscount();
			String orderNumber = offlineOrderVO.getOfflineOrderDetail().getOrderNumber();
			//清空原有会员折扣记录
			orderMemberDiscountDAO.clearMemberDiscountByOrderNumber(orderNumber);
			OrderMemberDiscount orderMemberDiscount = new OrderMemberDiscount();
			orderMemberDiscount.setOrderNumber(orderNumber);
			orderMemberDiscount.setMemberId(memberDiscountDetailVO.getMemberId());
			orderMemberDiscount.setMemberTypeId(memberDiscountDetailVO.getMemberTypeId());
			boolean centralManagementMember = memberTypeDAO.checkCentralManagementMemberByShopId(shopId);
			orderMemberDiscount.setBusinessType(centralManagementMember? "MERCHANT" :"SHOP");
			orderMemberDiscount.setDiscount(memberDiscountDetailVO.getDiscount());
			//添加会员折扣
			orderMemberDiscountDAO.insert(orderMemberDiscount);
			return orderMemberDiscount.getId() != null;
		}
		return true;
	}
	
	public boolean saveTransactionList(OfflineOrderVO offlineOrderVO, Long shopId,String deviceNumber) {
		TransactionList transactionList = offlineOrderVO.getTransactionList();
		if(transactionList == null){
			return true;
		}
		boolean result = true;
		String orderNumber = offlineOrderVO.getOfflineOrderDetail().getOrderNumber();
		if(CollectionUtils.isNotEmpty(transactionList.getCashTransaction())){
			for(CashTransactionDetailVO vo : transactionList.getCashTransaction()){
				CashTransaction cashTransaction = dozerMapper.map(vo, CashTransaction.class);
				cashTransaction.setOrderNumber(offlineOrderVO.getOfflineOrderDetail().getOrderNumber());
				if(result){
					result= result && cashTransactionDAO.insertOrUpdate(cashTransaction) > 0;
				}else{
					return false;
				}
			}
		}
		
		if(result && CollectionUtils.isNotEmpty(transactionList.getPosTransaction())){
			for(POSTransactionDetailVO vo : transactionList.getPosTransaction()){
				POSTransaction posTransaction = dozerMapper.map(vo, POSTransaction.class);
				posTransaction.setOrderNumber(orderNumber);
				posTransaction.setBusinessId(shopId);
				posTransaction.setBusinessType(BusinessType.SHOP);
				posTransaction.setType(POSTransactionType.BANK_CARD);
				if(result){
					result = result && posTransactionMapper.insertOrUpdate(posTransaction) > 0;
				}else{
					return false;
				}
			}
		}
		
		if(result && CollectionUtils.isNotEmpty(transactionList.getNfcTransaction())){
			for(NFCTransactionDetailVO vo : transactionList.getNfcTransaction()){
				BankNFCTransaction bankNFCTransaction = dozerMapper.map(vo, BankNFCTransaction.class);
				bankNFCTransaction.setOrderNumber(orderNumber);
				if(result){
					result =  result && bankNfcTransactionMapper.insertOrUpdate(bankNFCTransaction) > 0;
				}else{
					return false;
				}
			}
		}
		
		if(result && CollectionUtils.isNotEmpty(transactionList.getAlipayTransaction())){
			for(AlipayTransactionDetailVO vo : transactionList.getAlipayTransaction()){
				if(!result){
					return false;
				}
				AlipayTransaction alipayTransaction = dozerMapper.map(vo, AlipayTransaction.class);
				alipayTransaction.setOrderNumber(orderNumber);
				AlipayTransaction alipayTransactionPO = alipayTransactionMapper.getByOrderNumberAndSerial(orderNumber,alipayTransaction.getSerial());
				if(alipayTransactionPO == null) {
					POSGatewayAccount account = posGatewayAccountMapper.selectAlipayAccountByShopId(shopId);
					if(account == null || StringUtils.isBlank(account.getAccount())) {
						return false;
					}
					alipayTransaction.setAlipayPaymentStatus(TransactionPaymentStatus.FAILED.getValue());
					alipayTransaction.setDeviceNumber(deviceNumber);
					alipayTransaction.setSellerAccount(account.getAccount());
					alipayTransactionMapper.insert(alipayTransaction);
					result = result && alipayTransaction.getId() != null;
				}else if(alipayTransaction.getAlipayPaymentStatus() == TransactionPaymentStatus.REVERSAL.getValue()
						&& alipayTransactionPO.getAlipayPaymentStatus() == TransactionPaymentStatus.SUCCESS.getValue()){
					POSGatewayAccount account = posGatewayAccountMapper.selectAlipayAccountByAccountAndShopId(alipayTransactionPO.getSellerAccount(), shopId);
					if(DateUtils.isSameDay(new Date(), alipayTransactionPO.getTradeTime())){
						//cancel
						result = result && alipayQRCodePaymentService.cancelOfflinePayment(alipayTransactionPO, account);
					}else if(new Date().after(alipayTransactionPO.getTradeTime())){
						//refund
						result = result && alipayQRCodePaymentService.refundOfflinePayment(alipayTransactionPO, account);
					}
					result = result && alipayTransactionMapper.updateStatusById(alipayTransactionPO.getId(), TransactionPaymentStatus.REFUND.getValue());
				}else{
					//ignore other situations
				}
			}
		}
		
		if(result && CollectionUtils.isNotEmpty(transactionList.getWxpayTransaction())){
			//TODO
		}
		
		if(result && CollectionUtils.isNotEmpty(transactionList.getPrepaidCardTransaction())){
			for(PrepaidCardTransactionDetailVO vo : transactionList.getPrepaidCardTransaction()){
				if(!result){
					return false;
				}
				PrepaidCardTransaction prepaidCardTransaction = dozerMapper.map(vo, PrepaidCardTransaction.class);
				prepaidCardTransaction.setOrderNumber(orderNumber);
				PrepaidCardTransaction prepaidCardTransactionPO = prepaidCardTransactionDAO.getByOrderNumberAndSerial(orderNumber, prepaidCardTransaction.getSerial());
				//获取预付卡
		        Long prepaidCardId = prepaidService.getIdByMemberIdAndShopId(vo.getMemberId(),shopId);
		        if(prepaidCardId == null) {
		        	return false;
		        }
				if(prepaidCardTransactionPO == null){
					prepaidCardTransaction.setPrepaidCardPaymentStatusId(Long.valueOf(TransactionPaymentStatus.FAILED.getValue()));
					prepaidCardTransaction.setPrepaidCardId(prepaidCardId);
					prepaidCardTransactionDAO.insert(prepaidCardTransaction);
					result = result && prepaidCardTransaction.getId() != null;
				}else if(prepaidCardTransaction.getPrepaidCardPaymentStatusId().equals(Long.valueOf(TransactionPaymentStatus.REVERSAL.getValue()))
						&& prepaidCardTransactionPO.getPrepaidCardPaymentStatusId().equals(Long.valueOf(TransactionPaymentStatus.SUCCESS.getValue()))){
					result = result && prepaidCardPaymentService.refundForOfflinePrepaidCard(prepaidCardTransactionPO);
				}
			}
		}
		
		return result;
	}

}

