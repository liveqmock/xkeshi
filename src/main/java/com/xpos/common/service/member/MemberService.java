package com.xpos.common.service.member;

import java.util.List;

import com.xpos.common.entity.Merchant;
import com.xpos.common.entity.Shop;
import com.xpos.common.entity.face.Business;
import com.xpos.common.entity.face.Business.BusinessType;
import com.xpos.common.entity.member.Member;
import com.xpos.common.entity.member.MemberType;
import com.xpos.common.searcher.member.MemberSearcher;
import com.xpos.common.utils.Pager;

public interface MemberService {
	
	/**
	 * 按会员ID查询会员信息，不包含会员扩展属性信息
	 */
	Member findMemberByIdIgnoreAttribute(Long id);
	
	/**
	 * 按会员ID查询会员信息，包含会员扩展属性信息
	 */
	Member findMemberByIdWithAttributes(Long id);
	
	/**
	 * 按用户唯一标识，查询该用户所有已注册的会员信息，不包含会员扩展属性
	 */
	List<Member> findMembersByUserIgnoreAttribute(String uniqueNo);
	
	/**
	 * 按用户唯一标识，查询该用户所有已注册的会员信息，包含会员扩展属性
	 */
	List<Member> findMembersByUserWithAttributes(String uniqueNo);
	
	/**
	 * 按用户唯一标识和集团/商户信息，查询指定集团/商户创建的会员。不包含会员扩展属性
	 */
	Member findMemberByUserAndBusinessIgnoreAttribute(String uniqueNo, Long businessId, BusinessType businessType);
	
	/**
	 * 按用户唯一标识和集团/商户信息，查询指定集团/商户创建的会员。包含会员扩展属性
	 */
	Member findMemberByUserAndBusinessWithAttributes(String uniqueNo, Long businessId, BusinessType businessType);
	
	/**
	 * 商户通过手机号查询在当前商户或所属集团下注册的会员信息
	 */
	Member findMemberByMobileForShop(Long shopId, String mobile);

	/**
	 * 商户通过手机号查询在当前商户或所属集团下注册的会员信息
	 */
	Member findMemberByMobileForShop(Shop shop, String mobile);

	/**
	 * 集团通过手机号查询在所有下属商户注册的会员信息
	 */
	List<Member> findMembersByMobileForMerchant(Long merchantId, String mobile);
	
	/**
	 * 集团通过手机号查询在所有下属商户注册的会员信息
	 */
	List<Member> findMembersByMobileForMerchant(Merchant merchant, String mobile);
	
	Pager<Member> findMembersByBusiness(Business business, Pager<Member> pager, MemberSearcher searcher);
	
	/**
	 * 校验、保存新会员信息
	 * 1.校验创建者（business）和合法性
	 * 2.校验businessId、businessType与“会员统一管理”设置是否匹配
	 * 3.校验关联的手机号是否已注册
	 * 4.校验会员类型
	 * 5.校验会员属性
	 * 6.保存会员 & 保存会员属性
	 * @param member 新会员信息
	 * @param business 创建者（集团/商户）
	 * @throws RuntimeException (校验失败、保存失败时，抛异常，可通过getMessage()获取具体失败信息)
	 */
	boolean validateAndSave(Member member,Business business);
	
	/**
	 * 检查手机号是否已注册过该集团/商户的会员。
	 * 通过匹配businessId、businessType和mobile 
	 */
	boolean checkMobileRegisted(Member member);
	
	/** 检查Member对象的businessId & businessType是否符合所属集团的“会员统一管理”规则 */
	boolean validateBusinessWithCentralManagement(Member member);

	boolean validateAndUpdateMember(Member memberPO, Business business);
	
	/**
	 * 校验并删除指定会员
	 * 校验规则：能通过列表查看到该会员，即代表有删除权限
	 */
	String validateAndDeleteMember(Member member, Business business);
	
	/**
	 * 通过会员类型查询会员数量
	 */
	int countMemberByMemberType(MemberType memberType);
	
}
