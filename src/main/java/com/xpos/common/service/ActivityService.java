package com.xpos.common.service;

import com.xpos.common.entity.Activity;
import com.xpos.common.entity.example.ActivityExample;
import com.xpos.common.entity.face.Business;
import com.xpos.common.utils.Pager;

public interface ActivityService {
		
	public Pager<Activity> findActivities(Business business, ActivityExample example, Pager<Activity> pager);
	
	public Activity finsLatestActivity(Business business);
	
	public Pager<Activity> findAllActivitiesByShopId(Long shopId, Pager<Activity> pager);
	
	public Activity findActivityBySerial(String serial);
	
	public Activity findActivityById(Long id);
	
	public Activity findRelatedActivityByCouponInfoId(Long id);
	
	public boolean saveActivity(Activity activity, Long couponInfoId);
	
	public boolean updateActivity(Activity activity);

	public boolean deleteActivityById(Long id);

	public boolean unbindCouponInfo(Long activityId, Long couponInfoId);

	public boolean bindCouponInfo(Long activityId, Long couponInfoId);

	public boolean modifyStatus(Activity activity, boolean syncCoupon);
	
}
