package com.xpos.common.persistence.mybatis;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.xpos.common.entity.Shop;
import com.xpos.common.persistence.BaseMapper;


public interface ShopMapper extends BaseMapper<Shop>{

	public List<Shop> selectNormalCouponInfoShopListByCouponInfoId (@Param("couponInfoId")Long couponInfoId);
	
	public List<Shop> selectPackageCouponInfoShopListByCouponInfoId (@Param("couponInfoId")Long couponInfoId);
	
	public int joinMerchant(@Param("shopId")Long shopId, @Param("merchantId")Long merchantId);

	public int quitMerchant(@Param("shopId")Long shopId);
	
	/** 根据商户号搜索下属子商户的ID列表
	 * @param merchantId 商户号
	 * @param isVisibleIgnore 是否忽略商户可见
	 */
	public Long[] selectShopIdsByMerchantId(@Param("merchantId")Long merchantId, @Param("isVisibleIgnore")boolean isVisibleIgnore);

	/** 根据商户号搜索下属子商户
	 * @param merchantId 商户号
	 * @param isVisibleIgnore 是否忽略商户可见
	 */
	public List<Shop> selectShopListByMerchantId(@Param("merchantId")Long merchantId, @Param("isVisibleIgnore")boolean isVisibleIgnore);

	/**
	 * 更新商户的打印服务器配置
	 */
	public int updateShopPrinterServer(Shop shop);

    public int selectSameFullName(@Param("fullName")String fullName,@Param("shopId")String shopId);

}