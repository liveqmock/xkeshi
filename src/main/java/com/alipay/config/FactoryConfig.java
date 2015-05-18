package com.alipay.config;

import com.alipay.config.PaySourceConfig.PaySource;

/**
 * 
 * @author xk
 * 支付宝支付签约信息配置工厂
 */
public class FactoryConfig {
	
  private static PaySourceConfig  payConfig  = null;
	
  private FactoryConfig () {
	  
  }
  
  public static PaySourceConfig getPayConfig(PaySource enumPay){
	  if (enumPay == null) {
		  return null;
	  }
	  //爱客仕即时到账支付
	  if (PaySource.XKESHI_ALIPAY_DIRECT.equals(enumPay)) {
		  payConfig = XkeshiConfig.getInstance();
	  }
	  // 阳澄湖WAP即时到账支付
	  if (PaySource.YCLAKE_WAP_DIRECT.equals(enumPay)) {
		  payConfig = YCLakeWapConfig.getInstance();
	  }
	  // 双担保支付
	  if (PaySource.DUALFUN.equals(enumPay)) {
		  payConfig = DualfunConfig.getInstance();
	  }
	  
	  
	  return payConfig;
  }
}
