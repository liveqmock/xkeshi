package com.umpay.service;

import com.umpay.config.UmpayPaySourceConfig;
import com.umpay.config.UmpayPaySourceConfig.UmpayPaySource;


/**
 * 
 * @author hk
 *  联动优势wap端支付
 */
public interface UmpaywapService {
	
	
	/**
	 * 获取Api下单
	 * @throws Exception 
	 */
	public String getUMPAYTradeNo(Long paymentid,UmpayPaySource paySource) throws Exception;
	
	
	/**
	 * 获取联动优势首次支付的Url
	 */
	public String getFirstUMPAYUrl(Long paymentid, String tradeNo,UmpayPaySource paySource);

	
	/**
	 * 协议支付发送短信
	 * @param tradeNo
	 * @param userId
	 * @param usrpayagreementId
	 * @param paySource
	 * @return
	 * @throws Exception 
	 */
	public boolean sendAgreementMessage(String tradeNo, String userId,
			String usrpayagreementId, UmpayPaySource paySource) throws Exception;


	/**
	 * 协议支付确认支付
	 * @param tradeNo
	 * @param userId
	 * @param verify_code
	 * @param usrpayagreementId
	 * @param paySource
	 * @return
	 * @throws Exception
	 */
	public String generateUMPAYAgreement(String tradeNo, String userId,
			String verify_code, String usrpayagreementId, UmpayPaySource paySource)throws Exception;


	/**
	 * 解绑银行卡
	 * @param mer_cust_id
	 * @param usrpayagreementId
	 * @param paySource
	 * @return
	 */
	public String unbindMercust(String mer_cust_id ,String usrpayagreementId, UmpayPaySource paySource) throws Exception ;
}
