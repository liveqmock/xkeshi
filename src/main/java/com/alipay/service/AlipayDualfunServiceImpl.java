package com.alipay.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.alipay.config.FactoryConfig;
import com.alipay.config.PaySourceConfig;
import com.alipay.config.PaySourceConfig.PaySource;
import com.alipay.util.AlipaySubmit;

@Service
public class AlipayDualfunServiceImpl implements AlipayDualfunService{
    
	
	@Override
	public String createHttpPost (
				PaySource paySource  ,     //支付签约账户信息
				String  seller_email,  //卖家支付宝帐户
				String  out_trade_no , //商户订单号
				String  subject  ,     //订单名称
				String  price  ,       //付款金额
				String  quantity ,     //商品数量
				String  body  ,        //订单描述
				String  receive_name,  //收货人姓名
				String  receive_address,//收货人地址
				String  receive_zip,    //收货人邮编
				String  receive_phone , //收货人电话号码
				String  receive_mobile ,//收货人手机号码
				String  logistics_fee  , //物流费用
				String  logistics_type , //物流类型
				String  logistics_payment,//物流支付方式{两个值可选：SELLER_PAY（卖家承担运费）、BUYER_PAY（买家承担运费）}
				String   show_url        //商品展示地址
			) throws Exception {
		    paySource = paySource ==null ? PaySource.DUALFUN : paySource;
            PaySourceConfig payConfig = FactoryConfig.getPayConfig(paySource);
	        //支付类型
			String payment_type = "1";
			seller_email  = seller_email == null ? payConfig.getSellerEmail() : seller_email ;
			show_url  = show_url == null ? payConfig.getShowUrl() : show_url;
			/*  out_trade_no = new String("ychtest001")+new Date();
			  subject = new String("测试双担保接口支付");
			  price = new String("0.01");
			  quantity = "1";
			  logistics_fee = "0.00";
			  logistics_type = "EXPRESS";
			  logistics_payment = "SELLER_PAY";
			  body = new String("订单描述");
			  show_url = new String("http://www.xkeshi.com/shop/110616");
			  receive_name = new String("xukai");
			  receive_address = new String("浙江省XXX市XXX区XXX路XXX小区XXX栋XXX单元XXX号");
			  receive_zip = new String("311715");
			  receive_phone = new String("0571-88158090");
			  receive_mobile = new String("18768011400");*/
			//把请求参数打包成数组
			Map<String, String> sParaTemp = new HashMap<String, String>();
			sParaTemp.put("service", "trade_create_by_buyer");
	        sParaTemp.put("partner", payConfig.getPartnerID());
	        sParaTemp.put("_input_charset", payConfig.getCharSet());
			sParaTemp.put("payment_type", payment_type);
			sParaTemp.put("notify_url", payConfig.getNotifyUrl());
			sParaTemp.put("return_url", payConfig.getCallBackUrl());
			sParaTemp.put("seller_email", seller_email);
			sParaTemp.put("out_trade_no", out_trade_no);
			sParaTemp.put("subject", subject);
			sParaTemp.put("price", price);
			sParaTemp.put("quantity", quantity);
			sParaTemp.put("logistics_fee", logistics_fee);
			sParaTemp.put("logistics_type", logistics_type);
			sParaTemp.put("logistics_payment", logistics_payment);
			sParaTemp.put("body", body);
			sParaTemp.put("show_url", show_url);
			sParaTemp.put("receive_name", receive_name);
			sParaTemp.put("receive_address", receive_address);
			sParaTemp.put("receive_zip", receive_zip);
			sParaTemp.put("receive_phone", receive_phone);
			sParaTemp.put("receive_mobile", receive_mobile);
			
			//建立post http请求
			String sHtmlText = AlipaySubmit.buildRequest(sParaTemp,"post","确认",PaySource.DUALFUN);
    
		return sHtmlText;
	}
}


	
