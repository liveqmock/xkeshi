package com.xpos.common.searcher.member;

import com.xpos.common.entity.member.MemberType;
import com.xpos.common.entity.member.MerchantMemberType;

public class MerchantMemberSearcher extends MemberSearcher{
	private MemberType memberType = new MerchantMemberType();

	public MemberType getMemberType() {
		return memberType;
	}

	public void setMemberType(MemberType memberType) {
		this.memberType = memberType;
	}
	
	public boolean getHasParameter(){
		return super.getHasParameter() || (getShop() != null && getShop().getId() != null) || (memberType != null && memberType.getId() != null);
	}
	
}
