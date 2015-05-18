package com.xkeshi.common.db;

import java.io.Serializable;

/**
 * 排序接口 <br>
 * 
 * @author David
 *
 */
public interface Sort extends Serializable {
	
	String getOrderColumns();
	void setOrderColumns(String orderColumns);
	
	OrderType getOrderType() ;
	void setOrderType(OrderType orderType) ;
}
