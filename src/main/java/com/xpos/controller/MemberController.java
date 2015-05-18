package com.xpos.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.xkeshi.endpoint.GrantResult;
import com.xkeshi.endpoint.JSONResponse;
import com.xpos.common.entity.Merchant;
import com.xpos.common.entity.Shop;
import com.xpos.common.entity.face.Business;
import com.xpos.common.entity.face.Business.BusinessType;
import com.xpos.common.entity.member.Member;
import com.xpos.common.entity.member.MemberType;
import com.xpos.common.entity.member.MerchantMemberType;
import com.xpos.common.entity.member.ShopMemberType;
import com.xpos.common.searcher.member.MemberSearcher;
import com.xpos.common.service.ShopService;
import com.xpos.common.service.member.MemberService;
import com.xpos.common.service.member.MemberTypeService;
import com.xpos.common.utils.IDUtil;
import com.xpos.common.utils.Pager;

/**
 * 后台会员模块集团、商户通用的部分方法。
 * 具体集团或商户角色特定方法，参看MerchantMemberController、ShopMemberController
 */
@Controller
@RequestMapping("member")
public class MemberController extends BaseController{

	@Autowired
	private MemberService memberService;
	
	@Autowired
	private MemberTypeService memberTypeService;
	
	@Autowired
	private ShopService shopService;

 	/** 集团查看某个会员详情 */
	@RequestMapping(value="/{memberEid}", method=RequestMethod.GET)
	public String showMemberDetail(@PathVariable("memberEid")String memberEid, Model model) throws Exception{
		Long id = IDUtil.decode(memberEid);
		Member member = memberService.findMemberByIdWithAttributes(id);
		
		boolean result = false;
		String msg = "未找到指定会员";
		
		if(member == null){
			model.addAttribute("status", STATUS_FAILD);
			model.addAttribute("msg", msg);
			return "member/member_detail";
		}
		
		MemberSearcher searcher = new MemberSearcher();
		searcher.setMemberId(id);
		if(getBusiness() instanceof Merchant){
			Merchant merchant = (Merchant)getBusiness();
			Pager<Member> pager = memberService.findMembersByBusiness(merchant, null, searcher);
			if(pager.getTotalCount() > 0){
				result = true;
				model.addAttribute("member", member);
			}
		}else if(getBusiness() instanceof Shop){
			Shop shop = (Shop)getBusiness();
			Pager<Member> pager = memberService.findMembersByBusiness(shop, null, searcher);
			if(pager.getTotalCount() > 0){
				result = true;
				model.addAttribute("member", member);
			}
		}
		
		if(BusinessType.MERCHANT.equals(member.getBusinessType())){
			List<MerchantMemberType> memberTypes = memberTypeService.findMerchantMemberTypeListWithAttributeTemplateByMerchantId(member.getBusinessId());
			model.addAttribute("memberTypes", memberTypes);
			model.addAttribute("type", "merchant");
		}else if(BusinessType.SHOP.equals(member.getBusinessType())){
			List<ShopMemberType> memberTypes = memberTypeService.findShopMemberTypeListWithAttributeTemplateByShopId(member.getBusinessId());
			model.addAttribute("memberTypes", memberTypes);
			model.addAttribute("type", "shop");
		}
		
		if(!result){
			model.addAttribute("status", STATUS_FAILD);
			model.addAttribute("msg", msg);
		}
		return "member/member_detail";
		
	}
	
	/** 删除指定的某个会员 */
	@RequestMapping(value="/{memberEid}/delete", method=RequestMethod.DELETE)
	public String deleteMember(@PathVariable("memberEid")String memberEid, RedirectAttributes redirectAttributes, Model model) throws Exception {
		Long id = IDUtil.decode(memberEid);
		Member member = memberService.findMemberByIdWithAttributes(id);
		
		String redirectURL = null;
		if(getBusiness() instanceof Merchant){
			redirectURL = "/member/merchant/list";
		}else if(getBusiness() instanceof Shop){
			redirectURL = "/member/shop/list";
		}
		
		if(member == null){
			redirectAttributes.addFlashAttribute("status", STATUS_FAILD);
			redirectAttributes.addFlashAttribute("msg", "会员删除失败");
			return "redirect:" + redirectURL;
		}
		
		String errorMsg = memberService.validateAndDeleteMember(member, getBusiness());
		if(StringUtils.isBlank(errorMsg)){
			redirectAttributes.addFlashAttribute("status", STATUS_SUCCESS);
			redirectAttributes.addFlashAttribute("msg", "会员删除成功");
			return "redirect:" + redirectURL;
		}else{
			model.addAttribute("status", STATUS_FAILD);
			model.addAttribute("msg", errorMsg);
			return showMemberDetail(memberEid, model);
		}
	}
	
	/** 更新指定的某个统一管理的会员 */
	@RequestMapping(value="/update/merchant_type", method=RequestMethod.PUT)
	public String updateMerchantMember(@Valid MerchantMember member, RedirectAttributes redirectAttributes) throws Exception {
		return updateMember(member, redirectAttributes);
	}
	
	/** 更新指定的某个非统一管理的会员 */
	@RequestMapping(value="/update/shop_type", method=RequestMethod.PUT)
	public String updateShopMember(@Valid ShopMember member, RedirectAttributes redirectAttributes) throws Exception {
		return updateMember(member, redirectAttributes);
	}
	
	private String updateMember(@Valid Member memberForm, RedirectAttributes redirectAttributes) throws Exception {
		String redirectURL = null;
		if(getBusiness() instanceof Merchant){
			redirectURL = "/member/merchant/list";
		}else if(getBusiness() instanceof Shop){
			redirectURL = "/member/shop/list";
		}
		
		boolean result = false;
		String msg = null;
		try{
			result = memberService.validateAndUpdateMember(memberForm, getBusiness());
		}catch(Exception e){
			result = false;
			msg = e.getMessage();
		}
		
		if(result){
			redirectAttributes.addFlashAttribute("status", STATUS_SUCCESS);
			redirectAttributes.addFlashAttribute("msg", "会员更新成功");
			return "redirect:/member/" + memberForm.getEid();
		}else{
			redirectAttributes.addFlashAttribute("status", STATUS_FAILD);
			redirectAttributes.addFlashAttribute("msg", msg);
			return "redirect:" + redirectURL; //更新失败。 TODO 不明确失败原因，直接跳转回列表页。如果非权限原因，应该跳转回详情页
		}
	}
	
	protected boolean isMerchant() throws Exception{
		if(!(getBusiness() instanceof Merchant)){
			//非集团权限，抛异常
			throw new Exception();
		}
		Merchant merchant = (Merchant)getBusiness();
		if(merchant.getMemberCentralManagement() == null){
			throw new Exception("请先初始化会员管理设置");
		}
		return true;
	}
	
	protected boolean isUncentralManagementShop(Shop shop) throws Exception{
		if(!(getBusiness() instanceof Merchant) //非集团角色
				|| shop == null //shop不存在
				|| shop.getMerchant() == null //普通商户
				|| !shop.getMerchant().getId().equals(getBusiness().getSelfBusinessId()) //非该集团子商户
				|| Boolean.TRUE.equals(shop.getMerchant().getMemberCentralManagement())) { //会员统一管理的集团
			throw new Exception("商户不存在");
		}
		return true;
	}
	
	protected boolean isShop() throws Exception{
		if(!(getBusiness() instanceof Shop)){ //非商户角色，抛异常
			throw new Exception();
		}
		Shop shop = (Shop)getBusiness();
		if(shop.getMerchant() != null && shop.getMerchant().getMemberCentralManagement() == null){
			//子商户所属集团还未设置会员管理
			throw new Exception("请先初始化会员管理设置");
		}
		return true;
	}
	
	/**
	 * 检查会员手机号是否重复
	 */
	@RequestMapping(value="/mobile_validate/{mobile}",method=RequestMethod.GET)
	public HttpEntity<String> checkMobile(@PathVariable String mobile, Model model){
		Business business = getBusiness();
		GrantResult grantResult = null;
		if (business == null) {
			grantResult = new GrantResult(STATUS_FAILD, "操作无效，请登录后再试。");
		}
//		else if (memberService.findMemberByMobile(business,mobile) != null) {
//			model.addAttribute("status", STATUS_FAILD);
//			model.addAttribute("msg", "会员手机号已经存在");
//		}
		JSONResponse jsonResponse = new JSONResponse(grantResult);
		return new ResponseEntity<String>( jsonResponse.getBody(), jsonResponse.getHttpStatus()) ;
	}
	
	/**
	 * 批量导入会员
	 */
	@RequestMapping(value="/import", method = RequestMethod.POST)
	public String batchImportMember(MultipartHttpServletRequest request, Model model) throws Exception{
		Set<String> acceptContentTypes = new HashSet<>();
		acceptContentTypes.add("application/vnd.ms-excel");
		acceptContentTypes.add("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		
		MultipartFile memberList = request.getFile("memberlist");
		if(memberList == null || memberList.isEmpty()){ //上传文件非空校验
			model.addAttribute("success", false);
			model.addAttribute("errorMsg", "未上传商户数据文件");
			return "sms/send";
		}else if(!acceptContentTypes.contains(memberList.getContentType())){ //上传文件格式校验
			logger.debug("传文件["+memberList.getOriginalFilename()+"]的类型不匹配：["+ memberList.getContentType() +"]");
			model.addAttribute("success", false);
			model.addAttribute("errorMsg", "上传的商户数据文件格式错误！");
			return "sms/send";
		}
		
		String result = "";//memberService.batchImport(memberList, getBusiness());
		model.addAttribute("success", true);
		model.addAttribute("result", result);
		return "forward:/sms/send";
	}

}

class MerchantMember extends Member{

	private static final long serialVersionUID = -4444909890715946558L;
	
	private MemberType memberType = new MerchantMemberType();

	public MemberType getMemberType() {
		return memberType;
	}

	public void setMemberType(MemberType memberType) {
		this.memberType = memberType;
	}
	
}

class ShopMember extends Member{

	private static final long serialVersionUID = -4444909890715946558L;
	
	private MemberType memberType = new ShopMemberType();

	public MemberType getMemberType() {
		return memberType;
	}

	public void setMemberType(MemberType memberType) {
		this.memberType = memberType;
	}
	
}

