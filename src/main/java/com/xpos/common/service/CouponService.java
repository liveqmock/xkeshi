package com.xpos.common.service;

import java.util.List;
import java.util.Map;

import com.xpos.common.entity.Coupon;
import com.xpos.common.entity.Coupon.CouponStatus;
import com.xpos.common.entity.CouponInfo;
import com.xpos.common.entity.Shop;
import com.xpos.common.entity.ThirdpartyCoupon;
import com.xpos.common.entity.ThirdpartyCouponInfo;
import com.xpos.common.entity.example.CouponExample;
import com.xpos.common.entity.example.CouponInfoExample;
import com.xpos.common.entity.face.Business;
import com.xpos.common.entity.face.Business.BusinessType;
import com.xpos.common.entity.security.User;
import com.xpos.common.searcher.CouponInfoSearcher;
import com.xpos.common.utils.Pager;

public interface CouponService {
	/* ==================== coupon info related ==================== */
	public Pager<CouponInfo> findCouponInfos(Business business,CouponInfoExample example, Pager<CouponInfo> pager);
	
	public Pager<CouponInfo> findCouponInfoByIds(CouponInfoExample example, Pager<CouponInfo> pager);
	
	public Pager<CouponInfo> findPublishedCouponInfosByShopId(Business business,CouponInfoExample example, Pager<CouponInfo> pager);
	
	public List<CouponInfo> findMarketableCouponInfosByShopId(Long id, Pager<CouponInfo> pager);
	
	public List<CouponInfo> findBindableCouponInfosByShopId(Long shopId);
	
	public CouponInfo findCouponInfoById(Long id);
	
	public boolean saveCouponInfo(CouponInfo couponInfo, Long activityId);
	
	public boolean updateCouponInfo(CouponInfo couponInfo);

	public List<Long> findRelatedCouponInfos(Long activityId, Long couponInfoId);
	
	public boolean deleteCouponInfoById(Long id);
	
	/***
	 * 用户优惠券统计
	 * @param user
	 * @param business
	 * @return
	 */
	public Map<String, Integer>  couponCountMap(User user  ,Business business ,CouponExample  example);
	
	/*  -------------------------CouponInfo 集团形式下查询-------------------------------------------  */
	/**  集团下--热门销售查询 
	 *   businessType不能为空
	 *   businessId  不能为空
	 *  */
    public Pager<CouponInfo> merchantFindHotCouponInfo(Pager<CouponInfo>  pager,CouponInfoSearcher  couponInfoSearcher );
	
	
	
	/* ==================== coupon related ==================== */
	public int countByCouponStatus(CouponInfo couponInfo, List<CouponStatus> status);

	public Pager<Coupon> couponUsageStatistic(CouponExample example, Pager<Coupon> pager);

	public Coupon findCouponById(Long id);

	public Coupon findCouponByCode(String couponCode);
	
	public Pager<Coupon> findCoupons(CouponExample example, Pager<Coupon> pager);

	public boolean consumeCoupon(Coupon coupon);

	public Coupon findCouponByMobile(Long couponInfoId, String mobile);

	/** 查询用户的优惠券  */
	public Pager<Coupon>  findUserByBusinessCoupons(User user  , Business  business ,CouponExample  example  , Pager<Coupon>  pager);
	
	
	/**
	 * 保存新建的优惠券
	 * @param coupon 新建优惠券
	 * @param isMarkReceived 是否扣除CouponInfo库存（received字段）
	 */
	public boolean saveCoupon(Coupon coupon, boolean isMarkReceived);
	
	public Pager<CouponInfo> getCouponList(Pager<CouponInfo> pagination, CouponInfoExample example);
	
	public Pager<Coupon> getCouponListByPhone(Pager<Coupon> pagination, long mobile, CouponExample example);
	
	public String buy(Long cid, String mobile);
	
	public Integer countShopCoupons(Shop shop);
	
	public Integer countShopActivities(Shop shop);
	
	public ThirdpartyCouponInfo findThirdpartyCouponInfoById(Long id);
	
	public ThirdpartyCoupon findThirdCouponByMobile(Long thirdcouponInfoId, String mobile);
	
	public boolean saveThirdpatyCoupon(ThirdpartyCoupon tcoupon);
	
	public ThirdpartyCoupon findThirdCouponById(Long id);
	
	public Pager<ThirdpartyCoupon> getThirdCouponListByPhone(Pager<ThirdpartyCoupon> pager,Long mobile);
	
	public int countCoupons(CouponExample example);
	
	public int countCouponInfos(CouponInfoExample example);
	
	public Pager<CouponInfo> findCouponInfos(CouponInfoExample example, Pager<CouponInfo> pager);
	
	public void updateCouponStatus();
	
	public boolean batchUpdateCoupon(List<Coupon> coupon);
	
	public boolean updateCoupon(Coupon coupon);
	
	/* ==================== couponInfo package related ==================== */
	public boolean saveCouonInfoPackageRelation(CouponInfo parentCouponInfo);
	
	public boolean deleteCouponInfoPackageRelationById(Long parentId);
	
	public List<Long> findPackageIdsByItemId(Long id);
	
	public int findCouponCountByShopId(Long id, BusinessType shop);
	
	boolean deleteCouponInfoScopeRelationById(Long couponInfoId);
	
	public List<Coupon> findCouponPackageBySerial(String serial);
	
	public Pager<CouponInfo> merchantFindRelateCouponInfo(Pager<CouponInfo> pager,
			CouponInfoSearcher couponInfoSearcher);
	
	public Integer countShopCouponsVisible(Shop shop);
	
	public Pager<CouponInfo> findPublishedAndVisibleCouponInfosByShopId(
			Business business,CouponInfoExample example, Pager<CouponInfo> pager);

	public boolean updateCouponInfoReceived(int num, CouponInfo ci);

	public ThirdpartyCoupon findThirdCouponByCodeAndPaw(String cad);

	public List<String> getLimtPayTimeList();

	public CouponInfo findCouponInfoByIdAndBusiness(Long id,
			String businessType, String businessId);

	public List<CouponInfo> findCouponInfoByIds(String[] cidList);


}
