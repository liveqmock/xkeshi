package com.xkeshi.pojo.po;

import javax.persistence.Column;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 订单会员折扣
 *
 * Created by david-y on 2015/1/23.
 */
@Table(name = "order_member_discount")
public class OrderMemberDiscount extends Base  {

    @Column(name = "order_number")
    private String orderNumber;
    @Column(name = "member_type_id")
    private Long memberTypeId;
    @Column(name = "member_id")
    private Long memberId;
    @Column(name = "business_type")
    private String businessType;
    @Column(name = "discount")
    private BigDecimal discount;
    @Column(name = "created_time")
    private Date createdTime;
    @Column(name = "updated_time")
    private Date updatedTime;
    
    public Long getMemberId() {
		return memberId;
	}

	public void setMemberId(Long memberId) {
		this.memberId = memberId;
	}

	public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public Long getMemberTypeId() {
        return memberTypeId;
    }

    public void setMemberTypeId(Long memberTypeId) {
        this.memberTypeId = memberTypeId;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
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
