package com.xpos.common.persistence.mybatis;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.ResultType;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.xpos.common.entity.CouponInfo;
import com.xpos.common.entity.face.Business.BusinessType;
import com.xpos.common.persistence.BaseMapper;

public interface CouponInfoMapper extends BaseMapper<CouponInfo>{

	/**
	 * 根据活动选择可绑定的优惠券
	 * @param shopId
	 * @return
	 */
	@ResultMap("DetailMap")
	@Select("select c.* from CouponInfo c,Activity_CouponInfo ac where ac.activity_id = #{activityId} and ac.couponInfo_id = c.id and ac.deleted = false order by c.id desc")
	public List<CouponInfo> selectBindedByActivityId(Long activityId);

	/**
	 * 根据店铺选择可绑定的优惠券
	 * @param shopId
	 * @return
	 */
	@ResultMap("ListMap")
	@Select("select c.* from CouponInfo c where c.businessId = #{shopId} and not exists (select 1 from Activity_CouponInfo ac where ac.couponinfo_id = c.id and ac.deleted = false)")
	public List<CouponInfo> selectBindableByShopId(Long shopId);

	@ResultType(value = Long.class)
	@Select("select c.id from CouponInfo c LEFT JOIN Activity_CouponInfo ac on c.id = ac.couponInfo_id and ac.deleted = false where ac.activity_id = #{activityId} and c.id != #{couponInfoId} and c.deleted = false")
	public List<Long> selectRelatedCouponInfosById(@Param("activityId")Long activityId, @Param("couponInfoId")Long couponInfoId);
	
	@ResultType(value = Long.class)
	@Select("select businessId from CouponInfo_Scope where couponInfo_id = #{cid} and deleted = false")
	public List<Long> selectAllowedShopsByCouponInfoId(@Param("cid") Long couponInfoId);

	public int insertCouponInfoAllowedShop(@Param(value = "cid")Long couponInfoId, @Param(value = "businessId")Long businessId, @Param("businessType")String businessType);
	
	@ResultMap("ListMap")
	@Select("select ci.*, cip.quantity from CouponInfo_Package cip left join CouponInfo ci on ci.id = cip.item_id where cip.parent_id = #{parent_id} and cip.deleted = false and ci.deleted = false")
	public List<CouponInfo> selectItemsByParentId(@Param(value = "parent_id")Long couponInfoId);

	public int insertCouponInfoPackge(@Param(value = "parentId")Long parentId, @Param(value = "itemId")Long itemId, @Param("quantity")Integer quantity);

	@Update("update CouponInfo_Package set deleted = true where parent_id = #{parentId}")
	public int deleteCouponInfoPackge(@Param(value = "parentId")Long parentId);

	@ResultType(value = Long.class)
	@Select("select distinct(parent_id) from CouponInfo_Package where item_id = #{itemId} and deleted = false")
	public List<Long> findPackgeIdsByItemId(@Param("itemId")Long itemId);

	@ResultType(value = Integer.class)
	@Select("SELECT ifnull(SUM(received),0) from CouponInfo where businessId=#{id} and businessType=#{type}")
	public int findCouponCountByShopId(@Param(value = "id")Long id, @Param(value = "type")BusinessType type);

	@Update("update CouponInfo_Scope set deleted = true where couponInfo_id = #{couponInfoId}")
	public int deleteCouponInfoScope(@Param(value = "couponInfoId")Long couponInfoId);

	@Update("update CouponInfo set received=received+#{num} WHERE id=#{id} and deleted=FALSE ${isLimited}")
	public int updateCouponInfoReceived(@Param("num")int num, @Param("id")Long id, @Param("isLimited")String isLimited);

}