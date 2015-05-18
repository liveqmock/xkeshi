package com.xpos.common.entity;

import java.math.BigDecimal;

import javax.persistence.Column;

import com.xpos.common.entity.face.Business;
import com.xpos.common.entity.face.EncryptId;
import com.xpos.common.exception.ScopeException;


/** 集团（管理下属子品牌或实体店铺） */
public class Merchant extends BaseEntity implements Business, EncryptId{

	private static final long serialVersionUID = -5329805244879139932L;

	@Column
	private String fullName; //企业名称
	
	@Column
	private BigDecimal balance; //账户余额
	
	@Column
	private Boolean memberCentralManagement; //会员统一管理

	@Column
	private Boolean discountCentralManagement; //折扣统一管理
	
	@Column
	private Boolean balanceCentralManagement; //后台账户统一管理
	
	@Column
	private boolean visible ;
	
	@Column
	private String smsSuffix;
	
	@Column
	private Picture avatar; //集团logo
	
	@Column
	private String smsChannel;
	
	@Override
	public BusinessType getAccessBusinessType(BusinessModel model){
		return BusinessType.MERCHANT;
	}

	@Override
	public Long getAccessBusinessId(BusinessModel model){
		if(BusinessModel.ORDER.equals(model) || BusinessModel.POS.equals(model)){
			throw new ScopeException();
		}
		return getId();
	}

	@Override
	public Long getSelfBusinessId() {
		return getId();
	}

	@Override
	public BusinessType getSelfBusinessType() {
		return BusinessType.MERCHANT;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	public Boolean getMemberCentralManagement() {
		return memberCentralManagement;
	}

	public void setMemberCentralManagement(Boolean memberCentralManagement) {
		this.memberCentralManagement = memberCentralManagement;
	}

	public Boolean getDiscountCentralManagement() {
		return discountCentralManagement;
	}

	public void setDiscountCentralManagement(Boolean discountCentralManagement) {
		this.discountCentralManagement = discountCentralManagement;
	}

	public Boolean getBalanceCentralManagement() {
		return balanceCentralManagement;
	}

	public void setBalanceCentralManagement(Boolean balanceCentralManagement) {
		this.balanceCentralManagement = balanceCentralManagement;
	}

	public boolean getVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public String getSmsSuffix() {
		return smsSuffix;
	}

	public void setSmsSuffix(String smsSuffix) {
		this.smsSuffix = smsSuffix;
	}

	public Picture getAvatar() {
		return avatar;
	}

	public void setAvatar(Picture avatar) {
		this.avatar = avatar;
	}

	public String getSmsChannel() {
		return smsChannel;
	}

	public void setSmsChannel(String smsChannel) {
		this.smsChannel = smsChannel;
	}
}
