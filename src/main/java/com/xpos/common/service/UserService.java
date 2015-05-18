package com.xpos.common.service;

import java.io.IOException;
import java.util.Map;

import com.xpos.common.entity.face.Business;
import com.xpos.common.entity.security.User;

public interface UserService {

	
	/**
	 * 通过手机号查询用户
	 */
	public User findUserByMobile(String mobile);
	
	/**
	 * 通过手机号查询用户，若不存在则创建一个新用户
	 */
	public User findOrCreateTemproryUserByMobile(String mobile)  throws IOException ;

	public boolean save(User user) throws IOException;

	public boolean update(User user) throws IOException;

	/**
	 * 通过手机号和密码查询用户
	 */
	public User findUserBymobileAndpassword(String mobile, String password) throws IOException;

	/**
	 *	修改手机号
	 */
	public User updateMobile(String uniqueNo, String newmobile);

	/**
	 * 修改密码
	 * @param user 
	 * @param newPassword
	 * @return
	 */
	public boolean updateUserPassword (User user , String newPassword) throws IOException;
	
	/**
	 *  发送注册验证码
	 *  @return  map：信息
	 */
	public  Map<String, String> sendRegisterCode (Business business , String mobile);
	
	/** 
	 *  发送修改密码验证短信
	 *  @return  map：信息
	 * */
	public Map<String, String> sendModifyPasswordCode(Business business, String mobile);
	
	/**
	 *  发送修改手机号验证短信
	 *  @return  map：信息
	 * */
	public Map<String, String> sendModifyMobileCode(Business business, String mobile);
	
	public User findUserByUniqueNo(String uniqueNo);
	

}
