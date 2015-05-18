package com.xkeshi.pojo.vo;

import com.xkeshi.pojo.meta.MetaPrepaidCardChargeChannel;

/**
 * 预付卡充值记录列表VO
 *
 * Created by david-y on 2015/1/30.
 */
public class PrepaidCardChargeListVO {
    private Long id;
    private String createTime;
    private Integer initialCharge;
    private String mobileNumber;
    private String chargeAmount;
    private MetaPrepaidCardChargeChannel chargeChannel;
    private Long chargeChannelId;
    private String chargeRuleName;
    private String code;
    private String operatorName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public Integer getInitialCharge() {
        return initialCharge;
    }

    public void setInitialCharge(Integer initialCharge) {
        this.initialCharge = initialCharge;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getChargeAmount() {
        return chargeAmount;
    }

    public void setChargeAmount(String chargeAmount) {
        this.chargeAmount = chargeAmount;
    }

    public MetaPrepaidCardChargeChannel getChargeChannel() {
        return chargeChannel;
    }

    public void setChargeChannel(MetaPrepaidCardChargeChannel chargeChannel) {
        this.chargeChannel = chargeChannel;
    }

    public String getChargeRuleName() {
        return chargeRuleName;
    }

    public void setChargeRuleName(String chargeRuleName) {
        this.chargeRuleName = chargeRuleName;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public Long getChargeChannelId() {
        return chargeChannelId;
    }

    public void setChargeChannelId(Long chargeChannelId) {
        this.chargeChannelId = chargeChannelId;
    }
}
