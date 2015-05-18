package com.xkeshi.common.db;

import com.xkeshi.pojo.po.Base;
import net.sudaibang.common.exception.PoStructureException;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.Column;
import javax.persistence.Table;
import java.lang.reflect.Field;
import java.util.Map;

import static org.apache.ibatis.jdbc.SqlBuilder.*;

public class BaseSQLTemplate<T extends Base> {

	/**
	 *
	 * 通过ID查找单条记录
	 *
	 * @param para
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public String getByID(Map<String, Object> para) {

		BEGIN();
		SELECT("*");
		FROM(getTableName((Class) para.get("base")));
		WHERE("id= #{id} and `status` = 1");
		return SQL();
	}

	/**
	 *
	 * 根据PO中已设置的字段查询匹配的单条记录
	 *
	 * @param para
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public String getByProperty(Map<String, Object> para) {
			Class clz = (Class) para.get("base");
			String propertyName = (String) para.get("propertyName");
			Object value = para.get("value");
			Field field;
			try {
				field = clz.getDeclaredField(propertyName);
			} catch (NoSuchFieldException | SecurityException e) {
				throw new PoStructureException("无法在PO中找到'" + propertyName +"'属性",e);
			}
			String columnName = "";
			if (field.isAnnotationPresent(Column.class)) {
				columnName = field.getAnnotation(Column.class).name();
			} else {
				throw new PoStructureException("PO中所查询的属性未设置@Column注解");
			}
			return "select * from " + getTableName(clz)
					+ " where " + columnName + "='" + value + "' and `status` = 1 limit 1";
	}

	/**
	 *
	 * 根据PO中已设置的字段查询匹配的列表记录
	 *
	 * @param para
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public String getListByProperty(Map<String, Object> para) {
		Class clz = (Class) para.get("base");
		String propertyName = (String) para.get("propertyName");
		Object value = para.get("value");
		Field field;
		try {
			field = clz.getDeclaredField(propertyName);
		} catch (NoSuchFieldException | SecurityException e) {
			throw new PoStructureException("无法在PO中找到'" + propertyName +"'属性",e);
		}
		String columnName = "";
		if (field.isAnnotationPresent(Column.class)) {
			columnName = field.getAnnotation(Column.class).name();
		} else {
			throw new PoStructureException("PO中所查询的属性未设置@Column注解");
		}
		return "select * from " + getTableName(clz)
				+ " where " + columnName + "='" + value + "' and `status` = 1";
	}


    /**
     *
     * 根据PO中已设置的字段列表查询匹配的列表记录（And条件）
     *
     * @param para
     * @return
     */
    @SuppressWarnings("rawtypes")
    public String getListByPropertiesAnd(Map<String, Object> para) {
        Class clz = (Class) para.get("base");
        Property[] properties = (Property[]) para.get("properties");

        String sql = StringUtils.join("select * from ", getTableName(clz), " where ");

        for (Property property : properties) {

            Field field;
            try {
                field = clz.getDeclaredField(property.getName());
            } catch (NoSuchFieldException | SecurityException e) {
                throw new PoStructureException("无法在PO中找到'" + property.getName() +"'属性",e);
            }
            String columnName;
            if (field.isAnnotationPresent(Column.class)) {
                columnName = field.getAnnotation(Column.class).name();
            } else {
                throw new PoStructureException("PO中所查询的属性未设置@Column注解");
            }
            sql = StringUtils.join(sql, columnName + "='" + property.getValue() + "' and ");

        }

        sql = StringUtils.join(sql, " `status` = 1");
        return sql;
    }

    /**
     *
     * 根据PO中已设置的字段列表查询匹配的列表记录（Or条件）
     *
     * @param para
     * @return
     */
    @SuppressWarnings("rawtypes")
    public String getListByPropertiesOr(Map<String, Object> para) {
        Class clz = (Class) para.get("base");
        Property[] properties = (Property[]) para.get("properties");

        String sql = StringUtils.join("select * from ", getTableName(clz), " where ");

        for (Property property : properties) {

            Field field;
            try {
                field = clz.getDeclaredField(property.getName());
            } catch (NoSuchFieldException | SecurityException e) {
                throw new PoStructureException("无法在PO中找到'" + property.getName() +"'属性",e);
            }
            String columnName;
            if (field.isAnnotationPresent(Column.class)) {
                columnName = field.getAnnotation(Column.class).name();
            } else {
                throw new PoStructureException("PO中所查询的属性未设置@Column注解");
            }
            sql = StringUtils.join(sql, columnName + "='" + property.getValue() + "' or ");

        }

        //去结尾的or
        if (properties != null && properties.length > 0){
            sql = StringUtils.removeEnd(sql, "or ");
        }

        sql = StringUtils.join(sql, "and `status` = 1");
        return sql;
    }

    /**
     *
     * 根据PO中已设置的字段列表查询匹配的列表记录（Or条件）
     *
     * @param para
     * @return
     */
    @SuppressWarnings("rawtypes")
    public String getListByProperties(Map<String, Object> para) {
        Class clz = (Class) para.get("base");
        Property[] properties = (Property[]) para.get("properties");

        String sql = StringUtils.join("select * from ", getTableName(clz), " where `status` = 1 and ");

        for (int i = 0; i < properties.length ; i++) {

            Field field;
            try {
                field = clz.getDeclaredField(properties[i].getName());
            } catch (NoSuchFieldException | SecurityException e) {
                throw new PoStructureException("无法在PO中找到'" + properties[i].getName() +"'属性",e);
            }
            String columnName;
            if (field.isAnnotationPresent(Column.class)) {
                columnName = field.getAnnotation(Column.class).name();
            } else {
                throw new PoStructureException("PO中所查询的属性未设置@Column注解");
            }

            //如果是第一个属性则忽视and or
            if (i == 0){
                sql = StringUtils.join(sql, "( ", columnName, "='", properties[i].getValue(), "' ");
            } else {
                //判断是and还是or条件
                if (StringUtils.equals(properties[i].getOperator(), "and")){
                    sql = StringUtils.join(sql, "and ", columnName, "='", properties[i].getValue(), "' ");
                } else {
                    sql = StringUtils.join(sql, "or ", columnName, "='", properties[i].getValue(), "' ");
                }
            }

        }

        if (properties != null && properties.length > 0){
            sql = StringUtils.join(sql, ")");
        } else {
            sql = StringUtils.removeEnd(sql, "and ");
        }

        return sql;
    }



	/**
	 * 查询所有记录
	 *
	 * @param clz
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public String getListAll(Class clz) {
		BEGIN();
		SELECT("*");
		FROM(getTableName(clz));
		return SQL();
	}


	/**
	 * 统计记录数
	 *
	 * @param clz
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public String getCount(Class clz) {
		return "select count(*) from " + getTableName(clz);
	}

	// ==============增 删 改========================================
	public String insert(T obj) {
		BEGIN();

		INSERT_INTO(getTableName(obj.getClass()));
		obj.caculationColumnList();
		VALUES(obj.returnInsertColumnsName(), obj.returnInsertColumnsDefine());
		return SQL();
	}

	public String update(T obj) {
		String idname = "id";

		BEGIN();

		UPDATE(getTableName(obj.getClass()));
		obj.caculationColumnList();
		SET(obj.returnUpdateSet());
		WHERE(idname + "=#{" + idname + "}");

		return SQL();
	}

	public String delete(T obj) {
		String idname = "id";

		BEGIN();

		DELETE_FROM(getTableName(obj.getClass()));
		WHERE(idname + "=#{" + idname + "}");

		return SQL();
	}

	@SuppressWarnings("rawtypes")
	private static String getTableName(Class clz) {
		try {

            if (clz.isAnnotationPresent(Table.class)) {
                return ((Table)clz.getAnnotation(Table.class)).name();
            } else {
                throw new PoStructureException("PO中所查询的属性未设置@Column注解");
            }

		} catch (Exception e) {
			e.printStackTrace();
		}
        throw new PoStructureException("PO中缺少@Table注解");

    }

}