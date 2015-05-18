package com.xpos.common.entity.member;

import com.xpos.common.entity.Merchant;
import com.xpos.common.entity.face.Business;
import com.xpos.common.entity.face.Business.BusinessType;

public class MerchantMemberType extends MemberType{

	private static final long serialVersionUID = -612847925946222660L;
	
	private Merchant merchant;
	
	public Merchant getMerchant() {
		return merchant;
	}

	public void setMerchant(Merchant merchant) {
		this.merchant = merchant;
	}
	
	@Override
	public Business getBusiness(){
		return merchant;
	}

	@Override
	public Long getBusinessId() {
		return merchant.getId();
	}

	@Override
	public BusinessType getBusinessType() {
		return BusinessType.MERCHANT;
	}
	
}
