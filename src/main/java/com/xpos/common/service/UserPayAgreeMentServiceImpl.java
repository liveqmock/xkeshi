package com.xpos.common.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xpos.common.entity.Bank;
import com.xpos.common.entity.CouponPayment.CouponPaymentType;
import com.xpos.common.entity.UserPayAgreement;
import com.xpos.common.entity.example.BankExample;
import com.xpos.common.entity.example.UserPayAgreementExample;
import com.xpos.common.entity.security.User;
import com.xpos.common.persistence.mybatis.BankMapper;
import com.xpos.common.persistence.mybatis.UserPayAgreementMapper;

@Service
public class UserPayAgreeMentServiceImpl implements UserPayAgreeMentService{
	
	protected static final Logger logger = LoggerFactory.getLogger(UserPayAgreeMentServiceImpl.class);
	
	@Autowired
	private UserPayAgreementMapper userPayAgreementMapper;

	@Autowired
	private BankMapper bankMapper;

	@Override
	public List<UserPayAgreement> findUserPayAgreementListByUserIdAndType(
			Long userId, CouponPaymentType type) {
		UserPayAgreementExample example = new UserPayAgreementExample();
		example.createCriteria().addCriterion("user_id=", userId)
								.addCriterion("type='"+type+"'")
								.addCriterion("deleted=", false);
		return userPayAgreementMapper.selectByExample(example, null);
	}

	@Override
	public boolean updateUserPayAgreement(User user, CouponPaymentType type,
			String usr_busi_agreement_id, String usr_pay_agreement_id,
			String payType, String gate_id,String lastfourid) {
		BankExample bankExample = new BankExample();
		bankExample.createCriteria().addCriterion("type='"+payType+"'")
									.addCriterion("shorthand='"+gate_id+"'")
									.addCriterion("deleted=",false);
		Bank bank = bankMapper.selectOneByExample(bankExample);
		UserPayAgreementExample example = new UserPayAgreementExample();
		//一个支付协议号绑定一张银行卡,这里查询时就不关联银行卡了
		example.createCriteria().addCriterion("user_id=", user.getId())
								.addCriterion("type='"+type+"'")
								.addCriterion("usrbusiagreementId='"+usr_busi_agreement_id+"'")
								.addCriterion("usrpayagreementId='"+usr_pay_agreement_id+"'")
								.addCriterion("deleted=", false);
		if(userPayAgreementMapper.selectOneByExample(example)==null) {
			UserPayAgreement  userPayAgreement = new UserPayAgreement();
			userPayAgreement.setUser(user);
			userPayAgreement.setType(type);
			userPayAgreement.setUsrbusiagreementId(usr_busi_agreement_id);
			userPayAgreement.setUsrpayagreementId(usr_pay_agreement_id);
			userPayAgreement.setLastfourid(lastfourid);
			if(bank!=null) {
				userPayAgreement.setBank(bank);
			}
			return userPayAgreementMapper.insert(userPayAgreement)>0;
		}
		return true;
	}
	
	@Override
	public boolean deleteUserPayAgreement(String userId, CouponPaymentType type, String usr_pay_agreement_id,
			  String gate_id) {
		UserPayAgreementExample example = new UserPayAgreementExample();
		
		
		//一个支付协议号绑定一张银行卡,这里查询时就不关联银行卡了
		example.createCriteria().addCriterion("user_id=", userId)
								.addCriterion("type='"+type+"'")
								.addCriterion("usrpayagreementId='"+usr_pay_agreement_id+"'")
								.addCriterion("deleted=", false);
		UserPayAgreement  userPayAgreement = userPayAgreementMapper.selectOneByExample(example);
		if(userPayAgreement!=null) {
			userPayAgreement.setDeleted(true);
			return userPayAgreementMapper.updateByPrimaryKeyWithNullValue(userPayAgreement)>0;
		}
		return true;
	}

	@Override
	public UserPayAgreement findUserPayAgreementById(Long userPayAgreementId) {
		UserPayAgreementExample example = new UserPayAgreementExample();
			example.createCriteria().addCriterion("id = ", userPayAgreementId).addCriterion("deleted = ", false);
			List<UserPayAgreement> list = userPayAgreementMapper.selectByExample(example, null);
			if(list.size() == 1)
				return list.get(0);
			return null;
	}

}
