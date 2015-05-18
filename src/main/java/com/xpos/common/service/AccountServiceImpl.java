package com.xpos.common.service;

import java.io.IOException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xkeshi.dao.AccountDAO;
import com.xkeshi.utils.EncryptionUtil;
import com.xpos.common.entity.Merchant;
import com.xpos.common.entity.Operator;
import com.xpos.common.entity.Shop;
import com.xpos.common.entity.example.AccountExample;
import com.xpos.common.entity.example.Example.Criteria;
import com.xpos.common.entity.example.MerchantExample;
import com.xpos.common.entity.example.OperatorExample;
import com.xpos.common.entity.example.ShopExample;
import com.xpos.common.entity.face.Business;
import com.xpos.common.entity.face.Business.BusinessType;
import com.xpos.common.entity.member.MemberAttributeTemplate;
import com.xpos.common.entity.security.Account;
import com.xpos.common.persistence.mybatis.AccountMapper;
import com.xpos.common.persistence.mybatis.MerchantMapper;
import com.xpos.common.persistence.mybatis.OperatorMapper;
import com.xpos.common.persistence.mybatis.ShopMapper;
import com.xpos.common.service.member.MemberAttributeTemplateService;
import com.xpos.common.utils.FileMD5;

@Service
public class AccountServiceImpl implements AccountService{
	
	@Autowired
	private AccountMapper accountMapper;
	
	@Autowired
	private ShopMapper shopMapper;
	
	@Autowired
	private MerchantMapper merchantMapper;
	
	@Autowired
	private OperatorMapper operatorMapper;
	
	@Autowired
	private MemberAttributeTemplateService memberAttributeTemplateService;
	
	@Autowired
	private AccountDAO accountDao;
	
	@Override
	public boolean login(Operator operator) {
		OperatorExample example = new OperatorExample();
		example.createCriteria().addCriterion("shop_id=", operator.getShop().getId())
							   .addCriterion("username=", operator.getUsername())
							   .addCriterion("deleted=", false);
		Operator persisitence = operatorMapper.selectOneByExample(example);
		
		
		boolean result = false;
		try {
			result = persisitence.getPassword().equalsIgnoreCase(FileMD5.getFileMD5String(operator.getPassword().getBytes()));
		} catch (IOException e) {
		}
		return result;
	}
	
	@Override
	public List<Account> findAccountByBusiness(Business business) {
		AccountExample example = new AccountExample();
		Criteria criteria = example.createCriteria();
		if(business != null && business.getSelfBusinessId() != null )
			criteria.addCriterion("businessId=", business.getSelfBusinessId());
		if (business instanceof  Shop) 
			criteria.addCriterion("businessType=" ,Business.BusinessType.SHOP.toString());
		else if ( business instanceof Merchant)
			criteria.addCriterion("businessType=" ,Business.BusinessType.MERCHANT.toString());
		criteria.addCriterion("deleted=",false);
		return accountMapper.selectByExample(example, null);
	}
	
	public boolean deleteAccountByshopId(Long id) {
		AccountExample example = new AccountExample();
		example.createCriteria().addCriterion("businessId=", id)
								.addCriterion("businessType='SHOP'")
								.addCriterion("deleted=", false);
		Account account = accountMapper.selectOneByExample(example);
		if (account==null) return false;
		else 
		{
			account.setDeleted(true);
			return accountMapper.updateByPrimaryKey(account)==1;
		}
	}
	
	@Override
	@Transactional
	public String editAccountByshopId(Long id,Account account) throws IOException {
		boolean result = false;
		AccountExample example = new AccountExample();
		example.createCriteria().addCriterion("businessId=", id)
								.addCriterion("businessType='SHOP'")
								.addCriterion("deleted=",false);
		Account act = accountMapper.selectOneByExample(example);
		Shop shop = shopMapper.selectByPrimaryKey(id);
		if (act == null) {
			example.clear();
			example.createCriteria().addCriterion("username='"+account.getUsername()+"'")
									.addCriterion("deleted=",false);
			act = accountMapper.selectOneByExample(example);
			if (act != null) {
				return "商户账号已存在";
			}else if(!EncryptionUtil.isStrongPassword(account.getPassword())){
				return "密码长度为6-32位字符， 须同时包含字母和数字";
			}
			result =  OpenAccount(shop,account);
			return result?null:"商户账号添加失败";
		}else {
			account.setId(act.getId());
            String oldpassword = act.getPassword();
			example.clear();
			example.createCriteria().addCriterion("username='"+account.getUsername()+"'")
									.addCriterion("id != ", act.getId())
									.addCriterion("deleted=",false);
			act = accountMapper.selectOneByExample(example);
			if (act != null) {
				return "商户账号已存在";
			}else if(StringUtils.isNotBlank(account.getPassword()) && !EncryptionUtil.isStrongPassword(account.getPassword())){
				return "密码长度为6-32位字符， 须同时包含字母和数字";
			}
            if (StringUtils.isNotBlank(account.getPassword()))
			    account.setPassword(FileMD5.getFileMD5String(account.getPassword().getBytes()));
			com.xkeshi.pojo.po.Account account1=new com.xkeshi.pojo.po.Account();
			account1.setId(account.getId());
			account1.setUsername(account.getUsername());
            if(StringUtils.isNotBlank(account.getPassword()))
                account1.setPassword(account.getPassword());
            else
                account1.setPassword(oldpassword);
			account1.setIsInitPassword(true);
			result = accountDao.updateAccount(account1)>0;
			return result?null:"商户账号修改失败";
		}
	}


	@Override
	public Account findAccountByUsername(String username) {
		AccountExample example = new AccountExample();
		example.createCriteria().addCriterion("username=",username)
								.addCriterion("enable=", true)
								.addCriterion("deleted=",false);
		return accountMapper.selectOneByExample(example);
	}

	@Override
	public Business findBusinessByAccount(Account account) {
		
		if(Business.BusinessType.SHOP.equals(account.getBusinessType())){
			ShopExample example = new ShopExample();
			example.createCriteria().addCriterion("id=",account.getBusinessId())
									.addCriterion("deleted=", false);
			return shopMapper.selectOneByExample(example);
		}
		
		if(Business.BusinessType.MERCHANT.equals(account.getBusinessType())){
			MerchantExample example = new MerchantExample();
			example.createCriteria().addCriterion("id=",account.getBusinessId())
									.addCriterion("deleted=", false);
			return merchantMapper.selectOneByExample(example);
		}
		
		return null;
	}
	
	@Override
	@Transactional
	public boolean OpenAccount(Shop shop, Account account) throws IOException {//开户时调用,,Account_Role,Balance
		if(!EncryptionUtil.isStrongPassword(account.getPassword())){
			return false;
		}
		
		account.setId(null);
		account.setBusinessId(shop.getId());
		account.setBusinessType(BusinessType.SHOP);
		account.setEnable(true);
		account.setPassword(FileMD5.getFileMD5String(account.getPassword().getBytes()));
		if (accountMapper.insert(account)>0) {//创建Account
			boolean result = accountMapper.insertAccountRole(account.getId() , 2) > 0;//创建AccountRole
			
			if(shop.getMerchant() == null
					|| shop.getMerchant().getMemberCentralManagement() == null //无法判断集团会选择哪种会员管理模式，先为每个子商户创建默认模板
					|| !shop.getMerchant().getMemberCentralManagement()){
				//普通商户、非“会员统一管理”的子商户，创建独立维护的会员属性模板和会员类型
				MemberAttributeTemplate memberAttributeTemplate = new MemberAttributeTemplate();
				memberAttributeTemplate.setBusiness(shop);
				memberAttributeTemplate.setName("默认模板");
				memberAttributeTemplate.setEnabled(true);
				result = result && memberAttributeTemplateService.saveTemplate(memberAttributeTemplate, shop);
				
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean updateAccount(Account account) {
		return accountMapper.updateByPrimaryKey(account) == 1;
	}

	@Override
	public boolean verifyAccount(String userName, String password) {
		Account account = findAccountByUsername(userName);
		try {
			if(StringUtils.isNotBlank(password) && 
				account.getPassword().equals(FileMD5.getFileMD5String(password.getBytes()))) {
				return true;
			}
		} catch (IOException e) {
			return false; 
		}
		return false;
	}

	
	 
}
