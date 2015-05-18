package com.xkeshi.pojo.vo;

import java.util.List;

/**
 * 通过订单号编号查看支付流水
 */
public class OrderTransactionListVO {
	
    private List<OrderTransactionVO> transactionList;

	public List<OrderTransactionVO> getTransactionList() {
		return transactionList;
	}

	public void setTransactionList(List<OrderTransactionVO> transactionList) {
		this.transactionList = transactionList;
	}
  
    
   
    
}
