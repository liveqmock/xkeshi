package com.xpos.common.entity.member;

import java.math.BigDecimal;

import javax.persistence.Column;

import com.xpos.common.entity.NewBaseEntity;
import com.xpos.common.entity.Shop;

public class MerchantShopMemberDiscount extends NewBaseEntity {

	private static final long serialVersionUID = 6119524022832967027L;

	@Column
	private MerchantMemberType merchantMemberType;
	
	@Column
	private Shop shop;
	
	@Column
	private BigDecimal discount;

	public MerchantMemberType getMerchantMemberType() {
		return merchantMemberType;
	}

	public void setMerchantMemberType(MerchantMemberType merchantMemberType) {
		this.merchantMemberType = merchantMemberType;
	}

	public Shop getShop() {
		return shop;
	}

	public void setShop(Shop shop) {
		this.shop = shop;
	}

	public BigDecimal getDiscount() {
		return discount;
	}

	public void setDiscount(BigDecimal discount) {
		this.discount = discount;
	}
	
}
