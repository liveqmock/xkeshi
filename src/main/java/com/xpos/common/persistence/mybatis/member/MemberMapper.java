package com.xpos.common.persistence.mybatis.member;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.xpos.common.entity.face.Business.BusinessType;
import com.xpos.common.entity.member.Member;
import com.xpos.common.searcher.member.MemberSearcher;
import com.xpos.common.utils.Pager;

public interface MemberMapper{
	
	Member selectById(Long id);

	List<Member> selectByUser(@Param("uniqueNo")String uniqueNo);

	Member selectByUserAndBusiness(@Param("uniqueNo")String uniqueNo,
				@Param("businessId")Long businessId, @Param("businessType")BusinessType businessType);

	int insert(Member member);
	
	int update(Member member);

	int deleteById(Long id);

	int countMemberByMemberType(@Param("memberTypeId")Long memberTypeId, @Param("businessType")BusinessType businessType);

	List<Member> selectBySearcher(@Param("searcher")MemberSearcher searcher, @Param("pager")Pager<Member> pager);

	int countBySearcher(@Param("searcher")MemberSearcher searcher);

	Boolean checkMobileRegisted(@Param("businessId")Long businessId,
				@Param("businessType")BusinessType businessType, @Param("mobile")String mobile);
	
}
