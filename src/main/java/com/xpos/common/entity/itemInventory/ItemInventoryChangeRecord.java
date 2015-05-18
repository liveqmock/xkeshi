package com.xpos.common.entity.itemInventory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Table;

import com.xpos.common.entity.BaseEntity;
import com.xpos.common.entity.Shop;
import com.xpos.common.entity.face.Business.BusinessType;
import com.xpos.common.entity.face.EncryptId;
import com.xpos.common.entity.security.Account;

/**
 * 商品库存变更记录
 * @author snoopy
 */
@Table(name="item_inventory_change_record")
public class ItemInventoryChangeRecord extends BaseEntity implements EncryptId{

	private static final long serialVersionUID = -1374848308220897815L;

	/**
	 * 关联account表
	 * */
	@Column
	private Account account;
	
	/**
	 * 商户Id
	 */
	@Column
	private Long businessId;
	
	/**
	 * 商户类型
	 */
	@Column
	private BusinessType businessType;
	
	/**
	 * 入库的商品数量
	 */
	@Column(name="import_item_quantity")
	private Integer importItemQuantity; 
	
	/**
	 * 出库的商品数量
	 */
	@Column(name="export_item_quantity")
	private Integer exportItemQuantity; 
	/**
	 * 入库总数量
	 */
	@Column(name="import_total_quantity")
	private Integer importTotalQuantity; 
	/**
	 * 出库总数量
	 */
	@Column(name="export_total_quantity")
	private Integer exportTotalQuantity;
	
	/**
	 * 出入库明细List
	 */
	private List<ItemInventoryChangeDetail> itemInventoryChangeDetailList = new ArrayList<>();
	
	/**
	 * 商户名称
	 */
	private Shop shop;
	

	/**
	 * 操作时间
	 */
	private Date createdDate;
	
	
	public Account getAccount() {
		return account;
	}
	public void setAccount(Account account) {
		this.account = account;
	}
	public Long getBusinessId() {
		return businessId;
	}
	public void setBusinessId(Long businessId) {
		this.businessId = businessId;
	}
	public BusinessType getBusinessType() {
		return businessType;
	}
	public void setBusinessType(BusinessType businessType) {
		this.businessType = businessType;
	}
	public Integer getImportItemQuantity() {
		return importItemQuantity;
	}
	public void setImportItemQuantity(Integer importItemQuantity) {
		this.importItemQuantity = importItemQuantity;
	}
	public Integer getExportItemQuantity() {
		return exportItemQuantity;
	}
	public void setExportItemQuantity(Integer exportItemQuantity) {
		this.exportItemQuantity = exportItemQuantity;
	}
	public Integer getImportTotalQuantity() {
		return importTotalQuantity;
	}
	public void setImportTotalQuantity(Integer importTotalQuantity) {
		this.importTotalQuantity = importTotalQuantity;
	}
	public Integer getExportTotalQuantity() {
		return exportTotalQuantity;
	}
	public void setExportTotalQuantity(Integer exportTotalQuantity) {
		this.exportTotalQuantity = exportTotalQuantity;
	}
	public List<ItemInventoryChangeDetail> getItemInventoryChangeDetailList() {
		return itemInventoryChangeDetailList;
	}
	public void setItemInventoryChangeDetailList(
			List<ItemInventoryChangeDetail> itemInventoryChangeDetailList) {
		this.itemInventoryChangeDetailList = itemInventoryChangeDetailList;
	}
	public boolean addInventoryChangeDetail(ItemInventoryChangeDetail detail){
		return itemInventoryChangeDetailList.add(detail);
	}
	public Shop getShop() {
		return shop;
	}
	public void setShop(Shop shop) {
		this.shop = shop;
	}
	public Date getCreatedDate() {
		return createDate;
	}
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
	
	
}
