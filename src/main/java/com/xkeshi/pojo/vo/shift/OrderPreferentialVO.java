package com.xkeshi.pojo.vo.shift;

import java.util.List;

/**
 * 
 * @author xk
 * 订单优惠
 * 抵现金、
 */
public class OrderPreferentialVO {

	/*实体券*/
	private List<OrderPhysicalCouponVO>  orderPhysicalCouponList ;

	/*会员折扣*/
	private MemberDiscountVO  memberDicount ;

	
	
	public OrderPreferentialVO() {
		super();
	}

	public OrderPreferentialVO(
			List<OrderPhysicalCouponVO> orderPhysicalCouponList,
			MemberDiscountVO memberDicount) {
		super();
		this.orderPhysicalCouponList = orderPhysicalCouponList;
		this.memberDicount = memberDicount;
	}

	public MemberDiscountVO getMemberDicount() {
		return memberDicount;
	}

	public void setMemberDicount(MemberDiscountVO memberDicount) {
		this.memberDicount = memberDicount;
	}
	
	public List<OrderPhysicalCouponVO> getOrderPhysicalCouponList() {
		return orderPhysicalCouponList;
	}

	public void setOrderPhysicalCouponList(
			List<OrderPhysicalCouponVO> orderPhysicalCouponList) {
		this.orderPhysicalCouponList = orderPhysicalCouponList;
	}
	
}
