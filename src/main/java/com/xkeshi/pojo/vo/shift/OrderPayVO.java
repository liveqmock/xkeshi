package com.xkeshi.pojo.vo.shift;


/**
 * 
 * @author xk
 *  订单支付(爱客仕系统内部订单、预付卡充值订单、)
 */
public class OrderPayVO {

	/*支付宝支付*/
	private AlipayTransactionVO  alipayTransaction;
	
	/*微信支付*/
	private WXPayTransactionVO  wxPayTransaction ;
	
	/*现金支付*/
	private CashPayTransactionVO   cashPayTransaction; 
	
	/*预付卡支付*/
	private PrepaidCardPayTransactionVO prepaidCardPayTransaction;
	
	/*pos银行卡刷卡*/	
	private POSPayTransactionVO  posPayTransaction ;

	/*银行卡NFC电子现金*/
	private BankNFCPayTransactionVO bankNFCPayTransaction;
	
	
	public OrderPayVO() {
		super();
	}

	public WXPayTransactionVO getWxPayTransaction() {
		return wxPayTransaction;
	}

	public void setWxPayTransaction(WXPayTransactionVO wxPayTransaction) {
		this.wxPayTransaction = wxPayTransaction;
	}

	public BankNFCPayTransactionVO getBankNFCPayTransaction() {
		return bankNFCPayTransaction;
	}

	public void setBankNFCPayTransaction(
			BankNFCPayTransactionVO bankNFCPayTransaction) {
		this.bankNFCPayTransaction = bankNFCPayTransaction;
	}

	public AlipayTransactionVO getAlipayTransaction() {
		return alipayTransaction;
	}

	public void setAlipayTransaction(AlipayTransactionVO alipayTransaction) {
		this.alipayTransaction = alipayTransaction;
	}

	public CashPayTransactionVO getCashPayTransaction() {
		return cashPayTransaction;
	}

	public void setCashPayTransaction(CashPayTransactionVO cashPayTransaction) {
		this.cashPayTransaction = cashPayTransaction;
	}

	public PrepaidCardPayTransactionVO getPrepaidCardPayTransaction() {
		return prepaidCardPayTransaction;
	}

	public void setPrepaidCardPayTransaction(
			PrepaidCardPayTransactionVO prepaidCardPayTransaction) {
		this.prepaidCardPayTransaction = prepaidCardPayTransaction;
	}

	public POSPayTransactionVO getPosPayTransaction() {
		return posPayTransaction;
	}

	public void setPosPayTransaction(POSPayTransactionVO posPayTransaction) {
		this.posPayTransaction = posPayTransaction;
	}

	 
	 
	
	
}
