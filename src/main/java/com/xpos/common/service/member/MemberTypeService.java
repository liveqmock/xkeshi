package com.xpos.common.service.member;

import java.util.List;

import com.xpos.common.entity.Merchant;
import com.xpos.common.entity.Shop;
import com.xpos.common.entity.face.Business;
import com.xpos.common.entity.face.Business.BusinessType;
import com.xpos.common.entity.member.Member;
import com.xpos.common.entity.member.MemberType;
import com.xpos.common.entity.member.MerchantMemberType;
import com.xpos.common.entity.member.ShopMemberType;
import com.xpos.common.searcher.member.MemberTypeSearcher;
import com.xpos.common.utils.Pager;

public interface MemberTypeService {
	
	/** =========================================== 与具体MemberType类型无关 =========================================== */

	/** 查找指定business的默认会员类型
	 * businessType == MERCHANT, 如果该集团是统一管理，则返回MerchantMemberType，否则返回NULL。
	 * businessType == SHOP，如果普通商户或者非统一管理的子商户，返回ShopMemberType。如果统一管理的子商户，返回集团创建的MerchantMemberType
	 */
	MemberType findDefaultTypeByBusiness(long businessId, BusinessType businessType);

	/** 查找指定business的默认会员类型
	 * businessType == MERCHANT, 如果该集团是统一管理，则返回MerchantMemberType，否则返回NULL。
	 * businessType == SHOP，如果普通商户或者非统一管理的子商户，返回ShopMemberType。如果统一管理的子商户，返回集团创建的MerchantMemberType
	 */
	MemberType findDefaultTypeByBusiness(Business business);
	
	/** 检查集团、商户是否允许访问指定会员类型（读取权限） */
	boolean validateAccessibleByBusiness(MemberType memberType, Business business);
	
	/** 检查集团、商户是否允许对指定会员类型进行编辑（增、删、改的权限） */
	boolean validateEditableByBusiness(MemberType memberType, Business business);
	
	/**
	 * 校验Member对象关联的会员类型是否有效
	 * 1.会员类型是否存在
	 * 2.会员类型是否属于当前集团/商户
	 */
	boolean validateMemberType(Member member);
	
	boolean save(MemberType memberType,Business business);

	boolean update(MemberType memberType,Business business);
	
	boolean delete(MemberType memberType,Business business);
	
	/**判断是否是改会员类型是否正在使用*/
	boolean isUsed(MemberType memberType);
	
	//集团统一账户管理去重
	boolean distinct(MerchantMemberType memberType);
	//非集团统一账户管理去重
	boolean distinct(ShopMemberType memberType);
	
	/** =========================================== MerchantMemberType 相关方法 =========================================== */
	
	/** 通过merchantId，搜索对应集团下的所有MerchantMemberType类型(不包含MemberAttributeTemplate) */
	List<MerchantMemberType> findMerchantMemberTypeListByMerchantId(long merchantId);
	
	/** 通过merchantId，搜索对应集团下的所有MerchantMemberType类型(包含MemberAttributeTemplate) */
	List<MerchantMemberType> findMerchantMemberTypeListWithAttributeTemplateByMerchantId(long merchantId);
	
	/** 查询指定Id的MerchantMemberType类型(不包含MemberAttributeTemplate) */
	MerchantMemberType findMerchantMemberTypeById(long id);
	
	/** 查询指定Id的MerchantMemberType类型(包含MemberAttributeTemplate) */
	MerchantMemberType findMerchantMemberTypeWithAttributeTemplateById(long id);
	
	/**
	 * 集团查询统一管理的会员类型列表，不支持会员非统一管理的集团(抛异常)
	 */
	List<MerchantMemberType> findMerchantMemberTypeListByMerchant(Merchant merchant) throws Exception;
	
	/**
	 * 集团查询统一管理的会员类型列表，不支持会员非统一管理的集团(抛异常)，带分页和查询条件
	 */
	Pager<MerchantMemberType> searchMerchantMemberTypeListByMerchant(Pager<MerchantMemberType> pager , MemberTypeSearcher searcher , Merchant merchant) throws Exception;
	
	
	
	
	
	/** =========================================== ShopMemberType 相关方法 =========================================== */
	
	/** 通过shopId，搜索对应商户下的所有ShopMemberType类型(不包含MemberAttributeTemplate) */
	List<ShopMemberType> findShopMemberTypeListByShopId(long shopId);
	
	/** 通过shopId，搜索对应商户下的所有ShopMemberType类型(包含MemberAttributeTemplate) */
	List<ShopMemberType> findShopMemberTypeListWithAttributeTemplateByShopId(long shopId);
	
	/** 查询指定Id的ShopMemberType类型(不包含MemberAttributeTemplate) */
	ShopMemberType findShopMemberTypeById(long id);
	
	/** 查询指定Id的ShopMemberType类型(包含MemberAttributeTemplate) */
	ShopMemberType findShopMemberTypeWithAttributeTemplateById(long id);
	
	/**
	 * 子商户查询集团非统一管理的会员类型列表，或普通商户查询自己的会员类型列表
	 */
	List<ShopMemberType> findShopMemberTypeListByShop(Shop shop) throws Exception;

	/**
	 * 子商户查询集团非统一管理的会员类型列表，或普通商户查询自己的会员类型列表，带分页和查询条件
	 */
	Pager<ShopMemberType> searchShopMemberTypeListByShop(Pager<ShopMemberType> pager, MemberTypeSearcher searcher,Shop shop) throws Exception;

}
