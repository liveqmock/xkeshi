package com.xpos.common.searcher;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;

import com.xpos.common.entity.example.Example;
import com.xpos.common.entity.example.Example.Criteria;
import com.xpos.common.entity.example.ItemInventoryChangeRecordExample;
import com.xpos.common.entity.face.Business;
import com.xpos.common.entity.face.Business.BusinessModel;
import com.xpos.common.entity.itemInventory.ItemInventoryChangeRecord;

public class ItemInventoryChangeRecordSearcher extends AbstractSearcher<ItemInventoryChangeRecord>{
	
	private String accountName;//操作员
	
	private Business business;//被操作商户
	
	private Date startDateTime;//起始操作时间
	
	private Date endDateTime;//截止操作时间
	
	private String shopName;//操作商户
	
	@Override
	public Example<ItemInventoryChangeRecord> getExample() {

		example = new ItemInventoryChangeRecordExample();
		Criteria criteria = example.createCriteria();
		criteria.addCriterion("businessType=", business.getAccessBusinessType(BusinessModel.MENU).toString())
				.addCriterion("businessId=", business.getAccessBusinessId(BusinessModel.MENU))
				.addCriterion("deleted=",false);
		if(StringUtils.isNotBlank(accountName))
			criteria.addCriterion("account_id in (select id from Account where username like '%"+accountName.replace("'","")+"%' )");
		if(StringUtils.isNotBlank(shopName))
			criteria.addCriterion("businessId in (select id from Shop where name like '%"+shopName+"%' )");
		if(startDateTime != null){
			criteria.addCriterion("createDate >= ", new DateTime(startDateTime).toString("yyyy-MM-dd HH:mm:ss"));
		}
		if(endDateTime != null){
			criteria.addCriterion("createDate <= ",  new DateTime(endDateTime).toString("yyyy-MM-dd HH:mm:ss"));
		}
		example.setOrderByClause(" createDate DESC  ");
		return (ItemInventoryChangeRecordExample) example;
	}
	
	public boolean getHasParameter() {
		return StringUtils.isNotBlank(accountName) || startDateTime != null || endDateTime != null ||StringUtils.isNotBlank(shopName) ;
	}
	public Business getBusiness() {
		return business;
	}
	public void setBusiness(Business business) {
		this.business = business;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public Date getStartDateTime() {
		return startDateTime;
	}

	public void setStartDateTime(Date startDateTime) {
		this.startDateTime = startDateTime;
	}

	public Date getEndDateTime() {
		return endDateTime;
	}

	public void setEndDateTime(Date endDateTime) {
		this.endDateTime = endDateTime;
	}

	public String getShopName() {
		return shopName;
	}

	public void setShopName(String shopName) {
		this.shopName = shopName;
	}
	

	
}
