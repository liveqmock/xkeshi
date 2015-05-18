package com.xpos.common.searcher;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.xpos.common.entity.CouponPayment;
import com.xpos.common.entity.CouponPayment.CouponPaymentSource;
import com.xpos.common.entity.CouponPayment.CouponPaymentStatus;
import com.xpos.common.entity.CouponPayment.CouponPaymentType;
import com.xpos.common.entity.example.CouponPaymentExample;
import com.xpos.common.entity.example.Example;
import com.xpos.common.entity.face.Business;

public class CouponPaymentSearcher extends AbstractSearcher<CouponPayment>{

	private Business business;
	private Date startDate; //销售开始时间
	private Date endDate; //销售结束时间
	private String mobile ;
	private String key; //优惠券名称
	private CouponPaymentStatus status;
	private Set<CouponPaymentType> type;
	private Set<CouponPaymentSource> sourceSet;
	private String source;
	private String nickName;
	private String operator;

	public Business getBusiness() {
		return business;
	}

	public void setBusiness(Business business) {
		this.business = business;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public CouponPaymentStatus getStatus() {
		return status;
	}

	public void setStatus(CouponPaymentStatus status) {
		this.status = status;
	}

	public Set<CouponPaymentType> getType() {
		return type;
	}

	public void setType(Set<CouponPaymentType> type) {
		this.type = type;
	}

	public Set<CouponPaymentSource> getSourceSet() {
		return sourceSet;
	}

	public void setSourceSet(Set<CouponPaymentSource> sourceSet) {
		this.sourceSet = sourceSet;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
		if(StringUtils.isNotBlank(source)){
			sourceSet = new HashSet<>();
			for(CouponPaymentSource src : CouponPaymentSource.values()){
				if(StringUtils.contains(src.getDesc(), source)){
					sourceSet.add(src);
				}
			}
		}
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	@Override
	public Example<?> getExample() {
		example = new CouponPaymentExample();
		return (CouponPaymentExample)example;
	}
	
	public boolean getHasParameter() {
		return StringUtils.isNotBlank(key) || startDate != null || endDate != null
				|| StringUtils.isNotBlank(mobile) || !CollectionUtils.isEmpty(type)
				|| StringUtils.isNotBlank(source);
	}

}
