package com.xpos.common.searcher.physicalCoupon;

import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.xpos.common.entity.face.Business.BusinessType;

public class PhysicalCouponSearcher{
	
	private String name;//实体券名称
	
	private	BusinessType businessType;//添加来源
	
	private Long businessId;//添加来源的商户ID
	
	private Long [] shopIds;//适用商户IDs
	
	private Set<Integer> enables;//状态（启用or停用）

	public boolean getHasParameter() {
		return StringUtils.isNotBlank(name) || !CollectionUtils.isEmpty(enables)
				|| (shopIds !=null && shopIds.length>0)
				|| businessId != null;
	}
	public boolean gethasShopParameter() {
		return StringUtils.isNotBlank(name) || !CollectionUtils.isEmpty(enables)
				|| businessId != null;
	}
	
	public Long[] getShopIds() {
		return shopIds;
	}

	public void setShopIds(Long[] shopIds) {
		this.shopIds = shopIds;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public BusinessType getBusinessType() {
		return businessType;
	}

	public void setBusinessType(BusinessType businessType) {
		this.businessType = businessType;
	}

	public Long getBusinessId() {
		return businessId;
	}

	public void setBusinessId(Long businessId) {
		this.businessId = businessId;
	}

	public Set<Integer> getEnables() {
		return enables;
	}

	public void setEnables(Set<Integer> enables) {
		this.enables = enables;
	}


	
	
	
}
