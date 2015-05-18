package com.xkeshi.pojo.vo;

import java.math.BigDecimal;
import java.util.List;

/**
 * 通过订单号编号查看订单详情
 */
public class OrderDetailVO {
    private String orderNumber;
    private String operatorName;
    private String status;
    private String tradeTime;
    private Integer totalItemCount;
    private BigDecimal totalAmount;
    private String paymentType;
    private BigDecimal actuallyAmount;
    private BigDecimal discountAmount;
    private BigDecimal refundAmount;
    private String refundTime;
    private List<OrderItemDetailVO> itemList;
	public String getOrderNumber() {
		return orderNumber;
	}
	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}
	public String getOperatorName() {
		return operatorName;
	}
	public void setOperatorName(String operatorName) {
		this.operatorName = operatorName;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getTradeTime() {
		return tradeTime;
	}
	public void setTradeTime(String tradeTime) {
		this.tradeTime = tradeTime;
	}
	public Integer getTotalItemCount() {
		return totalItemCount;
	}
	public void setTotalItemCount(Integer totalItemCount) {
		this.totalItemCount = totalItemCount;
	}
	public BigDecimal getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}
	public String getPaymentType() {
		return paymentType;
	}
	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}
	public BigDecimal getActuallyAmount() {
		return actuallyAmount;
	}
	public void setActuallyAmount(BigDecimal actuallyAmount) {
		this.actuallyAmount = actuallyAmount;
	}
	public BigDecimal getDiscountAmount() {
		return discountAmount;
	}
	public void setDiscountAmount(BigDecimal discountAmount) {
		this.discountAmount = discountAmount;
	}
	public BigDecimal getRefundAmount() {
		return refundAmount;
	}
	public void setRefundAmount(BigDecimal refundAmount) {
		this.refundAmount = refundAmount;
	}
	public String getRefundTime() {
		return refundTime;
	}
	public void setRefundTime(String refundTime) {
		this.refundTime = refundTime;
	}
	public List<OrderItemDetailVO> getItemList() {
		return itemList;
	}
	public void setItemList(List<OrderItemDetailVO> itemList) {
		this.itemList = itemList;
	}
    
    
   
    
}
