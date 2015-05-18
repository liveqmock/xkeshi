package com.xkeshi.pojo.po;

import com.xpos.common.entity.face.EncryptId;
import com.xpos.common.utils.IDUtil;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by Administrator on 2015/3/11.
 */
public class AlipayTransactionDetail implements EncryptId {

    private Long id;
    private BigDecimal amount;
    private String serial;
    private String alipaySerial;
    private String buyerId;
    private String sellerAccount;
    private String deviceNumber;
    private String responseCode;
    private Date tradeTime;
    private Date createdTime;
    private Date updatedTime;
    private String statusCode;
    private String statusName;
    private String memberName;
    private String memberMobile;
    private String operatorUserName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOperatorUserName() {
        return operatorUserName;
    }

    public void setOperatorUserName(String operatorUserName) {
        this.operatorUserName = operatorUserName;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
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

    public String getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(String buyerId) {
        this.buyerId = buyerId;
    }

    public String getSellerAccount() {
        return sellerAccount;
    }

    public void setSellerAccount(String sellerAccount) {
        this.sellerAccount = sellerAccount;
    }

    public String getDeviceNumber() {
        return deviceNumber;
    }

    public void setDeviceNumber(String deviceNumber) {
        this.deviceNumber = deviceNumber;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public Date getTradeTime() {
        return tradeTime;
    }

    public void setTradeTime(Date tradeTime) {
        this.tradeTime = tradeTime;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public Date getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(Date updatedTime) {
        this.updatedTime = updatedTime;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public String getMemberMobile() {
        return memberMobile;
    }

    public void setMemberMobile(String memberMobile) {
        this.memberMobile = memberMobile;
    }

    public String getEid(){
        if(this instanceof EncryptId){
            return getId() != null ? IDUtil.encode(getId()) : null;
        }else
            return String.valueOf(getId());
    }
}
