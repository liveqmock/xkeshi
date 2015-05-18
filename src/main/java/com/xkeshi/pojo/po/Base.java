package com.xkeshi.pojo.po;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * PO 基础模板类
 * <p/>
 * 关系数据库表的设计规范中必须包含该类所涉及的属性（id, comment, status）
 * <p/>
 * ps:特殊系统权限表，关系表之类的不需要继承此类
 */
public abstract class Base implements Serializable {

    private static final long serialVersionUID = 424357324074665315L;

    @Id
    protected Long id;
    @Column(name = "comment")
    protected String comment;
    @Column(name = "status")
    protected Integer status; // "0" for freeze, "1" for active

    /**
     * 用于存放PO的列信息
     */
    private transient static Map<Class<? extends Base>, List<ColumnName>> columnMap = new HashMap<Class<? extends Base>, List<ColumnName>>();

    private boolean isNull(String fieldname) {
        Field field;
        try {
            field = this.getClass().getDeclaredField(fieldname);
            return isNull(field);
        } catch (Exception e) {
            return isNullInBase(fieldname);
        }
    }

    private boolean isNullInBase(String fieldname) {
        Field field;
        try {
            field = Base.class.getDeclaredField(fieldname);
            return isNull(field);
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isNull(Field field) {
        try {
            field.setAccessible(true);
            return field.get(this) == null;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 用于计算类定义 需要POJO中的属性定义@Column(name)
     */
    public void caculationColumnList() {
        if (columnMap.containsKey(this.getClass())) {
            return;
        }

        List<ColumnName> columnList = new ArrayList<ColumnName>();
        // Add class Base's fields
        Field[] Basefields = Base.class.getDeclaredFields();
        for (Field field : Basefields) {
            if (field.isAnnotationPresent(Column.class)) {
                ColumnName columnName = new ColumnName(field.getAnnotation(
                        Column.class).name(), field.getName());
                columnList.add(columnName);
            } else if (field.isAnnotationPresent(Id.class)) {
                ColumnName columnName = new ColumnName("id", field.getName());
                columnList.add(columnName);
            }
        }

        Field[] fields = this.getClass().getDeclaredFields();
        // this.getClass().getSuperclass()
        for (Field field : fields) {
            if (field.isAnnotationPresent(Column.class)) {
                ColumnName columnName = new ColumnName(field.getAnnotation(
                        Column.class).name(), field.getName());
                columnList.add(columnName);
            }
        }

        columnMap.put(this.getClass(), columnList);
    }

    /**
     * 获取用于WHERE的 有值字段表
     *
     * @return
     */
    public List<WhereColumn> returnWhereColumnsName() {
        Field[] fields = this.getClass().getDeclaredFields();
        List<WhereColumn> columnList = new ArrayList<WhereColumn>(fields.length);

        for (Field field : fields) {
            if (field.isAnnotationPresent(Column.class) && !isNull(field))
                columnList.add(new WhereColumn(field
                        .getAnnotation(Column.class).name(), field
                        .getGenericType().equals(String.class)));
        }

        return columnList;
    }

    /**
     * Where条件信息
     *
     * @author HUYAO
     */
    public class WhereColumn {
        public String name;
        public boolean isString;

        public WhereColumn(String name, boolean isString) {
            this.name = name;
            this.isString = isString;
        }
    }

    public class ColumnName {
        public String column;
        public String property;

        public ColumnName(String column, String property) {
            this.column = column;
            this.property = property;
        }
    }

    /**
     * 用于获取Insert的字段累加
     *
     * @return
     */
    public String returnInsertColumnsName() {
        StringBuilder sb = new StringBuilder();

        List<ColumnName> columnList = columnMap.get(this.getClass());
        int i = 0;

        for (ColumnName columnName : columnList) {
            if (isNull(columnName.property))
                continue;
            if (i++ != 0)
                sb.append(',');
            sb.append(columnName.column);
        }
        return sb.toString();
    }

    /**
     * 用于获取Insert的字段映射累加
     *
     * @return
     */
    public String returnInsertColumnsDefine() {
        StringBuilder sb = new StringBuilder();

        List<ColumnName> columnList = columnMap.get(this.getClass());
        int i = 0;
        for (ColumnName columnName : columnList) {
            if (isNull(columnName.property)) {
                continue;
            }
            if (i++ != 0)
                sb.append(',');
            sb.append("#{").append(columnName.property).append('}');
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

        List<ColumnName> columnList = columnMap.get(this.getClass());
        int i = 0;

        for (ColumnName columnName : columnList) {
            if (isNull(columnName.property)) {
                continue;
            }
            if (i++ != 0) {
                sb.append(',');
            }
            sb.append(columnName.column).append("=#{")
                    .append(columnName.property).append('}');
        }
        return sb.toString();
    }

    // ===================getter and
    // setter===============================================================

    public String getComment() {
        return comment;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Override
    public String toString() {
        Field[] fields = this.getClass().getDeclaredFields();
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (Field f : fields) {
            if (Modifier.isStatic(f.getModifiers())
                    || Modifier.isFinal(f.getModifiers()))
                continue;
            Object value = null;
            try {
                f.setAccessible(true);
                value = f.get(this);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            if (value != null) {
                sb.append(f.getName()).append('=').append(value).append(',');
            } else {
                sb.append(f.getName()).append('=').append("null").append(',');
            }
        }
        sb.append(']');

        return sb.toString();
    }

}
