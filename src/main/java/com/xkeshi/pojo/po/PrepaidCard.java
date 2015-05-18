package com.xkeshi.pojo.po;

import javax.persistence.Column;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <br>Author: David <br>
 * 2015/1/6.
 */
@Table(name = "prepaid_card")
public class PrepaidCard extends Base {


    @Column(name = "member_id")
    private Long memberId;
    @Column(name = "balance")
    private BigDecimal balance;

    @Column(name = "business_id")
    private Long businessId;
    @Column(name = "business_type_id")
    private Long businessTypeId;

    @Column(name = "password")
    private String password;
    @Column(name = "salt")
    private String salt;
    @Column(name = "created_time")
    private Date createdTime;
    @Column(name = "expire_date")
    private Date expireDate;
    @Column(name = "initial_charge_amount")
    private BigDecimal initialChargeAmount;
    @Column(name = "total_charge_amount")
    private BigDecimal totalChargeAmount;
    @Column(name = "total_charge_times")
    private Integer totalChargeTimes;
    @Column(name = "latest_charge_time")
    private Date latestChargeTime;
    @Column(name = "total_consume_amount")
    private BigDecimal totalConsumeAmount;
    @Column(name = "total_consume_times")
    private Integer totalConsumeTimes;
    @Column(name = "latest_consume_time")
    private Date latestConsumeTime;
    @Column(name = "enable")
    private Boolean enable;
    @Column(name = "initial_rule_id")
    private Long initialRuleId;
    @Column(name = "updated_time")
    private Date updatedTime;

    private String mobileNO;
    private String memberTypeName;

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public Long getBusinessId() {
        return businessId;
    }

    public void setBusinessId(Long businessId) {
        this.businessId = businessId;
    }

    public Long getBusinessTypeId() {
        return businessTypeId;
    }

    public void setBusinessTypeId(Long businessTypeId) {
        this.businessTypeId = businessTypeId;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public Date getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(Date expireDate) {
        this.expireDate = expireDate;
    }

    public BigDecimal getInitialChargeAmount() {
        return initialChargeAmount;
    }

    public void setInitialChargeAmount(BigDecimal initialChargeAmount) {
        this.initialChargeAmount = initialChargeAmount;
    }

    public BigDecimal getTotalChargeAmount() {
        return totalChargeAmount;
    }

    public void setTotalChargeAmount(BigDecimal totalChargeAmount) {
        this.totalChargeAmount = totalChargeAmount;
    }

    public Integer getTotalChargeTimes() {
        return totalChargeTimes;
    }

    public void setTotalChargeTimes(Integer totalChargeTimes) {
        this.totalChargeTimes = totalChargeTimes;
    }

    public Date getLatestChargeTime() {
        return latestChargeTime;
    }

    public void setLatestChargeTime(Date latestChargeTime) {
        this.latestChargeTime = latestChargeTime;
    }

    public BigDecimal getTotalConsumeAmount() {
        return totalConsumeAmount;
    }

    public void setTotalConsumeAmount(BigDecimal totalConsumeAmount) {
        this.totalConsumeAmount = totalConsumeAmount;
    }

    public Integer getTotalConsumeTimes() {
        return totalConsumeTimes;
    }

    public void setTotalConsumeTimes(Integer totalConsumeTimes) {
        this.totalConsumeTimes = totalConsumeTimes;
    }

    public Date getLatestConsumeTime() {
        return latestConsumeTime;
    }

    public void setLatestConsumeTime(Date latestConsumeTime) {
        this.latestConsumeTime = latestConsumeTime;
    }

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public Date getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(Date updatedTime) {
        this.updatedTime = updatedTime;
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

    public Long getInitialRuleId() {
        return initialRuleId;
    }

    public void setInitialRuleId(Long initialRuleId) {
        this.initialRuleId = initialRuleId;
    }
}


