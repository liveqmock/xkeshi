package com.xkeshi.pojo.vo;

/**
 * 预付卡基本信息
 * Created by david-y on 2015/1/29.
 */
public class PrepaidCardVO {
    private Long id;
    private String mobileNumber;
    private String memberTypeName;
    private String discount;
    private String balance;
    private String createdDate;
    private String initialChargeAmount;
    private Integer totalChargeTimes;
    private String latestConsumeTime;
    private Boolean enable;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMemberTypeName() {
        return memberTypeName;
    }

    public void setMemberTypeName(String memberTypeName) {
        this.memberTypeName = memberTypeName;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getInitialChargeAmount() {
        return initialChargeAmount;
    }

    public void setInitialChargeAmount(String initialChargeAmount) {
        this.initialChargeAmount = initialChargeAmount;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public Integer getTotalChargeTimes() {
        return totalChargeTimes;
    }

    public void setTotalChargeTimes(Integer totalChargeTimes) {
        this.totalChargeTimes = totalChargeTimes;
    }

    public String getLatestConsumeTime() {
        return latestConsumeTime;
    }

    public void setLatestConsumeTime(String latestConsumeTime) {
        this.latestConsumeTime = latestConsumeTime;
    }
}
