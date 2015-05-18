package com.xpos.common.entity.member;

import com.xpos.common.entity.Shop;
import com.xpos.common.entity.face.Business;
import com.xpos.common.entity.face.Business.BusinessType;

public class ShopMemberType extends MemberType{

	private static final long serialVersionUID = 7120603301579700012L;

	private Shop shop;

	public Shop getShop() {
		return shop;
	}

	public void setShop(Shop shop) {
		this.shop = shop;
	}
	
	@Override
	public Business getBusiness(){
		return shop;
	}

	@Override
	public Long getBusinessId() {
		return shop.getId();
	}

	@Override
	public BusinessType getBusinessType() {
		return BusinessType.SHOP;
	}
	
}
