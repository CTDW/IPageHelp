package com.ctdw.project;



import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.util.Properties;

/**
 * <p>分页拦截器</p>
 *
 * @author : yzh
 * @date : 2021-11-05 17:20
 **/
@Intercepts({
        @Signature(method = "query" , type = Executor.class, args = {MappedStatement.class, Object.class,
                RowBounds.class, ResultHandler.class})})
public class PageInterceptor implements Interceptor {

    private final Class<? extends PageSqlFactory> sqlDialect;

    public PageInterceptor(Class<? extends PageSqlFactory> sqlDialect) {
        this.sqlDialect = sqlDialect;
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        if (Executor.class.isAssignableFrom(target.getClass())) {
            PageExecutor executor = new PageExecutor((Executor) target,sqlDialect);
            return Plugin.wrap(executor, this);
        }
        return target;
    }

    @Override
    public void setProperties(Properties properties) {
    }
}
