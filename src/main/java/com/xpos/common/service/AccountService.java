package com.xpos.common.service;

import java.io.IOException;
import java.util.List;

import com.xpos.common.entity.Operator;
import com.xpos.common.entity.Shop;
import com.xpos.common.entity.face.Business;
import com.xpos.common.entity.security.Account;

public interface AccountService {

	public boolean login(Operator operator);
	
	public Account findAccountByUsername(String username);

	//登录账户
	public List<Account> findAccountByBusiness( Business  business);
	
	public Business findBusinessByAccount(Account account);
	
	public boolean deleteAccountByshopId(Long id);
	
	public String editAccountByshopId(Long id,Account account)throws IOException ;

	//创建店铺管理员账号
	public boolean OpenAccount(Shop shop, Account account) throws IOException;

	//编辑账户信息
	public boolean updateAccount(Account account);

	public boolean verifyAccount(String userName, String password);
	
	
}
