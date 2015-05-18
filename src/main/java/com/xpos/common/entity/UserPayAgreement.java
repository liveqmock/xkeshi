package com.xpos.common.entity;

import javax.persistence.Column;

import com.xpos.common.entity.CouponPayment.CouponPaymentType;
import com.xpos.common.entity.face.EncryptId;
import com.xpos.common.entity.security.User;

/**
 * 用户付款协议
 * @author hk
 */
public class UserPayAgreement extends BaseEntity implements EncryptId{

	private static final long serialVersionUID = -206932154197139157L;

	@Column
	private User user;
	
	@Column
	private CouponPaymentType type;
	
	@Column
	private String usrbusiagreementId;//用户唯一对应该交易平台的协议号,一个交易平台只有一个
	
	@Column
	private String usrpayagreementId;//用户对应某银行的支付协议号,一张银行卡对应一个
	
	@Column
	private Bank bank;
	
	@Column
	private String lastfourid;
	

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public CouponPaymentType getType() {
		return type;
	}

	public void setType(CouponPaymentType type) {
		this.type = type;
	}

	public String getUsrbusiagreementId() {
		return usrbusiagreementId;
	}

	public void setUsrbusiagreementId(String usrbusiagreementId) {
		this.usrbusiagreementId = usrbusiagreementId;
	}

	public String getUsrpayagreementId() {
		return usrpayagreementId;
	}

	public void setUsrpayagreementId(String usrpayagreementId) {
		this.usrpayagreementId = usrpayagreementId;
	}

	public Bank getBank() {
		return bank;
	}

	public void setBank(Bank bank) {
		this.bank = bank;
	}

	public String getLastfourid() {
		return lastfourid;
	}

	public void setLastfourid(String lastfourid) {
		this.lastfourid = lastfourid;
	}
	
	
}
