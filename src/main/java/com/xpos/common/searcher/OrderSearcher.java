package com.xpos.common.searcher;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

import com.xpos.common.entity.Order;
import com.xpos.common.entity.Order.Status;
import com.xpos.common.entity.Order.Type;
import com.xpos.common.entity.example.Example;
import com.xpos.common.entity.example.Example.Criteria;
import com.xpos.common.entity.example.OrderExample;
import com.xpos.common.entity.face.Business;
import com.xpos.common.entity.member.Member;
import com.xpos.common.utils.BusinessSQLBuilder;

public class OrderSearcher extends AbstractSearcher<Order>{

	private Business business;
	private String orderNumber;
	private Date startDate; //startDate & endDate精确度只到日期，拼写sql时会自动补全为： xx/xx/xx 00:00:00 ~ xx/xx/xx 23:59:59
	private Date endDate;
	private Date startDateTime; //startDateTime & endDateTime精确到秒，拼写sql时会不会自动补全，按参数直接拼接
	private Date endDateTime;
	private BigDecimal minAmount;
	private BigDecimal maxAmount;
	private Integer order;
	private Member member;
	private String mobileNumber;
	private Status status;
	private Set<Type> typeSet;
	private String nickName;
	private Long operatorId;
	private String operatorSessionCode;
	
	private String[] orderByClause = {"id ASC","id DESC","createDate ASC","createDate DESC"};
	
	public Business getBusiness() {
		return business;
	}

	public void setBusiness(Business business) {
		this.business = business;
	}
	
	public String getBusinessSQL(){
		return BusinessSQLBuilder.getSQL(business);
	}
	
	public String getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(String orderNumber) {
		if(StringUtils.isNotBlank(orderNumber)){
			this.orderNumber = orderNumber.trim();
		}
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

	public BigDecimal getMinAmount() {
		return minAmount;
	}

	public void setMinAmount(BigDecimal minAmount) {
		this.minAmount = minAmount;
	}

	public BigDecimal getMaxAmount() {
		return maxAmount;
	}

	public void setMaxAmount(BigDecimal maxAmount) {
		this.maxAmount = maxAmount;
	}

	public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}

	public String[] getOrderByClause() {
		return orderByClause;
	}

	public void setOrderByClause(String[] orderByClause) {
		this.orderByClause = orderByClause;
	}

	public Member getMember() {
		return member;
	}

	public void setMember(Member member) {
		this.member = member;
	}

	public Status getStatus() {
		return status;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public Set<Type> getTypeSet() {
		return typeSet;
	}

	public void setTypeSet(Set<Type> typeSet) {
		this.typeSet = typeSet;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		if(StringUtils.isNotBlank(nickName)){
			this.nickName = nickName.trim();
		}
	}

	public Date getStartDateTime() {
		return startDateTime;
	}

	public void setStartDateTime(Date startDateTime) {
		this.startDateTime = startDateTime;
	}

	public Date getEndDateTime() {
		return endDateTime;
	}

	public void setEndDateTime(Date endDateTime) {
		this.endDateTime = endDateTime;
	}

	public Long getOperatorId() {
		return operatorId;
	}

	public void setOperatorId(Long operatorId) {
		this.operatorId = operatorId;
	}

	public String getOperatorSessionCode() {
		return operatorSessionCode;
	}

	public void setOperatorSessionCode(String operatorSessionCode) {
		this.operatorSessionCode = operatorSessionCode;
	}

	@Override
	public Example<?> getExample() {
		example = new OrderExample();
		Criteria criteria = example.createCriteria();
		if(startDate != null){
			criteria.addCriterion("createDate >= ", new DateTime(startDate).toString("yyyy-MM-dd 00:00:00"));
		}
		if(endDate != null){
			criteria.addCriterion("createDate <= ",  new DateTime(endDate).toString("yyyy-MM-dd 23:59:59"));
		}
		if(startDateTime != null){
			criteria.addCriterion("createDate >= ", new DateTime(startDateTime).toString("yyyy-MM-dd HH:mm:ss"));
		}
		if(endDateTime != null){
			criteria.addCriterion("createDate <= ",  new DateTime(endDateTime).toString("yyyy-MM-dd HH:mm:ss"));
		}
		if(minAmount != null){
			criteria.addCriterion("totalAmount >= ", minAmount);
		}
		if(maxAmount != null){
			criteria.addCriterion("totalAmount <= ", maxAmount);
		}
		if(order != null && order < orderByClause.length){
			example.setOrderByClause(orderByClause[order]);
		}else{
			example.setOrderByClause(" id DESC");
		}
		if(StringUtils.isNotBlank(orderNumber)){
			criteria.addCriterion("orderNumber = ", orderNumber);
		}
		if (StringUtils.isNotBlank(operatorSessionCode)) {
			criteria.addCriterion("operator_session_code = ", operatorSessionCode);
		}
		if (operatorId != null) {
			criteria.addCriterion("operator_id =", operatorId);
		}
		if(typeSet != null && typeSet.size() > 0){
			String orsql = "(";
			int orCount = 0;
			for(Type type: typeSet){
				if(orCount!=0)
					orsql+=" OR ";
				orCount++;
				orsql+= ("type = \'" + type.toString() + "\'");
			}
			orsql+=")";
			criteria.addCriterion(orsql);
		}
		if(status != null){
			criteria.addCriterion("status = ", status.toString());
		}
		return (OrderExample)example;
	}
	
	public boolean getHasParameter() {
		return StringUtils.isNotBlank(orderNumber) || status != null || minAmount != null || maxAmount != null
				|| (member != null && StringUtils.isNotBlank(member.getName()))
				|| (member != null && StringUtils.isNotBlank(member.getMobile()))
				|| startDateTime != null || endDateTime != null || !CollectionUtils.isEmpty(typeSet)
				|| StringUtils.isNotBlank(nickName) 
				|| StringUtils.isNotBlank(mobileNumber) ;
	}

}
