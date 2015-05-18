package com.xkeshi.pojo.vo;

/**
 * <br>Author: David <br>
 * 2015/1/6.
 */
public class PrepaidCardListVO {

    private Long id;
    private String mobileNO;
    private String memberTypeName;
    private String createdTime;
    private Integer totalChargeTimes;
    private String totalChargeAmount;
    private Integer totalConsumeTimes;
    private String latestConsumeTime;
    private String balance;
    private Boolean enable;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMobileNO() {
        return mobileNO;
    }

    public void setMobileNO(String mobileNO) {
        this.mobileNO = mobileNO;
    }

    public String getMemberTypeName() {
        return memberTypeName;
    }

    public void setMemberTypeName(String memberTypeName) {
        this.memberTypeName = memberTypeName;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }

    public Integer getTotalChargeTimes() {
        return totalChargeTimes;
    }

    public void setTotalChargeTimes(Integer totalChargeTimes) {
        this.totalChargeTimes = totalChargeTimes;
    }

    public String getTotalChargeAmount() {
        return totalChargeAmount;
    }

    public void setTotalChargeAmount(String totalChargeAmount) {
        this.totalChargeAmount = totalChargeAmount;
    }

    public Integer getTotalConsumeTimes() {
        return totalConsumeTimes;
    }

    public void setTotalConsumeTimes(Integer totalConsumeTimes) {
        this.totalConsumeTimes = totalConsumeTimes;
    }

    public String getLatestConsumeTime() {
        return latestConsumeTime;
    }

    public void setLatestConsumeTime(String latestConsumeTime) {
        this.latestConsumeTime = latestConsumeTime;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }
}
