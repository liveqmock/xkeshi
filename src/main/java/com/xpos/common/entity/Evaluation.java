package com.xpos.common.entity;

import javax.persistence.Column;

import com.xpos.common.entity.security.User;

/**
 * 用户点评
 * @author snoopy
 */
public class Evaluation extends BaseEntity{

	private static final long serialVersionUID = 6294861122569550068L;

	@Column
	private User user;
	
	//shop/coupon
	@Column
	private EvaluationType type;
	
	@Column
	private Long businessId;
	
	@Column
	private double stars;
	
	@Column
	private String content;
	
	
	public enum EvaluationType {
		SHOP,COUPON
	}
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}


	public EvaluationType getType() {
		return type;
	}

	public void setType(EvaluationType type) {
		this.type = type;
	}

	public Long getBusinessId() {
		return businessId;
	}

	public void setBusinessId(Long businessId) {
		this.businessId = businessId;
	}

	public double getStars() {
		return stars;
	}

	public void setStars(double stars) {
		this.stars = stars;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	} 
	
	
	
}
