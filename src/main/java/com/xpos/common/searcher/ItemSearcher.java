package com.xpos.common.searcher;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.xpos.common.entity.Item;
import com.xpos.common.entity.example.Example;
import com.xpos.common.entity.example.Example.Criteria;
import com.xpos.common.entity.example.ItemExample;
import com.xpos.common.entity.face.Business;
import com.xpos.common.entity.face.Business.BusinessModel;

public class ItemSearcher extends AbstractSearcher<Item>{
	
	private String key;
	
	private Business business;
	
	private List<Long> ids;
	
	private Long categoryId;
	
	private Boolean marketable;
	
	@Override
	public Example<Item> getExample() {

		example = new ItemExample();
		Criteria criteria = example.createCriteria();
		criteria.addCriterion("businessType=", business.getAccessBusinessType(BusinessModel.MENU).toString())
				.addCriterion("businessId=", business.getAccessBusinessId(BusinessModel.MENU))
				.addCriterion("deleted=",false);
		if(StringUtils.isNotBlank(key))
			criteria.addCriterion("(name like '%"+key.replace("'","")+"%')");
		if(categoryId!=null)
			criteria.addCriterion("category_id="+categoryId+"");
		if(marketable != null)
			criteria.addCriterion("marketable =", marketable);
		if(ids != null && ids.size() > 0){
			String orsql = "(";
			int orCount = 0;
			for(Long id : ids){
				if(orCount!=0)
					orsql+=" OR ";
				orCount++;
				orsql+= ("id=" + id);
			}
			orsql+=")";
			criteria.addCriterion(orsql);
		}
		example.setOrderByClause(" sequence DESC, createDate DESC ");
		return (ItemExample) example;
	}
	
	public boolean getHasParameter() {
		return StringUtils.isNotBlank(key)||categoryId!=null||marketable != null;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Business getBusiness() {
		return business;
	}

	public void setBusiness(Business business) {
		this.business = business;
	}

	public List<Long> getIds() {
		return ids;
	}

	public void setIds(List<Long> ids) {
		this.ids = ids;
	}

	public Long getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}

	public Boolean getMarketable() {
		return marketable;
	}

	public void setMarketable(Boolean marketable) {
		this.marketable = marketable;
	}

}
