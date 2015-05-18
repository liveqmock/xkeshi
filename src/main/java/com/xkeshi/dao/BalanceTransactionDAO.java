package com.xkeshi.dao;

import java.math.BigDecimal;

import org.apache.ibatis.annotations.Param;

import com.xkeshi.pojo.po.BalanceTransaction;

public interface BalanceTransactionDAO {
	
	int  insertBalanceTransaction(  BalanceTransaction  balanceTransaction);
	
	BigDecimal findBalanceByBusiness(@Param("businessId") Long  businessId , @Param("businessType") String  businessType);
	
}
