package com.xpos.common.entity.itemInventory;

import javax.persistence.Column;
import javax.persistence.Table;

import com.xpos.common.entity.BaseEntity;
import com.xpos.common.entity.Item;
import com.xpos.common.entity.face.EncryptId;

/**
 * 商品库存变更记录详情
 * @author snoopy
 */
@Table(name="item_inventory_change_detail")
public class ItemInventoryChangeDetail extends BaseEntity implements EncryptId{

	private static final long serialVersionUID = -1374848308220897815L;
	public static final boolean INVENTORY_TYPE_IMPORT = true;
	public static final boolean INVENTORY_TYPE_EXPORT = false;

	/**
	 * 关联item_inventory_change_record表
	 * */
	@Column
	private ItemInventoryChangeRecord itemInventoryChangeRecord;
	
	/**
	 * 关联item表
	 */
	@Column
	private Item item;
	
	/**
	 * 出入库类型(true:入库，false:出库)
	 */
	@Column(name="inventory_type")
	private boolean inventoryType;
	
	/**
	 * 变更数量
	 */
	@Column
	private Integer quantity; 
	
	/**
	 * 变更前数量
	 */
	@Column(name="before_change_quantity")
	private Integer beforeChangeQuantity; 
	/**
	 * 变更后数量
	 */
	@Column(name="after_change_quantity")
	private Integer afterChangeQuantity;
	public ItemInventoryChangeRecord getItemInventoryChangeRecord() {
		return itemInventoryChangeRecord;
	}
	public void setItemInventoryChangeRecord(
			ItemInventoryChangeRecord itemInventoryChangeRecord) {
		this.itemInventoryChangeRecord = itemInventoryChangeRecord;
	}
	public Item getItem() {
		return item;
	}
	public void setItem(Item item) {
		this.item = item;
	}
	public boolean isInventoryType() {
		return inventoryType;
	}
	public void setInventoryType(boolean inventoryType) {
		this.inventoryType = inventoryType;
	}
	public Integer getQuantity() {
		return quantity;
	}
	public void setQuantity(Integer quantity) {
		if(quantity!=null) {
			if(quantity>0) {
				this.quantity = quantity;
			}else{
				this.quantity = Math.abs(quantity);
			}
		}
	}
	public Integer getBeforeChangeQuantity() {
		return beforeChangeQuantity;
	}
	public void setBeforeChangeQuantity(Integer beforeChangeQuantity) {
		this.beforeChangeQuantity = beforeChangeQuantity;
	}
	public Integer getAfterChangeQuantity() {
		return afterChangeQuantity;
	}
	public void setAfterChangeQuantity(Integer afterChangeQuantity) {
		this.afterChangeQuantity = afterChangeQuantity;
	} 
	
	
	
}
