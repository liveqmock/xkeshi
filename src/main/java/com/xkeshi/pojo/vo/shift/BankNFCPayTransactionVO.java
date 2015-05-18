package com.xkeshi.pojo.vo.shift;

import java.math.BigDecimal;
/**
 * 
 * @author xk
 * 银行NFC刷卡
 */
public class BankNFCPayTransactionVO {

	/*支付总金额*/
	private BigDecimal  totalAmount ;

	public BankNFCPayTransactionVO() {
		super();
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
