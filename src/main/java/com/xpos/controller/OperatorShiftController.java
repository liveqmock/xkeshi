package com.xpos.controller;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.xkeshi.pojo.vo.shift.OperatorShiftVO;
import com.xkeshi.pojo.vo.shift.ShiftItemVO;
import com.xkeshi.pojo.vo.shift.ShiftShopVO;
import com.xkeshi.pojo.vo.shift.SummarizeInfoResultVO;
import com.xkeshi.service.XMerchantService;
import com.xkeshi.service.XShopService;
import com.xpos.common.entity.Merchant;
import com.xpos.common.entity.Shop;
import com.xpos.common.entity.Terminal;
import com.xpos.common.entity.face.Business;
import com.xpos.common.entity.pos.POSOperationLog;
import com.xpos.common.searcher.OperatorShiftSearcher;
import com.xpos.common.service.OperatorShiftService;
import com.xpos.common.service.ShopService;
import com.xpos.common.service.TerminalService;
import com.xpos.common.utils.Pager;

/**
 * 
 * @author xk
 *
 */
@Controller
@RequestMapping("/shift")
public class OperatorShiftController extends BaseController {

	@Resource
	private OperatorShiftService  operatorShiftService  ;
	
	@Resource
	private ShopService  shopService  ;
	
	@Resource
	private  XShopService  xShopService ;
	
	@Resource
	private  XMerchantService  xMerchantService   ;
	
	@Resource
	private TerminalService  terminalService;
	
	
	/*交接班列表*/
	@RequestMapping(value  = "/list" ,method =  RequestMethod.GET)
	public String operatorShiftList (Model model , Pager<OperatorShiftVO>  pager ,OperatorShiftSearcher  searcher ){
		Business business = super.getBusiness();
		if (business != null) {
			pager = operatorShiftService.findOpeatorShiftList(business, pager, searcher);
			model.addAttribute("pager",pager);
			model.addAttribute("searcher", searcher);
			if (business instanceof Merchant) {
				List<ShiftShopVO> shops =  xMerchantService.getShiftShopVOByMerchantId(business.getSelfBusinessId());
				model.addAttribute("shops",shops);
			}
		}
		return "operator/shift_list";
	}
	
	/*交接班详情*/
	@RequestMapping(value  = "/detail/{operatorSessionCode}" ,method =  RequestMethod.GET)
	public String operatorShiftDetail(@PathVariable(value="operatorSessionCode") String operatorSessionCode  , RedirectAttributes redirectAttributes , Model model ,  Pager<ShiftItemVO>  pager ){
		Business business = super.getBusiness();
		if (business != null) {
			SummarizeInfoResultVO sumInfoVO = operatorShiftService.getSummarizeInfoResultVO(operatorSessionCode);
			if (sumInfoVO == null ) {
				redirectAttributes.addFlashAttribute("status", "failed");
				redirectAttributes.addFlashAttribute("msg", "未找到交接班信息");
			   return "redirect:/shift/list";
			}else if (sumInfoVO.getShiftInfo() == null){
				redirectAttributes.addFlashAttribute("status", "failed");
				redirectAttributes.addFlashAttribute("msg", "操作员信息不存在或已被删除");
			   return "redirect:/shift/list";
			}
			model.addAttribute("sumInfoVO", sumInfoVO);
			OperatorShiftVO  operatorShiftVO   =  operatorShiftService.getOpeatorShiftDetail(operatorSessionCode);
			model.addAttribute("operatorShiftVO", operatorShiftVO);
			pager = operatorShiftService.findOpeatorShiftDetail(pager, operatorSessionCode);
			model.addAttribute("pager",pager);
		}
		return "operator/shift_detail";
	}
	
	/**交接班设置*/
    @RequestMapping(value = "/shift_setting",method = RequestMethod.GET) 
	public String  settingShiftShopDetail(RedirectAttributes  redirect, Model  model){
    	Business business = super.getBusiness();
    	if (business instanceof  Shop) {
			com.xkeshi.pojo.po.Shop shop = xShopService.findShopByShopId(((Shop) business).getId());
    		model.addAttribute("enableShift", shop.getEnableShift());
    		model.addAttribute("visibleShiftReceivableData", shop.getVisibleShiftReceivableData());
    		return "operator/shift_setting";
		} else {
			redirect.addFlashAttribute("status", STATUS_FAILD);
			redirect.addFlashAttribute("msg", "无法查看");
		}
    	return "redirect:/shop/list";
	}
    
    /**交接班修改设置*/
    @RequestMapping(value = "/shift_setting",method = RequestMethod.PUT) 
    public String  settingShiftShop(@RequestParam("enableShift")boolean enableShift , @RequestParam(value= "visibleShiftReceivableData"  ,required  = false ) boolean visibleShiftReceivableData,RedirectAttributes  redirect){
    	Business business = super.getBusiness();
    	if (business instanceof  Shop) {
    		com.xkeshi.pojo.po.Shop shop = null;
    		//a. 需要交班切换到不需要交接班
    		if (!enableShift) {
    		    shop = xShopService.findShopByShopId(((Shop) business).getId());
    			if (shop != null && shop.getEnableShift()) {
					//b.判断设备是否都已经交接班
    				List<Terminal> terminals = terminalService.findTerminalsByShopId(shop.getId());
    				for (Terminal terminal : terminals) {
    					POSOperationLog posOperationLog = operatorShiftService.findOperatorSessionByDeviceNumber(terminal.getDeviceNumber());
    					if (posOperationLog != null) {
    						redirect.addFlashAttribute("status", STATUS_FAILD);
    						redirect.addFlashAttribute("msg", "设置失败,存在设备未完成交接班");
    						return "redirect:/shift/shift_setting";
						}
    				}
				}
			}
    		shop  =  new com.xkeshi.pojo.po.Shop();
    		shop.setId(business.getSelfBusinessId());
    		shop.setEnableShift(enableShift);
    		shop.setVisibleShiftReceivableData(visibleShiftReceivableData);
    		Boolean updateShopByShift = xShopService.updateShopByShift(shop);
    		if (updateShopByShift) {
				super.storeSession(com.xkeshi.pojo.po.Shop.SHOP_ENABLE_SHIFT, enableShift);
    			redirect.addFlashAttribute("status", STATUS_SUCCESS);
    			redirect.addFlashAttribute("msg", "设置成功");
			}else{
				redirect.addFlashAttribute("status", STATUS_FAILD);
				redirect.addFlashAttribute("msg", "设置失败");
			}
    		return "redirect:/shift/shift_setting";
		} else {
			redirect.addFlashAttribute("status", STATUS_FAILD);
			redirect.addFlashAttribute("msg", "无法修改");
		}
    	return "redirect:/shop/list";
    	 
    }
	
}
