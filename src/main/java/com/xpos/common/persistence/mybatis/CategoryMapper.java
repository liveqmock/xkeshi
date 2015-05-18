package com.xpos.common.persistence.mybatis;


import java.util.List;

import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;

import com.xpos.common.entity.Category;
import com.xpos.common.persistence.BaseMapper;

public interface CategoryMapper extends BaseMapper<Category>{
	/**
	 * 加载小类与小类引用的总数 
	 * @return
	 */
	@ResultMap("DetailMap")
	@Select("SELECT count(s.id) AS count, c.* FROM Category c LEFT OUTER JOIN Shop s ON c.id = s.category_id  AND s.deleted = FALSE where c.deleted = FALSE GROUP BY c.id ORDER BY c.sequence, c.parent_id ")
	public List<Category>  selectCountCategory( );
	
	
}