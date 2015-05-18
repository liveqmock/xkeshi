package com.xpos.controller;

import com.xkeshi.interceptor.form.AvoidDuplicateSubmission;
import com.xpos.common.entity.*;
import com.xpos.common.entity.Coupon.CouponStatus;
import com.xpos.common.entity.CouponInfo.CouponInfoType;
import com.xpos.common.entity.example.CouponExample;
import com.xpos.common.entity.example.CouponInfoExample;
import com.xpos.common.exception.GenericException;
import com.xpos.common.searcher.CouponInfoSearcher;
import com.xpos.common.searcher.CouponSearcher;
import com.xpos.common.service.*;
import com.xpos.common.utils.IDUtil;
import com.xpos.common.utils.Pager;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.joda.time.DateTime;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import javax.annotation.Resource;
import javax.validation.Valid;

import java.math.BigDecimal;
import java.util.*;

@Controller
@RequestMapping("coupon")
public class CouponController extends BaseController{
	
	@Resource
	private CouponService couponService;
	
	@Resource
	private PictureService pictureService;
	
	@Resource
	private ActivityService activityService;
	
	@Resource
	private TagService tagService  ; 
	
	@Resource
	private ShopService shopService  ;
	
	@AvoidDuplicateSubmission(addToken=true)
	@RequestMapping(value="/add", method=RequestMethod.GET)
	public String createCouponInfo(Model model, @RequestParam(value="activity", required=false) String activityEid){
		if(StringUtils.isNotBlank(activityEid)){
			model.addAttribute("activityEid", activityEid);
		}
		model.addAttribute("shopId", getBusiness().getSelfBusinessId());
		model.addAttribute("couponInfoType", CouponInfoType.NORMAL);
		model.addAttribute("tagMaps", tagService.findAllTags(getBusiness(), null));
		
		if(getBusiness() instanceof Merchant){
			//加载适用商户
			List<Shop> applicableShops = shopService.findShopListByMerchantId(getBusiness().getSelfBusinessId(), true);
			model.addAttribute("applicableShops", applicableShops);
		}
		model.addAttribute("limitPayTimeList", couponService.getLimtPayTimeList());
		return "coupon/coupon_info_input";
	}
	
	@AvoidDuplicateSubmission(addToken=true)
	@RequestMapping(value="/package/add", method=RequestMethod.GET)
	public String createCouponInfoPackage(Model model, @RequestParam(value="activity", required=false) String activityEid){
		if(StringUtils.isNotBlank(activityEid)){
			model.addAttribute("activityEid", activityEid);
		}
		model.addAttribute("shopId", getBusiness().getSelfBusinessId());
		model.addAttribute("couponInfoType", CouponInfoType.PACKAGE);
		model.addAttribute("tagMaps", tagService.findAllTags(getBusiness(), null));
		model.addAttribute("limitPayTimeList", couponService.getLimtPayTimeList());
		return "coupon/coupon_info_input";
	}
	
	@RequestMapping(value="/edit/{eid}", method=RequestMethod.GET)
	public String showEidtCouponInfo(Model model, @PathVariable("eid") String eid){
		Long id = IDUtil.decode(eid);
		CouponInfo couponInfo = couponService.findCouponInfoById(id);
		model.addAttribute("couponInfo", couponInfo);
		if(getBusiness()!=null) {
			model.addAttribute("tagMaps", tagService.findAllTags(getBusiness(), null));
			model.addAttribute("shopId", getBusiness().getSelfBusinessId());
			if(getBusiness() instanceof Merchant){
				//加载适用商户
				List<Shop> applicableShops = shopService.findShopListByMerchantId(getBusiness().getSelfBusinessId(), true);
				model.addAttribute("applicableShops", applicableShops);
			}
		}
		model.addAttribute("couponInfoType", CouponInfoType.NORMAL);
		model.addAttribute("limitPayTimeList", couponService.getLimtPayTimeList());
		
		return "coupon/coupon_info_update";
	}
	
	@RequestMapping(value="/edit/package/{eid}", method=RequestMethod.GET)
	public String showEidtCouponInfoPackage(Model model, @PathVariable("eid") String eid){
		Long id = IDUtil.decode(eid);
		CouponInfo couponInfo = couponService.findCouponInfoById(id);
		model.addAttribute("couponInfo", couponInfo);
		if(getBusiness()!=null) {
			model.addAttribute("tagMaps", tagService.findAllTags(getBusiness(), null));
			model.addAttribute("shopId", getBusiness().getSelfBusinessId());
		}
		model.addAttribute("couponInfoType", CouponInfoType.PACKAGE);
		model.addAttribute("limitPayTimeList", couponService.getLimtPayTimeList());
		return "coupon/coupon_info_update";
	}
	
	@RequestMapping(value="/detail/{eid}", method=RequestMethod.GET)
	public String couponInfoDetail(Model model, @PathVariable("eid") String eid){
		Long id = IDUtil.decode(eid);
		CouponInfo couponInfo = couponService.findCouponInfoById(id);
		model.addAttribute("couponInfo", couponInfo);
		
		//统计优惠券已使用、未使用数量
		List<CouponStatus> status = new ArrayList<>();
		status.add(CouponStatus.USED);
		int usedCouponCount = couponService.countByCouponStatus(couponInfo, status);
		model.addAttribute("usedCouponCount", usedCouponCount);
		
		status.clear();
		status.add(CouponStatus.AVAILABLE);
		int availableCouponCount = couponService.countByCouponStatus(couponInfo, status);
		model.addAttribute("availableCouponCount", availableCouponCount);
		
		//加载商户信息
		List<Shop> shops = shopService.findShopListByCouponInfoId(couponInfo.getId());
		model.addAttribute("shops",  shops);
		
		//加载关联活动
		Activity activity = activityService.findRelatedActivityByCouponInfoId(id);
		model.addAttribute("activity", activity);
		
		//加载该商户创建的其他可绑定的活动
		Pager<Activity> pager1 = new Pager<>();
		pager1.setPageSize(Integer.MAX_VALUE);//一次性加载出所有该商户创建的活动
		List<Activity> availableActivities = activityService.findAllActivitiesByShopId(couponInfo.getBusinessId(), pager1).getList();
		if(activity != null){
			Iterator<Activity> ite = availableActivities.iterator();
			while(ite.hasNext()){
				if(ite.next().getId().equals(activity.getId())){
					ite.remove();
				}
			}
		}
		model.addAttribute("availableActivities", availableActivities);
		
		//加载相同关联活动下的其他优惠券
		if(activity != null){
			Pager<CouponInfo> pager = null;
			List<Long> relatedCouponInfos = couponService.findRelatedCouponInfos(activity.getId(), id);
			if(relatedCouponInfos != null && relatedCouponInfos.size() > 0){
				pager = new Pager<>();
				pager.setPageSize(Integer.MAX_VALUE); //全部加载
				CouponInfoSearcher searcher = new CouponInfoSearcher();
				searcher.setIds(relatedCouponInfos);
				pager = couponService.findCouponInfos(getBusiness(), (CouponInfoExample)searcher.getExample(), pager);
			}
			model.addAttribute("pager", pager);
		}
		return "coupon/coupon_info_detail";
	}
	
	@RequestMapping(value="/detail/package/{eid}", method=RequestMethod.GET)
	public String couponInfoPackageDetail(Model model, @PathVariable("eid") String eid){
		Long id = IDUtil.decode(eid);
		CouponInfo couponInfo = couponService.findCouponInfoById(id);
		model.addAttribute("couponInfo", couponInfo);
		
		//加载关联活动
		Activity activity = activityService.findRelatedActivityByCouponInfoId(id);
		model.addAttribute("activity", activity);
		
		//加载该商户创建的其他可绑定的活动
		Pager<Activity> pager1 = new Pager<>();
		pager1.setPageSize(Integer.MAX_VALUE);//一次性加载出所有该商户创建的活动
		List<Activity> availableActivities = activityService.findAllActivitiesByShopId(couponInfo.getBusinessId(), pager1).getList();
		if(activity != null){
			Iterator<Activity> ite = availableActivities.iterator();
			while(ite.hasNext()){
				if(ite.next().getId().equals(activity.getId())){
					ite.remove();
				}
			}
		}
		model.addAttribute("availableActivities", availableActivities);
		
		//如果领用数量为零，套票内容仍可编辑。加载所有该商户的未过期的优惠券（已发布状态、活动截止时间晚于当前时间）
		if(couponInfo.getReceived() == 0){
			Pager<CouponInfo> pager = new Pager<>();
			pager.setPageSize(Integer.MAX_VALUE);
			CouponInfoExample example = new CouponInfoExample();
			example.createCriteria()
					.addCriterion("published = ", true)
					.addCriterion("type = ", CouponInfoType.NORMAL.toString())
					.addCriterion("endDate > ", new DateTime().toString("yyyy-MM-dd HH:mm:ss"));
			pager = couponService.findCouponInfos(getBusiness(), example, pager);
			model.addAttribute("pager", pager);
		}
		
		//加载适用商户
		Map<String, Shop> applicableShopMap = new HashMap<>();
		for(CouponInfo item : couponInfo.getItems()){
			for(Long shopId : item.getScope()){
				applicableShopMap.put(shopId.toString(), shopService.findShopByIdIgnoreVisible(shopId));
			}
		}
		model.addAttribute("applicableShopMap", applicableShopMap);
		
		//子优惠券使用数量统计
		Map<String, Integer> usedCouponCountMap = new HashMap<>();
		CouponSearcher searcher = new CouponSearcher();
		searcher.setParentId(couponInfo.getId());
		searcher.setType(CouponInfoType.CHILD);
		Set<CouponStatus> status = new HashSet<>();
		status.add(CouponStatus.USED);
		searcher.setStatus(status);
		for(CouponInfo item : couponInfo.getItems()){
			searcher.setCouponInfoId(item.getId());
			int count = couponService.countCoupons((CouponExample)searcher.getExample());
			usedCouponCountMap.put(item.getId().toString(), count);
		}
		model.addAttribute("usedCouponCountMap", usedCouponCountMap);
		
		return "coupon/coupon_info_package_detail";
	}
		
	/** 创建优惠活动  */
	@AvoidDuplicateSubmission(removeToken=true,errorRedirectURL="/coupon/couponInfo/list")
	@RequestMapping(value="processCreation", method=RequestMethod.POST)
	public String processCreation(@Valid CouponInfo couponInfo,  BindingResult result, MultipartFile picFile, MultipartFile thumbFile, String activityEid, RedirectAttributes redirectAttributes){
		if(result.hasErrors()) {
            if(CouponInfoType.NORMAL.equals(couponInfo.getType())) {
                if(couponInfo.getId() == null) {
                    redirectAttributes.addFlashAttribute("status",STATUS_FAILD);
                    redirectAttributes.addFlashAttribute("msg", "电子券创建出错，" + result.getFieldError().getDefaultMessage());
                    return "redirect:/coupon/add";
                }  
            } else if(CouponInfoType.PACKAGE.equals(couponInfo.getType())) {
                if(couponInfo.getId() == null) {
                    redirectAttributes.addFlashAttribute("status",STATUS_FAILD);
                    redirectAttributes.addFlashAttribute("msg", "电子券套票创建出错，" + result.getFieldError().getDefaultMessage());
                    return "redirect:/coupon/package/add";
                }  
            }
        }
        
		if(picFile != null && !picFile.isEmpty()){
			Picture pic = pictureService.getPictureFromMultipartFile(picFile);
			couponInfo.setPic(pic);
		}
		
		if(thumbFile !=null && !thumbFile.isEmpty()){
			Picture thumb = pictureService.getPictureFromMultipartFile(thumbFile);
			couponInfo.setThumb(thumb);
		}
	
		if(getBusiness() instanceof Merchant
				&& CouponInfoType.NORMAL.equals(couponInfo.getType())
				&& CollectionUtils.isEmpty(couponInfo.getScope())){
			Long[] shopIds = shopService.findShopIdsByMerchantId(couponInfo.getBusinessId(), true);
			Set<Long> scope = new HashSet<>();
			Collections.addAll(scope, shopIds);
			couponInfo.setScope(scope);
		}
		couponInfo.setBusinessId(getBusiness().getSelfBusinessId());
		couponInfo.setBusinessType(getBusiness().getSelfBusinessType());
		Long activityId = IDUtil.decode(activityEid);
		boolean saveCouponInfo = couponService.saveCouponInfo(couponInfo, activityId);
		if (saveCouponInfo) {
			redirectAttributes.addFlashAttribute("status", SUCCESS);
			redirectAttributes.addFlashAttribute("msg", "创建成功");
			if(CouponInfoType.NORMAL.equals(couponInfo.getType())){
				return "redirect:/coupon/detail/"+couponInfo.getEid();
			}else{
				return "redirect:/coupon/detail/package/"+couponInfo.getEid();
			}
		}else{
			redirectAttributes.addFlashAttribute("status", FAILD);
			redirectAttributes.addFlashAttribute("msg", "创建失败");
			if(CouponInfoType.NORMAL.equals(couponInfo.getType())){
				return "redirect:/coupon/couponInfo/list";
			}
			return "redirect:/coupon/couponInfoPackage/list";
		}
	}
	
	/** 创建优惠套票 */
	@AvoidDuplicateSubmission(removeToken=true,errorRedirectURL="/coupon/couponInfoPackage/list")
	@RequestMapping(value="process/creation/package", method=RequestMethod.POST)
	public String processCreationByCouponInfoPackage(@Valid CouponInfo couponInfo,  BindingResult result, MultipartFile picFile, MultipartFile thumbFile, String activityEid, RedirectAttributes redirectAttributes){
		return processCreation(couponInfo, result, picFile, thumbFile, activityEid, redirectAttributes);
	}
	
	/** 修改优惠活动 或 优惠套票 */
	@RequestMapping(value="/process_update", method=RequestMethod.POST)
	public String processUpdate(@Valid CouponInfo couponInfo,  BindingResult result, MultipartFile picFile, MultipartFile thumbFile, Model model, String activityEid, RedirectAttributes  redirectAttributes){
		if(result.hasErrors()) {
            if(CouponInfoType.NORMAL.equals(couponInfo.getType())) {
                if(couponInfo.getId() != null) {
                    redirectAttributes.addFlashAttribute("status",STATUS_FAILD);
                    redirectAttributes.addFlashAttribute("msg", "电子券修改出错，" + result.getFieldError().getDefaultMessage());
                    return "redirect:/coupon/edit/"+couponInfo.getId();
                }
            } else if(CouponInfoType.PACKAGE.equals(couponInfo.getType())) {
                if(couponInfo.getId() != null) {
                    redirectAttributes.addFlashAttribute("status",STATUS_FAILD);
                    redirectAttributes.addFlashAttribute("msg", "电子券套票修改出错，" + result.getFieldError().getDefaultMessage());
                    return "redirect:/coupon/edit/package/"+couponInfo.getId();
                }
            }
        }
		
		if(picFile != null && !picFile.isEmpty()){
			Picture pic = pictureService.getPictureFromMultipartFile(picFile);
			couponInfo.setPic(pic);
		}
		
		if(thumbFile !=null && !thumbFile.isEmpty()){
			Picture thumb = pictureService.getPictureFromMultipartFile(thumbFile);
			couponInfo.setThumb(thumb);
		}
	
		if(getBusiness() instanceof Merchant
				&& CouponInfoType.NORMAL.equals(couponInfo.getType())
				&& CollectionUtils.isEmpty(couponInfo.getScope())){
			Long[] shopIds = shopService.findShopIdsByMerchantId(couponInfo.getBusinessId(), true);
			Set<Long> scope = new HashSet<>();
			Collections.addAll(scope, shopIds);
			couponInfo.setScope(scope);
		}
		Long id = IDUtil.decode(couponInfo.getId().toString());
		couponInfo.setId(id);
		boolean updateCouponInfo = couponService.updateCouponInfo(couponInfo);
		if (updateCouponInfo) {
			redirectAttributes.addFlashAttribute("status", SUCCESS);
			redirectAttributes.addFlashAttribute("msg", "修改成功");
			if(CouponInfoType.NORMAL.equals(couponInfo.getType())){
				return "redirect:/coupon/detail/"+couponInfo.getEid();
			}else{
				return "redirect:/coupon/detail/package/"+couponInfo.getEid();
			}
		}else{
			redirectAttributes.addFlashAttribute("status", FAILD);
			redirectAttributes.addFlashAttribute("msg", "修改失败");
			return "redirect:/coupon/couponInfo/list";
		}
	}
	
	/** 优惠券列表 */
	@RequestMapping(value="couponInfo/list")
	public String findCouponInfoList(Pager<CouponInfo> pager, CouponInfoSearcher searcher, Model model){
		searcher.setType(CouponInfoType.NORMAL);
		pager = couponService.findCouponInfos(getBusiness(), (CouponInfoExample)searcher.getExample(), pager);
		if(super.getBusiness() instanceof  Merchant){
			//加载适用商户
			List<Shop> applicableShops = shopService.findShopListByMerchantId(getBusiness().getSelfBusinessId(), true);
			model.addAttribute("applicableShops", applicableShops);
		}
		model.addAttribute("pager", pager);
		model.addAttribute("searcher", searcher);
		return "coupon/coupon_info_list";
	}
	
	@RequestMapping(value="couponInfoPackage/list")
	public String findCouponInfoPackageList(Pager<CouponInfo> pager, CouponInfoSearcher searcher, Model model){
		searcher.setType(CouponInfoType.PACKAGE);
		pager = couponService.findCouponInfos(getBusiness(), (CouponInfoExample)searcher.getExample(), pager);
		model.addAttribute("pager", pager);
		model.addAttribute("searcher", searcher);
		
		Map<String, Shop> applicableShopMap = null;
		if(!CollectionUtils.isEmpty(pager.getList())){
			applicableShopMap = new HashMap<>();
			for(CouponInfo couponInfo : pager.getList()){
				for(CouponInfo item : couponInfo.getItems()){
					for(Long shopId : item.getScope()){
						applicableShopMap.put(shopId.toString(), shopService.findShopByIdIgnoreVisible(shopId));
					}
				}
			}
			
		}
		model.addAttribute("applicableShopMap", applicableShopMap);
		return "coupon/coupon_info_package_list";
	}
	
	/** 优惠券发放、使用数量统计 */
	@RequestMapping(value="/statistic/{eid}", method=RequestMethod.GET)
	public String couponUsageStatistic(@PathVariable("eid") String couponInfoEid, Pager<Coupon> pager, CouponSearcher searcher, Model model){
		Long couponInfoId = IDUtil.decode(couponInfoEid);
		//couponInfo相关信息
		CouponInfo couponInfo = couponService.findCouponInfoById(couponInfoId);
		
		if(couponInfo == null){//统计的优惠券非当前商户创建
			throw new GenericException("优惠券不存在");
		}
		model.addAttribute("couponInfo", couponInfo);
		
		//统计优惠券已使用、未使用数量
		List<CouponStatus> status = new ArrayList<>();
		status.add(CouponStatus.USED);
		int usedCouponCount = couponService.countByCouponStatus(couponInfo, status);
		model.addAttribute("usedCouponCount", usedCouponCount);
		
		status.clear();
		status.add(CouponStatus.AVAILABLE);
		int availableCouponCount = couponService.countByCouponStatus(couponInfo, status);
		model.addAttribute("availableCouponCount", availableCouponCount);
		
		status.clear();
		status.add(CouponStatus.EXPIRED);
		int expiredCouponCount = couponService.countByCouponStatus(couponInfo, status);
		model.addAttribute("expiredCouponCount", expiredCouponCount);
		
		status.clear();
		status.add(CouponStatus.REFUND_APPLY);
		status.add(CouponStatus.REFUND_ACCEPTED);
		status.add(CouponStatus.REFUND_SUCCESS);
		status.add(CouponStatus.REFUND_FAIL);
		int refundCouponCount = couponService.countByCouponStatus(couponInfo, status);
		model.addAttribute("refundCouponCount", refundCouponCount);
		
		model.addAttribute("status", StringUtils.join(searcher.getStatus(), ','));
		
		//加载商户信息
		Set<Long> shops = couponInfo.getScope();
		List<Shop> shopList = new ArrayList<>();
		for(Long s : shops) {
			shopList.add(shopService.findShopByIdIgnoreVisible(s));
		}
		model.addAttribute("shopList", shopList);
		
		//优惠码列表
		searcher.setCouponInfoId(couponInfoId);
		searcher.setType(CouponInfoType.NORMAL);
		pager = couponService.couponUsageStatistic((CouponExample)searcher.getExample(), pager);
		model.addAttribute("pager", pager);
		model.addAttribute("searcher", searcher);
		return "coupon/coupon_info_statistic";
	}
	
	/** 套票优惠券发放、使用数量统计 */
	@RequestMapping(value="/package/{eid}/statistic/{itemEid}", method=RequestMethod.GET)
	public String couponUsageStatistic(@PathVariable("eid") String parentEid, @PathVariable("itemEid") String itemEid,
			Pager<Coupon> pager, CouponSearcher searcher, Model model){
		Long parentId = IDUtil.decode(parentEid);
		Long itemId = IDUtil.decode(itemEid);
		//套票、资料相关信息校验
		CouponInfo parent = couponService.findCouponInfoById(parentId);
		CouponInfo item = null;
		
		if(parent == null || CollectionUtils.isEmpty(parent.getItems())){//统计的优惠券非当前商户创建
			throw new GenericException("优惠券不存在");
		}else{
			boolean isExist = false;
			for(CouponInfo cou : parent.getItems()){
				if(cou.getId().equals(itemId)){
					isExist = true;
					item = cou;
					break;
				}
			}
			if(!isExist){
				throw new GenericException("优惠券不存在");
			}else{
				parent.getItems().clear();
				parent.getItems().add(item); //清空套票关联的其余子票，仅留下当前查询的子票
			}
		}
		model.addAttribute("parent", parent);
		
		//统计优惠券已使用、未使用数量
		List<CouponStatus> status = new ArrayList<>();
		status.add(CouponStatus.USED);
		int usedCouponCount = couponService.countByCouponStatus(parent, status);
		model.addAttribute("usedCouponCount", usedCouponCount);
		
		status.clear();
		status.add(CouponStatus.AVAILABLE);
		int availableCouponCount = couponService.countByCouponStatus(parent, status);
		model.addAttribute("availableCouponCount", availableCouponCount);
		
		status.clear();
		status.add(CouponStatus.EXPIRED);
		int expiredCouponCount = couponService.countByCouponStatus(parent, status);
		model.addAttribute("expiredCouponCount", expiredCouponCount);
		
		status.clear();
		status.add(CouponStatus.REFUND_APPLY);
		status.add(CouponStatus.REFUND_ACCEPTED);
		status.add(CouponStatus.REFUND_SUCCESS);
		status.add(CouponStatus.REFUND_FAIL);
		int refundCouponCount = couponService.countByCouponStatus(parent, status);
		model.addAttribute("refundCouponCount", refundCouponCount);
		
		model.addAttribute("status", StringUtils.join(searcher.getStatus(), ','));
		
		//加载商户信息
		Set<Long> shops = item.getScope();
		List<String> shopList = new ArrayList<>();
		for(Long s : shops) {
			shopList.add(shopService.findShopByIdIgnoreVisible(s).getName());
		}
		model.addAttribute("shopList", shopList);
		
		//优惠码列表
		searcher.setCouponInfoId(itemId);
		searcher.setParentId(parentId);
		searcher.setType(CouponInfoType.CHILD);
		pager = couponService.couponUsageStatistic((CouponExample)searcher.getExample(), pager);
		model.addAttribute("pager", pager);
		model.addAttribute("searcher", searcher);
		return "coupon/coupon_info_package_statistic";
	}
	
	
	@RequestMapping(value="/{eid}/modify_status", method=RequestMethod.PUT)
	public String modifyStatusByCouponInfoId(@PathVariable("eid") String eid, @RequestParam("status")String status){
		//优惠券详情
		Long id = IDUtil.decode(eid);
		CouponInfo couponInfo = couponService.findCouponInfoById(id);
		
		if(CouponInfoType.PACKAGE.equals(couponInfo.getType())){
			if("PUBLISHED_VISIBLE".equalsIgnoreCase(status)){
				couponInfo.setPublished(true);
				couponInfo.setVisible(true);
			}else if("PUBLISHED_UNVISIBLE".equalsIgnoreCase(status)){
				couponInfo.setPublished(true);
				couponInfo.setVisible(false);
			}else if("UNPUBLISHED".equalsIgnoreCase(status)){
				couponInfo.setPublished(false);
			}
			
			try{
				couponService.updateCouponInfo(couponInfo);
			}catch(Exception e){
				logger.error("更新优惠券状态失败", e);
				throw new GenericException("更新优惠券状态失败");
			}
			
			return "redirect:/coupon/detail/package/"+eid;
		}else{
			//普通优惠券需校验是否可修改状态：没有被套票关联或者关联的套票已结束
			/*List<Long> packageIds = couponService.findPackageIdsByItemId(id);
			boolean result = true;
			if(!CollectionUtils.isEmpty(packageIds)){
				//遍历所有被关联的套票，只要任一被关联套票处于未删除，且未结束状态，则不允许修改状态
				for(Long packageId : packageIds){
					CouponInfo packageCouponInfo = couponService.findCouponInfoById(packageId);
					if(!packageCouponInfo.getDeleted() && packageCouponInfo.getEndDate().after(new Date()) && packageCouponInfo.getPublished()){
						result = false;
					}
				}
			}
			if(!result){
				throw new GenericException("更新状态失败，请确认是否已被其他优惠套票关联");
			}*/
			
			/*以上2014-8-28 snoopy去除*/
			
			if("PUBLISHED_VISIBLE".equalsIgnoreCase(status)){
				couponInfo.setPublished(true);
				couponInfo.setVisible(true);
			}else if("PUBLISHED_UNVISIBLE".equalsIgnoreCase(status)){
				couponInfo.setPublished(true);
				couponInfo.setVisible(false);
			}else if("UNPUBLISHED".equalsIgnoreCase(status)){
				couponInfo.setPublished(false);
			}
			
			try{
				couponService.updateCouponInfo(couponInfo);
			}catch(Exception e){
				logger.error("更新优惠券状态失败", e);
				throw new GenericException("更新优惠券状态失败");
			}
			
			return "redirect:/coupon/detail/"+eid;
		}
	}
	
	@RequestMapping(value="/{eid}/unbind", method=RequestMethod.GET)
	public String unbindActivityByCouponInfoId(@PathVariable("eid") String eid, @RequestParam("activity") String activityEid){
		Long id = IDUtil.decode(eid);
		Long activityId = IDUtil.decode(activityEid);
		
		CouponInfo couponInfo = couponService.findCouponInfoById(id);
		if(couponInfo == null){
			throw new RuntimeException();
		}
		
		activityService.unbindCouponInfo(activityId, id);
		return "redirect:/coupon/detail/"+eid;
	}
	
	@RequestMapping(value="/{eid}/bind", method=RequestMethod.POST)
	public String bindActivityByCouponInfoId(@PathVariable("eid") String eid, @RequestParam(value="activity") String activityEid){
		Long id = IDUtil.decode(eid);
		Long activityId = IDUtil.decode(activityEid);
		
		CouponInfo couponInfo = couponService.findCouponInfoById(id);
		if(couponInfo == null){
			throw new RuntimeException();
		}
		
		//加载当前关联活动
		Activity activity = activityService.findRelatedActivityByCouponInfoId(id);
		if(activity != null){
			//先解绑已关联活动
			if(!activityService.unbindCouponInfo(activity.getId(), id)){
				throw new RuntimeException("解绑当前已关联活动失败");
			}
		}
		
		//关联到新的活动
		activityService.bindCouponInfo(activityId, id);
		if(CouponInfoType.NORMAL.equals(couponInfo.getType())){
			return "redirect:/coupon/detail/"+eid;
		}else{
			return "redirect:/coupon/detail/package/"+eid;
		}
	}
	
	@RequestMapping(value="/delete/{eid}", method=RequestMethod.DELETE)
	public String deleteCouponInfoById(@PathVariable("eid") String eid, RedirectAttributesModelMap model){
		Long id = IDUtil.decode(eid);
		CouponInfo couponInfo = couponService.findCouponInfoById(id);
		if(couponInfo != null && CouponInfoType.NORMAL.equals(couponInfo.getType())){
			//查询所有已被关联为子优惠券的套票ID(couponInfoId)
			List<Long> packageIds = couponService.findPackageIdsByItemId(id);
			boolean result = true;
			if(!CollectionUtils.isEmpty(packageIds)){
				//遍历所有被关联的套票，只要任一被关联套票处于未删除，且未结束状态，则不允许删除
				for(Long packageId : packageIds){
					CouponInfo packageCouponInfo = couponService.findCouponInfoById(packageId);
					if(!packageCouponInfo.getDeleted() && packageCouponInfo.getEndDate().after(new Date())){
						result = false;
					}
				}
			}
			if(result){
				couponService.deleteCouponInfoById(id);
				model.addFlashAttribute("status", SUCCESS);
				model.addFlashAttribute("msg", "删除成功");
			}else{
				model.addFlashAttribute("status", FAILD);
				model.addFlashAttribute("msg", "删除失败，请确认是否已被其他优惠套票关联");
				return "redirect:/coupon/detail/" + eid;
			}
		}
		return "redirect:/coupon/couponInfo/list";
	}
	
	@RequestMapping(value="/delete/package/{eid}", method=RequestMethod.DELETE)
	public String deleteCouponInfoPackageById(@PathVariable("eid") String eid ,RedirectAttributes  redirectAttributes){
		Long id = IDUtil.decode(eid);
		CouponInfo couponInfo = couponService.findCouponInfoById(id);
		if(couponInfo != null && CouponInfoType.PACKAGE.equals(couponInfo.getType()) && couponInfo.getReceived() == 0){
			//只有领用数量为零才允许删除套票优惠活动
			couponService.deleteCouponInfoById(id);
			redirectAttributes.addFlashAttribute("status", SUCCESS);
			redirectAttributes.addFlashAttribute("msg", "删除成功!");
		}else{
			redirectAttributes.addFlashAttribute("status", FAILD);
			redirectAttributes.addFlashAttribute("msg", "删除失败!错误原因：已经有领用。");
		}
		return "redirect:/coupon/couponInfoPackage/list";
	}
	
	@RequestMapping(value="/modify/package/{eid}", method=RequestMethod.POST)
	public String modifyCouponInfoPackage(@PathVariable("eid")String eid, long[] itemId, int[] itemQuantity, RedirectAttributesModelMap model){
		//校验参数
		Long id = IDUtil.decode(eid);
		CouponInfo couponInfo = couponService.findCouponInfoById(id);
		if(couponInfo == null || couponInfo.getReceived() > 0){
			//父优惠券不存在、或者父优惠券已被领用，则不允许修改
			model.addFlashAttribute("status", STATUS_FAILD);
			model.addFlashAttribute("msg", "父优惠券不存在、或者父优惠券已被领用，不允许修改");
			return "redirect:/coupon/detail/package/"+eid;
		}else if(ArrayUtils.isEmpty(itemId) || ArrayUtils.isEmpty(itemQuantity) || itemId.length != itemQuantity.length){
			//未选中需关联的子优惠券、或优惠券数量为零
			model.addFlashAttribute("status", STATUS_FAILD);
			model.addFlashAttribute("msg", "未勾选需关联的子优惠券、或优惠券数量为零");
			return "redirect:/coupon/detail/package/"+eid;
		}
		
		List<CouponInfo> items = new ArrayList<>();
		couponInfo.setItems(items);
		int total = 0;
		BigDecimal totalPrice = new BigDecimal(0);
		for(int i = 0; i < itemId.length; i++){
			Long cid = itemId[i];
			CouponInfo couponInfoItem = couponService.findCouponInfoById(cid);
			if(couponInfoItem == null || !Boolean.TRUE.equals(couponInfoItem.getPublished())){
				//关联的子优惠券不存在、子优惠券未发布
				return "redirect:/coupon/detail/package/"+eid;
			}
			couponInfoItem.setQuantity(itemQuantity[i]);
			items.add(couponInfoItem);
			total += itemQuantity[i];
			totalPrice = totalPrice.add(couponInfoItem.getOriginalPrice() == null ? new BigDecimal(0) : couponInfoItem.getOriginalPrice().multiply(new BigDecimal(itemQuantity[i])));
		}
		
		if(total < 2){
			//必须至少2份优惠券
			//throw new GenericException("修改失败。份数总和不能少于2份");
			model.addFlashAttribute("status", STATUS_FAILD);
			model.addFlashAttribute("msg", "修改失败。份数总和不能少于2份");
			return "redirect:/coupon/detail/package/"+eid;
		}
		
		//2.修改套票信息
		couponInfo.setOriginalPrice(totalPrice);
		couponService.updateCouponInfo(couponInfo);//统计所有子票的价格并更新到couponInfo
		couponService.deleteCouponInfoPackageRelationById(id); //删除已有子优惠券关联
		couponService.saveCouonInfoPackageRelation(couponInfo);
		return "redirect:/coupon/detail/package/"+eid;
	}
}
