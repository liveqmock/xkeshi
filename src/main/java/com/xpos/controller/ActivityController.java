package com.xpos.controller;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.xpos.common.entity.Activity;
import com.xpos.common.entity.CouponInfo;
import com.xpos.common.entity.Picture;
import com.xpos.common.entity.example.ActivityExample;
import com.xpos.common.exception.GenericException;
import com.xpos.common.searcher.ActivitySearcher;
import com.xpos.common.service.ActivityService;
import com.xpos.common.service.CouponService;
import com.xpos.common.service.PictureService;
import com.xpos.common.utils.IDUtil;
import com.xpos.common.utils.Pager;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("activity")
public class ActivityController extends BaseController{
	
	@Resource
	private ActivityService activityService;
	
	@Resource
	private PictureService pictureService;
	
	@Resource
	private CouponService couponService;
	
	@RequestMapping(value="/create", method=RequestMethod.GET)
	public ModelAndView createActivity(ModelAndView mav, @RequestParam(value="coupon", required=false) String couponInfoEid){

        //掩饰用的权限

//        if (!checkRoleAll(Role.ROLE_ADMIN)){
//            mav.setViewName("none_roll");
//            return mav;
//        }

		if(StringUtils.isNotBlank(couponInfoEid)){
            mav.addObject("couponInfoEid", couponInfoEid);
		}
		if( getBusiness() != null ) {
            mav.addObject("shopId", getBusiness().getSelfBusinessId());
		}

        mav.setViewName("activity/activity_input");
		return mav;
	}
	
	@RequestMapping(value="/edit/{eid}", method=RequestMethod.GET)
	public String showEditActivity(Model model, @PathVariable("eid") String eid){
		Long id = IDUtil.decode(eid);
		Activity activity = activityService.findActivityById(id);
		model.addAttribute("activity", activity);
		model.addAttribute("shopId", getBusiness().getSelfBusinessId());
		return "activity/activity_input";
	}
	
	@RequestMapping(value="/detail/{eid}", method=RequestMethod.GET)
	public String showActivityDetail(Model model, @PathVariable("eid") String eid){
		Long id = IDUtil.decode(eid);
		//活动详情（基本信息 + 已绑定优惠券）
		Activity activity = activityService.findActivityById(id);
		if(activity == null){
			throw new GenericException("指定的活动不存在");
		}
		model.addAttribute("activity", activity);
		
		//加载该商户创建的未绑定优惠券
		List<CouponInfo> availableCouponInfos = couponService.findBindableCouponInfosByShopId(activity.getBusinessId());
		model.addAttribute("availableCouponInfos", availableCouponInfos);
		return "activity/activity_detail";
	}
	
	
	@RequestMapping(value="processCreation", method=RequestMethod.POST)
	public String processCreation(Activity activity, MultipartFile picFile, MultipartFile thumbFile, Model model, String couponInfoEid){
		if(picFile != null && !picFile.isEmpty()){
			Picture pic = pictureService.getPictureFromMultipartFile(picFile);
			activity.setPic(pic);
		}
		
		if(thumbFile !=null && !thumbFile.isEmpty()){
			Picture thumb = pictureService.getPictureFromMultipartFile(thumbFile);
			activity.setThumb(thumb);
		}
		
		if(activity.getId() == null){
			activity.setBusiness(getBusiness());
			Long couponInfoId = IDUtil.decode(couponInfoEid);
			activityService.saveActivity(activity, couponInfoId);
		}else{
			Long id = IDUtil.decode(activity.getId().toString());
			activity.setId(id);
			activityService.updateActivity(activity);
		}
		
		return "redirect:/activity/detail/"+activity.getEid();
	}
	
	@RequestMapping(value="list")
	public String findActivityList(Pager<Activity> pager, ActivitySearcher searcher, Model model){
		pager = activityService.findActivities(getBusiness(), (ActivityExample)searcher.getExample(), pager);
		model.addAttribute("pager", pager);
		model.addAttribute("searcher", searcher);
		return "activity/activity_list";
	}
	
	@RequestMapping(value="/delete/{eid}", method=RequestMethod.DELETE)
	public String deleteActivityById(@PathVariable("eid") String eid){
		Long id = IDUtil.decode(eid);
		activityService.deleteActivityById(id);
		return "redirect:/activity/list";
	}
	
	@RequestMapping(value="/{eid}/unbind", method=RequestMethod.GET)
	public String unbindCouponInfoByActivityId(@PathVariable("eid") String activityEid, @RequestParam(value="coupon") String couponInfoEid){
		Long activityId = IDUtil.decode(activityEid);
		Long couponInfoId = IDUtil.decode(couponInfoEid);
		activityService.unbindCouponInfo(activityId, couponInfoId);
		return "redirect:/activity/detail/"+activityEid;
	}
	
	@RequestMapping(value="/{eid}/bind", method=RequestMethod.POST)
	public String bindCouponInfoByActivityId(@PathVariable("eid") String activityEid, @RequestParam(value="coupon") String couponInfoEid){
		Long activityId = IDUtil.decode(activityEid);
		Long couponInfoId = IDUtil.decode(couponInfoEid);
		activityService.bindCouponInfo(activityId, couponInfoId);
		return "redirect:/activity/detail/"+activityEid;
	}
	
	@RequestMapping(value="/{eid}/modify_status", method=RequestMethod.PUT)
	public String modifyStatusByActivityId(@PathVariable("eid") String eid,
			@RequestParam("published")boolean published, @RequestParam(value="syncCoupon", defaultValue="false", required=false) boolean syncCoupon){
		Long id = IDUtil.decode(eid);
		
		//活动详情（基本信息 + 已绑定优惠券）
		Activity persistence = activityService.findActivityById(id);
		
		Activity activity = new Activity();
		activity.setId(persistence.getId());
		activity.setPublished(published);
		activity.setCouponInfos(persistence.getCouponInfos());
		try{
			activityService.modifyStatus(activity, syncCoupon);
		}catch(Exception e){
			logger.error("更新活动状态失败", e);
			throw new GenericException("更新活动状态失败");
		}
		return "redirect:/activity/detail/"+eid;
	}
}
