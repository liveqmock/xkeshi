package com.alipay.service;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.dom4j.DocumentException;

import com.alipay.config.PaySourceConfig.PaySource;
import com.xpos.common.entity.CouponPayment;
import com.xpos.common.entity.Refund;

/**
 * 
 * @author xk
 * 支付宝手机wap即时到账支付
 */
public interface AlipayWapDirectService {
	/**
	 * 功能：构造请求URL（GET方式请求）
	 * @param out_trade_no  //商户订单号
	 * @param subject       //订单名称
	 * @param total_fee     //付款金额
	 * @param PaySource       //支付类型
	 * @return
	 * @throws Exception
	 * @author xk
	 */
	public   String CreateUrl(String out_trade_no ,String subject ,String total_fee ,PaySource paySource) throws Exception ;
	
	/**
	 * 功能：构造退款请求(POST方式)
	 * 注：支持多笔退款
	 * @param refundDate   key:原支付流水号，refund:sum+remark
	 * @return
	 */
	public String  CreateRefundUrl(Map<String, Refund>  refundDate,PaySource  paySource);
	
	/**
	 * wap支付异步回调处理
	 * @param request
	 * @param paySource   //支付来源
	 * @return
	 * @throws UnsupportedEncodingException 
	 * @author xk
	 */
	public  boolean processAlipayNotifyWap(HttpServletRequest request , PaySource paySource) throws Exception ;

	/**
	 * wap支付同步回调处理
	 * @param request
	 * @param paySource   //支付来源
	 * @return
	 * @author xk
	 */
	public CouponPayment processAlipayCallBackWap(HttpServletRequest request, PaySource paySource)  throws DocumentException ;
	
	/**
	 * wap退款异步回调处理
	 * @param request
	 * @param paySource   //支付来源
	 * @return
	 * @throws UnsupportedEncodingException 
	 * @author xk
	 */
	public boolean processAlipayRefundNotifyWap(HttpServletRequest request , PaySource paySource) throws Exception;
	
}
