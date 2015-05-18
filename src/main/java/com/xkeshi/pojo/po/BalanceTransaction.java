package com.xkeshi.pojo.po;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Table;

/**
 * 
 * @author xk
 * @createDate 2015-02-28
 * @description 资金账户流水
 */
@Table(name="balancetransaction")
public class BalanceTransaction {

	@Column(name = "id")
	private  Long id  ; 
	
	/**账户余额*/
	@Column(name="balance")
	private BigDecimal balance;
	
	/**后台操作资金的账户*/
	@Column(name = "account_id")
	private Long accountId; 
	
	@Column(name="businessId")
	private Long businessId;
	
	@Column(name="businessType")
	private String businessType;
	
	/** 资金类型 DEDUCT(消费), INCREASE(充值)*/
	@Column(name="type")
	private String type;
	
	@Column(name="description")
	private String description;
	
	/**金额(消费为负数,充值为正数)*/
	@Column
	private BigDecimal amount;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	public Long getBusinessId() {
		return businessId;
	}

	public void setBusinessId(Long businessId) {
		this.businessId = businessId;
	}

	public String getBusinessType() {
		return businessType;
	}

	public void setBusinessType(String businessType) {
		this.businessType = businessType;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
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

	public Long getAccountId() {
		return accountId;
	}

	public void setAccountId(Long accountId) {
		this.accountId = accountId;
	}

 

}
