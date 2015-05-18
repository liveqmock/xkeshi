package com.xkeshi.common.db;

/**
 * 数据库方言接口
 * @author david
 *
 */
public interface Dialect {

    /**
     * 将sql转换为分页SQL
     *
     * @param sql    SQL语句
     * @param offset 开始条数
     * @param limit  每页显示多少纪录条数
     * @return 分页查询的sql
     */
    public String getLimitString(String sql, int offset, int limit);
    
    
    /**
     * 
     * 将sql转换为排序SQL
     * 
     * @param sql
     * @param orderColumns 排序的列，多个由逗号隔开
     * @param orderType 排序类型
     * @return
     */
    public String getOrderString(String sql, String orderColumns, OrderType orderType);
    
    public String getPaggingString(String sql, int offset, int limit);

}
