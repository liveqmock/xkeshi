package com.xkeshi.pojo.vo.shift;

import java.math.BigDecimal;

/**
 * 
 * @author xk
 * 实体券
 */
public class OrderPhysicalCouponVO {

	/*实体券的ID*/
	private Long  id  ;
	
	/*实体券的名称*/
	private String name ;
	
	/*实体券的总金额*/
	private BigDecimal  totalAmount ;

	/*实体券的面额*/
	private BigDecimal  amount ;
	
	/*实体券的核销总数*/
	private Integer   totalCount  ;
	
	public OrderPhysicalCouponVO() {
		super();
	}

	public OrderPhysicalCouponVO(String name, BigDecimal totalAmount) {
		super();
		this.name = name;
		this.totalAmount = totalAmount;
	}

	public BigDecimal getTotalAmount() {
		if (totalAmount != null) {
			return totalAmount.setScale(2,BigDecimal.ROUND_DOWN);
		}
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public BigDecimal getAmount() {
		return amount == null ?  null : amount.setScale(2, BigDecimal.ROUND_DOWN);
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public Integer getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(Integer totalCount) {
		this.totalCount = totalCount;
	}

	 
	
}
