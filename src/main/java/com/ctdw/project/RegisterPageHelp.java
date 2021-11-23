package com.ctdw.project;


import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Import(value = PageHelpConfiguration.class)
public @interface RegisterPageHelp {
    Class<? extends PageSqlFactory> sqlDialect() default MysqlDialect.class;
}
