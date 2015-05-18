package com.xkeshi.pojo.vo.transaction;

import java.math.BigDecimal;

public abstract class BaseTransactionVO {
   
	private BigDecimal amount ;
	
	public  abstract String   getTransactionType ();	

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	
	public abstract String getTransactionTypeDesc();
	
}
