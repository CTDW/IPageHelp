package com.ctdw.project;

import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.Resource;


/**
 * <p>分页配置类：注册拦截器</p>
 *
 * @author : yzh
 * @date : 2021-11-19 15:08
 **/
public class PageHelpConfiguration implements SmartInitializingSingleton, BeanPostProcessor {
    private Class<? extends PageSqlFactory> sqlDialect;
    private SqlSessionFactory sqlSessionFactory;

    @Override
    public void afterSingletonsInstantiated() {
        Configuration configuration = sqlSessionFactory.getConfiguration();
        configuration.addInterceptor(new PageInterceptor(sqlDialect));
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean.getClass().getAnnotation(SpringBootApplication.class) != null){
            RegisterPageHelp annotation = bean.getClass().getAnnotation(RegisterPageHelp.class);
            this.sqlDialect = annotation.sqlDialect();
        }
        return bean;
    }

    @Resource
    public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
        this.sqlSessionFactory = sqlSessionFactory;
    }
}
