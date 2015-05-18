package com.alipay.service;

import com.alipay.entity.QRCodePaymentNotify;
import com.xpos.common.entity.Shop;
import com.xpos.common.entity.pos.POSTransaction;

/**
 * 支付宝线下条码/二维码支付
 * 为兼容老接口暂时保留
 */
@Deprecated
public interface AlipayQRCodePaymentService {
	/**
	 * 功能：构造URL并调用接口创建支付宝订单
	 * @param shop  //收单商户
	 * @param dynamicId //动态条码、二维码
	 * @param signKey //收单支付宝校验码
	 * @param transaction //爱客仕系统订单
	 * @return response 响应内容
	 * @throws Exception
	 */
	public String submitAndPay(Shop shop, String dynamicId, String signKey, POSTransaction transaction) throws Exception;

	/** 处理统一下单支付请求的同步返回结果 */
	public POSTransaction processSubmitAndPayCallback(String responseText, String signKey, POSTransaction posTransaction) throws Exception;
	
	/** 处理统一下单支付请求的异步返回通知 */
	public boolean processSubmitAndPayNotify(QRCodePaymentNotify notify, String body);

	/** 发起查询指定订单的请求
	 * @return response响应内容
	 */
	public String query(String signKey, POSTransaction transaction) throws Exception;

	/** 处理订单查询请求的同步返回结果 */
	public String processQueryCallback(String responseText, String signKey, POSTransaction transaction) throws Exception;

	/** 发起撤销指定订单的请求 */
	public String cancel(String signKey, POSTransaction transaction) throws Exception;

	/** 处理订单撤销请求的同步返回结果 */
	public String processCancelCallback(String responseText, String signKey, POSTransaction transaction) throws Exception;

	/** 发起指定订单的退款请求 */
	public String refund(String signKey, POSTransaction transaction) throws Exception;

	/** 处理订单退款请求的同步返回结果 */
	public String processRefundCallback(String responseText, String signKey, POSTransaction transaction) throws Exception;
	
}
