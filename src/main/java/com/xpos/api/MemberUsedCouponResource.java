package com.xpos.api;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

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
import com.xpos.common.entity.Configuration;
import com.xpos.common.entity.Coupon;
import com.xpos.common.entity.Coupon.CouponStatus;
import com.xpos.common.entity.Terminal;
import com.xpos.common.entity.example.CouponExample;
import com.xpos.common.entity.face.Business.BusinessType;
import com.xpos.common.service.ConfigurationService;
import com.xpos.common.service.CouponService;
import com.xpos.common.service.TerminalService;
import com.xpos.common.utils.Pager;

public class MemberUsedCouponResource extends BaseResource {
	private Logger logger = LoggerFactory.getLogger(MemberUsedCouponResource.class);
	private final String MOBILE_NUM_REGEX = "MOBILE_NUMBER_REGEX";
	
	@Autowired
	private TerminalService terminalService;
	@Autowired
	private CouponService couponService;
	@Autowired
	private ConfigurationService confService;
	
	/**
	 * /merchant/{mid}/member/coupon/used/list?deviceNumber=xxx&phone=18888888888&date=2013-01-01&status=1&page=1&pagesize=20&queryStr=xxxxx
	 * @return
	 */
	@Get("json")
	public Representation getUsedCouponList(){
		String mid = (String) getRequestAttributes().get("mid");
		String deviceNumber = getQuery().getFirstValue("deviceNumber");
		String date = getQuery().getFirstValue("date");
		String phone = getQuery().getFirstValue("phone");
		String statusStr = getQuery().getFirstValue("status");
		String queryStr = getQuery().getFirstValue("queryStr");
		String pageTo = getQuery().getFirstValue("page");
		String pagesize = getQuery().getFirstValue("pagesize");
		
		CouponStatus status = null;
		Configuration conf = confService.findByName(MOBILE_NUM_REGEX);
		if(StringUtils.isBlank(deviceNumber)){
			return new JsonRepresentation(new ValidateError("0806", "deviceNumber不能为空"));
		}else if(StringUtils.isBlank(phone)){
			return new JsonRepresentation(new ValidateError("0804", "手机号不能为空"));
		}else if(!Pattern.matches(conf.getValue(), phone)){
			return new JsonRepresentation(new ValidateError("0805", "手机号格式错误"));
		}else if(StringUtils.isBlank(statusStr) || !StringUtils.isNumeric(statusStr) 
				|| (status = CouponStatus.queryByState(Integer.valueOf(statusStr))) == null){
			return new JsonRepresentation(new ValidateError("0807", "状态码不符合规则"));
		}
		
		//take care of device_number here...
		Terminal terminal = terminalService.findTerminalByDevice(deviceNumber);
		if(terminal == null){
			return new JsonRepresentation(new ValidateError("0801","设备未注册"));
		}else if(terminal.getShop() == null){
			return new JsonRepresentation(new ValidateError("0803","指定商户不存在/已删除"));
		}else if(!terminal.getShop().getId().equals(Long.valueOf(mid))){
			return new JsonRepresentation(new ValidateError("0802","商户与设备不匹配"));
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
		//不传该参数默认所有时间
		DateTime startDate = null;
		if(StringUtils.isNotBlank(date)){
			DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd");
			try{
				startDate = fmt.parseDateTime(date);
			}catch(Exception e){
				return new JsonRepresentation(new ValidateError("0808","日期格式错误"));
			}
		}
		
		JsonRepresentation re = null;
		Pager<Coupon> pager = new Pager<>();
		pager.setPageSize(size);
		pager.setPageNumber(currentPage);
		List<UsedCoupon> usedCouponList = new ArrayList<>();
		try{
			CouponExample example = new CouponExample();
			example.createCriteria().addCriterion("businessId = ", Long.valueOf(mid))
									.addCriterion("businessType = ", BusinessType.SHOP.toString())
									.addCriterion("mobile = ", phone)
									.addCriterion("status = ", status.toString());
			if(startDate != null){
				if(status.equals(CouponStatus.AVAILABLE) || status.equals(CouponStatus.EXPIRED)){
					example.appendCriterion("createDate >= ", startDate.toString("yyyy-MM-dd 00:00:00"));
					example.setOrderByClause(" createDate DESC");
				}else if(status.equals(CouponStatus.USED)){
					example.appendCriterion("consumeDate >= ", startDate.toString("yyyy-MM-dd 00:00:00"));
					example.setOrderByClause(" consumeDate DESC");
				}
			}
			
			couponService.findCoupons(example, pager);
			
			if(!CollectionUtils.isEmpty(pager.getList())){
				for(Coupon coupon : pager.getList()){
					UsedCoupon usedCoupon = new UsedCoupon();
					usedCoupon.setCid(coupon.getId());
//					usedCoupon.setCoupon(coupon.getCouponCode());
					usedCoupon.setPhone(phone);
					usedCoupon.setUsedDate(new DateTime(coupon.getConsumeDate()).toString("yyyy-MM-dd"));
					usedCoupon.setStatus(coupon.getStatus().getState());
					usedCoupon.setCouponInfoName(coupon.getCouponInfo().getName());
					usedCoupon.setCouponInfoIntro(coupon.getCouponInfo().getIntro());
					usedCoupon.setCouponInfoSerial(coupon.getCouponInfo().getEid());
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
			
		}catch(Exception e){
			logger.error("Cannot find MEMBER used coupon due to "+ e.getMessage(), e);
			re = new JsonRepresentation(ResCode.General.SERVER_INTERNAL_ERROR);
		}
		return re;
	}
	
}
