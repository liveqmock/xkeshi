package com.umpay.service;

import com.umpay.config.UmpayPaySourceConfig.UmpayPaySource;
import com.xpos.common.entity.Refund;


/**
 * 
 * @author hk
 *  联动优势wap端退款
 */
public interface UmpaywapRefundService {
	
	
	/**
	 * 提出退款
	 * @param refund
	 * @param paySource
	 * @return
	 */
	public String requestRefund(Refund refund ,UmpayPaySource paySource);
}
