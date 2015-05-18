package com.xkeshi.pojo.vo.shift;

import java.math.BigDecimal;

/**
 * 
 * @author xk
 * 会员折扣
 */
public class MemberDiscountVO {

	/*会员总折扣*/
	private BigDecimal  totalAmount ;

	
	public MemberDiscountVO() {
		super();
	}

	public MemberDiscountVO(BigDecimal totalAmount) {
		super();
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
	
}
