package com.xpos.common.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.xpos.common.entity.Item;
import com.xpos.common.entity.Shop;
import com.xpos.common.entity.example.ItemInventoryChangeDetailExample;
import com.xpos.common.entity.example.ItemInventoryExample;
import com.xpos.common.entity.face.Business;
import com.xpos.common.entity.face.Business.BusinessType;
import com.xpos.common.entity.itemInventory.ItemInventory;
import com.xpos.common.entity.itemInventory.ItemInventoryChangeDetail;
import com.xpos.common.entity.itemInventory.ItemInventoryChangeRecord;
import com.xpos.common.entity.security.Account;
import com.xpos.common.persistence.mybatis.ItemInventoryChangeDetailMapper;
import com.xpos.common.persistence.mybatis.ItemInventoryChangeRecordMapper;
import com.xpos.common.persistence.mybatis.ItemInventoryMapper;
import com.xpos.common.persistence.mybatis.ItemMapper;
import com.xpos.common.persistence.mybatis.ShopMapper;
import com.xpos.common.searcher.ItemInventoryChangeRecordSearcher;
import com.xpos.common.utils.Pager;

@Service
public class ItemInventoryServiceImpl implements ItemInventoryService{

	
	@Resource
	private ItemMapper itemMapper;
	
	@Resource
	private ItemInventoryMapper itemInventoryMapper;
	
	@Resource
	private ItemInventoryChangeDetailMapper itemInventoryChangeDetailMapper;
	
	@Resource
	private ItemInventoryChangeRecordMapper itemInventoryChangeRecordMapper;
	
	@Resource
	private ShopMapper shopMapper;

	@Override
	public Map<ItemInventoryChangeDetail, String> batchAdd(List<ItemInventoryChangeDetail> itemInventoryChangeDetailList,Business business, Account account) {
		Map<ItemInventoryChangeDetail, String> failMap = new HashMap<ItemInventoryChangeDetail, String>();
		ItemInventoryChangeRecord iicr = new ItemInventoryChangeRecord();
		int importItemQuantity = 0;//入库商品数
		int exportItemQuantity = 0;//出库商品数
		int importTotalQuantity = 0;//入库商品总数量
		int exportTotalQuantity = 0;//出库商品总数量
		for(ItemInventoryChangeDetail iicd : itemInventoryChangeDetailList) {
			//校验权限
			Item item = itemMapper.selectByPrimaryKey(iicd.getItem().getId());
			if(item == null || !business.getAccessBusinessType(Business.BusinessModel.MENU).equals(item.getBusinessType()) 
					|| !business.getAccessBusinessId(Business.BusinessModel.MENU).equals(item.getBusinessId())){
				iicd.setItem(item);
				failMap.put(iicd, "权限校验未通过");
				continue;
			}else if(iicd.getQuantity()==null || iicd.getQuantity()<=0 || iicd.getQuantity()>99999) {
				iicd.setItem(item);
				failMap.put(iicd, "库存变化范围为-99999至+99999");
				continue;
			}
			ItemInventoryExample example = new ItemInventoryExample();
			example.createCriteria().addCriterion("item_id=", item.getId())
									.addCriterion("deleted=", false);
			//查询当前库存
			ItemInventory itemInventory = itemInventoryMapper.selectOneByExample(example);
			try {
				itemInventoryMapper.update(itemInventory.getId(),iicd.isInventoryType(),Math.abs(iicd.getQuantity()));
			} catch (Exception e) {
				iicd.setItem(item);
				failMap.put(iicd, "出库后数量为负");
				continue;
			}
			iicd.setBeforeChangeQuantity(itemInventory.getInventory());
			if (iicd.isInventoryType()) {
				iicd.setAfterChangeQuantity(itemInventory.getInventory()+iicd.getQuantity());
				importItemQuantity++;
				importTotalQuantity+=Math.abs(iicd.getQuantity());
			}else {
				iicd.setAfterChangeQuantity(itemInventory.getInventory()-iicd.getQuantity());
				exportItemQuantity++;
				exportTotalQuantity+=Math.abs(iicd.getQuantity());
			}
			iicd.setItemInventoryChangeRecord(iicr);
			iicr.addInventoryChangeDetail(iicd);
		}
		
		if(iicr.getItemInventoryChangeDetailList().size() > 0){
			iicr.setImportItemQuantity(importItemQuantity);
			iicr.setExportItemQuantity(exportItemQuantity);
			iicr.setImportTotalQuantity(importTotalQuantity);
			iicr.setExportTotalQuantity(exportTotalQuantity);
			iicr.setBusinessType(business.getSelfBusinessType());
			iicr.setBusinessId(business.getSelfBusinessId());
			iicr.setAccount(account);
			boolean result = itemInventoryChangeRecordMapper.insertChangeRecord(iicr)>0;
			result &= itemInventoryChangeDetailMapper.insertChangeDetailList(iicr.getItemInventoryChangeDetailList())
					== iicr.getItemInventoryChangeDetailList().size();
		}
		return failMap;
	}
	
	@Override
	public Pager<ItemInventoryChangeRecord> searchItemInventorys(
			ItemInventoryChangeRecordSearcher searcher,
			Pager<ItemInventoryChangeRecord> pager) {
		List<ItemInventoryChangeRecord> list = new ArrayList<ItemInventoryChangeRecord>();
		int totalCount = 0 ;
		if(BusinessType.MERCHANT.equals(searcher.getBusiness().getSelfBusinessType())){
//			List<ItemInventoryChangeRecord> llist = itemInventoryChangeRecordMapper.selectByMerchantId(searcher.getBusiness().getSelfBusinessId(),pager);
			List<ItemInventoryChangeRecord> llist = itemInventoryChangeRecordMapper.selectByCondition(searcher.getBusiness().getSelfBusinessId(), searcher,pager);
//			totalCount = itemInventoryChangeRecordMapper.countByMerchantId(searcher.getBusiness().getSelfBusinessId());
			itemInventoryChangeRecordMapper.countByCondition(searcher.getBusiness().getSelfBusinessId(), searcher, pager);
			list.addAll(llist);
		}else {
			list = itemInventoryChangeRecordMapper.selectByExample(searcher.getExample(), pager);
			totalCount = itemInventoryChangeRecordMapper.countByExample(searcher.getExample());
		}
		pager.setList(list);
		pager.setTotalCount(totalCount);
		return pager;
	}

	@Override
	public ItemInventoryChangeRecord selectItemInventoryChangeRecordById(Long id) {
		return itemInventoryChangeRecordMapper.selectByPrimaryKey(id);
	}

	@Override
	public List<ItemInventoryChangeDetail> findDetaiListByRecordId(Long id,String key) {
		ItemInventoryChangeDetailExample example = new ItemInventoryChangeDetailExample();
		example.createCriteria().addCriterion("item_inventory_change_record_id=", id)
								.addCriterion("deleted=", false);
		if(StringUtils.isNotBlank(key)) {
			example.appendCriterion("item_id in (select id from Item where name like '%"+key.trim().replace("'","")+"%')");
		}
		return itemInventoryChangeDetailMapper.selectByExample(example, null);
	}

	
	
	

}
