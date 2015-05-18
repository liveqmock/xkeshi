package com.xkeshi.pojo.vo.offline;

import static com.xkeshi.common.em.OfflineOrderValidationResult.ACTUALLY_PAID_INVALID;
import static com.xkeshi.common.em.OfflineOrderValidationResult.CREATED_OR_UPDATED_DATE_INVALID;
import static com.xkeshi.common.em.OfflineOrderValidationResult.OPERATOR_IS_NULL;
import static com.xkeshi.common.em.OfflineOrderValidationResult.OPRATOR_SESSION_IS_NULL;
import static com.xkeshi.common.em.OfflineOrderValidationResult.ORDER_DISCOUNT_INVALID;
import static com.xkeshi.common.em.OfflineOrderValidationResult.ORDER_IS_NULL;
import static com.xkeshi.common.em.OfflineOrderValidationResult.ORDER_ITEM_INVLAID;
import static com.xkeshi.common.em.OfflineOrderValidationResult.ORDER_NUMBER_IS_NULL;
import static com.xkeshi.common.em.OfflineOrderValidationResult.ORDER_STATUS_INVALID;
import static com.xkeshi.common.em.OfflineOrderValidationResult.ORDER_TYPE_INVALID;
import static com.xkeshi.common.em.OfflineOrderValidationResult.TOTAL_AMOUNT_INVALID;
import static com.xkeshi.common.em.OfflineOrderValidationResult.TOTAL_AMOUNT_LESS_THAN_ACTUALLY_PAID;
import static com.xkeshi.common.em.OfflineOrderValidationResult.TRANSACTION_INVALID;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import com.xkeshi.common.em.OfflineOrderValidationResult;
import com.xkeshi.common.em.OrderPaymentStatus;
import com.xkeshi.common.em.TransactionPaymentStatus;

public class OfflineOrderValidator {
	private final BigDecimal ZERO = new BigDecimal(0);
	private final BigDecimal ONE = new BigDecimal(1);
	
	public OfflineOrderValidationResult validate(OfflineOrderVO offlineOrderVO, 
												Long[] physicalCoupons,
												boolean isShiftEnabled) {
        if (offlineOrderVO == null || offlineOrderVO.getOfflineOrderDetail() == null) {
        	return ORDER_IS_NULL;
        }
        OfflineOrderDetailVO offlineOrderDetailVO = offlineOrderVO.getOfflineOrderDetail();
        if (StringUtils.isBlank(offlineOrderDetailVO.getOrderNumber())) {
        	return ORDER_NUMBER_IS_NULL;
        }else if(!StringUtils.equals(offlineOrderDetailVO.getOrderType(), "XPOS_ORDER")) {
        	return ORDER_TYPE_INVALID;
        }else if(offlineOrderDetailVO.getTotalAmount() == null 
        		|| offlineOrderDetailVO.getTotalAmount().compareTo(ZERO) < 0){
        	return TOTAL_AMOUNT_INVALID;
        }else if(offlineOrderDetailVO.getActuallyPaid() == null 
        		|| offlineOrderDetailVO.getActuallyPaid().compareTo(ZERO) < 0){
        	return ACTUALLY_PAID_INVALID;
        }else if(offlineOrderDetailVO.getTotalAmount().compareTo(offlineOrderDetailVO.getActuallyPaid()) < 0){
        	return TOTAL_AMOUNT_LESS_THAN_ACTUALLY_PAID;
        }else if(OrderPaymentStatus.getByValue(offlineOrderDetailVO.getStatus()) == null){
        	return ORDER_STATUS_INVALID;
        }else if(offlineOrderDetailVO.getOperatorId() == null){
        	return OPERATOR_IS_NULL;
        }else if(isShiftEnabled && StringUtils.isBlank(offlineOrderDetailVO.getOperatorSessionCode())){
        	return OPRATOR_SESSION_IS_NULL;
        }else if(StringUtils.isBlank(offlineOrderDetailVO.getCreatedTime())
        		|| StringUtils.isBlank(offlineOrderDetailVO.getUpdatedTime())) {
        	return CREATED_OR_UPDATED_DATE_INVALID;
        }else if(!validOrderItems(offlineOrderVO.getOrderItemList())){
        	return ORDER_ITEM_INVLAID;
        }else if(!validDiscount(offlineOrderVO.getDiscount(), physicalCoupons)){
        	return ORDER_DISCOUNT_INVALID;
        }else if(!validTransactionList(offlineOrderVO.getTransactionList())){
        	return TRANSACTION_INVALID;
        }else{
        	return null;
        }
        
    }
	
	boolean validOrderItems(List<OrderItemVO> orderItemList){
		if(CollectionUtils.isNotEmpty(orderItemList)){
			for(OrderItemVO vo : orderItemList){
				if(vo == null || vo.getItemId() == null
						|| StringUtils.isBlank(vo.getItemName()) || vo.getPrice() == null
						|| vo.getPrice().compareTo(ZERO) < 0 || vo.getQuantity() == null
						|| vo.getQuantity() <= 0){
					return false;
				}
			}
		}else{
			return false;
		}
		return true;
	}

	boolean validDiscount(OrderDiscount orderDiscount, Long[] physicalCoupons){
		if(orderDiscount != null){
			if(ArrayUtils.isNotEmpty(orderDiscount.getPhysicalCouponIds())){
				for(Long physicalCouponId : orderDiscount.getPhysicalCouponIds()){
					if(!ArrayUtils.contains(physicalCoupons, physicalCouponId)){
						return false;
					}
				}
			}
			
			MemberDiscountDetailVO memberDiscount = orderDiscount.getMemberDiscount();
			if(memberDiscount != null){
				return memberDiscount.getMemberId() != null && memberDiscount.getMemberId() > 0
						&& memberDiscount.getMemberTypeId() != null && memberDiscount.getMemberTypeId() > 0
						&& memberDiscount.getDiscount() != null && memberDiscount.getDiscount().compareTo(ZERO) > 0
						&& memberDiscount.getDiscount().compareTo(ONE) <= 0;
			}
		}
		return true;
	}

	boolean validTransactionList(TransactionList transactionList){
		if(transactionList == null){
			return true;
		}
		if(CollectionUtils.isNotEmpty(transactionList.getCashTransaction())){
			for(CashTransactionDetailVO vo : transactionList.getCashTransaction()){
				if(vo == null || StringUtils.isBlank(vo.getSerial())
					|| vo.getReceived() == null || vo.getReceived().compareTo(ZERO) < 0
					|| vo.getAmount() == null || vo.getAmount().compareTo(ZERO) < 0
					|| vo.getReceived().compareTo(vo.getAmount()) < 0
					|| vo.getReturned() == null 
					|| vo.getReturned().compareTo(vo.getReceived().subtract(vo.getAmount())) != 0
					|| vo.getStatus() == null
					|| TransactionPaymentStatus.findByValue((int)vo.getStatus().longValue()) == null
					|| vo.getCreatedTime() == null
					|| vo.getUpdatedTime() == null){
					return false;
				}
			}
		}
		if(CollectionUtils.isNotEmpty(transactionList.getPrepaidCardTransaction())){
			for(PrepaidCardTransactionDetailVO vo : transactionList.getPrepaidCardTransaction()){
				if(vo == null || StringUtils.isBlank(vo.getSerial())
					|| vo.getAmount() == null || vo.getAmount().compareTo(ZERO) < 0
					|| TransactionPaymentStatus.findByValue(vo.getStatus()) == null
					|| vo.getCreatedTime() == null
					|| vo.getUpdatedTime() == null){
					return false;
				}
			}
		}
		if(CollectionUtils.isNotEmpty(transactionList.getAlipayTransaction())){
			for(AlipayTransactionDetailVO vo : transactionList.getAlipayTransaction()){
				if(vo == null || StringUtils.isBlank(vo.getSerial())
						|| vo.getAmount() == null || vo.getAmount().compareTo(ZERO) < 0
						|| TransactionPaymentStatus.findByValue(vo.getStatus()) == null
						|| vo.getCreatedTime() == null
						|| vo.getUpdatedTime() == null){
					return false;
				}
			}
		}
		if(CollectionUtils.isNotEmpty(transactionList.getWxpayTransaction())){
			for(WxpayTransactionDetailVO vo : transactionList.getWxpayTransaction()){
				if(vo == null || StringUtils.isBlank(vo.getSerial())
						|| vo.getAmount() == null || vo.getAmount().compareTo(ZERO) < 0
						|| TransactionPaymentStatus.findByValue(vo.getStatus()) == null
						|| vo.getCreatedTime() == null
						|| vo.getUpdatedTime() == null){
					return false;
				}
			}
		}
		if(CollectionUtils.isNotEmpty(transactionList.getPosTransaction())){
			for(POSTransactionDetailVO vo : transactionList.getPosTransaction()){
				if(vo == null || StringUtils.isBlank(vo.getGatewayAccount())
						|| StringUtils.isBlank(vo.getGatewayType())
						|| StringUtils.isBlank(vo.getSerial())
						|| vo.getAmount() == null || vo.getAmount().compareTo(ZERO) < 0
						|| TransactionPaymentStatus.findByValue(vo.getStatus()) == null
						|| StringUtils.isBlank(vo.getTerminal())
						|| vo.getCreatedTime() == null
						|| vo.getUpdatedTime() == null){
					return false;
				}
			}
		}
		if(CollectionUtils.isNotEmpty(transactionList.getNfcTransaction())){
			for(NFCTransactionDetailVO vo : transactionList.getNfcTransaction()){
				if(vo == null || StringUtils.isBlank(vo.getRegisterMid())
						|| StringUtils.isBlank(vo.getSerial())
						|| vo.getAmount() == null || vo.getAmount().compareTo(ZERO) < 0
						|| TransactionPaymentStatus.findByValue(vo.getStatus()) == null
						|| StringUtils.isBlank(vo.getTerminal())
						|| vo.getCreatedTime() == null
						|| vo.getUpdatedTime() == null){
					return false;
				}
			}
		}
		return true;
	}
}
