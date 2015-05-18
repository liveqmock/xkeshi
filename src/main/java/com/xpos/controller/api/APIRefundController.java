package com.xpos.controller.api;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.xkeshi.pojo.vo.Result;
import com.xkeshi.pojo.vo.SystemParam;
import com.xkeshi.pojo.vo.param.ManagerPasswordParam;
import com.xkeshi.service.OrdersService;
import com.xkeshi.service.XShopService;
import com.xpos.common.service.OperatorService;
import com.xpos.common.service.OrderService;
import com.xpos.common.service.ShopService;


/**
 * 订单API controller
 */
@Controller
@RequestMapping("api/")
public class APIRefundController {


    @Autowired
    private OrderService orderService;
    
    @Autowired
    private OrdersService ordersService;
    
    @Autowired
    private ShopService shopService;

    @Autowired
    private XShopService  xShopService   ;
    
    @Autowired
    private OperatorService operatorService;
    
   
   /**
    * 获取单笔订单的流水信息
    */
   @ResponseBody
   @RequestMapping(value="order/{orderNumber}/transactionList", method = RequestMethod.GET)
   public Result getOrderDetail(@ModelAttribute SystemParam systemParam,
		   @PathVariable(value="orderNumber")String orderNumber,@RequestParam("orderType")String orderType){
	   if(StringUtils.equalsIgnoreCase("XPOS_ORDER", orderType)){
		   return orderService.generateXPOSOrderTransaction(systemParam, orderNumber);
	   }else if(StringUtils.equalsIgnoreCase("THIRD_ORDER", orderType)){
		   
	   }
	   return new Result("1003", "订单类型错误", null);
	   
   }
   
   
   /**
    * 校验店长身份
    */
   @ResponseBody
   @RequestMapping(value="order/{orderNumber}/refund/authorization/verify", method = RequestMethod.POST)
   public Result checkManager(@ModelAttribute SystemParam systemParam,
		   @PathVariable(value="orderNumber")String orderNumber,
		   @RequestBody  ManagerPasswordParam param){
	   //校验店长身份
	   String tokenResult = operatorService.verifyManagerByToken(param.getToken(),systemParam.getMid(),orderNumber);
	   if(tokenResult != null) {
		   return new Result("1002", tokenResult, null);
	   }else {
		   return new Result("0", "店长身份校验成功", null);
	   }
	   
   }
}
