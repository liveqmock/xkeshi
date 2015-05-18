package com.xkeshi.pojo.po;

import javax.persistence.Column;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 实体券订单PO
 * Created by david-y on 2015/1/23.
 */
@Table(name = "physical_coupon_order")
public class PhysicalCouponOrder extends Base {
    @Column(name = "order_number")
    private String orderNumber;
    @Column(name = "third_order_code")
    private String thirdOrderCode;
    @Column(name = "physical_coupon_id")
    private Long physicalCouponId;
    @Column(name = "physical_coupon_name")
    private String physicalCouponName;
    @Column(name = "amount")
    private BigDecimal amount;
    @Column(name = "created_time")
    private Date createdTime;
    @Column(name = "updated_time")
    private Date updatedTime;

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

    public Long getPhysicalCouponId() {
        return physicalCouponId;
    }

    public void setPhysicalCouponId(Long physicalCouponId) {
        this.physicalCouponId = physicalCouponId;
    }

    public String getPhysicalCouponName() {
		return physicalCouponName;
	}

	public void setPhysicalCouponName(String physicalCouponName) {
		this.physicalCouponName = physicalCouponName;
	}

	public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
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
