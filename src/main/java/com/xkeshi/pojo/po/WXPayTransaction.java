package com.xkeshi.pojo.po;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Table;

 /**
  * 
  * @author xk
  * 微信支付
  */
@Table(name = "wxpay_transaction")
public class WXPayTransaction extends Base {
     
	private static final long serialVersionUID = -7353455174311343297L;
    @Column(name = "order_number")
    private String orderNumber;
    @Column(name = "third_order_code")
    private String thirdOrderCode;
    @Column(name = "prepaid_card_charge_order_code")
    private String prepaidCardChargeOrderCode;
    @Column(name = "amount")
    private BigDecimal amount;
    
	public String getOrderNumber() {
		return orderNumber;
	}
	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}
	public String getThirdOrderCode() {
		return thirdOrderCode;
	}
	public void setThirdOrderCode(String thirdOrderCode) {
		this.thirdOrderCode = thirdOrderCode;
	}
	public String getPrepaidCardChargeOrderCode() {
		return prepaidCardChargeOrderCode;
	}
	public void setPrepaidCardChargeOrderCode(String prepaidCardChargeOrderCode) {
		this.prepaidCardChargeOrderCode = prepaidCardChargeOrderCode;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
 
}
