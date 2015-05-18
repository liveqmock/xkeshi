package com.xkeshi.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xkeshi.dao.BalanceTransactionDAO;
import com.xkeshi.pojo.po.BalanceTransaction;
import com.xpos.common.entity.SMSMessage;
import com.xpos.common.service.SMSService;

@Service
public class XBalanceTransactionService {
   
	@Autowired
	private BalanceTransactionDAO  balanceTransactionDAO   ;
	
	@Autowired
	private SMSService  smsService  ;
	
	/**
	 * @param balanceTransaction
	 * <p>
	 * 	type =  DEDUCT(消费)   and amount <  0
	 *  type =  INCREASE(充值) and amount >= 0 
	 * </p>
	 * @return
	 */
	@Transactional
	public  boolean  insertBalanceTransaction(BalanceTransaction  balanceTransaction ,SMSMessage smsMessage){
		Long   businessId   = balanceTransaction.getBusinessId();
		String businessType = balanceTransaction.getBusinessType();
		if (businessId == null ||  StringUtils.isBlank(businessType)) 
			return false;
		String type = balanceTransaction.getType();
		BigDecimal amount = balanceTransaction.getAmount();
		if (StringUtils.equalsIgnoreCase(type, "DEDUCT") && new BigDecimal(0).compareTo(amount) <=0) 
			return false;
		if (StringUtils.equalsIgnoreCase(type, "INCREASE") && new BigDecimal(0).compareTo(amount) == 1)
			return false;
		synchronized (this) {
			//获取资金流水，当前余额
			BigDecimal balance = balanceTransactionDAO.findBalanceByBusiness(businessId, businessType);
			balance = balance.add(balanceTransaction.getAmount()).setScale(2, RoundingMode.HALF_UP);
			//if (balance.compareTo(new BigDecimal(0)) >= 0 && smsService.insertSMSMessage(smsMessage )) {
			//DOTO 扣费未启用,保留balance资金余额为负的情况
			if ( smsService.insertSMSMessage(smsMessage )) {
				balanceTransaction.setBalance(balance);
				balanceTransaction.setType(StringUtils.upperCase(balanceTransaction.getType()));
				return balanceTransactionDAO.insertBalanceTransaction(balanceTransaction)  >0 ;
			}
		}
		return false;
	}
	
	/**
	 * 获取business的余额
	 * @param business
	 * @return
	 */
	public BigDecimal findBalanceByBusiness(Long businessId  , String businessType){
		if (businessId  == null || StringUtils.isBlank(businessType) ) 
			return null;
		return balanceTransactionDAO.findBalanceByBusiness(businessId, businessType);
	}
	 
}
