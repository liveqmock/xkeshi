package com.xkeshi.pojo.po;

import java.math.BigDecimal;
import java.util.Date;

/**
 * <br>Author: David <br>
 * 2015/1/6.
 */

public class Merchant {


    private Long id;

    private String fullName;
    private Long balanceId;

    private String shopIds;

    private BigDecimal balance;
    private Boolean memberCentralManagement;
    private Boolean itemCentralManagement;
    private Boolean balanceCentralManagement;
    private Boolean discountCentralManagement;

    private String smsSuffix;

    private String smsChannel;

    private Long avatarId;

    private Boolean visible;

    private Date createdDate;

    private Date modifyDate;

    private Boolean deleted;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Long getBalanceId() {
        return balanceId;
    }

    public void setBalanceId(Long balanceId) {
        this.balanceId = balanceId;
    }

    public String getShopIds() {
        return shopIds;
    }

    public void setShopIds(String shopIds) {
        this.shopIds = shopIds;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public Boolean getMemberCentralManagement() {
        return memberCentralManagement;
    }

    public void setMemberCentralManagement(Boolean memberCentralManagement) {
        this.memberCentralManagement = memberCentralManagement;
    }

    public Boolean getItemCentralManagement() {
        return itemCentralManagement;
    }

    public void setItemCentralManagement(Boolean itemCentralManagement) {
        this.itemCentralManagement = itemCentralManagement;
    }

    public Boolean getBalanceCentralManagement() {
        return balanceCentralManagement;
    }

    public void setBalanceCentralManagement(Boolean balanceCentralManagement) {
        this.balanceCentralManagement = balanceCentralManagement;
    }

    public Boolean getDiscountCentralManagement() {
        return discountCentralManagement;
    }

    public void setDiscountCentralManagement(Boolean discountCentralManagement) {
        this.discountCentralManagement = discountCentralManagement;
    }

    public String getSmsSuffix() {
        return smsSuffix;
    }

    public void setSmsSuffix(String smsSuffix) {
        this.smsSuffix = smsSuffix;
    }

    public String getSmsChannel() {
        return smsChannel;
    }

    public void setSmsChannel(String smsChannel) {
        this.smsChannel = smsChannel;
    }

    public Long getAvatarId() {
        return avatarId;
    }

    public void setAvatarId(Long avatarId) {
        this.avatarId = avatarId;
    }

    public Boolean getVisible() {
        return visible;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getModifyDate() {
        return modifyDate;
    }

    public void setModifyDate(Date modifyDate) {
        this.modifyDate = modifyDate;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }
}
