package com.xpos.common.service;

import java.util.List;

import com.xpos.common.entity.CouponPayment.CouponPaymentType;
import com.xpos.common.entity.UserPayAgreement;
import com.xpos.common.entity.security.User;

public interface UserPayAgreeMentService {

	
	/**
	 * 修改用户绑定银行卡
	 */
	public boolean updateUserPayAgreement(User user, CouponPaymentType type,
			String usr_busi_agreement_id, String usr_pay_agreement_id, String payType, String gate_id,String lastfourid);

	/**
	 * 根据用户ID查询用户绑定的支付协议
	 */
	public List<UserPayAgreement> findUserPayAgreementListByUserIdAndType(
			Long userId, CouponPaymentType type);

	/**
	 * 根据ID查询用户绑定的支付协议
	 */
	public UserPayAgreement findUserPayAgreementById(Long userPayAgreementId);

	/**
	 * 删除用户支付协议
	 */
	boolean deleteUserPayAgreement(String userId, CouponPaymentType type, String usr_pay_agreement_id,
			  String gate_id);

}
