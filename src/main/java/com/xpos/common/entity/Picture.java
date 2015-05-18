package com.xpos.common.entity;


import javax.persistence.Column;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Picture extends BaseEntity{

	private static final long serialVersionUID = 2416171323083370261L;
	@Column
	@JsonIgnore
	private Long foreignId;
	@Column
	@JsonIgnore
	private PictureType pictureType;
	@Column
	private String path;
	@Column
	private String name;
	@Column
	private String originalName;
	@Column
	private String description;
	@Column
	@JsonIgnore
	private Integer width;
	@Column
	@JsonIgnore
	private Integer height;
	@Column
	private String tag;
	@Transient
	private byte[] data;
	
	
	public enum PictureType {
		ARTICLE,
		SHOP_BANNER,
		SHOP_AVATAR,
		COUPON_INFO_PIC,
		COUPON_INFO_THUMB,
		ITEM_COVER,
		ALBUM,
		ACTIVITY_PIC,
		ACTIVITY_THUMB,
		CATEGORY_BANNER,
		THIRDCOUPON_INFO_PIC,
		MERCHANT_AVATAR,
		MEMBER_TYPE_COVER;   //会员卡
	}
	
	public Long getForeignId() {
		return foreignId;
	}
	public void setForeignId(Long foreignId) {
		this.foreignId = foreignId;
	}
	public PictureType getPictureType() {
		return pictureType;
	}
	public void setPictureType(PictureType pictureType) {
		this.pictureType = pictureType;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getOriginalName() {
		return originalName;
	}
	public void setOriginalName(String originalName) {
		this.originalName = originalName;
	}
	public Integer getWidth() {
		return width;
	}
	public void setWidth(Integer width) {
		this.width = width;
	}
	public Integer getHeight() {
		return height;
	}
	public void setHeight(Integer height) {
		this.height = height;
	}
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
	public byte[] getData() {
		return data;
	}
	public void setData(byte[] data) {
		this.data = data;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	@Override
	public String toString(){
		return getPath() + "/" + getName();
	}
	
}
