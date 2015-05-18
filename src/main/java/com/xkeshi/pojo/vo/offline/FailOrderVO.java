package com.xkeshi.pojo.vo.offline;



/**
 * @author snoopy
 *
 */
public class FailOrderVO {
	
	private String orderNumber;
	
	private String errorCode;
	
	public FailOrderVO(String orderNumber,String errorCode) {
		this.orderNumber = orderNumber;
		this.errorCode = errorCode;
	}

	public String getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	
	
}
