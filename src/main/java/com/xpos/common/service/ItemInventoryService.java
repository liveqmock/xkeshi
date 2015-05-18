package com.xpos.common.service;

import java.util.List;
import java.util.Map;

import com.xpos.common.entity.face.Business;
import com.xpos.common.entity.itemInventory.ItemInventoryChangeDetail;
import com.xpos.common.entity.itemInventory.ItemInventoryChangeRecord;
import com.xpos.common.entity.security.Account;
import com.xpos.common.searcher.ItemInventoryChangeRecordSearcher;
import com.xpos.common.utils.Pager;

public interface ItemInventoryService {
	
	Map<ItemInventoryChangeDetail, String> batchAdd(List<ItemInventoryChangeDetail> itemInventoryChangeDetailList, Business business, Account account);

	Pager<ItemInventoryChangeRecord> searchItemInventorys(ItemInventoryChangeRecordSearcher searcher,Pager<ItemInventoryChangeRecord> pager);

	ItemInventoryChangeRecord selectItemInventoryChangeRecordById(Long id);

	List<ItemInventoryChangeDetail> findDetaiListByRecordId(Long id, String key);
	
}
