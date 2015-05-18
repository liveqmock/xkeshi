package com.xkeshi.dao;

import com.xkeshi.common.db.BaseSQLTemplate;
import com.xkeshi.common.db.Property;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * The interface Base dAO.
 */

public interface BaseDAO<T> {

    /**
     * 根据ID查询单条数据
     *
     * @param clz the clz
     * @param id  the id
     * @return by iD
     */
    @SelectProvider(type = BaseSQLTemplate.class, method = "getByID")
    @ResultMap("result")
    public T getByID(@Param("base") Class<?> clz, @Param("id") Long id);


    /**
     * 根据PO中已设置的字段查询匹配的单条记录。
     *
     * @param clz          the clz
     * @param propertyName the property name
     * @param value        the value
     * @return by property
     */
    @SelectProvider(type = BaseSQLTemplate.class, method = "getByProperty")
    @ResultMap("result")
    public T getByProperty(@Param("base") Class<?> clz,
                           @Param("propertyName") String propertyName, @Param("value") Object value);


    /**
     * 无分页查询，根据PO中已设置的字段查询同时全部匹配的记录
     *
     * @param clz          the clz
     * @param propertyName the property name
     * @param value        the value
     * @return list by property
     */
    @SelectProvider(type = BaseSQLTemplate.class, method = "getListByProperty")
    @ResultMap("result")
    public List<T> getListByProperty(@Param("base") Class<?> clz,
                                     @Param("propertyName") String propertyName, @Param("value") Object value);



    /**
     *
     * And 条件查询 获取结果集
     *
     * Gets list by property and.
     *
     * @param clz        the clz
     * @param properties the properties
     * @return the list by property and
     */
    @SelectProvider(type = BaseSQLTemplate.class, method = "getListByPropertiesAnd")
    @ResultMap("result")
    public List<T> getListByPropertiesAnd(@Param("base") Class<?> clz,
                                          @Param("properties") Property[] properties);


    /**
     *
     * Or 条件查询， 获取结果集
     *
     * Gets list by property or.
     *
     * @param clz        the clz
     * @param properties the properties
     * @return the list by property or
     */
    @SelectProvider(type = BaseSQLTemplate.class, method = "getListByPropertiesOr")
    @ResultMap("result")
    public List<T> getListByPropertiesOr(@Param("base") Class<?> clz,
                                         @Param("properties") Property[] properties);


    /**
     *
     * and 和 or 组合查询， 获取结果集
     * Gets list by properties .
     *
     * @param clz        the clz
     * @param properties the properties
     * @return the list by property or
     */
    @SelectProvider(type = BaseSQLTemplate.class, method = "getListByProperties")
    @ResultMap("result")
    public List<T> getListByProperties(@Param("base") Class<?> clz,
                                       @Param("properties") Property[] properties);


    /**
     * 查询全部
     *
     * @param clz the clz
     * @return list all
     */
    @SelectProvider(type = BaseSQLTemplate.class, method = "getListAll")
    @ResultMap("result")
    public List<T> getListAll(Class<?> clz);


    /**
     * 统计记录数
     *
     * @param clz the clz
     * @return count
     */
    @SelectProvider(type = BaseSQLTemplate.class, method = "getCount")
    public long getCount(Class<?> clz);


    // ====以下部分的SQL不需要手动实现====================================================

    /**
     * 增加记录
     *
     * @param t the t
     * @return int
     */
    @InsertProvider(type = BaseSQLTemplate.class, method = "insert")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    public int insert(T t);

    /**
     * 修改记录
     *
     * @param t the t
     * @return int
     */
    @UpdateProvider(type = BaseSQLTemplate.class, method = "update")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    public int update(T t);

    /**
     * 删除记录
     *
     * @param obj the obj
     * @return int
     */
    @DeleteProvider(type = BaseSQLTemplate.class, method = "delete")
    public int delete(T obj);


}
