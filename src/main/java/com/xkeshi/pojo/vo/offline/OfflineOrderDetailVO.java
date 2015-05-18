package com.xkeshi.pojo.vo.offline;

import java.math.BigDecimal;

public class OfflineOrderDetailVO {

	
	private String orderType;//订单类型
	
	private String orderNumber;//订单号
	
	private BigDecimal totalAmount;//订单原始金额
	
	private BigDecimal actuallyPaid;//实收金额
	
	private String type;//首次支付类型
	
	private Integer status;//订单状态(1:支付成功|2:未付款|3:支付失败|4:超时|5:撤销订单|6:部分支付成功|7:退款)
	
	private Long operatorId;//操作员Id
	
	private Long managerId;//退款授权店长ID
	
	private String operatorSessionCode;//交接班会话
	
	private String createdTime;//创建时间
	
	private String updatedTime;//修改时间

	public String getOrderType() {
		return orderType;
	}

	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}

	public String getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	public BigDecimal getActuallyPaid() {
		return actuallyPaid;
	}

	public void setActuallyPaid(BigDecimal actuallyPaid) {
		this.actuallyPaid = actuallyPaid;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Long getOperatorId() {
		return operatorId;
	}

	public void setOperatorId(Long operatorId) {
		this.operatorId = operatorId;
	}

	public Long getManagerId() {
		return managerId;
	}

	public void setManagerId(Long managerId) {
		this.managerId = managerId;
	}

	public String getOperatorSessionCode() {
		return operatorSessionCode;
	}

	public void setOperatorSessionCode(String operatorSessionCode) {
		this.operatorSessionCode = operatorSessionCode;
	}

	public String getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(String createdTime) {
		this.createdTime = createdTime;
	}

	public String getUpdatedTime() {
		return updatedTime;
	}

	public void setUpdatedTime(String updatedTime) {
		this.updatedTime = updatedTime;
	}
	
	
}
