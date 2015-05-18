package com.xpos.common.service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.xkeshi.service.XCouponService;
import com.xpos.common.entity.Configuration;
import com.xpos.common.entity.Coupon;
import com.xpos.common.entity.Coupon.CouponStatus;
import com.xpos.common.entity.CouponInfo;
import com.xpos.common.entity.CouponInfo.CouponInfoStatus;
import com.xpos.common.entity.CouponInfo.CouponInfoType;
import com.xpos.common.entity.Picture;
import com.xpos.common.entity.Picture.PictureType;
import com.xpos.common.entity.Shop;
import com.xpos.common.entity.ThirdpartyCoupon;
import com.xpos.common.entity.ThirdpartyCoupon.ThirdCouponStatus;
import com.xpos.common.entity.ThirdpartyCouponInfo;
import com.xpos.common.entity.example.CouponExample;
import com.xpos.common.entity.example.CouponInfoExample;
import com.xpos.common.entity.example.Example.Criteria;
import com.xpos.common.entity.example.PictureExample;
import com.xpos.common.entity.example.ThirdpartyCouponExample;
import com.xpos.common.entity.example.ThirdpartyCouponInfoExample;
import com.xpos.common.entity.face.Business;
import com.xpos.common.entity.face.Business.BusinessModel;
import com.xpos.common.entity.face.Business.BusinessType;
import com.xpos.common.entity.security.User;
import com.xpos.common.persistence.mybatis.ActivityMapper;
import com.xpos.common.persistence.mybatis.CouponInfoMapper;
import com.xpos.common.persistence.mybatis.CouponMapper;
import com.xpos.common.persistence.mybatis.PictureMapper;
import com.xpos.common.persistence.mybatis.ThirdpartyCouponInfoMapper;
import com.xpos.common.persistence.mybatis.ThirdpartyCouponMapper;
import com.xpos.common.searcher.CouponInfoSearcher;
import com.xpos.common.utils.BusinessSQLBuilder;
import com.xpos.common.utils.Pager;

@Service
public class CouponServiceImpl implements CouponService{
	private final static Logger logger = LoggerFactory.getLogger(CouponServiceImpl.class);
	
	@Resource
	private CouponInfoMapper couponInfoMapper;
	
	@Resource
	private CouponMapper couponMapper;

	@Resource
	private PictureMapper pictureMapper;
	
	@Resource
	private ActivityMapper activityMapper;
	
	@Resource
	private PictureService pictureService;
	
	@Resource
	private ActivityService activityService;

	@Resource
	private ThirdpartyCouponInfoMapper thirdpartycouponInfoMapper;
	
	@Resource
	private ThirdpartyCouponMapper thirdpartycouponMapper;
	
	@Resource
	private ConfigurationService confService  ;
	
	@Resource
	private ShopService  shopService  ;
	
	@Resource
	private XCouponService  xCouponService  ;
	
	@Override
	public Pager<CouponInfo> findCouponInfos(Business business,CouponInfoExample example, Pager<CouponInfo> pager) {
		if(example == null){
			example = new CouponInfoExample();
			example.createCriteria();
		}
		example.appendCriterion("deleted=", false);
		if(business != null)
			example.appendCriterion(BusinessSQLBuilder.getBusinessByShopInCouponInfoSQL(business.getSelfBusinessType(), business.getSelfBusinessId()));
		
		List<CouponInfo> list = couponInfoMapper.selectByExample(example, pager);
		int totalCount = couponInfoMapper.countByExample(example);
		pager.setTotalCount(totalCount);
		pager.setList(list);
		
		return pager;
	}
	

	@Override
	public Pager<CouponInfo> findCouponInfoByIds(CouponInfoExample example, Pager<CouponInfo> pager) {
		if(example == null){
			example = new CouponInfoExample();
		}
		example.appendCriterion("deleted=", false)
		       .addCriterion("published= ", true);
		List<CouponInfo> list = couponInfoMapper.selectByExample(example, pager);
		int totalCount = couponInfoMapper.countByExample(example);
		pager.setTotalCount(totalCount);
		pager.setList(list);
		return pager;
	}

	@Override
	public Pager<CouponInfo> findPublishedCouponInfosByShopId(Business business,CouponInfoExample example, Pager<CouponInfo> pager) {
		if(example == null){
			example = new CouponInfoExample();
		}
		example.appendCriterion("published= ", true)
		.addCriterion("deleted= ", false)
		.addCriterion(BusinessSQLBuilder.getBusinessByShopInCouponInfoSQL(BusinessType.SHOP, business.getAccessBusinessId(BusinessModel.COUPON)));
		
		List<CouponInfo> list = couponInfoMapper.selectByExample(example, pager);
		int totalCount = couponInfoMapper.countByExample(example);
		pager.setTotalCount(totalCount);
		pager.setList(list);
		return pager;
	}
	

	@Override
	public Pager<CouponInfo> findPublishedAndVisibleCouponInfosByShopId(
			Business business,CouponInfoExample example, Pager<CouponInfo> pager) {
		if(example == null){
			example = new CouponInfoExample();
		}
		example.appendCriterion("published= ", true)
		.addCriterion("deleted= ", false)
		.addCriterion("visible=", true)
		.addCriterion(BusinessSQLBuilder.getBusinessByShopInCouponInfoSQL(BusinessType.SHOP, business.getAccessBusinessId(BusinessModel.COUPON)));
		
		List<CouponInfo> list = couponInfoMapper.selectByExample(example, pager);
		int totalCount = couponInfoMapper.countByExample(example);
		pager.setTotalCount(totalCount);
		pager.setList(list);
		return pager;
	}

	@Override
	public List<CouponInfo> findMarketableCouponInfosByShopId(Long id, Pager<CouponInfo> pager) {
		DateFormat formater = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
		String now = formater.format(new Date());
		CouponInfoExample example = new CouponInfoExample();
		example.createCriteria().addCriterion("(limitCount = -1 or limitCount - received > 0)")
								.addCriterion("businessId=", id)
								.addCriterion("saleStartDate <= ", now)
								.addCriterion("saleEndDate>= ",now);
		
		return couponInfoMapper.selectByExample(example, pager);
	}
	
	@Override
	public List<CouponInfo> findBindableCouponInfosByShopId(Long shopId) {
		return couponInfoMapper.selectBindableByShopId(shopId);
	}



	@Override
	public CouponInfo findCouponInfoById(Long id) {
		CouponInfoExample example = new CouponInfoExample();
		example.createCriteria().addCriterion("id = ", id).addCriterion("deleted = ", false);
		List<CouponInfo> list = couponInfoMapper.selectByExample(example, null);
		if(list.size() == 1)
			return list.get(0);
		
		return null;
	}

	@Override
	@Transactional
	public boolean saveCouponInfo(CouponInfo couponInfo, Long activityId) {
		boolean needUpdate = false;
		
		couponInfo.setPublished(false);
		couponInfo.setTop(false);
		couponInfo.setReceived(0);
		couponInfo.setStars(3d);  //默认星级
		if(couponInfoMapper.insert(couponInfo) <= 0){
			throw new RuntimeException();
		}
		
		//保存优惠券适用商户
		if(couponInfo.getBusinessType().equals(BusinessType.SHOP)){
			if(couponInfoMapper.insertCouponInfoAllowedShop(couponInfo.getId(), couponInfo.getBusinessId(), couponInfo.getBusinessType().toString()) <= 0){
				throw new RuntimeException();
			}
		}else if(couponInfo.getBusinessType().equals(BusinessType.MERCHANT) && !CollectionUtils.isEmpty(couponInfo.getScope())){
			Set<Long> shopIds = couponInfo.getScope();
			for(Long shopId : shopIds){
				if(couponInfoMapper.insertCouponInfoAllowedShop(couponInfo.getId(), shopId, BusinessType.SHOP.toString()) <= 0){
					throw new RuntimeException();
				}
			}
		}
		
		//保存详情大图、缩略图
		if(couponInfo.getPic() != null){
			Picture pic = couponInfo.getPic();
			pic.setForeignId(couponInfo.getId());
			pic.setPictureType(PictureType.COUPON_INFO_PIC);
			if(!pictureService.uploadPicture(pic)){
				throw new RuntimeException();
			}else{
				needUpdate = true;
			}
		}
		
		if(couponInfo.getThumb() != null){
			Picture thumb = couponInfo.getThumb();
			thumb.setForeignId(couponInfo.getId());
			thumb.setPictureType(PictureType.COUPON_INFO_THUMB);
			if(!pictureService.uploadPicture(thumb)){
				throw new RuntimeException();
			}else{
				needUpdate = true;
			}
		}
		
		//绑定活动（activity）
		if(activityId != null && activityId > 0){
			activityService.bindCouponInfo(activityId, couponInfo.getId());
		}
		
		//更新pic_id, thumb_id
		if(needUpdate){
			if(couponInfoMapper.updateByPrimaryKey(couponInfo) <= 0){
				throw new RuntimeException();
			}
		}
		return true;
	}

	@Override
	public boolean updateCouponInfo(CouponInfo form) {
		CouponInfo couponInfo = couponInfoMapper.selectByPrimaryKey(form.getId());
		if(couponInfo == null || couponInfo.getDeleted()==true){
			return false;
		}
		
		migrateProperties(form, couponInfo);
		
		boolean result = false;
		
		//保存头像、banner
		if(form.getPic() != null && form.getPic().getData() != null){
			Picture pic = form.getPic();
			pic.setForeignId(couponInfo.getId());
			pic.setPictureType(PictureType.COUPON_INFO_PIC);
			result = pictureService.uploadPicture(pic);
			
			//更新数据库Picture表
			PictureExample example = new PictureExample();
			example.createCriteria()
					.addCriterion("foreignId = ", couponInfo.getId())
					.addCriterion("pictureType = ", PictureType.COUPON_INFO_PIC.toString());
			Picture persistence = pictureMapper.selectOneByExample(example);
			if(persistence != null){
				persistence.setOriginalName(pic.getOriginalName());
				persistence.setName(pic.getName());
				couponInfo.setPic(persistence);
				result = result & pictureMapper.updateByPrimaryKey(persistence) == 1;
			}else{
				couponInfo.setPic(pic);
				result = result & pictureMapper.insert(pic) > 0;
			}
		}
		
		if(form.getThumb() != null && form.getThumb().getData() != null){
			Picture thumb = form.getThumb();
			thumb.setForeignId(couponInfo.getId());
			thumb.setPictureType(PictureType.COUPON_INFO_THUMB);
			result = pictureService.uploadPicture(thumb);
			
			//更新数据库Picture表
			PictureExample example = new PictureExample();
			example.createCriteria()
					.addCriterion("foreignId = ", couponInfo.getId())
					.addCriterion("pictureType = ", PictureType.COUPON_INFO_THUMB.toString());
			Picture persistence = pictureMapper.selectOneByExample(example);
			if(persistence != null){
				persistence.setOriginalName(thumb.getOriginalName());
				persistence.setName(thumb.getName());
				couponInfo.setThumb(persistence);
				result = result & pictureMapper.updateByPrimaryKey(persistence) == 1;
			}else{
				couponInfo.setThumb(thumb);
				result = result & pictureMapper.insert(thumb) > 0;
			}
		}
		
		result = couponInfoMapper.updateByPrimaryKeyWithNullValue(couponInfo) > 0;
		
		//保存优惠券适用商户
		if(couponInfo.getBusinessType().equals(BusinessType.MERCHANT)){
			Set<Long> currentShopScope = couponInfo.getScope();
			Set<Long> shopScope = form.getScope();
			if(CollectionUtils.isEmpty(shopScope) && CollectionUtils.isEmpty(currentShopScope)) {
				//如果适用商户范围为空，不处理，直接返回
				return result;
			}else if(CollectionUtils.isEmpty(currentShopScope) && !CollectionUtils.isEmpty(shopScope)){
				//直接插入
				for(Long shopId : shopScope){
					if(couponInfoMapper.insertCouponInfoAllowedShop(couponInfo.getId(), shopId, BusinessType.SHOP.toString()) <= 0){
						throw new RuntimeException();
					}
				}
			}else if(!CollectionUtils.isEmpty(currentShopScope) && CollectionUtils.isEmpty(shopScope)){
				//作废已有适用商户信息
				result &= deleteCouponInfoScopeRelationById(couponInfo.getId()); //删除已有子优惠券关联
			}else if(!CollectionUtils.isEmpty(currentShopScope) && !CollectionUtils.isEmpty(shopScope)){
				if(currentShopScope.containsAll(shopScope) && shopScope.containsAll(currentShopScope)){
					//shop Scope 未作改变
					result &= true;
				}else{
					//1.删除已有适用商户信息
					result &= deleteCouponInfoScopeRelationById(couponInfo.getId()); //删除已有子优惠券关联
					
					//2.重新插入
					for(Long shopId : shopScope){
						if(couponInfoMapper.insertCouponInfoAllowedShop(couponInfo.getId(), shopId, BusinessType.SHOP.toString()) <= 0){
							throw new RuntimeException();
						}
					}
				}
			}
		}

		return result;
	}

	private void migrateProperties(CouponInfo form, CouponInfo couponInfo) {
		couponInfo.setName(form.getName());
		couponInfo.setIntro(form.getIntro());
		couponInfo.setStartDate(form.getStartDate());
		couponInfo.setEndDate(form.getEndDate());
		couponInfo.setDescription(form.getDescription());
		couponInfo.setRemark(form.getRemark());
		couponInfo.setLimitCount(form.getLimitCount());
		couponInfo.setUserLimitCount(form.getUserLimitCount());
		couponInfo.setPrice(form.getPrice());
		couponInfo.setOriginalPrice(form.getOriginalPrice());
		couponInfo.setSaleStartDate(form.getSaleStartDate());
		couponInfo.setSaleEndDate(form.getSaleEndDate());
		couponInfo.setTag(form.getTag());
		couponInfo.setLimitPayTime(form.getLimitPayTime());
		couponInfo.setAllowContinueSale(form.getAllowContinueSale());
		couponInfo.setInstructions(form.getInstructions());
		if(form.getReceived() != null){
			couponInfo.setReceived(form.getReceived());
		}
		if(form.getSupportNormalRefund() != null && form.getSupportNormalRefund()){
			couponInfo.setSupportNormalRefund(true);
		}else{
			couponInfo.setSupportNormalRefund(false);
		}
		
		if(form.getSupportExpiredRefund() != null && form.getSupportExpiredRefund()){
			couponInfo.setSupportExpiredRefund(true);
		}else{
			couponInfo.setSupportExpiredRefund(false);
		}
		
		if(form.getPublished() != null && form.getPublished()){
			couponInfo.setPublished(true);
		}else{
			couponInfo.setPublished(false);
		}
		
		if(form.getVisible() != null && form.getVisible()){
			couponInfo.setVisible(true);
		}else{
			couponInfo.setVisible(false);
		}
	}

	@Override
	public List<Long> findRelatedCouponInfos(Long activityId, Long couponInfoId) {
		return couponInfoMapper.selectRelatedCouponInfosById(activityId, couponInfoId);
	}

	@Override
	@Transactional
	public boolean deleteCouponInfoById(Long id) {
		CouponInfo couponInfo = couponInfoMapper.selectByPrimaryKey(id);
		if(couponInfo != null){
			couponInfo.setDeleted(true);
			boolean result = couponInfoMapper.updateByPrimaryKey(couponInfo) == 1;
			
			if(CouponInfoType.PACKAGE.equals(couponInfo.getType()) && !CollectionUtils.isEmpty(couponInfo.getItems())){
				//如果是套票，还需在关联表标记删除
				result &= deleteCouponInfoPackageRelationById(couponInfo.getId());
			}
			
			if(!result){
				throw new RuntimeException();
			}
			
			return result;
		}
		return false;
	}
	
	@Override
	public int countByCouponStatus(CouponInfo couponInfo, List<CouponStatus> status) {
		CouponExample example = new CouponExample();
		Criteria cri = example.createCriteria();
		cri.addCriterion("deleted=",false);
		if(CouponInfoType.NORMAL.equals(couponInfo.getType())){
			cri.addCriterion("couponInfo_id = ", couponInfo.getId())
				.addCriterion("type = 'NORMAL'");
		}else if(CouponInfoType.PACKAGE.equals(couponInfo.getType())){
			cri.addCriterion("parent_id = ", couponInfo.getId())
				.addCriterion("couponInfo_id = ", couponInfo.getItems().get(0).getId())
				.addCriterion("type = 'CHILD'");
		}
		if(!CollectionUtils.isEmpty(status)){
			StringBuilder str = new StringBuilder();
			for(CouponStatus sta : status){
				str.append("'").append(sta.name()).append("',");
			}
			str.deleteCharAt(str.length() - 1);
			cri.addCriterion("status in (" + str + ")");
		}
		return couponMapper.countByExample(example);
	}

	@Override
	public Pager<Coupon> couponUsageStatistic(CouponExample example, Pager<Coupon> pager) {
		if(example == null){
			example = new CouponExample();
			example.createCriteria();
		}
		example.appendCriterion("deleted = ", false);
		List<Coupon> list = couponMapper.selectByExample(example, pager);
		int totalCount = couponMapper.countByExample(example);
		pager.setTotalCount(totalCount);
		pager.setList(list);
		
		return pager;
	}

	@Override
	public Coupon findCouponById(Long id) {
		CouponExample example = new CouponExample();
		example.createCriteria().addCriterion("id = ", id).addCriterion("deleted = ", false);
		List<Coupon> list = couponMapper.selectByExample(example, null);
		if(list.size() == 1)
			return list.get(0);
		
		return null;
	}

	@Override
	public Coupon findCouponByCode(String couponCode) {
		CouponExample example = new CouponExample();
		example.createCriteria().addCriterion("couponCode = ", couponCode).addCriterion("deleted = ", false);
		return couponMapper.selectOneByExample(example);
	}
	
	@Override
	public Pager<Coupon> findCoupons(CouponExample example, Pager<Coupon> pager) {
		if(example == null){
			example = new CouponExample();
			example.createCriteria();
		}
		example.appendCriterion("deleted=", false);
		List<Coupon> list = couponMapper.selectByExample(example, pager);
		int totalCount = couponMapper.countByExample(example);
		pager.setTotalCount(totalCount);
		pager.setList(list);
		
		return pager;
	}

	/** 查询用户的优惠券  */
	public Pager<Coupon>  findUserByBusinessCoupons(User user  , Business  business ,
			                       CouponExample  example  , Pager<Coupon>  pager){
		Long userId  = user.getId();
		String mobile = user.getMobile();
		if (userId == null && StringUtils.isBlank(mobile)) 
		   return  pager;
		if (example == null) 
			example  = new CouponExample() ;
		 
		 if (StringUtils.isNotBlank(mobile) ) {
			 if (userId != null) {
				 example.appendCriterion("(user_id = "+userId+"  or mobile = '"+mobile+"')");
			 }else{
				 example.appendCriterion("mobile=", mobile);
			 }
		 }
		 example.appendCriterion("deleted=", false);
		if (business != null ) {
			 if (business.getSelfBusinessId() == null || business.getSelfBusinessType() == null) 
				return pager;
			 example.appendCriterion("couponinfo_id in (select cs.couponInfo_id from CouponInfo_Scope  cs  where cs.businessId  = "
		             +business.getSelfBusinessId()+" and businessType= '"+business.getSelfBusinessType().toString()+"' and deleted= false)");
		}
        pager.setList(couponMapper.selectByExample(example, pager));
        pager.setTotalCount(couponMapper.countByExample(example));
		return pager;
	}

	
	
	@Override
	public boolean consumeCoupon(Coupon coupon) {
		coupon.setStatus(CouponStatus.USED);
		CouponExample example = new CouponExample();
		example.createCriteria().addCriterion("deleted = ", false)
								.addCriterion("couponCode = ", coupon.getCouponCode());
		return couponMapper.updateByExample(coupon, example) == 1;
	}
	
	@Override
	public Pager<CouponInfo> getCouponList(
			Pager<CouponInfo> pager, CouponInfoExample example) {
		List <CouponInfo> resultList = new ArrayList<>();
		int totalCount = 0;
		try{
			example.appendCriterion("deleted=",false);
			totalCount = couponInfoMapper.countByExample(example);
			pager.setTotalCount(totalCount);
			if(totalCount>0){
				resultList = couponInfoMapper.selectByExample(example,pager);
			}
			pager.setList(resultList);
		} catch (DataAccessException e){
			logger.error("获取优惠券列表失败", e);
			pager.setTotalCount(totalCount);
			pager.setList(resultList);
		}
		return pager;
	}
	
	@Override
	public Pager<Coupon> getCouponListByPhone(Pager<Coupon> pager, long mobile, CouponExample example) {
		try{
			List<Coupon> couponList = null;
			if (example.getOredCriteria().get(0).getCriteria().size()==0) {
				example.appendCriterion("status ='AVAILABLE'");
			}
			String statusCond = example.getOredCriteria().get(0).getCriteria().get(0).getCondition().toString();
			example.appendCriterion("mobile='"+mobile+"'").addCriterion("deleted=",false);
			if (statusCond.indexOf("EXPIRED")!=-1) {
				pager.setTotalCount(couponMapper.selectByExpiredCount(mobile,pager));
				couponList = couponMapper.selectByExpired(mobile,pager);
			}else if(statusCond.indexOf("AVAILABLE")!=-1) {
				pager.setTotalCount(couponMapper.selectByAvailableCount(mobile,pager));
				couponList = couponMapper.selectByAvailable(mobile,pager);
			}else {
				pager.setTotalCount(couponMapper.countByExample(example));
				couponList = couponMapper.selectByExample(example, pager);
			}
			for(Coupon coupon : couponList) {
				coupon.setUsedDate(coupon.getConsumeDate());
			}
			pager.setList(couponList);
			return pager;
		} catch (DataAccessException e){
			logger.error("获取优惠券列表失败", e);
			throw e;
		}
	}
	@Override
	@Transactional
	public String buy(Long cid, String mobile) {
		//1.根据ID查优惠券
		CouponInfo couponInfo = couponInfoMapper.selectByPrimaryKey(cid);
		String result = null;
		//2.校验
		long current = System.currentTimeMillis();
		if(couponInfo == null || couponInfo.getDeleted()==true){
			result = "对不起，未找到优惠券";
			return result;
		}else if(couponInfo.getLimitCount()!=null && couponInfo.getReceived()!=null){
			if(couponInfo.getLimitCount()<=couponInfo.getReceived()) {
				result = "对不起，优惠券已售罄";
				return result;
			}
		}else if(!couponInfo.getStatus().equals(CouponInfoStatus.NORMAL)){
			result ="对不起，优惠活动已结束";
			return result;
		}else if(current < couponInfo.getSaleStartDate().getTime()){
			result ="对不起，抢购未开始";
			return result;
		}else if(current > couponInfo.getSaleEndDate().getTime()){
			result ="对不起，抢购已结束";
			return result;
		}
		CouponExample example = new CouponExample();
		example.createCriteria().addCriterion("mobile='"+mobile+"'")
								.addCriterion("couponInfo_id=",cid);
		
		List<Coupon> couponList = couponMapper.selectByExample(example, null);
		if(!CollectionUtils.isEmpty(couponList)){ //已领取
			//Coupon coupon = couponList.get(0);
			/*result.setMessage(coupon.getCouponCode());
			result.setSuccess(true);*/
			return null;
		}
		
		//3.减库存
		//已领用+1
		if(couponInfo.getLimitCount() != null){
			int count = 0;
			CouponInfo cInfo = new CouponInfo();
			cInfo = couponInfoMapper.selectByPrimaryKey(cid);
			cInfo.setReceived(cInfo.getReceived()==null?0:cInfo.getReceived()+1);
			count = couponInfoMapper.updateByPrimaryKey(cInfo);
			if(count != 1){
				throw new RuntimeException("添加领用记录失败，请重试");
			}
		}
		
		//4.记录领用记录
		com.xkeshi.pojo.po.Coupon coupon = new com.xkeshi.pojo.po.Coupon();
		coupon.setCouponInfoId(couponInfo.getId());
		coupon.setMobile(mobile);
		xCouponService.insert(coupon );
		return result;
	}

	@Override
	public Coupon findCouponByMobile(Long couponInfoId, String mobile) {
		CouponExample example = new CouponExample();
		example.createCriteria()
				.addCriterion("couponInfo_id = ", couponInfoId)
				.addCriterion("mobile = ", mobile);
		return couponMapper.selectOneByExample(example);
	}
	
	@Override
	public ThirdpartyCoupon findThirdCouponByMobile(Long thirdcouponInfoId, String mobile) {
		ThirdpartyCouponExample example = new ThirdpartyCouponExample();
		example.createCriteria()
		.addCriterion("thirdpartycouponInfo_id = ", thirdcouponInfoId)
		.addCriterion("mobile = ", mobile)
		.addCriterion("deleted=", false);
		return thirdpartycouponMapper.selectOneByExample(example);
	}

	@Override
	public boolean saveCoupon(Coupon coupon, boolean isMarkReceived) {
		com.xkeshi.pojo.po.Coupon xCoupon = new com.xkeshi.pojo.po.Coupon();
		CouponInfo couponInfo = null;
		if(coupon.getType().equals(CouponInfoType.NORMAL)){
			couponInfo = coupon.getCouponInfo();
		}else if(coupon.getType().equals(CouponInfoType.CHILD)){
			couponInfo = coupon.getParent();
		}
		xCoupon.setMobile(coupon.getMobile());
		xCoupon.setStatus(CouponStatus.AVAILABLE.toString());
		xCoupon.setCouponInfoId(couponInfo.getId());
		xCoupon.setBusinessId(coupon.getBusinessId());
		xCoupon.setBusinessType(coupon.getBusinessType().toString());
		xCoupon.setMemberId(coupon.getMember() != null ? coupon.getMember().getId() : null);
		xCoupon.setOperatorId(coupon.getOperator() != null ? coupon.getOperator().getId() : null);
		xCoupon.setPackageSerial(coupon.getPackageSerial());
		xCoupon.setParentId(coupon.getParent() != null ? coupon.getParent().getId() : null);
		xCoupon.setPaymentId(coupon.getPayment() != null ? coupon.getPayment().getId() : null);
		xCoupon.setRefundId(coupon.getRefund() != null  ? coupon.getRefund().getId() : null );
		xCoupon.setType(coupon.getType() != null ? coupon.getType().toString() : null);
		boolean result = xCouponService.insert(xCoupon );
		if(result && isMarkReceived){
			coupon.setCouponCode(xCoupon.getCouponCode());
			coupon.setUniqueCode(xCoupon.getUniqueCode());
			result =  (couponInfoMapper.updateCouponInfoReceived(1, couponInfo.getId(), "")==1);
		}
		return result;
	}
	
	@Override
	public boolean saveThirdpatyCoupon(ThirdpartyCoupon tcoupon) {
		String mobile = tcoupon.getMobile();
		ThirdpartyCoupon coupon = new ThirdpartyCoupon();
		ThirdpartyCouponExample example = new ThirdpartyCouponExample();
		example.createCriteria().addCriterion("thirdpartycouponInfo_id='"+tcoupon.getCouponInfo().getId()+"'")
								.addCriterion("status= '"+ThirdCouponStatus.PENDING+"'")
								.addCriterion("deleted=",false);
		coupon = thirdpartycouponMapper.selectOneByExample(example);
		if (coupon!=null) {
			ThirdpartyCoupon ccoupon = new ThirdpartyCoupon();
			ThirdpartyCouponExample cexample = new ThirdpartyCouponExample();
			cexample.createCriteria().addCriterion("id="+coupon.getId()+"");
			ccoupon.setMobile(mobile);
			ccoupon.setStatus(ThirdCouponStatus.AVAILABLE);
			ccoupon.setCouponInfo(coupon.getCouponInfo());
			boolean result = thirdpartycouponMapper.updateByExample(ccoupon, cexample) == 1;
			ThirdpartyCouponInfo couponInfo = tcoupon.getCouponInfo();
			couponInfo.setReceived(couponInfo.getReceived() + 1);
			result = result & (thirdpartycouponInfoMapper.updateByPrimaryKey(couponInfo) == 1);
			if(!result){
				throw new RuntimeException();
			}
			tcoupon.setId(coupon.getId());
			return true;
		}
		return false;
	}

	@Override
	public Integer countShopCoupons(Shop shop) {
		CouponInfoExample example = new CouponInfoExample();
		example.createCriteria().addCriterion("published=",true)
								 .addCriterion("deleted=", false)
						         .addCriterion(BusinessSQLBuilder.getBusinessByShopInCouponInfoSQL(BusinessType.SHOP, shop.getId()));
		return couponInfoMapper.countByExample(example);
	}
	
	@Override
	public Integer countShopCouponsVisible(Shop shop) {
		CouponInfoExample example = new CouponInfoExample();
		example.createCriteria().addCriterion("published=",true)
								.addCriterion("visible=", true)
								 .addCriterion("deleted=", false)
						         .addCriterion(BusinessSQLBuilder.getBusinessByShopInCouponInfoSQL(BusinessType.SHOP, shop.getId()));
		return couponInfoMapper.countByExample(example);
	}

	@Override
	public Integer countShopActivities(Shop shop) {
		 
		return activityMapper.countActivity(shop.getId());
	}
	
	@Override
	public ThirdpartyCouponInfo findThirdpartyCouponInfoById(Long id) {
		ThirdpartyCouponInfoExample example = new ThirdpartyCouponInfoExample();
		example.createCriteria().addCriterion("id = ", id).addCriterion("deleted = ", false);
		List<ThirdpartyCouponInfo> list = thirdpartycouponInfoMapper.selectByExample(example, null);
		if(list.size() == 1)
			return list.get(0);
		
		return null;
	}
	
	@Override
	public ThirdpartyCoupon findThirdCouponById(Long id) {
		ThirdpartyCouponExample example = new ThirdpartyCouponExample();
		example.createCriteria().addCriterion("id = ", id).addCriterion("deleted = ", false);
		List<ThirdpartyCoupon> list = thirdpartycouponMapper.selectByExample(example, null);
		if(list.size() == 1)
			return list.get(0);
		
		return null;
	}
	
	@Override
	public Pager<ThirdpartyCoupon> getThirdCouponListByPhone(Pager<ThirdpartyCoupon> pager,Long mobile) {
		try{
			ThirdpartyCouponExample example = new ThirdpartyCouponExample();
			example.createCriteria().addCriterion("mobile='"+mobile+"'")
									.addCriterion("deleted=", false);
			List<ThirdpartyCoupon> couponList = null;
			pager.setTotalCount(thirdpartycouponMapper.countByExample(example));
			couponList = thirdpartycouponMapper.selectByExample(example, pager);
			pager.setList(couponList);
			return pager;
		} catch (DataAccessException e){
			logger.error("获取优惠券列表失败", e);
			throw e;
		}
	}
	@Override
	public int countCoupons(CouponExample example) {
		if(example == null){
			example = new CouponExample();
			example.createCriteria();
		}
		example.appendCriterion("deleted=", false);
		return couponMapper.countByExample(example);
	}
	
	@Override
	public int countCouponInfos(CouponInfoExample example) {
		if(example == null){
			example = new CouponInfoExample();
			example.createCriteria();
		}
		example.appendCriterion("deleted=", false)
		.addCriterion("published=","1");
		return couponInfoMapper.countByExample(example);
	}
	
	@Override
	public Pager<CouponInfo> findCouponInfos(CouponInfoExample example,
			Pager<CouponInfo> pager) {
		example.appendCriterion("deleted=", false)
				.addCriterion("published=","1");
		pager.setList(couponInfoMapper.selectByExample(example, pager));
		pager.setTotalCount(couponInfoMapper.countByExample(example));
		return pager;
	}
	 /**
	  * 集团下--热门销售查询(排除已显示的优惠)
	  */
	@Override
	public Pager<CouponInfo> merchantFindHotCouponInfo(Pager<CouponInfo> pager, CouponInfoSearcher couponInfoSearcher) {
		BusinessType businessType = couponInfoSearcher.getBusinessType();
		Long businessId   = couponInfoSearcher.getBusinessId();
		String key = couponInfoSearcher.getKey();
		String tag = couponInfoSearcher.getTag();
		if (businessType == null || businessId == null) 
			return pager;
		 CouponInfoExample example = new  CouponInfoExample();
		 Criteria criteria = example.createCriteria();
		 CouponInfoType type = couponInfoSearcher.getType();
		if(type != null)
			 criteria.addCriterion("type = ", type.toString());
		if (StringUtils.isNotBlank(key))
		    criteria.addCriterion(" name like '%"+key+"%'");
		if (StringUtils.isNotBlank(tag))
			criteria.addCriterion(" tag like '%"+tag+"%'");
		 criteria.addCriterion("published = ", true);
		 criteria.addCriterion("deleted = ", false);
		 if(couponInfoSearcher.getIds()!=null ) {
			 for(Long ids:couponInfoSearcher.getIds()) {
				 criteria.addCriterion(" id !="+ids+"");
			 }
		 }
		 criteria.addCriterion(BusinessSQLBuilder.getBusinessSQL(businessType, businessId));
        //排序
		String[] orderByClause = couponInfoSearcher.getOrderByClause();
		Integer order = couponInfoSearcher.getOrder();
		if(order != null && order<orderByClause.length)
			example.setOrderByClause(orderByClause[order]);
		else
			example.setOrderByClause(" received DESC");
		pager.setList(couponInfoMapper.selectByExample(example, pager));
		pager.setTotalCount(couponInfoMapper.countByExample(example));
		return pager;
	}
	/**
	 * 集团下--相关-销售查询
	 */
	@Override
	public Pager<CouponInfo> merchantFindRelateCouponInfo(Pager<CouponInfo> pager, CouponInfoSearcher couponInfoSearcher) {
		if(couponInfoSearcher.getIds()==null) {
			return pager;
		}
		CouponInfoExample example = new  CouponInfoExample();
		Criteria criteria = example.createCriteria();
		criteria.addCriterion("published = ", true);
		criteria.addCriterion("deleted = ", false);
		criteria.addCriterion(" id in (select couponinfo_id from CouponInfo_Scope where deleted =false and businessId in "
				+ "(select businessId from CouponInfo_Scope "
				+ "where deleted=false and couponinfo_id="+couponInfoSearcher.getIds().get(0)+") "
			    + "and couponinfo_id !="+couponInfoSearcher.getIds().get(0)+")");
		pager.setList(couponInfoMapper.selectByExample(example, pager));
		pager.setTotalCount(couponInfoMapper.countByExample(example));
		return pager;
	}

	@Override
	@Transactional
	public boolean saveCouonInfoPackageRelation(CouponInfo parentCouponInfo) {
		//如果是套票，保存关联关系
		if(parentCouponInfo.getType().equals(CouponInfoType.PACKAGE) && !CollectionUtils.isEmpty(parentCouponInfo.getItems())){
			for(CouponInfo item : parentCouponInfo.getItems()){
				if(couponInfoMapper.insertCouponInfoPackge(parentCouponInfo.getId(), item.getId(), item.getQuantity()) <= 0){
					throw new RuntimeException();
				}
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean deleteCouponInfoPackageRelationById(Long parentId) {
		return couponInfoMapper.deleteCouponInfoPackge(parentId) > 0;
	}

	@Override
	public List<Long> findPackageIdsByItemId(Long id) {
		return couponInfoMapper.findPackgeIdsByItemId(id);
	}

	@Override
	public int findCouponCountByShopId(Long id, BusinessType type) {
		return couponInfoMapper.findCouponCountByShopId(id,type);
	}

	@Override
	public boolean deleteCouponInfoScopeRelationById(Long couponInfoId) {
		return couponInfoMapper.deleteCouponInfoScope(couponInfoId) > 0;
	}

	@Override
	public void updateCouponStatus() {
		couponMapper.updateStatus();
	}

	@Override
	public List<Coupon> findCouponPackageBySerial(String serial) {
		CouponExample example = new CouponExample();
		example.createCriteria().addCriterion("packageSerial = ", serial)
								.addCriterion("deleted = ", false);
		
		Pager<Coupon> pager = new Pager<>();
		pager.setPageSize(Integer.MAX_VALUE);//一次性加载套票中所有子票
		return couponMapper.selectByExample(example, pager);
	}

	@Override
	@Transactional
	public boolean batchUpdateCoupon(List<Coupon> couponList) {
		if(CollectionUtils.isEmpty(couponList)){
			return false;
		}
		
		for(Coupon coupon : couponList){
			if(couponMapper.updateByPrimaryKey(coupon) != 1){
				throw new RuntimeException();
			}
		}
		return true;
	}
	
	@Override
	public boolean updateCoupon(Coupon coupon) {
		return couponMapper.updateByPrimaryKey(coupon) == 1;
	}


	@Override
	public boolean updateCouponInfoReceived(int num, CouponInfo ci) {
		String isLimited = "";
		if(ci.getLimitCount()!=null && ci.getLimitCount()>0) {
			isLimited = "and received+"+num+"<=limitCount";
		}
		return couponInfoMapper.updateCouponInfoReceived(num,ci.getId(),isLimited) == 1;
	}


	@Override
	public Map<String, Integer> couponCountMap(User user, Business business ,CouponExample  example) {
		 Map<String,Integer> map =  new HashMap<>();
		 Long userId  = user.getId();
		 String mobile = user.getMobile();
		 if (userId == null && StringUtils.isBlank(mobile)) 
		   return  map;
		 if (example == null) 
			example  = new CouponExample() ;
		 if (StringUtils.isNotBlank(mobile)) {
			 if (userId != null) {
				 example.appendCriterion("(user_id = "+userId+"  or mobile = '"+mobile+"')");
			 }else{
				 example.appendCriterion("mobile=", mobile);
			 }
		 }
		if (business != null ) {
			 if (business.getSelfBusinessId() == null || business.getSelfBusinessType() == null) 
				return map;
			 example.appendCriterion("couponinfo_id in (select cs.couponInfo_id from CouponInfo_Scope  cs  where cs.businessId  = "
		             +business.getSelfBusinessId()+" and businessType= '"+business.getSelfBusinessType().toString()+"' and deleted= false)");
		}
		 example.appendCriterion("deleted=", false);
		 List<Coupon> couponList = couponMapper.selectByExample(example, null);
		 for (Coupon coupon : couponList) {
				CouponStatus status = coupon.getStatus();
				Integer  count = map.get(status.toString()) ;
				if (count == null ) 
					count  = 0;
				    count++;
				map.put(status.toString(), count);
			}
		return map;
	}


	@Override
	public ThirdpartyCoupon findThirdCouponByCodeAndPaw(String password) {
		ThirdpartyCouponExample example = new ThirdpartyCouponExample();
		example.createCriteria()
				.addCriterion("password = ", password)
				.addCriterion("deleted=", false);
		return thirdpartycouponMapper.selectOneByExample(example);
	}


	@Override
	public List<String> getLimtPayTimeList() {
		Configuration lptConf = confService.findByName("LimitPayTimeList");
		if(lptConf!=null && lptConf.getValue()!=null) {
			 List<String> limitPayTimeList = new ArrayList<String>(Arrays.asList(StringUtils.split(lptConf.getValue(),","))); 
			 return limitPayTimeList;
		}
		return null;
	}


	@Override
	public CouponInfo findCouponInfoByIdAndBusiness(Long id,
			String businessType, String businessId) {
		CouponInfoExample example = new CouponInfoExample();
		example.createCriteria().addCriterion("id = ", id)
						.addCriterion("deleted = ", false)
						.addCriterion(BusinessSQLBuilder.getBusinessByShopInCouponInfoSQL(Business.BusinessType.valueOf(businessType), Long.valueOf(businessId)));
		List<CouponInfo> list = couponInfoMapper.selectByExample(example, null);
		if(list.size() == 1)
			return list.get(0);
		
		return null;
	}


	@Override
	public List<CouponInfo> findCouponInfoByIds(String [] cidList) {
		if(cidList == null || cidList.length == 0){
			return null;
		}
		List<CouponInfo> list = new ArrayList<CouponInfo>();
		CouponInfoExample example = new CouponInfoExample();
		example.createCriteria().addCriterion("deleted=", false)
								.addCriterion("published=", true);

		StringBuilder orsql = new StringBuilder("(");
		int orCount = 0;
		for(String id : cidList){
			if(orCount++ > 0){
				orsql.append(" OR ");
			}
			orsql.append("id=").append(id);
		}
		orsql.append(")");
		example.appendCriterion(orsql.toString());
		list = couponInfoMapper.selectByExample(example, null);
		return list;
	}

}
