package com.xkeshi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xkeshi.dao.AccountDAO;
import com.xkeshi.pojo.po.Account;


@Service
public class XAccountService {
	@Autowired
	private AccountDAO accountDao;
	
	public Account selectByName(String name){
		return accountDao.selectByName(name);
	}
	
	/**修改密码*/
	@Transactional
	public boolean updateAccount(Account account) {
		return accountDao.updateAccount(account)>0;
	}

	public Account getAccountById(Long merchantId) {
		return accountDao.getAccountById(merchantId);
	}
}
