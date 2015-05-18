package com.xpos.common.entity;

import javax.persistence.Column;


public class Position extends BaseEntity{

	private static final long serialVersionUID = -7457365274153429641L;

	@Column
	private Long foreignId;
	
	@Column
	private PositionType type;
	
	@Column
	private Double latitude;
	
	@Column
	private Double longitude;
	
	public enum PositionType{
		SHOP, LANDMARK
	}
	public Long getForeignId() {
		return foreignId;
	}
	public void setForeignId(Long foreignId) {
		this.foreignId = foreignId;
	}
	public PositionType getType() {
		return type;
	}
	public void setType(PositionType type) {
		this.type = type;
	}
	public Double getLatitude() {
		return latitude;
	}
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}
	public Double getLongitude() {
		return longitude;
	}
	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	
	
}
