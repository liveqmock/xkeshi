package com.xkeshi.pojo.vo.shift;

import java.math.BigDecimal;
/**
 * 
 * @author xk
 * 微信支付
 */
public class WXPayTransactionVO {

	/*支付总金额*/
	private BigDecimal  totalAmount ;

	
	public WXPayTransactionVO() {
		super();
	}

	public WXPayTransactionVO(BigDecimal totalAmount) {
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
