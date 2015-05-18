package com.xpos.common.service.member;

import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xpos.common.entity.Merchant;
import com.xpos.common.entity.Shop;
import com.xpos.common.entity.face.Business;
import com.xpos.common.entity.face.Business.BusinessType;
import com.xpos.common.entity.member.MemberAttributeTemplate;
import com.xpos.common.persistence.mybatis.member.MemberAttributeTemplateMapper;
import com.xpos.common.service.MerchantService;
import com.xpos.common.service.ShopService;

@Service
public class MemberAttributeTemplateServiceImpl implements MemberAttributeTemplateService{
	
	@Autowired
	private MemberAttributeTemplateMapper memberAttributeTemplateMapper;
	
	@Autowired
	private MerchantService merchantService;
	
	@Autowired
	private ShopService shopService;
	
	@Override
	public boolean saveTemplate(MemberAttributeTemplate memberAttributeTemplate, Business business) {
		if(!validateEditableByBusiness(memberAttributeTemplate, business)){
			return false;
		}
		return memberAttributeTemplateMapper.insert(memberAttributeTemplate) > 0;
	}
	
	@Override
	public boolean validateEditableByBusiness(MemberAttributeTemplate template, Business business){
		if(template == null || business == null){
			return false;
		}
		
		Long businessId = business.getSelfBusinessId();
		BusinessType businessType = business.getSelfBusinessType();
		if(BusinessType.MERCHANT.equals(businessType)){//集团登陆时，是否允许对指定属性模板执行操作
			Merchant merchant = merchantService.findMerchant(businessId);
			if(merchant != null ) {
				if(BusinessType.MERCHANT.equals(template.getBusinessType())) {//集团“会员统一管理”,校验是否同一集团
					return businessId.equals(template.getBusinessId());
				}else if(BusinessType.SHOP.equals(template.getBusinessType())) {//集团非“会员统一管理”,校验传入的商户是否在该集团下
					return ArrayUtils.contains(shopService.findShopIdsByMerchantId(businessId, true), template.getBusinessId());
				}
			}
		}else if(BusinessType.SHOP.equals(businessType)){//子商户或普通商户登陆时，是否允许对指定会员类型执行操作
			Shop shop = shopService.findShopByIdIgnoreVisible(businessId);
			if(shop != null){
				if(shop.getMerchant() == null || !shop.getMerchant().getMemberCentralManagement()){
					//普通商户 或者 非统一管理的子商户，只能访问自己创建的ShopMemberType
					if(BusinessType.SHOP.equals(template.getBusinessType())){
						return businessId.equals(template.getBusinessId());
					}
				}
			}
		}
		return false;
	}
	
	@Override
	public MemberAttributeTemplate findById(long id){
		return memberAttributeTemplateMapper.selectById(id);
	}

	@Override
	public List<MemberAttributeTemplate> findByBusiness(long businessId, BusinessType businessType) {
		return memberAttributeTemplateMapper.selectTemplateListByBusiness(businessId, businessType);
	}
}

