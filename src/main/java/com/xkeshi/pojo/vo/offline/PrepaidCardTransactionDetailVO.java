package com.xkeshi.pojo.vo.offline;

import java.math.BigDecimal;

public class PrepaidCardTransactionDetailVO {
	
	private Long memberId;
	
	private String serial;//支付流水
	
	private BigDecimal amount;//应收的现金
	
	private int status;//状态
	
	private String createdTime;//创建时间
	
	private String updatedTime;//修改时间
	
	
	public Long getMemberId() {
		return memberId;
	}
	public void setMemberId(Long memberId) {
		this.memberId = memberId;
	}
	public String getSerial() {
		return serial;
	}
	public void setSerial(String serial) {
		this.serial = serial;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getCreatedTime() {
		return createdTime;
	}
	public void setCreatedTime(String createdTime) {
		this.createdTime = createdTime;
	}
	public String getUpdatedTime() {
		return updatedTime;
	}
	public void setUpdatedTime(String updatedTime) {
		this.updatedTime = updatedTime;
	}
}
