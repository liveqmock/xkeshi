package com.xpos.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.text.DecimalFormat;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dom4j.DocumentException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alipay.config.FactoryConfig;
import com.alipay.config.PaySourceConfig;
import com.alipay.config.PaySourceConfig.PaySource;
import com.alipay.service.AlipayWapDirectService;
import com.alipay.util.AlipayNotify;
import com.xpos.common.entity.BalanceTransaction;
import com.xpos.common.entity.CouponPayment;
import com.xpos.common.entity.CouponPayment.CouponPaymentStatus;
import com.xpos.common.entity.Payment;
import com.xpos.common.entity.Payment.PaymentStatus;
import com.xpos.common.entity.example.BalanceTransactionExample;
import com.xpos.common.service.BalanceTransactionService;
import com.xpos.common.service.RefundService;
import com.xpos.common.utils.Pager;

@Controller
@RequestMapping("balance")
public class BalanceController extends BaseController{
		
	@Resource
	private BalanceTransactionService balanceTransactionService;
	
	@Resource
	private AlipayWapDirectService  alipayWapDirectService;
	
	@Resource
	private RefundService  refundService ;
	
	@RequestMapping(value="/transaction/list", method=RequestMethod.GET)
	public String transactionList(Pager<BalanceTransaction> pager, Model model){
		
		//TODO Searcher
		BalanceTransactionExample example = new BalanceTransactionExample();
		example.createCriteria().addCriterion("businessType='"+getBusiness().getSelfBusinessType()+"'")
								.addCriterion("businessId=", getBusiness().getSelfBusinessId());
		example.setOrderByClause("createDate DESC");
		pager = balanceTransactionService.findBalanceTransaction(pager, example);
		
		model.addAttribute("balance", getBusiness().getBalance());
		model.addAttribute("pager", pager);
		
		return "balance/balance_transaction_list";
	}
	
	@RequestMapping(value="/charge", method=RequestMethod.POST)
	public String charge(BigDecimal amount, String bank, Model model) throws Exception{
		String url = balanceTransactionService.generateAlipayUrl(getAccount(), getBusiness(), amount, bank,PaySource.XKESHI_ALIPAY_DIRECT);
		
		return "redirect:" + url;
	}
	
	
	/**
	 * 阳澄湖WAP支付回调
	 * @throws DocumentException 
	 */
	@RequestMapping(value="/api/callBackYCLake",method = RequestMethod.GET)
	public String alipayWapReturn (HttpServletRequest request, Model model,HttpServletResponse response) {
		 String  url  = FactoryConfig.getPayConfig(PaySource.YCLAKE_WAP_DIRECT).getMerchantUrl();
		 try {
			  CouponPayment couponPayment = alipayWapDirectService.processAlipayCallBackWap(request, PaySource.YCLAKE_WAP_DIRECT);
			 if (couponPayment != null) {
				 url  += CouponPaymentStatus.PAID_SUCCESS.equals( couponPayment.getStatus()) 
						 ? "/user/coupons?type=AVAILABLE" :"/pay/pay_fail?cid="+couponPayment.getCouponInfo().getId();
			}
		} catch (DocumentException e) {
			 logger.error("[WAPPAY]阳澄湖同步通知支付宝!"+e.toString());
		} 
		 return "redirect:" +url;
	}
	/**
	 * 阳澄湖WAP支付异步通知
	 * @throws DocumentException 
	 */
	@RequestMapping(value="/api/wapNotifyYCLake",method = RequestMethod.POST)
	public void alipayWapNotify (HttpServletRequest request, Model model,HttpServletResponse response) {
		try {
			boolean result  = alipayWapDirectService.processAlipayNotifyWap(request, PaySource.YCLAKE_WAP_DIRECT);
			String  reString = result ?  "success" : "fail";
			 PrintWriter out = response.getWriter();
			 out.println(reString);
			 out.flush();
			 out.close();
		} catch (Exception e) {
			logger.error("[WAPPAY]异步通知支付宝出错:"+e.getMessage());
		}
	}
	/**
	 * 阳澄湖Wap退款异步通知
	 */
 	@RequestMapping(value="/api/refwapntfyclake",method = RequestMethod.POST)
	public void refundWapNotify(HttpServletRequest  request , HttpServletResponse response ,Model  model){
		logger.debug("[REFUNDWAPPAY]");
 		try {
			boolean refundNotifyWap = alipayWapDirectService.processAlipayRefundNotifyWap(request, PaySource.YCLAKE_WAP_DIRECT);
			String  reString = refundNotifyWap ?  "success" : "fail";
			 PrintWriter out = response.getWriter();
			 out.println(reString);
			 out.flush();
			 out.close();
		} catch (Exception e) {
			logger.error("[REFUNDWAPPAY]退款异步通知支付宝出错:"+e.getMessage());
		}
	}
	
	/**
	 * pc即时到账回调
	 */
	@RequestMapping(value = "/api/alipayReturn", method = RequestMethod.GET)
	public String alipayReturn(HttpServletRequest request, Model model,HttpServletResponse response) throws IOException{
		
		logger.info("[PAYMENT] 支付宝!");

		boolean result = processAlipay(request,PaySource.XKESHI_ALIPAY_DIRECT);
	
		if (result) {
			response.getOutputStream().print("success");
		} else {
			response.getOutputStream().print("fail");
		}

		return null;
	}
	/**
	 * pc即时到账回调
	 */
	@RequestMapping(value = "/api/alipayNotify", method = RequestMethod.POST)
	public String alipayNotify(HttpServletRequest request, Model model,
			HttpServletResponse response) throws IOException{

		logger.info("[PAYMENT] 支付宝通知!");

		boolean result = processAlipay(request,PaySource.XKESHI_ALIPAY_DIRECT);
	
		if (result) {
			response.getOutputStream().print("success");
		} else {
			response.getOutputStream().print("fail");
		}

		return "";
	}
	
	
	private boolean processAlipay(HttpServletRequest request ,PaySource payType){
		PaySourceConfig payConfig = FactoryConfig.getPayConfig(payType);
		String mySign = AlipayNotify.GetMysign(request.getParameterMap(),
				payConfig.getKey(),payType);
		
		if (mySign.equals(request.getParameter("sign"))) {
			String status = request.getParameter("trade_status");
			if (!"TRADE_FINISHED".equals(status)
					&& !"TRADE_SUCCESS".equals(status))
				return false;
			
			String paymentNum = request.getParameter("out_trade_no");
			String totalFee = request.getParameter("total_fee");
			String tradeNo = request.getParameter("trade_no");
			DecimalFormat df = new DecimalFormat("0.00");
			
			Payment payment = balanceTransactionService.findPaymentByNum(paymentNum);
			if(payment == null)
				return false;
						
			//实付金额与应付金额不等
			if(!df.format(payment.getAmount().doubleValue()).equals(totalFee))
				return false;
			
			//重复提交
			if(PaymentStatus.PAID.equals(payment.getStatus()))
				return false;
			
			payment.setOuterNum(tradeNo);
			return balanceTransactionService.processPayment(payment);
		}
		return false;
	}
}
