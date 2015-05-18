package com.xkeshi.pojo.vo.shift;

import java.math.BigDecimal;
import java.util.List;

/**
 * 
 * @author xk
 *  交接班操作接受json的数据
 */
public class ShiftVO {
	
	/**收银员实收现金*/
	private BigDecimal totalCashPaidAmount ;
	
	/**收银员实际核销的实体券*/
	private List<ConsumedPhysicalCouponVO>  physicalCoupons  ;
	
	public ShiftVO() {
		super();
	}

	public BigDecimal getTotalCashPaidAmount() {
		return totalCashPaidAmount;
	}

	public void setTotalCashPaidAmount(BigDecimal totalCashPaidAmount) {
		this.totalCashPaidAmount = totalCashPaidAmount;
	}

	public List<ConsumedPhysicalCouponVO> getPhysicalCoupons() {
		return physicalCoupons;
	}

	public void setPhysicalCoupons(List<ConsumedPhysicalCouponVO> physicalCoupons) {
		this.physicalCoupons = physicalCoupons;
	}

}
