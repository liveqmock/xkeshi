package com.xpos.common.entity.member;

import java.math.BigDecimal;

import javax.persistence.Column;

import com.xpos.common.entity.NewBaseEntity;
import com.xpos.common.entity.Picture;
import com.xpos.common.entity.face.Business;
import com.xpos.common.entity.face.Business.BusinessType;

public abstract class MemberType extends NewBaseEntity{

	private static final long serialVersionUID = -8176062387125530445L;

	@Column
	private String name; //会员类型的名称-如金卡会员、银卡会员
	
	@Column
	private BigDecimal discount; //会员折扣
	
	@Column
	private boolean isDefault ; //是否是默认会员类型(同一个集团、商户下有且只有一个默认会员类型）
	
	@Column
	private MemberAttributeTemplate memberAttributeTemplate; //会员资料模板（会员类型必须指定属性模板）
	
	@Column
	private Picture coverPicture; //会员卡图片

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public BigDecimal getDiscount() {
		return discount;
	}

	public void setDiscount(BigDecimal discount) {
		this.discount = discount;
	}

	public boolean isDefault() {
		return isDefault;
	}

	public void setDefault(boolean isDefault) {
		this.isDefault = isDefault;
	}
	
	public abstract Business getBusiness();
	
	public abstract Long getBusinessId();

	public abstract BusinessType getBusinessType();

	public MemberAttributeTemplate getMemberAttributeTemplate() {
		return memberAttributeTemplate;
	}

	public void setMemberAttributeTemplate(MemberAttributeTemplate memberAttributeTemplate) {
		this.memberAttributeTemplate = memberAttributeTemplate;
	}

	public Picture getCoverPicture() {
		return coverPicture;
	}

	public void setCoverPicture(Picture coverPicture) {
		this.coverPicture = coverPicture;
	}

	public boolean isValidDiscount(){
		if(discount != null && discount.compareTo(new BigDecimal(0)) > 0 && discount.compareTo(new BigDecimal(10)) <= 0){
			return true;
		}
		return false;
	}
}
