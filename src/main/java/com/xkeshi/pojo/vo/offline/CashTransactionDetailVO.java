package com.xkeshi.pojo.vo.offline;

import java.math.BigDecimal;

public class CashTransactionDetailVO {
	
	
	private String serial;//支付流水
	
	private BigDecimal amount;//应收的现金
	
	private BigDecimal received;//收到的现金
	
	private BigDecimal returned;//找零

	private Long status;//状态
	
	private String createdTime;//创建时间
	
	private String updatedTime;//修改时间
	
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
	public Long getStatus() {
		return status;
	}
	public void setStatus(Long status) {
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
