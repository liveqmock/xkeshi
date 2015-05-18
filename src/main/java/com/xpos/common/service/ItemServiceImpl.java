package com.xpos.common.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xpos.common.entity.Item;
import com.xpos.common.entity.ItemCategory;
import com.xpos.common.entity.Picture.PictureType;
import com.xpos.common.entity.example.ItemCategoryExample;
import com.xpos.common.entity.example.ItemExample;
import com.xpos.common.entity.example.ItemInventoryExample;
import com.xpos.common.entity.face.Business;
import com.xpos.common.entity.itemInventory.ItemInventory;
import com.xpos.common.persistence.mybatis.ItemCategoryMapper;
import com.xpos.common.persistence.mybatis.ItemInventoryMapper;
import com.xpos.common.persistence.mybatis.ItemMapper;
import com.xpos.common.persistence.mybatis.PictureMapper;
import com.xpos.common.searcher.ItemSearcher;
import com.xpos.common.utils.Pager;

@Service
public class ItemServiceImpl implements ItemService{

	@Resource
	private ItemCategoryMapper itemCategoryMapper;
	
	@Resource
	private ItemMapper itemMapper;
	
	@Resource
	private ItemInventoryMapper itemInventoryMapper;
	
	@Resource
	private PictureService pictureService;
	
	@Resource
	private PictureMapper pictureMapper;
	
	@Override
	public List<ItemCategory> findCategoryByBusiness(Business business) {
		
		//如果启用菜单集中控制
		/*if(business instanceof Shop){
			Shop shop = (Shop)business;
			if(shop.getMerchant() != null)
			return findCategoryByBusiness(shop.getMerchant());
		}*/
		//默认商户自己维护自己的商品类别
		ItemCategoryExample example = new ItemCategoryExample();
		
		example.createCriteria().addCriterion("businessId=", business.getAccessBusinessId(Business.BusinessModel.MENU))
								.addCriterion("businessType=", business.getAccessBusinessType(Business.BusinessModel.MENU).toString())
								.addCriterion("deleted=", false);
		example.setOrderByClause(" sequence DESC, createDate DESC ");
		
		
		return itemCategoryMapper.selectByExample(example, null);
	
	}
	@Override
	public boolean addOrUpdateCategory(ItemCategory itemCategory) {
		if(itemCategory.getId() != null)
			return itemCategoryMapper.updateByPrimaryKey(itemCategory) > 0;
		else
			return itemCategoryMapper.insert(itemCategory) > 0;
	}
	
	@Transactional
	@Override
	public boolean addItem(Item item) {
		boolean result = true;
		result = itemMapper.insert(item) > 0;

		if(item.getCover()!=null){
			item.getCover().setForeignId(item.getId());
			item.getCover().setPictureType(PictureType.ITEM_COVER);
			result = result && pictureService.uploadPicture(item.getCover());
			result = result && itemMapper.updateByPrimaryKey(item) > 0;//更新cover_id字段
		}
		//添加商品时默认库存为0
		ItemInventory itemInventory = item.getItemInventory();
		if (itemInventory == null) {
			itemInventory= new ItemInventory();
			item.setItemInventory(itemInventory);
		}
		itemInventory.setItem(item);
		result = result && itemInventoryMapper.insert(itemInventory)>0;
		if (!result) {
			item.setId(null);
			item.setCover(null);
			throw new RuntimeException("添加商品失败");
		}
		return result;
	}
	
	@Override
	public ItemCategory findCategoryById(Long id) {
		return itemCategoryMapper.selectByPrimaryKey(id);
	}
	
	@Override
	public Item findItemById(Long id) {
		Item item = itemMapper.selectByPrimaryKey(id);
		if(item != null){
			ItemInventoryExample example = new ItemInventoryExample();
			example.createCriteria().addCriterion("item_id=", item.getId())
									.addCriterion("deleted=", false);
			ItemInventory itemInventory = itemInventoryMapper.selectOneByExample(example);
			item.setItemInventory(itemInventory);
		}
		return item;
	}
	
	@Override
	public boolean deleteCategory(ItemCategory category) throws Exception {
		ItemExample example = new ItemExample(); 
		example.createCriteria().addCriterion("category_id=", category.getId())
								.addCriterion("deleted=", false);
		int count = itemMapper.countByExample(example);
		
		//该类目有关联的有效商品,则无法删除
		if(count >0 )
		{
			throw new Exception("该类目下包含有未删除的商品,无法删除");
		}	
		
		category.setDeleted(true);
		return itemCategoryMapper.updateByPrimaryKey(category) > 0;
	}
	
	@Override
	public boolean deleteItem(Item item) {
		item.setDeleted(true);
		return itemMapper.updateByPrimaryKey(item) > 0;
	}
	
	@Override
	public boolean updateItem(Item item) {
		boolean result = true;

		if(item.getCover()!=null){
			item.getCover().setForeignId(item.getId());
			item.getCover().setPictureType(PictureType.ITEM_COVER);
			result = result && pictureService.uploadPicture(item.getCover());
		}

		result = result && itemMapper.updateByPrimaryKey(item) > 0;

		return result;
	}
	@Override
	public Pager<Item> searchItems(ItemSearcher searcher,  Pager<Item> pager) {
		
		List<Item> list = itemMapper.selectByExample(searcher.getExample(), pager);
		for (Item item : list) {
			ItemInventoryExample example = new ItemInventoryExample();
			example.createCriteria().addCriterion("item_id=", item.getId())
									.addCriterion("deleted=", false);
			ItemInventory itemInventory = itemInventoryMapper.selectOneByExample(example);
			item.setItemInventory(itemInventory);
		}
		int totalCount = itemMapper.countByExample(searcher.getExample());
		pager.setList(list);
		pager.setTotalCount(totalCount);
		return pager;
	}
	
	@Override
	public boolean isDuplicateItemCategorysByNameAndBusiness( ItemCategory itemCategory, Business business) {
		return itemCategoryMapper.findItemCategorysByNameAndBusiness( itemCategory.getName(), itemCategory.getId(), business.getSelfBusinessId(),business.getSelfBusinessType().toString()) > 0 ;
	}

}
