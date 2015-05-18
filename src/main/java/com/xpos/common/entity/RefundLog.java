package com.xpos.common.entity;

import java.util.Date;

import javax.persistence.Column;

import com.xpos.common.entity.Refund.RefundStatus;
import com.xpos.common.entity.face.EncryptId;

/**
 * 优惠券退款记录
 */
public class RefundLog extends BaseEntity implements EncryptId{

	private static final long serialVersionUID = -498984985576306594L;
	
	@Column
	private Refund refund;
	@Column
	private String description;
	@Column
	private RefundStatus status; //退款状态
	
	private Date showCreateDate; // 显示创建时间
	
	public Refund getRefund() {
		return refund;
	}
	public void setRefund(Refund refund) {
		this.refund = refund;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public RefundStatus getStatus() {
		return status;
	}
	public void setStatus(RefundStatus status) {
		this.status = status;
	}
	public Date getShowCreateDate() {
		return getCreateDate();
	}
	
}
