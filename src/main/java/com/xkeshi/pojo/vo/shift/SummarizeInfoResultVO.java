package com.xkeshi.pojo.vo.shift;

import java.math.BigDecimal;

/**
 * 交接班清单
 * @author xk
 *
 */
public class SummarizeInfoResultVO {
	
	/*交接班信息*/
	private ShiftInfoVO  shiftInfo;

	/*当前班次支付成功的订单总数*/
	private Integer totalOrderCount ;
	
	/*订单总金额*/
	private BigDecimal totalOrderAmount ;
	
	/*应收现金*/
	private BigDecimal  totalReceivableCashAmount;
	
	/*订单优惠*/
	private OrderPreferentialVO   orderPreferential ; 
	
	/*订单支付(爱客仕系统订单或第三方订单)*/
	private OrderPayVO    orderPay ;
	
	
	public SummarizeInfoResultVO() {
		super();
	}
	 
	public BigDecimal getTotalReceivableCashAmount() {
		if (totalReceivableCashAmount != null) {
			return totalReceivableCashAmount.setScale(2,BigDecimal.ROUND_DOWN);
		}
		return totalReceivableCashAmount;
	}

	public void setTotalReceivableCashAmount(BigDecimal totalReceivableCashAmount) {
		this.totalReceivableCashAmount = totalReceivableCashAmount;
	}

	public Integer getTotalOrderCount() {
		return totalOrderCount;
	}
	public void setTotalOrderCount(Integer totalOrderCount) {
		this.totalOrderCount = totalOrderCount;
	}
	public BigDecimal getTotalOrderAmount() {
		if (totalOrderAmount != null) {
			return totalOrderAmount.setScale(2,BigDecimal.ROUND_DOWN);
		}
		return totalOrderAmount;
	}
	public void setTotalOrderAmount(BigDecimal totalOrderAmount) {
		this.totalOrderAmount = totalOrderAmount;
	}
	public OrderPreferentialVO getOrderPreferential() {
		return orderPreferential;
	}
	public void setOrderPreferential(OrderPreferentialVO orderPreferential) {
		this.orderPreferential = orderPreferential;
	}
	public OrderPayVO getOrderPay() {
		return orderPay;
	}
	public void setOrderPay(OrderPayVO orderPay) {
		this.orderPay = orderPay;
	}
	public ShiftInfoVO getShiftInfo() {
		return shiftInfo;
	}
	public void setShiftInfo(ShiftInfoVO shiftInfo) {
		this.shiftInfo = shiftInfo;
	}
	 
	 
	 
	
	
}




