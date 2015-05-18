package com.xpos.api;

import java.io.IOException;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.umpay.api.paygate.v40.Mer2Plat_v40;
import com.umpay.api.paygate.v40.Plat2Mer_v40;
import com.xpos.common.entity.Coupon.CouponStatus;
import com.xpos.common.entity.CouponPayment;
import com.xpos.common.entity.CouponPayment.CouponPaymentStatus;
import com.xpos.common.entity.CouponPayment.CouponPaymentType;
import com.xpos.common.entity.Refund;
import com.xpos.common.entity.Refund.RefundAccountType;
import com.xpos.common.entity.Refund.RefundStatus;
import com.xpos.common.service.CouponPaymentService;
import com.xpos.common.service.RefundService;
import com.xpos.common.service.UserPayAgreeMentService;
import com.xpos.controller.BaseController;

@Controller
@RequestMapping("umpay")
public class UmpayController extends BaseController{
		
	@Autowired
	private CouponPaymentService paymentService;
	
	@Autowired
	private UserPayAgreeMentService userPayAgreeMentService;
	
	@Autowired
	private RefundService refundService;
	
	
	/**
	 * 联动优势交易结果通知
	 * */
	@ResponseBody
	@RequestMapping(value = "api/paymentNotify", method = RequestMethod.GET)
	public String umpayReturn(HttpServletRequest request, Model model,HttpServletResponse response) throws IOException{
		boolean result = false;
	    Map ht = new HashMap();  
	    String name = "",values="";  
	    for(Enumeration names = request.getParameterNames(); names.hasMoreElements(); ht.put(name, values))  
	    {  
	        name = (String)names.nextElement();  
	        values = request.getParameter(name);  
	    }  
	    //获取UMPAY平台请求商户的支付结果通知数据,并对请求数据进行验签,此时商户接收到的支付结果通知会存放在这里,商户可以根据此处的trade_state订单状态来更新订单。  
	    Map reqData = new HashMap();  
	    Map resData = new HashMap();  
	    
	    
	    String order_id = request.getParameter("order_id").toString();//商户订单号
	    String trade_state = request.getParameter("trade_state").toString();//联动返回的订单状态
	    String trade_no = request.getParameter("trade_no").toString();//联动交易号,对应数据库serial字段
	    String pay_seq = request.getParameter("pay_seq")==null?"":request.getParameter("pay_seq").toString();//银行流水号,对应数据库traceNo字段
	    String pay_date = request.getParameter("pay_date").toString();//联动交易时间,只有年月日,暂时不采用
	    String error_code = request.getParameter("error_code").toString();//联动返回码
	    String usr_busi_agreement_id =  request.getParameter("usr_busi_agreement_id")==null?"":request.getParameter("usr_busi_agreement_id").toString();//联动绑定的用户交易账号
	    String usr_pay_agreement_id = request.getParameter("usr_pay_agreement_id")==null?"":request.getParameter("usr_pay_agreement_id").toString();//联动绑定的用户银行卡支付号
	    String gate_id = request.getParameter("gate_id")==null?"":request.getParameter("gate_id").toString();//银行英文缩写
	    String pay_type = request.getParameter("pay_type")==null?"":request.getParameter("pay_type").toString();//信用卡或借记卡
	    String lastfourid = request.getParameter("last_four_cardid")==null?"":request.getParameter("last_four_cardid").toString();//银行卡后4位
	    
	    //获取订单详情
	    CouponPayment payment = paymentService.findPaymentByCode(order_id);
	    CouponPaymentStatus curStatus = payment.getStatus();
	    CouponPayment _payment = new CouponPayment();
	    _payment.setCode(payment.getCode());
	    _payment.setRemark(trade_state);
	    _payment.setType(CouponPaymentType.UMPAY_WAP);
	    try{  
	        //如验证平台签名正确，即应响应UMPAY平台返回码为0000。【响应返回码代表通知是否成功，和通知的交易结果（支付失败、支付成功）无关】  
	        //验签支付结果通知 如验签成功，则返回ret_code=0000  
	        reqData = Plat2Mer_v40.getPlatNotifyData(ht);
	        resData.put("ret_code","0000");  
			
	        //判断回调的订单状态
			if ("TRADE_SUCCESS".equalsIgnoreCase(trade_state)){
				if(curStatus.equals(CouponPaymentStatus.UNPAID) || curStatus.equals(CouponPaymentStatus.PAID_FAIL)
						|| curStatus.equals(CouponPaymentStatus.PAID_TIMEOUT)) {
					//如果消费结果返回成功，且DB仍是待付款或失败，则修改DB为交易成功状态
					_payment.setStatus(CouponPaymentStatus.PAID_SUCCESS);
					_payment.setTraceNo(pay_seq);
					_payment.setSerial(trade_no);
					_payment.setTradeDate(new Date());
					_payment.setResponseCode(error_code);
					if(!paymentService.updateCouponPaymentByCode(_payment)){
						logger.error("联动优势付款回调更新数据库状态失败。couponPayment code:"+order_id);
						result=false;
					}else {
						result = true;
					}
					//支付成功后,进行用户协议支付的绑定操作
					if(payment.getUser()!=null 
							&& StringUtils.isNotEmpty(usr_busi_agreement_id) 
							&& StringUtils.isNotEmpty(usr_pay_agreement_id)
							&& StringUtils.isNotEmpty(gate_id)
							&& StringUtils.isNotEmpty(pay_type)) {
						if(!userPayAgreeMentService.updateUserPayAgreement(payment.getUser(),
								CouponPaymentType.UMPAY_WAP,usr_busi_agreement_id,
								usr_pay_agreement_id, pay_type,gate_id,lastfourid)) {
							logger.error("联动优势绑卡失败。");
							result = false;
						}else {
							result = true;
						}
					}
				}else{
					   //重复的支付成功通知，不做处理
				}
			} else {
				//支付失败，数据库记录状态后返回，不再发送优惠券
				_payment.setStatus(CouponPaymentStatus.PAID_FAIL);
				_payment.setTraceNo(pay_seq);
				_payment.setSerial(trade_no);
				_payment.setTradeDate(new Date());
				_payment.setResponseCode(error_code);
				if(!paymentService.updateCouponPaymentByCode(_payment)){
					logger.error("联动优势付款回调更新数据库状态失败。couponPayment code:"+order_id);
				}
				result=false;
			}
			
			//创建优惠券
			if(result){
				paymentService.paymentByCreateCoupon(payment, false);
			}
	    }catch(Exception e){
	        //如果验签失败，则抛出异常，返回ret_code=1111  
	        //System.out.println("验证签名发生异常" + e);  
	        logger.error("联动优势付款验签失败！");
	        resData.put("ret_code","1111");  
	    } 
	    //验签后的数据都组织在resData中。  
	    //生成平台响应UMPAY平台数据,将该串放入META标签，以下几个参数为结果通知必备参数。  
	    resData.put("mer_id", request.getParameter("mer_id"));  
	    resData.put("sign_type", request.getParameter("sign_type"));  
	    resData.put("version", request.getParameter("version"));  
	    //支付状态通知响应需提交order_id和mer_date，退款状态通知无需提交  
	    resData.put("order_id", request.getParameter("order_id"));  
	    resData.put("mer_date", request.getParameter("mer_date"));  
	    //resData.put("ret_msg", "success");  
	    String data = Mer2Plat_v40.merNotifyResData(resData);
		return getResultHtml(data);
	}
	
	/**
	 * 联动优势退款结果通知
	 * */
	@ResponseBody
	@RequestMapping(value = "api/refundNotify", method = RequestMethod.GET)
	public String umpayNotifyReturn(HttpServletRequest request, Model model,HttpServletResponse response) throws IOException{
		boolean result = false;
		Map ht = new HashMap();  
		String name = "",values="";  
		for(Enumeration names = request.getParameterNames(); names.hasMoreElements(); ht.put(name, values))  
		{  
			name = (String)names.nextElement();  
			values = request.getParameter(name);  
		}  
		//获取UMPAY平台请求商户的支付结果通知数据,并对请求数据进行验签,此时商户接收到的支付结果通知会存放在这里,商户可以根据此处的trade_state订单状态来更新订单。  
		Map reqData = new HashMap();  
		Map resData = new HashMap();  
		
		
		String refund_no = request.getParameter("refund_no").toString();//商户退费流水号,唯一。
		String refund_state = request.getParameter("refund_state")==null?"":request.getParameter("refund_state").toString();//联动返回的退款状态(分账情况不收到该值,此处不涉及)
		
		
		//获取订单详情
		Refund refund = refundService.findRefundBySerial(refund_no);
		try{  
			//如验证平台签名正确，即应响应UMPAY平台返回码为0000。【响应返回码代表通知是否成功，和通知的交易结果（支付失败、支付成功）无关】  
			//验签支付结果通知 如验签成功，则返回ret_code=0000  
			reqData = Plat2Mer_v40.getPlatNotifyData(ht);
			resData.put("ret_code","0000");  
			
			CouponStatus couponStatus = null;
			if(refund_state.equalsIgnoreCase("REFUND_PROCESS")) {
				refund.setStatus(RefundStatus.AUTO_EXECUTE);//平台已受理,财务处理中,状态更改为自动退款中
				refund.setRemark("自动退款中,联动优势返回信息："+"请求发起成功，财务正在处理");
				couponStatus = CouponStatus.REFUND_ACCEPTED;
			}else if(refund_state.equalsIgnoreCase("REFUND_UNKNOWN")||refund_state.equalsIgnoreCase("REFUND_FAIL")) {
				refund.setStatus(RefundStatus.AUTO_FAILED);//当退费由于网络等诸多因素造成超时时，会返回此状态。或直接返回退费失败。
				refund.setRemark("自动退款失败,联动优势返回信息："+"由于商户待结算账户余额不足，导致退款失败");
				couponStatus = CouponStatus.REFUND_FAIL;
			}else if(refund_state.equalsIgnoreCase("REFUND_SUCCESS")) {
				refund.setStatus(RefundStatus.AUTO_SUCCESS);//退费成功;
				refund.setRemark("自动退款成功,联动优势返回信息："+"平台给用户打款成功");
				couponStatus = CouponStatus.REFUND_SUCCESS;
			}
			refund.setSerial(refund_no);
			refund.setType(RefundAccountType.DEBIT_CARD);
			refundService.updateRelatedCoupon(refund, couponStatus);
			refundService.updateRefundByCode(refund);
		}catch(Exception e){
			//如果验签失败，则抛出异常，返回ret_code=1111  
			//System.out.println("验证签名发生异常" + e);  
			logger.error("联动优势付款验签失败！");
			resData.put("ret_code","1111");  
		} 
		//验签后的数据都组织在resData中。  
		//生成平台响应UMPAY平台数据,将该串放入META标签，以下几个参数为结果通知必备参数。  
		resData.put("mer_id", request.getParameter("mer_id"));  
		resData.put("sign_type", request.getParameter("sign_type"));  
		resData.put("version", request.getParameter("version"));  
		resData.put("ret_msg", "SUCCESS");  
		String data = Mer2Plat_v40.merNotifyResData(resData);
		return getResultHtml(data);
	}
	
	private String getResultHtml(String data){
        StringBuffer sb = new StringBuffer();
        sb.append("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\n" +
                "<html>\n" +
                "  <head>\n" +
                "<META NAME=\"MobilePayPlatform\" CONTENT=\"");
        sb.append(data);
        sb.append("\">\n" +
                "  </head>\n" +
                "  <body>\n" +
                "  </body>\n" +
                "</html>");
        return sb.toString();
    }
	
}
