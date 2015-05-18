package com.xkeshi.pojo.vo.transaction;

import com.xpos.common.entity.Order.Type;

public class WXPayTransactionVO extends BaseTransactionVO {

	@Override
	public String getTransactionType() {
		return Type.WXPAY_QRCODE.toString();
	}

	@Override
	public String getTransactionTypeDesc() {
		return Type.WXPAY_QRCODE.getDesc().toString();
	}

}
