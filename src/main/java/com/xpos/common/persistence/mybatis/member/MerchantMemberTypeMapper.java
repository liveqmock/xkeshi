package com.xpos.common.persistence.mybatis.member;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.xpos.common.entity.Picture;
import com.xpos.common.entity.member.MerchantMemberType;
import com.xpos.common.searcher.member.MemberTypeSearcher;
import com.xpos.common.utils.Pager;

public interface MerchantMemberTypeMapper{
	MerchantMemberType selectBasicById(long id);
	
	MerchantMemberType selectDetailById(long id);

	List<MerchantMemberType> selectBasicBySearcher(@Param("merchantId")long merchantId,
			@Param("searcher")MemberTypeSearcher searcher, @Param("pager")Pager<MerchantMemberType> pager);
	
	List<MerchantMemberType> selectBasicByMerchantId(long merchantId);

	List<MerchantMemberType> selectDetailByMerchantId(long merchantId);
	
	int insert(MerchantMemberType merchantMemberType);
	
	int update(MerchantMemberType merchantMemberType);

	int updateCoverPictureById(@Param("picture")Picture coverPicture, @Param("id")long id);

	int deletedById(long id);

	int countBasicByMerchantId(@Param("merchantId")long merchantId,@Param("searcher")MemberTypeSearcher searcher);
	
}
