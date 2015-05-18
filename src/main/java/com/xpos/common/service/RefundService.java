package com.xpos.common.service;

import java.util.List;
import java.util.Map;

import com.xpos.common.entity.Coupon;
import com.xpos.common.entity.Refund;
import com.xpos.common.entity.Coupon.CouponStatus;
import com.xpos.common.entity.example.RefundExample;
import com.xpos.common.entity.face.Business;
import com.xpos.common.utils.Pager;

public interface RefundService {
	
	public Pager<Refund> findRefunds(Business business, RefundExample example, Pager<Refund> pager);
	
	public Refund findRefundByCode(String code);

	public Refund findRefundBySerial(String serial);

	public Refund findRefundByCouponCode(String code);

	public Refund findRefundById(Long id);

	public Refund createRefund(Coupon coupon);

	public boolean saveRefund(Refund refund);
	
	public boolean updateRefund(Refund refund);

	public boolean updateRefundByCode(Refund refund);
	
	 /**
	  * 根据支付成功的支付流水号，修改或插入退款流水号
	  * @author xk
	  */
	public boolean updateRefundCodeByPayments( Refund  refund ,RefundExample  refundExample  );
	
	/** 根据退款流水号，查询退款记录 */
	public List<Refund>  findBatchNoRefundList(String batch_no);  
	

	/** 尝试发起自动退款 */
	public Map<String, String> executeAutoRefunding(Refund refund);
	
	/**根据退款状态更新优惠券**/
	public boolean updateRelatedCoupon(Refund refund, CouponStatus couponStatus);

}
