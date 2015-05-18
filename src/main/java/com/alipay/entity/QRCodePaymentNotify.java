package com.alipay.entity;

import java.math.BigDecimal;
import java.util.Date;

public class QRCodePaymentNotify {
	private Date notify_time; //通知时间
	private String notify_type; //通知类型
	private String notify_id;
	private String sign_type;
	private String sign;
	private QRCodePaymentNotifyActionType notify_action_type; //通知动作类型
	private String out_trade_no;
	private String subject;
	private String trade_no; //支付宝交易号
	private QRCodePaymentNotifyTradeStatus trade_status;
	private Date gmt_create; //交易创建时间
	private Date gmt_payment; //交易付款时间
	private String seller_email; //买家支付宝账号
	private String buyer_email; //卖家支付宝账号
	private String seller_id; //卖家支付宝用户号
	private String buyer_id; //买家支付宝用户号
	private BigDecimal price;
	private int quantity;
	private BigDecimal total_fee;
	private String body;
	private BigDecimal refund_fee;
	private String out_biz_no; //商户业务号(商户业务ID，主要是退款通知中返回退款申请的流水号)
	private String paytools_pay_amount; //支付成功的各个渠道金额信息。JSON格式，暂时只记录，不处理
	
	public enum QRCodePaymentNotifyActionType{
		createDirectPayTradeByBuyerAction, //创建
		payByAccountAction, //支付
		refundFPAction, //退款
		reverseAction, //撤销
		closeTradeAction, //关闭
		finishFPAction; //交易完成
	}
	
	public enum QRCodePaymentNotifyTradeStatus{
		WAIT_BUYER_PAY,  //交易创建，等待买家付款。
		TRADE_CLOSED, // 1.在指定时间段内未支付时关闭的交易；2.在交易完成全额退款成功时关闭的交易。
		TRADE_SUCCESS, //交易成功，且可对该交易做操作，如：多级分润、退款等。
		TRADE_PENDING, //等待卖家收款（买家付款后，如果卖家账号被冻结）。
		TRADE_FINISHED; //交易成功且结束，即不可再做任何操作
	}

	public Date getNotify_time() {
		return notify_time;
	}
	public void setNotify_time(Date notify_time) {
		this.notify_time = notify_time;
	}
	public String getNotify_type() {
		return notify_type;
	}
	public void setNotify_type(String notify_type) {
		this.notify_type = notify_type;
	}
	public String getNotify_id() {
		return notify_id;
	}
	public void setNotify_id(String notify_id) {
		this.notify_id = notify_id;
	}
	public String getSign_type() {
		return sign_type;
	}
	public void setSign_type(String sign_type) {
		this.sign_type = sign_type;
	}
	public String getSign() {
		return sign;
	}
	public void setSign(String sign) {
		this.sign = sign;
	}
	public QRCodePaymentNotifyActionType getNotify_action_type() {
		return notify_action_type;
	}
	public void setNotify_action_type(
			QRCodePaymentNotifyActionType notify_action_type) {
		this.notify_action_type = notify_action_type;
	}
	public String getOut_trade_no() {
		return out_trade_no;
	}
	public void setOut_trade_no(String out_trade_no) {
		this.out_trade_no = out_trade_no;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getTrade_no() {
		return trade_no;
	}
	public void setTrade_no(String trade_no) {
		this.trade_no = trade_no;
	}
	public QRCodePaymentNotifyTradeStatus getTrade_status() {
		return trade_status;
	}
	public void setTrade_status(QRCodePaymentNotifyTradeStatus trade_status) {
		this.trade_status = trade_status;
	}
	public Date getGmt_create() {
		return gmt_create;
	}
	public void setGmt_create(Date gmt_create) {
		this.gmt_create = gmt_create;
	}
	public Date getGmt_payment() {
		return gmt_payment;
	}
	public void setGmt_payment(Date gmt_payment) {
		this.gmt_payment = gmt_payment;
	}
	public String getSeller_email() {
		return seller_email;
	}
	public void setSeller_email(String seller_email) {
		this.seller_email = seller_email;
	}
	public String getBuyer_email() {
		return buyer_email;
	}
	public void setBuyer_email(String buyer_email) {
		this.buyer_email = buyer_email;
	}
	public String getSeller_id() {
		return seller_id;
	}
	public void setSeller_id(String seller_id) {
		this.seller_id = seller_id;
	}
	public String getBuyer_id() {
		return buyer_id;
	}
	public void setBuyer_id(String buyer_id) {
		this.buyer_id = buyer_id;
	}
	public BigDecimal getPrice() {
		return price;
	}
	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	public BigDecimal getTotal_fee() {
		return total_fee;
	}
	public void setTotal_fee(BigDecimal total_fee) {
		this.total_fee = total_fee;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public BigDecimal getRefund_fee() {
		return refund_fee;
	}
	public void setRefund_fee(BigDecimal refund_fee) {
		this.refund_fee = refund_fee;
	}
	public String getOut_biz_no() {
		return out_biz_no;
	}
	public void setOut_biz_no(String out_biz_no) {
		this.out_biz_no = out_biz_no;
	}
	public String getPaytools_pay_amount() {
		return paytools_pay_amount;
	}
	public void setPaytools_pay_amount(String paytools_pay_amount) {
		this.paytools_pay_amount = paytools_pay_amount;
	}
	
}
