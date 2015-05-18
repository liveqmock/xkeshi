
package com.umpay.service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.umpay.api.common.ReqData;
import com.umpay.api.exception.ReqDataException;
import com.umpay.api.paygate.v40.Mer2Plat_v40;
import com.umpay.config.FactoryConfig;
import com.umpay.config.UmpayPaySourceConfig;
import com.umpay.config.UmpayPaySourceConfig.UmpayPaySource;
import com.xpos.common.entity.CouponPayment;
import com.xpos.common.entity.Coupon.CouponStatus;
import com.xpos.common.entity.CouponPayment.CouponPaymentType;
import com.xpos.common.entity.Refund;
import com.xpos.common.entity.Refund.RefundAccountType;
import com.xpos.common.entity.Refund.RefundStatus;
import com.xpos.common.service.CouponPaymentService;
import com.xpos.common.service.RefundService;
import com.xpos.common.service.UserService;
import com.xpos.common.utils.DateUtil;
import com.xpos.common.utils.HttpUtils;
import com.xpos.common.utils.StringMatcher;
import com.xpos.common.utils.UUIDUtil;

@Service
public class UmpaywapRefundServiceImpl implements UmpaywapRefundService{
    

	@Resource
	public RefundService refundService;
	

	@Override
	public String requestRefund(Refund refund, UmpayPaySource paySource) {
		UmpayPaySourceConfig payConfig = FactoryConfig.getPayConfig(paySource);
		Map ht = new HashMap();  
		ht.put("service", "mer_refund");  
		ht.put("sign_type", "RSA");  
		ht.put("charset", "UTF-8");  
		ht.put("notify_url",payConfig.getPaymentRefundNotify());  
		//ht.put("notify_url","http://xposs.nat123.net/umpay/api/refundNotify");  
		ht.put("mer_id",payConfig.getMer_id()); 
		if(StringUtils.isNotEmpty(refund.getSerial())) {
			ht.put("refund_no", refund.getSerial());  
		}else {
			String randomSerial = "";
			for (int i = 0; i < Integer.MAX_VALUE; i++) {
				randomSerial = DateUtil.getDate("yyMMddHHmmss")+UUIDUtil.getRandomString(4);
				if(refundService.findRefundBySerial(randomSerial)==null) {
					break;
				}
			}
			ht.put("refund_no", randomSerial);  
		}
		ht.put("order_id", refund.getPayment().getCode());  
		ht.put("mer_date", DateUtil.getDate(refund.getPayment().getTradeDate(),"yyyyMMdd"));  
		ht.put("refund_amount", BigDecimal.valueOf(100).multiply(refund.getSum()).setScale(2, RoundingMode.HALF_UP).intValue());  
		ht.put("org_amount", BigDecimal.valueOf(100).multiply(refund.getPayment().getSum()).setScale(2, RoundingMode.HALF_UP).intValue());  
		ht.put("version", "4.0");  
		  
		try {
			ReqData reqData = Mer2Plat_v40.ReqDataByGet(ht);
			String url = reqData.getUrl();  
			String returnUrl = HttpUtils.httpsGet(url,null);
			String refund_state = StringMatcher.getSpiltString(returnUrl, "refund_state=", "&");
			String refund_no = StringMatcher.getSpiltString(returnUrl, "refund_no=", "&");
			String ret_msg = StringMatcher.getSpiltString(returnUrl, "ret_msg=", "&");
			CouponStatus couponStatus = null;
			if(refund_state.equalsIgnoreCase("REFUND_PROCESS")) {
				refund.setStatus(RefundStatus.AUTO_EXECUTE);//平台已受理,财务处理中,状态更改为自动退款中
				refund.setRemark("自动退款中,"+ret_msg);
				couponStatus = CouponStatus.REFUND_ACCEPTED;
			}else if(refund_state.equalsIgnoreCase("REFUND_UNKNOWN")||refund_state.equalsIgnoreCase("REFUND_FAIL")) {
				refund.setStatus(RefundStatus.AUTO_FAILED);//当退费由于网络等诸多因素造成超时时，会返回此状态。或直接返回退费失败。
				refund.setRemark("自动退款失败,"+ret_msg);
				couponStatus = CouponStatus.REFUND_FAIL;
			}else if(refund_state.equalsIgnoreCase("REFUND_SUCCESS")) {
				refund.setStatus(RefundStatus.AUTO_SUCCESS);//退费成功;
				refund.setRemark("自动退款成功");
				couponStatus = CouponStatus.REFUND_SUCCESS;
			}else {
				refund.setStatus(RefundStatus.AUTO_FAILED);
				if(ret_msg.indexOf("T+0")!=-1) {
					refund.setRemark("自动退款失败,"+"商户不支持T+0退款!");
				}else {
					refund.setRemark("自动退款失败,"+ret_msg);
				}
				
			}
			refund.setSerial(refund_no);
			refund.setType(RefundAccountType.DEBIT_CARD);
			refundService.updateRelatedCoupon(refund, couponStatus);
			refundService.updateRefundByCode(refund);
		} catch (ReqDataException e) {
			e.printStackTrace();
		} 
		
		return null;
	}
	
}


	
