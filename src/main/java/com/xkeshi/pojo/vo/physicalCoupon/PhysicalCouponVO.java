package com.xkeshi.pojo.vo.physicalCoupon;

import java.math.BigDecimal;

/**
 * @author snoopy
 *
 */
public class PhysicalCouponVO{
	
	private long id;
	
	private String name;
	
	private BigDecimal amount;
	
	public PhysicalCouponVO(){
		
	}
	
	public PhysicalCouponVO(long id , String name ,BigDecimal amount){
		this.id = id;
		this.name = name;
		this.amount = amount;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}


}