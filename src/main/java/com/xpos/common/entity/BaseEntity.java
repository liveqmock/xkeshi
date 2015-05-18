package com.xpos.common.entity;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.xpos.common.entity.face.EncryptId;
import com.xpos.common.utils.IDUtil;

public  class BaseEntity implements Serializable {

	private static final long serialVersionUID = -480097581231599147L;

	@Column
	private Long id;// ID
	
	@JsonIgnore
	@Column
	protected Date createDate;// 创建日期
	
	@JsonIgnore
	@Column
	protected Date modifyDate;// 修改日期
	
	@JsonIgnore
	@Column
	protected Boolean deleted;

	/**
	 * 获取POJO对应的表名 需要POJO中的属性定义@Table(name)
	 * 
	 * @return
	 */
	@JsonIgnore
	public String getTableName() {
		Table table = this.getClass().getAnnotation(Table.class);
		if (table != null)
			return table.name();
		else
			return this.getClass().getSimpleName();
	}

	/**
	 * 用于存放POJO的列信息
	 */
	private transient static Map<Class<? extends BaseEntity>, List<String>> columnMap = new HashMap<Class<? extends BaseEntity>, List<String>>();

	private static Field getField(Class<?> clazz, String fieldName)
	        throws NoSuchFieldException {
	    try {
	      return clazz.getDeclaredField(fieldName);
	    } catch (NoSuchFieldException e) {
	      Class<?> superClass = clazz.getSuperclass();
	      if (superClass == null) {
	        throw e;
	      } else {
	        return getField(superClass, fieldName);
	      }
	    }
	  }
	
	private boolean isNull(String fileName) {
		try {
			Field field = getField(this.getClass(), fileName);
			return isNull(field);

		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		return false;
	}

	private boolean isNull(Field field) {
		try {
			field.setAccessible(true);
			return field.get(this) == null;
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

		return false;
	}
	
	public void caculationColumnList(){
		if (columnMap.containsKey(this.getClass()))
			return;
		
		Class<?> currentClass = this.getClass();
		List<String> columnList = caculationColumnList(currentClass);
		currentClass = currentClass.getSuperclass();
		
		while(currentClass != null){
			columnList.addAll(caculationColumnList(currentClass));
			currentClass = currentClass.getSuperclass();
		}
		
		columnMap.put(this.getClass(), columnList);

	}

	/**
	 * 用于计算类定义 需要POJO中的属性定义@Column(name)
	 */
	private List<String> caculationColumnList(Class<?> clazz) {
		

		Field[] fields = clazz.getDeclaredFields();		
		List<String> columnList = new ArrayList<String>(fields.length);

		for (Field field : fields) {
			
			if(!field.isAnnotationPresent(Column.class))
				continue;
			
			if(BaseEntity.class.isAssignableFrom(field.getType())){
				columnList.add(field.getName() + "_id");
			}else 
				columnList.add(field.getName());
		}
		
		return columnList;

	}

	/**
	 * 用于获取Insert的字段累加
	 * 
	 * @return
	 */
	public String returnInsertColumnsName(boolean skipNull) {
		StringBuilder sb = new StringBuilder();

		List<String> list = columnMap.get(this.getClass());
		int i = 0;
		for (String column : list) {
			boolean isNull = false;
			if(column.contains("_id"))
				isNull = isNull(column.replace("_id", ""));
			else
				isNull = isNull(column);
			
			if (skipNull && isNull)
				continue;

			if (i++ != 0)
				sb.append(',');
			sb.append(column);
		}
		return sb.toString();
	}

	/**
	 * 用于获取Insert的字段映射累加
	 * 
	 * @return
	 */
	public String returnInsertColumnsDefine(boolean skipNull) {
		StringBuilder sb = new StringBuilder();

		List<String> list = columnMap.get(this.getClass());
		int i = 0;
		for (String column : list) {
			if(column.contains("_id")){
				column = column.replace("_id", "");
				boolean isNull = isNull(column);
				if(skipNull && isNull){
					continue;
				}else{
					if (i++ != 0)
						sb.append(',');
					
					if (isNull)
						sb.append("null");
					else
						sb.append("#{").append(column+".id").append('}');
				}
				
			}else{
				if (skipNull && isNull(column))
					continue;
				if (i++ != 0)
					sb.append(',');
				sb.append("#{").append(column).append('}');
			}
			
		}
		return sb.toString();
	}

	/**
	 * 用于获取Update Set的字段累加
	 * 
	 * @return
	 */
	public String returnUpdateSet() {
		StringBuilder sb = new StringBuilder();

		List<String> list = columnMap.get(this.getClass());
		int i = 0;
		for (String column : list) {
			if (isNull(column))
				continue;

			if (i++ != 0)
				sb.append(',');
			sb.append(column).append("=#{").append(column).append('}');
		}
		return sb.toString();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Date getModifyDate() {
		return modifyDate;
	}

	public void setModifyDate(Date modifyDate) {
		this.modifyDate = modifyDate;
	}

	public Boolean getDeleted() {
		return deleted;
	}

	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}
	
	public String getEid(){
		if(this instanceof EncryptId){
			return getId() != null ? IDUtil.encode(getId()) : null;
		}else
			return String.valueOf(getId());
	}

}
