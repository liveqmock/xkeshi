package com.xpos.common.persistence.mybatis;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.xpos.common.entity.itemInventory.ItemInventoryChangeRecord;
import com.xpos.common.persistence.BaseMapper;
import com.xpos.common.searcher.ItemInventoryChangeRecordSearcher;
import com.xpos.common.utils.Pager;

public interface ItemInventoryChangeRecordMapper extends BaseMapper<ItemInventoryChangeRecord>{

	int insertChangeRecord(ItemInventoryChangeRecord iicr);
	List<ItemInventoryChangeRecord> selectByMerchantId(@Param("merchantId")Long merchantId, @Param("pager")Pager<ItemInventoryChangeRecord> pager);
	int countByMerchantId(Long merchantId);
	List<ItemInventoryChangeRecord> selectByCondition(@Param("merchantId")Long merchantId, @Param("seacher")ItemInventoryChangeRecordSearcher seacher, @Param("pager")Pager<ItemInventoryChangeRecord> pager);
	int countByCondition(@Param("merchantId")Long merchantId, @Param("seacher")ItemInventoryChangeRecordSearcher seacher, @Param("pager")Pager<ItemInventoryChangeRecord> pager);
}
