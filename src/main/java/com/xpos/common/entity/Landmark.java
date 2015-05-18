package com.xpos.common.entity;

import javax.persistence.Column;

import com.xpos.common.entity.face.EncryptId;
import com.xpos.common.entity.face.Localizable;

/**
 * 
 * 地标(商业区域)
 * @author Johnny
 *
 */
public class Landmark extends BaseEntity implements Localizable, EncryptId{

	private static final long serialVersionUID = 7660316048225334863L;
	
	@Column
	private String cityCode;
	
	@Column
	private String district;
	
	@Column
	private String name;
	
	@Column(name = "position_id")
	private Position position;
	
	@Column
	private Double radius;
	
	public String getCityCode() {
		return cityCode;
	}
	public void setCityCode(String cityCode) {
		this.cityCode = cityCode;
	}
	public String getDistrict() {
		return district;
	}
	public void setDistrict(String district) {
		this.district = district;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Position getPosition() {
		return position;
	}
	public void setPosition(Position position) {
		this.position = position;
	}
	public Double getRadius() {
		return radius;
	}
	public void setRadius(Double radius) {
		this.radius = radius;
	}
	
	
}
