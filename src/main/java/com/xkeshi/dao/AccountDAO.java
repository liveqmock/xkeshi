package com.xkeshi.dao;

import org.apache.ibatis.annotations.Param;

import com.xkeshi.pojo.po.Account;


public interface AccountDAO {
	
	Account selectByName(String name);
	

	Account getAccountById(@Param("merchantId")Long merchantId);


	int updateAccount(@Param("account")Account account);
}
