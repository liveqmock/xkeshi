package com.xpos.common.persistence.mybatis.member;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.xpos.common.entity.member.Member;
import com.xpos.common.entity.member.MemberAttribute;
import com.xpos.common.searcher.member.MemberAttributeSearcher;
import com.xpos.common.utils.Pager;

public interface MemberAttributeMapper{
	MemberAttribute selectById(Long id);
	
	List<MemberAttribute> selectByTemplateId(Long templateId);
	
	List<MemberAttribute> selectByTemplateIdIgnoreEnabled(@Param("searcher")MemberAttributeSearcher searcher, @Param("pager")Pager<MemberAttribute> pager);
	
	int countByTemplateIdIgnoreEnabled(@Param("searcher")MemberAttributeSearcher searcher);
	
	/** 查找指定会员和模板的会员属性，搜索结果包含该会员已提交的会员属性 */
	List<MemberAttribute> selectByMemberAndTemplateWithStoreValue(@Param("memberId")Long memberId, @Param("templateId")Long templateId);

	int saveAttributeStoredValues(@Param("member")Member member, @Param("attributeList")List<MemberAttribute> memberAttributeList);

	int updateAttributeStoredValues(@Param("member")Member member, @Param("attributeList")List<MemberAttribute> memberAttributeList);
	
	int insert(MemberAttribute memberAttribute);

	int update(MemberAttribute memberAttribute);

	int delete(MemberAttribute memberAttribute);
	
}
