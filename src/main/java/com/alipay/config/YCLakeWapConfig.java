package com.alipay.config;
/**
 * 
 * @author xk
 * 支付宝wap，手机端即时到账支付--签约信息配置
 */
public class YCLakeWapConfig  implements PaySourceConfig{
	
	// 合作身份者ID，以2088开头由16位纯数字组成的字符串
	private  String partnerID = "2088511325420812";
	
	// 交易安全检验码，由数字和字母组成的32位字符串
	private  String key = "qq0ox4e0i85j72w649vfqar7x146pej0";// 如果签名方式设置为“MD5”时，请设置该参数
	
    // 商户的私钥
	private  String privateKey = "";// 如果签名方式设置为“0001”时，请设置该参数

    // 支付宝的公钥
	private  String aliPublicKey = ""; // 如果签名方式设置为“0001”时，请设置该参数

	//卖家支付宝帐户
	private  String sellerEmail = "szychdjq@sina.com" ;

	// 字符编码格式 目前支持  utf-8
	private  String charSet = "utf-8";
	
	// 签名方式，选择项：0001(RSA)、MD5
	private  String signType = "MD5";// 无线的产品中，签名方式为rsa时，sign_type需赋值为0001而不是RSA
	
	//服务器异步通知页面路径
	private  String notifyUrl = "http://vipoffline.xkeshi.com/balance/api/wapNotifyYCLake";//"http://makesecretalipay.nat123.net/balance/api/wapNotifyYCLake";

	//页面跳转同步通知页面路径
	private  String callBackUrl = "http://vipoffline.xkeshi.com/balance/api/callBackYCLake";//"http://makesecretalipay.nat123.net/balance/api/callBackYCLake";

	//操作中断返回地址
	private  String merchantUrl = "http://yangchenglake.gov.cn";//"http://yangchenglake.nat123.net";

	//支付宝网关地址
	private  String ALIPAY_GATEWAY_NEW = "http://wappaygw.alipay.com/service/rest.htm?";
   
	//支付宝退款接口服务器异步接收通知地址
	private String refundNotifyUrl  = "http://vipoffline.xkeshi.com/balance/api/refwapntfyclake";//"http://makesecretalipay.nat123.net/balance/api/refwapntfyclake" ;
	
	
	@Override
	public String getPartnerID() {
		return partnerID;
	}
	@Override
	public String getKey() {
		return key;
	}
	@Override
	public String getPrivateKey() {
		return privateKey;
	}
	@Override
	public String getAliPublicKey() {
		return aliPublicKey;
	}
	@Override
	public String getSellerEmail() {
		return sellerEmail;
	}
	@Override
	public String getCharSet() {
		return charSet;
	}
	@Override
	public String getSignType() {
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
	public String getMerchantUrl() {
		return merchantUrl;
	}

	@Override
	public String getAntiphishing() {
		return null;
	}

	@Override
	public String getShowUrl() {
		return null;
	}

	@Override
	public String getAlipayGateway() {
		return ALIPAY_GATEWAY_NEW;
	}

	@Override
	public String getTransPort() {
		return null;
	}
	@Override
	public String getRefundNotifyUrl() {
		return refundNotifyUrl;
	}
	 
	private YCLakeWapConfig(){
	}
	
	private static class YCLakeWapConfigHolder{
		private static YCLakeWapConfig instance = new YCLakeWapConfig();
	}
	
	public static YCLakeWapConfig getInstance(){
		return YCLakeWapConfigHolder.instance;
	}
}
