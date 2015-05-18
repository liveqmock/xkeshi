package com.alipay.config;

public interface PaySourceConfig {
	
	/** 支付来源  */
	public enum PaySource{
		/**爱客仕即时到账*/
		XKESHI_ALIPAY_DIRECT,
		/**阳澄湖WAP即时到账*/
		YCLAKE_WAP_DIRECT,
		/**双担保支付*/
		DUALFUN ;
	}

	
	/**合作身份者ID，以2088开头由16位纯数字组成的字符串*/
	public String getPartnerID() ;
	
	/** 交易安全检验码，由数字和字母组成的32位字符串*/
	public String getKey() ;
	
    /** 如果签名方式设置为“0001”时，请设置该参数*/
	public String getPrivateKey() ;
	
    /**支付宝的公钥 如果签名方式设置为“0001”时，请设置该参数*/
	public String getAliPublicKey();
	
	/** 签约支付宝账号或卖家收款支付宝帐户*/
	public String getSellerEmail(); 
	
	/**防钓鱼功能开关，'0'表示该功能关闭，'1'表示该功能开启。默认为关闭*/
	public String getAntiphishing() ;
	
	/** 字符编码格式 utf-8*/
	public String getCharSet() ;
	
	/** 签名方式，选择项：0001(RSA)、MD5*/
	public String getSignType() ;
	
	/** notify_url 交易过程中服务器通知的页面 要用 http:/**格式的完整路径，不允许加?id=123这类自定义参数*/
	public String getNotifyUrl() ;
	
	/** 页面跳转同步通知页面路径付完款后跳转的页面 要用 http:/**格式的完整路径，不允许加?id=123这类自定义参数*/
	public String getCallBackUrl();
	
	/** 网站商品的展示地址，不允许加?id=123这类自定义参数*/
	public String getShowUrl() ;
	
	/**用户付款中途退出返回商户的地址。需http://格式的完整路径，不允许加?id=123这类自定义参数*/
	public String getMerchantUrl();
	
	/**有密退款异步通知地址*/
	public String getRefundNotifyUrl();
	
	/**访问模式,根据自己的服务器是否支持ssl访问，若支持请选择https；若不支持请选择http*/
	public String getTransPort();
	
	/**支付宝网关地址*/
	public String getAlipayGateway() ;
	
}
