package com.spartan.dc.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;


@Configuration
@MapperScan(basePackages = "com.spartan.dc.dao.read", sqlSessionTemplateRef = "readSessionTemplate")
public class ReadMySqlSessionFactoryConfig extends SessionFactoryConfig {

    @Value("${mysql_conf.read_mapper_path}")
    private String readMapperPath;

    @Value("${mysql_conf.config_file}")
    private String mybatisConfigFilePath;

    @Autowired
    @Qualifier(value = "readMysqlDataSource")
    private DataSource readMysqlDataSource;


    @Bean(name = "readMysqlSessionFactory")
    public SqlSessionFactory readMysqlSessionFactory()
            throws Exception {
        return createSqlSessionFactory(mybatisConfigFilePath, readMapperPath, readMysqlDataSource);
    }


    @Bean(name = "readSqlTransactionManager")
    public PlatformTransactionManager sqlTransactionManager() {
        return new DataSourceTransactionManager(readMysqlDataSource);
    }

    @Bean(name = "readSessionTemplate")
    public SqlSessionTemplate readSessionTemplate(
            @Qualifier("readMysqlSessionFactory") SqlSessionFactory sqlSessionFactory) throws Exception {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}
