package com.xpos.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.xpos.common.searcher.member.MemberAttributeSearcher;
import com.xpos.common.searcher.member.MemberSearcher;
import com.xpos.common.searcher.member.MemberTypeSearcher;
import com.xpos.common.searcher.member.MerchantMemberSearcher;
import com.xpos.common.searcher.member.ShopMemberSearcher;
import com.xpos.common.service.PictureService;
import com.xpos.common.service.ShopService;
import com.xpos.common.service.member.MemberAttributeService;
import com.xpos.common.service.member.MemberAttributeTemplateService;
import com.xpos.common.service.member.MemberService;
import com.xpos.common.service.member.MemberTypeService;
import com.xpos.common.utils.IDUtil;
import com.xpos.common.utils.Pager;

/**
 * 集团角色对会员模块的操作
 */
@Controller
@RequestMapping("member/merchant")
public class MerchantMemberController extends MemberController{

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
	 * 会员列表
	 */
	@RequestMapping(value="/list", method=RequestMethod.GET)
	public String showMemberList(String nickName, Pager<Member> pager, MerchantMemberSearcher searcher, Model model) throws Exception{
		isMerchant();
		Merchant merchant = (Merchant)getBusiness();
		List<Shop> fullShops = shopService.findShopListByMerchantId(merchant.getId(), true);
		if(Boolean.TRUE.equals(merchant.getMemberCentralManagement())){
			memberService.findMembersByBusiness(merchant, pager, searcher);
			List<MerchantMemberType> memberTypes = memberTypeService.findMerchantMemberTypeListWithAttributeTemplateByMerchantId(merchant.getId());
			model.addAttribute("pager", pager);
			model.addAttribute("searcher", searcher);
			model.addAttribute("memberTypes", memberTypes);
			model.addAttribute("fullShops", fullShops);
			return "member/merchant_member_list";
		}else if(Boolean.FALSE.equals(merchant.getMemberCentralManagement())){
			List<Shop> subShops = new ArrayList<>();
			Map<String, Integer>  shopMemberCountMap  =  new HashMap<>();
			if(CollectionUtils.isNotEmpty(fullShops)){
				for(Shop shop : fullShops){
					if(StringUtils.isNotBlank(nickName) && shop.getName().indexOf(nickName) < 0){
						continue;
					}
					Pager<Member> countPager = new Pager<>();
					MemberSearcher countSearcher = new MemberSearcher();
					int count = memberService.findMembersByBusiness(shop, countPager, countSearcher).getTotalCount();
					subShops.add(shop);
					shopMemberCountMap.put(""+shop.getId(), count);
				}
			}
			model.addAttribute("shopMemberCountMap", shopMemberCountMap);
			model.addAttribute("subShops", subShops);
			model.addAttribute("nickName", nickName);
			return "member/merchant_shops_member_list";
		}
		return null;
	}
	
	/**
	 * 跳转进入添加会员页面
	 */
	@AvoidDuplicateSubmission(addToken=true)
	@RequestMapping(value="/add", method=RequestMethod.GET)
	public String showAddMerchantMember(Model model , RedirectAttributes  redirectAttributes) throws Exception{
		isMerchant();
		Merchant merchant = (Merchant)getBusiness();
		List<MerchantMemberType> memberTypes = memberTypeService.findMerchantMemberTypeListWithAttributeTemplateByMerchantId(merchant.getId());
		if(CollectionUtils.isEmpty(memberTypes)){
			redirectAttributes.addFlashAttribute( "msg","请先设置会员类型");
			redirectAttributes.addFlashAttribute( "status",FAILD);
			return "redirect:/member/merchant/list";
		}
		model.addAttribute("memberTypes", memberTypes);
		
		return "member/merchant_member_input";
	}
	
	/** 
	 * 保存会员
	 */
	@AvoidDuplicateSubmission(removeToken=true, errorRedirectURL="/member/merchant/list")
	@RequestMapping(value="/add", method=RequestMethod.POST)
	public String processAddMerchantMember(MerchantMember member, RedirectAttributes redirectAttributes) throws Exception{
		isMerchant();
		Merchant merchant = (Merchant)getBusiness();
		member.setBusiness(merchant);
		redirectAttributes.addFlashAttribute("status", STATUS_FAILD);
		redirectAttributes.addFlashAttribute("msg", "创建会员失败");
		try {
			if(memberService.validateAndSave(member, getBusiness())){
				redirectAttributes.addFlashAttribute("status", STATUS_SUCCESS);
				redirectAttributes.addFlashAttribute("msg", "创建会员成功");
			}
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("msg",e.getMessage());
		}
		return  "redirect:/member/merchant/list";
	}

	/**
	 * 非会员统一的集团查看某个子商户的会员列表
	 */
	@RequestMapping(value="{shopEid}/list", method=RequestMethod.GET)
	public String showSubShopMemberList(Pager<Member> pager, ShopMemberSearcher searcher, @PathVariable("shopEid") String shopEid, Model model) throws Exception{
		Long shopId = IDUtil.decode(shopEid);
		Shop shop = shopService.findShopByIdIgnoreVisible(shopId);
		isUncentralManagementShop(shop);
		
		List<ShopMemberType> memberTypes = memberTypeService.findShopMemberTypeListWithAttributeTemplateByShopId(shop.getId());
		memberService.findMembersByBusiness(shop, pager, searcher);
		model.addAttribute("pager", pager);
		model.addAttribute("searcher", searcher);
		model.addAttribute("memberTypes", memberTypes);
		model.addAttribute("shop", shop);
		return "member/merchant_subshop_member_list";
	}
	
	/**
	 * 非会员统一的集团跳转进入某个子商户的添加会员页面
	 */
	@AvoidDuplicateSubmission(addToken=true)
	@RequestMapping(value="{shopEid}/add", method=RequestMethod.GET)
	public String showSubShopAddShopMember(@PathVariable("shopEid")String shopEid, Model model , RedirectAttributes  redirectAttributes) throws Exception{
		Long shopId = IDUtil.decode(shopEid);
		Shop shop = shopService.findShopByIdIgnoreVisible(shopId);
		isUncentralManagementShop(shop);
		
		List<ShopMemberType> memberTypes = memberTypeService.findShopMemberTypeListWithAttributeTemplateByShopId(shop.getId());
		if(CollectionUtils.isEmpty(memberTypes)){
			redirectAttributes.addFlashAttribute( "msg","请先设置会员类型");
			redirectAttributes.addFlashAttribute( "status",FAILD);
			return "redirect:/member/merchant/"+shopEid+"/list";
		}
		model.addAttribute("memberTypes", memberTypes);
		model.addAttribute("shop", shop);
		return "member/merchant_subshop_member_input";
	}
	
	/** 
	 * 非会员统一的集团为某个子商户添加会员
	 */
	@AvoidDuplicateSubmission(removeToken=true,errorRedirectURL="/member/merchant/{shopEid}/list")
	@RequestMapping(value="{shopEid}/add", method=RequestMethod.POST)
	public String processAddShopMember(@PathVariable("shopEid")String shopEid, ShopMember member, RedirectAttributes redirectAttributes) throws Exception{
		Long shopId = IDUtil.decode(shopEid);
		Shop shop = shopService.findShopByIdIgnoreVisible(shopId);
		isUncentralManagementShop(shop);
		member.setBusiness(shop);
		redirectAttributes.addFlashAttribute("status", STATUS_FAILD);
		redirectAttributes.addFlashAttribute("msg", "创建会员失败");
		try {
			if(memberService.validateAndSave(member, shop)){
				redirectAttributes.addFlashAttribute("status", STATUS_SUCCESS);
				redirectAttributes.addFlashAttribute("msg", "创建会员成功");
			}
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("msg",e.getMessage());
		}
		return  "redirect:/member/merchant/" + shopEid + "/list";
	}
	
	/**
	 *  集团查看当前登陆集团的模板所有扩展属性 
	 **/
	@AvoidDuplicateSubmission(addToken=true)
	@RequestMapping(value="/template/list", method=RequestMethod.GET)
	public String showMemberAttributeTemplate(@RequestParam(required = false ,value="nickName") String nickName,Pager<MemberAttribute> pager, MemberAttributeSearcher searcher, Model model) throws Exception{
		isMerchant();
		Merchant merchant = (Merchant)getBusiness();
		List<Shop> fullShops = shopService.findShopListByMerchantId(merchant.getId(), true);
		if(merchant.getMemberCentralManagement()) {//集团会员统一管理
			MemberAttributeTemplate template = null;
			//通过登陆的集团信息找到对应的唯一的会员模板，再通过模板找到会员属性列表 
			List<MemberAttributeTemplate> templateList =
					memberAttributeTemplateService.findByBusiness(getBusiness().getSelfBusinessId(), getBusiness().getSelfBusinessType());
			if(CollectionUtils.isNotEmpty(templateList)){//校验集团会员模板
				template = templateList.get(0); 
				searcher.setMemberAttributeTemplateId(template.getId());
				pager = memberAttributeService.searchAttributeByTemplate(pager, searcher);
				model.addAttribute("attirbuteList",  pager.getList());
			}else {
				model.addAttribute("status", STATUS_FAILD);
				model.addAttribute("msg", "集团会员模板缺失");
				return "member/merchant_member_template";
			}
			model.addAttribute("pager", pager);
			model.addAttribute("allAttributeType", MemberAttribute.AttributeType.values());
			model.addAttribute("template", template);
			model.addAttribute("searcher", searcher);
			return "member/merchant_member_template";
		}else if(!merchant.getMemberCentralManagement()) {//集团会员非统一管理
			List<Shop> subShops = new ArrayList<>();
			Map<String, Integer>  shopMemberAttributeCountMap  =  new HashMap<>();
			if(CollectionUtils.isNotEmpty(fullShops)){
				for(Shop shop : fullShops){//循环所有该集团下的商户
					if(StringUtils.isNotBlank(nickName) && shop.getName().indexOf(nickName) < 0){
						continue;
					}
					List<MemberAttributeTemplate> templateList = memberAttributeTemplateService.findByBusiness(shop.getId(), BusinessType.SHOP);
					int count = 0 ;
					if(CollectionUtils.isNotEmpty(templateList)){
						count = memberAttributeService.findAttributeListByTemplateIgnoreEnabled(templateList.get(0).getId()).size();
					}
					subShops.add(shop);
					shopMemberAttributeCountMap.put(""+shop.getId(), count);
				}
			}
			model.addAttribute("shopMemberAttributeCountMap", shopMemberAttributeCountMap);
			model.addAttribute("subShops", subShops);
			model.addAttribute("nickName", nickName);
			return "member/merchant_shops_member_template_list";
		}
		return null;
	}
	
	/**
	 * 非会员统一的集团查看某个子商户的模版属性
	 */
	@AvoidDuplicateSubmission(addToken=true)
	@RequestMapping(value="{shopEid}/template/list", method=RequestMethod.GET)
	public String showSubShopMemberAttributeTemplate(
			Pager<MemberAttribute> pager, MemberAttributeSearcher searcher, @PathVariable("shopEid") String shopEid, Model model) throws Exception{
		isMerchant();
		Long shopId = IDUtil.decode(shopEid);
		Shop shop = shopService.findShopByIdIgnoreVisible(shopId);
		if(!(getBusiness() instanceof Merchant) //非集团角色
				|| shop == null //shop不存在
				|| shop.getMerchant() == null //普通商户
				|| !shop.getMerchant().getId().equals(getBusiness().getSelfBusinessId()) //非该集团子商户
				|| Boolean.TRUE.equals(shop.getMerchant().getMemberCentralManagement())) { //会员统一管理的集团
			throw new Exception("商户不存在");
		}
		MemberAttributeTemplate template = null;
		//通过登陆的集团信息找到对应的唯一的会员模板，再通过模板找到会员属性列表
		List<MemberAttributeTemplate> templateList =
				memberAttributeTemplateService.findByBusiness(shop.getId(),BusinessType.SHOP);
		if(CollectionUtils.isNotEmpty(templateList)){
			template = templateList.get(0); 
			searcher.setMemberAttributeTemplateId(template.getId());
			pager = memberAttributeService.searchAttributeByTemplate(pager, searcher);
			model.addAttribute("attirbuteList",  pager.getList());
		}
		model.addAttribute("pager", pager);
		model.addAttribute("allAttributeType", MemberAttribute.AttributeType.values());
		model.addAttribute("template", template);
		model.addAttribute("searcher", searcher);
		model.addAttribute("shopEid",shopEid);
		return "member/merchant_member_template";
	}
	
	/**
	 *会员统一的集团添加会员模版 
	 */
	@AvoidDuplicateSubmission(removeToken=true,errorRedirectURL="/member/merchant/template/list")
	@RequestMapping(value="/attribute/save", method=RequestMethod.POST)
	public String addMemberAttribute(@Valid MemberAttribute attribute, 
			BindingResult result, RedirectAttributes redirectAttributes) throws Exception{
		isMerchant();
		boolean success = true;
		String msg = "会员属性添加失败";
		if(result.hasErrors()) {
			 success = false;
			 msg = "参数校验不通过";
		}
		//判断会员属性的名称是否有重复的
		if(memberAttributeService.distinct(attribute)) {
			msg = "该会员模板已存在";
			redirectAttributes.addFlashAttribute("status", STATUS_FAILD);
			redirectAttributes.addFlashAttribute("msg", msg);
			return "redirect:/member/merchant/template/list";
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
		return "redirect:/member/merchant/template/list";
	}
	
	/**
	 *非会员统一的集团为某个子商户添加会员模版 
	 */
	@AvoidDuplicateSubmission(removeToken=true,errorRedirectURL="/member/merchant/{shopEid}/template/list")
	@RequestMapping(value="/{shopEid}/attribute/save", method=RequestMethod.POST,params="shopEid")
	public String addMemberAttribute(@PathVariable("shopEid") String shopEid,@Valid MemberAttribute attribute, 
			BindingResult result, RedirectAttributes redirectAttributes) throws Exception{
		isMerchant();
		boolean success = true;
		String msg = "会员属性添加失败"; 
		if(result.hasErrors()) {
			 success = false;
			 msg = "参数校验不通过";
		}
		//json数组格式
		if(StringUtils.isNotBlank(attribute.getOptionalValues()))
			attribute.setOptionalValues("[" + attribute.getOptionalValues() + "]");
		//1校验权限(根据shopEid判断是否是子商户添加)
			Long shopId = IDUtil.decode(shopEid);
			Shop shop = shopService.findShopByIdIgnoreVisible(shopId);
			isUncentralManagementShop(shop);
			success = success && memberAttributeService.save(attribute, shop);
		if(success) {
			redirectAttributes.addFlashAttribute("status", STATUS_SUCCESS);
			redirectAttributes.addFlashAttribute("msg", "会员属性添加成功");
		}else{
			redirectAttributes.addFlashAttribute("status", STATUS_FAILD);
			redirectAttributes.addFlashAttribute("msg", msg);
		}
		return "redirect:/member/merchant/"+shopEid+"/template/list";
	}
	

	/**
	 * 集团或集团子商户修改模板属性
	 */
	@RequestMapping(value="attribute/update" ,method = RequestMethod.PUT )
	public String  updateMemberAttribute(@RequestParam(required = false ,value="shopEid") String shopEid,  @Valid  MemberAttribute  attribute  , 
			BindingResult  result ,Model model ,RedirectAttributes redirectAttributes) throws Exception{
		isMerchant();
		boolean success = true;
		String msg = "会员属性修改失败";
		if(result.hasErrors()) {
			 success = false;
			 msg = "参数校验不通过";
		}
		//json数组格式
		if(StringUtils.isNotBlank(attribute.getOptionalValues()))
			attribute.setOptionalValues("[" + attribute.getOptionalValues() + "]");
		//1校验权限(根据shopEid判断是否是子商户添加)
		//判断一下，如果是text的方式的，就把选项之清空，因为会引起json解析的异常
		if(attribute.getAttributeType().name().equals("text")) {
			attribute.setOptionalValues(null);
		}
		if(StringUtils.isNotBlank(shopEid)) {
			Long shopId = IDUtil.decode(shopEid);
			Shop shop = shopService.findShopByIdIgnoreVisible(shopId);
			isUncentralManagementShop(shop);
			success = success && memberAttributeService.update(attribute, shop);
		}else {
			success = success && memberAttributeService.update(attribute, getBusiness());
		}
		if(success) {
			redirectAttributes.addFlashAttribute("status", STATUS_SUCCESS);
			redirectAttributes.addFlashAttribute("msg", "会员属性修改成功");
		}else{
			redirectAttributes.addFlashAttribute("status", STATUS_FAILD);
			redirectAttributes.addFlashAttribute("msg", msg);
		}
		if(StringUtils.isNotBlank(shopEid)) {
			return "redirect:/member/merchant/"+shopEid+"/template/list";
		}
		return "redirect:/member/merchant/template/list";
	}

	/**
	 * 删除模板属性
	 * @throws Exception 
	 */
	@RequestMapping(value="attribute/delete/{id}", method = RequestMethod.DELETE)
	public String deleteMemberAttribute(@RequestParam(required = false ,value="shopEid") String shopEid, 
			@PathVariable Long id  , Model model , RedirectAttributes redirectAttributes) throws Exception{
		isMerchant();
		MemberAttribute attribute = memberAttributeService.findAttributeById(id);
		if (memberAttributeService.delete(attribute, getBusiness())) {
			redirectAttributes.addFlashAttribute("status", STATUS_SUCCESS);
			redirectAttributes.addFlashAttribute("msg", "删除成功");
		}else{
			redirectAttributes.addFlashAttribute("status", STATUS_FAILD);
			redirectAttributes.addFlashAttribute("msg", "删除失败");
		}
		if(StringUtils.isNotBlank(shopEid)) {
			return "redirect:/member/merchant/"+shopEid+"/template/list";
		}
		return "redirect:/member/merchant/template/list";
	}
	
	/** 会员类型列表 */
	@AvoidDuplicateSubmission(addToken=true)
	@RequestMapping(value="type/list")
	public String showMemberTypeList(String nickName, Pager<MemberType> pager, MemberTypeSearcher searcher, Model model) throws Exception{
		isMerchant();
		Merchant merchant = (Merchant)getBusiness();
		if(Boolean.TRUE.equals(merchant.getMemberCentralManagement())){
			Pager<MerchantMemberType> merchantPager = new Pager<>();
			merchantPager.setP(pager.getP());
			merchantPager.setPageSize(pager.getPageSize());
			memberTypeService.searchMerchantMemberTypeListByMerchant(merchantPager, searcher, merchant);
			List<MemberAttributeTemplate> templateList =
					memberAttributeTemplateService.findByBusiness(merchant.getId(), BusinessType.MERCHANT);
			MemberAttributeTemplate publicTemplate = CollectionUtils.isNotEmpty(templateList) ? templateList.get(0) : null;
			model.addAttribute("pager", merchantPager);
			model.addAttribute("searcher", searcher);
			model.addAttribute("template", publicTemplate);
			return "member/merchant_member_type_list";
		}else if(Boolean.FALSE.equals(merchant.getMemberCentralManagement())){
			List<Shop> fullShops = shopService.findShopListByMerchantId(merchant.getId(), true);
			List<Shop> subShops = new ArrayList<>();
			Map<String, Integer>  shopMemberCountMap  =  new HashMap<>();
			if(CollectionUtils.isNotEmpty(fullShops)){
				for(Shop shop : fullShops){
					if(StringUtils.isNotBlank(nickName) && shop.getName().indexOf(nickName) < 0){
						continue;
					}
					int count = memberTypeService.findShopMemberTypeListByShop(shop).size();
					subShops.add(shop);
					shopMemberCountMap.put(""+shop.getId(), count);
				}
			}
			model.addAttribute("shopMemberCountMap", shopMemberCountMap);
			model.addAttribute("subShops", subShops);
			model.addAttribute("nickName", nickName);
			return "member/merchant_shops_member_type_list";
		}
		return null;
	}
	
	/**
	 * 非会员统一的集团查看某个子商户的会员类型列表
	 */
	@AvoidDuplicateSubmission(addToken=true)
	@RequestMapping(value="{shopEid}/type/list", method=RequestMethod.GET)
	public String showSubShopMemberTypeList(Pager<ShopMemberType> pager, MemberTypeSearcher searcher, @PathVariable("shopEid") String shopEid, Model model, RedirectAttributes  redirectAttributes) throws Exception{
		Long shopId = IDUtil.decode(shopEid);
		Shop shop = shopService.findShopByIdIgnoreVisible(shopId);
		isUncentralManagementShop(shop);
		
		memberTypeService.searchShopMemberTypeListByShop(pager, searcher, shop);
		List<MemberAttributeTemplate> templateList = memberAttributeTemplateService.findByBusiness(shop.getId(), BusinessType.SHOP);
		if (CollectionUtils.isEmpty(templateList)) {
			redirectAttributes.addFlashAttribute("status", STATUS_FAILD);
			redirectAttributes.addFlashAttribute("msg", "会员属性模版未创建");
			return "redirect:/member/merchant/type/list";
		}
		MemberAttributeTemplate publicTemplate = templateList.get(0);
		model.addAttribute("pager", pager);
		model.addAttribute("searcher", searcher);
		model.addAttribute("shop", shop);
		model.addAttribute("template", publicTemplate);
		return "member/merchant_subshop_member_type_list";
	}
	
	/** 保存会员 */
	@AvoidDuplicateSubmission(removeToken=true,errorRedirectURL="/member/merchant/type/list")
	@RequestMapping(value="type/add", method=RequestMethod.POST)
	public String processAddMerchantMemberType(MerchantMemberType memberType, MultipartFile coverPictureFile, RedirectAttributes redirectAttributes) throws Exception{
		isMerchant();
		Merchant merchant = (Merchant)getBusiness();
		
		memberType.setMerchant(merchant);
		if(coverPictureFile !=null && !coverPictureFile.isEmpty()){
			Picture coverPicture = pictureService.getPictureFromMultipartFile(coverPictureFile);
			memberType.setCoverPicture(coverPicture);
		}
		//会员类型名称是否重复
		if(memberTypeService.distinct(memberType)){
			redirectAttributes.addFlashAttribute("status", STATUS_FAILD);
			redirectAttributes.addFlashAttribute("msg", "该会员类型已存在");
			return "redirect:/member/merchant/type/list";
		}
		if(memberTypeService.save(memberType, merchant)){
			redirectAttributes.addFlashAttribute("status", STATUS_SUCCESS);
			redirectAttributes.addFlashAttribute("msg", "创建会员类型成功");
		}else{
			redirectAttributes.addFlashAttribute("status", STATUS_FAILD);
			redirectAttributes.addFlashAttribute("msg", "创建会员类型失败");
		}
		return  "redirect:/member/merchant/type/list";
	}
	
	
	/** 非会员统一的集团为某个子商户添加会员 */
	@AvoidDuplicateSubmission(removeToken=true,errorRedirectURL="/member/merchant/{shopEid}/type/list")
	@RequestMapping(value="{shopEid}/type/add", method=RequestMethod.POST)
	public String processAddShopMemberType(@PathVariable("shopEid")String shopEid, ShopMemberType memberType,
					MultipartFile coverPictureFile, RedirectAttributes redirectAttributes) throws Exception{
		Long shopId = IDUtil.decode(shopEid);
		Shop shop = shopService.findShopByIdIgnoreVisible(shopId);
		isUncentralManagementShop(shop);
		
		memberType.setShop(shop);
		if(coverPictureFile !=null && !coverPictureFile.isEmpty()){
			Picture coverPicture = pictureService.getPictureFromMultipartFile(coverPictureFile);
			memberType.setCoverPicture(coverPicture);
		}
		
		if(memberTypeService.save(memberType, shop)){
			redirectAttributes.addFlashAttribute("status", STATUS_SUCCESS);
			redirectAttributes.addFlashAttribute("msg", "创建会员类型成功");
		}else{
			redirectAttributes.addFlashAttribute("status", STATUS_FAILD);
			redirectAttributes.addFlashAttribute("msg", "创建会员类型失败");
		}
		return  "redirect:/member/merchant/" + shopEid + "/type/list";
	}
	
	/** 更新会员类型 */
	@RequestMapping(value="type/update", method=RequestMethod.POST)
	public String processUpdateMerchantMemberType(MerchantMemberType memberType, MultipartFile coverPictureFile, RedirectAttributes redirectAttributes) throws Exception{
		isMerchant();
		Merchant merchant = (Merchant)getBusiness();
		
		MerchantMemberType memberTypeDB = memberTypeService.findMerchantMemberTypeById(memberType.getId());
		if(memberTypeDB == null){
			redirectAttributes.addFlashAttribute("status", STATUS_FAILD);
			redirectAttributes.addFlashAttribute("msg", "编辑会员类型失败");
			return "redirect:/member/merchant/type/list";
		}else{
			memberType.setMerchant(memberTypeDB.getMerchant());
		}
		
		if(coverPictureFile !=null && !coverPictureFile.isEmpty()){
			Picture coverPicture = pictureService.getPictureFromMultipartFile(coverPictureFile);
			memberType.setCoverPicture(coverPicture);
		}
		
		if(memberTypeService.update(memberType, merchant)){
			redirectAttributes.addFlashAttribute("status", STATUS_SUCCESS);
			redirectAttributes.addFlashAttribute("msg", "编辑会员类型成功");
		}else{
			redirectAttributes.addFlashAttribute("status", STATUS_FAILD);
			redirectAttributes.addFlashAttribute("msg", "编辑会员类型失败");
		}
		return  "redirect:/member/merchant/type/list";
	}
	
	/** 非会员统一的集团为某个子商户更新会员类型 */
	@RequestMapping(value="{shopEid}/type/update", method=RequestMethod.POST)
	public String processUpdateShopMemberType(@PathVariable("shopEid")String shopEid, ShopMemberType memberType,
				MultipartFile coverPictureFile, RedirectAttributes redirectAttributes) throws Exception{
		isMerchant();
		Merchant merchant = (Merchant)getBusiness();
		
		Long shopId = IDUtil.decode(shopEid);
		Shop shop = shopService.findShopByIdIgnoreVisible(shopId);
		isUncentralManagementShop(shop);
		
		ShopMemberType memberTypeDB = memberTypeService.findShopMemberTypeById(memberType.getId());
		if(memberTypeDB == null){
			redirectAttributes.addFlashAttribute("status", STATUS_FAILD);
			redirectAttributes.addFlashAttribute("msg", "编辑会员类型失败");
			return "redirect:/member/merchant/"+ shopEid +"/type/list";
		}else{
			memberType.setShop(memberTypeDB.getShop());
		}
		
		if(coverPictureFile !=null && !coverPictureFile.isEmpty()){
			Picture coverPicture = pictureService.getPictureFromMultipartFile(coverPictureFile);
			memberType.setCoverPicture(coverPicture);
		}
		
		if(memberTypeService.update(memberType, merchant)){
			redirectAttributes.addFlashAttribute("status", STATUS_SUCCESS);
			redirectAttributes.addFlashAttribute("msg", "编辑会员类型成功");
		}else{
			redirectAttributes.addFlashAttribute("status", STATUS_FAILD);
			redirectAttributes.addFlashAttribute("msg", "编辑会员类型失败");
		}
		return  "redirect:/member/merchant/"+ shopEid +"/type/list";
	}
	
	/** 删除会员类型 */
	@RequestMapping(value="type/delete/{id}", method=RequestMethod.POST)
	public String deleteMerchantMemberType(@PathVariable("id")long id, RedirectAttributes redirectAttributes) throws Exception{
		isMerchant();
		Merchant merchant = (Merchant)getBusiness();
		
		MerchantMemberType merchantMemberType = new MerchantMemberType();
		merchantMemberType.setId(id);
		
		//先判断会员类型是否在被使用
		if(!memberTypeService.isUsed(merchantMemberType)) {
			redirectAttributes.addFlashAttribute("status", STATUS_FAILD);
			redirectAttributes.addFlashAttribute("msg", "会员类型正在被使用，删除失败");
			return "redirect:/member/merchant/type/list";
		}
		if(memberTypeService.delete(merchantMemberType, merchant)){
			redirectAttributes.addFlashAttribute("status", STATUS_SUCCESS);
			redirectAttributes.addFlashAttribute("msg", "会员类型删除成功");
		}else{
			redirectAttributes.addFlashAttribute("status", STATUS_FAILD);
			redirectAttributes.addFlashAttribute("msg", "会员类型删除失败");
		}
		return "redirect:/member/merchant/type/list";
	}
	
	/** 非会员统一的集团为某个子商户更新会员类型 */
	@RequestMapping(value="{shopEid}/type/delete/{id}", method=RequestMethod.POST)
	public String deleteShopMemberType(@PathVariable("shopEid")String shopEid, @PathVariable("id")long id, RedirectAttributes redirectAttributes) throws Exception{
		isMerchant();
		Merchant merchant = (Merchant)getBusiness();
		
		Long shopId = IDUtil.decode(shopEid);
		Shop shop = shopService.findShopByIdIgnoreVisible(shopId);
		isUncentralManagementShop(shop);
		
		ShopMemberType memberType = new ShopMemberType();
		memberType.setId(id);
		
		
		if(memberTypeService.delete(memberType, merchant)){
			redirectAttributes.addFlashAttribute("status", STATUS_SUCCESS);
			redirectAttributes.addFlashAttribute("msg", "会员类型删除成功");
		}else{
			redirectAttributes.addFlashAttribute("status", STATUS_FAILD);
			redirectAttributes.addFlashAttribute("msg", "会员类型删除失败");
		}
		return  "redirect:/member/merchant/" + shopEid + "/type/list";
	}
	
}

