package com.xpos.common.service.member;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xpos.common.entity.Merchant;
import com.xpos.common.entity.Picture;
import com.xpos.common.entity.Picture.PictureType;
import com.xpos.common.entity.Shop;
import com.xpos.common.entity.face.Business;
import com.xpos.common.entity.face.Business.BusinessType;
import com.xpos.common.entity.member.Member;
import com.xpos.common.entity.member.MemberAttributeTemplate;
import com.xpos.common.entity.member.MemberType;
import com.xpos.common.entity.member.MerchantMemberType;
import com.xpos.common.entity.member.ShopMemberType;
import com.xpos.common.persistence.mybatis.member.MerchantMemberTypeMapper;
import com.xpos.common.persistence.mybatis.member.ShopMemberTypeMapper;
import com.xpos.common.searcher.member.MemberTypeSearcher;
import com.xpos.common.service.MerchantService;
import com.xpos.common.service.PictureService;
import com.xpos.common.service.ShopService;
import com.xpos.common.utils.Pager;

@Service
public class MemberTypeServiceImpl implements MemberTypeService{
	
	@Autowired
	private MerchantMemberTypeMapper merchantMemberTypeMapper;
	
	@Autowired
	private ShopMemberTypeMapper shopMemberTypeMapper;

	@Autowired
	private MerchantService merchantService;
	
	@Autowired
	private ShopService shopService;
	
	@Autowired
	private PictureService pictureService;
	
	@Autowired
	private MemberService memberService;
	
	@Autowired
	private MemberAttributeTemplateService memberAttributeTemplateService;

	@Override
	public List<MerchantMemberType> findMerchantMemberTypeListByMerchantId(long merchantId) {
		return merchantMemberTypeMapper.selectBasicByMerchantId(merchantId);
	}
	
	@Override
	public List<ShopMemberType> findShopMemberTypeListByShopId(long shopId) {
		return shopMemberTypeMapper.selectBasicByShopId(shopId);
	}

	@Override
	public MerchantMemberType findMerchantMemberTypeById(long id) {
		return merchantMemberTypeMapper.selectBasicById(id);
	}

	@Override
	public ShopMemberType findShopMemberTypeById(long id) {
		return shopMemberTypeMapper.selectBasicById(id);
	}
	
	@Override
	public List<MerchantMemberType> findMerchantMemberTypeListWithAttributeTemplateByMerchantId(long merchantId) {
		return merchantMemberTypeMapper.selectDetailByMerchantId(merchantId);
	}
	
	@Override
	public List<ShopMemberType> findShopMemberTypeListWithAttributeTemplateByShopId(long shopId) {
		return shopMemberTypeMapper.selectDetailByShopId(shopId);
	}
	
	@Override
	public MerchantMemberType findMerchantMemberTypeWithAttributeTemplateById(long id) {
		return merchantMemberTypeMapper.selectDetailById(id);
	}
	
	@Override
	public ShopMemberType findShopMemberTypeWithAttributeTemplateById(long id) {
		return shopMemberTypeMapper.selectDetailById(id);
	}

	@Override
	public MemberType findDefaultTypeByBusiness(long businessId, BusinessType businessType) {
		MemberType memberType = null;
		if(BusinessType.MERCHANT.equals(businessType)){
			Merchant merchant = merchantService.findMerchant(businessId);
			if(merchant != null && merchant.getMemberCentralManagement()){
				memberType = findDefaultMerchantMemberType(businessId);
			}
		}else if(BusinessType.SHOP.equals(businessType)){
			Shop shop = shopService.findShopByIdIgnoreVisible(businessId);
			if(shop != null){
				if(shop.getMerchant() == null || !shop.getMerchant().getMemberCentralManagement()){
					//普通商户、非统一管理的子商户
					memberType = findDefaultShopMemberType(businessId);
				}else{
					//统一管理的子商户
					memberType = findDefaultMerchantMemberType(shop.getMerchant().getId());
				}
			}
		}
		return memberType;
	}

	@Override
	public MemberType findDefaultTypeByBusiness(Business business) {
		if(business != null){
			return findDefaultTypeByBusiness(business.getSelfBusinessId(), business.getSelfBusinessType());
		}
		return null;
	}
	
	private MerchantMemberType findDefaultMerchantMemberType(long merchantId){
		MerchantMemberType memberType = null;
		List<MerchantMemberType> typeList = findMerchantMemberTypeListByMerchantId(merchantId);
		if(CollectionUtils.isNotEmpty(typeList)){
			for(MerchantMemberType merchantMemberType : typeList){
				if(Boolean.TRUE.equals(merchantMemberType.isDefault())){
					memberType = merchantMemberType;
					break;
				}
			}
		}
		return memberType;
	}
	
	private ShopMemberType findDefaultShopMemberType(long shopId){
		ShopMemberType memberType = null;
		List<ShopMemberType> typeList = findShopMemberTypeListByShopId(shopId);
		if(CollectionUtils.isNotEmpty(typeList)){
			for(ShopMemberType shopMemberType : typeList){
				if(Boolean.TRUE.equals(shopMemberType.isDefault())){
					memberType = shopMemberType;
					break;
				}
			}
		}
		return memberType;
	}
	
	@Override
	public boolean validateAccessibleByBusiness(MemberType memberType, Business business){
		if(memberType == null || business == null){
			return false;
		}
		
		Long businessId = business.getSelfBusinessId();
		BusinessType businessType = business.getSelfBusinessType();
		if(BusinessType.MERCHANT.equals(businessType)){//集团登陆时，是否允许对指定会员类型执行操作
			Merchant merchant = merchantService.findMerchant(businessId);
			if(merchant != null ) {
				if(memberType instanceof MerchantMemberType) {//集团“会员统一管理”,校验是否同一集团
					return businessId.equals(memberType.getBusinessId());
				}else if(memberType instanceof ShopMemberType) {//集团非“会员统一管理”,校验传入的商户是否在该集团下
					return ArrayUtils.contains(shopService.findShopIdsByMerchantId(businessId, true), memberType.getBusinessId());
				}
			}
		}else if(BusinessType.SHOP.equals(businessType)){//子商户或普通商户登陆时，是否允许对指定会员类型执行操作
			Shop shop = shopService.findShopByIdIgnoreVisible(businessId);
			if(shop != null){
				if(shop.getMerchant() == null || !shop.getMerchant().getMemberCentralManagement()){
					//普通商户 或者 非统一管理的子商户，只能访问自己创建的ShopMemberType
					if(memberType instanceof ShopMemberType){
						return businessId.equals(memberType.getBusinessId());
					}
				}else{
					//统一管理的子商户，可以查看集团的公共会员类型
					if(memberType instanceof MerchantMemberType){
						return ArrayUtils.contains(shopService.findShopIdsByMerchantId(memberType.getBusinessId(), true), businessId);
					}
				}
			}
		}
		return false;
	}
	
	@Override
	public boolean validateEditableByBusiness(MemberType memberType, Business business){
		if(memberType == null || business == null){
			return false;
		}
		
		Long businessId = business.getSelfBusinessId();
		BusinessType businessType = business.getSelfBusinessType();
		if(BusinessType.MERCHANT.equals(businessType)){//集团登陆时，是否允许对指定会员类型执行操作
			Merchant merchant = merchantService.findMerchant(businessId);
			if(merchant != null ) {
				if(memberType instanceof MerchantMemberType) {//集团“会员统一管理”,校验是否同一集团
					return businessId.equals(memberType.getBusinessId());
				}else if(memberType instanceof ShopMemberType) {//集团非“会员统一管理”,校验传入的商户是否在该集团下
					return ArrayUtils.contains(shopService.findShopIdsByMerchantId(businessId, true), memberType.getBusinessId());
				}
			}
		}else if(BusinessType.SHOP.equals(businessType)){//子商户或普通商户登陆时，是否允许对指定会员类型执行操作
			Shop shop = shopService.findShopByIdIgnoreVisible(businessId);
			if(shop != null){
				if(shop.getMerchant() == null || !shop.getMerchant().getMemberCentralManagement()){
					//普通商户 或者 非统一管理的子商户，只能访问自己创建的ShopMemberType
					if(memberType instanceof ShopMemberType){
						return businessId.equals(memberType.getBusinessId());
					}
				}
			}
		}
		return false;
	}
	
	private boolean matchMemberTypeWithAttributeTemplate(MemberType memberType) {
		MemberAttributeTemplate template = memberType.getMemberAttributeTemplate();
		if(template == null || template.getId() == null){
			return false; //会员类型必须关联属性模板
		}
		
		MemberAttributeTemplate templatePO = memberAttributeTemplateService.findById(template.getId());
		if(templatePO == null || !Boolean.TRUE.equals(templatePO.isEnabled())){
			return false;
		}
		return memberType.getBusinessId().equals(templatePO.getBusinessId())
				&& memberType.getBusinessType().equals(templatePO.getBusinessType());
	}
	
	@Override
	public boolean validateMemberType(Member member) {
		if(member.getMemberType() == null || member.getMemberType().getId() == null){
			//memberType为必填属性，不能为空
			return false;
		}
		if(BusinessType.MERCHANT.equals(member.getBusinessType())){
			MerchantMemberType memberType = findMerchantMemberTypeById(member.getMemberType().getId());
			if(memberType == null || !memberType.getMerchant().getId().equals(member.getBusinessId())){
				return false;
			}
		}else if(BusinessType.SHOP.equals(member.getBusinessType())){
			ShopMemberType memberType = findShopMemberTypeById(member.getMemberType().getId());
			if(memberType == null || !memberType.getShop().getId().equals(member.getBusinessId())){
				return false;
			}
		}
		return true;
	}

	@Override
	@Transactional
	public boolean save(MemberType memberType,Business business) {
		//1.首先校验当前集团、商户能否保存此会员类型
		if(!validateEditableByBusiness(memberType, business)){
			return false;
		}
		
		//2.match with template
		if(!matchMemberTypeWithAttributeTemplate(memberType)){
			return false;
		}
		
		//3.折扣范围校验(页面输入范围 0 < x <= 10)
		if(memberType.isValidDiscount()){
			memberType.setDiscount(memberType.getDiscount().multiply(new BigDecimal(0.1d)).setScale(4, RoundingMode.HALF_UP));
		}else{
			return false;
		}
		
		//4.save MemberType & picture
		boolean result = true;
		Picture coverPicture = memberType.getCoverPicture();
		boolean uploadPicture = coverPicture != null && ArrayUtils.isNotEmpty(coverPicture.getData());
		
		if( memberType instanceof MerchantMemberType){
			MerchantMemberType merchantMemberType = (MerchantMemberType)memberType;
			
			result = result && updateMerchantMemberTypeDefault(merchantMemberType); //覆盖已有Default类型
			result = result && merchantMemberTypeMapper.insert(merchantMemberType) > 0;
			if(uploadPicture){
				coverPicture.setForeignId(merchantMemberType.getId());
				coverPicture.setPictureType(PictureType.MEMBER_TYPE_COVER);
				coverPicture.setDescription(merchantMemberType.getName() + "会员等级封面");
				result = result && pictureService.uploadPicture(coverPicture);
				result = result && merchantMemberTypeMapper.updateCoverPictureById(coverPicture, merchantMemberType.getId()) > 0;
			}
		}else if(memberType instanceof ShopMemberType){
			ShopMemberType shopMemberType = (ShopMemberType)memberType;
			
			result = result && updateShopMemberTypeDefault(shopMemberType); //覆盖已有Default类型
			result = result && shopMemberTypeMapper.insert(shopMemberType) > 0;
			if(uploadPicture){
				coverPicture.setForeignId(shopMemberType.getId());
				coverPicture.setPictureType(PictureType.MEMBER_TYPE_COVER);
				coverPicture.setDescription(shopMemberType.getName() + "会员等级封面");
				result = result && pictureService.uploadPicture(coverPicture);
				result = result && shopMemberTypeMapper.updateCoverPictureById(coverPicture, shopMemberType.getId()) > 0;
			}
		}
		return result;
	}
	
	/**
	 * 集团统一账户
	 * 判断保存的会员名称数据库中是否已经有同名的
	 */
	@Override
	public boolean distinct(MerchantMemberType memberType) {
		boolean result = false;
		//名字去重
		List<MerchantMemberType> typeList = findMerchantMemberTypeListByMerchantId(memberType.getBusinessId());
		if(CollectionUtils.isNotEmpty(typeList)){
			for(MerchantMemberType type : typeList){
				if(StringUtils.equals(type.getName(), memberType.getName())){
					result = true;
					break;
				}
			}
		}
		return result;
	}
	/**
	 * 非集团统一账户
	 */
	@Override
	public boolean distinct(ShopMemberType memberType) {
		boolean result = false;
		//名字去重
		List<ShopMemberType> typeList = findShopMemberTypeListByShopId(memberType.getBusinessId());
		if(CollectionUtils.isNotEmpty(typeList)){
			for(ShopMemberType type : typeList){
				if(StringUtils.equals(type.getName(), memberType.getName())){
					result = true;
					break;
				}
			}
		}
		return result;
	}

	
	@Override
	@Transactional
	public boolean update(MemberType memberType ,Business business) {
		//通过ID取数据库记录，后续操作用数据库的对应memberTypePO代替
		//check if exist by id
		MemberType memberTypePO = null;
		if(memberType instanceof MerchantMemberType){
			memberTypePO = findMerchantMemberTypeById(memberType.getId());
		}else if(memberType instanceof ShopMemberType){
			memberTypePO = findShopMemberTypeById(memberType.getId());
		}
		if(memberTypePO == null){
			return false;
		}
		
		//1.首先校验当前集团、商户能否修改此会员类型
		boolean result = validateEditableByBusiness(memberTypePO, business);
		memberTypePO.setName(memberType.getName());
		memberTypePO.setDiscount(memberType.getDiscount());
		memberTypePO.setDefault(memberType.isDefault());
		memberTypePO.setMemberAttributeTemplate(memberType.getMemberAttributeTemplate());
		memberTypePO.setCoverPicture(memberType.getCoverPicture());
		
		//2.match with template
		result = result && matchMemberTypeWithAttributeTemplate(memberType);
		
		//3.折扣范围校验(页面输入范围 0 < x <= 10)
		result = result && memberType.isValidDiscount();
		if(result){
			memberTypePO.setDiscount(memberTypePO.getDiscount().multiply(new BigDecimal(0.1d)).setScale(4, RoundingMode.HALF_UP));
		}
		
		//4.update MemberType & picture
		Picture coverPicture = memberTypePO.getCoverPicture();
		boolean uploadPicture = coverPicture != null && coverPicture.getData() != null && coverPicture.getData().length > 0;
		if(uploadPicture){
			coverPicture.setForeignId(memberTypePO.getId());
			coverPicture.setPictureType(PictureType.MEMBER_TYPE_COVER);
			coverPicture.setDescription(memberTypePO.getName() + "会员等级封面");
		}
		if( memberTypePO instanceof MerchantMemberType){
			MerchantMemberType merchantMemberType = (MerchantMemberType)memberTypePO;
			
			//名字去重
			List<MerchantMemberType> typeList = findMerchantMemberTypeListByMerchantId(merchantMemberType.getBusinessId());
			if(CollectionUtils.isNotEmpty(typeList)){
				for(MerchantMemberType type : typeList){
					if(StringUtils.equals(type.getName(), merchantMemberType.getName()) && !merchantMemberType.getId().equals(type.getId())){
						result = result && false;
						break;
					}
				}
			}
			
			result = result && updateMerchantMemberTypeDefault(merchantMemberType);
			result = result && merchantMemberTypeMapper.update(merchantMemberType) > 0;
			if(uploadPicture){
				result = result 
						&& pictureService.uploadPicture(coverPicture) 
						&& merchantMemberTypeMapper.updateCoverPictureById(coverPicture, merchantMemberType.getId()) > 0;
			}
		}else if(memberTypePO instanceof ShopMemberType){
			ShopMemberType shopMemberType = (ShopMemberType)memberTypePO;			
			
			//名字去重
			List<ShopMemberType> typeList = findShopMemberTypeListByShopId(shopMemberType.getBusinessId());
			if(CollectionUtils.isNotEmpty(typeList)){
				for(ShopMemberType type : typeList){
					if(StringUtils.equals(type.getName(), shopMemberType.getName()) && !shopMemberType.getId().equals(type.getId())){
						result = result && false;
						break;
					}
				}
			}
			
			result = result && updateShopMemberTypeDefault(shopMemberType);
			result = result && shopMemberTypeMapper.update(shopMemberType) > 0;
			if(uploadPicture){
				result = result
						&& pictureService.uploadPicture(coverPicture)
						&& shopMemberTypeMapper.updateCoverPictureById(coverPicture, shopMemberType.getId()) > 0;
			}
		}
		return result;
	}
	
	@Override
	public boolean delete(MemberType memberType,Business business) {
		//作为参数的memberType可能就只有ID，所以后续操作用数据库的对应memberTypePO代替
		//check if exist by id
		MemberType memberTypePO = null;
		if(memberType instanceof MerchantMemberType){
			memberTypePO = findMerchantMemberTypeById(memberType.getId());
		}else if(memberType instanceof ShopMemberType){
			memberTypePO = findShopMemberTypeById(memberType.getId());
		}
		if(memberTypePO == null){
			return false;
		}
		
		//1.首先校验当前集团、商户能否修改此会员类型
		if(!validateEditableByBusiness(memberTypePO, business)) {
			return false;
		}
		//2.default类型不能删除
		if(memberTypePO.isDefault()) {
			return false;
		}
		//3.该会员类型下存在会员不能删除
		//这个方法移动到isUsed(MemberType memberType)这个方法里面判断了
//		if(memberService.countMemberByMemberType(memberTypePO)>0) {
//			return false;
//		}
		//4.执行删除操作
		if(memberType instanceof MerchantMemberType) {
			return merchantMemberTypeMapper.deletedById(memberType.getId())>0;
		}else if(memberType instanceof ShopMemberType) {
			return shopMemberTypeMapper.deletedById(memberType.getId())>0;
		}
		return false;
	}
	
	/**
	 * 判断是否是改会员类型是否正在使用
	 * 
	 */
	public boolean isUsed(MemberType memberType) {
		MemberType memberTypePO = null;
		if(memberType instanceof MerchantMemberType){
			memberTypePO = findMerchantMemberTypeById(memberType.getId());
		}else if(memberType instanceof ShopMemberType){
			memberTypePO = findShopMemberTypeById(memberType.getId());
		}
		//这个地方判断一下是否为空，防止下面空指针异常，如果为空返回"true",放过去，因为controller的delete方发还会再判断一边的
		if(memberTypePO == null){
			return true;
		}
		//该会员类型下存在会员不能删除
		if(memberService.countMemberByMemberType(memberTypePO)>0) {
			return false;
		}
		return true;
	}
	
	@Transactional
	private boolean updateMerchantMemberTypeDefault(MerchantMemberType merchantMemberType) {
		//验证default类型
		List <MerchantMemberType> list  = findMerchantMemberTypeListWithAttributeTemplateByMerchantId(merchantMemberType.getBusinessId());
		if (list == null || list.isEmpty()) {//首次插入，设为“默认”类型
			merchantMemberType.setDefault(true);
		}else if (merchantMemberType.isDefault()) {//后续插入，若传入为“默认”类型，则覆盖原有的“默认”类型
			boolean result = true;
			for (MerchantMemberType memberT : list) {
				if (!memberT.getId().equals(merchantMemberType.getId())
						&& memberT.isDefault()) {
					memberT.setDefault(false);
					result = result && merchantMemberTypeMapper.update(memberT) > 0;//去除所有merchant_member_type中的“默认”类型
				}
			}
			return result;
		}else if(!merchantMemberType.isDefault()){ //后续插入，若传入为非"默认"类型
			//禁止将现有的”默认“类型切换成非默认
			for(MerchantMemberType memberT : list){
				if(memberT.getId().equals(merchantMemberType.getId()) && memberT.isDefault()){
					return false;
				}
			}
		}
		return true;
	}
	
	@Transactional
	private boolean updateShopMemberTypeDefault(ShopMemberType shopMemberType) {
		//验证default类型
		List <ShopMemberType> list  = findShopMemberTypeListWithAttributeTemplateByShopId(shopMemberType.getBusinessId());
		if (list == null || list.isEmpty()) {//首次插入，设为“默认”类型
			shopMemberType.setDefault(true);
		}else if (shopMemberType.isDefault()) {//后续插入，若传入为“默认”类型，则覆盖原有的“默认”类型
			boolean result = true;
			for (ShopMemberType memberT : list) {
				if (!memberT.getId().equals(shopMemberType.getId())
						&& memberT.isDefault()) {
					memberT.setDefault(false);
					result = result && shopMemberTypeMapper.update(memberT) > 0;//去除所有shop_member_type中的“默认”类型
				}
			}
			return result;
		}else if(!shopMemberType.isDefault()){ //后续插入，若传入为非"默认"类型
			//禁止将现有的”默认“类型切换成非默认
			for(ShopMemberType memberT : list){
				if(memberT.getId().equals(shopMemberType.getId()) && memberT.isDefault()){
					return false;
				}
			}
		}
		return true;
	}

	@Override
	public List<MerchantMemberType> findMerchantMemberTypeListByMerchant(Merchant merchant) throws Exception {
		if(merchant == null || merchant.getMemberCentralManagement() == null || !merchant.getMemberCentralManagement()) {
			throw new Exception();
		}
		return merchantMemberTypeMapper.selectBasicByMerchantId(merchant.getId());
	}

	@Override
	public List<ShopMemberType> findShopMemberTypeListByShop(Shop shop)throws Exception {
		if(shop == null) {
			throw new Exception();
		}
		return shopMemberTypeMapper.selectBasicByShopId(shop.getId());
	}

	@Override
	public Pager<MerchantMemberType> searchMerchantMemberTypeListByMerchant(Pager<MerchantMemberType> pager, 
			MemberTypeSearcher searcher,Merchant merchant) throws Exception {
		if(merchant == null || !merchant.getMemberCentralManagement()) {
			throw new Exception();
		}
		List<MerchantMemberType> merchantMemberTypeList = merchantMemberTypeMapper.selectBasicBySearcher(merchant.getId(),searcher,pager);
		int countMerchantMemberType = merchantMemberTypeMapper.countBasicByMerchantId(merchant.getId(),searcher);
		pager.setTotalCount(countMerchantMemberType);
		pager.setList(merchantMemberTypeList);
		return pager;
	}

	@Override
	public Pager<ShopMemberType> searchShopMemberTypeListByShop(Pager<ShopMemberType> pager,
			MemberTypeSearcher searcher, Shop shop)throws Exception {
		if(shop == null) {
			throw new Exception();
		}
		List<ShopMemberType> shopMemberTypeList = shopMemberTypeMapper.selectBasicBySearcher(shop.getId(),searcher,pager);
		int countShopMemberType = shopMemberTypeMapper.countBasicByShopId(shop.getId(),searcher);
		pager.setTotalCount(countShopMemberType);
		pager.setList(shopMemberTypeList);
		return pager;
	}

}

