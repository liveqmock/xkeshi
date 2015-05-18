package com.xpos.common.persistence.mybatis;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;

import com.xpos.common.entity.face.Business.BusinessType;
import com.xpos.common.entity.security.Account;
import com.xpos.common.persistence.BaseMapper;

public interface AccountMapper extends BaseMapper<Account>{
	
	public int  insertAccountRole (@Param(value = "accountId")Long accountId , @Param(value="accountRole") int accountRole );

	public int discardAccountByBusiness(@Param("businessId")Long businessid, @Param("businessType")BusinessType businessType);
}
