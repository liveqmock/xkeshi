package com.xpos.common.searcher;


import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;

import com.xpos.common.entity.Merchant;
import com.xpos.common.entity.example.Example;
import com.xpos.common.entity.example.Example.Criteria;
import com.xpos.common.entity.example.MerchantExample;

public class MerchantSearcher extends AbstractSearcher<Merchant>{
	
	private String key;
	
	private Date createStartDate;
	
	private Date createEndDate;
	
	private Date modifyStartDate ;
	
	private Date modifyEndDate ;
	
	public boolean getHasParameter(){
			return StringUtils.isNotBlank(key) || modifyStartDate != null || modifyEndDate != null  || createStartDate != null || createEndDate !=null;
	}
	
	@Override
	public Example<Merchant> getExample() {
		example = new MerchantExample();
		Criteria criteria = example.createCriteria();
	
		if ( createStartDate !=null ) 
			criteria.addCriterion(" createDate   >= ",new DateTime(createStartDate).toString("yyyy-MM-dd HH:mm:ss"));
		
		if ( createEndDate !=  null) 
			criteria.addCriterion(" createDate   <=  ",new DateTime(createEndDate).toString("yyyy-MM-dd HH:mm:ss"));
		
		if ( modifyStartDate !=null ) 
			criteria.addCriterion(" modifyDate   >= ",new DateTime(modifyStartDate).toString("yyyy-MM-dd HH:mm:ss"));
		
		if ( modifyEndDate !=  null) 
			criteria.addCriterion(" modifyDate   <=  ",new DateTime(createEndDate).toString("yyyy-MM-dd HH:mm:ss"));

		if (StringUtils.isNotBlank(key))
			criteria.addCriterion("fullName like '%"+key+"%'");
		
		return (MerchantExample) example;
	}

	public String getKey() {
		return key;
	}


	public void setKey(String key) {
		this.key = key;
	}
 

	public Date getModifyStartDate() {
		return modifyStartDate;
	}

	public void setModifyStartDate(Date modifyStartDate) {
		this.modifyStartDate = modifyStartDate;
	}

	public Date getModifyEndDate() {
		return modifyEndDate;
	}

	public void setModifyEndDate(Date modifyEndDate) {
		this.modifyEndDate = modifyEndDate;
	}

	public Date getCreateStartDate() {
		return createStartDate;
	}

	public void setCreateStartDate(Date createStartDate) {
		this.createStartDate = createStartDate;
	}

	public Date getCreateEndDate() {
		return createEndDate;
	}

	public void setCreateEndDate(Date createEndDate) {
		this.createEndDate = createEndDate;
	}

}
