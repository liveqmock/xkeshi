package com.alipay.config;
/**
 * 
 * @author xk
 * akeshi网站即时到账支付，支付宝配置
 */
public class XkeshiConfig implements  PaySourceConfig  {
	
	// partner和key提取方法：登陆签约支付宝账户--->点击“商家服务”就可以看到
	private  String partnerID = "2088801674377580"; // 合作身份者ID
	private  String key = "edonnhgqtxbt5v5h18wnnzsdqgkxacyp"; // 安全检验码
	private  String sellerEmail = "dzcm@vip.163.com"; // 签约支付宝账号或卖家收款支付宝帐户
	private  String antiphishing = "0";//防钓鱼功能开关，'0'表示该功能关闭，'1'表示该功能开启。默认为关闭
	//一旦开启，就无法关闭，根据商家自身网站情况请慎重选择是否开启。
	//申请开通方法：联系我们的客户经理或拨打商户服务电话0571-88158090，帮忙申请开通。
	//开启防钓鱼功能后，服务器、本机电脑必须支持远程XML解析，请配置好该环境。
	//若要使用防钓鱼功能，建议使用POST方式请求数据
	private  String charSet = "UTF-8"; // 页面编码
	private  String signType = "MD5"; // 加密方式 不需修改
	private  String transPort = "http";//访问模式,根据自己的服务器是否支持ssl访问，若支持请选择https；若不支持请选择http
	
	// notify_url 交易过程中服务器通知的页面 要用 http://格式的完整路径，不允许加?id=123这类自定义参数
	private  String notifyUrl = "http://vipoffline.xkeshi.com/balance/api/alipayNotify";
	// 付完款后跳转的页面 要用 http://格式的完整路径，不允许加?id=123这类自定义参数
	//页面跳转同步通知页面路径
	private  String callBackUrl = "http://vipoffline.xkeshi.com/balance/api/alipayReturn";
	
	private  String showUrl = "http://www.alipay.com"; // 网站商品的展示地址，不允许加?id=123这类自定义参数
	
	private String refundNotifyUrl = "";     //支付宝退款异步接收地址

    // 支付宝提供给商户的服务接入网关URL(新)
    private static final String ALIPAY_GATEWAY_NEW = "https://mapi.alipay.com/gateway.do?";
    
	@Override
	public String getPartnerID() {
		return partnerID;
	}
	@Override
	public String getKey() {
		return key;
	}
	@Override
	public String getSellerEmail() {
		return sellerEmail;
	}
	@Override
	public String getAntiphishing() {
		return antiphishing;
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
	public String getTransPort() {
		return transPort;
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
		return showUrl;
	}

	@Override
	public String getPrivateKey() {
		return null;
	}

	@Override
	public String getAliPublicKey() {
		return null;
	}
 
	@Override
	public String getAlipayGateway() {
		return ALIPAY_GATEWAY_NEW;
	}

	@Override
	public String getMerchantUrl() {
		return null;
	}
	@Override
	public String getRefundNotifyUrl() {
		 
		return refundNotifyUrl;
	}
	
	private XkeshiConfig(){
	}
	
	private static class XkeshiConfigHolder{
		private static XkeshiConfig instance = new XkeshiConfig();
	}
	
	public static XkeshiConfig getInstance(){
		return XkeshiConfigHolder.instance;
	}
	
}
