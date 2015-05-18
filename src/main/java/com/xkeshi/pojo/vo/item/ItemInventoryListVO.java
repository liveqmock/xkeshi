package com.xkeshi.pojo.vo.item;

import java.util.ArrayList;
import java.util.List;

/**
 * @author snoopy
 *
 */
public class ItemInventoryListVO{
	
	private List<ItemInventoryVO> itemInventoryList = new ArrayList<ItemInventoryVO>();
	
	public ItemInventoryListVO() {
		
	}
	public ItemInventoryListVO(List<ItemInventoryVO> itemInventoryList) {
		this.itemInventoryList = itemInventoryList;
	}
	public List<ItemInventoryVO> getItemInventoryList() {
		return itemInventoryList;
	}

	public void setItemInventoryList(List<ItemInventoryVO> itemInventoryList) {
		this.itemInventoryList = itemInventoryList;
	}


}