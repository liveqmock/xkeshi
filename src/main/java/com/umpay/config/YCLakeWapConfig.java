package com.umpay.config;


/**
 * 
 * @author hk
 * 联动优势wap，手机端即时到账支付--签约信息配置
 */
public class YCLakeWapConfig  implements UmpayPaySourceConfig{
	
	//阳澄湖商户号
	private String mer_id = "9362";
	
	//阳澄湖首次支付请求
	private String wapGuidanceUrl="https://m.soopay.net/q/xhtml/index.do";

	//阳澄湖网站返回地址(用户优惠券列表)
	private String yclakeurl="http://yangchenglake.gov.cn/order/rotate?url=/user/coupons?type=AVAILABLE";
	
	//交易成功后回调地址
	private String paymentNotify = "http://vipoffline.xkeshi.com/umpay/api/paymentNotify";
	
	//退款后的结果通知地址
	private String paymentRefundNotify = "http://vipoffline.xkeshi.com/umpay/api/refundNotify";
	
	@Override
	public String getMer_id() {
		return mer_id;
	}
	
	@Override
	public String getWapGuidanceUrl() {
		return wapGuidanceUrl;
	}

	@Override
	public String getYclakeurl() {
		return yclakeurl;
	}
	
	@Override
	public String getPaymentNotify() {
		return paymentNotify;
	}
	
	@Override
	public String getPaymentRefundNotify() {
		return paymentRefundNotify;
	}
	
	 
}
