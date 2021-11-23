package com.ctdw.project;


/**
 * <p>mysql方言</p>
 *
 * @author : yzh
 * @date : 2021-11-06 11:28
 **/
public class MysqlDialect extends PageSqlFactory {

    public String parsePageSql(String originSql, Integer page, Integer limit) {
        String offset = ((page - 1) * limit) + "," + limit;
        return originSql + " limit " + offset;
    }

}
