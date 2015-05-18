package com.xpos.common.persistence.mybatis;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.xpos.common.entity.itemInventory.ItemInventoryChangeDetail;
import com.xpos.common.persistence.BaseMapper;

public interface ItemInventoryChangeDetailMapper extends BaseMapper<ItemInventoryChangeDetail>{

	int insertChangeDetailList(@Param("detailList")List<ItemInventoryChangeDetail> itemInventoryChangeDetailList);

}
