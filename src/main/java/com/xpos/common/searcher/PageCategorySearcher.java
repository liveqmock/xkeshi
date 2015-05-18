package com.xpos.common.searcher;

import org.apache.commons.lang3.StringUtils;

import com.xpos.common.entity.PageCategory;
import com.xpos.common.entity.example.Example;
import com.xpos.common.entity.example.Example.Criteria;
import com.xpos.common.entity.example.PageCategoryExample;

public class PageCategorySearcher  extends AbstractSearcher<PageCategory> {
    
	private String key  ;
	
	private Integer order  ;
	
	private String[] orderByClause = {"id ASC","id DESC","author ASC","author DESC"};
	
	@Override
	public Example<PageCategory> getExample() {
		example = new PageCategoryExample();
		Criteria createCriteria = example.createCriteria();
		if (StringUtils.isNotBlank(key)) 
		createCriteria.addCriterion("(title like  '%"+key+"%' or author like  '%"+key+"%' )");
		if(order != null && order<orderByClause.length)
			example.setOrderByClause(orderByClause[order]);
		else
			example.setOrderByClause(" id DESC");
		return (PageCategoryExample)example;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}

}
