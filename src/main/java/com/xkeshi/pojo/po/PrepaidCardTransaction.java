package com.xkeshi.pojo.po;

import javax.persistence.Column;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * 预付卡支付PO
 *
 * Created by david-y on 2015/1/24.
 */
@Table(name = "prepaid_card_transaction")
public class PrepaidCardTransaction extends Base {
    @Column(name = "prepaid_card_id")
    private Long prepaidCardId;
    @Column(name = "order_number")
    private String orderNumber;
    @Column(name = "serial")
    private String serial;
    @Column(name = "third_order_code")
    private String thirdOrderCode;
    @Column(name = "amount")
    private BigDecimal amount;
    @Column(name = "prepaid_card_payment_status_id")
    private Long prepaidCardPaymentStatusId;
    @Column(name = "created_time")
    private Date createdTime;
    @Column(name = "updated_time")
    private Date updatedTime;

    public Long getPrepaidCardId() {
        return prepaidCardId;
    }

    public void setPrepaidCardId(Long prepaidCardId) {
        this.prepaidCardId = prepaidCardId;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

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

    public Long getPrepaidCardPaymentStatusId() {
        return prepaidCardPaymentStatusId;
    }

    public void setPrepaidCardPaymentStatusId(Long prepaidCardPaymentStatusId) {
        this.prepaidCardPaymentStatusId = prepaidCardPaymentStatusId;
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

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }
}
