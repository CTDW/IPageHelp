package com.ctdw.project;


import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.cursor.Cursor;
import org.apache.ibatis.executor.BatchResult;
import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.ExecutorException;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.ParameterMode;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.transaction.Transaction;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.TypeHandlerRegistry;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * <p>mybatis分页执行器</p>
 * 适配器
 *
 * @author : yzh
 * @date : 2021-11-05 22:13
 **/
public class PageExecutor implements Executor {

    private final Executor executor;
    private final Class<? extends PageSqlFactory> sqlDialect;

    public PageExecutor(Executor executor, Class<? extends PageSqlFactory> c) {
        this.executor = executor;
        this.sqlDialect = c;

    }

    @Override
    public int update(MappedStatement ms, Object parameter) throws SQLException {
        return executor.update(ms, parameter);
    }


    @Override
    public <E> List<E> query(MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, CacheKey cacheKey, BoundSql boundSql) throws SQLException {

        BaseEntity target = null;
        if (parameter instanceof BaseEntity) {
            target = (BaseEntity) parameter;
        } else if (parameter instanceof HashMap) {
            HashMap paramMap = (HashMap) parameter;
            for (Object o : paramMap.values()) {
                if (o instanceof BaseEntity) {
                    target = (BaseEntity) o;
                }
            }
        }
        if (target != null) {
            Integer limit = target.getLimit();
            Integer page = target.getPage();
            if (limit != null & page != null) {
                //获取总页数
                int totalCount = getTotalCount(ms, boundSql, parameter);
                //总分页数
                int totalPage = totalCount / limit;
                PageHandle.totalPage.set(totalPage);
                PageHandle.totalCount.set(totalCount);
                //修改查询参数
                String sql = boundSql.getSql();
                PageSqlFactory pageSqlFactory = PageSqlFactory.createSqlDialect(sqlDialect);
                String newSql = pageSqlFactory.parsePageSql(sql, page, limit);
                BoundSql newBoundSql =
                        new BoundSql(ms.getConfiguration(), newSql, boundSql.getParameterMappings(), boundSql.getParameterObject());
                return executor.query(ms, parameter, rowBounds, resultHandler, cacheKey, newBoundSql);

            }
        }
        return executor.query(ms, parameter, rowBounds, resultHandler,
                cacheKey, boundSql);
    }


    public int getTotalCount(MappedStatement ms, BoundSql boundSql, Object parameter) {
        String sql = boundSql.getSql();
        String countSql = getCountSql(sql);
        Connection connection = null;
        PreparedStatement stmt;
        ResultSet rs;
        try {
            connection = ms.getConfiguration().getEnvironment().getDataSource()
                    .getConnection();
            stmt = connection.prepareStatement(countSql);
            setParameters(stmt, ms, boundSql, parameter);
            rs = stmt.executeQuery();
            if (rs.next())
                return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null && !connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    @SuppressWarnings("unchecked")
    private void setParameters(PreparedStatement ps, MappedStatement mappedStatement, BoundSql boundSql,
                               Object parameterObject) throws SQLException {
        ErrorContext.instance().activity("setting parameters").object(mappedStatement.getParameterMap().getId());
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        if (parameterMappings != null) {
            Configuration configuration = mappedStatement.getConfiguration();
            TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
            MetaObject metaObject = parameterObject == null ? null : configuration.newMetaObject(parameterObject);
            for (int i = 0; i < parameterMappings.size(); i++) {
                ParameterMapping parameterMapping = parameterMappings.get(i);
                if (parameterMapping.getMode() != ParameterMode.OUT) {
                    Object value;
                    String propertyName = parameterMapping.getProperty();
                    if (parameterObject == null) {
                        value = null;
                    } else if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
                        value = parameterObject;
                    } else if (boundSql.hasAdditionalParameter(propertyName)) {
                        value = boundSql.getAdditionalParameter(propertyName);
                    } else {
                        value = metaObject == null ? null : metaObject.getValue(propertyName);
                    }
                    @SuppressWarnings("rawtypes")
                    TypeHandler typeHandler = parameterMapping.getTypeHandler();
                    if (typeHandler == null) {
                        throw new ExecutorException("There was no TypeHandler found for parameter " + propertyName
                                + " of statement " + mappedStatement.getId());
                    }
                    typeHandler.setParameter(ps, i + 1, value, parameterMapping.getJdbcType());
                }
            }
        }
    }

    private String getCountSql(String sql) {
        String countHql = " SELECT count(*) "
                + removeSelect(removeOrders(sql));

        return countHql;
    }

    private static String removeSelect(String hql) {
        int beginPos = hql.toLowerCase().indexOf("from");
        if (beginPos < 0) {
            throw new IllegalArgumentException(" hql : " + hql + " must has a keyword 'from'");
        }
        return hql.substring(beginPos);
    }

    protected String removeOrders(String sql) {
        Pattern p = Pattern.compile("ORDER\\s*by[\\w|\\W|\\s|\\S]*", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(sql);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            m.appendReplacement(sb, "");
        }
        m.appendTail(sb);
        return sb.toString();
    }


    @Override
    public <E> List<E> query(MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler) throws SQLException {
        BoundSql boundSql = ms.getBoundSql(parameter);
        CacheKey cacheKey = createCacheKey(ms, parameter, rowBounds, boundSql);
        return query(ms, parameter, rowBounds, resultHandler, cacheKey, boundSql);
    }

    @Override
    public <E> Cursor<E> queryCursor(MappedStatement ms, Object parameter, RowBounds rowBounds) throws SQLException {
        return executor.queryCursor(ms, parameter, rowBounds);
    }

    @Override
    public List<BatchResult> flushStatements() throws SQLException {
        return executor.flushStatements();
    }

    @Override
    public void commit(boolean required) throws SQLException {
        executor.commit(required);
    }

    @Override
    public void rollback(boolean required) throws SQLException {
        executor.rollback(required);
    }

    @Override
    public CacheKey createCacheKey(MappedStatement ms, Object parameterObject, RowBounds rowBounds, BoundSql boundSql) {
        return executor.createCacheKey(ms, parameterObject, rowBounds, boundSql);
    }

    @Override
    public boolean isCached(MappedStatement ms, CacheKey key) {
        return executor.isCached(ms, key);
    }

    @Override
    public void clearLocalCache() {
        executor.clearLocalCache();
    }

    @Override
    public void deferLoad(MappedStatement ms, MetaObject resultObject, String property, CacheKey key, Class<?> targetType) {
        executor.deferLoad(ms, resultObject, property, key, targetType);
    }

    @Override
    public Transaction getTransaction() {
        return executor.getTransaction();
    }

    @Override
    public void close(boolean forceRollback) {
        executor.close(forceRollback);
    }

    @Override
    public boolean isClosed() {
        return executor.isClosed();
    }

    @Override
    public void setExecutorWrapper(Executor executor) {
        executor.setExecutorWrapper(executor);
    }


}
