package com.xkeshi.pojo.po;

import javax.persistence.Column;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 第三方订单
 * <p/>
 * Created by david-y on 2015/1/21.
 */
@Table(name = "third_order")
public class ThirdOrder extends Base {

    @Column(name = "third_order_code")
    private String thirdOrderCode;
    @Column(name = "amount")
    private BigDecimal amount;
    @Column(name = "shop_id")
    private Long shopId;

    @Column(name = "shop_name")
    private String shopName;
    @Column(name = "device_number")
    private String deviceNumber;

    @Column(name = "operator_id")
    private Long operatorId;


    @Column(name = "operator_session_code")
    private String operatorSessionCode;
    @Column(name = "consumer_name")
    private String consumerName;
    @Column(name = "consumer_gender")
    private String consumerGender;
    @Column(name = "consumer_mobile_number")
    private String consumerMobileNumber;
    @Column(name = "x_app_code")
    private String xAppCode;
    @Column(name = "x_app_version")
    private String xAppVersion;


    @Column(name = "third_order_payment_status_id")
    private Long thirdOrderPaymentStatusId;
    @Column(name = "created_time")
    private Date createdTime;
    @Column(name = "updated_time")
    private Date updatedTime;

    public String getThirdOrderCode() {
        return thirdOrderCode;
    }

    public void setThirdOrderCode(String thirdOrderCode) {
        this.thirdOrderCode = thirdOrderCode;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Long getShopId() {
        return shopId;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getDeviceNumber() {
        return deviceNumber;
    }

    public void setDeviceNumber(String deviceNumber) {
        this.deviceNumber = deviceNumber;
    }

    public Long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
    }

    public String getOperatorSessionCode() {
        return operatorSessionCode;
    }

    public void setOperatorSessionCode(String operatorSessionCode) {
        this.operatorSessionCode = operatorSessionCode;
    }

    public String getConsumerName() {
        return consumerName;
    }

    public void setConsumerName(String consumerName) {
        this.consumerName = consumerName;
    }

    public String getConsumerGender() {
        return consumerGender;
    }

    public void setConsumerGender(String consumerGender) {
        this.consumerGender = consumerGender;
    }

    public String getConsumerMobileNumber() {
        return consumerMobileNumber;
    }

    public void setConsumerMobileNumber(String consumerMobileNumber) {
        this.consumerMobileNumber = consumerMobileNumber;
    }

    public String getxAppCode() {
        return xAppCode;
    }

    public void setxAppCode(String xAppCode) {
        this.xAppCode = xAppCode;
    }

    public String getxAppVersion() {
        return xAppVersion;
    }

    public void setxAppVersion(String xAppVersion) {
        this.xAppVersion = xAppVersion;
    }

    public Long getThirdOrderPaymentStatusId() {
        return thirdOrderPaymentStatusId;
    }

    public void setThirdOrderPaymentStatusId(Long thirdOrderPaymentStatusId) {
        this.thirdOrderPaymentStatusId = thirdOrderPaymentStatusId;
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
}
