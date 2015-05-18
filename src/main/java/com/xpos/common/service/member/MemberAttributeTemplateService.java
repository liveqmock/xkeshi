package com.xpos.common.service.member;

import java.util.List;

import com.xpos.common.entity.face.Business;
import com.xpos.common.entity.face.Business.BusinessType;
import com.xpos.common.entity.member.MemberAttributeTemplate;

public interface MemberAttributeTemplateService {
	
	/**
	 * @param memberAttributeTemplate
	 * @param business 创建者
	 */
	boolean saveTemplate(MemberAttributeTemplate memberAttributeTemplate, Business business);

	MemberAttributeTemplate findById(long id);
	
	List<MemberAttributeTemplate> findByBusiness(long businessId, BusinessType businessType);

	boolean validateEditableByBusiness(MemberAttributeTemplate template, Business business);
	
}
