package com.alipay.config;

/* *
    支付宝双保险支付--签约信息配置
 */
public class DualfunConfig  implements PaySourceConfig   {
	
	// 合作身份者ID，以2088开头由16位纯数字组成的字符串
	private  String partnerId = "";
	// 商户的私钥
	private  String key = "";

	// 字符编码格式 目前支持 gbk 或 utf-8
	private  String charSet = "utf-8";
	
	// 签名方式 不需修改
	private  String signType = "MD5";

	//服务器异步通知页面路径
	private  String notifyUrl = "";

	//页面跳转同步通知页面路径
	private  String callBackUrl = "";
 
	//操作中断返回地址
	private  String merchantUrl = "http://v2.xkeshi.com";
	
	//收款人账户
	private String	seller_email = "";
	
	private  String ALIPAY_GATEWAY_NEW = "https://mapi.alipay.com/gateway.do?";
		
	
	@Override
	public String getPartnerID() {
		// TODO Auto-generated method stub
		return partnerId;
	}

	@Override
	public String getKey() {
		// TODO Auto-generated method stub
		return key;
	}

	@Override
	public String getPrivateKey() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getAliPublicKey() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getSellerEmail() {
		return seller_email;
	}

	@Override
	public String getAntiphishing() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getCharSet() {
		// TODO Auto-generated method stub
		return charSet;
	}

	@Override
	public String getSignType() {
		// TODO Auto-generated method stub
		return signType;
	}

	@Override
	public String getNotifyUrl() {
		return notifyUrl;
	}

	@Override
	public String getCallBackUrl() {
		return callBackUrl;
	}

	@Override
	public String getShowUrl() {
		return null;
	}

	@Override
	public String getMerchantUrl() {
		return merchantUrl;
	}

	@Override
	public String getTransPort() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getAlipayGateway() {
		return ALIPAY_GATEWAY_NEW;
	}

	@Override
	public String getRefundNotifyUrl() {
		// TODO Auto-generated method stub
		return null;
	}

	private DualfunConfig(){
	}
	
	private static class DualfunConfigHolder{
		private static DualfunConfig instance = new DualfunConfig();
	}
	
	public static DualfunConfig getInstance(){
		return DualfunConfigHolder.instance;
	}

}
