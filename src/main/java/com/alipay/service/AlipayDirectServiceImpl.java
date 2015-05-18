package com.alipay.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.alipay.config.FactoryConfig;
import com.alipay.config.PaySourceConfig.PaySource;
import com.alipay.util.AlipayBase;
@SuppressWarnings({ "rawtypes", "unchecked" })
@Service
public class AlipayDirectServiceImpl  implements  AlipayDirectService {
	  
		/**
		 * 功能：构造请求URL（GET方式请求）
		 * @param partner 合作身份者ID
		 * @param seller_email 签约支付宝账号或卖家支付宝帐户
		 * @param return_url 付完款后跳转的页面 要用 以http开头格式的完整路径，不允许加?id=123这类自定义参数
		 * @param notify_url 交易过程中服务器通知的页面 要用 以http开格式的完整路径，不允许加?id=123这类自定义参数
		 * @param show_url 网站商品的展示地址，不允许加?id=123这类自定义参数
		 * @param out_trade_no 请与贵网站订单系统中的唯一订单号匹配
		 * @param subject 订单名称，显示在支付宝收银台里的“商品名称”里，显示在支付宝的交易管理的“商品名称”的列表里。
		 * @param body 订单描述、订单详细、订单备注，显示在支付宝收银台里的“商品描述”里
		 * @param total_fee 订单总金额，显示在支付宝收银台里的“应付总额”里
		 * @param paymethod 默认支付方式，四个值可选：bankPay(网银); cartoon(卡通); directPay(余额);  CASH(网点支付)
		 * @param defaultbank 默认网银代号，代号列表见club.alipay.com/read.php?tid=8681379
		 * @param encrypt_key 防钓鱼时间戳
		 * @param exter_invoke_ip 买家本地电脑的IP地址
		 * @param extra_common_param 自定义参数，可存放任何内容（除等特殊字符外），不会显示在页面上
		 * @param buyer_email 默认买家支付宝账号
		 * @param royalty_type 提成类型，该值为固定值：10，不需要修改
		 * @param royalty_parameters 提成信息集，与需要结合商户网站自身情况动态获取每笔交易的各分润收款账号、各分润金额、各分润说明。最多只能设置10条
		 * @param it_b_pay 超时时间，不填默认是15天。八个值可选：1h(1小时),2h(2小时),3h(3小时),1d(1天),3d(3天),7d(7天),15d(15天),1c(当天)
		 * @param input_charset 字符编码格式 目前支持 GBK 或 utf-8
		 * @param key 安全校验码
		 * @param sign_type 加密方式 不需修改
		 * @return 请求URL
		 * @throws Exception 
		 */
		@Override
		public  String CreateUrl(
				PaySource paySource,
				String partner,
				String seller_email,
				String return_url,
				String notify_url,
				String show_url,
				String out_trade_no,
				String subject,
				String body,
				String total_fee,
				String paymethod,
				String defaultbank,
				String encrypt_key,
				String exter_invoke_ip,
				String extra_common_param,
	            String buyer_email,
				String royalty_type,
				String royalty_parameters,
				String it_b_pay,
	            String input_charset,
	            String key,
	            String sign_type) throws Exception{
					
			Map sPara = new HashMap();
			sPara.put("service","create_direct_pay_by_user");
			sPara.put("payment_type","1");
			sPara.put("partner", partner);
			sPara.put("seller_email", seller_email);
			sPara.put("return_url", return_url);
			sPara.put("notify_url", notify_url);
			sPara.put("_input_charset", input_charset);
			sPara.put("show_url", show_url);
			sPara.put("out_trade_no", out_trade_no);
			sPara.put("subject", subject);
			sPara.put("body", body);
			sPara.put("total_fee", total_fee);
			sPara.put("paymethod", paymethod);
			sPara.put("defaultbank", defaultbank);
			sPara.put("anti_phishing_key", encrypt_key);
			sPara.put("exter_invoke_ip", exter_invoke_ip);
			sPara.put("extra_common_param", extra_common_param);
			sPara.put("buyer_email", buyer_email);
			sPara.put("royalty_type", royalty_type);
			sPara.put("royalty_parameters", royalty_parameters);
			sPara.put("it_b_pay", it_b_pay);
			sPara.put("default_login", "N");

			Map sParaNew = AlipayBase.ParaFilter(sPara); //除去数组中的空值和签名参数
			String mysign = AlipayBase.BuildMysign(sParaNew, key,paySource);//生成签名结果
			//把数组所有元素，按照“参数=参数值”的模式用“&”字符拼接成字符串
			String arg = AlipayBase.CreateLinkString_urlencode(sParaNew, input_charset);
			//得到请求URL
			return FactoryConfig.getPayConfig(paySource).getAlipayGateway()  + arg + "sign=" + mysign + "&sign_type=" + sign_type;
		}
}
