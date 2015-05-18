package com.xkeshi.pojo.vo.shift;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 
 * @author XK
 * 操作员交接班的记录
 */
public class OperatorShiftVO {
	
	private Long id  ;
	
	/**操作员id*/
	private Long operatorId ; 
	
	/**操作员的真实姓名*/
	private String operatorRealName ; 
	
	/**操作员当班会话*/
	private String operatorSessionCode ;
	
	/**商户的id*/
	private  Integer shopId ;
	
	private String  shopName  ;
	
	/**点单总数*/
	private Integer totalOrderCount ;
	
	/**总销售量*/
	private Integer  totalOrderItemCount ;
	
	/**核销电子券总数*/
	private Integer  totalConsumeCount;
	
	/**新增会员数*/
	private Integer totalMemberCount ;
	
	/**点单总金额*/
	private BigDecimal  totalOrderAmount;

	/**实收现金(金额由操作员在确认交接班前，输入)*/
	private BigDecimal totalActuallyAmount ;
	
	/**应收金额*/
	private BigDecimal  totalReceivableAmount;
	
	/**实收现金差额*/
	private BigDecimal  totalDifferenceCashAmount;
	
	/**核销实体券金额*/
	private BigDecimal  totalPhysicalCouponAmount ;
	
	/**交接班开始时间*/
	private Date shiftedStartTime  ; 
	
	/**交接班结束时间*/
	private Date shiftedEndTime ;

	private Date createdTime  ;
	
	private String comment;
	
	private Integer  status ;

	//预付赠送总额
	private BigDecimal prepaidcardTotalPresentedAmount            ;
	//预付卡实充金额
	private BigDecimal prepaidcardtotalRealityRechargeAmount      ;
	//预付卡充值次数
	private int prepaidcardRechargeAmountCount                    ;


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

	public Integer getShopId() {
		return shopId;
	}

	public void setShopId(Integer shopId) {
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

	public String getShopName() {
		return shopName;
	}

	public void setShopName(String shopName) {
		this.shopName = shopName;
	}
	
	
}
