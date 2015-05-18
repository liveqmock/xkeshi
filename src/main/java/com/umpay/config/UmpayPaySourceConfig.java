package com.umpay.config;


public interface UmpayPaySourceConfig {
	
	/** 支付来源  */
	public enum UmpayPaySource{
		/**阳澄湖WAP即时到账*/
		YCLAKE_WAP_U;
	}
	
	public String getMer_id() ;
	
	public String getWapGuidanceUrl() ;

	public String getYclakeurl() ;

	public String getPaymentNotify();
	
	public String getPaymentRefundNotify();
	
}
