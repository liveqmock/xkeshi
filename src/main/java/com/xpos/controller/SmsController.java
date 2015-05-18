package com.xpos.controller;

import java.math.BigDecimal;
import java.math.RoundingMode;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.xkeshi.interceptor.form.AvoidDuplicateSubmission;
import com.xpos.common.entity.CouponInfo;
import com.xpos.common.entity.CouponInfo.CouponInfoType;
import com.xpos.common.entity.SMSMessage;
import com.xpos.common.entity.SMSTask;
import com.xpos.common.entity.Shop;
import com.xpos.common.entity.example.SMSTaskExample;
import com.xpos.common.entity.face.Business.BusinessType;
import com.xpos.common.entity.member.Member;
import com.xpos.common.exception.CouponInfoException;
import com.xpos.common.searcher.member.MemberSearcher;
import com.xpos.common.service.CouponService;
import com.xpos.common.service.SMSService;
import com.xpos.common.service.ShopService;
import com.xpos.common.service.member.MemberService;
import com.xpos.common.utils.IDUtil;
import com.xpos.common.utils.Pager;

@Controller
@RequestMapping("sms")
public class SmsController extends BaseController{
	
	@Resource
	private MemberService memberService;
	@Resource
	private SMSService smsService;
	@Resource
	private CouponService couponService;
	@Resource
	private ShopService shopService;
	
	@Value("#{settings['sms.unitPrice']}")
	private double smsUnitPrice;
	
	@RequestMapping(value="task/list", method=RequestMethod.GET)
	public String findSMSTaskList(Pager<SMSTask> pager, Model model){
		pager = smsService.findSMSTasks(getBusiness(), new SMSTaskExample(), pager);
		model.addAttribute("pager", pager);
		return "sms/task_list";
	}
	
	@AvoidDuplicateSubmission(addToken=true)
	@RequestMapping(value="send",  method=RequestMethod.GET)
	public String showAdd(Model model, @RequestParam(value="couponInfoEid", required=false, defaultValue="") String couponInfoEid,
			@RequestParam(value="type", required=false, defaultValue="") String couponInfoType){
		
		int count = memberService.findMembersByBusiness(getBusiness(), new Pager<Member>(), new MemberSearcher()).getTotalCount();
		model.addAttribute("memberCount", count);
		model.addAttribute("couponInfoEid", couponInfoEid);
		model.addAttribute("balance", getBusiness().getBalance());
		
		if(StringUtils.equalsIgnoreCase(couponInfoType, "P")){
			model.addAttribute("couponInfoType", CouponInfoType.PACKAGE.toString());
		}else if(StringUtils.equalsIgnoreCase(couponInfoType, "N")){
			model.addAttribute("couponInfoType", CouponInfoType.NORMAL.toString());
		}
		return "sms/send";
	}
	
	@AvoidDuplicateSubmission(removeToken=true,errorRedirectURL="/sms/task/list")
	@RequestMapping(value="send",  method=RequestMethod.POST)
	public String send(SMSTask task, String sendType, String mobiles, Model model, @ModelAttribute("couponInfoEid")String couponInfoEid,
			@RequestParam("type") String couponInfoType, RedirectAttributes  attributes){
		//校验
		if(StringUtils.isBlank(task.getTemplate())){
			attributes.addFlashAttribute("status", FAILD);
			attributes.addFlashAttribute("msg", "短信发送内容不能为空");
			return "redirect:/sms/send?type="+couponInfoType+"&couponInfoEid="+couponInfoEid;
		}
		
		//发送
		if("all".equalsIgnoreCase(sendType)){
			int count = memberService.findMembersByBusiness(getBusiness(), new Pager<Member>(), new MemberSearcher()).getTotalCount();
			if(count <= 0){
				attributes.addFlashAttribute("status", FAILD);
				attributes.addFlashAttribute("msg", "会员数量为空,请导入会员");
				return "redirect:/sms/send?type="+couponInfoType+"&couponInfoEid="+couponInfoEid;
			}else if(getBusiness().getBalance().compareTo(new BigDecimal(count).multiply(new BigDecimal(smsUnitPrice)).setScale(2, RoundingMode.HALF_UP)) <= 0){ //校验账户余额，以及与待发短信数量比较
				attributes.addFlashAttribute("msg", "账户余额不足，请先充值");
				attributes.addFlashAttribute("status", FAILD);
				return "redirect:/sms/send?type="+couponInfoType+"&couponInfoEid="+couponInfoEid;
			}
			Long couponInfoId = IDUtil.decode(couponInfoEid);
			CouponInfo couponInfo = null;
			try{
				couponInfo = couponService.findCouponInfoById(couponInfoId);
			}catch(Exception e){
				couponInfo = null;
			}
			
			String result = null;
			if(couponInfo != null){
				result = smsService.sendByMemberList(getBusiness(), getAccount(), task, couponInfo);
			}else{
				result = smsService.sendByMemberList(getBusiness(), getAccount(), task);
			}
			model.addAttribute("resultMessage", result);
		}
		
		if("custom".equalsIgnoreCase(sendType) && StringUtils.isNotBlank(mobiles)){
			String[] mobileArray = mobiles.split("\r\n");
			if(getBusiness().getBalance().compareTo(new BigDecimal(mobileArray.length).multiply(new BigDecimal(smsUnitPrice)).setScale(2, RoundingMode.HALF_UP)) <= 0){ //校验账户余额，以及与待发短信数量比较
				attributes.addFlashAttribute("msg", "账户余额不足，请先充值");
				attributes.addFlashAttribute("status", FAILD);
				return "redirect:/sms/send?type="+couponInfoType+"&couponInfoEid="+couponInfoEid;
			}
			
			Long couponInfoId = IDUtil.decode(couponInfoEid);
			CouponInfo couponInfo = null;
			try{
				couponInfo = couponService.findCouponInfoById(couponInfoId);
			}catch(Exception e){
				couponInfo = null;
			}
			String result = null;
			try {
				if(couponInfo != null){
					result = smsService.sendByMobileList(getBusiness(), getAccount(), task, mobileArray, couponInfo);
				}else{
					result = smsService.sendByMobileList(getBusiness(), getAccount(), task, mobileArray);
				}
				model.addAttribute("resultMessage", result);
			} catch (CouponInfoException e) {
				attributes.addFlashAttribute("status", FAILD);
				attributes.addFlashAttribute("msg", e.getMessage());
				return "redirect:/sms/send?type="+couponInfoType+"&couponInfoEid="+couponInfoEid;
			}
		}
		return "redirect:/sms/task/list";
	}
	
	@RequestMapping(value="task/detail/{eid}", method=RequestMethod.GET)
	public String taskDetail(@PathVariable("eid")String eid, Pager<SMSMessage> pager, Model model){
		Long id = IDUtil.decode(eid);
		SMSTask smsTask = smsService.findSMSTaskById(id);
		if(smsTask != null ) {
			if(getBusiness().getSelfBusinessType().equals(BusinessType.MERCHANT)) {
				if(smsTask.getBusinessType().equals(BusinessType.SHOP)) {
					Shop shop = shopService.findShopByIdIgnoreVisible(smsTask.getBusinessId());
					if(shop == null || shop.getMerchant() == null || !shop.getMerchant().getId().equals(getBusiness().getSelfBusinessId())){
						return null;
					}
				}else {
					if(!getBusiness().getSelfBusinessId().equals(smsTask.getBusinessId())) {
						return null;
					}
				}
			}else{
				if(!smsTask.getBusinessId().equals(getBusiness().getSelfBusinessId())
						|| !smsTask.getBusinessType().equals(getBusiness().getSelfBusinessType())) {
					return null;
				}
			}
			model.addAttribute("smsTask", smsTask);
			
			int[] statistic = smsService.statusStatisticBySMSTask(smsTask);
			model.addAttribute("statistic", statistic);
			
			pager = smsService.findSMSMessagesBySMSTask(pager, smsTask);
			model.addAttribute("pager", pager);
		}
		return "sms/task_detail";
	}
}
