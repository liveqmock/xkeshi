package com.xpos.common.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.xpos.common.entity.face.Business;
import com.xpos.common.entity.face.Business.BusinessType;
import com.xpos.common.entity.face.EncryptId;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.NumberFormat;
import org.springframework.format.annotation.NumberFormat.Style;

import javax.persistence.Column;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * 
 * 优惠券信息
 * @author Johnny
 *
 */
public class CouponInfo extends BaseEntity implements EncryptId{

	private static final long serialVersionUID = 7591991516296003378L;

	@Column
    @NotBlank(message = "名称不能为空")
    @Length(max = 10, message = "名称长度过长")
	private String name;
	
	@Column
	private Long businessId;
	
	@Column
	private BusinessType businessType;
	
	@Column
    @NotBlank(message = "简介不能为空")
	private String intro;
	
	@Column
    @NotBlank(message = "描述详情不能为空")
	private String description;
	
	@Column
	private String remark;
	
	@Column
    @NotNull(message = "有效期开始时间不能为空")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date startDate;
	
	@Column
    @NotNull(message = "有效期结束时间不能为空")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date endDate;
	
	@Column
	private Boolean supportNormalRefund;
	
	@Column
	private Boolean supportExpiredRefund;
	
	@Column
	private Boolean top;
	
	@Column
	@NumberFormat(style = Style.CURRENCY)
	private BigDecimal price;
	
	@Column
	@NumberFormat(style = Style.CURRENCY)
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
	
	@Column
	private Boolean visible;
	
	@Transient
	private CouponInfoStatus status;
	
	@Column
	private CouponInfoType type;

	@Column
	private Double  stars ;
	
	@Transient
	private List<CouponInfo> items;
	
	@Transient
	private Integer quantity;
	
	@Column
	private Integer userLimitCount; //每个用户限抢购,-1为不限
	
	@Column
	private String limitPayTime;//订单未付款失效时间

	@Column
	private Boolean allowContinueSale;//交易关闭后是否可销售
	
	@Column
	private String instructions;//优惠券使用说明
	
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
	
	public enum CouponInfoType{
		NORMAL, PACKAGE, CHILD;
	}
	
	public String getLimitPayTime() {
		return limitPayTime;
	}

	public void setLimitPayTime(String limitPayTime) {
		this.limitPayTime = limitPayTime;
	}

	public Boolean getAllowContinueSale() {
		return allowContinueSale;
	}

	public void setAllowContinueSale(Boolean allowContinueSale) {
		this.allowContinueSale = allowContinueSale;
	}

	public Double getStars() {
		return stars;
	}

	public void setStars(Double stars) {
		this.stars = stars;
	}

	public Boolean getPublished() {
		return published;
	}

	public void setPublished(Boolean published) {
		this.published = published;
	}
	public Boolean getVisible() {
		return visible;
	}
	
	public void setVisible(Boolean visible) {
		this.visible = visible;
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
		description = StringUtils.replace(description, "\n", "");
		description = StringUtils.replace(description, "\r", "");
		description = StringUtils.replaceChars(description, (char) 12288, ' ');
		return description;
	}

	public void setDescription(String description) {
		description = StringUtils.replace(description, "\n", "");
		description = StringUtils.replace(description, "\r", "");
		description = StringUtils.replaceChars(description, (char) 12288, ' ');
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
		
		if(this.getStartDate() != null && now.before(this.getStartDate()))
			return CouponInfoStatus.PENDING;
		
		if(this.getEndDate() != null && now.after(this.getEndDate()))
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
	
	public Set<Long> getScope(){
		return scope;
	}
	
	public CouponInfoType getType() {
		return type;
	}

	public void setType(CouponInfoType type) {
		this.type = type;
	}

	public List<CouponInfo> getItems() {
		return items;
	}

	public void setItems(List<CouponInfo> items) {
		this.items = items;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public Integer getUserLimitCount() {
		return userLimitCount;
	}

	public void setUserLimitCount(Integer userLimitCount) {
		this.userLimitCount = userLimitCount;
	}

	public String getInstructions() {
		return instructions;
	}

	public void setInstructions(String instructions) {
		this.instructions = instructions;
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
	public boolean canBuyStatus(){
		if(this.getEndDate() != null && System.currentTimeMillis() > this.getEndDate().getTime()){
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
