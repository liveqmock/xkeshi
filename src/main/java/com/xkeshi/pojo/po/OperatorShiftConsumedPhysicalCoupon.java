package com.xkeshi.pojo.po;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Table;

/**
 * @author xk 交接班操作员核销实体券
 */
@Table(name = "operator_shift_consumed_physical_coupon")
public class OperatorShiftConsumedPhysicalCoupon {

	@Column(name = "id")
	private Long id;
	
	/**交接班记录id*/
	@Column(name = "operator_shift_id")
	private Long operatorShiftId;
	
	/** 核销的实体券的id*/
	@Column(name = "physical_coupon_id")
	private Long physicalCouponId;

	/**核销的实体券的名称*/
	@Column(name = "physical_coupon_name")
	private String physicalCouponName;

	/**核销实体券的面额*/
	@Column(name = "physical_coupon_amount")
	private BigDecimal physicalCouponAmount;

	/**核销的总数量*/
	@Column(name = "total_consumed_count")
	private Integer totalConsumedCount;

	@Column(name = "comment")
	private String comment;

	@Column(name = "status")
	private Integer status;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getOperatorShiftId() {
		return operatorShiftId;
	}

	public void setOperatorShiftId(Long operatorShiftId) {
		this.operatorShiftId = operatorShiftId;
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

	public BigDecimal getPhysicalCouponAmount() {
		return physicalCouponAmount;
	}

	public void setPhysicalCouponAmount(BigDecimal physicalCouponAmount) {
		this.physicalCouponAmount = physicalCouponAmount;
	}

	public Integer getTotalConsumedCount() {
		return totalConsumedCount;
	}

	public void setTotalConsumedCount(Integer totalConsumedCount) {
		this.totalConsumedCount = totalConsumedCount;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

}
