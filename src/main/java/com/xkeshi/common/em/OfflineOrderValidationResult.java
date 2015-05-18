package com.xkeshi.common.em;

public enum OfflineOrderValidationResult {
	ORDER_ALREADY_EXISTS("1000","订单已存在"),
	ORDER_IS_NULL("1001", "订单内容不能为空"),
	ORDER_NUMBER_IS_NULL("1002", "订单号不能为空"),
	ORDER_TYPE_INVALID("1003", "订单类型不匹配"),
	TOTAL_AMOUNT_INVALID("1004", "订单总金额校验失败"),
	ACTUALLY_PAID_INVALID("1005", "订单实际支付金额校验失败"),
	TOTAL_AMOUNT_LESS_THAN_ACTUALLY_PAID("1006", "订单金额小于实际支付金额"),
	ORDER_STATUS_INVALID("1007", "订单状态不匹配"),
	OPERATOR_IS_NULL("1008", "操作员不能为空"),
	MANAGER_ID_INVALID("1009", "店长账号不匹配"),
	OPRATOR_SESSION_IS_NULL("1010", "交接班会话不能为空"),
	CREATED_OR_UPDATED_DATE_INVALID("1011", "订单创建时间或修改时间错误"),
	ORDER_ITEM_INVLAID("1012", "商品列表校验失败"),
	ORDER_DISCOUNT_INVALID("1013", "优惠信息校验失败"),
	TRANSACTION_INVALID("1014", "支付信息校验失败"),
	FAIL_TO_SAVE_ORDER("2000","订单保存失败");
	
	private String errorCode;
	private String desc;
	
	private OfflineOrderValidationResult(String errorCode, String desc) {
		this.errorCode = errorCode;
		this.desc = desc;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}
}
