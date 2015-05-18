package com.xpos.common.searcher;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

import com.xpos.common.entity.Coupon;
import com.xpos.common.entity.Coupon.CouponStatus;
import com.xpos.common.entity.CouponInfo.CouponInfoType;
import com.xpos.common.entity.example.CouponExample;
import com.xpos.common.entity.example.Example;
import com.xpos.common.entity.example.Example.Criteria;
import com.xpos.common.entity.face.Business;
import com.xpos.common.filter.UserGlanceInfoFilter;
import com.xpos.common.utils.BusinessSQLBuilder;

public class CouponSearcher extends AbstractSearcher<Coupon>{

	private Business business;
	private List<Long> ids;
	private Long couponInfoId;
	private Set<CouponStatus> status;
	private Date startDate;
	private Date endDate;
	private CouponInfoType type;          //优惠券类型
	private Long parentId;
	private UserGlanceInfoFilter uGInfo ; //用户浏览信息
	private String mobile ;
	private String packageSerial;
	private Long userId;
	private String uniqueNo;

	public String getUniqueNo() {
		return uniqueNo;
	}

	public void setUniqueNo(String uniqueNo) {
		this.uniqueNo = uniqueNo;
	}

	public boolean getHasParameter(){
		return StringUtils.isNotBlank(mobile) || status != null || business  != null;
	}
	 
	public String getParameterString(String skipKey){
		if(!getHasParameter())
			return "";
		StringBuilder builder = new StringBuilder("?");
		 if (business != null) 
			builder.append("businessid="+business.getSelfBusinessId()).append("&")
			       .append("businessType="+business.getSelfBusinessType().toString()).append("&");
		 if (status != null) {
			for (CouponStatus couponStatus : status) 
				builder.append("status="+couponStatus.toString()).append("&");
		}
		String string = builder.toString();
		return string.substring(0, string.length()-1);
	}
	
	
	/**拼接链接参数**/
	public String getParameterString(){
		return getParameterString(null);
	}
	
	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Business getBusiness() {
		return business;
	}
	
	public void setBusiness(Business business) {
		this.business = business;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public CouponInfoType getType() {
		return type;
	}

	public void setType(CouponInfoType type) {
		this.type = type;
	}

	public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	public List<Long> getIds() {
		return ids;
	}

	public void setIds(List<Long> ids) {
		this.ids = ids;
	}

	public void addId(Long id) {
		if(ids == null)
			ids = new ArrayList<Long>();
		ids.add(id);
	}

	public Long getCouponInfoId() {
		return couponInfoId;
	}

	public void setCouponInfoId(Long couponInfoId) {
		this.couponInfoId = couponInfoId;
	}

	public Set<CouponStatus> getStatus() {
		return status;
	}

	public void setStatus(Set<CouponStatus> status) {
		this.status = status;
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
	
	public UserGlanceInfoFilter getuGInfo() {
		return uGInfo;
	}

	public void setuGInfo(UserGlanceInfoFilter uGInfo) {
		this.uGInfo = uGInfo;
	}

	public String getPackageSerial() {
		return packageSerial;
	}

	public void setPackageSerial(String packageSerial) {
		this.packageSerial = packageSerial;
	}

	@Override
	public Example<?> getExample() {
		example = new CouponExample();
		Criteria criteria = example.createCriteria();
		if(business != null){
			criteria.addCriterion(BusinessSQLBuilder.getBusinessSQL(getBusiness().getSelfBusinessType(),getBusiness().getSelfBusinessId()));
		}
		if (mobile !=null ) {
			criteria.addCriterion("mobile = ", mobile);
		}
		if(ids != null && ids.size() > 0){
			String orsql = "(";
			int orCount = 0;
			for(Long id : ids){
				if(orCount!=0)
					orsql+=" OR ";
				orCount++;
				orsql+= ("id=" + id);
			}
			orsql+=")";
			criteria.addCriterion(orsql);
		}
		if(couponInfoId != null && couponInfoId > 0){
			criteria.addCriterion("couponInfo_id = ", couponInfoId);
		}
		if(status != null && status.size() > 0){
			String orsql = "(";
			int orCount = 0;
			for(CouponStatus stat: status){
				if(orCount!=0)
					orsql+=" OR ";
				orCount++;
				orsql+= ("status = '" + stat + "'");
			}
			orsql+=")";
			criteria.addCriterion(orsql);
		}
		if(startDate != null){
			criteria.addCriterion("consumeDate >= ", new DateTime(startDate).toString("yyyy-MM-dd 00:00:00"));
		}
		if(endDate != null){
			criteria.addCriterion("consumeDate <= ", new DateTime(endDate).toString("yyyy-MM-dd 23:59:59"));
		}
		if(type != null){
			criteria.addCriterion("type = ", type.toString());
		}
		if(parentId != null){
			criteria.addCriterion("parent_id = ", parentId);
		}
		if(userId != null){
			criteria.addCriterion("user_id = ", userId);
		}
		if(uniqueNo != null){
			criteria.addCriterion("user_id = (select id from User where uniqueNo='"+uniqueNo+"')");
		}
		if(StringUtils.isNotBlank(packageSerial)){
			criteria.addCriterion(" packageSerial = ", packageSerial);
		}
		example.setOrderByClause(" id DESC");
		return (CouponExample)example;
	}

}
