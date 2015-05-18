package com.xkeshi.pojo.vo.shift;

/**
 * 
 * @author xk
 * 核销实体券
 */
public class ConsumedPhysicalCouponVO {
	
	/*实体券的id*/
	private long id;
	
	/*核销实体券的数量*/
	private Integer  count   ;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}
	
	
	
}
