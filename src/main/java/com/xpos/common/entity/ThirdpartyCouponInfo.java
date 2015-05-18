package com.xpos.common.entity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.xpos.common.entity.face.Business;
import com.xpos.common.entity.face.Business.BusinessType;
import com.xpos.common.entity.face.EncryptId;


/**
 * 
 * 第三方优惠券信息
 * @author Snoopy
 *
 */
public class ThirdpartyCouponInfo extends BaseEntity implements EncryptId{

	private static final long serialVersionUID = 7591991516296003378L;

	@Column
	private String name;
	
	@Column
	private Long businessId;
	
	@Column
	private BusinessType businessType;
	
	@Column
	private String intro;
	
	@Column
	private String description;
	
	/*
	 * 适用商户
	 */
	@Column
	private String applicableShop;
	
	/*
	 * 优惠券使用说明
	 */
	@Column
	private String instructions;
	
	@Column
	private String remark;
	
	@Column
	private Date startDate;
	
	@Column
	private Date endDate;
	
	@Column
	private Boolean supportNormalRefund;
	
	@Column
	private Boolean supportExpiredRefund;
	
	@Column
	private Boolean top;
	
	@Column
	private BigDecimal price;
	
	@Column
	private BigDecimal originalPrice;
	
	@Column
	private Date saleStartDate;
	
	@Column
	private Date saleEndDate;
	
	@Column
	private Integer limitCount; //总数限制
	
	@Column
	private Integer received; //已领取
	
	@Column
	private String tag;
	
	@Column
	private Picture pic;	
	
	@Column
	private Picture thumb;
	
	@JsonIgnore
	private Set<Long> scope;
	
	@Column
	private Boolean published;
	
	@Transient
	private CouponInfoStatus status;
	
	public enum CouponInfoStatus{
		UNPUBLISHED(1), PENDING(2), NORMAL(3), EXPIRE(4);
		int state;
		public int getState(){
			return state;
		}
		CouponInfoStatus(int state){
			this.state = state;
		}
	}

	public Boolean getPublished() {
		return published;
	}

	public String getApplicableShop() {
		return applicableShop;
	}

	public void setApplicableShop(String applicableShop) {
		this.applicableShop = applicableShop;
	}

	public String getInstructions() {
		return instructions;
	}

	public void setInstructions(String instructions) {
		this.instructions = instructions;
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
		this.businessId = business.getAccessBusinessId(Business.BusinessModel.COUPON);
		this.businessType = business.getAccessBusinessType(Business.BusinessModel.COUPON);
	}

	public String getIntro() {
		return intro;
	}

	public void setIntro(String intro) {
		this.intro = intro;
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

	public Boolean getSupportNormalRefund() {
		return supportNormalRefund;
	}

	public void setSupportNormalRefund(Boolean supportNormalRefund) {
		this.supportNormalRefund = supportNormalRefund;
	}

	public Boolean getSupportExpiredRefund() {
		return supportExpiredRefund;
	}

	public void setSupportExpiredRefund(Boolean supportExpiredRefund) {
		this.supportExpiredRefund = supportExpiredRefund;
	}

	public CouponInfoStatus getStatus() {
		Date now = new Date();
		if(!published)
			return CouponInfoStatus.UNPUBLISHED;
		
		if(now.before(this.getStartDate()))
			return CouponInfoStatus.PENDING;
		
		if(now.after(this.getEndDate()))
			return CouponInfoStatus.EXPIRE;
		
		return CouponInfoStatus.NORMAL;
	}

	public Boolean getTop() {
		return top;
	}

	public void setTop(Boolean top) {
		this.top = top;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public BigDecimal getOriginalPrice() {
		return originalPrice;
	}

	public void setOriginalPrice(BigDecimal originalPrice) {
		this.originalPrice = originalPrice;
	}

	public Date getSaleStartDate() {
		return saleStartDate;
	}

	public void setSaleStartDate(Date saleStartDate) {
		this.saleStartDate = saleStartDate;
	}

	public Date getSaleEndDate() {
		return saleEndDate;
	}

	public void setSaleEndDate(Date saleEndDate) {
		this.saleEndDate = saleEndDate;
	}

	public Integer getLimitCount() {
		return limitCount;
	}

	public void setLimitCount(Integer limitCount) {
		this.limitCount = limitCount;
	}

	public Integer getReceived() {
		return received;
	}

	public void setReceived(Integer received) {
		this.received = received;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
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
	
	
	public void addScope(Long id){
		if(scope == null)
			scope = new HashSet<Long>();
	
		scope.add(id);
	}
	
	public void setScope(Set<Long> ids){
		this.scope = ids;
	}
	
	public String getScope(){
		String ids = "";
		for(Long id:scope){
			ids+=id.toString()+"|";
		}
		return ids;
	}
	
	@Transient
	public Boolean canBuy(){
		if(this.getSaleStartDate() != null && System.currentTimeMillis() < this.getSaleStartDate().getTime()){
			return false;//销售时间未开始
		}else if(this.getSaleEndDate() != null && System.currentTimeMillis() > this.getSaleEndDate().getTime()){
			return false;//销售时间已结束
		}else if(this.getEndDate() != null && System.currentTimeMillis() > this.getEndDate().getTime()){
			return false;//活动时间已结束
		}else if(this.getLimitCount() != null && this.getLimitCount() > 0 && this.getReceived() >= this.getLimitCount()){
			return false;//数量超出限制
		}
		return true;
	}
	@Transient
	public String canBuyState(){
		if(this.getSaleStartDate() != null && System.currentTimeMillis() < this.getSaleStartDate().getTime()){
			return "-1";//销售时间未开始
		}else if(this.getSaleEndDate() != null && System.currentTimeMillis() > this.getSaleEndDate().getTime()){
			return "-2";//销售时间已结束
		}else if(this.getLimitCount() != null && this.getLimitCount() > 0 && this.getReceived() >= this.getLimitCount()){
			return "-3";//数量超出限制
		}
		return "1";
	}
}
