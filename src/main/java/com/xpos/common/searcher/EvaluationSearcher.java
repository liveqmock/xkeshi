package com.xpos.common.searcher;

import com.xpos.common.entity.Evaluation;
import com.xpos.common.entity.Evaluation.EvaluationType;
import com.xpos.common.entity.example.EvaluationExample;
import com.xpos.common.entity.example.Example;
import com.xpos.common.entity.example.Example.Criteria;

public class EvaluationSearcher extends AbstractSearcher<Evaluation>{

	private EvaluationType type;
	
	private Long   userId ;
	
	private Long businessId  ;
	
	private String content;
	
	private String stars;
	
	private String uniqueNo;
	
	
	
	public String getUniqueNo() {
		return uniqueNo;
	}

	public void setUniqueNo(String uniqueNo) {
		this.uniqueNo = uniqueNo;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getStars() {
		return stars;
	}

	public void setStars(String stars) {
		this.stars = stars;
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

	public EvaluationType getType() {
		return type;
	}

	public void setType(EvaluationType type) {
		this.type = type;
	}

	@Override
	public Example<?> getExample() {
		example = new EvaluationExample();
		Criteria criteria = example.createCriteria();
		if(type != null)
			criteria.addCriterion("type = ", type.toString());
		if (userId != null)
			criteria.addCriterion("user_id=",userId);
		if(uniqueNo != null){
			criteria.addCriterion("user_id = (select id from User where uniqueNo='"+uniqueNo+"')");
		}
		if (businessId != null)
			criteria.addCriterion("businessId=", businessId);
			example.setOrderByClause(" createDate DESC");
		return (EvaluationExample)example;
	}

}
