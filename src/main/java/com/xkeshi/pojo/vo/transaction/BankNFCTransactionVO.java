package com.xkeshi.pojo.vo.transaction;

import com.xpos.common.entity.Order.Type;

public class BankNFCTransactionVO extends BaseTransactionVO {

	@Override
	public String getTransactionType() {
		return Type.BANK_NFC_CARD.toString();
	}

	@Override
	public String getTransactionTypeDesc() {
		
		return Type.BANK_NFC_CARD.getDesc().toString();
	}

}
