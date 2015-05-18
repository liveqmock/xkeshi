package com.xpos.common.service.member;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xkeshi.dao.PrepaidDAO;
import com.xkeshi.pojo.po.PrepaidCard;
import com.xpos.common.entity.Merchant;
import com.xpos.common.entity.Shop;
import com.xpos.common.entity.face.Business;
import com.xpos.common.entity.face.Business.BusinessType;
import com.xpos.common.entity.member.Member;
import com.xpos.common.entity.member.MemberAttribute;
import com.xpos.common.entity.member.MemberType;
import com.xpos.common.exception.MemberException;
import com.xpos.common.persistence.mybatis.member.MemberMapper;
import com.xpos.common.searcher.member.MemberSearcher;
import com.xpos.common.service.MerchantService;
import com.xpos.common.service.ShopService;
import com.xpos.common.utils.Pager;

@Service
public class MemberServiceImpl implements MemberService{
	
	@Autowired
	private MemberMapper memberMapper;
	
	@Autowired
	private PrepaidDAO prepaidDAO;
	
	@Autowired
	private MemberTypeService memberTypeService;
	
	@Autowired
	private MemberAttributeService memberAttributeService;
	
	@Autowired
	private MerchantService merchantService;
	
	@Autowired
	private ShopService shopService;
	
	@Override
	public Member findMemberByIdIgnoreAttribute(Long id) {
		return findMemberById(id, false);
	}

	@Override
	public Member findMemberByIdWithAttributes(Long id) {
		return findMemberById(id, true);
	}
	
	private Member findMemberById(Long id, boolean requireAttribute){
		Member member = memberMapper.selectById(id);
		if(member != null && requireAttribute){
			MemberType memberType = member.getMemberType();
			if(memberType != null
					&& memberType.getMemberAttributeTemplate() != null
					&& CollectionUtils.isNotEmpty(memberType.getMemberAttributeTemplate().getMemberAttributeList())){
				//会员模板非空，且已设置会员属性，则加载当前会员的属性值
				List<MemberAttribute> memberAttributeList = memberAttributeService.findByMemberAndTemplateWithStoredValues(id, memberType.getMemberAttributeTemplate().getId());
				memberType.getMemberAttributeTemplate().setMemberAttributeList(memberAttributeList);
			}
		}
		return member;
	}
	
	@Override
	public List<Member> findMembersByUserIgnoreAttribute(String uniqueNo) {
		return findMemberByUser(uniqueNo, false);
	}
	
	@Override
	public List<Member> findMembersByUserWithAttributes(String uniqueNo) {
		return findMemberByUser(uniqueNo, true);
	}
	
	private List<Member> findMemberByUser(String uniqueNo, boolean requireAttribute){
		List<Member> memberList = memberMapper.selectByUser(uniqueNo);
		if(CollectionUtils.isNotEmpty(memberList) && requireAttribute){
			for(Member member : memberList){
				Long memberId = member.getId();
				MemberType memberType = member.getMemberType();
				if(memberType != null
						&& memberType.getMemberAttributeTemplate() != null
						&& CollectionUtils.isNotEmpty(memberType.getMemberAttributeTemplate().getMemberAttributeList())){
					//会员模板非空，且已设置会员属性，则加载当前会员的具体属性值
					List<MemberAttribute> memberAttributeList = memberAttributeService.findByMemberAndTemplateWithStoredValues(memberId, memberType.getMemberAttributeTemplate().getId());
					memberType.getMemberAttributeTemplate().setMemberAttributeList(memberAttributeList);
				}
			}
		}
		return memberList;
	}

	@Override
	public Member findMemberByUserAndBusinessIgnoreAttribute(String uniqueNo, Long businessId, BusinessType businessType) {
		return findMemberByUserAndBusiness(uniqueNo, businessId, businessType, false);
	}

	@Override
	public Member findMemberByUserAndBusinessWithAttributes(String uniqueNo, Long businessId, BusinessType businessType) {
		return findMemberByUserAndBusiness(uniqueNo, businessId, businessType, true);
	}
	
	private Member findMemberByUserAndBusiness(String uniqueNo, Long businessId, BusinessType businessType, boolean requireAttribute){
		Member member = memberMapper.selectByUserAndBusiness(uniqueNo, businessId, businessType);
		if(member != null && requireAttribute){
			Long memberId = member.getId();
			MemberType memberType = member.getMemberType();
			if(memberType != null
					&& memberType.getMemberAttributeTemplate() != null
					&& CollectionUtils.isNotEmpty(memberType.getMemberAttributeTemplate().getMemberAttributeList())){
				//会员模板非空，且已设置会员属性，则加载当前会员的具体属性值
				List<MemberAttribute> memberAttributeList = memberAttributeService.findByMemberAndTemplateWithStoredValues(memberId, memberType.getMemberAttributeTemplate().getId());
				memberType.getMemberAttributeTemplate().setMemberAttributeList(memberAttributeList);
			}
		}
		return member;
	}

	@Override
	public Pager<Member> findMembersByBusiness(Business business, Pager<Member> pager, MemberSearcher searcher) {
		if(searcher == null){
			searcher = new MemberSearcher();
		}
		if(pager == null){
			pager = new Pager<>();
			pager.setPageSize(Integer.MAX_VALUE);
		}
		
		if(business == null){
			//超级管理员
		}else if(business instanceof Merchant){ //集团
			Merchant merchant = (Merchant)business;
			if(merchant.getMemberCentralManagement()){ //统一管理会员
				searcher.setBusinessId(merchant.getId());
				searcher.setBusinessType(BusinessType.MERCHANT);
			}else{ //非统一管理会员
				searcher.setShopIds(shopService.findShopIdsByMerchantId(merchant.getId(), true));
				searcher.setBusinessType(BusinessType.SHOP);
			}
		}else if(business instanceof Shop){ //普通商户、子商户
			Shop shop = (Shop)business;
			Merchant merchant = shop.getMerchant();
			if(merchant == null){ //普通商户
				searcher.setBusinessId(shop.getId());
				searcher.setBusinessType(BusinessType.SHOP);
			}else if(merchant != null && merchant.getMemberCentralManagement()){
				//集团统一管理会员的子商户（只能查看到在其分店添加的会员）
				searcher.setBusinessId(merchant.getId());
				searcher.setBusinessType(BusinessType.MERCHANT);
				searcher.setShop(shop);
			}else if(merchant != null && !merchant.getMemberCentralManagement()){
				//非集团统一管理会员的子商户
				searcher.setBusinessId(shop.getId());
				searcher.setBusinessType(BusinessType.SHOP);
			}
		}
		
		List<Member> list = memberMapper.selectBySearcher(searcher, pager);
		int totalCount = memberMapper.countBySearcher(searcher);
		pager.setList(list);
		pager.setTotalCount(totalCount);
		return pager;
	}
	
	@Override
	@Transactional
	public boolean validateAndSave(Member member, Business business) {
		if(member == null || StringUtils.isBlank(member.getMobile()) || member.getBusinessId() == null ||
				member.getBusinessType() == null || business == null){
			return false;
		}
		
		//1.校验创建者的合法性
		if(!matchMemberBusinessWithCurrentBusiness(member, business)) {
			throw new MemberException("注册会员来源与当前商户不符");
		}
		
		//2. 校验businessId、businessType与后台“会员统一管理”设置是否匹配
		if(!validateBusinessWithCentralManagement(member)){
			throw new MemberException("会员类型设置出错"); 
		}
		
		//3.校验关联的手机号是否已注册
		if(checkMobileRegisted(member)){
			throw new MemberException("手机号已存在，请勿重复注册");
		}
		
		//4. 集团、商户的会员类型已设置且包含指定类型
		if(!memberTypeService.validateMemberType(member)) {
			throw new MemberException("会员类型指定错误");
		}
		
		//5. 保存基本属性
		if(!save(member)){
			throw new MemberException("会员保存失败，请稍后再试");
		}
		
		//6. 校验会员属性值并保存
		if(!memberAttributeService.validateAndSaveStoredValues(member)){
			throw new MemberException("会员属性保存失败，请稍后再试");
		}
		return true;
	}
	 

	/**
	 * 校验会员注册来源和当前商户是否匹配
	 */
	private boolean matchMemberBusinessWithCurrentBusiness(Member member,Business business) {
		if(business.getSelfBusinessType().equals(member.getBusinessType())) {//同属于集团或同属于商户时，校验id是否相同
			return business.getSelfBusinessId().equals(member.getBusinessId());
		}else if(BusinessType.MERCHANT.equals(business.getSelfBusinessType())) {
			return ArrayUtils.contains(shopService.findShopIdsByMerchantId(business.getSelfBusinessId(), true), member.getBusinessId());
		}else if(BusinessType.SHOP.equals(business.getSelfBusinessType())) {
			return ArrayUtils.contains(shopService.findShopIdsByMerchantId(member.getBusinessId(), true), business.getSelfBusinessId());
		}
		return false;
	}

	private boolean save(Member member) {
		return memberMapper.insert(member) > 0;
	}

	@Override
	public boolean checkMobileRegisted(Member member) {
		String mobile = member.getMobile();
		Long businessId = member.getBusinessId();
		BusinessType businessType = member.getBusinessType();
		
		return memberMapper.checkMobileRegisted(businessId, businessType, mobile);
	}

	@Override
	public boolean validateBusinessWithCentralManagement(Member member) {
		long businessId = member.getBusinessId();
		BusinessType businessType = member.getBusinessType();
		Shop sourceShop = member.getShop();
		
		if(BusinessType.MERCHANT.equals(businessType)){
			Merchant merchant = merchantService.findMerchant(businessId);
			if(merchant == null || !merchant.getMemberCentralManagement()){
				return false;
			}else if(sourceShop != null && sourceShop.getId() != null
					&& !ArrayUtils.contains(shopService.findShopIdsByMerchantId(businessId, true), sourceShop.getId())){
				//shop属性非空，但是Shop不属于指定的集团
				return false;
			}
		}else if(BusinessType.SHOP.equals(businessType)){
			Shop shop = shopService.findShopByIdIgnoreVisible(businessId);
			if(shop == null){ //商户不存在
				return false;
			}else if(shop.getMerchant() == null){ //普通商户
				member.setShop(null);
				return true;
			}else if(shop.getMerchant() != null && shop.getMerchant().getMemberCentralManagement()){
				//子商户类型，但是集团设置为会员统一管理
				return false;
			}else if(shop.getMerchant() != null && !shop.getMerchant().getMemberCentralManagement()){
				member.setShop(null);
				return true;
			}
		}
		return true;
	}

	@Override
	@Transactional
	public boolean validateAndUpdateMember(Member memberPO, Business business){
		//简单校验非空
		Member memberDB = null;
		if(memberPO == null || business == null){
			return false;
		}else if((memberDB = findMemberByIdIgnoreAttribute(memberPO.getId())) == null){
			//按memberId未查到相应Member对象
			return false;
		}
		
		memberPO.setBusinessId(memberDB.getBusinessId());
		memberPO.setBusinessType(memberDB.getBusinessType());
		memberPO.setShop(memberDB.getShop());//会员注册来源保持不变
		
		//1.校验更新者的合法性
		if(!matchMemberBusinessWithCurrentBusiness(memberPO, business)) {
			throw new RuntimeException("注册会员来源与当前商户不符");
		}
		
		//2.校验businessId、businessType与后台“会员统一管理”设置是否匹配
		if(!validateBusinessWithCentralManagement(memberPO)){
			throw new RuntimeException("会员类型设置出错"); 
		}
		
		//3.校验Mobile
		if(StringUtils.isNotBlank(memberPO.getMobile())
				&& !StringUtils.equals(memberDB.getMobile(), memberPO.getMobile())
				&& checkMobileRegisted(memberPO)){
			//手机号修改，且已注册
			throw new RuntimeException("更新失败，手机号已注册");
		}
		
		//4.集团、商户的会员类型已设置且包含指定类型
		if(memberPO.getMemberType() != null
				&& !memberTypeService.validateMemberType(memberPO)) {
			throw new RuntimeException("会员类型指定错误");
		}
		
		//5. 保存基本属性
		if(!update(memberPO)){
			throw new RuntimeException("会员更新失败，请稍后再试");
		}
		
		//6. 校验会员属性值并保存
		if(!memberAttributeService.validateAndUpdateStoredValues(memberPO)){
			throw new RuntimeException("会员属性更新失败，请稍后再试");
		}
		
		return true;
	}
	
	private boolean update(Member member) {
		return memberMapper.update(member) > 0;
	}
	
	public String validateAndDeleteMember(Member member, Business business) {
		MemberSearcher searcher = new MemberSearcher();
		searcher.setMemberId(member.getId());
		//根据当前集团/商户和指定会员ID，查找后台匹配的会员数量
		int totalCount = findMembersByBusiness(business, new Pager<Member>(), searcher).getTotalCount();
		if(totalCount == 1){ //如果有会员匹配到，说明该集团/商户有权限删除
			//检查该会员是否有预付卡，且余额不为零
			PrepaidCard prepaidCard = prepaidDAO.getByMemberId(member.getId());
			if(prepaidCard != null && prepaidCard.getBalance().compareTo(new BigDecimal(0)) == 1){
				return "该会员预付卡内有余额，无法删除相关信息";
			}
			return delete(member) ? null : "会员删除失败";
		}
		return "会员删除失败";
	}
	
	private boolean delete(Member member) {
		return memberMapper.deleteById(member.getId()) > 0;
	}

	@Override
	public Member findMemberByMobileForShop(Long shopId, String mobile) {
		Shop shop = shopService.findShopByIdIgnoreVisible(shopId);
		return findMemberByMobileForShop(shop, mobile);
	}

	@Override
	public Member findMemberByMobileForShop(Shop shop, String mobile) {
		MemberSearcher searcher = new MemberSearcher();
		searcher.setMobile(mobile);
		Pager<Member> pager = findMembersByBusiness(shop, null, searcher);
		int totalCount = pager.getTotalCount();
		
		if(totalCount <= 0){
			//手机号未匹配到任何会员，返回null
			return null;
		}else if(totalCount == 1){
			return pager.getList().get(0);
		}
		return null;
	}
	
	@Override
	public List<Member> findMembersByMobileForMerchant(Long merchantId, String mobile) {
		Merchant merchant = merchantService.findMerchant(merchantId);
		return findMembersByMobileForMerchant(merchant, mobile);
	}

	@Override
	public List<Member> findMembersByMobileForMerchant(Merchant merchant, String mobile) {
		MemberSearcher searcher = new MemberSearcher();
		searcher.setMobile(mobile);
		Pager<Member> pager = findMembersByBusiness(merchant, null, searcher);
		int totalCount = pager.getTotalCount();
		
		if(totalCount <= 0){
			//手机号未匹配到任何会员，返回null
			return null;
		}else {
			return pager.getList();
		}
	}

	@Override
	public int countMemberByMemberType(MemberType memberType) {
		Long memberTypeId = memberType.getId();
		BusinessType businessType = memberType.getBusinessType();
		return memberMapper.countMemberByMemberType(memberTypeId,businessType);
	}
	

}

