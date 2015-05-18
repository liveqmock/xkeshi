package com.xkeshi.pojo.vo;

import java.math.BigDecimal;

/**
 * 每日统计数据概要
 */
public class DailyStatisticsSummaryVO {
	private int orderCount; //收银总笔数
	private BigDecimal orderAmount; //收银总金额
	private int cashTransactionCount; //现金笔数
	private BigDecimal cashTransactionAmount; //现金总金额
	private int alipayTransactionCount; //支付宝笔数
	private BigDecimal alipayTransactionAmount; //支付宝总金额
	private int POSTransactionCount; //刷卡笔数
	private BigDecimal POSTransactionAmount; //刷卡总金额
	private int BankNFCTransactionCount; //闪付笔数
	private BigDecimal BankNFCTransactionAmount; //闪付总金额
	private int wxpayTransactionCount; //微信总笔数
	private BigDecimal wxpayTransactionAmount; //微信总金额
	
	private int prepaidCardChargeOrderCount; //预付卡充值笔数
	private BigDecimal prepaidCardChargeOrderAmount; //预付卡充值总金额
	
	private int physicalCouponCount; //实体券核销数量
	private BigDecimal physicalCouponAmount; //实体券核销总金额
	
	private int couponConsumedCount; //电子券核销数量
	
	private int registeredMemberCount; //新增会员数量

	public int getOrderCount() {
		return orderCount;
	}

	public void setOrderCount(int orderCount) {
		this.orderCount = orderCount;
	}

	public BigDecimal getOrderAmount() {
		return orderAmount;
	}

	public void setOrderAmount(BigDecimal orderAmount) {
		this.orderAmount = orderAmount;
	}

	public int getCashTransactionCount() {
		return cashTransactionCount;
	}

	public void setCashTransactionCount(int cashTransactionCount) {
		this.cashTransactionCount = cashTransactionCount;
	}

	public BigDecimal getCashTransactionAmount() {
		return cashTransactionAmount;
	}

	public void setCashTransactionAmount(BigDecimal cashTransactionAmount) {
		this.cashTransactionAmount = cashTransactionAmount;
	}

	public int getAlipayTransactionCount() {
		return alipayTransactionCount;
	}

	public void setAlipayTransactionCount(int alipayTransactionCount) {
		this.alipayTransactionCount = alipayTransactionCount;
	}

	public BigDecimal getAlipayTransactionAmount() {
		return alipayTransactionAmount;
	}

	public void setAlipayTransactionAmount(BigDecimal alipayTransactionAmount) {
		this.alipayTransactionAmount = alipayTransactionAmount;
	}

	public int getPOSTransactionCount() {
		return POSTransactionCount;
	}

	public void setPOSTransactionCount(int pOSTransactionCount) {
		POSTransactionCount = pOSTransactionCount;
	}

	public BigDecimal getPOSTransactionAmount() {
		return POSTransactionAmount;
	}

	public void setPOSTransactionAmount(BigDecimal pOSTransactionAmount) {
		POSTransactionAmount = pOSTransactionAmount;
	}

	public int getBankNFCTransactionCount() {
		return BankNFCTransactionCount;
	}

	public void setBankNFCTransactionCount(int bankNFCTransactionCount) {
		BankNFCTransactionCount = bankNFCTransactionCount;
	}

	public BigDecimal getBankNFCTransactionAmount() {
		return BankNFCTransactionAmount;
	}

	public void setBankNFCTransactionAmount(BigDecimal bankNFCTransactionAmount) {
		BankNFCTransactionAmount = bankNFCTransactionAmount;
	}

	public int getWxpayTransactionCount() {
		return wxpayTransactionCount;
	}

	public void setWxpayTransactionCount(int wxpayTransactionCount) {
		this.wxpayTransactionCount = wxpayTransactionCount;
	}

	public BigDecimal getWxpayTransactionAmount() {
		return wxpayTransactionAmount;
	}

	public void setWxpayTransactionAmount(BigDecimal wxpayTransactionAmount) {
		this.wxpayTransactionAmount = wxpayTransactionAmount;
	}

	public int getPrepaidCardChargeOrderCount() {
		return prepaidCardChargeOrderCount;
	}

	public void setPrepaidCardChargeOrderCount(int prepaidCardChargeOrderCount) {
		this.prepaidCardChargeOrderCount = prepaidCardChargeOrderCount;
	}

	public BigDecimal getPrepaidCardChargeOrderAmount() {
		return prepaidCardChargeOrderAmount;
	}

	public void setPrepaidCardChargeOrderAmount(
			BigDecimal prepaidCardChargeOrderAmount) {
		this.prepaidCardChargeOrderAmount = prepaidCardChargeOrderAmount;
	}

	public int getPhysicalCouponCount() {
		return physicalCouponCount;
	}

	public void setPhysicalCouponCount(int physicalCouponCount) {
		this.physicalCouponCount = physicalCouponCount;
	}

	public BigDecimal getPhysicalCouponAmount() {
		return physicalCouponAmount;
	}

	public void setPhysicalCouponAmount(BigDecimal physicalCouponAmount) {
		this.physicalCouponAmount = physicalCouponAmount;
	}

	public int getCouponConsumedCount() {
		return couponConsumedCount;
	}

	public void setCouponConsumedCount(int couponConsumedCount) {
		this.couponConsumedCount = couponConsumedCount;
	}

	public int getRegisteredMemberCount() {
		return registeredMemberCount;
	}

	public void setRegisteredMemberCount(int registeredMemberCount) {
		this.registeredMemberCount = registeredMemberCount;
	}
	
	
}
