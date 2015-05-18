package com.xpos.common.service;

import java.util.List;
import java.util.Map;

import com.xpos.common.entity.Category;
import com.xpos.common.entity.Landmark;
import com.xpos.common.entity.Region;

public interface BaseDataService {
	
	/************ Category ****************/
	public Category findCategoryById(Long id);
	public Map<Category, List<Category>> findAllCategory();
	public Map<Long, List<Category>> findAllCategoryGoupByParentId();
	public Category findCategoryFromMap(Map<Category, List<Category>> categoriesMap, Long id);
	public List<Category> getTopCategory();
	

	/*************Region*******************/
	public List<Region> findRegionByCityCode(String cityCode);
	
	/*************Landmark*****************/
	public List<Landmark> findLandmarkByCityCode(String cityCode);
	

	
	
}
