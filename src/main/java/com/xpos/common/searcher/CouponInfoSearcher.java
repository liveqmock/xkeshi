package com.xpos.common.searcher;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.xpos.common.entity.CouponInfo;
import com.xpos.common.entity.CouponInfo.CouponInfoType;
import com.xpos.common.entity.example.CouponInfoExample;
import com.xpos.common.entity.example.Example;
import com.xpos.common.entity.example.Example.Criteria;
import com.xpos.common.entity.face.Business;
import com.xpos.common.entity.face.Business.BusinessType;
import com.xpos.common.utils.IDUtil;

public class CouponInfoSearcher extends AbstractSearcher<CouponInfo>{

	private Business business;
	private List<Long> ids;
	private String key;
	private Date startDate;
	private Date endDate;
	private String serial;       //优惠编号
	private String status;
	private CouponInfoType type; //类型：套票、普通票
	private String tag;
	private Long  businessId  ;   
	private BusinessType  businessType ;
	private Integer order;
	private String[] orderByClause = {"stras ASC","stars DESC","received ASC","received DESC","creatDate ASC","createDate DESC" };
	
	public boolean getHasParameter(){
		return StringUtils.isNotBlank(tag) || StringUtils.isNotBlank(key) || startDate != null || endDate != null || StringUtils.isNotBlank(serial) || StringUtils.isNotBlank(status);
	}
	
	public String getParameterString(String skipKey){
		if(!getHasParameter())
			return "";
		
		StringBuilder builder = new StringBuilder("?");
		if(ids != null && ids.size() > 0 && !"key".equals(skipKey))
			builder.append("ids=").append(StringUtils.join(ids, ',')).append("&");
		if(StringUtils.isNotBlank(key) && !"key".equals(skipKey))
			builder.append("key=").append(key).append("&");
		if(startDate != null && !"startDate".equals(skipKey))
			builder.append("startDate=").append(startDate).append("&");
		if(endDate != null && !"endDate".equals(skipKey))
			builder.append("endDate=").append(endDate).append("&");
		if(StringUtils.isNotBlank(serial) && !"serial".equals(skipKey))
			builder.append("serial=").append(serial).append("&");
		if(StringUtils.isNotBlank(status) && !"status".equals(skipKey))
			builder.append("status=").append(status).append("&");
		if(StringUtils.isNotBlank(tag) && !"tag".equals(skipKey))
			builder.append("tag=").append(tag).append("&");
		String string = builder.toString();
		return string.substring(0, string.length()-1);
	}
	
	
	/**拼接链接参数**/
	public String getParameterString(){
		return getParameterString(null);
		
			
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

	public Business getBusiness() {
		return business;
	}

	public void setBusiness(Business business) {
		this.business = business;
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


	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
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

	public String getSerial() {
		return serial;
	}

	public void setSerial(String serial) {
		if(StringUtils.isNotBlank(serial) && StringUtils.isNumeric(serial)){
			this.serial = serial;
		}
	}
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	public CouponInfoType getType() {
		return type;
	}

	public void setType(CouponInfoType type) {
		this.type = type;
	}
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}

	@Override
	public Example<?> getExample() {
		example = new CouponInfoExample();
		Criteria criteria = example.createCriteria();
		if(business != null){
			criteria.addCriterion("businessId = ", business.getSelfBusinessId())
					.addCriterion("businessType = '" + business.getSelfBusinessType() + "'");
		}
		if (businessId != null) {
			criteria.addCriterion("businessId = ", businessId);
		}
		if (businessType != null) {
			criteria.addCriterion("businessType = " , businessType.toString());
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
		if(StringUtils.isNotBlank(key)){
			if(CouponInfoType.PACKAGE.equals(type)){
				criteria.addCriterion("((name like CONCAT('%',\"" + key + "\", '%') and type = 'PACKAGE') or id in (select distinct(cip.parent_id) from CouponInfo ci left join CouponInfo_Package cip on ci.id = cip.item_id and ci.name like CONCAT('%',\"" + key + "\", '%') and ci.deleted = false and cip.deleted = false and ci.type = 'NORMAL'))");
			} else {
				criteria.addCriterion("(name like CONCAT('%',\"" + key + "\", '%'))");
			}
		}
		if(startDate != null){
			criteria.addCriterion("startDate >= ", startDate);
		}
		if(endDate != null){
			criteria.addCriterion("endDate <= ", endDate);
		}
		if(StringUtils.isNotBlank(serial)){
			Long id = IDUtil.decode(serial);
			id = id == null ? 0 : id;
			criteria.addCriterion("id = ", id);
		}
		if(StringUtils.isNotBlank(status)){
			String orsql = "(";
			int orCount = 0;
			for(String str : StringUtils.split(status, ',')){
				if(orCount!=0)
					orsql+=" OR ";
				orCount++;
				if("PUBLISHED_VISIBLE".equalsIgnoreCase(str)){
					orsql += "( published = true AND visible = true )";
				}else if("PUBLISHED_UNVISIBLE".equalsIgnoreCase(str)){
					orsql += "( published = true AND visible = false )";
				}else if("UNPUBLISHED".equalsIgnoreCase(str)){
					orsql += "(published = false)";
				}
			}
			orsql+=")";
			criteria.addCriterion(orsql);
		}
		if(type != null){
			criteria.addCriterion("type = ", type.toString());
		}
		if(tag != null){
			criteria.addCriterion("tag = ", tag.trim().toString());
		}
		example.setOrderByClause(" createDate DESC");
		return (CouponInfoExample)example;
	}

}
