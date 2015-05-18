package com.xkeshi.pojo.vo;

import java.math.BigDecimal;
import java.util.List;

/**
 * 客户小票打印基本信息
 */
public class PrintOrderSummaryVO {
	private String orderNumber;
    private String shopName;
    private int counter;
    private String tradeTime;
    private String operatorName;
    private String address;
    private String contact;
    private List<PrintOrderItemSummaryVO> itemList;
    private BigDecimal totalAmount;
    private BigDecimal discountAmount;
    private BigDecimal actuallyAmount;
    private BigDecimal cashReceived;
    private BigDecimal cashReturned;
    private BigDecimal prepaidReceived;
    private BigDecimal posReceived;
    private BigDecimal nfcReceived;
    private BigDecimal alipayReceived;
    private BigDecimal wxpayReceived;
	public String getShopName() {
		return shopName;
	}
	public void setShopName(String shopName) {
		this.shopName = shopName;
	}
	public int getCounter() {
		return counter;
	}
	public void setCounter(int counter) {
		this.counter = counter;
	}
	public String getTradeTime() {
		return tradeTime;
	}
	public void setTradeTime(String tradeTime) {
		this.tradeTime = tradeTime;
	}
	public String getOperatorName() {
		return operatorName;
	}
	public void setOperatorName(String operatorName) {
		this.operatorName = operatorName;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getContact() {
		return contact;
	}
	public void setContact(String contact) {
		this.contact = contact;
	}
	public List<PrintOrderItemSummaryVO> getItemList() {
		return itemList;
	}
	public void setItemList(List<PrintOrderItemSummaryVO> itemList) {
		this.itemList = itemList;
	}
	public BigDecimal getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}
	public BigDecimal getDiscountAmount() {
		return discountAmount;
	}
	public void setDiscountAmount(BigDecimal discountAmount) {
		this.discountAmount = discountAmount;
	}
	public BigDecimal getActuallyAmount() {
		return actuallyAmount;
	}
	public void setActuallyAmount(BigDecimal actuallyAmount) {
		this.actuallyAmount = actuallyAmount;
	}
	public BigDecimal getCashReceived() {
		return cashReceived;
	}
	public void setCashReceived(BigDecimal cashReceived) {
		this.cashReceived = cashReceived;
	}
	public BigDecimal getCashReturned() {
		return cashReturned;
	}
	public void setCashReturned(BigDecimal cashReturned) {
		this.cashReturned = cashReturned;
	}
	public BigDecimal getPrepaidReceived() {
		return prepaidReceived;
	}
	public void setPrepaidReceived(BigDecimal prepaidReceived) {
		this.prepaidReceived = prepaidReceived;
	}
	public BigDecimal getPosReceived() {
		return posReceived;
	}
	public void setPosReceived(BigDecimal posReceived) {
		this.posReceived = posReceived;
	}
	public BigDecimal getNfcReceived() {
		return nfcReceived;
	}
	public void setNfcReceived(BigDecimal nfcReceived) {
		this.nfcReceived = nfcReceived;
	}
	public BigDecimal getAlipayReceived() {
		return alipayReceived;
	}
	public void setAlipayReceived(BigDecimal alipayReceived) {
		this.alipayReceived = alipayReceived;
	}
	public BigDecimal getWxpayReceived() {
		return wxpayReceived;
	}
	public void setWxpayReceived(BigDecimal wxpayReceived) {
		this.wxpayReceived = wxpayReceived;
	}
	public String getOrderNumber() {
		return orderNumber;
	}
	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}
    
}
