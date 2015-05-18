package com.alipay.entity;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

public class QRCodePaymentXML {
	private String request;
	private String responseStr;
	private QRCodePaymentResponse response;
	private String is_success;
	private String error;
	private boolean isSuccess;
	private String sign;
	private String sign_type;
	
	public String getRequest(){
		return request;
	}
	public String getResponseStr() {
		return responseStr;
	}
	public QRCodePaymentResponse getResponse() {
		return response;
	}
	public boolean isSuccess() {
		isSuccess = StringUtils.equalsIgnoreCase(is_success, "T");
		return isSuccess;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	public String getSign() {
		return sign;
	}
	public void setSign(String sign) {
		this.sign = sign;
	}
	public String getSign_type() {
		return sign_type;
	}
	public void setSign_type(String sign_type) {
		this.sign_type = sign_type;
	}
	public void setResponse(QRCodePaymentResponse response) {
		this.response = response;
	}

	public static class QRCodePaymentResponse{
		private QRCodePaymentResultCode result_code;
		private String trade_no;
		private String out_trade_no;
		private String buyer_user_id;
		private String buyer_logon_id;
		private QRCodePaymentErrorCode detail_error_code;
		private String detail_error_des;
		private String extend_info;
		private List<TradeFundBill> fund_bill_list;
		private String coupon_list;
		private BigDecimal total_fee;
		private String gmt_payment;
		
		public enum QRCodePaymentResultCode{
			ORDER_FAIL, //下单失败
			ORDER_SUCCESS_PAY_SUCCESS, //下单成功并且支付成功
			ORDER_SUCCESS_PAY_FAIL, //下单成功支付失败
			ORDER_SUCCESS_PAY_INPROCESS, //下单成功支付处理中
			UNKNOWN; //处理结果未知
		}
		
		public enum QRCodePaymentErrorCode{
			TRADE_SETTLE_ERROR,  //分账信息校验失败
			TRADE_BUYER_NOT_MATCH,  //交易买家不匹配
			CONTEXT_INCONSISTENT,  //交易信息被篡改
			TRADE_HAS_SUCCESS,  //交易已经支付
			TRADE_HAS_CLOSE,  //交易已经关闭
			REASON_ILLEGAL_STATUS,  //交易的状态不合法
			EXIST_FORBIDDEN_WORD,  //订单信息中包含违禁词
			PARTNER_ERROR,  //合作伙伴信息不正确
			ACCESS_FORBIDDEN,  //没有权限使用该产品 
			SELLER_NOT_EXIST,  //卖家不存在
			BUYER_NOT_EXIST,  //买家不存在
			BUYER_ENABLE_STATUS_FORBID,  //买家状态非法，无法继续交易
			BUYER_SELLER_EQUAL,  //卖家买家账号相同，不能进行交易
			INVALID_PARAMETER,  //参数无效
			UN_SUPPORT_BIZ_TYPE,  //不支持的业务类型
			INVALID_RECEIVE_ACCOUNT,  //卖家不在设置的收款账户列表之中
			BUYER_PAYMENT_AMOUNT_DAY_LIMIT_ERROR,  //买家的付款日限额超限
			ERROR_BUYER_CERTIFY_LEVEL_LIMIT,  //买家未通过人行人证
			ERROR_SELLER_CERTIFY_LEVEL_LIMIT,  //卖家未通过人行认证
			CLIENT_VERSION_NOT_MATCH,  //钱包版本过低，请升级到最新版本后使用
			PULL_MOBILE_CASHIER_FAIL,  //唤起无线快捷收银台失败
			SOUNDWAVE_PARSER_FAIL, //动态ID解析失败
			AUTH_NO_ERROR;
		}

		
		public QRCodePaymentResultCode getResult_code() {
			return result_code;
		}

		public String getTrade_no() {
			return trade_no;
		}

		public String getOut_trade_no() {
			return out_trade_no;
		}

		public String getBuyer_user_id() {
			return buyer_user_id;
		}

		public String getBuyer_logon_id() {
			return buyer_logon_id;
		}

		public QRCodePaymentErrorCode getDetail_error_code() {
			return detail_error_code;
		}

		public String getDetail_error_des() {
			return detail_error_des;
		}

		public String getExtend_info() {
			return extend_info;
		}

		public List<TradeFundBill> getFund_bill_list() {
			return fund_bill_list;
		}

		public String getCoupon_list() {
			return coupon_list;
		}

		public BigDecimal getTotal_fee() {
			return total_fee;
		}

		public String getGmt_payment() {
			return gmt_payment;
		}

	}
	
	public Map<String, String> getResponseParam() {
		Map<String, String> param = new HashMap<>();
		if(response != null){
			for(Field field : response.getClass().getDeclaredFields()){
				Object object;
				try {
					field.setAccessible(true);
					object = field.get(response);
					if(object != null){
						if(StringUtils.equals("fund_bill_list", field.getName())){
							StringBuffer sBuffer = new StringBuffer("<fund_bill_list>");
							@SuppressWarnings("unchecked")
							List<TradeFundBill> list = (List<TradeFundBill>)object;
							for(TradeFundBill tfb : list){
								sBuffer.append(tfb.toString());
							}
							sBuffer.append("</fund_bill_list>");
							param.put(field.getName(), sBuffer.toString());
						}else{
							param.put(field.getName(), object.toString());
						}
					}
				} catch (IllegalArgumentException | IllegalAccessException e) {
				}
			}
		}
		return param;
	}
	
}
