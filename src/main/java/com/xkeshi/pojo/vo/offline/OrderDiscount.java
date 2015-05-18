package com.xkeshi.pojo.vo.offline;

public class OrderDiscount {
	
	private Long[] physicalCouponIds;
	
	private MemberDiscountDetailVO memberDiscount;

	public Long[] getPhysicalCouponIds() {
		return physicalCouponIds;
	}

	public void setPhysicalCouponIds(Long[] physicalCouponIds) {
		this.physicalCouponIds = physicalCouponIds;
	}

	public MemberDiscountDetailVO getMemberDiscount() {
		return memberDiscount;
	}

	public void setMemberDiscount(MemberDiscountDetailVO memberDiscount) {
		this.memberDiscount = memberDiscount;
	}
	
}
