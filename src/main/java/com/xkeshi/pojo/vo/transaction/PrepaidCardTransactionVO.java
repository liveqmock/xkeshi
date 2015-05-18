package com.xkeshi.pojo.vo.transaction;

import com.xpos.common.entity.Order.Type;

public class PrepaidCardTransactionVO extends BaseTransactionVO {

	@Override
	public String getTransactionType() {
		return Type.PREPAID.toString();
	}

	@Override
	public String getTransactionTypeDesc() {
		return Type.PREPAID.getDesc().toString();
	}

}
