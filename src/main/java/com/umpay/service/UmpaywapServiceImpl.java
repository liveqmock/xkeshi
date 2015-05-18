
package com.umpay.service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.umpay.api.common.ReqData;
import com.umpay.api.paygate.v40.Mer2Plat_v40;
import com.umpay.config.FactoryConfig;
import com.umpay.config.UmpayPaySourceConfig;
import com.umpay.config.UmpayPaySourceConfig.UmpayPaySource;
import com.xpos.common.entity.CouponPayment;
import com.xpos.common.entity.CouponPayment.CouponPaymentType;
import com.xpos.common.service.CouponPaymentService;
import com.xpos.common.service.UserPayAgreeMentService;
import com.xpos.common.service.UserService;
import com.xpos.common.utils.HttpUtils;
import com.xpos.common.utils.StringMatcher;

@Service
public class UmpaywapServiceImpl implements UmpaywapService{
    
	@Autowired
	public CouponPaymentService couponPaymentService;
	
	@Autowired
	public UserService userService;
	
	@Autowired
	public UserPayAgreeMentService userPayAgreeMentService;
	
	private Map<String,String> getUMPAYAPIMapBypeyment(CouponPayment payment,UmpayPaySourceConfig  payConfig) {
		Map<String,String> map =new HashMap<String,String>();  
		map.put("service","pay_req_shortcut");  
		map.put("charset","UTF-8");  
		map.put("mer_id",payConfig.getMer_id());  
		map.put("sign_type","RSA");  
		map.put("ret_url",payConfig.getYclakeurl());  
		//map.put("ret_url","http://yclake.nat123.net/user/coupons?type=AVAILABLE");  
		map.put("notify_url",payConfig.getPaymentNotify());  
		//map.put("notify_url","http://xposs.nat123.net/umpay/api/paymentNotify"); 
		map.put("res_format","HTML");  
		map.put("version","4.0");  
		map.put("goods_id",payment.getCouponInfo().getId().toString());  
		map.put("goods_inf",payment.getCouponInfo().getName());  
		map.put("order_id",payment.getCode());  
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		map.put("mer_date",sdf.format(new Date()));  
		map.put("amount",""+BigDecimal.valueOf(100).multiply(payment.getSum()).setScale(2, RoundingMode.HALF_UP).intValue());  
		map.put("amt_type","RMB");  
		return map;
	}
	
	@Override
	public String getUMPAYTradeNo(Long paymentid, UmpayPaySource paySource) throws Exception {
		UmpayPaySourceConfig payConfig = FactoryConfig.getPayConfig(paySource);
		CouponPayment payment = couponPaymentService.findPaymentById(paymentid);
		Map<String,String> map = getUMPAYAPIMapBypeyment(payment,payConfig);
			ReqData reqData = Mer2Plat_v40.ReqDataByGet(map);
			String url = reqData.getUrl(); 
			String tradeNoUrl = HttpUtils.httpsGet(url, null);
				String retCode = StringMatcher.getSpiltString(tradeNoUrl, "ret_code=", "&");
				if(retCode.equalsIgnoreCase("0000")) {
					String tradeNo = StringMatcher.getSpiltString(tradeNoUrl, "trade_no=", "&");
					return tradeNo;
				}
				return null;
					
	}
	
	@Override
	public String getFirstUMPAYUrl(Long paymentid, String tradeNo,UmpayPaySource paySource) {
		UmpayPaySourceConfig payConfig = FactoryConfig.getPayConfig(paySource);
		CouponPayment payment = couponPaymentService.findPaymentById(paymentid);
		return payConfig.getWapGuidanceUrl()+"?tradeNo="+tradeNo+"&merCustId="+payment.getUser().getId();
	}

	@Override
	public boolean sendAgreementMessage(String tradeNo, String userId,
			String usrpayagreementId, UmpayPaySource paySource) throws Exception {
		UmpayPaySourceConfig payConfig = FactoryConfig.getPayConfig(paySource);
		Map<String,String> map =new HashMap<String,String>(); 
		map.put("service","req_smsverify_shortcut");  
		map.put("mer_id",payConfig.getMer_id());  
		map.put("sign_type","RSA");  
		map.put("version","4.0");  
		map.put("trade_no",tradeNo);  
		//协议支付请求参数  
		map.put("mer_cust_id",userId);  
		//map.put("usr_busi_agreement_id",usr_busi_agreement_id);  
		map.put("usr_pay_agreement_id",usrpayagreementId);  
		ReqData reqData = Mer2Plat_v40.ReqDataByGet(map);
		String url = reqData.getUrl();  
		String returnUrl = HttpUtils.httpsGet(url, null);
		String retCode = StringMatcher.getSpiltString(returnUrl, "ret_code=", "&");
		if(retCode.equalsIgnoreCase("0000")) {
			return true;
		}
		return false;
	}

	@Override
	public String generateUMPAYAgreement(String tradeNo, String userId,
			String verify_code, String usrpayagreementId, UmpayPaySource paySource)
			throws Exception {
		UmpayPaySourceConfig payConfig = FactoryConfig.getPayConfig(paySource);
		Map<String,String> ht = new HashMap<String,String>();  
		ht.put("service", "agreement_pay_confirm_shortcut");  
		ht.put("sign_type", "RSA");  
		ht.put("charset", "UTF-8");  
		ht.put("version", "4.0");  
		ht.put("mer_id", payConfig.getMer_id());  
		ht.put("trade_no", tradeNo);  
		ht.put("mer_cust_id", String.valueOf(userId));  
		ht.put("verify_code", verify_code);  
		//使用协议进行支付，上送如下参数：支付协议号必填、商户用户标识和用户业务协议号必填其一  
		//ht.put("usr_busi_agreement_id",usr_busi_agreement_id);  
		ht.put("usr_pay_agreement_id",usrpayagreementId);  
		//在协议支付中，商户可选传如下参数，作为验证用户支付要素的凭证   
		//ht.put("valid_date", valid_date);  
		//ht.put("cvv2", cvv2);  
		//ht.put("birthday", birthday);  
		ReqData reqData = Mer2Plat_v40.ReqDataByGet(ht);
		String url = reqData.getUrl(); 
		String returnUrl = HttpUtils.httpsGet(url, null);
		String retCode = StringMatcher.getSpiltString(returnUrl, "ret_code=", "&");
		if(retCode.equalsIgnoreCase("0000")) {
			return  null; 
		}else {
			return StringMatcher.getSpiltString(returnUrl, "ret_msg=", "&");
		}
	}

	@Override
	public String unbindMercust(String mer_cust_id,String usrpayagreementId,
			UmpayPaySource paySource) throws Exception  {
		UmpayPaySourceConfig payConfig = FactoryConfig.getPayConfig(paySource);
		Map<String,String> map = new HashMap<String,String>();  
		map.put("service","unbind_mercust_protocol_shortcut");  
		map.put("charset","UTF-8");  
		map.put("mer_id",payConfig.getMer_id());  
		map.put("sign_type","RSA");  
		map.put("version","4.0");  
		//商户用户标识和用户业务协议号必填其一  
		map.put("mer_cust_id",mer_cust_id);  
		//map.put("usr_busi_agreement_id",usr_busi_agreement_id);  
		map.put("usr_pay_agreement_id",usrpayagreementId);  
		ReqData reqData = Mer2Plat_v40.ReqDataByGet(map);  
		String url = reqData.getUrl();  
		String returnUrl = HttpUtils.httpsGet(url, null);
		String retCode = StringMatcher.getSpiltString(returnUrl, "ret_code=", "&");
		if(retCode.equalsIgnoreCase("0000")) {
			//解绑成功,数据库移除该银行卡
			userPayAgreeMentService.deleteUserPayAgreement(mer_cust_id, CouponPaymentType.UMPAY_WAP, usrpayagreementId, payConfig.getMer_id());
			return  null; 
		}else {
			return StringMatcher.getSpiltString(returnUrl, "ret_msg=", "&");
		}
	}
	
}


	
