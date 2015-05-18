package com.xpos.common.persistence.mybatis.member;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.xpos.common.entity.Picture;
import com.xpos.common.entity.member.ShopMemberType;
import com.xpos.common.searcher.member.MemberTypeSearcher;
import com.xpos.common.utils.Pager;

public interface ShopMemberTypeMapper{
	ShopMemberType selectBasicById(long id);
	
	ShopMemberType selectDetailById(long id);

	List<ShopMemberType> selectBasicBySearcher(@Param("shopId")long shopId,
			@Param("searcher")MemberTypeSearcher searcher, @Param("pager")Pager<ShopMemberType> pager);
	
	List<ShopMemberType> selectBasicByShopId(long shopId);

	List<ShopMemberType> selectDetailByShopId(long shopId);
	
	int updateCoverPictureById(@Param("picture")Picture coverPicture, @Param("id")long id);
	
	int insert(ShopMemberType shopMemberType);
	
	int update(ShopMemberType shopMemberType);

	int deletedById(long id);

	int countBasicByShopId(@Param("shopId")long shopId, @Param("searcher")MemberTypeSearcher searcher);

	
}
