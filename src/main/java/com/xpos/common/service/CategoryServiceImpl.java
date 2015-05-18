package com.xpos.common.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xpos.common.entity.Category;
import com.xpos.common.entity.Picture;
import com.xpos.common.entity.Picture.PictureType;
import com.xpos.common.entity.example.CategoryExample;
import com.xpos.common.entity.example.Example.Criteria;
import com.xpos.common.entity.example.PictureExample;
import com.xpos.common.persistence.mybatis.CategoryMapper;
import com.xpos.common.persistence.mybatis.PictureMapper;
@Service
public class CategoryServiceImpl implements CategoryService {
	
	@Resource
	private CategoryMapper  categoryMapper   ;
    
	@Resource
	private PictureService  pictureService   ;
	
	@Resource
	private PictureMapper   pictureMapper    ;
	
	
	@Override
	@Transactional
	public boolean saveCategory(Category category) {
		boolean result  = false ;
		result =categoryMapper.insert(category)>0;
		//保存banner图片
		Picture banner = category.getBanner();
		if (banner!=null){
			banner.setPictureType(PictureType.CATEGORY_BANNER);
			banner.setForeignId(category.getId());
			result = result && pictureService.uploadPicture(banner);
		}
		if (result) 
			//更新图片id
			result =categoryMapper.updateByPrimaryKey(category)>0;
		return result;
	}

	@Override
	public boolean deleteCategory(Long categoryId) {
		Category category = categoryMapper.selectByPrimaryKey(categoryId);
		if (category!= null) {
			category.setDeleted(true);
			return categoryMapper.updateByPrimaryKey(category)>0;
		}
		return false;
	}

	@Override
	public boolean updateCategory(Category category) {
		boolean result  = false ;
		Picture banner = category.getBanner();
		result = categoryMapper.updateByPrimaryKey(category)>0;
		if (banner!=null){
			PictureExample example = new PictureExample();
			example.createCriteria()
					.addCriterion("foreignId = ", category.getId())
					.addCriterion("pictureType = ", PictureType.CATEGORY_BANNER.toString());
			Picture persistence = pictureMapper.selectOneByExample(example);
			banner.setForeignId(category.getId());
			banner.setPictureType(PictureType.CATEGORY_BANNER);
			result = pictureService.uploadPicture(banner) ;
			if (result && persistence != null) {
				persistence.setName(banner.getName());
				persistence.setPath(banner.getPath());
				persistence.setOriginalName(banner.getOriginalName());
				result = pictureMapper.updateByPrimaryKey(persistence)>0;
				category.setBanner(persistence);
			}else{
				result = pictureMapper.insert(banner)>0;
			}
		}
		if (result) 
			result = categoryMapper.updateByPrimaryKey(category)>0;
		return result;
	}

	@Override
	public Category findCategoryById(Long id) {
		return findCategoryIsVisible(id, false);
	}
	
	@Override
	public Category findCategoryByIdWinthUnvisible(Long id) {
		return findCategoryIsVisible(id, true);
	}
	

	private Category findCategoryIsVisible(Long id  , boolean isVisible){
		CategoryExample  categoryExample  = new CategoryExample();
		Criteria criteria = categoryExample.createCriteria();
		criteria.addCriterion("id=", id);
		if (!isVisible)  
			criteria.addCriterion("visible=", true);
		return  categoryMapper.selectOneByExample(categoryExample);
	}

	@Override
	public List<Category> findAllCountCategory() {
		List<Category> selectCountCategory = categoryMapper.selectCountCategory();
		Map<Long, Category> categoryMap  = new LinkedHashMap<Long, Category>();
		Iterator<Category> iterator = selectCountCategory.iterator();
		//暂时获取大类
		while ( iterator.hasNext()) {
			Category category = iterator.next();
			if (category.getParent()==null) {
				categoryMap.put(category.getId(), category);
				iterator.remove();
			}			
		}
		
		//统计大类的数量，并向大类中注入所属的小类
		for (Category category : selectCountCategory) {
			Category categoryLittle = categoryMap.get(category.getParent().getId());
			if (categoryLittle != null) {
				categoryLittle.setCount(categoryLittle.getCount()+category.getCount());
				List<Category> categorys = categoryLittle.getCategorys();
				categorys.add(category);
			}
		}
		return  new ArrayList<>(categoryMap.values());
	}

	@Override
	public List<Category> findAllParentCategory(Long id) {
		  CategoryExample categoryExample = new CategoryExample();
		  Criteria criteria = categoryExample.createCriteria();
		  criteria.addCriterion("parent_id=", id);
		  criteria.addCriterion("deleted=", false);
		  String orderByClause  = "sequence asc, createDate desc";
		 categoryExample.setOrderByClause(orderByClause );
		 return  categoryMapper.selectByExample(categoryExample, null);
	}

	@Override
	@Transactional
	public boolean updateSequenceCategoryList(List<Category> categoryList) {
		 int successcount = 0 ; 
		 for (Category category : categoryList) {
			 successcount += categoryMapper.updateByPrimaryKey(category);
		}
		return successcount==categoryList.size();
	}
	
/*	@Override
	@Cacheable(value="xpos-maintain", key="'categoryMap'")
	public Map<Long, List<Category>> loadCategoryMap() {
		//从DB查出所有类目
		List<Category> _categoryList = categoryMapper.selectCountCategory();
		categoryMap.clear();
		for(Category _cate : _categoryList){
			Long parentId = _cate.getParent().getId();
			List<Category> _cateList = categoryMap.get(parentId);
			if(_cateList == null){
				_cateList = new ArrayList<>();
				categoryMap.put(parentId, _cateList);
			}
			_cateList.add(_cate);
		}
		return categoryMap;
	}*/
 
}
