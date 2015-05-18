package com.xpos.common.entity;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.xkeshi.utils.StringUtil;
import com.xpos.common.entity.face.Business;
import com.xpos.common.entity.face.Business.BusinessType;
import com.xpos.common.entity.face.EncryptId;
import com.xpos.common.entity.itemInventory.ItemInventory;

public class Item extends BaseEntity implements EncryptId{
	
	private static final long serialVersionUID = 4575646547124643526L;

	@Column
	@JsonIgnore
	private Long businessId;
	
	@Column
	@JsonIgnore
	private BusinessType businessType;
	
	@Column
	@NotBlank(message="名称不能为空")
	@Length(min=1, max=250, message="名称最长250个字符")
	private String name;
	
	@Column
	private BigDecimal price;
	
	@Column
	@NotNull
	private ItemCategory category;
	
	@Column
	@Max(100)
	@Min(1)
	private Integer sequence;
	
	@Column
	private Picture cover;
	
	@Column
	@NotBlank(message="单位不能为空")
	@Length(max=3, min=1, message="单位最长3个字符")
	private String unit;
	
	@Column
	private boolean marketable;
	
	@Column
	private Long printerId;
	
	private ItemInventory itemInventory;
	
	
	public ItemInventory getItemInventory() {
		return itemInventory;
	}

	public void setItemInventory(ItemInventory itemInventory) {
		this.itemInventory = itemInventory;
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



	public String getName() {
		return name;
	}



	public void setName(String name) {
		this.name = name;
	}



	public BigDecimal getPrice() {
		return price;
	}



	public void setPrice(BigDecimal price) {
		this.price = price;
	}



	public ItemCategory getCategory() {
		return category;
	}



	public void setCategory(ItemCategory category) {
		this.category = category;
	}



	public Picture getCover() {
		return cover;
	}



	public void setCover(Picture cover) {
		this.cover = cover;
	}



	public String getUnit() {
		return unit;
	}



	public void setUnit(String unit) {
		this.unit = unit;
	}

	public boolean isMarketable() {
		return marketable;
	}

	public void setMarketable(boolean marketable) {
		this.marketable = marketable;
	}

	public Integer getSequence() {
		return sequence;
	}

	public void setSequence(Integer sequence) {
		this.sequence = sequence;
	}
	public Long getPrinterId() {
		return printerId;
	}

	public void setPrinterId(Long printerId) {
		this.printerId = printerId;
	}

	public void setBusiness(Business business){
		this.businessId = business.getAccessBusinessId(Business.BusinessModel.MENU);
		this.businessType = business.getAccessBusinessType(Business.BusinessModel.MENU);
	}

	//*********************************************************
	private String nameOmit ; //name omit

	public String getNameOmit() {
		String originalName = this.name;
		if(originalName != null && originalName.length() > 0){
			if(StringUtil.isChinese(originalName)){
				if(originalName.length() > 10){
					return originalName.substring(0,10).concat("...");
				}
			}else{
				if(originalName.length() > 20){
					return originalName.substring(0,20).concat("...");
				}
			}
		}
		return originalName;
	}

	public void setNameOmit(String nameOmit) {
		this.nameOmit = nameOmit;
	}
}
