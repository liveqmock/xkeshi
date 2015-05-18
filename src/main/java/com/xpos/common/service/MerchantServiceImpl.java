package com.xpos.common.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xkeshi.dao.AccountDAO;
import com.xpos.common.entity.Merchant;
import com.xpos.common.entity.Picture;
import com.xpos.common.entity.Picture.PictureType;
import com.xpos.common.entity.Shop;
import com.xpos.common.entity.example.AccountExample;
import com.xpos.common.entity.example.MerchantExample;
import com.xpos.common.entity.face.Business;
import com.xpos.common.entity.face.Business.BusinessType;
import com.xpos.common.entity.member.MemberAttributeTemplate;
import com.xpos.common.entity.security.Account;
import com.xpos.common.persistence.mybatis.AccountMapper;
import com.xpos.common.persistence.mybatis.MerchantMapper;
import com.xpos.common.persistence.mybatis.PictureMapper;
import com.xpos.common.persistence.mybatis.ShopMapper;
import com.xpos.common.service.member.MemberAttributeTemplateService;
import com.xpos.common.utils.FileMD5;
import com.xpos.common.utils.Pager;

@Service
public class MerchantServiceImpl implements MerchantService{
	
	@Autowired
	private MerchantMapper merchantMapper;
	
	@Autowired
	private AccountMapper accountMapper;

	@Autowired
	private MemberAttributeTemplateService memberAttributeTemplateService;

	@Autowired
	private ShopMapper shopMapper;
	
	@Autowired
	private PictureMapper pictureMapper;
	
	@Autowired
	private PictureService pictureService;
	
	@Autowired
	private AccountDAO accountDAO;
	
	@Override
	@Transactional
	public boolean saveMerchant(Merchant merchant ,Account  account) throws IOException {
		boolean  status  = true  ;
		merchant.setBalance(new BigDecimal(0));
		//创建集团
		status = merchantMapper.insert(merchant)>0 && status;
		if(merchant.getAvatar() != null){
			Picture avatar = merchant.getAvatar();
			avatar.setForeignId(merchant.getId());
			avatar.setPictureType(PictureType.MERCHANT_AVATAR);
			pictureService.uploadPicture(avatar);
			merchant.setAvatar(avatar);
		}
		//创建登录账户
		account.setId(null);
		account.setBusinessId(merchant.getId());
		account.setBusinessType(BusinessType.MERCHANT);
		account.setEnable(true);
		account.setPassword(FileMD5.getFileMD5String(account.getPassword().getBytes()));
		status = status && accountMapper.insert(account) > 0;
		//创建AccountRole
		status = status && accountMapper.insertAccountRole(account.getId(),3) > 0;
		if(merchant.getMemberCentralManagement() == null //系统管理员创建商户时，未指定集团会员管理模式
				|| merchant.getMemberCentralManagement()){ //集团设置统一管理会员，创建统一的会员模板
			//创建模板
			MemberAttributeTemplate memberAttributeTemplate = new MemberAttributeTemplate();
			memberAttributeTemplate.setBusiness(merchant);
			memberAttributeTemplate.setName("默认模板");
			memberAttributeTemplate.setEnabled(true);
			status = status && memberAttributeTemplateService.saveTemplate(memberAttributeTemplate, merchant);
			
			/*创建默认的会员类型
			MerchantMemberType type = new MerchantMemberType();
			type.setMemberAttributeTemplate(memberAttributeTemplate);
			type.setDiscount(new BigDecimal(1));
			type.setName("普通会员");
			type.setDefault(true); //默认会员类型
			type.setMerchant(merchant);
			status = status && merchantMemberTypeMapper.insert(type) > 0;
			 */
		}
		status = status && merchantMapper.updateByPrimaryKey(merchant) > 0;
		return status;
	}

	@Override
	@Transactional
	public boolean updateMerchant(Merchant merchant, com.xkeshi.pojo.po.Account account) throws IOException {
		if(merchant.getAvatar() != null){
			Picture avatar = merchant.getAvatar();
			avatar.setForeignId(merchant.getId());
			avatar.setPictureType(PictureType.MERCHANT_AVATAR);
			pictureService.uploadPicture(avatar) ;
		}
		boolean status  = true;  
		 Merchant merchant2 = merchantMapper.selectByPrimaryKey(merchant.getId());
		 if (merchant2 != null ) {
				status =  merchantMapper.updateByPrimaryKey(merchant) > 0 && status;
			 if (account != null) {
				 AccountExample accountExample = new AccountExample();
				 accountExample.appendCriterion("id= ", account.getId())
				 .addCriterion("businessId = ", merchant2.getId())
				 .addCriterion("businessType = ", Business.BusinessType.MERCHANT.toString())
				 .addCriterion("enable = ", true)
				 .addCriterion("deleted = ", false);
				 Account account2 = accountMapper.selectOneByExample(accountExample);
				 if(account2 !=  null) {
					 if(StringUtils.isBlank(account.getPassword())){
						 account.setPassword(account2.getPassword());
					 }else{
						 account.setPassword(FileMD5.getFileMD5String(account.getPassword().getBytes()));
					 }
					 status =  accountDAO.updateAccount(account) > 0 && status;
				 }
			}
		}else{
			status  = false;
		}
		return status;
	}

	
 
	@Override
	public List<Merchant> findAllMerchant( MerchantExample example) {
		if(example ==null ){
			example  = new MerchantExample();
		}
		example.appendCriterion("deleted=", false)
			   .addCriterion("visible=", true);
		return merchantMapper.selectByExample(example, null);
	}

	@Override
	public Merchant findMerchant(Long id) {
		return merchantMapper.selectByPrimaryKey(id);
	}

	@Override
	public Pager<Merchant> findMerchants(MerchantExample example, Pager<Merchant> pager) {
		if(example == null)
			example  = new MerchantExample();
		example.appendCriterion("deleted = ", false);
		example.setOrderByClause("modifyDate desc ,createDate desc");
		pager.setList(merchantMapper.selectByExample(example, pager));
		pager.setTotalCount(merchantMapper.countByExample(example));
		return pager;
	}

	@Override
	@Transactional
	public boolean deleteMerchant(Long merchantId) {
		Merchant merchant = merchantMapper.selectByPrimaryKey(merchantId);
		if(merchant != null) {
			List<Shop> shopList = shopMapper.selectShopListByMerchantId(merchantId, true);
			//1.删除商户
			merchant.setDeleted(true);
			boolean result = merchantMapper.updateByPrimaryKey(merchant) > 0;
			result = result && accountMapper.discardAccountByBusiness(merchant.getId(), BusinessType.MERCHANT) > 0;
			if(result && CollectionUtils.isNotEmpty(shopList)){
				for (Shop shop : shopList) {
					//遍历删除子商户
					shop.setMerchant(null);//集团删除，子商户保留但是状态为“无集团”
					result = result && shopMapper.updateByPrimaryKeyWithNullValue(shop) > 0;
					result = result && accountMapper.discardAccountByBusiness(shop.getId(), BusinessType.SHOP) > 0;
				}
			}
			return result;
		}
		return false;
	}

	@Override
	public boolean update(Merchant merchant) {
		return merchantMapper.updateByPrimaryKey(merchant) > 0;
	}
	
	@Override
	@Transactional
	public boolean batchSetVisible(Long[] ids, boolean visible) {
		boolean result = true;
		for(Long id:ids){
			 Merchant merchant = merchantMapper.selectByPrimaryKey(id);
			 merchant.setVisible(visible);
			result =  merchantMapper.updateByPrimaryKey(merchant) > 0 && result;
			if(!result) break;
		}
		return result;
	}

	@Override
	@Transactional
	public boolean deleteMerchants(Long[] merchantIds) {
		if(ArrayUtils.isNotEmpty(merchantIds)){
			for (Long merchantId : merchantIds) {
				deleteMerchant(merchantId);
			}
			return true;
		}else{
			return false;
		}
	}
}


