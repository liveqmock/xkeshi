package com.xpos.common.persistence;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.binding.MapperMethod.ParamMap;
import org.apache.ibatis.jdbc.SQL;

import com.xpos.common.entity.BaseEntity;
import com.xpos.common.entity.example.Example;
import com.xpos.common.entity.example.Example.Criteria;
import com.xpos.common.entity.example.Example.Criterion;
import com.xpos.common.utils.Pager;

public class BaseDaoTemplate <T extends BaseEntity>{
	
	final private Log logger = LogFactory.getLog(getClass());
	
	public String selectByPrimaryKey(final T entity){
		String sql = new SQL() {{
			SELECT("*");
			FROM(entity.getTableName());
			WHERE("id=#{id}");
		}}.toString();

		logger.debug(sql.replaceAll("\n", " "));
		return sql;
	}
	
	public String selectOneByExample(final Example<?> example){
		String sql = new SQL() {{
			SELECT("*");
			FROM(example.getTableName());
			exampleWhereClause(this,example);
			}}.toString();
		
		if(StringUtils.isNotBlank(example.getOrderByClause()))
			sql+=(" ORDER BY " + example.getOrderByClause());
			

		sql+=" limit 0,1";
		
		logger.debug(sql.replaceAll("\n", " "));
		return sql;
	}
	
	
	@SuppressWarnings("unchecked")
	public String selectByExample(ParamMap<?> map){
		final Example<?> example = (Example<?>)map.get("example");
		final Pager<T> pager = map.get("pager")==null?null:(Pager<T>)map.get("pager");
		String sql = new SQL() {{
			SELECT("*");
			FROM(example.getTableName());
			exampleWhereClause(this,example);
			}}.toString();
		
		if(StringUtils.isNotBlank(example.getOrderByClause()))
			sql+=(" ORDER BY " + example.getOrderByClause());
			
		if(pager != null)
			sql+=" limit " + pager.getStartNumber() + "," + pager.getPageSize();
		
		logger.debug(sql.replaceAll("\n", " "));
		return sql;
	}
	
	
	public String insert(final T entity) {  
		
		String sql = new SQL() {{
			INSERT_INTO(entity.getTableName());
			entity.caculationColumnList();
			VALUES(entity.returnInsertColumnsName(false), entity.returnInsertColumnsDefine(false));
		}}.toString();
		
		logger.debug(sql.replaceAll("\n", " "));
		return sql;
	}  
	
	public String insertSelective(final T entity){
		String sql =  new SQL() {{
			INSERT_INTO(entity.getTableName());
			entity.caculationColumnList();
			VALUES(entity.returnInsertColumnsName(true), entity.returnInsertColumnsDefine(true));
		}}.toString();
		
		logger.debug(sql.replaceAll("\n", " "));
		return sql;
	}
	
	public String countByExample(final Example<?> example){
		String sql =  new SQL() {{
			SELECT("count(id)");
			FROM(example.getTableName());
			exampleWhereClause(this, example);
		}}.toString();
		logger.debug(sql.replaceAll("\n", " "));
		return sql;
	}
	
	public String deleteByExample(final Example<?> example){
		String sql = new SQL(){{
			DELETE_FROM(example.getTableName());
			exampleWhereClause(this,example);
		}}.toString();
		logger.debug(sql.replaceAll("\n", " "));
		return sql;
	}
	
	public String deleteByPrimaryKey(final T entity){
		String sql = new SQL(){{
			DELETE_FROM(entity.getTableName());
			WHERE("id=#{id}");
		}}.toString();
		logger.debug(sql.replaceAll("\n", " "));
		return sql;
	}
	
	@SuppressWarnings("unchecked")
	public String updateByExample(ParamMap<?> map){
		final T entity = (T)map.get("record");
		final Example<T> example = (Example<T>)map.get("example"); 
		
		String sql = new SQL(){{
			UPDATE(entity.getTableName());
			Map<Field, Object> fields = getEntityFields(entity.getClass(), entity);
			for(Field field:fields.keySet()){
				Object value = fields.get(field);
				if(value == null && BaseEntity.class.isAssignableFrom(field.getType())){
					SET(field.getName() + "_id=null");
				}else if(value instanceof BaseEntity){
					SET(field.getName() + "_id=#{record."+field.getName()+".id}");
				}else{
					SET(field.getName()+ "=#{record." + field.getName() +"}");
				}
					
			}
			exampleWhereClause(this, example);
		}}.toString();
		logger.debug(sql.replaceAll("\n", " "));
		return sql;
	}
	
	@SuppressWarnings("unchecked")
	public String updateByExampleSelective(ParamMap<?> map){
		final T entity = (T)map.get("record");
		final Example<T> example = (Example<T>)map.get("example"); 
		String sql = new SQL(){{
			UPDATE(entity.getTableName());
			Map<Field, Object> fields = getEntityFields(entity.getClass(), entity);
			for(Field field:fields.keySet()){
				Object value = fields.get(field);
				if(value != null){
					if(value instanceof BaseEntity){
						SET(field.getName() + "_id=#{record."+field.getName()+".id}");
					}else if(field.isAnnotationPresent(Column.class) && StringUtils.isNotBlank(field.getAnnotation(Column.class).name())){
						SET(field.getAnnotation(Column.class).name() + "=#{record." + field.getName() +"}");
					}else{
						SET(field.getName()+ "=#{record." + field.getName() +"}");
					}
				}
					
			}
			exampleWhereClause(this,example);
		}}.toString();
		logger.debug(sql.replaceAll("\n", " "));
		return sql;
		
	}
	
	public String updateByPrimaryKey(final T entity){
		String sql = new SQL(){{
			UPDATE(entity.getTableName());
			Map<Field, Object> fields = getEntityFields(entity.getClass(), entity);
			for(Field field:fields.keySet()){
				Object value = fields.get(field);
				if(value == null && BaseEntity.class.isAssignableFrom(field.getType())){
					SET(field.getName() + "_id=null");
				}else if(value instanceof BaseEntity){
					SET(field.getName() + "_id=#{"+field.getName()+".id}");
				}else{
					SET(field.getName()+ "=#{" + field.getName() +"}");
				}
					
			}
			WHERE("id=#{id}");
		}}.toString();
		logger.debug(sql.replaceAll("\n", " "));
		return sql;
	}
	
	public String updateByPrimaryKeySelective(final T entity){
		String sql = new SQL(){{
			UPDATE(entity.getTableName());
			Map<Field, Object> fields = getEntityFields(entity.getClass(), entity);
			for(Field field:fields.keySet()){
				Object value = fields.get(field);
				if(value != null){
					if(value instanceof BaseEntity){
						SET(field.getName() + "_id=#{"+field.getName()+".id}");
					}else{
						SET(field.getName()+ "=#{" + field.getName() +"}");
					}
				}
					
			}
			WHERE("id=#{id}");
		}}.toString();
		logger.debug(sql.replaceAll("\n", " "));
		return sql;
	}
	
	private SQL exampleWhereClause(SQL sql, final Example<?> example){
		int orCount = 0;
		for(Criteria criteria : example.getOredCriteria()){
			if(orCount!=0)
				sql.OR();
			orCount++;
			if(criteria.isValid()){
				for(Criterion criterion : criteria.getCriteria()){
					if(criterion.isNoValue())
						sql.WHERE(criterion.getCondition());
					else if(criterion.isSingleValue())
						sql.WHERE(criterion.getCondition() + criterion.getValue());
					else if(criterion.isBetweenValue())
						sql.WHERE(criterion.getCondition() + criterion.getValue() + " and " + criterion.getSecondValue());
					else if(criterion.isListValue()){
						StringBuilder valueBuilder = new StringBuilder("(");
						int vCount = 0;
						for(Object v:(List<?>)criterion.getValue()){
							if(vCount!=0)
								valueBuilder.append(",");
							vCount++;
							valueBuilder.append(v);
						}
						valueBuilder.append(")");
						sql.WHERE(criterion.getCondition() + valueBuilder.toString());
					}
				}
			}
		}
		return sql;
	}
	
	private Map<Field, Object> getEntityFields(Class<?> clazz, T entity){
		Map<Field, Object> result = null;
		try{
			result = new HashMap<Field, Object>();
			for(Field field : clazz.getDeclaredFields()){
				
				if(!field.isAnnotationPresent(Column.class))
					continue;
				
				field.setAccessible(true);
				result.put(field, field.get(entity));
			}
			Class<?> superClass = clazz.getSuperclass();
			if(superClass!=null && !superClass.equals(Object.class))
				result.putAll(getEntityFields(superClass, entity));
		}catch(IllegalArgumentException|IllegalAccessException e){
			
		}
		return result;
	}

}
