package com.xkeshi.pojo.vo.transaction;

import com.xpos.common.entity.Order.Type;

public class AlipayTransactionVO extends BaseTransactionVO {

	@Override
	public String getTransactionType() {
		return Type.ALIPAY_QRCODE.toString();
	}
	
	@Override
	public String getTransactionTypeDesc() {
		return Type.ALIPAY_QRCODE.getDesc().toString();
	}

}
