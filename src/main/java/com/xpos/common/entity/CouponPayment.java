package com.xpos.common.entity;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Transient;

import org.springframework.format.annotation.DateTimeFormat;

import com.xpos.common.entity.face.EncryptId;
import com.xpos.common.entity.security.User;

/**
 * 优惠券支付流水(可与银行对账)
 */
public class CouponPayment extends BaseEntity implements EncryptId{

	private static final long serialVersionUID = -265454100675414L;
	
	@Column
	private CouponInfo couponInfo;
	@Column
	private User user;
	@Column
	private String mobile;
	@Column
	private BigDecimal sum; //订单金额
	@Column
	private String code; //内部订单Id
	@Column
	private String serial;//外部系统订单流水
	@Column
	private CouponPaymentStatus status; //订单状态
	@Column
	private CouponPaymentType type; //订单类型
	@Column
	private Integer quantity; //购买份数
	@Column
	private String cardNumber; //卡号后4位
	@Column
	private String buyerAccount; //支付账户:email/mobile
	@Column
	private String responseCode; //交易返回码
	@Column
	private String terminal; //刷卡终端号
	@Column
	private String location;
	@Column
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date tradeDate;
	@Column
	private String remark;
	@Column
	private String refNo; //交易参考号
	@Column
	private String batchNo; //交易批次号
	@Column
	private String traceNo; //交易跟踪号
	@Transient
	private String password;
	@Column
	private CouponPaymentSource source;
	
	public enum CouponPaymentStatus{
		PAID_FAIL(-1),//付款失败
		UNPAID(0), //等待付款
		PAID_SUCCESS(1),//付款成功
		PAID_REFUND(2),//退款历史成功订单
		PAID_TIMEOUT(3),//交易超时订单关闭
		PAID_REVOCATION(4);//撤销当天成功的订单
		
		private int state;
		CouponPaymentStatus(int state){
			this.state = state;
		}
		
		public int getState() {
			return state;
		}
		
		public static final CouponPaymentStatus queryByState(int state){
			for(CouponPaymentStatus status : CouponPaymentStatus.values()){
				if(status.getState() == state){
					return status;
				}
			}
			return null;
		}
		
	}
	
	public enum CouponPaymentType{
		DEBIT_CARD(1, "银行卡"), //刷银行借记卡消费
		CREDIT_CARD(2, "信用卡"), //刷信用卡消费
		PREPAID_CARD(3, "充值卡"), //充值卡消费
		MEMBER_POINT(4, "积分"), //会员积分消费
		CMCC_TICKET(5, "电子券"), //移动电子券
		WEI_XIN(6, "微信"), //微信
		ALIPAY_WAP(7, "支付宝手机网站"), //支付宝
		ALIPAY_SHORTCUT(8, "支付宝快捷"), //支付宝
		UMPAY_WAP(9, "联动优势手机网站"), //联动优势-手机支付
		UMPAY_WEB(10, "联动优势PC"), //联动优势-PC支付
		SMS_PUSH(11, "短信赠送"), //短信推送
		EXTERNAL_APPLY(12, "第三方领取"); //外部系统调用接口领取
		
		private int type;
		private String desc;
		
		CouponPaymentType(int type, String desc){
			this.type = type;
			this.desc = desc;
		}
		
		public int getType() {
			return type;
		}
		
		public String getDesc(){
			return desc;
		}
	}
	
	public enum CouponPaymentSource{
		XKESHI_WEB("爱客仕官网"), //爱客仕官网
		XKESHI_WAP("爱客仕微网站"), //爱客仕微网站
		YANGCHENGLAKE("阳澄湖"), //阳澄湖网站
		hz_daily_wx("杭州日报");//杭州日报
		private String desc;
		
		CouponPaymentSource(String desc){
			this.desc = desc;
		}
		
		public String getDesc(){
			return desc;
		}
		
	}

	public String getBuyerAccount() {
		return buyerAccount;
	}

	public void setBuyerAccount(String buyerAccount) {
		this.buyerAccount = buyerAccount;
	}

	public CouponInfo getCouponInfo() {
		return couponInfo;
	}

	public void setCouponInfo(CouponInfo couponInfo) {
		this.couponInfo = couponInfo;
	}

	public User getUser() {
		return user;
	}
	
	public void setUser(User user) {
		this.user = user;
	}
	
	public String getMobile() {
		return mobile;
	}
	
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	
	
	public BigDecimal getSum() {
		return sum != null ? sum : new BigDecimal(0);
	}

	public void setSum(BigDecimal sum) {
		this.sum = sum;
	}

	public String getCode() {
		return code;
	}
	
	public void setCode(String code) {
		this.code = code;
	}
	
	public String getSerial() {
		return serial;
	}
	
	public void setSerial(String serial) {
		this.serial = serial;
	}
	
	public CouponPaymentStatus getStatus() {
		return status;
	}
	
	public void setStatus(CouponPaymentStatus status) {
		this.status = status;
	}
	
	public CouponPaymentType getType() {
		return type;
	}
	
	public void setType(CouponPaymentType type) {
		this.type = type;
	}
	
	public CouponPaymentSource getSource() {
		return source;
	}

	public void setSource(CouponPaymentSource source) {
		this.source = source;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public String getCardNumber() {
		return cardNumber;
	}
	
	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}
	
	public String getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}

	public String getTerminal() {
		return terminal;
	}

	public void setTerminal(String terminal) {
		this.terminal = terminal;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public Date getTradeDate() {
		return tradeDate;
	}

	public void setTradeDate(Date tradeDate) {
		this.tradeDate = tradeDate;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getRefNo() {
		return refNo;
	}

	public void setRefNo(String refNo) {
		this.refNo = refNo;
	}

	public String getBatchNo() {
		return batchNo;
	}

	public void setBatchNo(String batchNo) {
		this.batchNo = batchNo;
	}

	public String getTraceNo() {
		return traceNo;
	}

	public void setTraceNo(String traceNo) {
		this.traceNo = traceNo;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	/** 计算每张优惠券实际付款的均价 */
	public BigDecimal getAvaragePrice(){
		return getSum().divide(new BigDecimal(getQuantity()), 2, RoundingMode.HALF_UP);
	}
}
