package com.xpos.common.searcher;

import org.apache.commons.lang.StringUtils;

import com.xpos.common.entity.Region;
import com.xpos.common.entity.example.Example;
import com.xpos.common.entity.example.Example.Criteria;
import com.xpos.common.entity.example.RegionExample;

public class RegionSearcher extends AbstractSearcher<Region> {

	private String provinceName;

	private String cityName;

	private String districtName;

	@Override
	public Example<?> getExample() {

		example = new RegionExample();
		Criteria criteria = example.createCriteria();
		/*
		if (StringUtils.isNotBlank(districtName))
			criteria.addCriterion("(districtName like '%" +districtName + "%')");
		else if (StringUtils.isNotBlank(cityName))
			criteria.addCriterion("(cityName = '" +cityName + "')");
		防止百度地图，获取的市、区名字不相同引起的问题。，确保省份名字相同
		*/
		 if (StringUtils.isNotBlank(provinceName))
			criteria.addCriterion("(provinceName = '" +provinceName + "')");
		
		return (RegionExample) example;

	}

	public String getProvinceName() {
		return provinceName;
	}

	public void setProvinceName(String provinceName) {
		this.provinceName = provinceName;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public String getDistrictName() {
		return districtName;
	}

	public void setDistrictName(String districtName) {
		this.districtName = districtName;
	}

}
