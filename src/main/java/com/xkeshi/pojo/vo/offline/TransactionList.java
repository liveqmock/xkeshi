package com.xkeshi.pojo.vo.offline;

import java.util.List;

public class TransactionList {
	
	private List<CashTransactionDetailVO> cashTransaction;
	private List<PrepaidCardTransactionDetailVO> prepaidCardTransaction;
	private List<POSTransactionDetailVO> posTransaction;
	private List<NFCTransactionDetailVO> nfcTransaction;
	private List<AlipayTransactionDetailVO> alipayTransaction;
	private List<WxpayTransactionDetailVO> wxpayTransaction;
	
	public List<CashTransactionDetailVO> getCashTransaction() {
		return cashTransaction;
	}
	public void setCashTransaction(List<CashTransactionDetailVO> cashTransaction) {
		this.cashTransaction = cashTransaction;
	}
	public List<PrepaidCardTransactionDetailVO> getPrepaidCardTransaction() {
		return prepaidCardTransaction;
	}
	public void setPrepaidCardTransaction(
			List<PrepaidCardTransactionDetailVO> prepaidCardTransaction) {
		this.prepaidCardTransaction = prepaidCardTransaction;
	}
	public List<POSTransactionDetailVO> getPosTransaction() {
		return posTransaction;
	}
	public void setPosTransaction(List<POSTransactionDetailVO> posTransaction) {
		this.posTransaction = posTransaction;
	}
	public List<NFCTransactionDetailVO> getNfcTransaction() {
		return nfcTransaction;
	}
	public void setNfcTransaction(List<NFCTransactionDetailVO> nfcTransaction) {
		this.nfcTransaction = nfcTransaction;
	}
	public List<AlipayTransactionDetailVO> getAlipayTransaction() {
		return alipayTransaction;
	}
	public void setAlipayTransaction(
			List<AlipayTransactionDetailVO> alipayTransaction) {
		this.alipayTransaction = alipayTransaction;
	}
	public List<WxpayTransactionDetailVO> getWxpayTransaction() {
		return wxpayTransaction;
	}
	public void setWxpayTransaction(List<WxpayTransactionDetailVO> wxpayTransaction) {
		this.wxpayTransaction = wxpayTransaction;
	}
	
}
