package com.spartan.dc.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;


@Configuration
@MapperScan(basePackages = "com.spartan.dc.dao.write", sqlSessionTemplateRef = "writeSessionTemplate")
public class WriteMySqlSessionFactoryConfig extends SessionFactoryConfig {

    @Value("${mysql_conf.write_mapper_path}")
    private String writeMapperPath;

    @Value("${mysql_conf.config_file}")
    private String mybatisConfigFilePath;

    @Autowired
    @Qualifier(value = "writeMysqlDataSource")
    private DataSource writeMysqlDataSource;


    @Bean(name = "writeMysqlSessionFactory")
    @Primary
    public SqlSessionFactory writeMysqlSessionFactory()
            throws Exception {
        return createSqlSessionFactory(mybatisConfigFilePath, writeMapperPath, writeMysqlDataSource);
    }


    @Bean(name = "writeSqlTransactionManager")
    @Primary
    public PlatformTransactionManager sqlTransactionManager() {
        return new DataSourceTransactionManager(writeMysqlDataSource);
    }

    @Bean(name = "writeSessionTemplate")
    @Primary
    public SqlSessionTemplate writeSessionTemplate(
            @Qualifier("writeMysqlSessionFactory") SqlSessionFactory sqlSessionFactory) throws Exception {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}
