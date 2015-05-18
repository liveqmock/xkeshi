package com.xpos.controller.api;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.xkeshi.pojo.vo.OrderDetailVO;
import com.xkeshi.pojo.vo.PrintOrderSummaryVO;
import com.xkeshi.pojo.vo.Result;
import com.xkeshi.pojo.vo.SystemParam;
import com.xkeshi.pojo.vo.UnpaidItemListResultVO;
import com.xkeshi.pojo.vo.UnpaidOrderListResultVO;
import com.xkeshi.service.OrdersService;
import com.xkeshi.service.XShopService;
import com.xpos.common.service.OrderService;
import com.xpos.common.service.ShopService;


/**
 * 订单API controller
 */
@Controller
@RequestMapping("api/")
public class APIOrderController extends BaseAPIController {


    @Autowired
    private OrderService orderService;
    
    @Autowired
    private OrdersService ordersService;
    
    @Autowired
    private ShopService shopService;

    @Autowired
    private XShopService  xShopService   ;
    
    /**
     * 查询未支付订单数量
     */
    @ResponseBody
    @RequestMapping(value = "order/unpaid_count", method = RequestMethod.GET)
    public Result unpaidOrderCount(@ModelAttribute SystemParam param) {
    	com.xkeshi.pojo.po.Shop shopPO = xShopService.findShopByShopId(param.getMid());
		if (shopPO.getEnableShift()) {
			String operatorSessionCode = super.getOperatorSession(param);
			return new Result("0","查询未支付订单数量成功",ordersService.getUnpaidOrderCountByOperatorSessionCode(operatorSessionCode));
		}
		return new Result("0","查询未支付订单数量成功",ordersService.getUnpaidOrderCountByOperatorId(param.getOperatorId()));
    }
    
    /**
     * 查询未支付订单列表
     */
    @ResponseBody
    @RequestMapping(value = "order/unpaid/list", method = RequestMethod.GET)
    public Result unpaidOrderList(@ModelAttribute SystemParam param) {
    	UnpaidOrderListResultVO unpaidOrderListResultVO = new UnpaidOrderListResultVO();
    	com.xkeshi.pojo.po.Shop shopPO = xShopService.findShopByShopId(param.getMid());
		if (shopPO.getEnableShift()) {
			String operatorSessionCode = super.getOperatorSession(param);
			unpaidOrderListResultVO.setUnpaidList(ordersService.getUnpaidOrderListByOperatorSessionCode(operatorSessionCode));
		}else {
			unpaidOrderListResultVO.setUnpaidList(ordersService.getUnpaidOrderListByOperatorId(param.getOperatorId()));
		}
		Result result = new Result("查询未支付订单列表成功","0");
		result.setResult(unpaidOrderListResultVO);
		return result;
    }
    

    /**
     * 查询未支付订单列表
     */
    @ResponseBody
    @RequestMapping(value = "order/{orderNumber}/unpaid/item/list", method = RequestMethod.GET)
    public Result unpaidOrderItemList(@ModelAttribute SystemParam param,
    		@PathVariable(value="orderNumber")String orderNumber) {
    	com.xkeshi.pojo.po.Shop shopPO = xShopService.findShopByShopId(param.getMid());
		if (shopPO.getEnableShift()) {
			String operatorSessionCode = super.getOperatorSession(param);
			if (StringUtils.isBlank(operatorSessionCode)) {
				return new Result("-1", "交接班失败:操作员当班会话错误，请退出后重新登录。", null);
			}
		}
		if (StringUtils.isBlank(orderNumber)) {
			return new Result("-1", "订单号错误", null);
		}
		Result result = new Result("查询未支付订单商品列表成功","0");
		UnpaidItemListResultVO unpaidItemListResultVO = new UnpaidItemListResultVO();
		unpaidItemListResultVO.setUnpaidItemList(ordersService.getUnpaidOrderItemListByOrderNumber(orderNumber));
		result.setResult(unpaidItemListResultVO);
		return result;
    }

    /**
     * 获取单笔订单的小票打印信息
     */
   @ResponseBody
   @RequestMapping(value="order/{orderNumber}/print_summary", method = RequestMethod.GET)
   public Result getOrderPrintSummary(@ModelAttribute SystemParam systemParam,
									@PathVariable(value="orderNumber")String orderNumber,
									@RequestParam("orderType")String orderType){
	    if(StringUtils.equalsIgnoreCase("XPOS_ORDER", orderType)){
		    return orderService.generateXPOSOrderPrintSummary(systemParam, orderNumber);
	    }else if(StringUtils.equalsIgnoreCase("THIRD_ORDER", orderType)){

	    }
	    return new Result("查询成功", "0", new PrintOrderSummaryVO());
   }
   /**
    * 获取单笔订单的详细信息
    */
   @ResponseBody
   @RequestMapping(value="order/{orderNumber}/detail", method = RequestMethod.GET)
   public Result getOrderDetail(@ModelAttribute SystemParam systemParam,
		   @PathVariable(value="orderNumber")String orderNumber,
		   @RequestParam("orderType")String orderType){
	   if(StringUtils.equalsIgnoreCase("XPOS_ORDER", orderType)){
		   return orderService.generateXPOSOrderDetail(systemParam, orderNumber);
	   }else if(StringUtils.equalsIgnoreCase("THIRD_ORDER", orderType)){
		   
	   }
	   return new Result("0","查询成功",new OrderDetailVO());
   }
}
