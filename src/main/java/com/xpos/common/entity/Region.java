package com.xpos.common.entity;

import javax.persistence.Column;


/**
 * 
 * 行政区域
 * @author Johnny
 *
 */
public class Region extends BaseEntity{
	
	private static final long serialVersionUID = -5467685842148183409L;
	
	@Column
	private String provinceName;
	@Column
	private String provinceCode;
	@Column
	private String cityName;
	@Column
	private String cityCode;
	@Column
	private String districtName;
	@Column
	private String districtCode;
	
	public String getProvinceName() {
		return provinceName;
	}
	public void setProvinceName(String provinceName) {
		this.provinceName = provinceName;
	}
	public String getProvinceCode() {
		return provinceCode;
	}
	public void setProvinceCode(String provinceCode) {
		this.provinceCode = provinceCode;
	}
	public String getCityName() {
		return cityName;
	}
	public void setCityName(String cityName) {
		this.cityName = cityName;
	}
	public String getCityCode() {
		return cityCode;
	}
	public void setCityCode(String cityCode) {
		this.cityCode = cityCode;
	}
	public String getDistrictName() {
		return districtName;
	}
	public void setDistrictName(String districtName) {
		this.districtName = districtName;
	}
	public String getDistrictCode() {
		return districtCode;
	}
	public void setDistrictCode(String districtCode) {
		this.districtCode = districtCode;
	}
}
