package com.xpos.controller.api;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xkeshi.pojo.vo.SystemParam;
import com.xpos.common.entity.*;
import com.xpos.common.entity.member.Member;
import com.xpos.common.searcher.ItemSearcher;
import com.xpos.common.service.ConfigurationService;
import com.xpos.common.service.ItemService;
import com.xpos.common.service.OrderService;
import com.xpos.common.service.ShopService;
import com.xpos.common.service.TerminalService;
import com.xpos.common.service.member.MemberService;
import com.xpos.common.utils.Pager;
import com.xpos.controller.BaseController;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;

@Controller
@RequestMapping("api")
public class ApiController extends BaseController{
	
	private final static String STATUS_KEY = "status";
	private final static String SUCCESS = "1";
	private final static String FAILD = "0";
	
	private String imagePrefix = "http://xpos-img.b0.upaiyun.com";
	
	@Resource
	private ItemService itemService;
	
	@Resource
	private ShopService shopService;
	
	@Resource
	private OrderService orderService;
	
	@Resource
	private TerminalService terminalService;
	
	@Resource
	private MemberService memberService  ;
	
	@Resource
	private ConfigurationService confService;
	
	
	@RequestMapping(value="/categories", method=RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> showItemCategories(){
	
		List<ItemCategory> categories = itemService.findCategoryByBusiness(getBusiness());
		Map<String, Object> map = new HashMap<>();
		map.put("categories", categories);
		map.put(STATUS_KEY, SUCCESS);
		
		return map;
		
	}
	
	@RequestMapping(value="/shop", method=RequestMethod.GET)
	@ResponseBody
	public Map<String, String> shopInfo(){
		Shop shop = (Shop)getBusiness();
		Map<String, String> map = new HashMap<>();
		map.put("status",  SUCCESS);
		map.put("shopName", shop.getName());
		map.put("contact", shop.getContact());
		map.put("address", shop.getAddress());
		return map;
	}
	
	@RequestMapping(value="/items", method=RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> showItems(SystemParam systemParam){
		Shop shop = null;
		if(systemParam!=null) {
			Long mid =systemParam.getMid();
			if(mid !=null) {
				shop = shopService.findShopByIdIgnoreVisible(mid);
			}
		}
		Pager<Item> pager = new Pager<Item>();
		pager.setPageSize(Pager.MAX_PAGE_SIZE);
		ItemSearcher searcher = new ItemSearcher();
		searcher.setBusiness(shop);
		searcher.setMarketable(true);
		
		Map<String, List<Item>> tempMap = new LinkedHashMap<String, List<Item>>();
		List<Item> items = itemService.searchItems(searcher, pager).getList();
		List<ItemCategory> categories = itemService.findCategoryByBusiness(shop);
		for(ItemCategory itemCategory:categories)
			tempMap.put(itemCategory.getName(), new ArrayList<Item>());
		
		for(Item item:items)
			tempMap.get(item.getCategory().getName()).add(item);
		
		Map<String, Object> map = new HashMap<>();
		map.put("imagePrefix", imagePrefix);
		map.put("items", tempMap.entrySet());
		map.put(STATUS_KEY, SUCCESS);
		
		return map;
		
	}
	
	@RequestMapping(value="/order", method=RequestMethod.POST)
	@ResponseBody
	public Map<String, String> saveOrder(String order, HttpServletRequest request) throws JsonParseException, JsonMappingException, IOException{
		
		//TODO 直接用框架做json mapper
		Order orderObject = new ObjectMapper().readValue(order, Order.class);
			
		orderObject.setBusiness(getBusiness());
		//
		orderService.saveOrder(orderObject);
		Map<String, String> map = new HashMap<>();
		map.put(STATUS_KEY, SUCCESS);
		
		return map;
	}
	
	///member/{mobile}?deviceNumber=xxx
	@RequestMapping(value="/member/{mobile}",method=RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> ShopMember(@PathVariable("mobile") String mobile , 
							 @RequestParam("deviceNum") String	deviceNumber){
		Map<String, Object> map = new HashMap<>();
		map.put("status",FAILD);
		if(StringUtils.isBlank(deviceNumber)){
			map.put("msg", "deviceNumber不能为空");
		}
		Terminal terminal = terminalService.findTerminalByDevice(deviceNumber);
		if(terminal == null){
			map.put("msg","设备未注册");
		}else if(terminal.getShop() == null){
			map.put("msg","指定商户不存在/已删除");
		}else{
			Shop shop = terminal.getShop(); 
			Member member = memberService.findMemberByMobileForShop(shop, mobile);
			if (member == null) {
				map.put("msg","会员不存在");
			}else{
				map.put("status",SUCCESS);
				map.put("member", member);
			}
		}
		return  map;
	}
	
	@RequestMapping(value="conf/refresh", method = RequestMethod.GET)
	@ResponseBody
	public String refreshGlobalConfiguration(){
		return confService.initialize(true) ? "SUCCESS" : "FAILED";
	}
	
}
