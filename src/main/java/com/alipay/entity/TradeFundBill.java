package com.alipay.entity;

public class TradeFundBill {
	private String fund_channel;
	private String amount;
	public String getFund_channel() {
		return fund_channel;
	}
	public String getAmount() {
		return amount;
	}
	public String toString(){
		return "<TradeFundBill><amount>"+amount+"</amount><fund_channel>"+fund_channel+"</fund_channel></TradeFundBill>";
	}
}
