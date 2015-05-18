package com.xpos.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.xkeshi.endpoint.GrantResult;
import com.xkeshi.endpoint.JSONResponse;
import com.xkeshi.interceptor.form.AvoidDuplicateSubmission;
import com.xpos.common.entity.Item;
import com.xpos.common.entity.ItemCategory;
import com.xpos.common.entity.Merchant;
import com.xpos.common.entity.Picture;
import com.xpos.common.entity.Shop;
import com.xpos.common.entity.ShopPrinter;
import com.xpos.common.entity.face.Business;
import com.xpos.common.searcher.ItemSearcher;
import com.xpos.common.service.ItemService;
import com.xpos.common.service.PictureService;
import com.xpos.common.service.ShopPrinterService;
import com.xpos.common.service.ShopService;
import com.xpos.common.utils.IDUtil;
import com.xpos.common.utils.Pager;

@Controller
@RequestMapping("item")
public class ItemController extends BaseController{
	
	@Autowired
	private ItemService itemService;
	
	@Autowired
	private PictureService pictureService;
	
	@Autowired
	private ShopService shopService;
	
	@Autowired
	private ShopPrinterService shopPrinterService;

	/**
	 * 普通商户展示旗下商品列表
	 */
	@RequestMapping(value="/list", method=RequestMethod.GET)
	public String itemList(ItemSearcher searcher, Pager<Item> pager,  Model model){
		searcher.setBusiness(getBusiness());
		pager = itemService.searchItems(searcher, pager);
		List<ItemCategory> categories = itemService.findCategoryByBusiness(getBusiness());
		List<ShopPrinter> printers =  shopPrinterService.findShopPrintersByShopId(getBusiness().getSelfBusinessId(), null).getList();
		model.addAttribute("printers", printers);
		model.addAttribute("searcher", searcher);
		model.addAttribute("items", pager);
		model.addAttribute("categories", categories);
		return "item/item_list";
	}
	
	/**
	 * 普通商户删除旗下商品
	 */
	@RequestMapping(value="/{id}", method=RequestMethod.DELETE)
	public HttpEntity<String> deleteItem(@PathVariable Long id, RedirectAttributes  redirectAttributes){
		Item item = itemService.findItemById(id);
		GrantResult grantResult = new GrantResult(FAILD, "删除失败");
		//check permission
		if(item != null 
				&& getBusiness().getAccessBusinessType(Business.BusinessModel.MENU).equals(item.getBusinessType()) 
				&& getBusiness().getAccessBusinessId(Business.BusinessModel.MENU).equals(item.getBusinessId())){
			boolean result = itemService.deleteItem(item);
			if(result)
				  grantResult = new GrantResult(SUCCESS, "删除成功");
		}
		 JSONResponse jsonResponse = new JSONResponse(grantResult);
	     return new ResponseEntity<String>( jsonResponse.getBody(), jsonResponse.getHttpStatus());
	}
	
	
	/**
	 * 普通商户跳转至添加商品页面
	 */
	@AvoidDuplicateSubmission(addToken=true)
	@RequestMapping(value="/input", method=RequestMethod.GET)
	public String showAddItem(Model model){
		List<ShopPrinter> printers =  shopPrinterService.findShopPrintersByShopId(getBusiness().getSelfBusinessId(), null).getList();
		List<ItemCategory> categories = itemService.findCategoryByBusiness(getBusiness());
		model.addAttribute("categories", categories);
		model.addAttribute("printers", printers);
		return "item/item_input";
	}
	
	/**
	 * 普通商户跳转至修改商品页面
	 */
	@RequestMapping(value="/edit/{eid}", method=RequestMethod.GET)
	public String showShopEditItem(@PathVariable("eid") String eid, Model model){
		Long id = IDUtil.decode(eid);
		Item item = itemService.findItemById(id);
		//check permission
		if(item != null 
				&& getBusiness().getAccessBusinessType(Business.BusinessModel.MENU).equals(item.getBusinessType()) 
				&& getBusiness().getAccessBusinessId(Business.BusinessModel.MENU).equals(item.getBusinessId())){
			List<ItemCategory> categories = itemService.findCategoryByBusiness(getBusiness());
			List<ShopPrinter> printers =  shopPrinterService.findShopPrintersByShopId(getBusiness().getSelfBusinessId(), null).getList();
			model.addAttribute("item", item);
			model.addAttribute("categories", categories);
			model.addAttribute("printers", printers);
		}
		return "item/item_input";
	}
	
	/**
	 * 普通商户提交商品修改
	 */
	@RequestMapping(value="/edit", method=RequestMethod.POST)
	public String editAddItem(Item item, MultipartFile coverFile,RedirectAttributes redirectAttributes, Model model){
		if(item.getPrice()==null || item.getName()==null || item.getCategory() == null) {
			redirectAttributes.addFlashAttribute("status",STATUS_FAILD);
		    redirectAttributes.addFlashAttribute("msg", "保存失败，请查看必填项是否完整");
		    return "redirect:/item/list";
		}
		
		if(coverFile != null && !coverFile.isEmpty()){
			Picture cover = pictureService.getPictureFromMultipartFile(coverFile);
			item.setCover(cover);	
		}
		item.setBusiness(getBusiness());
		//check permission
		if(getBusiness().getAccessBusinessType(Business.BusinessModel.MENU).equals(item.getBusinessType()) 
				&& getBusiness().getAccessBusinessId(Business.BusinessModel.MENU).equals(item.getBusinessId())){
			boolean updateItem = itemService.updateItem(item);
			if (updateItem) {
				redirectAttributes.addFlashAttribute("status",STATUS_SUCCESS);
				redirectAttributes.addFlashAttribute("msg", "修改成功");
			}else{
				model.addAttribute("status",STATUS_FAILD);
				model.addAttribute("msg", "修改失败了");
				model.addAttribute("item", item);
				List<ItemCategory> categories = itemService.findCategoryByBusiness(getBusiness());
				List<ShopPrinter> printers =  shopPrinterService.findShopPrintersByShopId(getBusiness().getSelfBusinessId(), null).getList();
				model.addAttribute("categories", categories);
				model.addAttribute("printers", printers);
				return "item/item_input";
			}
		}else {
			redirectAttributes.addFlashAttribute("status",STATUS_FAILD);
			redirectAttributes.addFlashAttribute("msg", "修改失败了，请检查登陆账号权限");
		}
			return "redirect:/item/list";
	}
	
	
	
	/**
	 * 普通商户提交添加商品
	 */
	@AvoidDuplicateSubmission(removeToken=true,errorRedirectURL="/item/list")
	@RequestMapping(value="", method=RequestMethod.POST)
	public String addItem(Item item, MultipartFile coverFile,RedirectAttributes redirectAttributes, Model model){
		if(item.getPrice()==null || item.getName()==null || item.getCategory() == null) {
			redirectAttributes.addFlashAttribute("status",STATUS_FAILD);
		    redirectAttributes.addFlashAttribute("msg", "保存失败，请查看必填项是否完整");
		    return "redirect:/item/list";
		}
		if(coverFile != null && !coverFile.isEmpty()){
			Picture cover = pictureService.getPictureFromMultipartFile(coverFile);
			item.setCover(cover);	
		}
		item.setBusiness(getBusiness());
		boolean addItem = false;
		try{
			addItem = itemService.addItem(item);
			if (addItem) {
				redirectAttributes.addFlashAttribute("status",STATUS_SUCCESS);
				redirectAttributes.addFlashAttribute("msg", "添加成功");
			}else{
				redirectAttributes.addFlashAttribute("status",STATUS_FAILD);
				redirectAttributes.addFlashAttribute("msg", "添加失败了");
				redirectAttributes.addAttribute("item", item);
				return showAddItem(model);
			}
		}catch(Exception e){
			model.addAttribute("status",STATUS_FAILD);
			model.addAttribute("msg", "添加失败了");
			model.addAttribute("item", item);
			return showAddItem(model);
		}
		return "redirect:/item/list";
	}
	
	/**
	 * 集团账号登陆查看“收银->商品管理”菜单
	 * @param nickName 商户简称
	 */
	@RequestMapping(value="/merchant/list", method=RequestMethod.GET)
	public String merchantSummaryList(String nickName, Pager<Item> pager, Model model){
		if(getBusiness() instanceof Merchant){
			//遍历集团下的子商户
			List<Shop> fullShops = shopService.findShopListByMerchantId(getBusiness().getSelfBusinessId(), true);
			List<Shop> subShops = new ArrayList<>();
			Map<String, Integer>  shopByItemCount  =  new HashMap<String, Integer>();
			for(Shop shop : fullShops){
				if(shop != null){
					if(StringUtils.isNotBlank(nickName) &&  (shop.getName()).indexOf(nickName) == -1){
						continue;
					}
					Pager<Item> countPager = new Pager<>();
					ItemSearcher searcher = new ItemSearcher();
					searcher.setBusiness(shop);
					countPager = itemService.searchItems(searcher, countPager);
					subShops.add(shop);
					shopByItemCount.put(shop.getId()+"",countPager.getTotalCount() );
				}
			}
			Collections.sort(subShops, new Comparator<Shop>(){ //按子商户添加的顺序最新到最老排序
				@Override
				public int compare(Shop s1, Shop s2) {
					return s1.getCreateDate().getTime() - s2.getCreateDate().getTime() > 0 ? -1 : 1;
				}
			});
            model.addAttribute("shopByItemCount", shopByItemCount);
            model.addAttribute("subShops", subShops);
			model.addAttribute("nickName", nickName);
			return "item/merchant_item_list";
		}
		return null;
	}
	
	/** 集团账号查看子商户的商品管理目录 */
	@RequestMapping(value="/merchant/{shopEid}/list", method=RequestMethod.GET)
	public String viewListByMerchant(@PathVariable("shopEid")String eid, ItemSearcher searcher, Pager<Item> pager,  Model model){
		Long id = IDUtil.decode(eid);
		Shop shop = shopService.findShopByIdIgnoreVisible(id);
		if(shop == null || shop.getMerchant() == null || !shop.getMerchant().getId().equals(getBusiness().getSelfBusinessId())){
			return null;
		}
		List<ItemCategory> categories = itemService.findCategoryByBusiness(shop);
		searcher.setBusiness(shop);
		Pager<Item> items = itemService.searchItems(searcher, pager);
		List<ShopPrinter> printers =  shopPrinterService.findShopPrintersByShopId(shop.getId(), null).getList();
		model.addAttribute("printers", printers);
		model.addAttribute("searcher", searcher);
		model.addAttribute("items", items);
		model.addAttribute("shop", shop);
		model.addAttribute("categories", categories);
		
		
		return "item/merchant_subShop_item_list";
	}
	
	/** 集团账户创建下属子商户商品 */
	@AvoidDuplicateSubmission(addToken= true)
	@RequestMapping(value="/merchant/{shopEid}/add", method=RequestMethod.GET)
	public String showEditAddItem(@PathVariable("shopEid") String eid, Model model ){
		Long id = IDUtil.decode(eid);
		Shop shop = shopService.findShopByIdIgnoreVisible(id);
		if(shop == null || shop.getMerchant() == null || !shop.getMerchant().getId().equals(getBusiness().getSelfBusinessId())){
			return "redirect:/404";
		}
		List<ItemCategory> categories = itemService.findCategoryByBusiness(shop);
		List<ShopPrinter> printers =  shopPrinterService.findShopPrintersByShopId(shop.getId(), null).getList();
		model.addAttribute("printers", printers);
		model.addAttribute("categories", categories);
		model.addAttribute("shop", shop);
		return "item/merchant_item_input";
	}
	
	/** 集团账户创建下属子商户商品 */
	@AvoidDuplicateSubmission(removeToken = true  ,  errorRedirectURL = "/item/merchant/{shopEid}/list")
	@RequestMapping(value="/merchant/{shopEid}/add", method=RequestMethod.POST)
	public String addItemByMerchant(Item item, @PathVariable("shopEid")String eid, MultipartFile coverFile, RedirectAttributes redirectAttributes){
		if(item.getPrice()==null || item.getName()==null || item.getCategory() == null) {
			redirectAttributes.addFlashAttribute("status",STATUS_FAILD);
		    redirectAttributes.addFlashAttribute("msg", "保存失败，请查看必填项是否完整");
		    return "redirect:/item/merchant/"+eid+"/list";
		}
		
		Long id = IDUtil.decode(eid);
		Shop shop = shopService.findShopByIdIgnoreVisible(id);
		if(shop == null || shop.getMerchant() == null || !shop.getMerchant().getId().equals(getBusiness().getSelfBusinessId())){
			redirectAttributes.addFlashAttribute("status",STATUS_FAILD);
		    redirectAttributes.addFlashAttribute("msg", "商户信息错误");
		    return "redirect:/item/merchant/"+eid+"/list";
		}
		
		if(coverFile != null && !coverFile.isEmpty()){
			Picture cover = pictureService.getPictureFromMultipartFile(coverFile);
			item.setCover(cover);	
		}
		item.setBusiness(shop);
		boolean saveItem = false;
		if(item.getId() == null || item.getId() <= 0){
			try {
				saveItem = itemService.addItem(item);
			} catch (Exception e) {
				redirectAttributes.addFlashAttribute("status",STATUS_FAILD);
				redirectAttributes.addFlashAttribute("msg", e.getMessage());
			}
		}
		if (saveItem) {
			redirectAttributes.addFlashAttribute("status",STATUS_SUCCESS);
			redirectAttributes.addFlashAttribute("msg", "添加成功");
		}else{
			redirectAttributes.addFlashAttribute("status",STATUS_FAILD);
			redirectAttributes.addFlashAttribute("msg", "添加失败");
		}
			return "redirect:/item/merchant/"+eid+"/list";
	}
	
	
	/** 集团账户修改下属子商户商品 */
	@RequestMapping(value="/merchant/{shopEid}/edit/{id}", method=RequestMethod.GET)
	public String showEditAddItem(@PathVariable("shopEid") String eid, @PathVariable(value="id") Long itemId, Model model){
		Long id = IDUtil.decode(eid);
		Shop shop = shopService.findShopByIdIgnoreVisible(id);
		if(shop == null || shop.getMerchant() == null || !shop.getMerchant().getId().equals(getBusiness().getSelfBusinessId())){
			return null;
		}
		if(itemId != null && itemId > 0){
			Item item = itemService.findItemById(itemId);
			if(item != null 
					&& shop.getAccessBusinessType(Business.BusinessModel.MENU).equals(item.getBusinessType()) 
					&& shop.getAccessBusinessId(Business.BusinessModel.MENU).equals(item.getBusinessId())){
				model.addAttribute("item", item);
			}
		}
		List<ItemCategory> categories = itemService.findCategoryByBusiness(shop);
		List<ShopPrinter> printers =  shopPrinterService.findShopPrintersByShopId(shop.getId(), null).getList();
		model.addAttribute("printers", printers);
		model.addAttribute("categories", categories);
		model.addAttribute("shop", shop);
		return "item/merchant_item_input";
	}
	
	@RequestMapping(value="/merchant/edit", method=RequestMethod.POST)
	public String editAddItem(Item item, @RequestParam("shopEid")String eid, MultipartFile coverFile, RedirectAttributes redirectAttributes){
		if(item.getPrice()==null || item.getName()==null || item.getCategory() == null) {
			redirectAttributes.addFlashAttribute("status",STATUS_FAILD);
		    redirectAttributes.addFlashAttribute("msg", "保存失败，请查看必填项是否完整");
		    return "redirect:/item/merchant/"+eid+"/list";
		}
		
		Long id = IDUtil.decode(eid);
		Shop shop = shopService.findShopByIdIgnoreVisible(id);
		if(shop == null || shop.getMerchant() == null || !shop.getMerchant().getId().equals(getBusiness().getSelfBusinessId())){
			return null;
		}
		
		if(coverFile != null && !coverFile.isEmpty()){
			Picture cover = pictureService.getPictureFromMultipartFile(coverFile);
			item.setCover(cover);	
		}
		item.setBusiness(shop);
		boolean updateItem = false;
		if(item.getId() == null || item.getId() <= 0){
			try {
				updateItem = itemService.addItem(item);
			} catch (Exception e) {
				redirectAttributes.addFlashAttribute("status",STATUS_FAILD);
				redirectAttributes.addFlashAttribute("msg", e.getMessage());
			}
		}else{
			updateItem = itemService.updateItem(item);
		}
		if (updateItem) {
			redirectAttributes.addFlashAttribute("status",STATUS_SUCCESS);
			redirectAttributes.addFlashAttribute("msg", "修改成功");
		}else{
			redirectAttributes.addFlashAttribute("status",STATUS_FAILD);
			redirectAttributes.addFlashAttribute("msg", "修改失败了");
		}
			return "redirect:/item/merchant/"+eid+"/list";
	}
	
	/** 集团账户删除下属子商户商品 */
	@RequestMapping(value="/merchant/{shopEid}/{id}", method=RequestMethod.DELETE)
	public HttpEntity<String> deleteItemByMerchant(@PathVariable("shopEid")String eid, @PathVariable("id") Long itemId, Model model){
		Long id = IDUtil.decode(eid);
		Shop shop = shopService.findShopByIdIgnoreVisible(id);
		if(shop.getMerchant() == null || !shop.getMerchant().getId().equals(getBusiness().getSelfBusinessId())){
			return null;
		}
		GrantResult grantResult = new GrantResult(FAILD, "请删除失败");
		Item item = itemService.findItemById(itemId);
		//check permission
		if(item != null 
				&& shop.getAccessBusinessType(Business.BusinessModel.MENU).equals(item.getBusinessType())
				&& shop.getAccessBusinessId(Business.BusinessModel.MENU).equals(item.getBusinessId())){
			boolean result = itemService.deleteItem(item);
			if(result){
				grantResult = new GrantResult(SUCCESS, "请删除成功");
			} 
		}
		 JSONResponse jsonResponse = new JSONResponse(grantResult);
	     return new ResponseEntity<String>( jsonResponse.getBody(), jsonResponse.getHttpStatus());
	}
}
