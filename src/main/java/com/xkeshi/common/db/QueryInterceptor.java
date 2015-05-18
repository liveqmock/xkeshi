package com.xkeshi.common.db;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.sql.Connection;
import java.util.Map;
import java.util.Properties;

/**
 * <p>
 * 数据库分页和排序插件，只拦截查询语句.
 * </p>
 *
 */
@Intercepts({@Signature(type = Executor.class, method = "query",
        args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class})})
public class QueryInterceptor extends BaseInterceptor {

    private static final long serialVersionUID = 3576678797374122941L;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {

        final MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
        if (mappedStatement.getId().matches(_SQL_PATTERN)) { //拦截需要分页的SQL
        	Map paramMap = (Map) invocation.getArgs()[1];
//            Object parameter = paramMap.get("param2");
            BoundSql boundSql = mappedStatement.getBoundSql(paramMap);
            String originalSql = boundSql.getSql().trim();
            Map parameterObject = (Map) boundSql.getParameterObject();
            if (boundSql.getSql() == null || "".equals(boundSql.getSql()))
                return null;

            //查询参数--上下文传参
            Query query = null;
            
            //map传参每次都将currentPage重置,先判读map再判断context
            if (parameterObject != null) {
            	try {
            		//设置query对象
					Object oQuery =parameterObject.get("query");
					if (oQuery != null) {
						query = convertParameter(oQuery, query); //当DAO中的参数为一个Map<String,Object>，且query为map中对象
					}else{
						query = convertParameter(parameterObject.get("param1"), query); //当DAO为参数列表，且Query对象为第一个参数
					}
				} catch (Exception e) {
					query = convertParameter(parameterObject.get("param1"), query); //当DAO为参数列表，且Query对象为第一个参数
				}
            }
            if (query != null) {
            	
            	//处理排序
            	if (StringUtils.isNotBlank(query.getOrderColumns())) {
            		originalSql = SQLHelp.generateOrderSql(originalSql, query, DIALECT);
				}
            	
                int totPage = query.getTotalRows();
                //得到总记录数
                if (totPage == 0) {
                    Connection connection = mappedStatement.getConfiguration().getEnvironment().getDataSource().getConnection();
                    totPage = SQLHelp.getCount(originalSql, connection, mappedStatement, parameterObject, boundSql);
                }
                
                

                //初始化分页相关参数
                query.initPagination(totPage, query.getPageSize(), query.getCurrentPage());

                //分页查询 本地化对象 修改数据库注意修改实现

                String pageSql = SQLHelp.generatePageSql(originalSql, query, DIALECT);
                if (log.isDebugEnabled()) {
                    log.debug("查询SQL:" + pageSql);
                }
                invocation.getArgs()[2] = new RowBounds(RowBounds.NO_ROW_OFFSET, RowBounds.NO_ROW_LIMIT);
                BoundSql newBoundSql = new BoundSql(mappedStatement.getConfiguration(), pageSql, boundSql.getParameterMappings(), boundSql.getParameterObject());
                MappedStatement newMs = copyFromMappedStatement(mappedStatement, new BoundSqlSqlSource(newBoundSql));

                invocation.getArgs()[0] = newMs;
            }
        }
        return invocation.proceed();
    }


    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
        super.initProperties(properties);
    }

    private MappedStatement copyFromMappedStatement(MappedStatement ms,
                                                    SqlSource newSqlSource) {
        MappedStatement.Builder builder = new MappedStatement.Builder(ms.getConfiguration(),
                ms.getId(), newSqlSource, ms.getSqlCommandType());
        builder.resource(ms.getResource());
        builder.fetchSize(ms.getFetchSize());
        builder.statementType(ms.getStatementType());
        builder.keyGenerator(ms.getKeyGenerator());
        if (ms.getKeyProperties() != null) {
            for (String keyProperty : ms.getKeyProperties()) {
                builder.keyProperty(keyProperty);
            }
        }
        builder.timeout(ms.getTimeout());
        builder.parameterMap(ms.getParameterMap());
        builder.resultMaps(ms.getResultMaps());
        builder.cache(ms.getCache());
        return builder.build();
    }

    public static class BoundSqlSqlSource implements SqlSource {
        BoundSql boundSql;

        public BoundSqlSqlSource(BoundSql boundSql) {
            this.boundSql = boundSql;
        }

        public BoundSql getBoundSql(Object parameterObject) {
            return boundSql;
        }
    }
}
