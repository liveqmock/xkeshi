package com.xpos.controller.api;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.xkeshi.pojo.vo.Result;
import com.xkeshi.pojo.vo.SystemParam;
import com.xkeshi.pojo.vo.item.ItemInventoryListVO;
import com.xkeshi.pojo.vo.item.ItemInventoryVO;
import com.xpos.common.entity.Item;
import com.xpos.common.entity.Shop;
import com.xpos.common.searcher.ItemSearcher;
import com.xpos.common.service.ConfigurationService;
import com.xpos.common.service.ItemService;
import com.xpos.common.service.ShopService;
import com.xpos.common.utils.Pager;
import com.xpos.controller.BaseController;

@Controller
@RequestMapping("api/item")
public class ApiItemController extends BaseController{
	
	private final static String SUCCESS = "0";
	private final static String FAILED = "1";
	
	@Resource
	private ItemService itemService;
	
	@Resource
	private ShopService shopService;
	
	@Resource
	private ConfigurationService confService;
	
	
	@RequestMapping(value="/item_category/{categoryId}/inventory", method=RequestMethod.GET)
	@ResponseBody
	public Result getItemInventoryByCategoryId(@PathVariable Long categoryId ,SystemParam param){
		Result result = null;
		try {
			Pager<Item> pager = new Pager<Item>();
			pager.setPageSize(Pager.MAX_PAGE_SIZE);
			ItemSearcher searcher = new ItemSearcher();
			if(param.getMid()==null) {
				return new Result(FAILED,"库存信息获取失败");
			}
			Shop shop = shopService.findShopByIdIgnoreVisible(param.getMid());
			if(shop == null) {
				return new Result(FAILED,"库存信息获取失败");
			}
			searcher.setBusiness(shop);
			searcher.setMarketable(true);
			searcher.setCategoryId(categoryId);
			List<Item> items = itemService.searchItems(searcher, pager).getList();
			List<ItemInventoryVO> itemInventoryList = new ArrayList<>();
			if(!CollectionUtils.isEmpty(items)){
				for(Item item : items){
					itemInventoryList.add(new ItemInventoryVO(item.getId(), item.getItemInventory().getInventory()));
				}
			}
			result = new Result(SUCCESS,"库存信息", new ItemInventoryListVO(itemInventoryList));
		} catch (Exception e) {
			result = new Result(FAILED,"库存信息获取失败");
		}
		return result;
	}
	
	
}


