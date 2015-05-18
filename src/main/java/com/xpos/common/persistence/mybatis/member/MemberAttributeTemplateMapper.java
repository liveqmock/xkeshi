package com.xpos.common.persistence.mybatis.member;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.xpos.common.entity.face.Business.BusinessType;
import com.xpos.common.entity.member.MemberAttributeTemplate;

public interface MemberAttributeTemplateMapper{

	MemberAttributeTemplate selectById(long id);

	List<MemberAttributeTemplate> selectTemplateListByBusiness(@Param("businessId")long businessId,
			@Param("businessType")BusinessType businessType);
	
	int insert(MemberAttributeTemplate memberAttributeTemplate);

}
