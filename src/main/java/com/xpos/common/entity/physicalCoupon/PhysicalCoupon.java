package com.xpos.common.entity.physicalCoupon;

import java.math.BigDecimal;

import com.xpos.common.entity.NewBaseEntity;
import com.xpos.common.entity.face.Business;

public class PhysicalCoupon extends NewBaseEntity{
	
	private static final long serialVersionUID = -3103130635194979218L;
	
	/**
	 * 实体券名称
	 */
	private String name;
	
	/**
	 * 面值
	 */
	private BigDecimal amount;
	
	/**
	 * 商户ID
	 */
	private Long business_id;
	
	/**
	 * 商户类型
	 */
	private Business.BusinessType business_type;

	/**
	 * 权重
	 */
	private Integer weight;
	
	/**
	 * 启用停用状态(1启用0停用)
	 */
	private boolean enable;

	
	//以下是列表查询的字段
	private int shopCount;//适用商户数
	
	private String shopName;//实体券来源（只有普通商户才显示，集团只显示“集团”）

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public Long getBusiness_id() {
		return business_id;
	}

	public void setBusiness_id(Long business_id) {
		this.business_id = business_id;
	}

	public Business.BusinessType getBusiness_type() {
		return business_type;
	}

	public void setBusiness_type(Business.BusinessType business_type) {
		this.business_type = business_type;
	}

	public Integer getWeight() {
		return weight;
	}

	public void setWeight(Integer weight) {
		this.weight = weight;
	}

	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	public int getShopCount() {
		return shopCount;
	}

	public void setShopCount(int shopCount) {
		this.shopCount = shopCount;
	}

	public String getShopName() {
		return shopName;
	}

	public void setShopName(String shopName) {
		this.shopName = shopName;
	}
	
	
	

}
