package com.xpos.common.searcher;

import com.xpos.common.entity.Favorites;
import com.xpos.common.entity.Favorites.FavoritesType;
import com.xpos.common.entity.example.Example;
import com.xpos.common.entity.example.Example.Criteria;
import com.xpos.common.entity.example.FavoritesExample;

public class FavoritesSearcher extends AbstractSearcher<Favorites>{

	private FavoritesType type;
	
	private Long   userId ;
	
	private String uniqueNo;
	
	private Long businessId  ;
	
	
	
	public String getUniqueNo() {
		return uniqueNo;
	}

	public void setUniqueNo(String uniqueNo) {
		this.uniqueNo = uniqueNo;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getBusinessId() {
		return businessId;
	}

	public void setBusinessId(Long businessId) {
		this.businessId = businessId;
	}

	public FavoritesType getType() {
		return type;
	}

	public void setType(FavoritesType type) {
		this.type = type;
	}

	@Override
	public Example<?> getExample() {
		example = new FavoritesExample();
		Criteria criteria = example.createCriteria();
		if(type != null)
			criteria.addCriterion("type = ", type.toString());
		if (userId != null)
			criteria.addCriterion("user_id=",userId);
		if(uniqueNo != null) {
			criteria.addCriterion("user_id = (select id from User where uniqueNo='"+uniqueNo+"')");
		}
		if (businessId != null)
			criteria.addCriterion("businessId=", businessId);
			example.setOrderByClause(" createDate DESC");
		return (FavoritesExample)example;
	}

}
