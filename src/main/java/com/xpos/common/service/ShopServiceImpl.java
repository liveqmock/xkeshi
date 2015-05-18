package com.xpos.common.service;

import com.xpos.common.entity.*;
import com.xpos.common.entity.CouponInfo.CouponInfoType;
import com.xpos.common.entity.Picture.PictureType;
import com.xpos.common.entity.Position.PositionType;
import com.xpos.common.entity.example.*;
import com.xpos.common.entity.example.Example.Criteria;
import com.xpos.common.entity.face.Business;
import com.xpos.common.entity.face.Business.BusinessType;
import com.xpos.common.persistence.mybatis.*;
import com.xpos.common.utils.Pager;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ShopServiceImpl implements ShopService {
	
	@Autowired
	private ShopMapper shopMapper;
	
	@Autowired
	private ShopInfoMapper shopInfoMapper;
	
	@Autowired
	private PositionMapper positionMapper;

	@Autowired
	private ContactMapper contactMapper;
	
	@Autowired
	private ArticleMapper articleMapper;
	
	@Autowired
	private CouponInfoMapper couponInfoMapper;
	
	@Autowired
	private OrderMapper orderMapper;
	
	@Autowired
	private PictureService pictureService;
	
	@Autowired
	private MerchantService merchantService;
	
	@Override
	@Transactional
	public boolean saveShop(Shop shop) {
		boolean result = true;
		shop.setStars(3.00D);
		result = shopMapper.insert(shop) > 0;
		
		//保存联系人
		if(!CollectionUtils.isEmpty(shop.getContacts())){
			for(Contact contact : shop.getContacts()){
				contact.setBusiness(shop);
				result = result && contactMapper.insert(contact) > 0;
			}
		}
		
		//保存头像、banner
		if(shop.getAvatar() != null){
			Picture avatar = shop.getAvatar();
			avatar.setForeignId(shop.getId());
			avatar.setPictureType(PictureType.SHOP_AVATAR);
			result = result && pictureService.uploadPicture(avatar);
		}
		
		if(shop.getBanner() != null){
			Picture banner = shop.getBanner();
			banner.setForeignId(shop.getId());
			banner.setPictureType(PictureType.SHOP_BANNER);
			result = result && pictureService.uploadPicture(banner);
		}
		
		//Position
		if(shop.getPosition() != null){
			Position ps = shop.getPosition();
			ps.setType(PositionType.SHOP);
			ps.setForeignId(shop.getId());
			result = result && positionMapper.insert(ps) > 0;
			shop.setPosition(ps);
		}
		
		//更新banner_id, avatar_id
		result = result && shopMapper.updateByPrimaryKey(shop) > 0;
		result = result && orderMapper.initOrderShopCounter(shop.getId()) > 0;
		return result;
	}

	@Override
	@Transactional
	public boolean updateShop(Shop shop){
		boolean result = true;
		
		//保存联系人
		if(!CollectionUtils.isEmpty(shop.getContacts())){
			for(Contact contact : shop.getContacts()){
				contact.setBusiness(shop);
				if(contact.getId()!=null){
					result = result && contactMapper.updateByPrimaryKey(contact) > 0;
				}else{
					result = result && contactMapper.insert(contact) > 0;
				}
			}
		}
		
		//更新头像、banner（上传新图片，但仍保留原有图片并不删除记录，只是将Shop表关联到新图片的ID）
		if(shop.getAvatar() != null){
			Picture avatar = shop.getAvatar();
			avatar.setForeignId(shop.getId());
			avatar.setPictureType(PictureType.SHOP_AVATAR);
			result = result && pictureService.uploadPicture(avatar);
		}
		
		if(shop.getBanner() != null && shop.getCategory().getBanner()==null){
			Picture banner = shop.getBanner();
			banner.setForeignId(shop.getId());
			banner.setPictureType(PictureType.SHOP_BANNER);
			result = result && pictureService.uploadPicture(banner);
		}
		
		//Position
		if(shop.getPosition() != null){
			//删除原先Position
			PositionExample example = new PositionExample();
			example.createCriteria().addCriterion("deleted=",false)
									.addCriterion("foreignId="+shop.getId())
									.addCriterion("type='SHOP'");
			positionMapper.deleteByExample(example);
			Position ps = shop.getPosition();
			ps.setType(PositionType.SHOP);
			ps.setForeignId(shop.getId());
			result = result && positionMapper.insert(ps) > 0;
			shop.setPosition(ps);
		}
		
		//更新banner_id, avatar_id
		result = result && shopMapper.updateByPrimaryKey(shop) > 0;
		return result;
	}
	
	@Override
	public Shop findShopById(Long id){
		return findShopById(id, false);
	}
	
	@Override
	public Shop findShopByIdIgnoreVisible(Long id) {
		return findShopById(id, true);
	}
	
	private Shop findShopById(Long id, boolean isVisibleIgnore){
		ShopExample example = new ShopExample();
		example.createCriteria().addCriterion("id = ", id);
		if(!isVisibleIgnore){
			example.appendCriterion("visible = ", true);
		}
		return shopMapper.selectOneByExample(example);
	}
	
	@Override
	public Long[] findShopIdsByMerchantId(Long merchantId, boolean isVisibleIgnore) {
		return shopMapper.selectShopIdsByMerchantId(merchantId, isVisibleIgnore);
	}
	
	@Override
	public List<Shop> findShopListByMerchantId(Long merchantId, boolean isVisibleIgnore) {
		return shopMapper.selectShopListByMerchantId(merchantId, isVisibleIgnore);
	}
	
	@Override
	public Pager<Shop> findShopList(Pager<Shop> pager, ShopExample example){
		return findShopList(pager, example, false);
	}
	
	@Override
	public Pager<Shop> findShopListIgnoreVisible(Pager<Shop> pager, ShopExample example){
		return findShopList(pager, example, true);
	}
	
	private Pager<Shop> findShopList(Pager<Shop> pager, ShopExample example, boolean isVisibleIgnore) {
		if(example == null){
			example = new ShopExample();
		}
		Criteria criterion = example.appendCriterion("deleted = ", false);
		if(!isVisibleIgnore){
			criterion.addCriterion("visible = ", true);
		}
		
		List<Shop> list = shopMapper.selectByExample(example, pager);
		int totalCount = shopMapper.countByExample(example);
		pager.setTotalCount(totalCount);
		pager.setList(list);
		
		return pager;
	}
	
	@Override
	@Transactional
	public boolean batchSetVisible(Long[] shopIds, boolean visible) {
		boolean result = true;
		for(Long id : shopIds){
			Shop shop = shopMapper.selectByPrimaryKey(id);
			shop.setVisible(visible);
			result = result && shopMapper.updateByPrimaryKey(shop) > 0;
		}
		return result;
	}
	
	@Override
	public List<Shop> findShopListByCouponInfoId(Long couponInfoId) {
		CouponInfo ci = couponInfoMapper.selectByPrimaryKey(couponInfoId);
		if(ci.getType().equals(CouponInfoType.PACKAGE)) {
			return shopMapper.selectPackageCouponInfoShopListByCouponInfoId(couponInfoId);
		}else {
			return shopMapper.selectNormalCouponInfoShopListByCouponInfoId(couponInfoId);
		}
	}
	
	@Override
	public boolean joinMerchant(Long shopId, Long merchantId) {
		Shop shop = shopMapper.selectByPrimaryKey(shopId);
		Merchant merchant = merchantService.findMerchant(merchantId);
		if(shop != null && merchant != null){ //商户存在
			if(shop.getMerchant() != null && shop.getMerchant().getId().equals(merchantId)){
				//关联相同集团，直接返回
				return true;
			}
			return shopMapper.joinMerchant(shopId, merchant.getId()) > 0;
		}
		return false;
	}
	
	@Override
	public boolean quitMerchant(Long shopId) {
		Shop shop = shopMapper.selectByPrimaryKey(shopId);
		if(shop != null && shop.getMerchant() != null){ //商户存在，且已关联某个集团
			//取消关联
			return shopMapper.quitMerchant(shopId) > 0;
		}
		return false;
	}
	
	/********************* SHOP关联对象相关方法 ***********************/
	@Override
	public List<Contact> findContactsByShopId(Long id) {
		ContactExample example = new ContactExample();
		example.createCriteria().addCriterion("businessId=",id)
								.addCriterion("businessType=", BusinessType.SHOP.toString())
								.addCriterion("deleted=",false);
		return contactMapper.selectByExample(example, null);
	}

	@Override
	public List<Article> findArticlesByShopId(Long id) {
		ArticleExample example = new ArticleExample();
		example.createCriteria().addCriterion("shop_id=", id)
							    .addCriterion("deleted=", false);
		return articleMapper.selectByExample(example, null);
	}

	@Override
	public boolean saveOrUpdateArticle(Article article) {
		if(article.getId() != null){
			return articleMapper.updateByPrimaryKey(article) > 0;
		}else{
			return articleMapper.insert(article) > 0;
		}
	}
	
	@Override
	public ShopInfo findShopInfoByShopId(Long id) {
		if(id == null){
			return null;
		}
		ShopInfoExample example = new ShopInfoExample();
		example.createCriteria().addCriterion("shopId=",id)
		.addCriterion("deleted=", false);
		return shopInfoMapper.selectOneByExample(example);
	}
	
	@Override
	public String saveOrUpdateShopInfo(ShopInfo shopInfo) {
		boolean result = false;
		ShopInfo sif = findShopInfoByShopId(shopInfo.getShopId());
		if(sif != null) {
			result = shopInfoMapper.updateByShopId(shopInfo) > 0;
		} else {
			result = shopInfoMapper.insert(shopInfo) > 0;
		}
		if (!result) {
			return "修改商户信息失败";
		}
		return null;
	}

	@Override
	public String findSmsSuffixByBusinessTypeAndBusinessId(BusinessType businessType, Long businessId) {
		String smsSuffix = "爱客仕xpos";
		/*if(BusinessType.SHOP.equals(businessType)) {
			Shop shop = findShopByIdIgnoreVisible(businessId);
			if(shop == null){
				return smsSuffix;
			}else{
				Merchant m = shop.getMerchant();
				if(m != null && m.getBalanceCentralManagement()){
					smsSuffix = m.getSmsSuffix();
				}else{
					ShopInfo shopInfo = findShopInfoByShopId(businessId);
					smsSuffix = shopInfo != null ? shopInfo.getSmssuffix() : "";
				}
			}
		}else if(BusinessType.MERCHANT.equals(businessType)) {
			Merchant merchant = merchantService.findMerchant(businessId);
			if(merchant != null) {
				smsSuffix = merchant.getSmsSuffix();
			}
		}*/
		return smsSuffix;
	}

	@Override
	public String findSmsChannelByBusinessTypeAndBusinessId(BusinessType businessType, Long businessId) {
		if(BusinessType.MERCHANT.equals(businessType)) {
			Merchant merchant = merchantService.findMerchant(businessId);
			return findSmsChannelByBusiness(merchant);
		}else if(BusinessType.SHOP.equals(businessType)) {
			Shop shop = findShopByIdIgnoreVisible(businessId);
			return findSmsChannelByBusiness(shop);
		}
		return null;
	}

	@Override
	public String findSmsChannelByBusiness(Business business) {
		/*if(business instanceof Merchant){
			return ((Merchant)business).getSmsChannel();
		}else if(business instanceof Shop){
			Shop shop = (Shop)business;
			if(shop.getMerchant() != null && shop.getMerchant().getBalanceCentralManagement()){ //如果是集团统一管理账户，用集团通道发送
				return shop.getMerchant().getSmsChannel();
			}else{
				ShopInfo shopInfo = findShopInfoByShopId(shop.getId());
				return shopInfo == null ? null : shopInfo.getSmsChannel();
			}
		}*/
		return "xpos";
	}

	@Override
	public boolean updateShopPrinterService(Shop shop) {
		return  shopMapper.updateShopPrinterServer(shop)>0;
	}

    /**
     * 获取shopInfo，如果为空，则创建后在返回
     *
     * @param id
     * @return
     */
    public ShopInfo getShopInfoByShopId(Long id) {
        ShopInfo info = findShopInfoByShopId(id);
        if(info == null){
            ShopInfo po = new ShopInfo();
            po.setShopId(id);
            po.setEnableCash(1);
            shopInfoMapper.insert(po);
            return po;
        }
        return info;
    }

    @Override
    public String saveOrUpdateShopInfoCash(ShopInfo shopInfo) {
        boolean result = false;
        ShopInfo sif = findShopInfoByShopId(shopInfo.getShopId());
        if(sif != null) {
            result = shopInfoMapper.updateShopInfoCash(shopInfo) > 0;
        } else {
            result = shopInfoMapper.insert(shopInfo) > 0;
        }
        if (!result) {
            return "修改商户信息失败";
        }
        return null;
    }

    @Override
    public boolean validateFullNameIsExist(String fullName,String shopId) {
        return shopMapper.selectSameFullName(fullName,shopId) > 0;
    }
}

