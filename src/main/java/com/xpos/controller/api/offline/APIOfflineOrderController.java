package com.xpos.controller.api.offline;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.xkeshi.pojo.vo.Result;
import com.xkeshi.pojo.vo.SystemParam;
import com.xkeshi.pojo.vo.offline.OfflineOrderVO;
import com.xkeshi.service.InterruptedOrdersService;
import com.xkeshi.service.OfflineOrdersService;
import com.xpos.controller.BaseController;


@Controller
@RequestMapping("/api")
public class APIOfflineOrderController extends BaseController{
	
	@Autowired
	private OfflineOrdersService offlineOrderService;
	
	@Autowired
	private InterruptedOrdersService interruptedOrderService;
	
	/**
	 * 用于处理批量提交的离线订单数据(只支持现金支付及实体券优惠)
	 * <b>如果订单已存在，不予处理</b>
	 */
	@ResponseBody
	@RequestMapping(value="offline/order/upload",method = RequestMethod.POST)
	public Result orderUpload(@RequestBody List<OfflineOrderVO> offlineOrderList,@ModelAttribute SystemParam systemParam) {
		return offlineOrderService.uploadOfflineOrder(systemParam,offlineOrderList);
	}
	
	/**
	 * 用于处理收银订单从创建到支付完成过程中，任一环节发生断网或服务器未响应情况下的订单
	 */
	@ResponseBody
	@RequestMapping(value="/offline/interrupted_order/upload",method = RequestMethod.POST)
	public Result interruptedOrderUpload(@RequestBody List<OfflineOrderVO> offlineOrderList, @ModelAttribute SystemParam systemParam) {
		return interruptedOrderService.uploadInterruptedOrder(systemParam, offlineOrderList);
	}
	
}
