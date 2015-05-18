package com.xpos.common.persistence.mybatis;

import java.util.List;

import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;

import com.xpos.common.entity.PageCategory;
import com.xpos.common.persistence.BaseMapper;

public interface PageCategoryMapper extends BaseMapper<PageCategory> {
     
	/**
	 * 统计大类和小类下的页面
	 */
	@ResultMap("DetailMap")
	@Select("SELECT count(p.id) AS count,pc.* FROM PageCategory pc LEFT OUTER JOIN Page p ON pc.id = p.pageCategory_id "
			+ "AND p.deleted = FALSE GROUP BY pc.id ORDER BY pc.parent_id ,pc.sequnce ,pc.createDate desc ")
	public List<PageCategory>  selectPageCategoryCount();
	
}
