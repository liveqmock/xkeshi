package com.xpos.controller;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.xkeshi.interceptor.form.AvoidDuplicateSubmission;
import com.xkeshi.pojo.vo.physicalCoupon.PhysicalCouponWriteOffVO;
import com.xpos.common.entity.Shop;
import com.xpos.common.entity.face.Business;
import com.xpos.common.entity.face.Business.BusinessType;
import com.xpos.common.entity.physicalCoupon.PhysicalCoupon;
import com.xpos.common.entity.physicalCoupon.PhysicalCouponShop;
import com.xpos.common.searcher.physicalCoupon.PhysicalCouponOrderSearcher;
import com.xpos.common.searcher.physicalCoupon.PhysicalCouponSearcher;
import com.xpos.common.service.PhysicalCouponService;
import com.xpos.common.service.ShopService;
import com.xpos.common.utils.Pager;

@Controller
@RequestMapping("physical_coupon")
public class PhysicalCouponController extends BaseController{
	
	@Autowired
	private PhysicalCouponService physicalCouponService;
	
	@Autowired
	private ShopService shopService;
	
	/**
	 * 实体券核销明细
	 */
	@RequestMapping(value="/used/list", method=RequestMethod.GET)
	public String usedPhysicalCouponList(Model model, PhysicalCouponOrderSearcher searcher,Pager<PhysicalCouponWriteOffVO> pager){
		//1.校验权限
		Business business = getBusiness();
		if(business == null ) {//管理员登陆
			return null;
		}
		Long shopId = null;
		if(BusinessType.MERCHANT.equals(business.getSelfBusinessType())) {//集团登陆,查看旗下所有商户的实体券核销记录
			if(searcher.getShopIds()!=null && searcher.getShopIds().length==1) {//已传入指定核销商户信息
				shopId = searcher.getShopIds()[0];
			}else {//未传入指定核销商户信息
				Long[] shopIds = shopService.findShopIdsByMerchantId(business.getSelfBusinessId(), true);
				if(shopIds!=null && shopIds.length>0) {
					searcher.setShopIds(shopIds);
				}else {//集团下无商户
					model.addAttribute("status", "failed");
					model.addAttribute("msg", "集团下没有子商户,无法查看实体券");
					return "statistics/physical_coupon/used_list";
				}
			}
			List<Shop> shopResourceList = shopService.findShopListByMerchantId(business.getSelfBusinessId(), true);
			model.addAttribute("shopResourceList", shopResourceList);
		}else if(BusinessType.SHOP.equals(business.getSelfBusinessType())) {//普通商户登陆，查看自己的实体券核销记录
			searcher.setShopIds(new Long []{business.getSelfBusinessId()});
		}
		pager = physicalCouponService.getOrderPhysicalCouponList(searcher, pager);
		int orderCount = physicalCouponService.findCountOrderPhysicalCoupon(searcher) ;//核销的订单数量
		int usedCount =pager.getTotalCount(); //physicalCouponService.getOrderPhysicalCouponList(searcher, null).getList().size();//核销的实体券数量
		BigDecimal totalAmount = physicalCouponService.orderPhysicalCouponTotalAmount(searcher);//核销总金额
		if(shopId!=null) {
			searcher.setShopIds(new Long[]{shopId});
		}else {
			searcher.setShopIds(null);
		}
		model.addAttribute("searcher", searcher);
		model.addAttribute("orderCount", orderCount);
		model.addAttribute("usedCount", usedCount);
		model.addAttribute("totalAmount", totalAmount);
		model.addAttribute("pager", pager);
		return "statistics/physical_coupon/used_list";
	}
	
	/**
	 * 集团查看实体券列表
	 */
	@RequestMapping(value="/merchant/list",method=RequestMethod.GET)
	public String physicalCouponMerchantList( Model model,PhysicalCouponSearcher searcher,Pager<PhysicalCoupon> pager) {
		Business business = getBusiness();
		if(business == null || BusinessType.SHOP.equals(business.getSelfBusinessType())) {//管理员或普通商户登陆
			return null;
		}else if(BusinessType.MERCHANT.equals(business.getSelfBusinessType())) {//集团登陆,查看自己创建的实体券和旗下商户创建的实体券
			Long shopId = null;
			if(searcher.getShopIds() != null && searcher.getShopIds().length==1) {//限定适用商户
				shopId = searcher.getShopIds()[0];
			}else {
				Long[] shopIds = shopService.findShopIdsByMerchantId(business.getSelfBusinessId(), true);
				if(shopIds!=null && shopIds.length>0) {
					searcher.setShopIds(shopIds);
				}else {//集团下没有商户
					model.addAttribute("status", "failed");
					model.addAttribute("msg", "集团下没有子商户,无法查看实体券");
					return "physical_coupon/merchant_physical_coupon_list";
				}
			}
			if(searcher.getBusinessId()!=null) {
				if(searcher.getBusinessId()==-1L) {
					searcher.setBusinessType(BusinessType.MERCHANT);
					searcher.setBusinessId(business.getSelfBusinessId());
				}else {
					searcher.setBusinessType(BusinessType.SHOP);
				}
			}
			pager = physicalCouponService.findPhysicalCouponList(searcher,pager);
			//来源商户列表(页面第一项添加集团)，适用商户列表
			List<Shop> shopResourceList = shopService.findShopListByMerchantId(business.getSelfBusinessId(), true);
			if(shopId!=null) {
				searcher.setShopIds(new Long[]{shopId});
			}else {
				searcher.setShopIds(null);
			}
			model.addAttribute("shopResourceList", shopResourceList);
			model.addAttribute("pager", pager);
			model.addAttribute("searcher", searcher);
			return "physical_coupon/merchant_physical_coupon_list";
		}
		return null;
	}
	
	/**
	 * 普通商户查看实体券列表
	 */
	@RequestMapping(value="/shop/list",method=RequestMethod.GET)
	public String physicalCouponShopList(Model model,PhysicalCouponSearcher searcher,Pager<PhysicalCoupon> pager) {
		Business business = getBusiness();
		if(business == null || BusinessType.MERCHANT.equals(business.getSelfBusinessType())) {//管理员或集团登陆
			model.addAttribute("status", "falied");
			model.addAttribute("msg", "权限校验失败");
			return "physical_coupon/shop_physical_coupon_list";
		}else if(BusinessType.SHOP.equals(business.getSelfBusinessType())) {//普通商户登陆,查看自己创建的实体券和所在集团的实体券列表
			searcher.setShopIds(new Long[]{business.getSelfBusinessId()});
			Shop shop = shopService.findShopByIdIgnoreVisible(business.getSelfBusinessId());
			if(searcher.getBusinessId()!=null) {
				if(searcher.getBusinessId()==-1L) {//来源是集团
					searcher.setBusinessType(BusinessType.MERCHANT);
					searcher.setBusinessId(shop.getMerchant().getSelfBusinessId());
				}else {
					searcher.setBusinessType(BusinessType.SHOP);
					searcher.setBusinessId(shop.getId());
				}
			}
			pager = physicalCouponService.findPhysicalCouponList(searcher,pager);
			model.addAttribute("searcher", searcher);
			model.addAttribute("shop", shop);
			model.addAttribute("pager", pager);
			return "physical_coupon/shop_physical_coupon_list";
		}
		return null;
	}
	
	
	/**
	 * 跳转到添加页面
	 */
	@AvoidDuplicateSubmission(addToken=true)
	@RequestMapping(value="/add",method = RequestMethod.GET)
	public String showAddPhysicalCoupon(Model model) {
		Business business = getBusiness(); 
		model.addAttribute("physicalCoupon", null);
		if(BusinessType.MERCHANT.equals(business.getSelfBusinessType())) {//集团登陆,查询适用列表
			List<Shop> shopList = shopService.findShopListByMerchantId(business.getSelfBusinessId(), true);
			if(shopList != null && shopList.size()>0) {
				model.addAttribute("shopList",shopList);
				return "physical_coupon/merchant_physical_coupon_input";
			}else {//集团下无子商户
				model.addAttribute("status", "failed");
				model.addAttribute("msg", "集团下没有子商户,无法创建实体券");
				return "physical_coupon/merchant_physical_coupon_list";
			}
		}else if(BusinessType.SHOP.equals(business.getSelfBusinessType())){//普通商户登陆
			return "physical_coupon/shop_physical_coupon_input";
		}
		return null;
	}
		
	/**
	 * 集团添加实体券
	 */
	@AvoidDuplicateSubmission(removeToken=true,errorRedirectURL="/physical_coupon/merchant/list")
	@RequestMapping(value="/merchant/add",method = RequestMethod.POST)
	public String addMerchantPhysicalCoupon(Model model,PhysicalCoupon physicalCoupon,Long[]shopList,RedirectAttributes redirectAttributes) {
		Business business = getBusiness();
		if(physicalCoupon == null || shopList == null || shopList.length<=0) {
			model.addAttribute("status", "failed");
			model.addAttribute("msg", "参数校验失败，或没有勾选适用商户");
			return showAddPhysicalCoupon(model);
		}else if(BusinessType.MERCHANT.equals(business.getSelfBusinessType())) {//集团登陆
			physicalCoupon.setBusiness_id(business.getSelfBusinessId());
			physicalCoupon.setBusiness_type(BusinessType.MERCHANT);
			if(physicalCouponService.add(physicalCoupon,shopList)) {
				redirectAttributes.addFlashAttribute("status", "success");
				redirectAttributes.addFlashAttribute("msg", "添加成功");
			}else {
				model.addAttribute("status", "failed");
				model.addAttribute("msg", "添加失败");
				return showAddPhysicalCoupon(model);
			}
		}
		return "redirect:/physical_coupon/merchant/list";
	}
	
	/**
	 * 普通商户添加实体券
	 */
	@AvoidDuplicateSubmission(removeToken=true,errorRedirectURL="/physical_coupon/shop/list")
	@RequestMapping(value="/shop/add",method = RequestMethod.POST)
	public String addShopPhysicalCoupon(Model model,PhysicalCoupon physicalCoupon,RedirectAttributes redirectAttributes) {
		Business business = getBusiness();
		if(physicalCoupon == null ) {
			model.addAttribute("status", "failed");
			model.addAttribute("msg", "参数校验失败");
			return showAddPhysicalCoupon(model);
		}else if(BusinessType.SHOP.equals(business.getSelfBusinessType())) {//商户登陆
			physicalCoupon.setBusiness_id(business.getSelfBusinessId());
			physicalCoupon.setBusiness_type(BusinessType.SHOP);
			if(physicalCouponService.add(physicalCoupon,new Long[]{business.getSelfBusinessId()})) {
				redirectAttributes.addFlashAttribute("status", "success");
				redirectAttributes.addFlashAttribute("msg", "添加成功");
			}else {
				model.addAttribute("status", "failed");
				model.addAttribute("msg", "添加失败");
				return showAddPhysicalCoupon(model);
			}
		}
		return "redirect:/physical_coupon/shop/list";
	}
	
	/**
	 * 跳转到修改页面
	 */
	@RequestMapping(value="/{id}/update",method = RequestMethod.GET)
	public String showUpdatePhysicalCoupon(Model model,@PathVariable Long id) {
		Business business = getBusiness();
		if(id == null) {
			return null;
		}
		PhysicalCoupon pc = physicalCouponService.findPhysicalCouponById(id);
		if(pc == null) {
			return null;
		}
		model.addAttribute("physicalCoupon", pc);
		//权限校验
		if(BusinessType.MERCHANT.equals(business.getSelfBusinessType())) {//集团登陆
			if((BusinessType.MERCHANT.equals(pc.getBusiness_type()) //1、集团创建，查看是否是同一个集团 2、商户创建，查看是否属于登陆集团的子商户
					&& business.getSelfBusinessId() == pc.getBusiness_id())  ||
				(BusinessType.SHOP.equals(pc.getBusiness_type()) && ArrayUtils.contains(shopService.findShopIdsByMerchantId(business.getSelfBusinessId(), true),pc.getBusiness_id()))) {
				List<Shop> shopList = shopService.findShopListByMerchantId(business.getSelfBusinessId(), true);
				List<PhysicalCouponShop> pcsList = physicalCouponService.findShopListByPhysicalCouponId(pc.getId());
				if(shopList != null && shopList.size()>0 && pcsList != null && pcsList.size()>0) {
					model.addAttribute("pcsList",pcsList);//适用商户
					model.addAttribute("shopList",shopList);//所有子商户
					return "physical_coupon/merchant_physical_coupon_input";
				}else {//集团下无子商户
					model.addAttribute("status", "failed");
					model.addAttribute("msg", "集团下没有子商户,无法创建实体券");
					return "physical_coupon/merchant_physical_coupon_list";
				}
			}
		}else if(BusinessType.SHOP.equals(business.getSelfBusinessType())){//普通商户登陆
			return "physical_coupon/shop_physical_coupon_input";
		}
		return null;
	}
	
	/**
	 * 集团修改实体券
	 */
	@RequestMapping(value="/merchant/update",method = RequestMethod.POST)
	public String updateMerchantPhysicalCoupon(Model model,PhysicalCoupon physicalCoupon,Long[]shopList,RedirectAttributes redirectAttributes) {
		Business business = getBusiness();
		if(physicalCoupon == null || physicalCoupon.getId() == null || shopList == null || shopList.length<=0) {
			model.addAttribute("status", "failed");
			model.addAttribute("msg", "参数校验失败，或没有勾选适用商户");
			return showUpdatePhysicalCoupon(model,physicalCoupon.getId());
		}else if(BusinessType.MERCHANT.equals(business.getSelfBusinessType())) {//集团登陆
			//校验权限
			PhysicalCoupon pc = physicalCouponService.findPhysicalCouponById(physicalCoupon.getId());
			if(pc == null) {
				model.addAttribute("status", "failed");
				model.addAttribute("msg", "参数校验失败");
				return showUpdatePhysicalCoupon(model,physicalCoupon.getId());
			}
			if((BusinessType.MERCHANT.equals(pc.getBusiness_type()) //1、集团创建，查看是否是同一个集团 2、商户创建，查看是否属于登陆集团的子商户
					&& business.getSelfBusinessId() == pc.getBusiness_id())  ||
				(BusinessType.SHOP.equals(pc.getBusiness_type()) && ArrayUtils.contains(shopService.findShopIdsByMerchantId(business.getSelfBusinessId(), true),pc.getBusiness_id()))) {
				physicalCoupon.setBusiness_id(pc.getBusiness_id());
				physicalCoupon.setBusiness_type(pc.getBusiness_type());
				if(BusinessType.SHOP.equals(pc.getBusiness_type())) {//普通商户创建时，适用商户是其本身
					shopList = new Long[]{pc.getBusiness_id()};
				}
				if(physicalCouponService.update(physicalCoupon,shopList)) {
					redirectAttributes.addFlashAttribute("status", "success");
					redirectAttributes.addFlashAttribute("msg", "修改成功");
				}else {
					model.addAttribute("status", "failed");
					model.addAttribute("msg", "修改失败");
					return showUpdatePhysicalCoupon(model,physicalCoupon.getId());
				}
			}
		}
		return  "redirect:/physical_coupon/merchant/list";
	}
	
	/**
	 * 普通商户修改实体券
	 */
	@RequestMapping(value="/shop/update",method = RequestMethod.POST)
	public String updateShopPhysicalCoupon(Model model,PhysicalCoupon physicalCoupon,RedirectAttributes redirectAttributes) {
		Business business = getBusiness();
		if(physicalCoupon == null || physicalCoupon.getId() == null) {
			redirectAttributes.addFlashAttribute("status", "failed");
			redirectAttributes.addFlashAttribute("msg", "参数校验失败");
		}else if(BusinessType.SHOP.equals(business.getSelfBusinessType())) {//集团登陆
			//校验权限
			PhysicalCoupon pc = physicalCouponService.findPhysicalCouponById(physicalCoupon.getId());
			if(pc == null) {
				model.addAttribute("status", "failed");
				model.addAttribute("msg", "参数校验失败");
				return showUpdatePhysicalCoupon(model,physicalCoupon.getId());
			}//只有商户自己创建的实体券才有权限修改
			if(BusinessType.SHOP.equals(pc.getBusiness_type()) && pc.getBusiness_id().equals(business.getSelfBusinessId())) {
				physicalCoupon.setBusiness_id(pc.getBusiness_id());
				physicalCoupon.setBusiness_type(pc.getBusiness_type());
				if(physicalCouponService.update(physicalCoupon,new Long[]{business.getSelfBusinessId()})) {
					redirectAttributes.addFlashAttribute("status", "success");
					redirectAttributes.addFlashAttribute("msg", "修改成功");
				}else {
					model.addAttribute("status", "failed");
					model.addAttribute("msg", "修改失败");
					return showUpdatePhysicalCoupon(model,physicalCoupon.getId());
				}
			}
		}
		return "redirect:/physical_coupon/shop/list";
	}
	
	/**
	 * 集团用户查看实体券详情
	 */
	@RequestMapping(value="/{id}/detail")
	public ModelAndView physicalCouponDetail(ModelAndView mav,@PathVariable Long id) {
		Business business = getBusiness();
		if(id == null || business == null || BusinessType.SHOP.equals(business.getSelfBusinessType())) {
			return null;
		}
		PhysicalCoupon pc = physicalCouponService.findPhysicalCouponById(id);
		if(pc == null) {
			return null;
		}
		//校验权限
		if((BusinessType.MERCHANT.equals(pc.getBusiness_type()) //1、集团创建，查看是否是同一个集团 2、商户创建，查看是否属于登陆集团的子商户
				&& business.getSelfBusinessId() == pc.getBusiness_id())  ||
			(BusinessType.SHOP.equals(pc.getBusiness_type()) && 
				ArrayUtils.contains(shopService.findShopIdsByMerchantId(business.getSelfBusinessId(), true),pc.getBusiness_id()))) {
			List<PhysicalCouponShop> physicalCouponShopList = physicalCouponService.findShopListByPhysicalCouponId(pc.getId());
			mav.addObject("pcshopList", physicalCouponShopList);
			mav.addObject("physicalCoupon", pc);
			mav.setViewName("physical_coupon/detail");
			return mav;
		}
		return null;
	}
	
	/**
	 * 启用或停用
	 */
	@RequestMapping(value="/{id}/open",method = RequestMethod.GET)
	public String updatePhysicalCoupon(Model model,@PathVariable Long id,boolean enable,RedirectAttributes redirectAttributes) {
		Business business = getBusiness();
		if(id == null) {
			return null;
		}
		PhysicalCoupon pc = physicalCouponService.findPhysicalCouponById(id);
		if(pc == null) {
			return null;
		}
		pc.setEnable(enable);
		//权限校验
		if(BusinessType.MERCHANT.equals(business.getSelfBusinessType())) {//集团登陆
			if((BusinessType.MERCHANT.equals(pc.getBusiness_type()) //1、集团创建，查看是否是同一个集团 2、商户创建，查看是否属于登陆集团的子商户
					&& business.getSelfBusinessId() == pc.getBusiness_id())  ||
				(BusinessType.SHOP.equals(pc.getBusiness_type()) && ArrayUtils.contains(shopService.findShopIdsByMerchantId(business.getSelfBusinessId(), true),pc.getBusiness_id()))) {
				
				if(physicalCouponService.update(pc)) {
					redirectAttributes.addFlashAttribute("status", "success");
					redirectAttributes.addFlashAttribute("msg", enable?"启用成功":"停用成功");
				}else {
					redirectAttributes.addFlashAttribute("status", "failed");
					redirectAttributes.addFlashAttribute("msg", enable?"启用成功":"停用成功");
				}
				return  "redirect:/physical_coupon/merchant/list";
			}
		}else if(BusinessType.SHOP.equals(business.getSelfBusinessType())){//普通商户登陆
			if(physicalCouponService.update(pc)) {
				redirectAttributes.addFlashAttribute("status", "success");
				redirectAttributes.addFlashAttribute("msg",enable?"启用成功":"停用成功");
			}else {
				redirectAttributes.addFlashAttribute("status", "failed");
				redirectAttributes.addFlashAttribute("msg",enable?"启用成功":"停用成功");
			}
			return "redirect:/physical_coupon/shop/list";
		}
		return null;
	}
	
	
	/**
	 * 删除
	 */
	@RequestMapping(value="/{id}/delete",method = RequestMethod.DELETE)
	public String deletePhysicalCoupon(Model model,@PathVariable Long id,RedirectAttributes redirectAttributes) {
		Business business = getBusiness();
		if(id == null) {
			return null;
		}
		PhysicalCoupon pc = physicalCouponService.findPhysicalCouponById(id);
		if(pc == null) {
			return null;
		}
		pc.setStatus(0);
		//权限校验
		if(BusinessType.MERCHANT.equals(business.getSelfBusinessType())) {//集团登陆
			if((BusinessType.MERCHANT.equals(pc.getBusiness_type()) //1、集团创建，查看是否是同一个集团 2、商户创建，查看是否属于登陆集团的子商户
					&& business.getSelfBusinessId() == pc.getBusiness_id())  ||
				(BusinessType.SHOP.equals(pc.getBusiness_type()) && ArrayUtils.contains(shopService.findShopIdsByMerchantId(business.getSelfBusinessId(), true),pc.getBusiness_id()))) {
				
				if(physicalCouponService.update(pc)) {
					redirectAttributes.addFlashAttribute("status", "success");
					redirectAttributes.addFlashAttribute("msg", "删除成功");
				}else {
					redirectAttributes.addFlashAttribute("status", "failed");
					redirectAttributes.addFlashAttribute("msg", "删除失败");
				}
				return "redirect:/physical_coupon/merchant/list";
			}
		}else if(BusinessType.SHOP.equals(business.getSelfBusinessType())){//普通商户登陆
			if(physicalCouponService.update(pc)) {
				redirectAttributes.addFlashAttribute("status", "success");
				redirectAttributes.addFlashAttribute("msg", "删除成功");
			}else {
				redirectAttributes.addFlashAttribute("status", "failed");
				redirectAttributes.addFlashAttribute("msg", "删除失败");
			}
			return "redirect:/physical_coupon/shop/list";
		}
		return null;
	}
	
	
	
}
