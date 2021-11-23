package com.ctdw.project;

import java.lang.reflect.Constructor;
import java.util.HashSet;
import java.util.Set;

/**
 * <p>sql工厂</p>
 *
 * @author : yzh
 * @date : 2021-11-06 11:00
 **/
abstract class PageSqlFactory {

    //sql方言缓存
    protected static final Set<PageSqlFactory> sqlDialectCache = new HashSet<>();

    public abstract String parsePageSql(String originSql, Integer page, Integer limit);

    public static PageSqlFactory createSqlDialect(Class<? extends PageSqlFactory> sqlDialectClass) {
        for (PageSqlFactory pageSql : sqlDialectCache) {
            if (sqlDialectClass.isAssignableFrom(pageSql.getClass())) {
                return pageSql;
            }
        }
        Constructor<? extends PageSqlFactory> constructor;
        try {
            constructor = sqlDialectClass.getConstructor();
            PageSqlFactory pageSqlFactory = constructor.newInstance();
            sqlDialectCache.add(pageSqlFactory);
            return pageSqlFactory;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }


    }


}
