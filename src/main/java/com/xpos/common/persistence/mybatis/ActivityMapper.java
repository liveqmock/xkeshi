package com.xpos.common.persistence.mybatis;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.xpos.common.entity.Activity;
import com.xpos.common.persistence.BaseMapper;

public interface ActivityMapper extends BaseMapper<Activity>{

	
	@Update("update Activity_CouponInfo set deleted = true where activity_id = #{activityId} and couponinfo_id = #{couponInfoId} and deleted = false")
	public int unbindCouponInfoById(@Param(value = "activityId")Long activityId, @Param(value = "couponInfoId")Long couponInfoId);

	@Insert("insert Activity_CouponInfo(activity_id, couponinfo_id) values(#{activityId}, #{couponInfoId})")
	public int bindCouponInfoById(@Param(value = "activityId")Long activityId, @Param(value = "couponInfoId")Long couponInfoId);

	@ResultMap("DetailMap")
	@Select("select * from Activity a left join Activity_CouponInfo ac on a.id = ac.activity_id and ac.deleted = false where ac.couponInfo_id = #{couponInfoId} and a.deleted = false")
	public Activity findRelatedActivityByCouponInfoId(Long couponInfoId);
	
	/**
	 * 统计商户的活动的数量-- 集团管理下适用商户的优惠券被添加进活动的优惠券中。
	 */
	@Select("SELECT IFNULL((count1.sum + count2.sum), 0) FROM ( SELECT COUNT(a.id) sum FROM Activity a WHERE "
     +" ( a.published = TRUE AND a.deleted = FALSE AND ( businessId = #{businessId} AND businessType = 'SHOP' 	) ) ) count1,"
     +" ( SELECT COUNT(DISTINCT ac.activity_id) sum FROM CouponInfo_Scope cs LEFT OUTER JOIN Activity_CouponInfo ac "
     +" ON cs.couponInfo_id = ac.couponInfo_id LEFT OUTER JOIN CouponInfo ci ON ci.id = cs.couponInfo_id WHERE "
     +" cs.businessId = #{businessId} AND cs.deleted = FALSE AND ci.published = TRUE AND ci.deleted = FALSE 	AND ac.deleted = FALSE ) count2")
	public int countActivity(Long businessId);
	
}