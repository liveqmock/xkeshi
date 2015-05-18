package com.umpay.config;

import com.umpay.config.UmpayPaySourceConfig.UmpayPaySource;

/**
 * 
 * @author hk
 * 联动优势支付签约信息配置工厂
 */
public class FactoryConfig {
	
  private static UmpayPaySourceConfig  payConfig  = null;
	
  private FactoryConfig () {
	  
  }
  
  public static UmpayPaySourceConfig getPayConfig(UmpayPaySource enumPay){
	  if (enumPay == null) {
		  return null;
	  }
	  // 阳澄湖WAP即时到账支付
	  if (UmpayPaySource.YCLAKE_WAP_U.equals(enumPay)) {
		  if (payConfig == null) {
			  payConfig = new YCLakeWapConfig();
		}
	  }
	  
	  
	  return payConfig;
  }
}
