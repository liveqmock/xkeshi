package com.xpos.common.searcher.member;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.xpos.common.entity.Shop;
import com.xpos.common.entity.face.Business.BusinessType;
import com.xpos.common.entity.member.MemberType;

public class MemberSearcher{
	
	private String key; //name、nickName、email
	
	private Long memberId;
	
	private Long businessId;
	
	private BusinessType businessType;
	
	private String mobile;
	
	private Date birthday;
	
	private String gender;
	
	private Shop shop;
	
	private Long[] shopIds;
	
	private MemberType memberType;
	
	private Date createStartDate;
	
	private Date createEndDate;
	
	private Long operatorId;
	
	public boolean getHasParameter(){
		return StringUtils.isNotBlank(key) || (memberType != null && memberType.getId() != null)
				|| birthday != null || StringUtils.isNotBlank(gender) || createStartDate != null || createEndDate !=null || StringUtils.isNotBlank(mobile);
	}
	
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Long getMemberId() {
		return memberId;
	}

	public void setMemberId(Long memberId) {
		this.memberId = memberId;
	}

	public Long getBusinessId() {
		return businessId;
	}

	public void setBusinessId(Long businessId) {
		this.businessId = businessId;
	}

	public BusinessType getBusinessType() {
		return businessType;
	}

	public void setBusinessType(BusinessType businessType) {
		this.businessType = businessType;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public Shop getShop() {
		return shop;
	}

	public void setShop(Shop shop) {
		this.shop = shop;
	}

	public Long[] getShopIds() {
		return shopIds;
	}

	public void setShopIds(Long[] shopIds) {
		this.shopIds = shopIds;
	}

	public MemberType getMemberType() {
		return memberType;
	}

	public void setMemberType(MemberType memberType) {
		this.memberType = memberType;
	}

	public Date getCreateStartDate() {
		return createStartDate;
	}

	public void setCreateStartDate(Date createStartDate) {
		this.createStartDate = createStartDate;
	}

	public Date getCreateEndDate() {
		return createEndDate;
	}

	public void setCreateEndDate(Date createEndDate) {
		this.createEndDate = createEndDate;
	}

	public Long getOperatorId() {
		return operatorId;
	}

	public void setOperatorId(Long operatorId) {
		this.operatorId = operatorId;
	}
	
}
