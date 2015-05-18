package com.xpos.common.persistence.mybatis;

import org.apache.ibatis.annotations.Param;

import com.xpos.common.entity.security.User;
import com.xpos.common.persistence.BaseMapper;

public interface UserMapper extends BaseMapper<User>{
	
	public User selectByUniqueNo(@Param("user_unique_no") String user_unique_no);
}
