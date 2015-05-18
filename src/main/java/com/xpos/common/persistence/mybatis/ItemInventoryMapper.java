package com.xpos.common.persistence.mybatis;

import org.apache.ibatis.annotations.Param;

import com.xpos.common.entity.itemInventory.ItemInventory;
import com.xpos.common.persistence.BaseMapper;

public interface ItemInventoryMapper extends BaseMapper<ItemInventory>{
	
	
	int update(@Param("id")Long id, @Param("inventoryType")boolean inventoryType,@Param("quantity") int quantity);
	
	int updateByItemId(@Param("itemId")Long itemId, @Param("quantity") int quantity);

	int updateAddByItemId(@Param("itemId")Long itemId, @Param("quantity") int quantity);


}
