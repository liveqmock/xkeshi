package com.xkeshi.pojo.po;

import javax.persistence.Column;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 预付卡充值订单（记录）
 *
 * Created by david-y on 2015/1/21.
 */
@Table(name = "prepaid_card_charge_order")
public class PrepaidCardChargeOrder extends Base {

    @Column(name = "code")
    private String code;
    @Column(name = "shop_id")
    private Long shopId;
    @Column(name = "is_initial")
    private Boolean isInitial;
    @Column(name = "total_amount")
    private BigDecimal totalAmount;
    @Column(name = "actual_amount")
    private BigDecimal actualAmount;
    @Column(name = "operator_id")
    private Long operatorId;
    @Column(name = "prepaid_card_id")
    private Long prepaidCardId;
    @Column(name = "member_id")
    private Long memberId;
    @Column(name = "operator_session_code")
    private String operatorSessionCode;
    @Column(name = "charge_status_id")
    private Long chargeStatusId;
    @Column(name = "charge_channel_id")
    private Long chargeChannelId;
    @Column(name = "created_time")
    private Date createdTime;
    @Column(name = "updated_time")
    private Date updatedTime;


    private Long memberTypeId;//会员类型ID
    private String chargeChannelStr;//支付通道
    private String chargeStatusStr;//支付状态
    private String operatorName;//操作员名称
    private String mobileNumber;//手机号
    private BigDecimal giftAmount;//赠送金额



    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Long getShopId() {
        return shopId;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getActualAmount() {
        return actualAmount;
    }

    public void setActualAmount(BigDecimal actualAmount) {
        this.actualAmount = actualAmount;
    }

    public Long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
    }

    public Long getPrepaidCardId() {
        return prepaidCardId;
    }

    public void setPrepaidCardId(Long prepaidCardId) {
        this.prepaidCardId = prepaidCardId;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public String getOperatorSessionCode() {
        return operatorSessionCode;
    }

    public void setOperatorSessionCode(String operatorSessionCode) {
        this.operatorSessionCode = operatorSessionCode;
    }

    public Long getChargeStatusId() {
        return chargeStatusId;
    }

    public void setChargeStatusId(Long chargeStatusId) {
        this.chargeStatusId = chargeStatusId;
    }

    public Long getChargeChannelId() {
        return chargeChannelId;
    }

    public void setChargeChannelId(Long chargeChannelId) {
        this.chargeChannelId = chargeChannelId;
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

    public String getChargeChannelStr() {
        return chargeChannelStr;
    }

    public void setChargeChannelStr(String chargeChannelStr) {
        this.chargeChannelStr = chargeChannelStr;
    }

    public String getChargeStatusStr() {
        return chargeStatusStr;
    }

    public void setChargeStatusStr(String chargeStatusStr) {
        this.chargeStatusStr = chargeStatusStr;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public BigDecimal getGiftAmount() {
        return giftAmount;
    }

    public void setGiftAmount(BigDecimal giftAmount) {
        this.giftAmount = giftAmount;
    }

    public Boolean getIsInitial() {
        return isInitial;
    }

    public void setIsInitial(Boolean isInitial) {
        this.isInitial = isInitial;
    }

    public Long getMemberTypeId() {
        return memberTypeId;
    }

    public void setMemberTypeId(Long memberTypeId) {
        this.memberTypeId = memberTypeId;
    }
}
