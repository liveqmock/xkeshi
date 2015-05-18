package com.xpos.common.entity;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;

import com.xpos.common.entity.face.Business;
import com.xpos.common.entity.face.EncryptId;
import com.xpos.common.entity.face.Localizable;


/** 普通商户或集团下的子商户 */
public class Shop extends BaseEntity implements Business, Localizable, EncryptId{

	private static final long serialVersionUID = -2997249392697502638L;
	
	public static String SHOP_ENABLE_SHIFT = "_SHOP_ENABLE_SHIFT";

		//商户类别
		@Column(name="category_id")
		private Category category; 
		
		//行政区域
		@Column
		private Region region;
		
		@Column
		private String cityCode;
		
		//地址
		@Column
		private String address;
		
		@Column
		private Merchant merchant;
		
		//主页
		@Column
		private String domain;
		
		//联系电话
		@Column
		private String contact;
		
		@Column
		private String shopHours;
		
		@Column
		private Picture banner;
		
		@Column
		private Picture avatar;

		@Column
		@NotNull
		private String name;
		
		@Column
		private String fullName;
		
		@Column
		private Boolean visible;
		
		@Column
		private String wechat;
		
		@Column
		private String weibo;

		@Column
		private BigDecimal balance;
		
		@Column
		private Position position;
		
		@Column  
		private String tag ; 
		
		@Column  
		private double stars ; 
		
		private String printerIp;
		
		private String printerPort;
		
		private boolean printerEnable;
		
		private List<Contact> contacts;
		
		private boolean enableMultiplePayment;
		
		public String getPrinterIp() {
			return printerIp;
		}

		public void setPrinterIp(String printerIp) {
			this.printerIp = printerIp;
		}

		public String getPrinterPort() {
			return printerPort;
		}

		public void setPrinterPort(String printerPort) {
			this.printerPort = printerPort;
		}

		public boolean isPrinterEnable() {
			return printerEnable;
		}

		public void setPrinterEnable(boolean printerEnable) {
			this.printerEnable = printerEnable;
		}

		public Category getCategory() {
			return category;
		}

		public void setCategory(Category category) {
			this.category = category;
		}

		public Region getRegion() {
			return region;
		}

		public String getCityCode() {
			return cityCode;
		}

		public void setCityCode(String cityCode) {
			this.cityCode = cityCode;
		}

		public void setRegion(Region region) {
			this.region = region;
			this.cityCode = region.getCityCode();
		}

		public String getAddress() {
			return address;
		}

		public void setAddress(String address) {
			this.address = address;
		}

		public String getDomain() {
			return domain;
		}

		public void setDomain(String domain) {
			this.domain = domain;
		}

		public Picture getBanner() {
			if(banner!=null)
				return banner;
			else if(category!=null && category.getBanner()!=null)
				return category.getBanner();
			else if(category!=null && category.getParent()!=null)
				return category.getParent().getBanner();
			else
				return null;
				
		}

		public void setBanner(Picture banner) {
			this.banner = banner;
		}

		public Picture getAvatar() {
			return avatar;
		}

		public void setAvatar(Picture avatar) {
			this.avatar = avatar;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getFullName() {
			/*if(merchant != null)
				this.fullName = merchant.getFullName();
			*/
			return fullName;
		}

		public void setFullName(String fullName) {
			this.fullName = fullName;
		}

		public String getWechat() {
			return wechat;
		}

		public void setWechat(String wechat) {
			this.wechat = wechat;
		}

		public String getWeibo() {
			return weibo;
		}

		public void setWeibo(String weibo) {
			this.weibo = weibo;
		}

		public List<Contact> getContacts() {
			return contacts;
		}

		public void setContacts(List<Contact> contacts) {
			this.contacts = contacts;
		}

		public String getContact() {
			return contact;
		}

		public void setContact(String contact) {
			this.contact = contact;
		}

		public String getShopHours() {
			return shopHours;
		}

		public void setShopHours(String shopHours) {
			this.shopHours = shopHours;
		}

		public Merchant getMerchant() {
			return merchant;
		}

		public void setMerchant(Merchant merchant) {
			this.merchant = merchant;
		}
		
		public Boolean getVisible() {
			return visible;
		}

		public String getTag() {
			return tag;
		}

		public void setTag(String tag) {
			this.tag = tag;
		}

		public boolean isEnableMultiplePayment() {
			return enableMultiplePayment;
		}

		public void setEnableMultiplePayment(boolean enableMultiplePayment) {
			this.enableMultiplePayment = enableMultiplePayment;
		}

		public double getStars() {
			return stars;
		}

		public void setStars(double stars) {
			this.stars = stars;
		}

		public void setVisible(Boolean visible) {
			this.visible = visible;
		}

		public Position getPosition() {
			return position;
		}

		public void setPosition(Position position) {
			this.position = position;
		}

		public void setBalance(BigDecimal balance) {
			this.balance = balance;
		}
		/** 如果当前商户属于某个集团，且集团设置账户统一管理，则使用集团的账户 */
		public BigDecimal getBalance(){
			if(this.merchant != null && this.merchant.getBalanceCentralManagement()){
				return merchant.getBalance();
			}
			return balance;
		}
		
		@Override
		public Long getAccessBusinessId(BusinessModel model){
			if(this.merchant != null){
				if(BusinessModel.MEMBER.equals(model) && this.merchant.getMemberCentralManagement()){
					return merchant.getAccessBusinessId(model);	
				}
			}
			return getId();
		}
		
		@Override
		public BusinessType getAccessBusinessType(BusinessModel model){
			if(this.merchant != null){
				if(BusinessModel.MEMBER.equals(model) && this.merchant.getMemberCentralManagement()){
					return merchant.getAccessBusinessType(model);
				} else {
					return BusinessType.SHOP;
				}
			}else{
				return BusinessType.SHOP;
			}
		}

		@Override
		public Long getSelfBusinessId() {
			return getId();
		}

		@Override
		public BusinessType getSelfBusinessType() {
			return BusinessType.SHOP;
		}
	
}
