package com.xpos.controller;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.xkeshi.pojo.vo.transaction.BaseTransactionVO;
import com.xpos.common.entity.Merchant;
import com.xpos.common.entity.Order;
import com.xpos.common.entity.Shop;
import com.xpos.common.entity.face.Business;
import com.xpos.common.entity.physicalCoupon.PhysicalCouponOrder;
import com.xpos.common.searcher.OrderSearcher;
import com.xpos.common.service.OrderService;
import com.xpos.common.service.PhysicalCouponService;
import com.xpos.common.service.ShopService;
import com.xpos.common.utils.Pager;

@Controller
@RequestMapping("order")
public class OrderController extends BaseController{

	@Resource
	private OrderService orderService;
	
	@Resource
	private ShopService shopService;
	
	@Resource
	private PhysicalCouponService physicalCouponService;
	@RequestMapping("list")
	public String list(OrderSearcher searcher, Pager<Order> pager, Model model){
		pager = orderService.findOrders(getBusiness(), pager, searcher);
		model.addAttribute("pager", pager);
		model.addAttribute("searcher", searcher);
		
		//数量、金额统计
		String totalAmount = orderService.getTotalAmount(getBusiness(), searcher);
		//订单列表页面的交易金额合计,原来的交易金额合计是直接从orders表中的actuallyPaid这个字段中取出求和的
		//这个字段的计算方式是中的totalsAmount(orders表)*discount(order_memeber_discount表)-实体券
		//caculateAmount是把减掉的实体券部分给加回来了
		String caculateAmount = orderService.getCaculateAmount(getBusiness(), searcher);
		model.addAttribute("totalAmount", totalAmount);
		model.addAttribute("caculateAmount", caculateAmount);
		if(getBusiness() instanceof Merchant){
			//集团账号登陆，加载所有商户
			Map<String, Business> applicableShops = new HashMap<>();
			List<Shop> shopList = shopService.findShopListByMerchantId(getBusiness().getSelfBusinessId(), true);
			for(Shop shop : shopList){
				applicableShops.put(shop.getId().toString(), shop);
			}
			model.addAttribute("applicableShops", applicableShops);
			
		}
		
		return "item/order_list";
	}
	/**点单明细*/
	@RequestMapping(value = "{bizId}/detail/{orderNum}", method = RequestMethod.GET)
	public ModelAndView getDetail(@PathVariable("bizId") Long bizId, @PathVariable("orderNum") String orderNum
			, ModelAndView mav){
		Order order = orderService.findByOrderNumber(orderNum);
		if(order == null || !order.getBusinessId().equals(bizId)){
			mav.addObject("status", "failed");
			mav.setViewName("item/order_list");
			return mav;
		}
		//根据ordernumbr来查询出实体券订单列表
		List<PhysicalCouponOrder> phyCouponOrderList = physicalCouponService.findPhysicalCouponOrderByOrderNum(orderNum);
		//根据ordernumber从physical_coupon_order表中计算出实体券的总金额
		BigDecimal phyAmount = physicalCouponService.calculatePhyAmount(orderNum);
		//double discountAmount = actuallyPaid + phyAmount;
		List<BaseTransactionVO> paidList = orderService.getOrderTransactionList(orderNum);
		BigDecimal actualTotalPaid = new BigDecimal(0);//这个值是从每种支付流水表中取出然后叠加来显示在页面上的实际支付金额
		for(int i = 0; i < paidList.size(); i++) {
			actualTotalPaid = actualTotalPaid.add(paidList.get(i).getAmount());
		}
		mav.addObject("paidList", paidList);
		mav.addObject("phyCouponOrderList", phyCouponOrderList);
		mav.addObject("phyAmount", phyAmount);
		mav.addObject("actualTotalPaid", actualTotalPaid);
		mav.addObject("shop", shopService.findShopByIdIgnoreVisible(bizId));
		mav.addObject("order", order);
		mav.setViewName("item/order_detail");
		return mav;
	}
}
