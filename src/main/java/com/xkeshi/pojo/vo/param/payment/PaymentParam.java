package com.xkeshi.pojo.vo.param.payment;

import java.math.BigDecimal;

/**
 * 支付接口请求通用参数
 *
 * Created by david-y on 2015/1/22.
 */
public class PaymentParam {
	
	private String serial;
    private String orderType;
    private Long memberId;
    private Long[] physicalCouponIds;
    private BigDecimal amount;
    
    
    
    public String getSerial() {
		return serial;
	}

	public void setSerial(String serial) {
		this.serial = serial;
	}

	public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public Long[] getPhysicalCouponIds() {
        return physicalCouponIds;
    }

    public void setPhysicalCouponIds(Long[] physicalCouponIds) {
        this.physicalCouponIds = physicalCouponIds;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
