package com.xpos.common.service;

import java.io.IOException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xkeshi.dao.OrderDAO;
import com.xpos.common.entity.Operator;
import com.xpos.common.entity.example.OperatorExample;
import com.xpos.common.persistence.mybatis.OperatorMapper;
import com.xpos.common.utils.FileMD5;
import com.xpos.common.utils.TokenUtil;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OperatorServiceImpl implements OperatorService{
	private Logger logger = LoggerFactory.getLogger(OperatorServiceImpl.class);
	
	private String TOKEN_SEPERATOR = "\\|\\|\\|";
	
	@Autowired
	private OperatorMapper operatorMapper;
	
	@Autowired
	private OrderDAO orderDao; 
	
	@Override
	public boolean login(Operator operator) {
		OperatorExample example = new OperatorExample();
		example.createCriteria().addCriterion("shop_id=", operator.getShop().getId())
								.addCriterion("username=", operator.getUsername())
								.addCriterion("deleted=", false);
		Operator persistence = operatorMapper.selectOneByExample(example);
		
		if(persistence == null || persistence.getId() == null){
			return false;
		}
		try {
			if(persistence.getPassword().equalsIgnoreCase(FileMD5.getFileMD5String(operator.getPassword().getBytes()))){
				operator.setId(persistence.getId());
				return true;
			}else
				return false;
		} catch (IOException e) {
			logger.error("收银员登陆失败", e);
			return false;
		}
	}

	@Override
	public Operator findById(Long id) {
		OperatorExample example = new OperatorExample();
		example.createCriteria().addCriterion("id = ", id).addCriterion("deleted = ", false);
		List<Operator> list = operatorMapper.selectByExample(example, null);
		if(list.size() == 1)
			return list.get(0);
		
		return null;
	}
	
	@Override
	public String save(Operator operator) {
		OperatorExample example = new OperatorExample();
		example.createCriteria().addCriterion("(username='"+operator.getUsername()+"' or realName = '"+operator.getRealName()+"')")
								.addCriterion("shop_id=",operator.getShop().getSelfBusinessId())
								.addCriterion("deleted=", false);
		if(operatorMapper.selectOneByExample(example) != null) {
			return "POS终端账号或姓名已存在";
		}
		
		try{
			operator.setPassword(FileMD5.getFileMD5String(operator.getPassword().getBytes()));
		}catch(IOException e){
			return "POS终端账号添加失败";
		}
		return operatorMapper.insert(operator)==1?null:"POS终端账号添加失败";
	}

	@Override
    @Transactional
	public String update(Operator operator) {
		OperatorExample example = new OperatorExample();
		example.createCriteria().addCriterion("(username='"+operator.getUsername()+"' or realName = '"+operator.getRealName()+"')")
								.addCriterion("shop_id=",operator.getShop().getSelfBusinessId())
								.addCriterion("id!=",operator.getId())
								.addCriterion("deleted=", false);

		if( operatorMapper.selectOneByExample(example) != null)
			return "POS终端账号或姓名已存在";
        Operator operatorDB =   operatorMapper.selectByPrimaryKey(operator.getId());
		try {
            if (org.apache.commons.lang3.StringUtils.isNotBlank(operator.getPassword())){
                operator.setPassword(FileMD5.getFileMD5String(operator.getPassword().getBytes()));
             } else {
                operator.setPassword(operatorDB.getPassword());
            }
		} catch (IOException e) {
		}
		 return operatorMapper.updateByPrimaryKey(operator)==1?null:"POS终端账号修改失败";
	}

	public boolean deleteById(Long id) {
		Operator operator = operatorMapper.selectByPrimaryKey(id);
		if (operator != null) {
			operator.setDeleted(true);
			return operatorMapper.updateByPrimaryKey(operator) == 1;
		}else {
			return false;
		}
	}

	@Override
	public String verifyManagerByToken(String token,Long shopId,String orderNumber) {
		if(StringUtils.isBlank(token)) {
			return "参数不能为空";
		}
		String plainText = TokenUtil.decrypt(token);
		String[] array = plainText.split(TOKEN_SEPERATOR);
		String userName = array[0];
		String passWord = array[1];
		Operator operator  = operatorMapper.findManagerByNameAndShopId(userName,shopId);
		if(operator == null || operator.getId() == null) {
			return "店长身份校验失败";
		}
		try {
			if(!operator.getPassword().equalsIgnoreCase(FileMD5.getFileMD5String(passWord.getBytes()))){
				return "店长身份校验失败";
			}else {
				orderDao.updateOrderManager(orderNumber,operator.getId());
				return null;
			}
		} catch (IOException e) {
			return "店长身份校验失败";
		}
		
	}
}
