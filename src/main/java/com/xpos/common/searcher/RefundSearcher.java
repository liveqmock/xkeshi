package com.xpos.common.searcher;

import org.apache.commons.lang.StringUtils;

import com.xpos.common.entity.Refund;
import com.xpos.common.entity.example.Example;
import com.xpos.common.entity.example.Example.Criteria;
import com.xpos.common.entity.example.RefundExample;

public class RefundSearcher extends AbstractSearcher<Refund>{

	private String key;
	private String status; //TODO
	private Integer order;
	private final String[] orderByClause = {"id ASC","id DESC","creatDate ASC","createDate DESC" };
	
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}

	@Override
	public Example<?> getExample() {
		example = new RefundExample();
		Criteria criteria = example.createCriteria();
		if(StringUtils.isNotBlank(status)){
			String orsql = "(";
			int orCount = 0;
			for(String str : StringUtils.split(status, ',')){
				if(orCount!=0)
					orsql+=" OR ";
				orCount++;
				orsql+= ("status = '" + str + "'");
			}
			orsql+=")";
			criteria.addCriterion(orsql);
		}
		if(order != null && order > 0){
			example.setOrderByClause(orderByClause[order]);
		}else{
			example.setOrderByClause(" id DESC");
		}
		return (RefundExample)example;
	}

}
