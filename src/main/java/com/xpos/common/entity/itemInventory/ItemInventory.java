package com.xpos.common.entity.itemInventory;

import javax.persistence.Column;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.xpos.common.entity.BaseEntity;
import com.xpos.common.entity.Item;
import com.xpos.common.entity.face.EncryptId;

/**
 * 商品库存
 * @author snoopy
 */
@Table(name="item_inventory")
public class ItemInventory extends BaseEntity implements EncryptId{

	private static final long serialVersionUID = 108958329873844027L;
	
	/**
	 * 关联item表
	 * */
	@Column
	@JsonIgnore
	private Item item;
	
	/**
	 * 商品库存
	 */
	@Column
	private Integer inventory;
	

	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	public Integer getInventory() {
		return inventory;
	}

	public void setInventory(Integer inventory) {
		this.inventory = inventory;
	}

}
