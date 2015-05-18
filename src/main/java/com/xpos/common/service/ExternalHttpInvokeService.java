package com.xpos.common.service;

import java.util.Map;

import com.xpos.common.entity.Refund;
import com.xpos.common.entity.pos.POSTransaction;

/**
 * 通过HTTP协议，调用外部系统的接口
 * @author chengj
 *
 */
public interface ExternalHttpInvokeService {

	/** 移动电子券接口文档：UMPAY SW 浙江电子券平台商户接入规范 V 0_1_2.doc，接口5.1【商户无磁有密支付】  */
	POSTransaction createCMCCTicketOrder(POSTransaction transaction, String uid, String deviceNumber) throws Exception;

	/** 移动电子券接口文档：UMPAY SW 浙江电子券平台商户接入规范 V_0_1_2.doc，接口5.5【无磁有密支付个人协议增加】 */
	String signTicketPaymentAgreement(String mobile) throws Exception;

	/** 移动电子券接口文档：UMPAY SW 浙江电子券平台商户接入规范 V_0_1_2.doc，接口5.7【余额查询】 */
	int getTicketBalanceByPhone(String mobile) throws Exception;

	/** 移动电子券接口文档：UMPAY SW 浙江电子券平台商户接入规范 V_0_1_2.doc，接口5.4【商户无磁有密/无密支付冲正】 */
	String cancelCMCCTicketOrder(POSTransaction transaction) throws Exception;

	/** 盛付通接口文档：盛付通MNPAPI开发指南， 接口7.2 【查询交易接口】  */
	String queryShengPayOrderDetail(POSTransaction transaction, String type) throws Exception;

	/** 微信支付退款及对账开发指南.pdf，【退款明细查询接口】 */
	Map<String, String> queryTenpayRefundDetail(Refund refund);

	/** 微信支付退款及对账开发指南.pdf，【退款接口】 */
	Map<String, String> executeTenpayRefund(Refund refund);

}
