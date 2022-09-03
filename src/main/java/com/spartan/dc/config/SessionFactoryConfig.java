package com.spartan.dc.config;

import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;


public class SessionFactoryConfig {



    public SqlSessionFactory createSqlSessionFactory(String configFilePath, String mapperPath, DataSource dataSource) throws Exception {

        MybatisSqlSessionFactoryBean sessionFactory = new MybatisSqlSessionFactoryBean();

        sessionFactory.setConfigLocation(new ClassPathResource(configFilePath));


        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        String packageSearchPath = PathMatchingResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + mapperPath;
        sessionFactory.setMapperLocations(resolver.getResources(packageSearchPath));


        sessionFactory.setDataSource(dataSource);

        return sessionFactory.getObject();
    }
}
