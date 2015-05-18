package com.xpos.controller;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.drongam.hermes.entity.SMS;
import com.xpos.common.entity.Coupon.CouponStatus;
import com.xpos.common.entity.CouponInfo.CouponInfoType;
import com.xpos.common.entity.Refund;
import com.xpos.common.entity.Refund.RefundStatus;
import com.xpos.common.entity.RefundLog;
import com.xpos.common.entity.example.RefundExample;
import com.xpos.common.searcher.RefundSearcher;
import com.xpos.common.service.CouponService;
import com.xpos.common.service.RefundLogService;
import com.xpos.common.service.RefundService;
import com.xpos.common.service.SMSService;
import com.xpos.common.service.ShopService;
import com.xpos.common.utils.Pager;

@Controller
@RequestMapping("refund")
public class RefundController extends BaseController{
	
	@Resource
	private CouponService couponService;
	
	@Resource
	private RefundService refundService;
	
	@Resource
	private RefundLogService refundLogService;
	
	@Resource
	private SMSService smsService;
	
	@Resource
	private ShopService shopService;
	
	/** 退款列表 */
	@RequestMapping(value="/list", method=RequestMethod.GET)
	public String findRefundList(Pager<Refund> pager, RefundSearcher searcher, Model model){
		pager = refundService.findRefunds(getBusiness(), (RefundExample)searcher.getExample(), pager);
		model.addAttribute("pager", pager);
		model.addAttribute("searcher", searcher);
		return "coupon/coupon_refund_list";
	}
	
	/** 拒绝申请 */
	@RequestMapping(value="/update/reject", method = RequestMethod.POST)
	public String rejectApply(Refund ref){
		Refund refund = refundService.findRefundByCode(ref.getCode());
		RefundStatus status = ref.getStatus();
		if(refund == null || status == null || refund.getStatus().equals(status)){
			return "redirect:/refund/list";
		}
		
		RefundStatus curStatus = refund.getStatus();
		if(RefundStatus.REJECTED.equals(status) && RefundStatus.APPLY.equals(curStatus)){
			//拒绝退款申请
			//1.update refund
			refund.setStatus(status);
			String log = "管理员审核拒绝" + (StringUtils.isNotBlank(ref.getRemark())?"(备注说明："+ref.getRemark()+")":"");
			refund.setRemark(log);
			refundService.updateRefund(refund);
			
			//2.update coupon
			refundService.updateRelatedCoupon(refund, CouponStatus.REFUND_FAIL);
			
			//3.send SMS
			SMS sms = new SMS();
			if(CouponInfoType.NORMAL.equals(refund.getCoupon().getType())){
				sms.setMessage("您的【" +refund.getCoupon().getCouponInfo().getName()+ "】退款申请失败，请与我们客服联系");
			}else if(CouponInfoType.CHILD.equals(refund.getCoupon().getType())){
				sms.setMessage("您的【" +refund.getCoupon().getParent().getName()+ "】退款申请失败，请与我们客服联系");
			}
			sms.setMobile(refund.getCoupon().getMobile());
			smsService.sendSMSAndDeductions(refund.getBusinessId() ,refund.getBusinessType(),sms,null,"拒绝退款申请" );
		}
		
		return "redirect:/refund/list";
	}
	
	/** 通过申请 */
	@RequestMapping(value="/update/accept", method = RequestMethod.POST)
	public String acceptApply(Refund ref, RedirectAttributes model){
		Refund refund = refundService.findRefundByCode(ref.getCode());
		RefundStatus status = ref.getStatus();
		if(refund == null || status == null || refund.getStatus().equals(status)){
			return "redirect:/refund/list";
		}
		
		RefundStatus curStatus = refund.getStatus();
		if(RefundStatus.ACCEPTED.equals(status) && RefundStatus.APPLY.equals(curStatus)){
			//通过退款申请
			//1.update refund
			refund.setStatus(status);
			String log = "管理员审核通过";
			refund.setRemark(log);
			refundService.updateRefund(refund);
			
			//2. update coupon
			refundService.updateRelatedCoupon(refund, CouponStatus.REFUND_ACCEPTED);
			
			//3.try auto refund process
			Map<String, String> map = refundService.executeAutoRefunding(refund);
			model.addFlashAttribute("refundForm", map.get("refundForm"));
		}
		
		//send SMS
		if(RefundStatus.AUTO_SUCCESS.equals(refund.getStatus())){
			SMS sms = new SMS();
			if(CouponInfoType.NORMAL.equals(refund.getCoupon().getType())){
				sms.setMessage("您的【" +refund.getCoupon().getCouponInfo().getName()+ "】退款成功，退款款项将在10个工作日内退至您支付的账户");
			}else if(CouponInfoType.CHILD.equals(refund.getCoupon().getType())){
				sms.setMessage("您的【" +refund.getCoupon().getParent().getName()+ "】退款成功，退款款项将在10个工作日内退至您支付的账户");
			}
			sms.setMobile(refund.getCoupon().getMobile());
			smsService.sendSMSAndDeductions(refund.getBusinessId() ,refund.getBusinessType(),sms,null,"退款审核通过" );
		}
		return "redirect:/refund/list";
	}
	
	/** 重新发起自动退款 */
	@RequestMapping(value = "/update/retryRefund", method = RequestMethod.GET)
	public String retryAutoRefund(Refund ref, RedirectAttributes model){
		Refund refund = refundService.findRefundByCode(ref.getCode());
		if(refund == null){
			return "redirect:/refund/list";
		}else if(RefundStatus.AUTO_FAILED.equals(refund.getStatus()) || RefundStatus.MANUAL_FAILED.equals(refund.getStatus())){
			//退款失败尝试重新发起自动退款
			
//			updateRelatedCoupon(refund, CouponStatus.REFUND_ACCEPTED); //自动退款重试暂时不修改优惠券状态
			
			Map<String, String> map = refundService.executeAutoRefunding(refund);
			model.addFlashAttribute("refundForm", map.get("refundForm"));
		}else if(RefundStatus.AUTO_EXECUTE.equals(refund.getStatus()) || RefundStatus.MANUAL_EXECUTE.equals(refund.getStatus())){
			//自动退款中，尝试重新发起自动退款
			Map<String, String> map = refundService.executeAutoRefunding(refund);
			model.addFlashAttribute("refundForm", map.get("refundForm"));
		}else if(RefundStatus.ACCEPTED.equals(refund.getStatus())  ){
			//支付宝wap通过退款，但在支付宝退款页面中断了退款操作
			Map<String, String> map = refundService.executeAutoRefunding(refund);
			model.addFlashAttribute("refundForm", map.get("refundForm"));
		}
		
		//send SMS
		if(RefundStatus.AUTO_SUCCESS.equals(refund.getStatus())){
			SMS sms = new SMS();
			if(CouponInfoType.NORMAL.equals(refund.getCoupon().getType())){
				sms.setMessage("您的【" +refund.getCoupon().getCouponInfo().getName()+ "】退款成功，退款款项将在10个工作日内退至您支付的账户");
			}else if(CouponInfoType.CHILD.equals(refund.getCoupon().getType())){
				sms.setMessage("您的【" +refund.getCoupon().getParent().getName()+ "】退款成功，退款款项将在10个工作日内退至您支付的账户");
			}
			sms.setMobile(refund.getCoupon().getMobile());
			smsService.sendSMSAndDeductions(refund.getBusinessId() ,refund.getBusinessType(),sms,null,"重新自动退款" );
		}
		return "redirect:/refund/list";
	}

	
	/** 切换至人工退款 */
	@RequestMapping(value="/update/toManual", method = RequestMethod.GET)
	public String switchToManualProcess(Refund ref){
		Refund refund = refundService.findRefundByCode(ref.getCode());
		if(refund == null){
			return "redirect:/refund/list";
		}
		
		RefundStatus curStatus = refund.getStatus();
		if(RefundStatus.AUTO_FAILED.equals(curStatus)){
			//自动退款失败，可切换至人工退款流程
			
//			updateRelatedCoupon(refund, CouponStatus.REFUND_ACCEPTED); //自动退款重试暂时不修改优惠券状态
			
			refund.setStatus(RefundStatus.MANUAL_EXECUTE);
			refund.setRemark("进入人工退款流程");
			refundService.updateRefund(refund);
		}else if(RefundStatus.AUTO_EXECUTE.equals(curStatus)){
			//自动退款中，强制切换至人工流程
		}
		return "redirect:/refund/list";
	}
	
	/** 人工退款成功 */
	@RequestMapping(value="/update/manualSuccess", method = RequestMethod.POST)
	public String manualSucceeded(Refund ref){
		Refund refund = refundService.findRefundByCode(ref.getCode());
		if(refund == null){
			return "redirect:/refund/list";
		}
		
		RefundStatus curStatus = refund.getStatus();
		if(RefundStatus.MANUAL_EXECUTE.equals(curStatus)){
			//人工退款成功
			//1.update reund
			refund.setStatus(RefundStatus.MANUAL_SUCCESS);
			String log = "人工退款成功" + (StringUtils.isNotBlank(ref.getRemark())?"(备注说明："+ref.getRemark()+")":"");
			refund.setRemark(log);
			refundService.updateRefund(refund);
			
			//2.update coupon
			refundService.updateRelatedCoupon(refund, CouponStatus.REFUND_SUCCESS);
			
			//3.send SMS
			SMS sms = new SMS();
			if(CouponInfoType.NORMAL.equals(refund.getCoupon().getType())){
				sms.setMessage("您的【" +refund.getCoupon().getCouponInfo().getName()+ "】退款成功");
			}else if(CouponInfoType.CHILD.equals(refund.getCoupon().getType())){
				sms.setMessage("您的【" +refund.getCoupon().getParent().getName()+ "】退款成功");
			}
			sms.setMobile(refund.getCoupon().getMobile());
			smsService.sendSMSAndDeductions(refund.getBusinessId() ,refund.getBusinessType(),sms,null,"人工退款成功" );
		}
		return "redirect:/refund/list";
	}
	
	/** 人工退款失败 */
	@RequestMapping(value="/update/manualFail", method = RequestMethod.POST)
	public String manualFailed(Refund ref){
		Refund refund = refundService.findRefundByCode(ref.getCode());
		if(refund == null){
			return "redirect:/refund/list";
		}
		
		RefundStatus curStatus = refund.getStatus();
		if(RefundStatus.MANUAL_EXECUTE.equals(curStatus)){
			//人工退款成功
			//1.update refund
			refund.setStatus(RefundStatus.MANUAL_FAILED);
			String log = "人工退款失败" + (StringUtils.isNotBlank(ref.getRemark())?"(备注说明："+ref.getRemark()+")":"");
			refund.setRemark(log);
			refundService.updateRefund(refund);
			
			//2.update coupon
			refundService.updateRelatedCoupon(refund, CouponStatus.REFUND_FAIL);
			
			//3.send SMS
			SMS sms = new SMS();
			if(CouponInfoType.NORMAL.equals(refund.getCoupon().getType())){
				sms.setMessage("您的【" +refund.getCoupon().getCouponInfo().getName()+ "】退款失败，请与我们客服联系");
			}else if(CouponInfoType.CHILD.equals(refund.getCoupon().getType())){
				sms.setMessage("您的【" +refund.getCoupon().getParent().getName()+ "】退款失败，请与我们客服联系");
			}
			sms.setMobile(refund.getCoupon().getMobile());
			smsService.sendSMSAndDeductions(refund.getBusinessId() ,refund.getBusinessType(),sms,null,"人工退款失败" );
		}
		return "redirect:/refund/list";
	}
	
	/** 查看退款交易记录  */
	@RequestMapping(value = "/refundlog" ,method = RequestMethod.GET)
	public String refundLog(Model model, @RequestParam("code") String code ,RedirectAttributes attributes){
		Refund refund = refundService.findRefundByCode(code);
		if (refund == null) {
			attributes.addFlashAttribute("status", FAILD);
			attributes.addFlashAttribute("msg", "该笔退款记录不存在");
			return "redirect:/refund/list";
		}
		List<RefundLog> refundLogs = refundLogService.refundLogs(code);
		model.addAttribute("refund", refund);
		model.addAttribute("refundLogs", refundLogs);
		return  "coupon/refundlog";
	}
	
}
