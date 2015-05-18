package com.xpos.common.persistence;

import java.util.List;

import org.apache.ibatis.annotations.DeleteProvider;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;

import com.xpos.common.entity.BaseEntity;
import com.xpos.common.entity.example.Example;
import com.xpos.common.utils.Pager;

public interface BaseMapper<T extends BaseEntity> {	
	
	@ResultMap("DetailMap")
	T selectByPrimaryKey(Long id);
	
	@SelectProvider(type = BaseDaoTemplate.class,method = "selectOneByExample")
	@ResultMap("DetailMap")
	T selectOneByExample(Example<T> example);
	
	@SelectProvider(type = BaseDaoTemplate.class,method = "selectByExample")
	@ResultMap("ListMap")
	List<T> selectByExample(@Param("example")Example<T> example, @Param("pager")Pager<T> pager);


	@InsertProvider(type = BaseDaoTemplate.class,method = "insertSelective") 
	@Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int insert(T record);

	@InsertProvider(type = BaseDaoTemplate.class,method = "insert")  
	@Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int insertWithNullValue(T record);
	
	@SelectProvider(type = BaseDaoTemplate.class,method = "countByExample") 
    int countByExample(Example<T> example);
    
	@DeleteProvider(type = BaseDaoTemplate.class,method = "deleteByExample") 
    int deleteByExample(Example<T> example);
	
	@DeleteProvider(type = BaseDaoTemplate.class,method = "deleteByPrimaryKey") 
    int deleteByPrimaryKey(T entity);

	@UpdateProvider(type = BaseDaoTemplate.class,method = "updateByExampleSelective") 
	int updateByExample(@Param("record") T record, @Param("example") Example<T> example);

	@UpdateProvider(type = BaseDaoTemplate.class,method = "updateByExample") 
	int updateByExampleWithNullValue(@Param("record") T record, @Param("example") Example<T> example);
	
	@UpdateProvider(type = BaseDaoTemplate.class,method = "updateByPrimaryKeySelective") 
	int updateByPrimaryKey(T record);

	@UpdateProvider(type = BaseDaoTemplate.class,method = "updateByPrimaryKey") 
	int updateByPrimaryKeyWithNullValue(T record);
    
	
}
