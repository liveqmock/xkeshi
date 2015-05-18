package com.xpos.common.entity;

import java.math.BigDecimal;

import javax.persistence.Column;

import com.xpos.common.entity.face.Business.BusinessType;
import com.xpos.common.entity.security.Account;

public class BalanceTransaction extends BaseEntity{

	private static final long serialVersionUID = 7827204344581598439L;

	@Column
	private Long businessId;
	
	@Column
	private BusinessType businessType;
	
	@Column
	private Account account;
	
	@Column
	private BigDecimal balance;
	
	@Column
	private BalanceChangeType type;
	
	@Column
	private String description;
	
	@Column
	private BigDecimal amount;

	public enum BalanceChangeType{
		DEDUCT, CHARGE
	}

	public Long getBusinessId() {
		return businessId;
	}

	public void setBusinessId(Long businessId) {
		this.businessId = businessId;
	}

	public BusinessType getBusinessType() {
		return businessType;
	}

	public void setBusinessType(BusinessType businessType) {
		this.businessType = businessType;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	public BalanceChangeType getType() {
		return type;
	}

	public void setType(BalanceChangeType type) {
		this.type = type;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

}
