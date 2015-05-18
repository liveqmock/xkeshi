package com.alipay.service;

import com.alipay.config.PaySourceConfig.PaySource;

/**
 * 
 * @author xk
 *  支付宝双担保支付
 */
public interface AlipayDualfunService {
	/**
	 * 支付宝双担保支付生成httpPost  
	 * @param PaySource          支付签约账户信息
	 * @param seller_email     卖家支付宝帐户
	 * @param out_trade_no     商户订单号
	 * @param subject          订单名称
	 * @param price            付款金额
	 * @param quantity         商品数量
	 * @param body             订单描述
	 * @param receive_name     收货人姓名
	 * @param receive_address  收货人地址
	 * @param receive_zip      收货人邮编
	 * @param receive_phone    收货人电话号码
	 * @param receive_mobile   收货人手机号码
	 * @param logistics_fee    物流费用
	 * @param logistics_type   物流类型
	 * @param logistics_payment物流支付方式{两个值可选：SELLER_PAY（卖家承担运费）、BUYER_PAY（买家承担运费）}
	 * @param show_url         商品展示地址
	 * @return
	 */
    public String createHttpPost(
			PaySource paySource  ,
			String seller_email,
			String out_trade_no ,
			String subject  ,
			String price  ,
			String body  ,
			String receive_name,
			String receive_address ,
			String receive_zip,
			String receive_phone ,
			String receive_mobile ,
			String quantity ,
			String logistics_fee  ,
			String logistics_type ,
			String logistics_payment,
			String show_url  
		) throws Exception;
}
