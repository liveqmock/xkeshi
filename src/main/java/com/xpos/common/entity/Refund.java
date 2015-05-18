package com.xpos.common.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;

import org.springframework.format.annotation.DateTimeFormat;

import com.xpos.common.entity.face.EncryptId;
import com.xpos.common.entity.face.Business.BusinessType;
import com.xpos.common.entity.security.User;

/**
 * 优惠券退款
 */
public class Refund extends BaseEntity implements EncryptId{

	private static final long serialVersionUID = -4583784805576306594L;
	
	@Column
	private Coupon coupon;
	@Column
	private CouponPayment payment;
	@Column
	private User user;
	@Column
	private BigDecimal sum; //退款金额
	@Column
	private String code; //内部退款序号
	@Column
	private String batchNo ; //退款批次号
	@Column
	private String serial;//外部系统退款流水号
	@Column
	private RefundStatus status; //退款状态
	@Column
	private RefundAccountType type; //退款账户类型
	@Column
	private String account; //退款账号（银行卡号或第三方平台账号）
	@Column
	private String responseCode; //交易返回码
	@Column
	private String remark;    //退款理由
	@Column
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date tradeDate;
	@Column
	private Long businessId;
	@Column
	private BusinessType businessType;
	
	public enum RefundStatus{
		REJECTED(-1), //拒绝
		APPLY(1),//待审核
		ACCEPTED(2), //审核通过
		AUTO_EXECUTE(3),//自动退款中
		MANUAL_EXECUTE(4),//人工退款中
		AUTO_SUCCESS(5),//自动退款成功
		AUTO_FAILED(6),//自动退款失败
		MANUAL_SUCCESS(7), //人工退款成功
		MANUAL_FAILED(8); //人工退款失败
		
		private int state;
		RefundStatus(int state){
			this.state = state;
		}
		
		public int getState() {
			return state;
		}
		
		public static final RefundStatus queryByState(int state){
			for(RefundStatus status : RefundStatus.values()){
				if(status.getState() == state){
					return status;
				}
			}
			return null;
		}
		
	}
	
	public enum RefundAccountType{
		DEBIT_CARD(1), //银行卡
		CREDIT_CARD(2), //信用卡
		ALIPAY(3), //支付宝
		TENPAY(4); //财付通
		
		private int type;
		
		RefundAccountType(int type){
			this.type = type;
		}
		
		public int getType() {
			return type;
		}
	}

	public String getBatchNo() {
		return batchNo;
	}

	public void setBatchNo(String batchNo) {
		this.batchNo = batchNo;
	}

	public Coupon getCoupon() {
		return coupon;
	}

	public void setCoupon(Coupon coupon) {
		this.coupon = coupon;
	}

	public CouponPayment getPayment() {
		return payment;
	}

	public void setPayment(CouponPayment payment) {
		this.payment = payment;
	}

	public User getUser() {
		return user;
	}
	
	public void setUser(User user) {
		this.user = user;
	}
	
	public BigDecimal getSum() {
		return sum;
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
	
	public RefundStatus getStatus() {
		return status;
	}
	
	public void setStatus(RefundStatus status) {
		this.status = status;
	}
	
	public RefundAccountType getType() {
		return type;
	}
	
	public void setType(RefundAccountType type) {
		this.type = type;
	}

	public String getAccount() {
		return account;
	}
	
	public void setAccount(String account) {
		this.account = account;
	}
	
	public String getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Date getTradeDate() {
		return tradeDate;
	}

	public void setTradeDate(Date tradeDate) {
		this.tradeDate = tradeDate;
	}
	
	public Long getBusinessId() {
		return businessId;
	}

	public void setBusinessId(Long businessId) {
		this.businessId = businessId;
	}

	public BusinessType getBusinessType() {
		return businessType;
	}

	public void setBusinessType(BusinessType businessType) {
		this.businessType = businessType;
	}


}
