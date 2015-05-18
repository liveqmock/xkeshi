package com.xpos.common.entity;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Transient;

import com.xpos.common.entity.face.Business;
import com.xpos.common.entity.face.Business.BusinessType;
import com.xpos.common.entity.face.EncryptId;

/**
 * 活动信息
 * @author chengj
 */
public class Activity extends BaseEntity implements EncryptId{

	private static final long serialVersionUID = -1049093925969843470L;

	@Column
	private String name;
	
	@Column
	private String intro;
	
	@Column
	private Picture pic; //详情页顶部大图
	
	@Column
	private Picture thumb; //列表用缩略图
	
	@Column
	private Date startDate;
	
	@Column
	private Date endDate;
	
	@Column
	private String description;
	
	@Column
	private String remark;
	
	@Column
	private Boolean published;
	
	@Transient
	private ActivityStatus status;
	
	@Column
	private Long businessId;
	
	@Column
	private BusinessType businessType;
	
	private List<CouponInfo> couponInfos;
	
	public enum ActivityStatus{
		UNPUBLISHED, PENDING, NORMAL, EXPIRE;
	}
	
	public Boolean getPublished() {
		return published;
	}

	public void setPublished(Boolean published) {
		this.published = published;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIntro() {
		return intro;
	}

	public void setIntro(String intro) {
		this.intro = intro;
	}

	public Picture getPic() {
		return pic;
	}

	public void setPic(Picture pic) {
		this.pic = pic;
	}

	public Picture getThumb() {
		return thumb;
	}

	public void setThumb(Picture thumb) {
		this.thumb = thumb;
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public ActivityStatus getStatus() {
		Date now = new Date();
		if(!published)
			return ActivityStatus.UNPUBLISHED;
		
		if(now.before(this.getStartDate()))
			return ActivityStatus.PENDING;
		
		if(now.after(this.getEndDate()))
			return ActivityStatus.EXPIRE;
		
		return ActivityStatus.NORMAL;
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
	
	public void setBusiness(Business business){
		this.businessId = business.getAccessBusinessId(Business.BusinessModel.ACTIVITY);
		this.businessType = business.getAccessBusinessType(Business.BusinessModel.ACTIVITY);
	}

	public List<CouponInfo> getCouponInfos() {
		return couponInfos;
	}

	public void setCouponInfos(List<CouponInfo> couponInfos) {
		this.couponInfos = couponInfos;
	}	
}
