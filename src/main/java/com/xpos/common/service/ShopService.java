package com.xpos.common.service;

import com.xpos.common.entity.Article;
import com.xpos.common.entity.Contact;
import com.xpos.common.entity.Shop;
import com.xpos.common.entity.ShopInfo;
import com.xpos.common.entity.example.ShopExample;
import com.xpos.common.entity.face.Business;
import com.xpos.common.entity.face.Business.BusinessType;
import com.xpos.common.utils.Pager;

import java.util.List;

public interface ShopService {

	public boolean saveShop(Shop shop);
	
	public boolean updateShop(Shop shop);
	
	/** 按ID查询商户（仅限前台可见，即visible = true） */
	public Shop findShopById(Long id);
	
	/** 按ID查询商户（忽略是否前台可见） */
	public Shop findShopByIdIgnoreVisible(Long id);

	/** 按集团ID查询关联的所有子商户的Id
	 * @param merchantId 集团ID
	 * @param isVisibleIgnore 是否忽略隐藏的子商户（true:找出所有子商户，false：仅限visible=true的商户）
	 */
	public Long[] findShopIdsByMerchantId(Long merchantId, boolean isVisibleIgnore);

	/** 按集团ID查询关联的所有子商户
	 * @param merchantId 集团ID
	 * @param isVisibleIgnore 是否忽略隐藏的子商户（true:找出所有子商户，false：仅限visible=true的商户）
	 */
	public List<Shop> findShopListByMerchantId(Long merchantId, boolean isVisibleIgnore);
	
	/** 搜索商户列表,后台标识为隐藏的商铺不显示 */
	public Pager<Shop> findShopList(Pager<Shop> pager, ShopExample example);
	
	/** 搜索商户列表,包含隐藏的商户 */
	public Pager<Shop> findShopListIgnoreVisible(Pager<Shop> pager, ShopExample example);
	
	/** 批量修改商户是否公开、隐藏 */
	public boolean batchSetVisible(Long[] shopIds, boolean visible);
	
	/** 查询优惠券可使用商户范围 */
	public List<Shop> findShopListByCouponInfoId(Long couponInfoId);
	
	/** 商户关联到集团下（如果当前已关联到某个集团，则覆盖更新） */
	public boolean joinMerchant(Long shopId, Long merchantId);

	/** 商户取消关联集团 */
	public boolean quitMerchant(Long shopId);
	
	/*********************SHOP相关对象***********************/
	public List<Contact> findContactsByShopId(Long id);
	
	public List<Article> findArticlesByShopId(Long id);
	
	public boolean saveOrUpdateArticle(Article article);
	
	public ShopInfo findShopInfoByShopId(Long id);
	
	public String saveOrUpdateShopInfo(ShopInfo shopInfo);
	
	public String findSmsSuffixByBusinessTypeAndBusinessId(BusinessType businessType, Long businessId);
	
	public String findSmsChannelByBusinessTypeAndBusinessId(BusinessType businessType, Long businessId);
	
	public String findSmsChannelByBusiness(Business business);

	public boolean updateShopPrinterService(Shop shop);

    public boolean validateFullNameIsExist(String fullName,String shopId);

    ShopInfo getShopInfoByShopId(Long id);

    String saveOrUpdateShopInfoCash(ShopInfo shopInfo);
}
