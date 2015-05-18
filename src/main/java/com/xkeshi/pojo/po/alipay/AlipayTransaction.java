package com.xkeshi.pojo.po.alipay;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.xpos.common.entity.NewBaseEntity;

public class AlipayTransaction extends NewBaseEntity{
	private static final long serialVersionUID = 3404784769434293804L;
	
	private String orderNumber; //爱客仕系统内部订单号
	private String thirdOrderCode; //第三方平台订单号
	private String prepaidCardChargeOrderCode; //预付卡充值订单号
	private String sellerAccount; //卖家支付宝账号
	private String buyerId; //买家支付宝ID
	private String buyerAccount; //买家支付宝账号
	private BigDecimal amount;
	private int alipayPaymentStatus; //支付状态
	private String serial; //该笔支付在爱客仕平台的唯一流水号
	private String alipaySerial; //支付宝平台的交易流水号
	private String responseCode;
	private String deviceNumber;
	private Date tradeTime;
	
	public String getOrderNumber() {
		return orderNumber;
	}
	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}
	public String getThirdOrderCode() {
		return thirdOrderCode;
	}
	public void setThirdOrderCode(String thirdOrderCode) {
		this.thirdOrderCode = thirdOrderCode;
	}
	public String getPrepaidCardChargeOrderCode() {
		return prepaidCardChargeOrderCode;
	}
	public void setPrepaidCardChargeOrderCode(String prepaidCardChargeOrderCode) {
		this.prepaidCardChargeOrderCode = prepaidCardChargeOrderCode;
	}
	public String getSellerAccount() {
		return sellerAccount;
	}
	public void setSellerAccount(String sellerAccount) {
		this.sellerAccount = sellerAccount;
	}
	public String getBuyerId() {
		return buyerId;
	}
	public void setBuyerId(String buyerId) {
		this.buyerId = buyerId;
	}
	public String getBuyerAccount() {
		return buyerAccount;
	}
	public void setBuyerAccount(String buyerAccount) {
		this.buyerAccount = buyerAccount;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	public int getAlipayPaymentStatus() {
		return alipayPaymentStatus;
	}
	public void setAlipayPaymentStatus(int alipayPaymentStatus) {
		this.alipayPaymentStatus = alipayPaymentStatus;
	}
	public String getSerial() {
		return serial;
	}
	public void setSerial(String serial) {
		this.serial = serial;
	}
	public String getAlipaySerial() {
		return alipaySerial;
	}
	public void setAlipaySerial(String alipaySerial) {
		this.alipaySerial = alipaySerial;
	}
	public String getResponseCode() {
		return responseCode;
	}
	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}
	public String getDeviceNumber() {
		return deviceNumber;
	}
	public void setDeviceNumber(String deviceNumber) {
		this.deviceNumber = deviceNumber;
	}
	public Date getTradeTime() {
		return tradeTime;
	}
	public void setTradeTime(Date tradeTime) {
		this.tradeTime = tradeTime;
	}
	
	public String getOrderCodeByType(String orderType){
		if(StringUtils.equals("XPOS_ORDER", orderType)){
			return getOrderNumber();
		}else if(StringUtils.equals("XPOS_PREPAID", orderType)){
			return getPrepaidCardChargeOrderCode();
		}else if(StringUtils.equals("THIRD_ORDER", orderType)){
			return getThirdOrderCode();
		}
		return null;
	}
	
	public void setOrderCodeByType(String orderCode, String orderType){
		if(StringUtils.equals("XPOS_ORDER", orderType)){
			setOrderNumber(orderCode);
		}else if(StringUtils.equals("XPOS_PREPAID", orderType)){
			setPrepaidCardChargeOrderCode(orderCode);
		}else if(StringUtils.equals("THIRD_ORDER", orderType)){
			setThirdOrderCode(orderCode);
		}
	}
}
