package com.xkeshi.pojo.vo.item;

/**
 * @author snoopy
 *
 */
public class ItemInventoryVO{
	
	private long itemId;
	
	private int inventory;
	
	public ItemInventoryVO(){
		
	}
	
	public ItemInventoryVO(long itemId, int inventory){
		this.itemId = itemId;
		this.inventory = inventory;
	}

	public long getItemId() {
		return itemId;
	}

	public void setItemId(long itemId) {
		this.itemId = itemId;
	}

	public int getInventory() {
		return inventory;
	}

	public void setInventory(int inventory) {
		this.inventory = inventory;
	}

}