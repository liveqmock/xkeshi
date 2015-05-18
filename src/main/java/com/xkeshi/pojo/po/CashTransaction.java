package com.xkeshi.pojo.po;

import javax.persistence.Column;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by david-y on 2015/1/23.
 */
@Table(name = "cash_transaction")
public class CashTransaction extends Base {
    @Column(name = "serial")
    private String serial;
    @Column(name = "order_number")
    private String orderNumber;
    @Column(name = "third_order_code")
    private String thirdOrderCode;
    @Column(name = "prepaid_card_charge_order_code")
    private String prepaidCardChargeOrderCode;
    @Column(name = "amount")
    private BigDecimal amount;
    @Column(name = "cash_payment_status_id")
    private Long cashPaymentStatusId;
    @Column(name = "created_time")
    private Date createdTime;
    @Column(name = "updated_time")
    private Date updatedTime;
    @Column(name = "received")
    private BigDecimal received;
    @Column(name = "returned")
    private BigDecimal returned;


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

    public String getPrepaidCardChargeOrderCode() {
        return prepaidCardChargeOrderCode;
    }

    public void setPrepaidCardChargeOrderCode(String prepaidCardChargeOrderCode) {
        this.prepaidCardChargeOrderCode = prepaidCardChargeOrderCode;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Long getCashPaymentStatusId() {
        return cashPaymentStatusId;
    }

    public void setCashPaymentStatusId(Long cashPaymentStatusId) {
        this.cashPaymentStatusId = cashPaymentStatusId;
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

	public BigDecimal getReceived() {
		return received;
	}

	public void setReceived(BigDecimal received) {
		this.received = received;
	}

	public BigDecimal getReturned() {
		return returned;
	}

	public void setReturned(BigDecimal returned) {
		this.returned = returned;
	}
}
