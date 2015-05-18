package com.xpos.common.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xpos.common.entity.example.Example.Criteria;
import com.xpos.common.entity.example.UserExample;
import com.xpos.common.entity.face.Business;
import com.xpos.common.entity.security.User;
import com.xpos.common.persistence.mybatis.UserMapper;
import com.xpos.common.utils.FileMD5;
import com.xpos.common.utils.UUIDUtil;

@Service
public class UserServiceImpl implements UserService{
	
	protected static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
	
	@Autowired
	private UserMapper userMapper;

	@Autowired
	private SMSService  smsService  ;
	
	@Override
	public User findUserByMobile(String mobile) {
		UserExample example = new UserExample();
		example.createCriteria().addCriterion("mobile=",mobile)
								.addCriterion("deleted=",false);
		return userMapper.selectOneByExample(example);
	}
	
	@Override
	public boolean save (User user) throws IOException {
		user.setUniqueNo(UUIDUtil.getRandomString(32));
		user.setPassword(FileMD5.getFileMD5String(user.getPassword().getBytes()));
		try {
			return userMapper.insert(user)==1;
		} catch (Exception e) {
			logger.error("注册失败"+e.toString());
		}
		return false;
	}
	
	@Override
	public boolean update(User user) throws IOException {
		user.setPassword(FileMD5.getFileMD5String(user.getPassword().getBytes()));
		return userMapper.updateByPrimaryKey(user)==1;
		
	}

	@Override
	public User findUserBymobileAndpassword(String mobile, String password) throws IOException {
		UserExample example = new UserExample();
		example.createCriteria().addCriterion("mobile=", mobile)
								.addCriterion("password=", FileMD5.getFileMD5String(password.getBytes()))
								.addCriterion("deleted=", false);
		return userMapper.selectOneByExample(example);
	}

	@Override
	public User updateMobile(String uniqueNo, String newmobile) {
		if(StringUtils.isBlank(uniqueNo) ||StringUtils.isBlank(newmobile)) {
			return null;
		}
		User user = userMapper.selectByUniqueNo(uniqueNo);
		if(user == null) {
			return null;
		}
		user.setMobile(newmobile);
		return userMapper.updateByPrimaryKey(user)>0?user:null;
	}

	/** 发送注册短信  */
	@Override
	public Map<String, String> sendRegisterCode (Business business, String mobile ){
		Map<String,String> map = new HashMap<>();
		User user = this.findUserByMobile(mobile);
		if (user != null) {
			map.put("error", "该手机号已经注册");
			map.put("status", "faild");
		  	/*
			if (user.getBusinessType() == null) {
				 //该手机已经爱客仕注册用户
				map.put("error", "该手机号已经注册");
				map.put("status", "faild");
				map.put("loginsource", "爱客仕");//登录来源
			} else {
				Member member     = memberService.findMemberByMobile(business, mobile);
				User userByMember =  member != null  ?  member.getUser() : null;
				 if (    member       == null  //商户或集体没有注册该会员
				      || userByMember == null  //会员与用户待关联
				    ) {
					 map.put("conjunction", "false"); //会员与用户待关联
					 map.put("error", "会员未关联");
					 if (member != null) {
						 map.put("membersource",  getMemberSource(member));//注册来源
					 }
					 smsService.sendSMSCode(business, mobile);
				} else if (userByMember != null) {
					if ( userByMember.getId() == user.getId()) {
						map.put("status", "faild");
						map.put("error", "该手机号已经注册");
					} else {//TODO  现关联会员与现xka关联不一致
						map.put("error", "该手机号已经注册");
						map.put("status", "faild");
						map.put("membersource",  getMemberSource(member));//注册来源
					}
				} 
			}
			*/
		}else{
			 map.put("status", "success");
			 smsService.sendSMSCode(business, mobile);
		}
		return map;
	}
	
	/**修改密码验证短信*/
	@Override
	public Map<String, String> sendModifyPasswordCode(Business business, String mobile){
	     User user = this.findUserByMobile(mobile);
	     Map<String,String> map = new HashMap<>();
	     if (user == null) {
	    	 map.put("status", "faild");
		     map.put("error", "用户未注册");
		}else{
			smsService.sendSMSCode(business, mobile);
			map.put("status", "success");
		}
	     return map;
	}
	
	/**修改手机号验证短信*/
	@Override
	public Map<String, String> sendModifyMobileCode(Business business, String mobile){
		User user = this.findUserByMobile(mobile);
		Map<String,String> map = new HashMap<>();
		if (user != null) {
			map.put("status", "faild");
			map.put("error", "该手机号已经注册");
		}else{
			map.put("status", "success");
			smsService.sendSMSCode(business, mobile);
		}
		return map;
	}
	
	@Override
	public boolean updateUserPassword (User user , String newPassword) throws IOException{
		if (user == null) 
		 return false;	
		UserExample userExample = new UserExample();
		Criteria criteria = userExample.createCriteria();
		if (user.getId() != null) 
			criteria.addCriterion("id=", user.getId()) ;
		if (StringUtils.isNotBlank(user.getMobile())) 
		   criteria.addCriterion("mobile=", user.getMobile());
		if (StringUtils.isNotBlank(user.getPassword())) 
			criteria.addCriterion("password=", FileMD5.getFileMD5String(user.getPassword().getBytes()));
            criteria.addCriterion("deleted=", false);		   	
		User oldUser = userMapper.selectOneByExample(userExample);
		if (oldUser != null && StringUtils.isNotBlank(newPassword)) {
			oldUser.setPassword(FileMD5.getFileMD5String(newPassword.getBytes()));
			return userMapper.updateByPrimaryKey(oldUser) > 0;
		}
		return false;
	}
	
	@Override
	public User findUserByUniqueNo(String uniqueNo) {
		return userMapper.selectByUniqueNo(uniqueNo);
	}

	@Override
	public User findOrCreateTemproryUserByMobile(String mobile) throws IOException {
		 User user = findUserByMobile(mobile);
		 if(user == null) {
			 user = new User();
			 user.setUniqueNo(UUIDUtil.getRandomString(32));
			 user.setPassword(FileMD5.getFileMD5String(StringUtils.substring(mobile, 5, 11).getBytes()));
			 user.setMobile(mobile);
			 userMapper.insert(user);
		 }
		 return user;
	}

}
