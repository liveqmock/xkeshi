package com.xpos.common.searcher;

import org.apache.commons.lang3.StringUtils;

import com.xpos.common.entity.PageTemplate;
import com.xpos.common.entity.example.Example;
import com.xpos.common.entity.example.Example.Criteria;
import com.xpos.common.entity.example.PageTemplateExample;

public class PageTemplateSearcher  extends AbstractSearcher<PageTemplate> {
    
	private String key  ;
	
	private Integer order  ;
	
	private String[] orderByClause = {"id ASC","id DESC","author ASC","author DESC"};
	
	@Override
	public Example<PageTemplate> getExample() {
		example = new PageTemplateExample();
		Criteria createCriteria = example.createCriteria();
		if (StringUtils.isNotBlank(key)) 
		createCriteria.addCriterion("(title like  '%"+key+"%')");
		
		if(order != null && order<orderByClause.length)
			example.setOrderByClause(orderByClause[order]);
		else
			example.setOrderByClause(" id DESC");
		return (PageTemplateExample)example;
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
