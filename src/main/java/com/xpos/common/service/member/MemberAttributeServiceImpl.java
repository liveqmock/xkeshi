package com.xpos.common.service.member;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.xpos.common.entity.face.Business;
import com.xpos.common.entity.face.Business.BusinessType;
import com.xpos.common.entity.member.Member;
import com.xpos.common.entity.member.MemberAttribute;
import com.xpos.common.entity.member.MemberAttributeTemplate;
import com.xpos.common.exception.MemberException;
import com.xpos.common.persistence.mybatis.member.MemberAttributeMapper;
import com.xpos.common.searcher.member.MemberAttributeSearcher;
import com.xpos.common.service.ShopService;
import com.xpos.common.utils.Pager;

@Service
public class MemberAttributeServiceImpl implements MemberAttributeService{
	
	@Autowired
	private MemberAttributeMapper memberAttributeMapper;
	
	@Autowired
	private MemberTypeService memberTypeService;
	
	@Autowired
	private MemberAttributeTemplateService memberAttributeTemplateService;
	
	@Autowired
	private ShopService shopService;
	
	@Override
	public MemberAttribute findAttributeById(Long id){
		return memberAttributeMapper.selectById(id);
	}

	@Override
	public List<MemberAttribute> findByMemberAndTemplateWithStoredValues(Long memberId, Long templateId) {
		return memberAttributeMapper.selectByMemberAndTemplateWithStoreValue(memberId, templateId);
	}

	@Override
	public List<MemberAttribute> findAttributeListByTemplate(long templateId) {
		return memberAttributeMapper.selectByTemplateId(templateId);
	}

	@Override
	public List<MemberAttribute> findAttributeListByTemplateIgnoreEnabled(long templateId) {
		MemberAttributeSearcher searcher = new MemberAttributeSearcher();
		searcher.setMemberAttributeTemplateId(templateId);
		return memberAttributeMapper.selectByTemplateIdIgnoreEnabled(searcher, null);
	}
	
	@Override
	public Pager<MemberAttribute> searchAttributeByTemplate(Pager<MemberAttribute> pager, MemberAttributeSearcher searcher){
		List<MemberAttribute> attributeList = memberAttributeMapper.selectByTemplateIdIgnoreEnabled(searcher, pager);
		int totalCount = memberAttributeMapper.countByTemplateIdIgnoreEnabled(searcher);
		pager.setList(attributeList);
		pager.setTotalCount(totalCount);
		return pager;
	}
  
	@Override
	public boolean validateAndSaveStoredValues(Member member) {
		MemberAttributeTemplate attributeTemplateDB = null;
		MemberAttributeTemplate attributeTemplatePO = member.getMemberType().getMemberAttributeTemplate();
		//load MemberAttributeType from DB
		if(BusinessType.MERCHANT.equals(member.getBusinessType())){
			attributeTemplateDB = memberTypeService.findMerchantMemberTypeWithAttributeTemplateById(member.getMemberType().getId()).getMemberAttributeTemplate();
		}else if(BusinessType.SHOP.equals(member.getBusinessType())){
			attributeTemplateDB = memberTypeService.findShopMemberTypeWithAttributeTemplateById(member.getMemberType().getId()).getMemberAttributeTemplate();
		}
		
		if(attributeTemplateDB == null || CollectionUtils.isEmpty(attributeTemplateDB.getMemberAttributeList())){
			//该会员类型后台未指定属性模板 或者 该属性模板未创建会员属性
			return true;
		}else if(attributeTemplatePO == null || CollectionUtils.isEmpty(attributeTemplatePO.getMemberAttributeList())){
			//页面未传入会员属性模板，或者属性模板缺少属性值
			return false;
		}
		
		//把页面传入的会员模板属性转换成Map，方便读取
		Map<Long, MemberAttribute> map = new HashMap<>();
		for(MemberAttribute attribute : attributeTemplatePO.getMemberAttributeList()){
			map.put(attribute.getId(), attribute);
		}
		
		for(MemberAttribute attribute : attributeTemplateDB.getMemberAttributeList()){
			String storedValue = map.get(attribute.getId()) != null ? map.get(attribute.getId()).getStoredValue() : null;
			attribute.setStoredValue(storedValue);
			if(Boolean.TRUE.equals(attribute.isEnabled()) && Boolean.TRUE.equals(attribute.isRequired()) && StringUtils.isBlank(attribute.getStoredValue())){ //校验必填项
				throw new RuntimeException(attribute.getName() + "为必填项，不能为空");
			}
			if(StringUtils.isNotBlank(attribute.getOptionalValues())){ //有候选项
				String optionalValues = attribute.getOptionalValues();
				System.out.println("optionalValues="+optionalValues);
				JSONArray jsonArray = JSONArray.parseArray(attribute.getOptionalValues()); //解析JSON
				switch(attribute.getAttributeType()){
					case select:
						if(StringUtils.isNotBlank(storedValue) && !jsonArray.contains(storedValue)){ //候选项未包含提交参数值
							throw new MemberException(attribute.getName() + "，请选择系统提供的选项");
						}
						break;
					case checkbox:
						String[] storeValues = StringUtils.split(attribute.getStoredValue(), ',');
						if(ArrayUtils.isNotEmpty(storeValues)){
							for(String storeValue : storeValues){
								if(!jsonArray.contains(storeValue)){ //候选项未包含提交参数值
									throw new MemberException(attribute.getName() + "，请选择系统提供的选项");
								}
							}
						}
					default:
						break;  
				}
			}
		}
		return memberAttributeMapper.saveAttributeStoredValues(member, attributeTemplateDB.getMemberAttributeList()) > 0;
	}

	@Override
	public boolean validateAndUpdateStoredValues(Member member) {
		MemberAttributeTemplate attributeTemplateDB = null;
		MemberAttributeTemplate attributeTemplatePO = member.getMemberType().getMemberAttributeTemplate();
		
		//load MemberAttributeType from DB
		if(BusinessType.MERCHANT.equals(member.getBusinessType())){
			attributeTemplateDB = memberTypeService.findMerchantMemberTypeWithAttributeTemplateById(member.getMemberType().getId()).getMemberAttributeTemplate();
		}else if(BusinessType.SHOP.equals(member.getBusinessType())){
			attributeTemplateDB = memberTypeService.findShopMemberTypeWithAttributeTemplateById(member.getMemberType().getId()).getMemberAttributeTemplate();
		}
		
		if(attributeTemplateDB == null || CollectionUtils.isEmpty(attributeTemplateDB.getMemberAttributeList())){
			//该会员类型后台未指定属性模板 或者 该属性模板未创建会员属性
			return true;
		}else if(attributeTemplatePO == null || CollectionUtils.isEmpty(attributeTemplatePO.getMemberAttributeList())){
			//页面未传入会员属性模板，或者属性模板缺少属性值
			return true;
		}
		
		//把页面传入的会员模板属性转换成Map，方便读取
		Map<Long, MemberAttribute> map = new HashMap<>();
		for(MemberAttribute attribute : attributeTemplatePO.getMemberAttributeList()){
			map.put(attribute.getId(), attribute);
		}
		
		for(MemberAttribute attribute : attributeTemplateDB.getMemberAttributeList()){
			String storedValue = map.get(attribute.getId()) != null ? map.get(attribute.getId()).getStoredValue() : null;
			attribute.setStoredValue(storedValue);
			if(Boolean.TRUE.equals(attribute.isEnabled()) && Boolean.TRUE.equals(attribute.isRequired()) && StringUtils.isBlank(attribute.getStoredValue())){ //校验必填项
				throw new RuntimeException(attribute.getName() + "为必填项，不能为空");
			}
			if(StringUtils.isNotBlank(attribute.getOptionalValues())){ //有候选项
				JSONArray jsonArray = JSONArray.parseArray(attribute.getOptionalValues()); //解析JSON
				switch(attribute.getAttributeType()){
					case select:
						if(StringUtils.isNotBlank(storedValue) && !jsonArray.contains(storedValue)){ //候选项未包含提交参数值
							throw new RuntimeException(attribute.getName() + "，请选择系统提供的选项");
						}
						break;
					case checkbox:
						String[] storeValues = StringUtils.split(attribute.getStoredValue(), ',');
						if(ArrayUtils.isNotEmpty(storeValues)){
							for(String storeValue : storeValues){
								if(!jsonArray.contains(storeValue)){ //候选项未包含提交参数值
									throw new RuntimeException(attribute.getName() + "，请选择系统提供的选项");
								}
							}
						}
					default:
						break;  
				}
			}
		}
		return memberAttributeMapper.updateAttributeStoredValues(member, attributeTemplateDB.getMemberAttributeList()) > 0;
	}

	@Override
	public boolean validateAccessibleByBusiness(MemberAttribute memberAttribute, Business business){
		if(memberAttribute == null || memberAttribute.getMemberAttributeTemplate() == null
				|| memberAttribute.getMemberAttributeTemplate().getId() == null 
				|| business == null){
			return false;
		}

		MemberAttributeTemplate template = memberAttributeTemplateService.findById(memberAttribute.getMemberAttributeTemplate().getId());
		if(BusinessType.MERCHANT.equals(template.getBusinessType()) 
				&& BusinessType.MERCHANT.equals(business.getSelfBusinessType())
				&& template.getBusinessId().equals(business.getSelfBusinessId())){
			//模板的创建者是Merchant(说明肯定是会员统一管理)，且操作的Business也是该Merchant
			return true;
		}else if(BusinessType.SHOP.equals(template.getBusinessType()) 
				&& BusinessType.SHOP.equals(business.getSelfBusinessType())
				&& template.getBusinessId().equals(business.getSelfBusinessId())){
			//模板的创建者是普通商户、子商户(说明非会员统一管理)，且操作的Business也是该商户
			return true;
		}else if(BusinessType.SHOP.equals(template.getBusinessType())
				&& BusinessType.MERCHANT.equals(business.getSelfBusinessType())
				&& ArrayUtils.contains(shopService.findShopIdsByMerchantId(business.getSelfBusinessId(), true), template.getBusinessId())){
			//模板创建者是子商户，且操作的Business是其所属的集团。（集团有权限帮子商户编辑，无论是否会员统一管理）
			return true;
		}
		
		return false;
	}

	@Override
	public boolean save(MemberAttribute memberAttribute, Business business) {
		if(!validateAccessibleByBusiness(memberAttribute, business)){
			return false;
		}
		
		return memberAttributeMapper.insert(memberAttribute) > 0;
	}
	
	@Override
	public boolean update(MemberAttribute memberAttribute, Business business){
		if(!validateAccessibleByBusiness(memberAttribute, business)){
			return false;
		}
		
		//检验名字是否重复
		List<MemberAttribute> attributeList = findAttributeListByTemplateIgnoreEnabled(memberAttribute.getMemberAttributeTemplate().getId());
		if(CollectionUtils.isNotEmpty(attributeList)){
			for(MemberAttribute attr : attributeList){
				if(StringUtils.equals(attr.getName(), memberAttribute.getName())
						&& !attr.getId().equals(memberAttribute.getId())){
					return false;
				}
			}
		}
		
		return memberAttributeMapper.update(memberAttribute) > 0;
	}

	@Override
	public boolean delete(MemberAttribute memberAttribute, Business business){
		if(!validateAccessibleByBusiness(memberAttribute, business)){
			return false;
		}
		return memberAttributeMapper.delete(memberAttribute) > 0;
	}
	
	/**
	 * 检查会员属性名称是否有重复的
	 * @return
	 * 
	 */
	public boolean distinct(MemberAttribute memberAttribute) {
		List<MemberAttribute> attributeList = findAttributeListByTemplateIgnoreEnabled(memberAttribute.getMemberAttributeTemplate().getId());
		if(CollectionUtils.isNotEmpty(attributeList)){
			for(MemberAttribute attr : attributeList){
				if(StringUtils.equals(attr.getName(), memberAttribute.getName())){
					return true;
				}
			}
		}
		return false;
	}
	
}

