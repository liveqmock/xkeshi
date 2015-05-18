package com.alipay.entity;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

public class QRCodeRefundXML {
	private String request;
	private String responseStr;
	private QRCodeRefundResponse response;
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
	public QRCodeRefundResponse getResponse() {
		return response;
	}
	public boolean isSuccess() {
		isSuccess = StringUtils.equalsIgnoreCase(is_success, "T");
		return isSuccess;
	}
	public String getError() {
		return error;
	}
	public String getSign() {
		return sign;
	}
	public String getSign_type() {
		return sign_type;
	}

	public static class QRCodeRefundResponse{
		private QRCodeRefundResultCode result_code;
		private String trade_no;
		private String out_trade_no;
		private String buyer_user_id;
		private String buyer_logon_id;
		private String fund_change;
		private QRCodeRefundErrorCode detail_error_code;
		private String detail_error_des;
		private String display_message;
		
		public enum QRCodeRefundResultCode{
			SUCCESS, //退款成功
			FAIL, //退款失败
			UNKNOWN; //结果未知
		}
		
		public enum QRCodeRefundErrorCode{
			INVALID_PARAMETER,  //参数无效
			TRADE_ROLE_ERROR,  //没有该笔交易的退款或撤销权限
			DISCORDANT_REPEAT_REQUEST,  //同一笔退款或撤销单号金额不一致
			TRADE_HAS_CLOSE,  //交易已经关闭
			REASON_TRADE_BEEN_FREEZEN,  //交易已经被冻结
			BUYER_ERROR,  //买家不存在
			SELLER_ERROR,  //卖家不存在
			TRADE_NOT_EXIST,  //交易不存在
			TRADE_STATUS_ERROR,  //交易状态不合法
			TRADE_HAS_FINISHED,  //交易已结束
			REFUND_AMT_NOT_EQUAL_TOTAL;  //撤销或退款金额与订单金额不一致
		}
		
		public QRCodeRefundResultCode getResult_code() {
			return result_code;
		}
		public String getTrade_no() {
			return trade_no;
		}
		public String getOut_trade_no() {
			return out_trade_no;
		}
		public QRCodeRefundErrorCode getDetail_error_code() {
			return detail_error_code;
		}
		public String getDetail_error_des() {
			return detail_error_des;
		}
		public String getBuyer_user_id() {
			return buyer_user_id;
		}
		public String getBuyer_logon_id() {
			return buyer_logon_id;
		}
		public String getFund_change() {
			return fund_change;
		}
		public String getDisplay_message() {
			return display_message;
		}
	}
	
	public Map<String, String> getResponseParam() {
		Map<String, String> param = new HashMap<>();
		if(StringUtils.isNotBlank(responseStr)){
			String[] kvs = StringUtils.split(responseStr, "##");
			for(String str : kvs){
				String[] kv = StringUtils.split(str, "||");
				String key = kv[0];
				String value = null;
				if(kv.length == 2){
					value = kv[1];
				}
				param.put(key, value);
			}
		}
		return param;
	}
	
}
