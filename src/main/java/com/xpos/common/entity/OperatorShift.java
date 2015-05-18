package com.xpos.common.entity;

import javax.persistence.Column;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 
 * @author XK
 * 操作员交接班的记录
 */
@Table(name= "operator_shift")
public class OperatorShift {
	
	
	@Column(name = "id")
	private Long id  ;
	
	/**操作员id*/
	@Column(name="operator_id")
	private Long operatorId ; 
	
	/**操作员的真实姓名*/
	@Column(name="operator_real_name")
	private String operatorRealName ; 
	
	/**操作员当班会话*/
	@Column(name="operator_session_code")
	private String operatorSessionCode ;
	
	/**商户的id*/
	@Column(name="shop_id")
	private  Long shopId ;
	
	/**点单总数*/
	@Column(name="total_order_count")
	private Integer totalOrderCount ;
	
	/**总销售量*/
	@Column(name="total_order_item_count")
	private Integer  totalOrderItemCount ;
	
	/**核销电子券总数*/
	@Column(name="total_consume_count")
	private Integer  totalConsumeCount;
	
	/**新增会员数*/
	@Column(name="total_member_count")
	private Integer totalMemberCount ;
	
	/**点单总金额*/
	@Column(name="total_order_amount")
	private BigDecimal  totalOrderAmount;

	/**实收现金(金额由操作员在确认交接班前，输入)*/
	@Column(name="total_actually_amount")
	private BigDecimal totalActuallyAmount ;
	
	/**应收金额*/
	@Column(name="total_receivable_amount")
	private BigDecimal  totalReceivableAmount;
	
	/**实收现金差额*/
	@Column(name= "total_difference_cash_amount")
	private BigDecimal  totalDifferenceCashAmount;
	
	/**核销实体券金额*/
	@Column(name="total_physical_coupon_amount")
	private BigDecimal  totalPhysicalCouponAmount ;
	
	/**交接班开始时间*/
	@Column(name="shifted_start_time")
	private Date shiftedStartTime  ; 
	
	/**交接班结束时间*/
	@Column(name="shifted_end_time")
	private Date shiftedEndTime ;

	@Column(name="created_time")
	private Date createdTime  ;
	
	@Column(name="comment")
	private String comment;
	
	@Column(name="status")
	private Integer  status ;

	//预付赠送总额
	private BigDecimal prepaidcardTotalPresentedAmount            ;
	//预付卡实充金额
	private BigDecimal prepaidcardtotalRealityRechargeAmount      ;
	//预付卡充值次数
	private int prepaidcardRechargeAmountCount                    ;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getOperatorId() {
		return operatorId;
	}

	public void setOperatorId(Long operatorId) {
		this.operatorId = operatorId;
	}

	public String getOperatorRealName() {
		return operatorRealName;
	}

	public void setOperatorRealName(String operatorRealName) {
		this.operatorRealName = operatorRealName;
	}

	public String getOperatorSessionCode() {
		return operatorSessionCode;
	}

	public void setOperatorSessionCode(String operatorSessionCode) {
		this.operatorSessionCode = operatorSessionCode;
	}

	public Integer getTotalOrderCount() {
		return totalOrderCount;
	}

	public void setTotalOrderCount(Integer totalOrderCount) {
		this.totalOrderCount = totalOrderCount;
	}

	public Integer getTotalOrderItemCount() {
		return totalOrderItemCount;
	}

	public void setTotalOrderItemCount(Integer totalOrderItemCount) {
		this.totalOrderItemCount = totalOrderItemCount;
	}

	public BigDecimal getTotalDifferenceCashAmount() {
		return totalDifferenceCashAmount;
	}

	public void setTotalDifferenceCashAmount(BigDecimal totalDifferenceCashAmount) {
		this.totalDifferenceCashAmount = totalDifferenceCashAmount;
	}

	public Integer getTotalConsumeCount() {
		return totalConsumeCount;
	}

	public void setTotalConsumeCount(Integer totalConsumeCount) {
		this.totalConsumeCount = totalConsumeCount;
	}

	public Integer getTotalMemberCount() {
		return totalMemberCount;
	}

	public void setTotalMemberCount(Integer totalMemberCount) {
		this.totalMemberCount = totalMemberCount;
	}

	public BigDecimal getTotalOrderAmount() {
		return totalOrderAmount;
	}

	public void setTotalOrderAmount(BigDecimal totalOrderAmount) {
		this.totalOrderAmount = totalOrderAmount;
	}

	public BigDecimal getTotalActuallyAmount() {
		return totalActuallyAmount;
	}

	public void setTotalActuallyAmount(BigDecimal totalActuallyAmount) {
		this.totalActuallyAmount = totalActuallyAmount;
	}

	public Date getShiftedStartTime() {
		return shiftedStartTime;
	}

	public void setShiftedStartTime(Date shiftedStartTime) {
		this.shiftedStartTime = shiftedStartTime;
	}

	public Date getShiftedEndTime() {
		return shiftedEndTime;
	}

	public void setShiftedEndTime(Date shiftedEndTime) {
		this.shiftedEndTime = shiftedEndTime;
	}

	public Date getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}


	public Long getShopId() {
		return shopId;
	}

	public void setShopId(Long shopId) {
		this.shopId = shopId;
	}

	public BigDecimal getTotalReceivableAmount() {
		return totalReceivableAmount;
	}

	public void setTotalReceivableAmount(BigDecimal totalReceivableAmount) {
		this.totalReceivableAmount = totalReceivableAmount;
	}

	public BigDecimal getTotalPhysicalCouponAmount() {
		return totalPhysicalCouponAmount;
	}

	public void setTotalPhysicalCouponAmount(BigDecimal totalPhysicalCouponAmount) {
		this.totalPhysicalCouponAmount = totalPhysicalCouponAmount;
	}

	public BigDecimal getPrepaidcardTotalPresentedAmount() {
		return prepaidcardTotalPresentedAmount;
	}

	public void setPrepaidcardTotalPresentedAmount(BigDecimal prepaidcardTotalPresentedAmount) {
		this.prepaidcardTotalPresentedAmount = prepaidcardTotalPresentedAmount;
	}

	public BigDecimal getPrepaidcardtotalRealityRechargeAmount() {
		return prepaidcardtotalRealityRechargeAmount;
	}

	public void setPrepaidcardtotalRealityRechargeAmount(BigDecimal prepaidcardtotalRealityRechargeAmount) {
		this.prepaidcardtotalRealityRechargeAmount = prepaidcardtotalRealityRechargeAmount;
	}

	public int getPrepaidcardRechargeAmountCount() {
		return prepaidcardRechargeAmountCount;
	}

	public void setPrepaidcardRechargeAmountCount(int prepaidcardRechargeAmountCount) {
		this.prepaidcardRechargeAmountCount = prepaidcardRechargeAmountCount;
	}
}
