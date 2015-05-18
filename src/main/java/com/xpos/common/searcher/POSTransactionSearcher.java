package com.xpos.common.searcher;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.util.CollectionUtils;

import com.xpos.common.entity.Coupon;
import com.xpos.common.entity.example.Example;
import com.xpos.common.entity.example.Example.Criteria;
import com.xpos.common.entity.example.POSTransactionExample;
import com.xpos.common.entity.face.Business;
import com.xpos.common.entity.pos.POSGatewayAccount.POSGatewayAccountType;
import com.xpos.common.entity.pos.POSTransaction.POSTransactionStatus;
import com.xpos.common.entity.pos.POSTransaction.POSTransactionType;
import com.xpos.common.utils.BusinessSQLBuilder;

public class POSTransactionSearcher extends AbstractSearcher<Coupon>{

	private Business business;
	private Set<POSTransactionStatus> statusSet;
	private POSTransactionType type;
	private Date startDate; //startDate & endDate精确度只到日期，拼写sql时会自动补全为： xx/xx/xx 00:00:00 ~ xx/xx/xx 23:59:59
	private Date endDate;
	private Date startDateTime; //startDateTime & endDateTime精确到秒，拼写sql时会不会自动补全，按参数直接拼接
	private Date endDateTime;
	private String mobile;
	private BigDecimal minSum;
	private BigDecimal maxSum;
	private POSGatewayAccountType gatewayAccountType;
	private Integer order;
	private String key;
	private String nickName;
	
	private String[] orderByClause = {"id ASC","id DESC","tradeDate ASC","tradeDate DESC"};

	public Business getBusiness() {
		return business;
	}

	public void setBusiness(Business business) {
		this.business = business;
	}

	public Set<POSTransactionStatus> getStatusSet() {
		return statusSet;
	}

	public void setStatusSet(Set<POSTransactionStatus> statusSet) {
		this.statusSet = statusSet;
	}

	public POSTransactionType getType(){
		return type;
	}
	
	public void setType(POSTransactionType type){
		this.type = type;
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

	public BigDecimal getMinSum() {
		return minSum;
	}

	public void setMinSum(BigDecimal minSum) {
		this.minSum = minSum;
	}

	public BigDecimal getMaxSum() {
		return maxSum;
	}

	public void setMaxSum(BigDecimal maxSum) {
		this.maxSum = maxSum;
	}
	
	public POSGatewayAccountType getGatewayAccountType() {
		return gatewayAccountType;
	}
	
	public void setGatewayAccountType(POSGatewayAccountType gatewayAccountType) {
		this.gatewayAccountType = gatewayAccountType;
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

	public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}
	
	public String getKey() {
		return key;
	}
	
	public void setKey(String key) {
		this.key = key;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	@Override
	public Example<?> getExample() {
		example = new POSTransactionExample();
		Criteria criteria = example.createCriteria();
		if(business != null){
			criteria.addCriterion(BusinessSQLBuilder.getSQL(business));
		}
		if(statusSet != null && statusSet.size() > 0){
			String orsql = "(";
			int orCount = 0;
			for(POSTransactionStatus  status: statusSet){
				if(orCount!=0)
					orsql+=" OR ";
				orCount++;
				orsql+= ("status = \'" + status.toString() + "\'");
			}
			orsql+=")";
			criteria.addCriterion(orsql);
		}
		if(type != null){
			criteria.addCriterion("type = ", type.toString());
		}
		if(gatewayAccountType != null){
			criteria.addCriterion("gatewayType = ", gatewayAccountType.toString());
		}
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
		if(minSum != null){
			criteria.addCriterion("sum >= ", minSum);
		}
		if(maxSum != null){
			criteria.addCriterion("sum <= ", maxSum);
		}
		if(order != null && order < orderByClause.length){
			example.setOrderByClause(orderByClause[order]);
		}else{
			example.setOrderByClause(" id DESC");
		}
		if (POSTransactionType.BANK_CARD.equals(type)){
			if(StringUtils.isNotBlank(key)){
				criteria.addCriterion("(mobile like '%"+key+"%' or cardNumber like '%"+key+"%' )");
			}
		}else if (POSTransactionType.CMCC_TICKET.equals(type)){
			if(StringUtils.isNotBlank(key)){
				criteria.addCriterion("code = ", key);
			}
			if(StringUtils.isNotBlank(mobile)){
				criteria.addCriterion("mobile like '%"+mobile+"'");
			}
		}
		return (POSTransactionExample)example;
	}
	
	public boolean getHasParameter(){
		return StringUtils.isNotBlank(key) || startDate != null || endDate != null || !CollectionUtils.isEmpty(statusSet)
				|| StringUtils.isNotBlank(mobile) || minSum != null || maxSum != null || StringUtils.isNotBlank(nickName);
	}
	
}
