package com.xkeshi.pojo.vo.transaction;

import com.xpos.common.entity.Order.Type;

public class POSTransactionVO extends BaseTransactionVO{

	@Override
	public String getTransactionType() {
		return Type.BANKCARD.toString();
	}

	@Override
	public String getTransactionTypeDesc() {
		return Type.BANKCARD.getDesc().toString();
	}

}
