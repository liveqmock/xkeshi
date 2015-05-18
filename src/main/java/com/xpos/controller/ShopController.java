package com.xpos.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.xkeshi.endpoint.GrantResult;
import com.xkeshi.endpoint.JSONResponse;
import com.xkeshi.interceptor.form.AvoidDuplicateSubmission;
import com.xkeshi.pojo.vo.ShopDiscountSettingVO;
import com.xkeshi.pojo.vo.param.POSGatewayAccountParam;
import com.xkeshi.pojo.vo.param.ShopDiscountSettingParam;
import com.xkeshi.pojo.vo.param.ShopInfoParam;
import com.xkeshi.service.ShopDiscountSettingService;
import com.xkeshi.service.XShopService;
import com.xkeshi.utils.EncryptionUtil;
import com.xpos.common.entity.Article;
import com.xpos.common.entity.Category;
import com.xpos.common.entity.Contact;
import com.xpos.common.entity.Merchant;
import com.xpos.common.entity.Operator;
import com.xpos.common.entity.Picture;
import com.xpos.common.entity.Region;
import com.xpos.common.entity.Shop;
import com.xpos.common.entity.ShopInfo;
import com.xpos.common.entity.ShopInfo.ConsumeType;
import com.xpos.common.entity.ShopPrinter;
import com.xpos.common.entity.Terminal;
import com.xpos.common.entity.Terminal.TerminalType;
import com.xpos.common.entity.example.PictureExample;
import com.xpos.common.entity.example.RegionExample;
import com.xpos.common.entity.example.ShopExample;
import com.xpos.common.entity.example.TerminalExample;
import com.xpos.common.entity.face.Business;
import com.xpos.common.entity.face.Business.BusinessType;
import com.xpos.common.entity.pos.POSGatewayAccount;
import com.xpos.common.entity.pos.POSOperationLog;
import com.xpos.common.entity.security.Account;
import com.xpos.common.searcher.PictureSearcher;
import com.xpos.common.searcher.ShopSearcher;
import com.xpos.common.searcher.TerminalSearcher;
import com.xpos.common.service.AccountService;
import com.xpos.common.service.BaseDataService;
import com.xpos.common.service.MerchantService;
import com.xpos.common.service.OperatorService;
import com.xpos.common.service.OperatorShiftService;
import com.xpos.common.service.POSGatewayAccountService;
import com.xpos.common.service.PictureService;
import com.xpos.common.service.RegionService;
import com.xpos.common.service.ShopPrinterService;
import com.xpos.common.service.ShopService;
import com.xpos.common.service.TerminalService;
import com.xpos.common.utils.FileMD5;
import com.xpos.common.utils.Pager;

@Controller
@RequestMapping("shop")
public class ShopController extends BaseController{
	

	@Autowired
	private ShopService shopService;
	
	@Autowired
	private BaseDataService baseDataService;
	
	@Autowired
	private PictureService pictureService;
	
	@Autowired
	private TerminalService terminalService;
	
	@Autowired
	private AccountService accountService;
	
	@Autowired
	private MerchantService merchantService;
	
	@Autowired
	private RegionService regionService;
	
	@Autowired
	private OperatorService operatorService;
	
	@Autowired
	private POSGatewayAccountService posGatewayAccountService;
	
	@Autowired
	private ShopPrinterService shopPrinterService;
	
	@Autowired
	private XShopService  xShopService  ;
	
	@Autowired
	private OperatorShiftService  operatorShiftService ;

    @Autowired
    private ShopDiscountSettingService discountWayService;

	/** 显示添加店铺页面  */
	@AvoidDuplicateSubmission(addToken = true) 
	@RequestMapping(value="/add", method=RequestMethod.GET)
	public String showAddShop(Model model){
		Map<Long, List<Category>> findAllCategoryGoupByParentId = null;
		List<Category> parentCategories = baseDataService.getTopCategory();		
		List<Region> regions = baseDataService.findRegionByCityCode(getCityCode());
	    findAllCategoryGoupByParentId = baseDataService.findAllCategoryGoupByParentId();
		model.addAttribute("categories", findAllCategoryGoupByParentId);
		model.addAttribute("parentCategories", parentCategories);
		model.addAttribute("regions", regions);
		return "shop/shop_input";
	}
	/**  添加店铺  */
	@AvoidDuplicateSubmission(removeToken = true,errorRedirectURL="/shop/list")
	@RequestMapping(value="/add", method=RequestMethod.POST)
	public String addShop(@Valid Shop shop,  MultipartFile bannerFile, Model  model, MultipartFile avatarFile, RedirectAttributes redirectAttributes){
		redirectAttributes.addFlashAttribute("status", STATUS_FAILD);
		redirectAttributes.addFlashAttribute("msg", "商户创建失败");
		try {
            if(shopService.validateFullNameIsExist(shop.getFullName().trim(), (shop.getId() == null ? null : shop.getId().toString()))){
                redirectAttributes.addFlashAttribute("msg", "存在相同的商户全称！");
                return "redirect:/shop/add";
            } else if (shopSaveOrUpdate(shop, bannerFile, avatarFile)){
                redirectAttributes.addFlashAttribute("status", STATUS_SUCCESS);
                redirectAttributes.addFlashAttribute("msg", "商户创建成功");
            }
        } catch (Exception e) {
			 logger.error(e.toString());
		}
		return  (super.getBusiness() instanceof Merchant) ? "redirect:/shop/"+shop.getId() : "redirect:/shop/list" ;
	}
	private boolean shopSaveOrUpdate(Shop shop, MultipartFile bannerFile, MultipartFile avatarFile) {
		if(bannerFile != null && !bannerFile.isEmpty()){
			Picture banner = pictureService.getPictureFromMultipartFile(bannerFile);
			shop.setBanner(banner);
		}
		if(avatarFile !=null && !avatarFile.isEmpty()){
			Picture avatar = pictureService.getPictureFromMultipartFile(avatarFile);
			shop.setAvatar(avatar);
		}
		if (shop.getRegion() != null && shop.getRegion().getId()!=null) {
			RegionExample regionExample = new RegionExample();
			regionExample.appendCriterion("districtCode=", shop.getRegion().getId());
			Region findRegion = regionService.findRegion(regionExample);
			if (findRegion != null){
				shop.setCityCode(findRegion.getCityCode());
			}
		}
		if(shop.getId()!=null){
			Shop shopPO = shopService.findShopByIdIgnoreVisible(shop.getId());
			if(shopPO != null){
				if(shopPO.getMerchant()== null && getBusiness() instanceof Merchant){
					shop.setMerchant((Merchant)getBusiness());
				}
				return  shopService.updateShop(shop);
			}
		}else{
			if (shop.getMerchant() == null && getBusiness() instanceof Merchant) {
				//集团下关联集团商户
				shop.setMerchant((Merchant)getBusiness());
			}
			return shopService.saveShop(shop);
		}
		return  false;
	}
	
	/** 显示编辑店铺页面  */
	@RequestMapping(value="/edit/{shopId}", method=RequestMethod.GET)
	public String showEidtShop(@PathVariable Long shopId,Model model){
		//设置公用信息
		model.addAttribute("parentCategories", baseDataService.getTopCategory());
		model.addAttribute("categories", baseDataService.findAllCategoryGoupByParentId());
		model.addAttribute("regions", baseDataService.findRegionByCityCode(getCityCode()));
		
		Shop shop  = shopService.findShopByIdIgnoreVisible(shopId);
		if(shop == null) {
			model.addAttribute("status",FAILD);
			model.addAttribute("msg","编辑商户信息失败");
			return "shop/shop_input";
		}
		Business business = super.getBusiness();
		if (business instanceof Merchant){
			//防止集团管理员，修改其他集团下的商户信息
			if(shop.getMerchant() == null || !shop.getMerchant().getId().equals(business.getSelfBusinessId())){
				model.addAttribute("status",FAILD);
				model.addAttribute("msg","编辑商户信息失败");
				return "shop/shop_input";
			}
		}else if (business instanceof Shop && !shop.getId().equals(business.getSelfBusinessId())){
			//防止商户修改其他商户信息
			model.addAttribute("status",FAILD);
			model.addAttribute("msg","编辑商户信息失败");
			return "shop/shop_input";
		}
		List<Contact> contacts = shopService.findContactsByShopId(shopId); //商户联系人资料
		model.addAttribute("contacts", contacts);
		model.addAttribute("shop", shop);
		return "shop/shop_input";
	}
		
	/** 修改店铺  */
	@RequestMapping(value="/edit", method=RequestMethod.POST)
	public String updateShop(@Valid Shop shop, Model  model,  MultipartFile bannerFile,  MultipartFile avatarFile, RedirectAttributes redirectAttributes){
		redirectAttributes.addFlashAttribute("status", STATUS_FAILD);
		redirectAttributes.addFlashAttribute("msg", "商户资料修改失败");
		try {
            if(shopService.validateFullNameIsExist(shop.getFullName().trim(), shop.getId().toString())) {
                redirectAttributes.addFlashAttribute("msg", "存在相同的商户全称！");
                return "redirect:/shop/edit/"+shop.getId();
            } else if (shopSaveOrUpdate(shop, bannerFile, avatarFile)){
				redirectAttributes.addFlashAttribute("status", STATUS_SUCCESS);
				redirectAttributes.addFlashAttribute("msg", "商户资料修改成功");
			} 
		} catch (Exception e) {
			 logger.error(e.toString());
		}
		return  (super.getBusiness() instanceof Shop) ? "redirect:/shop/"+shop.getId() : "redirect:/shop/list" ;
	}
	
	@RequestMapping(value="/list", method=RequestMethod.GET)
	public String findShops(Pager<Shop> pager, ShopSearcher searcher, Model model){
		ShopExample example = (ShopExample)searcher.getExample();
		Business business = super.getBusiness();
		List<Merchant> findAllMerchant   = new ArrayList<>();
		if (business instanceof Merchant) {
			 //merchant管理员
			example.appendCriterion("merchant_id = ", business.getSelfBusinessId());
			findAllMerchant.add((Merchant) business);
		}else if(business instanceof Shop) {
			return "shop/shop_list";
		}
		if(business == null){
			//admin管理员
			//TODO 待前后端一起优化
			Pager<Merchant> merchantPager = new Pager<Merchant>();
			merchantPager.setPageSize(Integer.MAX_VALUE);
		    findAllMerchant = merchantService.findMerchants(null,merchantPager).getList();
	    }
		List<Category> parentCategories = baseDataService.getTopCategory();	
		pager = shopService.findShopListIgnoreVisible(pager, example);
		model.addAttribute("pager", pager);
		model.addAttribute("parentCategories", parentCategories);
		model.addAttribute("merchants", findAllMerchant);
		model.addAttribute("categories", baseDataService.findAllCategoryGoupByParentId());
		model.addAttribute("searcher", searcher);
		return "shop/shop_list";
	}
    
	@RequestMapping(value="/relmerchant" ,method  = RequestMethod.POST)
	public String relMerchant(@RequestParam("shopIds")   Long shopIds [] ,  Merchant  merchant, RedirectAttributes  redirectAttributes) throws IOException{
		Business business = super.getBusiness();
		redirectAttributes.addFlashAttribute("status",STATUS_FAILD);
		redirectAttributes.addFlashAttribute("msg",  "关联失败");
		if(business == null){
			if(merchant.getId() !=null ) {
				//添加关联商户
		        addShopByMerchant(shopIds, merchant, redirectAttributes);
			} else {
				//取消关联商户
				cancelShopByMerchant(shopIds, redirectAttributes);
			}
		}
		return  "redirect:/shop/list";
	}
	/**
	 * 取消关联集团下的商户
	 */
	private void cancelShopByMerchant(Long[] shopIds, RedirectAttributes redirectAttributes) {
		boolean result = true;
		if(getBusiness() == null){
			for (Long shopId : shopIds) {
				result = result && shopService.quitMerchant(shopId);
			}
		}else{
			result = false;
		}
		if(result){
			redirectAttributes.addFlashAttribute("status",STATUS_SUCCESS);
			redirectAttributes.addFlashAttribute("msg",  "成功取消关联");
		}
	}
	
	/**
	 * 添加关联集团下的商户
	 */
	private void addShopByMerchant(Long[] shopIds, Merchant merchant, RedirectAttributes redirectAttributes) {
		merchant = merchantService.findMerchant(merchant.getId());
		if(merchant != null){
			boolean result = true;
			Long[] joinedShops = shopService.findShopIdsByMerchantId(merchant.getId(), true);
			for (Long shopId : shopIds) {
				if(ArrayUtils.contains(joinedShops, shopId)){
					continue;
				}
				result = result && shopService.joinMerchant(shopId, merchant.getId());
			}
			if(result){
				redirectAttributes.addFlashAttribute("status",STATUS_SUCCESS);
				redirectAttributes.addFlashAttribute("msg",  "关联成功");
			}
		}
	}
	
	
	@RequestMapping(value="/{id}", method=RequestMethod.GET)
	public String showShopDetail(@PathVariable Long id, Model model){
		Business business = super.getBusiness();
		Shop shop  = shopService.findShopByIdIgnoreVisible(id);
		if (shop == null){
			model.addAttribute("status",FAILD);
			model.addAttribute("msg","你查看的商户不存在");
			return "shop/shop_detail";
		}
		
		if (business instanceof Merchant){
			//防止集团管理员，查看非该集团子商户信息
			if(shop.getMerchant() == null || !shop.getMerchant().getId().equals(business.getSelfBusinessId())){
				model.addAttribute("status",FAILD);
				model.addAttribute("msg","你查看的商户不存在");
				return "shop/shop_detail";
			}
		}else if(business instanceof Shop && !shop.getId().equals(business.getSelfBusinessId())){
			model.addAttribute("status",FAILD);
			model.addAttribute("msg","你查看的商户不存在");
			return "shop/shop_detail";
		}
		List<Contact> contacts = shopService.findContactsByShopId(id);
		model.addAttribute("shop", shop);
		model.addAttribute("contacts", contacts);
		return "shop/shop_detail";
	}
	
	@AvoidDuplicateSubmission(addToken= true)
	@RequestMapping(value="/{id}/account", method=RequestMethod.GET)
	public String showShopAccount(@PathVariable Long id, Model model){
		Shop shop = new Shop();
		shop.setId(id);
		List<Account> account = accountService.findAccountByBusiness(shop);
		ShopInfo shopInfo = shopService.findShopInfoByShopId(id);
		model.addAttribute("shopInfo", shopInfo);
		model.addAttribute("accountList", account);
		model.addAttribute("shopId", id);
		return "shop/shop_account";
	}
	
	
	@RequestMapping(value="/account/edit/{id}", method=RequestMethod.POST)
	public String editShopAccount(@PathVariable Long id, Model model, Account account,
								BindingResult result ,RedirectAttributes redirectAttributes) throws IOException{
		Business business = super.getBusiness();
		if (business == null) {
			if(result.hasErrors()){
				redirectAttributes.addFlashAttribute("status",STATUS_FAILD);
				redirectAttributes.addFlashAttribute("msg", "修改商户账号出错，" + result.getAllErrors());
				return "redirect:/shop/"+id+"/account";
			}
		}
		//account.setIsInitPassword(true);
		String resultType = accountService.editAccountByshopId(id,account);
		if (resultType !=null) {
			redirectAttributes.addFlashAttribute("status", STATUS_FAILD);
			redirectAttributes.addFlashAttribute("msg", "修改商户账号出错，"+resultType);
			return "redirect:/shop/"+id+"/account";
		}
		redirectAttributes.addFlashAttribute("status", STATUS_SUCCESS);
		redirectAttributes.addFlashAttribute("msg", "修改商户账号成功");
		
		return "redirect:/shop/"+id+"/account";
	}
	
	@RequestMapping(value="/account/delete/{id}", method=RequestMethod.DELETE)
	public HttpEntity<String> deleteShopAccount(@PathVariable Long id, Model model){
		boolean result = accountService.deleteAccountByshopId(id);
		JSONResponse  jsonResponse = new JSONResponse(result ? new GrantResult(SUCCESS, "删除成功") : new GrantResult(FAILD, "删除失败"));;
		return new ResponseEntity<String>( jsonResponse.getBody(), jsonResponse.getHttpStatus()) ;
	}
	 
	@RequestMapping(value="/{id}/about", method=RequestMethod.GET)
	public String showShopAbout(@PathVariable Long id, Model model){
		model.addAttribute("shopId", id);
		List<Article> articles = shopService.findArticlesByShopId(id);
		model.addAttribute("articles", articles);
		return "shop/shop_about";
	}
	
	@RequestMapping(value="{id}/article", method=RequestMethod.POST)
	public String addArticle(@PathVariable Long id, Article article, Model model){
		Shop shop = new Shop();
		shop.setId(id);
		article.setShop(shop);
		article.setAuthor(getAccount());
		shopService.saveOrUpdateArticle(article);
		model.addAttribute("shopId", id);
		return "redirect:/shop/"+id+"/about";
	}
	
	/**终端帐号管理*/
	@AvoidDuplicateSubmission(addToken=true)
	@RequestMapping(value="/{id}/pos", method=RequestMethod.GET)
	public String showShopPos(@PathVariable Long id, Model model){
		List<Operator> operatorList = terminalService.findOperatorsByShopId(id);
		List<Terminal> terminalList = terminalService.findTerminalsByShopId(id);
		ShopInfo shopInfo = shopService.getShopInfoByShopId(id);
        List<ShopDiscountSettingVO> disCountWayList = discountWayService.getShopDiscountWayById(id);
        //获取支付宝支付方式
        POSGatewayAccount alipay = posGatewayAccountService.findByShopIdAndType(id, POSGatewayAccount.POSGatewayAccountType.ALIPAY);
        //获取微信支付方式
        POSGatewayAccount wechat = posGatewayAccountService.findByShopIdAndType(id, POSGatewayAccount.POSGatewayAccountType.WECHAT);
        //获取银行卡支付方式
        POSGatewayAccount bankCard = posGatewayAccountService.findBankCardPosGatewayAccount(id);
        model.addAttribute("alipay",alipay);
        model.addAttribute("wechat",wechat);
        model.addAttribute("bankCard",bankCard);
        model.addAttribute("disCountWayList",disCountWayList);
        model.addAttribute("sizeOfOperatorsList",operatorList.size());
		model.addAttribute("shopInfo", shopInfo);
		model.addAttribute("operatorList", operatorList);
		model.addAttribute("terminalList", terminalList);
		model.addAttribute("shopId", id);
		return "shop/shop_pos";
	}
	
	@AvoidDuplicateSubmission(removeToken=true,errorRedirectURL="/shop/{shopId}/pos")
	@RequestMapping(value="/operator/add/{shopId}", method=RequestMethod.POST)
	public String addShopPos(@PathVariable Long shopId, Model model,@Valid Operator operator,  
			                 BindingResult result , RedirectAttributes  redirectAttributes ){
		if(result.hasErrors()){
			redirectAttributes.addFlashAttribute("status", STATUS_FAILD);
			redirectAttributes.addFlashAttribute("msg", result.getFieldError().getDefaultMessage());
			return  "redirect:/shop/"+shopId+"/pos";
		}
		Shop shop = shopService.findShopByIdIgnoreVisible(shopId);
		if(shop == null){
			redirectAttributes.addFlashAttribute("status", STATUS_FAILD);
			redirectAttributes.addFlashAttribute("msg", "POS终端操作员添加失败");
			return  "redirect:/shop/"+shopId+"/pos";
		}
		
		operator.setShop(shop);
		String resultType = operatorService.save(operator);
		if (resultType !=null) {
			redirectAttributes.addFlashAttribute("status", STATUS_FAILD);
			redirectAttributes.addFlashAttribute("msg", resultType);
			return  "redirect:/shop/"+shopId+"/pos";
		}
		redirectAttributes.addFlashAttribute("status", STATUS_SUCCESS);
		redirectAttributes.addFlashAttribute("msg", "POS终端操作员添加成功");
		return "redirect:/shop/"+shopId+"/pos";
	}
	
	@RequestMapping(value="/operator/edit/{shopId}", method=RequestMethod.PUT)
	public String editShopPos(@PathVariable Long shopId, Model model,Operator operator,
								BindingResult result ,  RedirectAttributes redirectAttributes) throws IOException{
		if(result.hasErrors()){
			redirectAttributes.addFlashAttribute("status", STATUS_FAILD);
			redirectAttributes.addFlashAttribute("msg", result.getAllErrors());
			return  "redirect:/shop/"+shopId+"/pos";
		}
		Shop shop = shopService.findShopByIdIgnoreVisible(shopId);
		if(shop == null){
			redirectAttributes.addFlashAttribute("status", STATUS_FAILD);
			redirectAttributes.addFlashAttribute("msg", "POS终端操作员编辑失败");
			return  "redirect:/shop/"+shopId+"/pos";
		}
		operator.setShop(shop);
		String resultType = operatorService.update(operator);
		if (resultType !=null) {
			redirectAttributes.addFlashAttribute("status", STATUS_FAILD);
			redirectAttributes.addFlashAttribute("msg", resultType);
			return  "redirect:/shop/"+shopId+"/pos";
		}
		redirectAttributes.addFlashAttribute("status", STATUS_SUCCESS);
		redirectAttributes.addFlashAttribute("msg", "POS终端操作员编辑成功");
		return "redirect:/shop/"+shopId+"/pos";
	}
	
	
	@RequestMapping(value="/operator/delete/{opid}", method=RequestMethod.DELETE)
	public HttpEntity<String> deletePosOperator(@PathVariable Long opid, Model model){
		boolean result = operatorService.deleteById(opid);
		JSONResponse  jsonResponse = new JSONResponse(result? new GrantResult(SUCCESS, "SUCCESS") : new GrantResult(FAILD, "FAILD"));;
		return new ResponseEntity<String>( jsonResponse.getBody(), jsonResponse.getHttpStatus()) ;
	}
	
	@AvoidDuplicateSubmission(removeToken=true,errorRedirectURL="/shop/{shopId}/pos")
	@RequestMapping(value="/terminal/add/{shopId}", method=RequestMethod.POST)
	public String addTerminal(@PathVariable Long shopId, Model model,@Valid Terminal terminal, BindingResult result, RedirectAttributes redirect) {
		Business business = super.getBusiness();
		if (business != null) {
			redirect.addFlashAttribute("status", FAILD);
			redirect.addFlashAttribute("msg", "操作不被允许");
			return (business instanceof  Shop) ? "redirect:/shop/pos_terminal" :"redirect:/shop/"+shopId+"/pos";
		}
		if(result.hasErrors()){
			model.addAttribute("status", FAILD);
			model.addAttribute("msg", "设备号不能为空");
			return "redirect:/shop/"+shopId+"/pos";
		}
		if (terminalService.findTerminalByDevice(terminal.getDeviceNumber()) != null) {
			redirect.addFlashAttribute("status", FAILD);
			redirect.addFlashAttribute("msg", "该设备码已被绑定");
		} else {
			terminal.setTerminalType(TerminalType.CASHIER);  //TODO 先写死成“收银台”类型，后续需要改接口，传入参数设备类型
			String resultType = terminalService.addTerminalByShopId(shopId,terminal);
			if (resultType !=null) {
				model.addAttribute("status", FAILD);
				model.addAttribute("msg", resultType);
				return "redirect:/shop/"+shopId+"/pos";
			}
		}
		return "redirect:/shop/"+shopId+"/pos";
	}
	
	@RequestMapping(value="/terminal/delete/{shopId}", method=RequestMethod.DELETE)
	public String removeTerminal(@PathVariable Long shopId, Model model,Terminal terminal  ,RedirectAttributes  attribute) {
		Business business = super.getBusiness();
		attribute.addFlashAttribute("status", STATUS_FAILD);
		attribute.addFlashAttribute("msg", "解绑失败");
		terminal = terminalService.findTerminalsByTerminalId(terminal.getId());
		if (business instanceof  Shop  ) {
			//跨商户解绑 
			if ( !shopId.equals(((Shop) business).getId()) || (terminal.getShop() != null  &&  !shopId.equals(terminal.getShop().getId()))) 
				return "redirect:/shop/pos_terminal";
		} else if (business  instanceof  Merchant) {
		   return "redirect:/shop/"+shopId+"/pos";
		}  
		com.xkeshi.pojo.po.Shop shop = xShopService.findShopByShopId(shopId);
		//检查是否已经交接班
		if (shop != null && shop.getEnableShift()) {
			POSOperationLog operationLog = operatorShiftService.findOperatorSessionByDeviceNumber(terminal.getDeviceNumber());
			if (operationLog != null ) {
				attribute.addFlashAttribute("msg", "解绑失败,该设备未完成交接");
				return  (business instanceof  Shop) ? "redirect:/shop/pos_terminal" : "redirect:/shop/"+shopId+"/pos";
			}
		}
		if (terminalService.removeTerminalById(terminal)) {
			attribute.addFlashAttribute("status", STATUS_SUCCESS);
			attribute.addFlashAttribute("msg", "解绑成功");
		}
		return (business instanceof  Shop) ? "redirect:/shop/pos_terminal" :"redirect:/shop/"+shopId+"/pos";
	}
	
	/**
	 * POS机终端
	 * @param pager
	 * @param searcher
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/terminal/list", method=RequestMethod.GET)
	public String findTerminals(Pager<Terminal> pager, TerminalSearcher searcher, Model model){
		pager = terminalService.findTerminalList(pager, (TerminalExample) searcher.getExample());
		model.addAttribute("pager", pager);
		model.addAttribute("searcher", searcher);
		return  "shop/terminal_list";
	}
	
	@RequestMapping(value="/shopInfo/edit/{shopId}", method=RequestMethod.PUT)
	public String editShopInfo(@PathVariable Long shopId, Model model, @Valid ShopInfo shopInfo,RedirectAttributes redirectAttributes) throws IOException{
		shopInfo.setShopId(shopId);
		String resultType = shopService.saveOrUpdateShopInfo(shopInfo);
		if (resultType !=null) {
			redirectAttributes.addFlashAttribute("status", FAILD);
			redirectAttributes.addFlashAttribute("msg", resultType);
			return showShopAccount(shopId,model);
		}
		redirectAttributes.addFlashAttribute("success", SUCCESS);
		redirectAttributes.addFlashAttribute("msg", "设置短信和二级域名成功");
		return "redirect:/shop/"+shopId+"/account";
	}
	
	@AvoidDuplicateSubmission(removeToken=true)
	@RequestMapping(value="/{shopId}/gatewayAccount/add", method=RequestMethod.POST)
	public String addGatewayAccount(@PathVariable Long shopId, Model model, @Valid POSGatewayAccount account, BindingResult result,RedirectAttributes redirectAttributes) throws IOException{
		if(result.hasErrors()){
			redirectAttributes.addFlashAttribute("status", FAILD);
			redirectAttributes.addFlashAttribute("msg", result.getFieldError().getDefaultMessage());
			return "redirect:/shop/"+shopId+"/account";
		}
		
		account.setBusinessId(shopId);
		account.setBusinessType(BusinessType.SHOP);
		if (posGatewayAccountService.save(account)) {
			redirectAttributes.addFlashAttribute("status", SUCCESS);
			redirectAttributes.addFlashAttribute("msg", "添加支付方式和终端成功");
			return "redirect:/shop/"+shopId+"/account";
		}else{
			redirectAttributes.addFlashAttribute("status", FAILD);
			redirectAttributes.addFlashAttribute("msg", "添加支付方式和终端失败");
			return "redirect:/shop/"+shopId+"/account";
		}
	}
	
	@RequestMapping(value="/{shopId}/gatewayAccount/edit", method=RequestMethod.POST)
	public String editGatewayAccount(@PathVariable Long shopId, Model model, @Valid POSGatewayAccount account, BindingResult result,RedirectAttributes redirectAttributes) throws IOException{
		if(result.hasErrors()){
			redirectAttributes.addFlashAttribute("status", FAILD);
			redirectAttributes.addFlashAttribute("msg", result.getFieldError().getDefaultMessage());
			return "redirect:/shop/"+shopId+"/account";
		}
		
		account.setBusinessId(shopId);
		account.setBusinessType(BusinessType.SHOP);
		if (posGatewayAccountService.update(account)) {
			redirectAttributes.addFlashAttribute("status", SUCCESS);
			redirectAttributes.addFlashAttribute("msg", "修改支付方式和终端成功");
			return "redirect:/shop/"+shopId+"/account";
		}else{
			redirectAttributes.addFlashAttribute("status", FAILD);
			redirectAttributes.addFlashAttribute("msg", "修改支付方式和终端失败");
			return "redirect:/shop/"+shopId+"/account";
		}
	}
	
	@RequestMapping(value="/{shopId}/gatewayAccount/delete", method=RequestMethod.POST)
	public String editGatewayAccount(@PathVariable Long shopId, Model model, @RequestParam("id") Long accountId,RedirectAttributes redirectAttributes) throws IOException{
		//校验权限
		Business business = super.getBusiness();
		Shop shop  = shopService.findShopByIdIgnoreVisible(shopId);
		if (shop == null){
			redirectAttributes.addFlashAttribute("status", FAILD);
			redirectAttributes.addFlashAttribute("msg", "支付方式和终端删除失败");
			return "redirect:/shop/"+shopId+"/account";
		}
		
		if (business instanceof Merchant){
			//防止集团管理员，删除非该集团子商户信息
			if(shop.getMerchant() == null || !shop.getMerchant().getId().equals(business.getSelfBusinessId())){
				redirectAttributes.addFlashAttribute("status", FAILD);
				redirectAttributes.addFlashAttribute("msg", "支付方式和终端删除失败");
				return "redirect:/shop/"+shopId+"/account";
			}
		}else if(business instanceof Shop && !shop.getId().equals(business.getSelfBusinessId())){
			redirectAttributes.addFlashAttribute("status", FAILD);
			redirectAttributes.addFlashAttribute("msg", "支付方式和终端删除失败");
			return "redirect:/shop/"+shopId+"/account";
		}
		
		if (posGatewayAccountService.deleteByShopId(shopId, accountId)) {
			redirectAttributes.addFlashAttribute("status", SUCCESS);
			redirectAttributes.addFlashAttribute("msg", "支付方式和终端删除成功");
			return "redirect:/shop/"+shopId+"/account";
		}else{
			redirectAttributes.addFlashAttribute("status", FAILD);
			redirectAttributes.addFlashAttribute("msg", "支付方式和终端删除失败");
			return "redirect:/shop/"+shopId+"/account";
		}
	}
	
	@RequestMapping(value="/{shopId}/consumeType/edit", method=RequestMethod.POST)
	public String editConsumeType(@PathVariable Long shopId, Model model, @RequestParam("consumeType") String consumeTypeStr) throws IOException{
		//校验权限
		Business business = super.getBusiness();
		Shop shop  = shopService.findShopByIdIgnoreVisible(shopId);
		if (shop == null){
			model.addAttribute("status",FAILD);
			model.addAttribute("msg","修改点单和付款方式失败");
			return showShopPos(shopId,model);
		}
		
		if (business instanceof Merchant){
			//防止集团管理员，删除非该集团子商户信息
			if(shop.getMerchant() == null || !shop.getMerchant().getId().equals(business.getSelfBusinessId())){
				model.addAttribute("status",FAILD);
				model.addAttribute("msg","修改点单和付款方式失败");
				return showShopPos(shopId,model);
			}
		}else if(business instanceof Shop && !shop.getId().equals(business.getSelfBusinessId())){
			model.addAttribute("status",FAILD);
			model.addAttribute("msg","修改点单和付款方式失败");
			return showShopPos(shopId,model);
		}
		
		//校验参数
		ConsumeType consumeType = null;
		for(ConsumeType type : ConsumeType.values()){
			if(StringUtils.equalsIgnoreCase(type.toString(), consumeTypeStr)){
				consumeType = type;
				break;
			}
		}
		if(consumeType == null){
			model.addAttribute("status",FAILD);
			model.addAttribute("msg","修改点单和付款方式失败");
			return showShopPos(shopId,model);
		}
		
		ShopInfo shopInfo = shopService.findShopInfoByShopId(shopId);
		if(shopInfo == null){
			shopInfo = new ShopInfo();
			shopInfo.setShopId(shopId);
		}
		shopInfo.setShopId(shopId);
		shopInfo.setConsumeType(consumeType);
		if (shopService.saveOrUpdateShopInfo(shopInfo) == null) {
			model.addAttribute("status", SUCCESS);
			model.addAttribute("msg", "修改点单和付款方式成功");
		}else{
			model.addAttribute("status", FAILD);
			model.addAttribute("msg", "修改点单和付款方式失败");
		}
		return showShopPos(shopId,model);
	}
	
	@RequestMapping(value="/publish", method=RequestMethod.PUT)
	public String publish(boolean visible, Long[] shopIds, Model model){
		Business business = getBusiness();
		if (shopIds == null ) {
			model.addAttribute("status", FAILD);
			model.addAttribute("msg", "未选择设置商户");
			return findShops(new Pager<Shop>(), new ShopSearcher(), model);
		}
		if(business instanceof Shop){ //商户无设置权限
			model.addAttribute("status", FAILD);
			model.addAttribute("msg", "商户无设置权限");
			return findShops(new Pager<Shop>(), new ShopSearcher(), model);
		}else if(business instanceof Merchant){
			Long[] fullIds = shopService.findShopIdsByMerchantId(business.getSelfBusinessId(), true);
			List<Long> fullIdList = Arrays.asList(fullIds);
			List<Long> subIdList = Arrays.asList(shopIds);
			if(!fullIdList.containsAll(subIdList)){
				model.addAttribute("status", FAILD);
				model.addAttribute("msg", "修改失败");
				return findShops(new Pager<Shop>(), new ShopSearcher(), model);
			}
		}
		if(shopService.batchSetVisible(shopIds, visible)){
			model.addAttribute("status", SUCCESS);
			model.addAttribute("msg", "修改成功");
		}else{
			model.addAttribute("status", FAILD);
			model.addAttribute("msg", "修改失败");
		}
		return findShops(new Pager<Shop>(), new ShopSearcher(), model);
	}
	
	@RequestMapping(value = "/{id}/album"  , method = RequestMethod.GET)
	public String shopAlbum(@PathVariable("id") Long shopId ,Pager<Picture> pager, PictureSearcher searcher,  Model model){
		Business business = getBusiness();
		if(business == null ){  //管理员操作  --代为商铺创建
			Shop shop = new Shop();
			shop.setId(shopId);
			business  = shop ;
		}
		pager = pictureService.findShopByAlbumList(pager, (PictureExample)searcher.getExample() ,business ,shopId);
		model.addAttribute("pager", pager);
		model.addAttribute("searcher", searcher);
		model.addAttribute("shopId", shopId);
		return  "shop/shop_album";
	}
	 
	@AvoidDuplicateSubmission(addToken=true)
	@RequestMapping(value="/album/add/{id}", method=RequestMethod.GET)
	public String showAddAlbum (@PathVariable("id") Long shopId ,Model model){
		model.addAttribute("shopId", shopId);
		return "shop/shop_album_input";
	}
	@RequestMapping(value="/album/editPic/{picId}", method=RequestMethod.GET)
	public String showEditAlbum (@RequestParam("shopId") Long shopId,Model model,@PathVariable Long picId){
		model.addAttribute("pic", pictureService.findAlbumBypicId(picId));
		model.addAttribute("shopId", shopId);
		return "shop/shop_album_input";
	}
	
	@AvoidDuplicateSubmission(removeToken=true,errorRedirectURL="/shop/{shopId}/album")
	@RequestMapping(value="/album/add/{shopId}", method=RequestMethod.POST)
	public String addAlbum(@PathVariable("shopId") Long shopId,MultipartFile albumFile, RedirectAttributes model,Picture picture){
		model.addFlashAttribute("status", STATUS_SUCCESS);
		model.addFlashAttribute("msg", "添加图片成功");
		if (!albumFile.isEmpty()) {
			Picture album = pictureService.getPictureFromMultipartFile(albumFile);
			//判断是否存在相同的MD5
			Business business = getBusiness();
			if(business == null || business.getSelfBusinessType()== BusinessType.MERCHANT){  //管理员操作  --代为商铺创建
				Shop shop = new Shop();
				shop.setId(shopId);
				business  = shop ;
			}
			if (pictureService.findDetailPicture(album,business)==null) {
				picture.setData(album.getData());
				picture.setName(album.getName());
				picture.setOriginalName(album.getOriginalName());
				pictureService.saveAlbum(picture,shopId);
			}else {
				model.addFlashAttribute("status", STATUS_FAILD);
				model.addFlashAttribute("msg", "上传的图片已存在，请勿重复上传!");
				return "redirect:/shop/album/add/"+shopId;
			}
		}
		return "redirect:/shop/"+shopId+"/album";
	}
	
	@RequestMapping(value="/delete/{id}/{shopId}", method=RequestMethod.DELETE)
	public String deleteAlbum(@PathVariable Long id, @PathVariable Long shopId,RedirectAttributes model){
		if(pictureService.deleteAlbum(id)) {
			model.addFlashAttribute("status",STATUS_SUCCESS);
			model.addFlashAttribute("msg", "删除图片成功");
		}else {
			model.addFlashAttribute("status", STATUS_FAILD);
			model.addFlashAttribute("msg", "删除图片失败");
		}
		return "redirect:/shop/"+shopId+"/album";
	}
	@RequestMapping(value="/album/edit/{picId}", method=RequestMethod.POST)
	public String editAlbum(@RequestParam("shopId") Long shopId,MultipartFile albumFile, RedirectAttributes model,Picture picture,@PathVariable Long picId){
		model.addFlashAttribute("status",STATUS_SUCCESS);
		model.addFlashAttribute("msg", "修改图片成功");
		if (!albumFile.isEmpty()) {
			Picture album = pictureService.getPictureFromMultipartFile(albumFile);
			Business business = getBusiness();
			if(business == null || business.getSelfBusinessType()== BusinessType.MERCHANT){  //管理员或集团操作  --代为商铺创建
				Shop shop = new Shop();
				shop.setId(shopId);
				business  = shop ;
			}
			//判断是否存在相同的MD5(除自身以外)
			if (pictureService.findDetailPicture(album,business)==null) {
				picture.setId(picId);
				picture.setData(album.getData());
				picture.setName(album.getName());
				picture.setOriginalName(album.getOriginalName());
				pictureService.editAlbum(picture,shopId);
			}else {
				model.addFlashAttribute("status",STATUS_FAILD);
				model.addFlashAttribute("msg", "上传的图片已存在，请勿重复上传");
				return "redirect:/shop/album/editPic/"+picId+"?shopId="+shopId;
			}
		}else {
			picture.setId(picId);
			pictureService.editAlbumWithOutFile(picture);
		}
		return "redirect:/shop/"+shopId+"/album";
	}
	@RequestMapping(value="/resetpwd", method=RequestMethod.GET)
	public String showPasswordReset(Model model){
		return "/shop/reset_pwd";
	}
	
	@RequestMapping(value="/resetpwd", method=RequestMethod.POST)
	public String processPasswordReset(@RequestParam("oldPwd") String oldPwd,
			@RequestParam("newPwd") String newPwd, Model model){
		
		model.addAttribute("status", false);
		
		if(StringUtils.isBlank(oldPwd) || StringUtils.isBlank(newPwd)){
			model.addAttribute("msg", "密码格式错误，请重新输入");
			return "/shop/reset_pwd";
		}else if(!EncryptionUtil.isStrongPassword(newPwd)){
			model.addAttribute("msg", "密码长度为6-32位字符， 须同时包含字母和数字");
			return "/shop/reset_pwd";
		}
		
		Account account = getAccount();
		String currentPwd = account.getPassword();
		try {
			String encodedOldPwd = FileMD5.getFileMD5String(oldPwd.getBytes());
			if(!currentPwd.equals(encodedOldPwd)){
				model.addAttribute("msg", "输入的原密码不正确");
				return "/shop/reset_pwd";
			}
			account.setPassword(FileMD5.getFileMD5String(newPwd.getBytes()));
			boolean result = accountService.updateAccount(account);
			if(result){
				model.addAttribute("status", true);
				model.addAttribute("msg", "密码修改成功！");
			}else{
				model.addAttribute("msg", "密码修改失败");
			}
			return "/shop/reset_pwd";
		} catch (Exception e) {
			model.addAttribute("msg", "密码修改失败");
			return "/shop/reset_pwd";
		}
	}
	
	@RequestMapping(value="/pos_terminal" ,method=RequestMethod.GET )
	public String showShopPosTerminal(Model model){
		Business business = super.getBusiness();
		if (business  instanceof Shop ) {
			List<Operator> operatorList = terminalService.findOperatorsByShopId(((Shop) business).getId());
			List<Terminal> terminalList = terminalService.findTerminalsByShopId(((Shop) business).getId());
			model.addAttribute("operatorList", operatorList);
			model.addAttribute("terminalList", terminalList);
			model.addAttribute("shopId", ((Shop) business).getId());
		}
		return "shop/shop_pos_terminal";
	}
	
	@AvoidDuplicateSubmission(addToken=true)
	@RequestMapping(value="/shop_printer/list",method = RequestMethod.GET)
	public String showShopPrinterList(Model model,Pager<ShopPrinter> pager) {
		Business business = getBusiness();
		if(BusinessType.SHOP.equals(business.getSelfBusinessType())) {
			pager = shopPrinterService.findShopPrintersByShopId(business.getSelfBusinessId(), pager);
			model.addAttribute("pager", pager);
		}else {
			model.addAttribute("status", "failed");
			model.addAttribute("msg", "只有商户才有权限");
		}
		return "shop/shop_printer_list";
	}
	
	@AvoidDuplicateSubmission(removeToken=true,errorRedirectURL="/shop/shop_printer/list")
	@RequestMapping(value="/shop_printer/add",method = RequestMethod.POST)
	public String addShopPrinter(Model model,ShopPrinter shopPrinter,RedirectAttributes redirecAttributes) {
		if(shopPrinter == null || StringUtils.isBlank(shopPrinter.getName()) || StringUtils.isBlank(shopPrinter.getIp())) {
			redirecAttributes.addFlashAttribute("status", "failed");
			redirecAttributes.addFlashAttribute("msg", "参数有误");
			return "redirect:/shop/shop_printer/list";
		}
		Business business = getBusiness();
		if(BusinessType.SHOP.equals(business.getSelfBusinessType())) {
			shopPrinter.setShopId(business.getSelfBusinessId());
			if(shopPrinterService.save(shopPrinter)) {
				redirecAttributes.addFlashAttribute("status", "success");
				redirecAttributes.addFlashAttribute("msg", "添加成功");
			}else {
				redirecAttributes.addFlashAttribute("status", "failed");
				redirecAttributes.addFlashAttribute("msg", "添加失败");
			}
		}else {
			redirecAttributes.addFlashAttribute("status", "failed");
			redirecAttributes.addFlashAttribute("msg", "只有商户才有权限查看");
		}
		return "redirect:/shop/shop_printer/list";
	}
	
	
	@RequestMapping(value="/shop_printer/edit",method = RequestMethod.POST)
	public String editShopPrinter(Model model,ShopPrinter shopPrinter,RedirectAttributes redirecAttributes) {
		Business business = getBusiness();
		if(shopPrinter == null 
				|| shopPrinter.getId() == null 
				|| StringUtils.isBlank(shopPrinter.getName()) 
				|| StringUtils.isBlank(shopPrinter.getIp())) {
			redirecAttributes.addFlashAttribute("status", "failed");
			redirecAttributes.addFlashAttribute("msg", "参数有误");
			return "redirect:/shop/shop_printer/list";
		}
		if(BusinessType.SHOP.equals(business.getSelfBusinessType())) {
			ShopPrinter sp =  shopPrinterService.findShopPrinterById(shopPrinter.getId());
			if(sp.getShopId().equals(getBusiness().getSelfBusinessId())) {
				if(shopPrinterService.update(shopPrinter)) {
					redirecAttributes.addFlashAttribute("status", "success");
					redirecAttributes.addFlashAttribute("msg", "修改成功");
				}else {
					redirecAttributes.addFlashAttribute("status", "failed");
					redirecAttributes.addFlashAttribute("msg", "修改失败");
				}
			}
		}else {
			redirecAttributes.addFlashAttribute("status", "failed");
			redirecAttributes.addFlashAttribute("msg", "只有商户才有权限查看");
		}
		return "redirect:/shop/shop_printer/list";
	}
	
	@RequestMapping(value="/shop_printer/{id}/update",method=RequestMethod.GET)
	public String updateShopPrinter(Model model,@PathVariable Long id,boolean enable,RedirectAttributes redirecAttributes) {
		if(id != null) {
			ShopPrinter shopPrinter =  shopPrinterService.findShopPrinterById(id);
			if(shopPrinter != null && shopPrinter.getId()!=null 
					&& BusinessType.SHOP.equals(getBusiness().getSelfBusinessType())
					&& shopPrinter.getShopId().equals(getBusiness().getSelfBusinessId())) {
				shopPrinter.setEnable(enable);
				if(shopPrinterService.update(shopPrinter)) {
					redirecAttributes.addFlashAttribute("status", "success");
					redirecAttributes.addFlashAttribute("msg", enable?"启用成功":"停用成功");
					return "redirect:/shop/shop_printer/list";
				}
			}
		}
		redirecAttributes.addFlashAttribute("status", "failed");
		redirecAttributes.addFlashAttribute("msg", enable?"启用成功":"停用成功");
		return "redirect:/shop/shop_printer/list";
	}
	
	@RequestMapping(value="/shop_printer/{id}/delete",method=RequestMethod.DELETE)
	public String deleteShopPrinter(Model model,@PathVariable Long id,RedirectAttributes redirectAttributes) {
		if(id != null) {
			ShopPrinter shopPrinter =  shopPrinterService.findShopPrinterById(id);
			if(shopPrinter != null && shopPrinter.getId()!=null 
					&& BusinessType.SHOP.equals(getBusiness().getSelfBusinessType())
					&& shopPrinter.getShopId().equals(getBusiness().getSelfBusinessId())) {
				shopPrinter.setStatus(0);//1可用0停用
				if(shopPrinterService.update(shopPrinter)) {
					redirectAttributes.addFlashAttribute("status", "success");
					redirectAttributes.addFlashAttribute("msg", "删除成功");
					return "redirect:/shop/shop_printer/list";
				}
			}
		}
		redirectAttributes.addFlashAttribute("status", "failed");
		redirectAttributes.addFlashAttribute("msg", "删除失败");
		return "redirect:/shop/shop_printer/list";
	}
	
	@RequestMapping(value="/shop_printer_service",method = RequestMethod.GET)
	public String showPrinterService(Model model) {
		if(getBusiness() == null || BusinessType.MERCHANT.equals(getBusiness().getSelfBusinessType())) {
			model.addAttribute("status", "failed");
			model.addAttribute("msg", "只有商户才有权限查看");
		}else {
			Shop shop  = shopService.findShopByIdIgnoreVisible(getBusiness().getSelfBusinessId());
			model.addAttribute("shop", shop);
		}
		return "shop/shop_printer_service";
	}
	
	@RequestMapping(value="/shop_printer_service",method = RequestMethod.POST)
	public String updatePrinterService(Model model,Shop shop) {
		if(getBusiness() == null || BusinessType.MERCHANT.equals(getBusiness().getSelfBusinessType())) {
			model.addAttribute("status", "failed");
			model.addAttribute("msg", "参数校验失败");
		}else if(shop.isPrinterEnable() && (StringUtils.isBlank(shop.getPrinterIp())
				|| StringUtils.isBlank(shop.getPrinterPort()))) {
			model.addAttribute("status", "failed");
			model.addAttribute("msg", "缺少必填项");
		}else {
			shop.setId(getBusiness().getSelfBusinessId());
			if(shopService.updateShopPrinterService(shop)) {
				model.addAttribute("status", "success");
				model.addAttribute("msg", "设置成功");
			}
		}
		return  showPrinterService(model);
	}

    @RequestMapping(value = "/pay_setting", method = RequestMethod.GET)
    public String settingPayShopDetail(RedirectAttributes redirect, Model model) {
        Business business = super.getBusiness();
        if (business instanceof Shop) {
            com.xkeshi.pojo.po.Shop shop = xShopService.findShopByShopId(((Shop) business).getId());
            model.addAttribute("enableMultiplePayment", shop.getEnableMultiplePayment());
            return "shop/multiple_payment_setting";
        } else {
            redirect.addFlashAttribute("status", STATUS_FAILD);
            redirect.addFlashAttribute("msg", "无法查看");
        }
        return "redirect:/shop/list";
    }

    @RequestMapping(value = "/pay_setting", method = RequestMethod.PUT)
    public String settingPayShop(@RequestParam("enableMultiplePayment")boolean enableMultiplePayment, RedirectAttributes redirect) {
        Business business = super.getBusiness();
        if (business instanceof  Shop) {

            com.xkeshi.pojo.po.Shop shop = new com.xkeshi.pojo.po.Shop();
            shop.setId(business.getSelfBusinessId());
            shop.setEnableMultiplePayment(enableMultiplePayment);

            Boolean updateShopByMultiplePayment = xShopService.updateShopByMultiplePayment(shop);

            if (updateShopByMultiplePayment) {
                super.storeSession(com.xkeshi.pojo.po.Shop.SHOP_ENABLE_MULTIPLE_PAYMENT, enableMultiplePayment);
                redirect.addFlashAttribute("status", STATUS_SUCCESS);
                redirect.addFlashAttribute("msg", "设置成功");
            }else{
                redirect.addFlashAttribute("status", STATUS_FAILD);
                redirect.addFlashAttribute("msg", "设置失败");
            }
            return "redirect:/shop/pay_setting";
        } else {
            redirect.addFlashAttribute("status", STATUS_FAILD);
            redirect.addFlashAttribute("msg", "无法修改");
        }
        return "redirect:/shop/list";

    }

    @RequestMapping(value="/discount/update/{discountWayNameId}", method=RequestMethod.GET)
    public String discountUpdate(@PathVariable Long discountWayNameId, ShopDiscountSettingParam param){
        param.setDiscountWayNameId(discountWayNameId);
        discountWayService.discountUpdate(param);
        return "redirect:/shop/" + param.getShopId() + "/pos";
    }

    @RequestMapping(value="/payment/update", method=RequestMethod.GET)
    public String paymentUpdate(ShopInfoParam param){
        ShopInfo shopInfo = new ShopInfo();
        shopInfo.setShopId(param.getShopId());
        shopInfo.setEnableCash(param.getEnableCash());
        shopService.saveOrUpdateShopInfoCash(shopInfo);
        return "redirect:/shop/" + param.getShopId() + "/pos";
    }

    @RequestMapping(value="/{shopId}/payment/update", method=RequestMethod.POST)
    public String updatePaymentByType(@PathVariable Long shopId, POSGatewayAccountParam param,RedirectAttributes redirect){
        String url = StringUtils.join("redirect:/shop/",param.getShopId(),"/pos");
        //后台验证用户填写商户号信息是否规范
        if(StringUtils.isEmpty(param.getAccount()) || param.getAccount().length()>120){
            redirect.addFlashAttribute("status", STATUS_FAILD);
            redirect.addFlashAttribute("msg", "设置失败，商户号为120字符以内的字符串");
            return url;
        }
        //后台验证用户填写校验码信息是否规范
        if(StringUtils.isEmpty(param.getSignKey()) || param.getSignKey().length()>120){
            redirect.addFlashAttribute("status", STATUS_FAILD);
            redirect.addFlashAttribute("msg", "设置失败，校验码为120字符以内的字符串");
            return url;
        }
        posGatewayAccountService.saveOrUpdate(param);
        return url;
    }

    @RequestMapping(value="/{shopId}/payment/updateBandCard", method=RequestMethod.POST)
    public String updateBandCard(@PathVariable Long shopId, POSGatewayAccountParam param,RedirectAttributes redirect){
        String url = StringUtils.join("redirect:/shop/",param.getShopId(),"/pos");
        //后台验证用户填写商户号信息是否规范
        if(StringUtils.isEmpty(param.getAccount()) || param.getAccount().length()>120){
            redirect.addFlashAttribute("status", STATUS_FAILD);
            redirect.addFlashAttribute("msg", "设置失败，商户号为120字符以内的字符串");
            return url;
        }
        //后台验证用户填写校验码信息是否规范
        if(StringUtils.isEmpty(param.getSignKey()) || param.getSignKey().length()>120){
            redirect.addFlashAttribute("status", STATUS_FAILD);
            redirect.addFlashAttribute("msg", "设置失败，校验码为120字符以内的字符串");
            return url;
        }
        //后台验证用户填写终端号信息是否规范
        if(StringUtils.isEmpty(param.getTerminal()) || param.getTerminal().length()>120){
            redirect.addFlashAttribute("status", STATUS_FAILD);
            redirect.addFlashAttribute("msg", "设置失败，终端号为120字符以内的字符串");
            return url;
        }
        posGatewayAccountService.updateBandCard(param);
        return url;
    }

    @RequestMapping(value="/validate", method=RequestMethod.GET)
    public HttpEntity<String> validateFullName(String fullName,String shopId){
        boolean result = shopService.validateFullNameIsExist(fullName,shopId);
        JSONResponse  jsonResponse = new JSONResponse(result ? new GrantResult(SUCCESS, "存在相同的商户全称") : new GrantResult(FAILD, "不存在相同的商户全称"));
        return new ResponseEntity<String>( jsonResponse.getBody(), jsonResponse.getHttpStatus()) ;
    }

}
