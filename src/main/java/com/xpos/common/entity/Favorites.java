package com.xpos.common.entity;

import javax.persistence.Column;

import com.xpos.common.entity.security.User;

/**
 * 用户收藏
 * @author snoopy
 */
public class Favorites extends BaseEntity{

	private static final long serialVersionUID = 4519396350989010800L;

	@Column
	private User user;
	
	//shop/couponInfo
	@Column
	private FavoritesType type;
	
	@Column
	private Long businessId;
	
     
	public enum  FavoritesType {
		SHOP,COUPONINFO
	}
	
	
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}


	public FavoritesType getType() {
		return type;
	}

	public void setType(FavoritesType type) {
		this.type = type;
	}

	public Long getBusinessId() {
		return businessId;
	}

	public void setBusinessId(Long businessId) {
		this.businessId = businessId;
	} 
	
	
}
