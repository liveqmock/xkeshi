package com.xkeshi.pojo.vo;

import java.math.BigDecimal;
import java.util.List;

/**
 * 预付卡充值小票打印基本信息
 */
public class PrepaidChargeOrderSummaryVO {
	private String orderNumber;
    private String shopName;
    private String tradeTime;
    private String operatorName;
    private String address;
    private String contact;
    private Boolean isInitial;
    private String memberTypeName;
    private String mobileNumber;
    private BigDecimal chargeAmount;
    private BigDecimal giftAmount;
    private BigDecimal balance;

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
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

    public String getMemberTypeName() {
        return memberTypeName;
    }

    public void setMemberTypeName(String memberTypeName) {
        this.memberTypeName = memberTypeName;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public BigDecimal getChargeAmount() {
        return chargeAmount;
    }

    public void setChargeAmount(BigDecimal chargeAmount) {
        this.chargeAmount = chargeAmount;
    }

    public BigDecimal getGiftAmount() {
        return giftAmount;
    }

    public void setGiftAmount(BigDecimal giftAmount) {
        this.giftAmount = giftAmount;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public Boolean getIsInitial() {
        return isInitial;
    }

    public void setIsInitial(Boolean isInitial) {
        this.isInitial = isInitial;
    }
}
