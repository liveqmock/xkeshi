package com.xpos.common.service;

import java.util.List;

import com.xpos.common.entity.Item;
import com.xpos.common.entity.ItemCategory;
import com.xpos.common.entity.face.Business;
import com.xpos.common.searcher.ItemSearcher;
import com.xpos.common.utils.Pager;

public interface ItemService {
	
	List<ItemCategory> findCategoryByBusiness(Business bussiness);
	
	boolean deleteItem(Item item);
	
	boolean deleteCategory(ItemCategory category) throws Exception;
	
	ItemCategory findCategoryById(Long id);
	
	Item findItemById(Long id);
	
	boolean isDuplicateItemCategorysByNameAndBusiness(ItemCategory itemCategory,Business business);
	
    boolean addOrUpdateCategory(ItemCategory itemCategory);
	
	boolean addItem(Item item);
	
	boolean updateItem(Item item);
	
	Pager<Item> searchItems(ItemSearcher searcher, Pager<Item> pager);
}
