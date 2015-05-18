package com.xkeshi.pojo.vo.transaction;

import java.math.BigDecimal;

import com.xpos.common.entity.Order.Type;

public class CashTransactionVO extends BaseTransactionVO {
	private BigDecimal received;
	private BigDecimal returned;

	@Override
	public String getTransactionType() {
		return Type.CASH.toString();
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

	@Override
	public String getTransactionTypeDesc() {
		return Type.CASH.getDesc().toString();
	}
	
}
