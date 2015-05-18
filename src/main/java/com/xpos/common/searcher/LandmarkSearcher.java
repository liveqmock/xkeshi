package com.xpos.common.searcher;

import org.apache.commons.lang.StringUtils;

import com.xpos.common.entity.Landmark;
import com.xpos.common.entity.example.Example;
import com.xpos.common.entity.example.Example.Criteria;
import com.xpos.common.entity.example.LandmarkExample;

public class LandmarkSearcher extends AbstractSearcher<Landmark>{
	
	private String key;
	
	
	@Override
	public Example<Landmark> getExample() {

		example = new LandmarkExample();
		Criteria criteria = example.createCriteria();
		if(StringUtils.isNotBlank(key))
			criteria.addCriterion("(name like '%"+key+"%')");
	
		return (LandmarkExample) example;
	}

	public String getKey() {
		return key;
	}


	public void setKey(String key) {
		this.key = key;
	}

	
	 
}
