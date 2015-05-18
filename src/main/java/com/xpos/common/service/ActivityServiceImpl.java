package com.xpos.common.service;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xpos.common.entity.Activity;
import com.xpos.common.entity.CouponInfo;
import com.xpos.common.entity.Picture;
import com.xpos.common.entity.Picture.PictureType;
import com.xpos.common.entity.example.ActivityExample;
import com.xpos.common.entity.example.PictureExample;
import com.xpos.common.entity.face.Business;
import com.xpos.common.entity.face.Business.BusinessModel;
import com.xpos.common.exception.AssociationException;
import com.xpos.common.persistence.mybatis.ActivityMapper;
import com.xpos.common.persistence.mybatis.CouponInfoMapper;
import com.xpos.common.persistence.mybatis.PictureMapper;
import com.xpos.common.utils.BusinessSQLBuilder;
import com.xpos.common.utils.IDUtil;
import com.xpos.common.utils.Pager;

@Service
public class ActivityServiceImpl implements ActivityService{
	
	@Resource
	private ActivityMapper activityMapper;

	@Resource
	private PictureMapper pictureMapper;
	
	@Resource
	private PictureService pictureService;
	
	@Resource
	private CouponInfoMapper couponInfoMapper;
	
	@Override
	public Pager<Activity> findActivities(Business business, ActivityExample example, Pager<Activity> pager) {
		if(example == null){
			example = new ActivityExample();
		}
		example.appendCriterion("deleted=", false);
		
		example.appendCriterion(BusinessSQLBuilder.getBusinessByShopSQL(business.getSelfBusinessType(), business.getSelfBusinessId()));
		
		List<Activity> list = activityMapper.selectByExample(example, pager);
		int totalCount = activityMapper.countByExample(example);
		pager.setTotalCount(totalCount);
		pager.setList(list);
		
		return pager;
	}

	@Override
	public Pager<Activity> findAllActivitiesByShopId(Long shopId, Pager<Activity> pager) {
		ActivityExample example = new ActivityExample();
		example.createCriteria().addCriterion("businessId = ", shopId).addCriterion("deleted = ", false);
		List<Activity> list = activityMapper.selectByExample(example, pager);
		int totalCount = activityMapper.countByExample(example);
		pager.setTotalCount(totalCount);
		pager.setList(list);
		return pager;
	}

	@Override
	public Activity findActivityBySerial(String serial) {
		if(StringUtils.isBlank(serial)){
			return null;
		}
		//convert serial to id
		Long id = IDUtil.decode(serial);
		return findActivityById(id);
	}

	@Override
	public Activity findActivityById(Long id) {
		ActivityExample example = new ActivityExample();
		example.createCriteria().addCriterion("id = ", id).addCriterion("deleted = ", false);
		Activity activity = activityMapper.selectOneByExample(example);
		return activity;
	}

	@Override
	public Activity findRelatedActivityByCouponInfoId(Long couponInfoId) {
		return activityMapper.findRelatedActivityByCouponInfoId(couponInfoId);
	}
	
	@Override
	@Transactional
	public boolean saveActivity(Activity activity, Long couponInfoId) {
		boolean result = false;
		boolean needUpdate = false;
		
		activity.setPublished(false);
		result = activityMapper.insert(activity) > 0;
		
		//保存详情大图、缩略图
		if(activity.getPic() != null){
			Picture pic = activity.getPic();
			pic.setForeignId(activity.getId());
			pic.setPictureType(PictureType.ACTIVITY_PIC);
			result = pictureService.uploadPicture(pic);
			if(result)
				needUpdate = true;
		}
		
		if(activity.getThumb() != null){
			Picture thumb = activity.getThumb();
			thumb.setForeignId(activity.getId());
			thumb.setPictureType(PictureType.ACTIVITY_THUMB);
			result = pictureService.uploadPicture(thumb);
			if(result)
				needUpdate = true;
		}
		
		//更新pic_id, thumb_id
		if(needUpdate)
			activityMapper.updateByPrimaryKey(activity);
		
		//指定优惠券直接关联到新的活动
		if(couponInfoId != null && couponInfoId > 0){
			Activity relatedActivity = findRelatedActivityByCouponInfoId(couponInfoId);//加载优惠券已关联的活动
			if(relatedActivity != null){
				//先解绑已关联活动
				if(!unbindCouponInfo(relatedActivity.getId(), couponInfoId)){
					throw new RuntimeException("解绑当前已关联活动失败");
				}
			}
			activityMapper.bindCouponInfoById(activity.getId(), couponInfoId);//重新绑定新建的活动
		}
		return result;
	}

	@Override
	public boolean updateActivity(Activity activity) {
		boolean result = false;
		
		//保存头像、banner
		if(activity.getPic() != null){
			Picture pic = activity.getPic();
			pic.setForeignId(activity.getId());
			pic.setPictureType(PictureType.ACTIVITY_PIC);
			result = pictureService.uploadPicture(pic);
			
			//更新数据库Picture表
			PictureExample example = new PictureExample();
			example.createCriteria()
					.addCriterion("foreignId = ", activity.getId())
					.addCriterion("pictureType = ", PictureType.ACTIVITY_PIC.toString());
			Picture persistence = pictureMapper.selectOneByExample(example);
			if(persistence != null){
				persistence.setOriginalName(pic.getOriginalName());
				persistence.setName(pic.getName());
				activity.setPic(persistence);
				result = result & pictureMapper.updateByPrimaryKey(persistence) == 1;
			}else{
				result = result & pictureMapper.insert(pic) > 0;
			}
		}
		
		if(activity.getThumb() != null){
			Picture thumb = activity.getThumb();
			thumb.setForeignId(activity.getId());
			thumb.setPictureType(PictureType.ACTIVITY_THUMB);
			result = pictureService.uploadPicture(thumb);
			
			//更新数据库Picture表
			PictureExample example = new PictureExample();
			example.createCriteria()
					.addCriterion("foreignId = ", activity.getId())
					.addCriterion("pictureType = ", PictureType.ACTIVITY_THUMB.toString());
			Picture persistence = pictureMapper.selectOneByExample(example);
			if(persistence != null){
				persistence.setOriginalName(thumb.getOriginalName());
				persistence.setName(thumb.getName());
				activity.setThumb(persistence);
				result = result & pictureMapper.updateByPrimaryKey(persistence) == 1;
			}else{
				result = result & pictureMapper.insert(thumb) > 0;
			}
		}
		
		result = activityMapper.updateByPrimaryKey(activity) > 0;
		return result;
	}

	@Override
	public boolean deleteActivityById(Long id) {
		Activity activity = new Activity();
		activity.setId(id);
		activity.setDeleted(true);
		return activityMapper.updateByPrimaryKey(activity) == 1;
	}

	@Override
	public boolean unbindCouponInfo(Long activityId, Long couponInfoId) {
		Activity activity = findActivityById(activityId);
		if(activity.getPublished() != null && activity.getPublished()){
			throw new AssociationException("关联取消失败，活动已开始");//如果活动已开始，不支持解绑优惠券
		}
		return activityMapper.unbindCouponInfoById(activityId, couponInfoId) == 1;
	}
	
	@Override
	public boolean bindCouponInfo(Long activityId, Long couponInfoId) {
		return activityMapper.bindCouponInfoById(activityId, couponInfoId) == 1;
	}

	@Override
	@Transactional
	public boolean modifyStatus(Activity activity, boolean syncCoupon) {
		//1.修改活动状态
		boolean result = activityMapper.updateByPrimaryKey(activity) > 0;
		if(!result){
			throw new RuntimeException();
		}
		
		//2.修改相关优惠券状态
		if(syncCoupon && !CollectionUtils.isEmpty(activity.getCouponInfos())){
			for(CouponInfo persistence : activity.getCouponInfos()){
				CouponInfo couponInfo = new CouponInfo();
				couponInfo.setId(persistence.getId());
				couponInfo.setPublished(activity.getPublished());
				if(couponInfoMapper.updateByPrimaryKey(couponInfo) != 1){;
					throw new RuntimeException();
				}
			}
		}
		
		return true;
	}

	@Override
	public Activity finsLatestActivity(Business business) {
		ActivityExample example = new ActivityExample();
		example.createCriteria().addCriterion("businessId=", business.getAccessBusinessId(BusinessModel.ACTIVITY))
								.addCriterion("businessType=", business.getAccessBusinessType(BusinessModel.ACTIVITY).toString())
								.addCriterion("deleted=",false);
		example.setOrderByClause(" createDate DESC");
		
		return activityMapper.selectOneByExample(example);
	}

}
