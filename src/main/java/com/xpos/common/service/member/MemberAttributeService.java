package com.xpos.common.service.member;

import java.util.List;

import com.xpos.common.entity.face.Business;
import com.xpos.common.entity.member.Member;
import com.xpos.common.entity.member.MemberAttribute;
import com.xpos.common.searcher.member.MemberAttributeSearcher;
import com.xpos.common.utils.Pager;

public interface MemberAttributeService {
	
	MemberAttribute findAttributeById(Long id);

	/**
	 * 查询指定会员和所在会员属性模板下的所有会员属性值
	 * @param memberId 会员ID
	 * @param templateId 会员属性模板ID
	 * @return 包含会员属性值的MemberAttribute
	 */
	List<MemberAttribute> findByMemberAndTemplateWithStoredValues(Long memberId, Long templateId);
	
	/** 查询指定属性模板中<b>已启用</b>的属性 */
	List<MemberAttribute> findAttributeListByTemplate(long templateId);
	
	/** 查询指定属性模板中的属性，<b>忽略是否已启用</b> */
	List<MemberAttribute> findAttributeListByTemplateIgnoreEnabled(long templateId);
	
	/**
	 * 后台分页查询指定属性模板下的属性值列表（忽略是否已启用）
	 */
	Pager<MemberAttribute> searchAttributeByTemplate(Pager<MemberAttribute> pager, MemberAttributeSearcher searcher);
	
	/**
	 * 校验指定Member的会员属性值，并保存到数据库
	 */
	boolean validateAndSaveStoredValues(Member member);

	/**
	 * 校验指定Member的会员属性值，并更新到数据库
	 * 同一个属性将覆盖原有值
	 */
	boolean validateAndUpdateStoredValues(Member member);

	/**
	 * 检查集团、商户是否有权限操作会员属性
	 * 通过Business对象和属性所属的MemberAttributeTemplate & memberType比较
	 */
	boolean validateAccessibleByBusiness(MemberAttribute memberAttribute, Business business);
	
	/**
	 * 新建会员属性
	 */
	boolean save(MemberAttribute memberAttribute, Business business);

	boolean update(MemberAttribute memberAttribute, Business business);
	
	boolean delete(MemberAttribute memberAttribute, Business business);
	
	boolean distinct(MemberAttribute memberAttribute);
}
