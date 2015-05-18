package com.xpos.controller.api;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.xkeshi.pojo.po.Shop;
import com.xkeshi.pojo.vo.Result;
import com.xkeshi.pojo.vo.SystemParam;
import com.xkeshi.pojo.vo.shift.ShiftItemResultVO;
import com.xkeshi.pojo.vo.shift.ShiftVO;
import com.xkeshi.pojo.vo.shift.SummarizeInfoResultVO;
import com.xkeshi.service.XShopService;
import com.xpos.common.service.OperatorShiftService;
/**
 * 
 * @author xk
 * 交接班（新丰小吃）
 */
@Controller
@RequestMapping("/api")
public class ApiShiftController extends   BaseAPIController{
	
	@Resource
	private OperatorShiftService  operatorShiftService   ;
	
	@Resource
	private XShopService  xShopService  ;
	
	
	/** 交接班操作  */
	@RequestMapping( value = "/operator/shift" , method = RequestMethod.POST,produces = "application/json;charset=UTF-8")
	public @ResponseBody Result  shiftController(  SystemParam  systemParam ,  @RequestBody(required  = false) ShiftVO  shiftVO ){
		    Shop shop = xShopService.findShopByShopId(systemParam.getMid());
		    if (shop != null && !shop.getEnableShift()) 
		    	return new Result("-1", "交接班失败:该商户不支持交接班", null);
		    if (shiftVO  != null && shiftVO.getTotalCashPaidAmount() == null ) 
				return new Result("-1", "交接班失败:totalCashPaidAmount不能为空", null);
			String operatorSessionCode = super.getOperatorSession(systemParam);
			if (StringUtils.isBlank(operatorSessionCode)) 
				return new Result("-1", "交接班失败:操作员当班会话错误，请退出后重新登录。", null);
			try{
				boolean executeOperatorShift = operatorShiftService.executeOperatorShift(systemParam ,shiftVO);
				if (executeOperatorShift) 
					return new Result("0", "交接班成功", null);
			}catch(Exception  e){
				logger.error(e.toString());
			}
		return new Result("-1", "交接班失败", null);
	}
	
	/** 交接班清单 */
	@RequestMapping( value = "/operator/shift/summarize" , method = RequestMethod.GET ,produces = "application/json;charset=UTF-8")
	public @ResponseBody Result  shiftSummarizeController( SystemParam  systemParam ){
		Shop shop = xShopService.findShopByShopId(systemParam.getMid());
	    if (shop != null && !shop.getEnableShift()) 
	    	return new Result("-1", "获取失败:该商户不支持交接班", null);
		String operatorSessionCode = super.getOperatorSession(systemParam);
		if (StringUtils.isBlank(operatorSessionCode)) 
			return new Result("-1", "获取失败:操作员当班会话错误，请退出后重新登录。", null);
		try{
			SummarizeInfoResultVO summarizeInfoResultVO = operatorShiftService.getSummarizeInfoResultVO(operatorSessionCode);
			return new Result("0", "交接班清单", summarizeInfoResultVO);
		}catch(Exception  e){
			logger.error(e.toString());
			return new Result("-1", "获取失败", null);
		}
	}
	
	/** 交接班销售商品汇总清单 */
	@RequestMapping( value = "/operator/shift/order_item/list" , method = RequestMethod.GET ,produces = "application/json;charset=UTF-8")
	public @ResponseBody Result  shiftOrderItemController( SystemParam  systemParam   ){
		Shop shop = xShopService.findShopByShopId(systemParam.getMid());
	    if (shop != null && !shop.getEnableShift()) 
	    	return new Result("-1", "获取失败:该商户不支持交接班", null);
		String operatorSessionCode = super.getOperatorSession(systemParam);
		if (StringUtils.isBlank(operatorSessionCode)) 
			return new Result("-1", "获取失败:操作员当班会话错误，请退出后重新登录。", null);
		try{
			ShiftItemResultVO shiftItemResultVO = operatorShiftService.getShiftItem(operatorSessionCode);
			return new Result("0", "销售商品汇总", shiftItemResultVO);
		}catch(Exception  e){
			logger.error(e.toString());
			return new Result("-1", "获取失败", null);
		}
	}
	
	
}



