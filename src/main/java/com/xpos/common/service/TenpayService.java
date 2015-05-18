package com.xpos.common.service;

import com.xpos.common.entity.Refund;

public interface TenpayService {
	
	/** 查询指定订单退款详情 */
	Refund queryRefundDetail(Refund refund);

	/** 执行退款操作 */
	boolean refund(Refund refund);
	
}
