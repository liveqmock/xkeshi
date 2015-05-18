package com.alipay.service;

import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alipay.config.FactoryConfig;
import com.alipay.config.PaySourceConfig;
import com.alipay.config.PaySourceConfig.PaySource;
import com.alipay.util.AlipayBase;
import com.alipay.util.AlipayCore;
import com.alipay.util.AlipayNotify;
import com.alipay.util.AlipaySubmit;
import com.alipay.util.UtilDate;
import com.xpos.common.entity.Coupon;
import com.xpos.common.entity.Coupon.CouponStatus;
import com.xpos.common.entity.CouponPayment;
import com.xpos.common.entity.CouponPayment.CouponPaymentStatus;
import com.xpos.common.entity.CouponPayment.CouponPaymentType;
import com.xpos.common.entity.Refund;
import com.xpos.common.entity.Refund.RefundAccountType;
import com.xpos.common.entity.Refund.RefundStatus;
import com.xpos.common.entity.RefundLog;
import com.xpos.common.entity.example.CouponPaymentExample;
import com.xpos.common.persistence.mybatis.CouponPaymentMapper;
import com.xpos.common.service.CouponPaymentService;
import com.xpos.common.service.CouponService;
import com.xpos.common.service.RefundLogService;
import com.xpos.common.service.RefundService;
import com.xpos.common.utils.UUIDUtil;

@Service
public class AlipayWapDirectServiceImpl implements AlipayWapDirectService {
     
	private static Logger   logger  = Logger.getLogger(AlipayWapDirectServiceImpl.class);
	private static String format = "xml";

	private static String v = "2.0";

	@Resource
	private CouponPaymentMapper couponPaymentMapper;

	@Resource
	private CouponPaymentService couponPaymentService;

	@Resource
	private CouponService couponService;

	@Resource
	private RefundService refundService;

	@Resource
	private RefundLogService  refundLogService  ;
	//支付宝退款失败的错误原因
	private String getErrorMsg(String errorCode) {
		Map<String, String>  errorMap  = new HashMap<>();
		errorMap.put("TXN_RESULT_ACCOUNT_BALANCE_NOT_ENOUGH", "(原因：退款账户余额不足，退款失败)");
		errorMap.put("TRADE_HAS_CLOSED", "(原因：交易关闭)");
		errorMap.put("EXTERFACE_IS_CLOSED", "(原因:接口已关闭)");
		errorMap.put("BATCH_NUM_EXCEED_LIMIT", "(原因：总比数大于1000)");
		errorMap.put("REFUND_DATE_ERROR", "(原因:错误的退款时间)");
		errorMap.put("NOT_THIS_SELLER_TRADE", "(原因:不是当前卖家的交易)");
		errorMap.put("DUPLICATE_BATCH_NO", "(原因：重复的批次号)");
		errorMap.put("TRADE_STATUS_ERROR", "(原因：交易状态不允许退款 )");
		errorMap.put("RESULT_FACE_AMOUNT_NOT_VALID", "(原因：退款票面价不能大于支付票面价)");
		return errorMap.get(errorCode) == null ? "(原因：退款失败，请检查账户余额或联系买家)" :errorMap.get(errorCode);
	}
	/**
	 * 功能：构造请求URL（GET方式请求）
	 * 
	 * @param out_trade_no
	 *            //商户订单号
	 * @param subject
	 *            //订单名称
	 * @param total_fee
	 *            //付款金额
	 * @return
	 * @throws Exception
	 */
	public String CreateUrl(String out_trade_no, String subject,
			String total_fee, PaySource paySource) throws Exception {
		paySource = PaySource.YCLAKE_WAP_DIRECT;
		PaySourceConfig payConfig = FactoryConfig
				.getPayConfig(PaySource.YCLAKE_WAP_DIRECT);
		// 请求业务参数详细
		String req_dataToken = "<direct_trade_create_req>" + "<notify_url>"
				+ payConfig.getNotifyUrl() + "</notify_url>"
				+ "<call_back_url>" + payConfig.getCallBackUrl()
				+ "</call_back_url>" + "<seller_account_name>"
				+ payConfig.getSellerEmail() + "</seller_account_name>"
				+ "<out_trade_no>" + out_trade_no + "</out_trade_no>"
				+ "<subject>" + subject + "</subject>" + "<total_fee>"
				+ total_fee + "</total_fee>" + "<merchant_url>"
				+ payConfig.getMerchantUrl() + "</merchant_url>"
				+ "<pay_expire>" + 60 + "</pay_expire>" // 一小时失效
				+ "</direct_trade_create_req>";

		// 把请求参数打包成数组
		Map<String, String> sParaTempToken = new HashMap<String, String>();
		sParaTempToken.put("service", "alipay.wap.trade.create.direct");
		sParaTempToken.put("partner", payConfig.getPartnerID());
		sParaTempToken.put("_input_charset", payConfig.getCharSet());
		sParaTempToken.put("sec_id", payConfig.getSignType());
		sParaTempToken.put("format", format);
		sParaTempToken.put("v", v);
		sParaTempToken.put("req_id", UtilDate.getOrderNum());
		sParaTempToken.put("req_data", req_dataToken);

		// 建立请求
		String sHtmlTextToken = AlipaySubmit
				.buildRequest(payConfig.getAlipayGateway(), "", "",
						sParaTempToken, paySource);
		// URLDECODE返回的信息
		sHtmlTextToken = URLDecoder.decode(sHtmlTextToken,
				payConfig.getCharSet());
		// 获取token
		String request_token = AlipayBase.getRequestToken(sHtmlTextToken,
				paySource);

		// 根据授权码token调用交易接口alipay.wap.auth.authAndExecute
		// 业务详细
		String req_data = "<auth_and_execute_req><request_token>"
				+ request_token + "</request_token></auth_and_execute_req>";

		// 把请求参数打包成数组
		Map<String, String> sParaTemp = new HashMap<String, String>();
		sParaTemp.put("service", "alipay.wap.auth.authAndExecute");
		sParaTemp.put("partner", payConfig.getPartnerID());
		sParaTemp.put("_input_charset", payConfig.getCharSet());
		sParaTemp.put("sec_id", payConfig.getSignType());
		sParaTemp.put("format", format);
		sParaTemp.put("v", v);
		sParaTemp.put("req_data", req_data);
		// 除去数组中的空值和签名参数
		Map<String, String> sPara = AlipayCore.paraFilter(sParaTemp);
		// 生成签名结果
		String mysign = AlipayBase.buildRequestMysign(sPara, paySource);
		sPara.put("sign", mysign);
		if (!sPara.get("service").equals("alipay.wap.trade.create.direct")
				&& !sPara.get("service").equals(
						"alipay.wap.auth.authAndExecute")) {
			sPara.put("sign_type", payConfig.getSignType());
		}
		String arg = AlipayBase.CreateLinkString_urlencode(sPara,
				payConfig.getCharSet());
		return FactoryConfig.getPayConfig(paySource).getAlipayGateway() + arg;
	}

	@Override
	public boolean processAlipayNotifyWap(HttpServletRequest request,
			PaySource paySource) throws Exception {
		Map<String, String> params = requestParamsMap(request);
		boolean verifyNotify = AlipayNotify.verifyNotify(params, paySource);
		if (verifyNotify) {
			Element doc_notify_data = DocumentHelper.parseText(
					request.getParameter("notify_data")).getRootElement();
			// 商户订单号
			String outTradeNo = doc_notify_data.element("out_trade_no")
					.getText();
			// 支付宝交易号
			String tradeNo = doc_notify_data.element("trade_no").getText();
			// 交易状态
			String tradeStatus = doc_notify_data.element("trade_status")
					.getText();
			// 支付时间
			String gmtPayment = doc_notify_data.element("gmt_payment")
					.getText();
			// 支付账户 email或mobile
			String buyerAccount = doc_notify_data.element("buyer_email")
					.getText();
			CouponPaymentExample couponPaymentExample = new CouponPaymentExample();
			couponPaymentExample.appendCriterion("code=", outTradeNo)
					.addCriterion("deleted = ", false);
			CouponPayment couponPayment = couponPaymentMapper
					.selectOneByExample(couponPaymentExample);
			if (couponPayment == null)
				return true;
			couponPayment.setSerial(tradeNo);
			couponPayment.setType(CouponPaymentType.ALIPAY_WAP);
			buyerAccount = buyerAccount.length() > 100 ? buyerAccount
					.substring(0, 100) : buyerAccount;
			couponPayment.setBuyerAccount(buyerAccount);
			try {
				couponPayment.setTradeDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(gmtPayment));
			} catch (ParseException e) {
				e.printStackTrace();
			}
			// 支付成功
			if ("TRADE_FINISHED".equals(tradeStatus) || "TRADE_SUCCESS".equals(tradeStatus)) {
				if(	   CouponPaymentStatus.UNPAID.equals(couponPayment.getStatus())
						|| CouponPaymentStatus.PAID_FAIL.equals(couponPayment.getStatus())
						|| CouponPaymentStatus.PAID_TIMEOUT.equals(couponPayment.getStatus())) {
						 couponPayment.setStatus(CouponPaymentStatus.PAID_SUCCESS);
						 if(couponPaymentService.updateCouponPayment(couponPayment))
						 couponPaymentService.paymentByCreateCoupon(couponPayment, false);
					}
			} else {
				couponPayment.setStatus(CouponPaymentStatus.PAID_FAIL);
				couponPaymentService.updateCouponPayment(couponPayment);
			}
			return true;
		}
		return false;
	}

	@SuppressWarnings("rawtypes")
	private Map<String, String> requestParamsMap(HttpServletRequest request) {
		Map<String, String> params = new HashMap<String, String>();
		Map requestParams = request.getParameterMap();
		for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
			String name = (String) iter.next();
			String[] values = (String[]) requestParams.get(name);
			String valueStr = "";
			for (int i = 0; i < values.length; i++) {
				valueStr = (i == values.length - 1) ? valueStr + values[i]
						: valueStr + values[i] + ",";
			}
			params.put(name, valueStr);
		}
		return params;
	}

	@Override
	public CouponPayment processAlipayCallBackWap(HttpServletRequest request,
			PaySource paySource) {
		PaySourceConfig payConfig = FactoryConfig.getPayConfig(paySource);
		String mySign = AlipayNotify.GetMysign(request.getParameterMap(),
				payConfig.getKey(), paySource);
		if (mySign.equals(request.getParameter("sign"))) {
			String code = request.getParameter("out_trade_no");
			String tradeNo = request.getParameter("trade_no");
			CouponPaymentExample couponPaymentExample = new CouponPaymentExample();
			couponPaymentExample.appendCriterion("code=", code)
					.addCriterion("serial = ", tradeNo)
					.addCriterion("deleted = ", false);
			return couponPaymentMapper.selectOneByExample(couponPaymentExample);
		}
		return null;
	}

	@Override
	public boolean processAlipayRefundNotifyWap(HttpServletRequest request,
			PaySource paySource) throws Exception {
		Map<String, String> params = requestParamsMap(request);
		boolean verify = AlipayNotify.verify(params, paySource);
		if (verify) {
			// 批次号
			String batch_no = new String(request.getParameter("batch_no").getBytes("ISO-8859-1"), "UTF-8");
			String tradeDate = new String(request.getParameter("notify_time").getBytes("ISO-8859-1"), "UTF-8");
			// 批量退款数据中转账成功的笔数
			// String success_num = new
			// String(request.getParameter("success_num").getBytes("ISO-8859-1"),"UTF-8");
			// 批量退款数据中的详细信息
			// 格式:格式为：交易号^退款金额^处理结果$退费账号^退费账户ID^退费金额^处理结果
			//2014091161388606^0.01^SUCCESS#2014091262431257^0.01^SUCCESS, 
			String result_details = new String(request.getParameter("result_details").getBytes("ISO-8859-1"), "UTF-8");
			List<Refund> refundList = refundService.findBatchNoRefundList(batch_no);
			String[] details = result_details.split("#");
			for (String detail : details) {
				String[] datas = detail.split("\\^");
				for (Refund refund : refundList) {
					String description  =  "自动退款失败:";
					logger.debug("支付宝Wap退款交易状态：交易流水"+datas[0]+",状态:"+datas[2]);
					if (refund.getPayment().getSerial().equals(datas[0]) && !RefundStatus.AUTO_SUCCESS.equals(refund.getStatus()) ) {
						refund.setType(RefundAccountType.ALIPAY);
						try {
							refund.setTradeDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(tradeDate));
						} catch (ParseException e) {
							e.printStackTrace();
						}
						Coupon coupon = refund.getCoupon();
						if ("SUCCESS".equalsIgnoreCase(datas[2])) {
							refund.setStatus(RefundStatus.AUTO_SUCCESS);
							coupon.setStatus(CouponStatus.REFUND_SUCCESS);
							description  =  "自动退款成功";
						}else{
							description += getErrorMsg(datas[2]);
							refund.setStatus(RefundStatus.AUTO_FAILED);
							coupon.setStatus(CouponStatus.REFUND_FAIL);
						}
						refund.setRemark(description);
						refundService.updateRefund(refund);
					}
				}

			}

			return true;
		}
		return false;
	}


	@Override
	@Transactional
	public String CreateRefundUrl(Map<String, Refund> refundDate,
			PaySource paySource) {
		// 把请求参数打包成数组
		PaySourceConfig payConfig = FactoryConfig.getPayConfig(paySource);

		Map<String, String> sParaTemp = new HashMap<String, String>();
		sParaTemp.put("service", "refund_fastpay_by_platform_pwd");
		sParaTemp.put("partner", payConfig.getPartnerID());
		sParaTemp.put("_input_charset", payConfig.getCharSet());
		sParaTemp.put("notify_url", payConfig.getRefundNotifyUrl());
		sParaTemp.put("seller_email", payConfig.getSellerEmail());
		sParaTemp.put("refund_date", UtilDate.getDateFormatter()); // 退款当前时间。格式：2007-10-01 13:13:13
		String batch_no = UtilDate.getDate() + UUIDUtil.getRandomString(24);// 退款流水格式：当天日期[8位]+序列号[3至24位]，如：201008010000001
		sParaTemp.put("batch_no", batch_no); // 退款批次号
		sParaTemp.put("batch_num", refundDate.size() + ""); // 退款笔数
		StringBuilder detailData = new StringBuilder();
		// 退款详细数据：格式：支付宝流水号^退款金额^退款说明
		// 2014091149408057^0.01^协商退款第一笔#2014091149408057^0.01^协商退款第二笔
		StringBuilder serials = new StringBuilder();
		Set<Map.Entry<String, Refund>> set = refundDate.entrySet();
		boolean updateRefund  = true  ;
		for (Iterator<Map.Entry<String, Refund>> it = set.iterator(); it.hasNext();) {
			Map.Entry<String, Refund> entry = (Map.Entry<String, Refund>) it.next();
			Refund refund = entry.getValue();
			if (refund != null) {
				detailData.append(entry.getKey()).append("^")
						  .append(refund.getSum()).append("^")
						  .append(refund.getRemark()).append("#");
				serials.append(entry.getKey()).append(",");
				refund.setBatchNo(batch_no); // 退款批次号
				updateRefund = updateRefund && refundService.updateRefund(refund);
			}
		}
		sParaTemp.put("detail_data",detailData.substring(0, detailData.length() - 1));
		if (updateRefund) {
			return AlipaySubmit.buildRequest(sParaTemp, "POST", "确认", paySource);
		}
		return null;
	}

}
