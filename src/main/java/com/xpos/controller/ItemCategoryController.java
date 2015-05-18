package com.xpos.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.xkeshi.endpoint.GrantResult;
import com.xkeshi.endpoint.JSONResponse;
import com.xkeshi.interceptor.form.AvoidDuplicateSubmission;
import com.xpos.common.entity.ItemCategory;
import com.xpos.common.entity.Merchant;
import com.xpos.common.entity.Shop;
import com.xpos.common.entity.face.Business;
import com.xpos.common.service.ItemService;
import com.xpos.common.service.ShopService;
import com.xpos.common.utils.IDUtil;

@Controller
@RequestMapping("item")
public class ItemCategoryController extends BaseController{
	
	@Resource
	private ItemService itemService;
	
	@Resource
	private ShopService shopService;

	/**
	 * 普通商户查看商品分类
	 */
	@AvoidDuplicateSubmission(addToken=true)
	@RequestMapping(value="/category", method=RequestMethod.GET)
	public String listCategory(Model model){
		List<ItemCategory> categories = itemService.findCategoryByBusiness(getBusiness());
		model.addAttribute("categories", categories);
		return "item/item_category_list";
	}
	/**
	 * 普通商户提交 添加或修改商品分类
	 */
	@AvoidDuplicateSubmission(removeToken = true, errorRedirectURL = "/item/category")
	@RequestMapping(value = "/category", method = RequestMethod.POST)
	public String addCategory(@Valid ItemCategory itemCategory,
			BindingResult result, RedirectAttributes redirectAttributes) {

		if (result.hasErrors()) {
			redirectAttributes.addFlashAttribute("error", result.getFieldError().getDefaultMessage());
			return "redirect:/item/category";
		}

		itemCategory.setBusiness(getBusiness());

		if (itemService.isDuplicateItemCategorysByNameAndBusiness(itemCategory,getBusiness())) {
			redirectAttributes.addFlashAttribute("error", "类目已存在!");
		} else {
			Long itemCategoryId = itemCategory.getId();
			boolean res = itemService.addOrUpdateCategory(itemCategory);
			if(res){
				 redirectAttributes.addFlashAttribute("success", itemCategoryId == null ? "添加类目成功!":"修改类目成功！");
			}else{
				redirectAttributes.addFlashAttribute("error",    itemCategoryId == null ? "添加类目失败!" : "修改类目失败！");
			}

		}
		return "redirect:/item/category";
	}
	
	/**
	 * 普通商户删除旗下的商品分类
	 * @throws Exception 
	 */
	@RequestMapping(value="/category/{id}", method=RequestMethod.DELETE)
	public HttpEntity<String> deleteCategory(@PathVariable Long id, Model model){
		GrantResult grantResult = new GrantResult(FAILD, "删除失败,请重试");
		ItemCategory category = itemService.findCategoryById(id);
		//check permission
		if(getBusiness().getAccessBusinessType(Business.BusinessModel.MENU).equals(category.getBusinessType()) 
				&& getBusiness().getAccessBusinessId(Business.BusinessModel.MENU).equals(category.getBusinessId())){
			boolean result;
			try {
				result = itemService.deleteCategory(category);
				if(result)
				 grantResult = new GrantResult(SUCCESS, "删除成功");
			} catch (Exception e) {
				 grantResult = new GrantResult(FAILD, e.getMessage());
			}
		}
		  JSONResponse jsonResponse = new JSONResponse(grantResult);
		  return new ResponseEntity<String>( jsonResponse.getBody(), jsonResponse.getHttpStatus()) ;
	}
	
	
	/**
	 * 集团账号登陆查看“收银->分类管理”菜单
	 * @param nickName 商户简称
	 */
	@RequestMapping(value="/merchant/category", method=RequestMethod.GET)
	public String merchentCategoryList(String nickName, Model model){
		if(getBusiness() instanceof Merchant){
			//遍历集团下的子商户
			List<Shop> fullShops = shopService.findShopListByMerchantId(getBusiness().getSelfBusinessId(), true);
			List<Shop> subShops = new ArrayList<>();
			Map<String, Integer>  shopByCategoryCount  =  new HashMap<String, Integer>();
			for(Shop shop : fullShops){
				if(shop != null){
					if(StringUtils.isNotBlank(nickName) && (shop.getName()).indexOf(nickName) == -1){
						continue;
					}
					List<ItemCategory> categories = itemService.findCategoryByBusiness(shop);
					subShops.add(shop);
					shopByCategoryCount.put(shop.getId()+"", categories.size());
				}
			}
			Collections.sort(subShops, new Comparator<Shop>(){ //按子商户添加的顺序最新到最老排序
				@Override
				public int compare(Shop s1, Shop s2) {
					return s1.getCreateDate().getTime() - s2.getCreateDate().getTime() > 0 ? -1 : 1;
				}
			});
			model.addAttribute("subShops", subShops);
			model.addAttribute("shopByCategoryCount", shopByCategoryCount);
			model.addAttribute("nickName", nickName);
			return "item/merchant_category_list";
		}
		return null;
	}
 
	/** 集团账号查看子商户的分类管理目录 */
	@AvoidDuplicateSubmission(addToken=true )
	@RequestMapping(value="/merchant/{shopEid}/cateList", method=RequestMethod.GET)
	public String viewCateListByMerchant(@PathVariable("shopEid")String eid, Model model){
		if(getBusiness() instanceof Merchant){
			Long id = IDUtil.decode(eid);
			Shop shop = shopService.findShopByIdIgnoreVisible(id);
			if(shop == null || shop.getMerchant() == null || !shop.getMerchant().getId().equals(getBusiness().getSelfBusinessId())){
				return null;
			}
			List<ItemCategory> categories = itemService.findCategoryByBusiness(shop);
			model.addAttribute("categories", categories);
			model.addAttribute("shop", shop);
		}
		return "item/merchant_subShop_category_list";
	}
	
	/** 集团账号维护子商户分类 */
	@AvoidDuplicateSubmission(removeToken = true ,errorRedirectURL = "/item/merchant/{shopEid}/cateList")
	@RequestMapping(value="/merchant/{shopEid}/category", method=RequestMethod.POST)
	public String addCategory(@PathVariable("shopEid") String eid, @Valid ItemCategory itemCategory, BindingResult result, RedirectAttributes  model){
		if(getBusiness() instanceof Merchant){
			Long id = IDUtil.decode(eid);
			Shop shop = shopService.findShopByIdIgnoreVisible(id);
			if(shop == null || shop.getMerchant() == null || !shop.getMerchant().getId().equals(getBusiness().getSelfBusinessId())){
				return "redirect:/404";
			}
			if(result.hasErrors()){
				model.addFlashAttribute("error", result.getFieldErrors());
				return "item/merchant_subShop_category_list";
			}
			itemCategory.setBusiness(shop);
			if (itemService.isDuplicateItemCategorysByNameAndBusiness(itemCategory,shop)) {
				model.addFlashAttribute("error", "类目已存在!");
			} else {
				Long itemCategoryId = itemCategory.getId();
				boolean updateCategory = itemService.addOrUpdateCategory(itemCategory);
				if(updateCategory){
					model.addFlashAttribute("success", itemCategoryId == null ? "添加类目成功!":"修改类目成功！");
				}else{
					model.addFlashAttribute("error",   itemCategoryId == null ? "添加类目失败!":"修改类目失败！");
				}
			}
		}
		return  "redirect:/item/merchant/"+eid+"/cateList";
	}
	
	/** 集团账号删除子商户下的类目 */
	@RequestMapping(value="/merchant/{shopEid}/category/{cateId}", method=RequestMethod.DELETE)
	public HttpEntity<String> deleteCategory(@PathVariable("shopEid") String eid, @PathVariable("cateId") Long cateId, Model model){
		 GrantResult grantResult = new GrantResult(FAILD, "删除失败");
		if(getBusiness() instanceof Merchant){
			Long id = IDUtil.decode(eid);
			Shop shop = shopService.findShopByIdIgnoreVisible(id);
			if(shop.getMerchant() != null &&  shop.getMerchant().getId().equals(getBusiness().getSelfBusinessId())){
				ItemCategory category = itemService.findCategoryById(cateId);
				//check
				if(category.getBusinessId().equals(shop.getAccessBusinessId(Business.BusinessModel.MENU))){
					try {
						if(itemService.deleteCategory(category))
							grantResult = new GrantResult(SUCCESS, "删除成功");
					} catch (Exception e) {
						grantResult = new GrantResult(FAILD, e.getMessage());
					}
				}
			}
			
		}
		JSONResponse jsonResponse = new JSONResponse(grantResult);
	    return new ResponseEntity<String>( jsonResponse.getBody(), jsonResponse.getHttpStatus()) ;
	}
}
