package com.xpos.common.exception;

public class CouponInfoException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public CouponInfoException() {
		super("优惠券删除、失效或未发布");
		 
	}

	public CouponInfoException(String arg0) {
		super(arg0);
	}

}
