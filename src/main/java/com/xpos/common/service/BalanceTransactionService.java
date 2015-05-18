package com.xpos.common.service;

import java.math.BigDecimal;

import com.alipay.config.PaySourceConfig.PaySource;
import com.xpos.common.entity.BalanceTransaction;
import com.xpos.common.entity.Payment;
import com.xpos.common.entity.example.BalanceTransactionExample;
import com.xpos.common.entity.face.Business;
import com.xpos.common.entity.security.Account;
import com.xpos.common.utils.Pager;

public interface BalanceTransactionService {
	
	//账户变动明细(分页)
	public Pager<BalanceTransaction> findBalanceTransaction(Pager<BalanceTransaction> pager, BalanceTransactionExample example);
	
	//账户金额增加
	public boolean increaseBalance(Account account, Business business, BigDecimal amount, String descrption);
	
	//扣除账户余额
	public boolean deductBalance(Account account, Business business, BigDecimal amount, String description);
	
	//生成支付宝链接
	public String generateAlipayUrl(Account account, Business business, BigDecimal amount, String bank ,PaySource paySource) throws Exception;
	
	public Payment findPaymentByNum(String num);
	
	public boolean processPayment(Payment payment);
	
}
