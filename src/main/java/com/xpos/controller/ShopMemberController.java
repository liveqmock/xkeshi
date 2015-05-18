package com.xpos.controller;

import java.util.List;

import javax.validation.Valid;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.xkeshi.interceptor.form.AvoidDuplicateSubmission;
import com.xpos.common.entity.Merchant;
import com.xpos.common.entity.Picture;
import com.xpos.common.entity.Shop;
import com.xpos.common.entity.face.Business.BusinessType;
import com.xpos.common.entity.member.Member;
import com.xpos.common.entity.member.MemberAttribute;
import com.xpos.common.entity.member.MemberAttributeTemplate;
import com.xpos.common.entity.member.MemberType;
import com.xpos.common.entity.member.MerchantMemberType;
import com.xpos.common.entity.member.ShopMemberType;
import com.xpos.common.exception.MemberException;
import com.xpos.common.searcher.member.MemberAttributeSearcher;
import com.xpos.common.searcher.member.MemberTypeSearcher;
import com.xpos.common.searcher.member.ShopMemberSearcher;
import com.xpos.common.service.PictureService;
import com.xpos.common.service.ShopService;
import com.xpos.common.service.member.MemberAttributeService;
import com.xpos.common.service.member.MemberAttributeTemplateService;
import com.xpos.common.service.member.MemberService;
import com.xpos.common.service.member.MemberTypeService;
import com.xpos.common.utils.Pager;

/**
 * 商户角色对会员模块的操作
 */
@Controller
@RequestMapping("member/shop")
public class ShopMemberController extends MemberController{

	@Autowired
	private MemberService memberService;
	
	@Autowired
	private MemberTypeService memberTypeService;
	
	@Autowired
	private MemberAttributeTemplateService memberAttributeTemplateService;
	
	@Autowired
	private MemberAttributeService memberAttributeService;
	
	@Autowired
	private ShopService shopService;
	
	@Autowired
	private PictureService pictureService;
	
	/**
	 * 商户查看会员列表
	 */
	@RequestMapping(value="/list", method=RequestMethod.GET)
	public String showMemberList(Pager<Member> pager, ShopMemberSearcher searcher, Model model) throws Exception{
		isShop();
		
		Shop shop = (Shop)getBusiness();
		List<? extends MemberType> memberTypes = null;
		if(shop.getMerchant() == null 
				|| Boolean.FALSE.equals(shop.getMerchant().getMemberCentralManagement())){
			//普通商户、会员非统一管理子商户
			memberTypes = memberTypeService.findShopMemberTypeListWithAttributeTemplateByShopId(shop.getId());
		}else if(Boolean.TRUE.equals(shop.getMerchant().getMemberCentralManagement())){
			Merchant merchant = shop.getMerchant();
			memberTypes = memberTypeService.findMerchantMemberTypeListWithAttributeTemplateByMerchantId(merchant.getId());
		}else{
			throw new Exception("请用集团账号登陆并初始化会员管理设置");
		}
		memberService.findMembersByBusiness(shop, pager, searcher);
		searcher.setShop(null);
		model.addAttribute("pager", pager);
		model.addAttribute("searcher", searcher);
		model.addAttribute("memberTypes", memberTypes);
		return "member/shop_member_list";
	}
	
	
	/**
	 * 跳转进入添加会员页面
	 */
	@AvoidDuplicateSubmission(addToken=true)
	@RequestMapping(value="/add", method=RequestMethod.GET)
	public String showAddMerchantMember(Model model , RedirectAttributes  redirectAttributes) throws Exception{
		isShop();
		Shop shop = (Shop)getBusiness();
		List<? extends MemberType> memberTypes = null;
		if(shop.getMerchant() == null 
				|| Boolean.FALSE.equals(shop.getMerchant().getMemberCentralManagement())){
			//普通商户、会员非统一管理子商户
			memberTypes = memberTypeService.findShopMemberTypeListWithAttributeTemplateByShopId(shop.getId());
			model.addAttribute("type", "SHOP");
		}else if(Boolean.TRUE.equals(shop.getMerchant().getMemberCentralManagement())){
			Merchant merchant = shop.getMerchant();
			memberTypes = memberTypeService.findMerchantMemberTypeListWithAttributeTemplateByMerchantId(merchant.getId());
			model.addAttribute("type", "MERCHANT");
		}else{
			throw new Exception("请用集团账号登陆并初始化会员管理设置");
		}
		if (CollectionUtils.isEmpty(memberTypes)) {
			redirectAttributes.addFlashAttribute( "msg","请先设置会员类型");
			redirectAttributes.addFlashAttribute( "status",FAILD);
			return "redirect:/member/shop/list";
		}
		model.addAttribute("memberTypes", memberTypes);
		return "member/shop_member_input";
	}
	
	/** 
	 * 保存统一管理的会员
	 */
	@AvoidDuplicateSubmission(removeToken=true,errorRedirectURL="/member/shop/list")
	@RequestMapping(value="/addByMerchant", method=RequestMethod.POST)
	public String processAddMerchantMember(MerchantMember member, RedirectAttributes redirectAttributes) throws Exception{
		isShop();
		
		Shop shop = (Shop)getBusiness();
		
		if(!Boolean.TRUE.equals(shop.getMerchant().getMemberCentralManagement())){
			throw new Exception("无法新建集团统一管理类型的会员");
		}
		member.setBusiness(shop.getMerchant());
		member.setShop(shop);
		redirectAttributes.addFlashAttribute("status", STATUS_FAILD);
		redirectAttributes.addFlashAttribute("msg", "创建会员失败");
		try {
			if(memberService.validateAndSave(member, getBusiness())){
				redirectAttributes.addFlashAttribute("status", STATUS_SUCCESS);
				redirectAttributes.addFlashAttribute("msg", "创建会员成功");
			}
		} catch (MemberException e) {
			redirectAttributes.addFlashAttribute("msg",e.getMessage());
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("msg","创建会员失败");
		}
		return  "redirect:/member/shop/list";
	}
	
	/** 
	 * 保存非统一管理的会员 
	 */
	@AvoidDuplicateSubmission(removeToken=true,errorRedirectURL="/member/shop/list")
	@RequestMapping(value="/addByShop", method=RequestMethod.POST)
	public String processAddShopMember(ShopMember member, RedirectAttributes redirectAttributes) throws Exception{
		isShop(); 
		Shop shop = (Shop)getBusiness();
		
		redirectAttributes.addFlashAttribute("status", STATUS_FAILD);
		if(shop.getMerchant() != null && Boolean.TRUE.equals(shop.getMerchant().getMemberCentralManagement())){
			redirectAttributes.addFlashAttribute("msg", "无法创建商户独立维护的会员类型");
		}   
		member.setBusiness(shop);
		redirectAttributes.addFlashAttribute("msg", "创建会员失败");
		try {
			if(memberService.validateAndSave(member, getBusiness())){
				redirectAttributes.addFlashAttribute("status", STATUS_SUCCESS);
				redirectAttributes.addFlashAttribute("msg", "创建会员成功");
			}
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("msg",e.getMessage());
		}
		return  "redirect:/member/shop/list";
	}

	/**
	 *  商户查看当前登陆商户的模板所有扩展属性 
	 **/
	@AvoidDuplicateSubmission(addToken=true)
	@RequestMapping(value="/template/list", method=RequestMethod.GET)
	public String showMemberAttributeTemplate(@RequestParam(required = false ,value="nickName") String nickName,
			Pager<MemberAttribute> pager, MemberAttributeSearcher searcher, Model model) throws Exception{
		isShop();
		Shop shop = (Shop)getBusiness();
		List<MemberAttributeTemplate> templateList = null;
		if(shop.getMerchant()!= null && shop.getMerchant().getMemberCentralManagement()) {//子商户所属集团统一管理会员，子商户无权修改
			model.addAttribute("editable", false);
			templateList = memberAttributeTemplateService.findByBusiness(shop.getMerchant().getId(),BusinessType.MERCHANT);
		}else {
			model.addAttribute("editable", true); 
			templateList = memberAttributeTemplateService.findByBusiness(getBusiness().getSelfBusinessId(), BusinessType.SHOP);
		}
		MemberAttributeTemplate template = null;
		//通过登陆的集团信息找到对应的唯一的会员模板，再通过模板找到会员属性列表
		if(CollectionUtils.isNotEmpty(templateList)){
			template = templateList.get(0); 
			searcher.setMemberAttributeTemplateId(template.getId());
			pager = memberAttributeService.searchAttributeByTemplate(pager, searcher);
			model.addAttribute("attirbuteList",  pager.getList());
		}else {
			model.addAttribute("status", STATUS_FAILD);
			model.addAttribute("msg", "商户会员模板缺失,请联系管理员");
			return "member/shop_member_template";
		}
		model.addAttribute("pager", pager);
		model.addAttribute("allAttributeType", MemberAttribute.AttributeType.values());
		model.addAttribute("template", template);
		model.addAttribute("searcher", searcher);
		return "member/shop_member_template";
	}
	
	
	
	/**
	 * 子商户或普通商户添加模板属性
	 */
	@AvoidDuplicateSubmission(removeToken=true,errorRedirectURL="/member/shop/template/list")
	@RequestMapping(value="attribute/save", method=RequestMethod.POST)
	public String addMemberAttribute(@Valid MemberAttribute attribute, 
			BindingResult result ,Model model,RedirectAttributes redirectAttributes) throws Exception{
		isShop();
		boolean success = true;
		String msg = "会员属性添加失败";
		if(result.hasErrors()) {
			 success = false;
			 msg = "参数校验不通过";
		}
		//判断数据库中是否有重复的名称
		if(memberAttributeService.distinct(attribute)) {
			msg = "该会员模板已存在";
			redirectAttributes.addFlashAttribute("status", STATUS_FAILD);
			redirectAttributes.addFlashAttribute("msg", msg);
			return "redirect:/member/shop/template/list";
		}
		//json数组格式
		if(StringUtils.isNotBlank(attribute.getOptionalValues()))
			attribute.setOptionalValues("[" + attribute.getOptionalValues() + "]");
		success = success && memberAttributeService.save(attribute, getBusiness());
		if(success) {
			redirectAttributes.addFlashAttribute("status", STATUS_SUCCESS);
			redirectAttributes.addFlashAttribute("msg", "会员属性添加成功");
		}else{
			redirectAttributes.addFlashAttribute("status", STATUS_FAILD);
			redirectAttributes.addFlashAttribute("msg", msg);
		}
		return "redirect:/member/shop/template/list";
	}

	/**
	 * 集团或集团子商户修改模板属性
	 */
	@RequestMapping(value="attribute/update" ,method = RequestMethod.PUT )
	public String  updateMemberAttribute(@Valid  MemberAttribute attribute  , 
			BindingResult result ,Model model ,RedirectAttributes redirectAttributes) throws Exception{
		isShop();
		boolean success = true;
		String msg = "会员属性修改失败";
		if(result.hasErrors()) {
			 success = false;
			 msg = "参数校验不通过";
		}
		//json数组格式
		if(StringUtils.isNotBlank(attribute.getOptionalValues()))
			attribute.setOptionalValues("[" + attribute.getOptionalValues() + "]");
		success = success && memberAttributeService.update(attribute, getBusiness());
		if(success) {
			redirectAttributes.addFlashAttribute("status", STATUS_SUCCESS);
			redirectAttributes.addFlashAttribute("msg", "会员属性修改成功");
		}else{
			redirectAttributes.addFlashAttribute("status", STATUS_FAILD);
			redirectAttributes.addFlashAttribute("msg", msg);
		}
		return "redirect:/member/shop/template/list";
	}

	/**
	 * 删除模板属性
	 * @throws Exception 
	 */
	@RequestMapping(value="attribute/delete/{id}", method = RequestMethod.DELETE)
	public String deleteMemberAttribute(@PathVariable Long id  , Model model , RedirectAttributes redirectAttributes) throws Exception{
		isShop();
		MemberAttribute attribute = memberAttributeService.findAttributeById(id);
		if (memberAttributeService.delete(attribute, getBusiness())) {
			redirectAttributes.addFlashAttribute("status", STATUS_SUCCESS);
			redirectAttributes.addFlashAttribute("msg", "删除成功");
		}else{
			redirectAttributes.addFlashAttribute("status", STATUS_FAILD);
			redirectAttributes.addFlashAttribute("msg", "删除失败");
		}
		return "redirect:/member/shop/template/list";
	}
	
	/**
	 * 商户查看会员类型列表
	 */
	@AvoidDuplicateSubmission(addToken=true)
	@RequestMapping(value="type/list", method=RequestMethod.GET)
	public String showMemberTypeList(Pager<MemberType> pager, MemberTypeSearcher searcher, Model model) throws Exception{
		isShop();
		
		Shop shop = (Shop)getBusiness();
		List<? extends MemberType> memberTypes = null;
		if(shop.getMerchant() == null 
				|| Boolean.FALSE.equals(shop.getMerchant().getMemberCentralManagement())){
			//普通商户、会员非统一管理子商户
			Pager<ShopMemberType> shopPager = new Pager<>();
			shopPager.setP(pager.getP());
			shopPager.setPageSize(pager.getPageSize());
			memberTypes = memberTypeService.searchShopMemberTypeListByShop(shopPager, searcher, shop).getList();
			List<MemberAttributeTemplate> templateList =
					memberAttributeTemplateService.findByBusiness(shop.getId(), BusinessType.SHOP);
			MemberAttributeTemplate publicTemplate = CollectionUtils.isNotEmpty(templateList) ? templateList.get(0) : null;
			model.addAttribute("editable", true);
			model.addAttribute("pager", shopPager);
			model.addAttribute("template", publicTemplate);
		}else if(Boolean.TRUE.equals(shop.getMerchant().getMemberCentralManagement())){
			Merchant merchant = shop.getMerchant();
			Pager<MerchantMemberType> merchantPager = new Pager<>();
			merchantPager.setP(pager.getP());
			merchantPager.setPageSize(pager.getPageSize());
			memberTypes = memberTypeService.searchMerchantMemberTypeListByMerchant(merchantPager, searcher, merchant).getList();
			model.addAttribute("pager", merchantPager);
			model.addAttribute("editable", false); //统一维护的会员类型，子商户只能查看
		}else{
			throw new Exception("请用集团账号登陆并初始化会员管理设置");
		}
		model.addAttribute("searcher", searcher);
		model.addAttribute("memberTypes", memberTypes);
		return "member/shop_member_type_list";
	}
	
	
	/** 保存会员类型 */
	@AvoidDuplicateSubmission(removeToken=true,errorRedirectURL="/member/shop/type/list")
	@RequestMapping(value="type/add", method=RequestMethod.POST)
	public String processAddMerchantMemberType(ShopMemberType memberType, MultipartFile coverPictureFile, RedirectAttributes redirectAttributes) throws Exception{
		isShop();
		Shop shop = (Shop)getBusiness();
		
		memberType.setShop(shop);
		if(coverPictureFile !=null && !coverPictureFile.isEmpty()){
			Picture coverPicture = pictureService.getPictureFromMultipartFile(coverPictureFile);
			memberType.setCoverPicture(coverPicture);
		}
		//会员类型名称是否重复
		if(memberTypeService.distinct(memberType)){
			redirectAttributes.addFlashAttribute("status", STATUS_FAILD);
			redirectAttributes.addFlashAttribute("msg", "该会员类型已存在");
			return "redirect:/member/shop/type/list";
		}
		if(memberTypeService.save(memberType, shop)){
			redirectAttributes.addFlashAttribute("status", STATUS_SUCCESS);
			redirectAttributes.addFlashAttribute("msg", "创建会员类型成功");
		}else{
			redirectAttributes.addFlashAttribute("status", STATUS_FAILD);
			redirectAttributes.addFlashAttribute("msg", "创建会员类型失败");
		}
		return  "redirect:/member/shop/type/list";
	}
	
	/** 更新会员类型 */
	@RequestMapping(value="type/update", method=RequestMethod.POST)
	public String processUpdateShopMemberType(ShopMemberType memberType,
				MultipartFile coverPictureFile, RedirectAttributes redirectAttributes) throws Exception{
		isShop();
		Shop shop = (Shop)getBusiness();
		
		ShopMemberType memberTypeDB = memberTypeService.findShopMemberTypeById(memberType.getId());
		if(memberTypeDB == null){
			redirectAttributes.addFlashAttribute("status", STATUS_FAILD);
			redirectAttributes.addFlashAttribute("msg", "编辑会员类型失败");
			return "redirect:/member/type/list";
		}else{
			memberType.setShop(memberTypeDB.getShop());
		}
		
		if(coverPictureFile !=null && !coverPictureFile.isEmpty()){
			Picture coverPicture = pictureService.getPictureFromMultipartFile(coverPictureFile);
			memberType.setCoverPicture(coverPicture);
		}
		
		if(memberTypeService.update(memberType, shop)){
			redirectAttributes.addFlashAttribute("status", STATUS_SUCCESS);
			redirectAttributes.addFlashAttribute("msg", "编辑会员类型成功");
		}else{
			redirectAttributes.addFlashAttribute("status", STATUS_FAILD);
			redirectAttributes.addFlashAttribute("msg", "编辑会员类型失败");
		}
		return  "redirect:/member/shop/type/list";
	}
	
	/** 删除会员类型 */
	@RequestMapping(value="type/delete/{id}", method=RequestMethod.POST)
	public String deleteMerchantMemberType(@PathVariable("id")long id, RedirectAttributes redirectAttributes) throws Exception{
		isShop();
		Shop shop = (Shop)getBusiness();
		
		ShopMemberType shopMemberType = new ShopMemberType();
		shopMemberType.setId(id);
		
		if(memberTypeService.delete(shopMemberType, shop)){
			redirectAttributes.addFlashAttribute("status", STATUS_SUCCESS);
			redirectAttributes.addFlashAttribute("msg", "会员类型删除成功");
		}else{
			redirectAttributes.addFlashAttribute("status", STATUS_FAILD);
			redirectAttributes.addFlashAttribute("msg", "会员类型删除失败");
		}
		return "redirect:/member/shop/type/list";
	}
}

