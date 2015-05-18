package com.xpos.api;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONObject;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import com.xpos.api.param.UsedCoupon;
import com.xpos.api.result.ResCode;
import com.xpos.api.result.ValidateError;
import com.xpos.common.entity.Coupon;
import com.xpos.common.entity.Coupon.CouponStatus;
import com.xpos.common.entity.Terminal;
import com.xpos.common.entity.example.CouponExample;
import com.xpos.common.entity.face.Business.BusinessType;
import com.xpos.common.searcher.CouponSearcher;
import com.xpos.common.service.CouponService;
import com.xpos.common.service.TerminalService;
import com.xpos.common.utils.Pager;


public class MerchantUsedCouponResource extends BaseResource {
	private Logger logger = LoggerFactory.getLogger(MerchantUsedCouponResource.class);
	
	@Autowired
	private TerminalService terminalService;
	@Autowired
	private CouponService couponService;
	
	/**
	 * /merchant/{mid}/coupon/used/list?deviceNumber=xxx&startDate=2013-01-01&page=1&pagesize=20
	 * @return
	 */
	@Get("json")
	public Representation getUsedConpon(){
		
		String mid = (String) getRequestAttributes().get("mid");
		String deviceNumber = getQuery().getFirstValue("deviceNumber");
		String startDate = getQuery().getFirstValue("startDate");
		String pageTo = getQuery().getFirstValue("page");
		String pagesize = getQuery().getFirstValue("pagesize");
		
		if(StringUtils.isBlank(deviceNumber)){
			return new JsonRepresentation(new ValidateError("0604", "deviceNumber不能为空"));
		}
		
		//take care of device_number here...
		Terminal terminal = terminalService.findTerminalByDevice(deviceNumber);
		if(terminal == null){
			return new JsonRepresentation(new ValidateError("0601","设备未注册"));
		}else if(terminal.getShop() == null){
			return new JsonRepresentation(new ValidateError("0603","指定商户不存在/已删除"));
		}else if(!terminal.getShop().getId().equals(Long.valueOf(mid))){
			return new JsonRepresentation(new ValidateError("0602","商户与设备不匹配"));
		}
		
		//Default display the first page
		Integer currentPage = (StringUtils.isBlank(pageTo) || !StringUtils.isNumeric(pageTo))? 1:Integer.parseInt(pageTo);
		if(currentPage <= 0){
			currentPage = 1;
		}
		//Default page size is 20
		Integer size = (StringUtils.isBlank(pagesize) || !StringUtils.isNumeric(pagesize))? 20:Integer.parseInt(pagesize);
		if(size <= 0){
			size = 20;
		}
		
		//parse start date
		DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd");
		DateTime _startDate = null;
		try{
			_startDate = fmt.parseDateTime(startDate);
		}catch(Exception e){
		}
		
		JsonRepresentation re = null;
		Pager<Coupon> pager = new Pager<>();
		pager.setPageSize(size);
		pager.setPageNumber(currentPage);
		List<UsedCoupon> usedCouponList = new ArrayList<>();
		try{
			//核销数量统计
			DateTime today = new DateTime();
			DateTime yesterday = today.minusDays(1);
			
			CouponSearcher couponSearcher = new CouponSearcher();
			Set<CouponStatus> couponStatus = new HashSet<>();
			couponStatus.add(CouponStatus.USED);
			couponSearcher.setBusiness(terminal.getShop());
			couponSearcher.setStatus(couponStatus);
			couponSearcher.setStartDate(today.toDate());
			couponSearcher.setEndDate(today.toDate());
			int todayConsumedCouponCount = couponService.countCoupons((CouponExample)couponSearcher.getExample());
			couponSearcher.setStartDate(yesterday.toDate());
			couponSearcher.setEndDate(yesterday.toDate());
			int yesterdayConsumedCouponCount = couponService.countCoupons((CouponExample)couponSearcher.getExample());
			
			
			CouponExample example = new CouponExample();
			example.createCriteria().addCriterion("businessId = ", Long.valueOf(mid))
									.addCriterion("businessType = ", BusinessType.SHOP.toString())
									.addCriterion("status = ", CouponStatus.USED.toString());
			if(_startDate != null){
				example.appendCriterion("consumeDate >= ", _startDate.toString("yyyy-MM-dd 00:00:00"));
			}
			example.setOrderByClause(" consumeDate desc");
			couponService.findCoupons(example, pager);
			
			if(!CollectionUtils.isEmpty(pager.getList())){
				for(Coupon coupon : pager.getList()){
					UsedCoupon usedCoupon = new UsedCoupon();
					usedCoupon.setCid(coupon.getId());
					usedCoupon.setCoupon(coupon.getCouponCode());
					String phone = "";
					if(StringUtils.isNotBlank(coupon.getMobile()) && coupon.getMobile().length() == 11){
						phone = new StringBuilder(coupon.getMobile()).replace(3, 7, "****").toString();
					}
					usedCoupon.setPhone(phone);
					usedCoupon.setUsedDate(new DateTime(coupon.getConsumeDate()).toString("yyyy-MM-dd"));
					usedCoupon.setCouponInfoName(coupon.getCouponInfo().getName());
					usedCouponList.add(usedCoupon);
				}
			}
			re = new JsonRepresentation(ResCode.General.OK);
			JSONObject json = re.getJsonObject();
			json.put("total", pager.getTotalCount());
			json.put("page", currentPage);
			json.put("pagesize", size);
			json.put("hasPrefix", pager.isForward());
			json.put("hasNext", pager.isNext());
			json.put("list", usedCouponList);
			json.put("todayCouponCount", todayConsumedCouponCount);
			json.put("yesterdayCouponCount", yesterdayConsumedCouponCount);
			
		}catch(Exception e){
			logger.error("Cannot find MERCHANT used coupon. ", e);
			re = new JsonRepresentation(ResCode.General.SERVER_INTERNAL_ERROR);
		}
		return re;
	}
}
