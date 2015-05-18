package com.xpos.controller.api;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.xkeshi.pojo.vo.Result;
import com.xkeshi.pojo.vo.SystemParam;
import com.xkeshi.pojo.vo.shopPrinter.ShopPrinterListVO;
import com.xkeshi.pojo.vo.shopPrinter.ShopPrinterServiceVO;
import com.xkeshi.pojo.vo.shopPrinter.ShopPrinterVO;
import com.xpos.common.entity.Shop;
import com.xpos.common.entity.ShopPrinter;
import com.xpos.common.service.ShopPrinterService;
import com.xpos.common.service.ShopService;
import com.xpos.controller.BaseController;

@Controller
@RequestMapping("api/xposprinter")
public class ApiShopPrinterController extends BaseController{
	
	private final static String SUCCESS = "0";
	private final static String FAILED = "1";
	
	@Autowired
	private ShopPrinterService shopPrinterService;

	@Autowired
	private ShopService shopService;
	
	
	
	/**
	 * 查询打印机档口列表
	 */
	@RequestMapping(value="/print_list", method=RequestMethod.GET)
	@ResponseBody
	public Result findShopPrinterListByShopId(SystemParam systemParam){
		if(systemParam.getMid() == null) {
			return new Result(FAILED,"查询打印机档口列表失败");
		}
		try {
			List<ShopPrinter> list =  shopPrinterService.findShopPrintersByShopId(systemParam.getMid(), null).getList();
			List<ShopPrinterVO> voList = new ArrayList<>();
			if(!CollectionUtils.isEmpty(list)) {
				for(ShopPrinter shopPrinter : list) {
					voList.add(new ShopPrinterVO(shopPrinter.getId(),shopPrinter.getName(),shopPrinter.getIp(),shopPrinter.isEnable()==true?1:0,shopPrinter.getComment()));
				}
				return new Result(SUCCESS, "查询打印机档口列表成功",new ShopPrinterListVO(voList));
			}else {
				return new Result(SUCCESS, "查询打印机档口列表成功",null);
			}
		} catch (Exception e) {
			return new Result(FAILED, "查询打印机档口列表失败",null);
		}
	}

	/**
	 * 打印机服务查询
	 */
	@RequestMapping(value="/print_service", method=RequestMethod.GET)
	@ResponseBody
	public Result findPhysicalCouponListByShopId(SystemParam systemParam){
		if(systemParam.getMid() == null) {
			return new Result(FAILED,"打印机服务信息查询失败");
		}
		try {
			Shop shop = shopService.findShopByIdIgnoreVisible(systemParam.getMid());
			ShopPrinterServiceVO vo = new ShopPrinterServiceVO(shop.getPrinterIp(),shop.getPrinterPort(),shop.isPrinterEnable()==true?1:0);
			return new Result(SUCCESS, "打印机服务信息查询成功",vo);
		} catch (Exception e) {
			return new Result(FAILED, "打印机服务信息查询失败",null);
		}
	}

	
}


