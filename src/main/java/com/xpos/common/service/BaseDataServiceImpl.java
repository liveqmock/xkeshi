package com.xpos.common.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.xpos.common.entity.Category;
import com.xpos.common.entity.Landmark;
import com.xpos.common.entity.Region;
import com.xpos.common.entity.example.CategoryExample;
import com.xpos.common.entity.example.LandmarkExample;
import com.xpos.common.entity.example.RegionExample;
import com.xpos.common.persistence.mybatis.CategoryMapper;
import com.xpos.common.persistence.mybatis.LandmarkMapper;
import com.xpos.common.persistence.mybatis.RegionMapper;

@Service
public class BaseDataServiceImpl implements BaseDataService{

	@Resource
	private CategoryMapper categoryMapper;
	
	@Resource
	private RegionMapper regionMapper;
	
	@Resource
	private LandmarkMapper landmarkMapper;
	
	@Override
	public Category findCategoryById(Long id) {
		return categoryMapper.selectByPrimaryKey(id);
	}
	
	@Override
	public Map<Category, List<Category>> findAllCategory() {
		CategoryExample example = new CategoryExample();
		example.createCriteria().addCriterion("deleted=", 0)
								.addCriterion("visible=", 1);
		List<Category> list = categoryMapper.selectByExample(example, null);
		if(list.size() > 0){
			Map<Category, List<Category>> result = new LinkedHashMap<Category, List<Category>>();
			for(Category category:list){
				if(category.getParent() == null){   
					//父节点
					/*if(!result.containsKey(category)){
						List<Category> children = new ArrayList<Category>();
						result.put(category, children);
					}*/	
				}else{              
					//子节点
					if(!result.containsKey(category.getParent())){ 
						List<Category> children = new ArrayList<Category>();
						result.put(category.getParent(), children);
					}
					result.get(category.getParent()).add(category);
				}
			}
			return result;
		}
		return null;	
	}

	@Override
	public List<Region> findRegionByCityCode(String cityCode) {
		RegionExample example = new RegionExample();
		example.createCriteria().addCriterion("cityCode=",cityCode);
		return regionMapper.selectByExample(example, null);
	}

	@Override
	public List<Category> getTopCategory() {
		CategoryExample example = new CategoryExample();
		example.createCriteria().addCriterion("parent_id=0")
								.addCriterion("deleted=", false)
								.addCriterion("visible=", true);
		List<Category> list = categoryMapper.selectByExample(example, null);
		return list;
	}
	
	@Override
	public Category findCategoryFromMap(
			Map<Category, List<Category>> categoriesMap, Long id) {
		
		for(Category category:categoriesMap.keySet()){
			if(category.getId().equals(id))
				return category;
		}
		
		
		for(List<Category> list:categoriesMap.values()){
			for(Category category:list){
				if(category.getId().equals(id))
					return category;
			}
		}
		return null;
	}


	@Override
	public Map<Long, List<Category>> findAllCategoryGoupByParentId() {
		CategoryExample categoryExample = new CategoryExample();
		categoryExample.createCriteria()
						.addCriterion("visible=", true)
						.addCriterion("deleted=", false);
		List<Category> list = categoryMapper.selectByExample(categoryExample, null);
		Map<Long, List<Category>> categories = new HashMap<Long, List<Category>>();
		for(Category category:list){
			if(category.getParent() == null)
				categories.put(category.getId(), new ArrayList<Category>());
		}
		for(Category category : list){
			if(category.getParent()!=null)
				categories.get(category.getParent().getId()).add(category);
				
		}
		return categories;
	}

	@Override
	public List<Landmark> findLandmarkByCityCode(String cityCode) {
		LandmarkExample example = new LandmarkExample();
		example.createCriteria().addCriterion("cityCode=", cityCode)
								.addCriterion("deleted=", false);
		return landmarkMapper.selectByExample(example, null);
	}

}
