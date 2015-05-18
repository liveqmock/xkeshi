package com.xpos.common.service;

import java.util.List;
import java.util.Map;

import com.xpos.common.entity.Category;


//TODO 跟BaseDataService 里有重复
public interface CategoryService {
	
	boolean saveCategory(Category category);
	
	boolean deleteCategory(Long categoryId);
	
	boolean updateCategory(Category category);
	
	/**
	 * 显示未隐藏的商户分类
	 * @param id
	 * @return
	 * @author xuk
	 */
	Category findCategoryById(Long id);
	
	/**
	 * 可以显示隐藏的商户分类
	 * @param id
	 * @return
	 * @author xuk
	 */
	Category findCategoryByIdWinthUnvisible(Long id);
	 
	/**
	 * 加载所有大类或大类下的小类 (包括隐藏的)
	 */
	List<Category>  findAllParentCategory(Long id);
	
	 /**
	  * 显示全部商户分类和商户的总数量
	  * @author xuk
	  */
	List<Category> findAllCountCategory();
	/**
	 *  批量修改
	 * @param categoryList
	 * @return
	 */
    boolean updateSequenceCategoryList(List<Category> categoryList );
    
}
