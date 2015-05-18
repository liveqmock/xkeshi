package com.alipay.entity;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

public class QRCodeQueryXML {
	private String request;
	private String responseStr;
	private QRCodeQueryResponse response;
	private String is_success;
	private String error;
	private boolean isSuccess;
	private String sign;
	private String sign_type;
	
	public String getRequest(){
		return request;
	}
	public String getResponseStr(){
		return responseStr;
	}
	public QRCodeQueryResponse getResponse() {
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
	public void setResponse(QRCodeQueryResponse response) {
		this.response = response;
	}

	public static class QRCodeQueryResponse{
		private QRCodeQueryResultCode result_code;
		private String trade_no;
		private String out_trade_no;
		private String buyer_user_id;
		private String buyer_logon_id;
		private String partner;
		private QRCodeQueryTradeStatus trade_status;
		private QRCodeQueryErrorCode detail_error_code;
		private String detail_error_des;
		private List<TradeFundBill> fund_bill_list;
		private BigDecimal total_fee;
		private String send_pay_date;
		
		public enum QRCodeQueryResultCode{
			SUCCESS, //查询成功
			FAIL, //查询失败
			PROCESS_EXCEPTION; //处理异常
		}
		
		public enum QRCodeQueryErrorCode{
			INVALID_PARAMETER, //参数无效
			TRADE_NOT_EXIST; //交易不存在
		}
		
		public enum QRCodeQueryTradeStatus{
			WAIT_BUYER_PAY, //交易创建，等待买家付款。
			TRADE_CLOSED, //1.在指定时间段内未支付时关闭的交易； 2.在交易完成全额退款成功时关闭的交易。
			TRADE_SUCCESS, //交易成功，且可对该交易做操作，如：多级分润、退款等。
			TRADE_PENDING, //等待卖家收款（买家付款后，如果卖家账号被冻结）。
			TRADE_FINISHED, //交易成功且结束，即不可再做任何操作。
		}

		public QRCodeQueryResultCode getResult_code() {
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
		public String getPartner() {
			return partner;
		}
		public QRCodeQueryTradeStatus getTrade_status() {
			return trade_status;
		}
		public QRCodeQueryErrorCode getDetail_error_code() {
			return detail_error_code;
		}
		public String getDetail_error_des() {
			return detail_error_des;
		}
		public List<TradeFundBill> getFund_bill_list() {
			return fund_bill_list;
		}
		public BigDecimal getTotal_fee() {
			return total_fee;
		}
		public String getSend_pay_date() {
			return send_pay_date;
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
