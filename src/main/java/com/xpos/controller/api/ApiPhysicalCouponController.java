package com.xpos.controller.api;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.xkeshi.pojo.vo.Result;
import com.xkeshi.pojo.vo.SystemParam;
import com.xkeshi.pojo.vo.physicalCoupon.OrderPhysicalCouponListVO;
import com.xkeshi.pojo.vo.physicalCoupon.OrderPhysicalCouponVO;
import com.xkeshi.pojo.vo.physicalCoupon.PhysicalCouponListVO;
import com.xkeshi.pojo.vo.physicalCoupon.PhysicalCouponVO;
import com.xkeshi.service.XShopService;
import com.xpos.common.entity.physicalCoupon.PhysicalCoupon;
import com.xpos.common.entity.physicalCoupon.PhysicalCouponOrder;
import com.xpos.common.searcher.physicalCoupon.PhysicalCouponOrderSearcher;
import com.xpos.common.service.PhysicalCouponService;
import com.xpos.common.utils.DateUtil;

@Controller
@RequestMapping("/api/physical_coupon")
public class ApiPhysicalCouponController extends BaseAPIController{
	
	private final static String SUCCESS = "0";
	private final static String FAILED = "1";
	
	@Autowired
	private PhysicalCouponService physicalCouponService;
	
	@Autowired
	private XShopService  xShopService   ;
	
	@RequestMapping(value="/list", method=RequestMethod.GET)
	@ResponseBody
	public Result findPhysicalCouponListByShopId(SystemParam systemParam){
		if(systemParam.getMid() == null) {
			return new Result(FAILED,"查询商户实体券列表失败");
		}
		try {
			List<PhysicalCoupon> list =  physicalCouponService.findAvailablePhysicalCouponListByShopId(systemParam.getMid());
			List<PhysicalCouponVO> voList = new ArrayList<>();
			if(!CollectionUtils.isEmpty(list)) {
				for(PhysicalCoupon pyhsicalCoupon : list) {
					voList.add(new PhysicalCouponVO(pyhsicalCoupon.getId(),pyhsicalCoupon.getName(),pyhsicalCoupon.getAmount()));
				}
				return new Result(SUCCESS, "查询商户实体券列表成功",new PhysicalCouponListVO(voList));
			}else {
				return new Result(SUCCESS, "查询商户实体券列表成功",null);
			}
		} catch (Exception e) {
			return new Result(FAILED, "查询商户实体券列表失败",null);
		}
	}

	/**
	 * 查看商家当日的实体券核销情况
	 */
	@RequestMapping(value="used/list", method=RequestMethod.GET)
	@ResponseBody
	public Result findPhysicalCouponListByShopId(PhysicalCouponOrderSearcher orderPhysicalCouponSearcher, 
			SystemParam systemParam){
		if(systemParam.getMid() == null) {
			return new Result(FAILED,"实体券核销记录查询失败");
		}
		if(orderPhysicalCouponSearcher.getDate()==null) {
			return new Result(FAILED,"缺少日期参数");
		}
		com.xkeshi.pojo.po.Shop shopPO = xShopService.findShopByShopId(systemParam.getMid());
		if (shopPO.getEnableShift()) {
			String operatorSessionCode = super.getOperatorSession(systemParam);
			orderPhysicalCouponSearcher.setOperatorSessionCode(operatorSessionCode);
		}
		
		try {
			orderPhysicalCouponSearcher.setShopIds(new Long[]{systemParam.getMid()});
			List<PhysicalCouponOrder> list = physicalCouponService.findOrderPhysicalCouponList(orderPhysicalCouponSearcher,null).getList();
			List<OrderPhysicalCouponVO> voList = new ArrayList<>();
			BigDecimal totalAmount = new BigDecimal(0) ;//定义当日实体券产生的总金额
			if(!CollectionUtils.isEmpty(list)) {
				for(PhysicalCouponOrder orderPyhsicalCoupon : list) {
					voList.add(new OrderPhysicalCouponVO(orderPyhsicalCoupon.getOrder().getOrderNumber(),
							orderPyhsicalCoupon.getAmount(),
							orderPyhsicalCoupon.getPhysicalCouponName(),
							DateUtil.getDate(orderPyhsicalCoupon.getCreatedTime(),"HH:mm")));
					totalAmount = totalAmount.add(orderPyhsicalCoupon.getAmount());
				}
				return new Result(SUCCESS, "实体券核销记录",new OrderPhysicalCouponListVO(list.size(),totalAmount,voList));
			}else{
				return new Result(SUCCESS, "实体券核销记录",null);
			}
		} catch (Exception e) {
			return new Result(FAILED, "实体券核销记录查询失败",null);
		}
	}

	
}


