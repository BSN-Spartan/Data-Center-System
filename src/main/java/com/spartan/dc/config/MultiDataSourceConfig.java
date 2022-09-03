package com.spartan.dc.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;


@Configuration
public class MultiDataSourceConfig {

    @Value("${mysql_conf.driver}")
    private String mysqlDriver;

    @Value("${mysql.write_url}")
    private String writeMysqlUrl;

    @Value("${mysql.write_username}")
    private String writeMysqlUserName;

    @Value("${mysql.write_password}")
    private String writeMysqlPassword;

    @Value("${mysql.read_url}")
    private String readReadMysqlUrl;

    @Value("${mysql.read_username}")
    private String readMysqlUserName;

    @Value("${mysql.read_password}")
    private String readMysqlPassword;


    @Bean(name = "writeMysqlDataSource")
    @Qualifier("writeMysqlDataSource")
    @Primary
    public DataSource writeMysqlDataSource() {
        return createDataSource(mysqlDriver, writeMysqlUrl, writeMysqlUserName, writeMysqlPassword);
    }

    @Bean(name = "readMysqlDataSource")
    @Qualifier("readMysqlDataSource")
    public DataSource readMysqlDataSource() {
        return createDataSource(mysqlDriver, readReadMysqlUrl, readMysqlUserName, readMysqlPassword);
    }


    private DataSource createDataSource(String driverClassName, String url, String userName, String password) {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setDriverClassName(driverClassName);
        dataSource.setUrl(url);
        dataSource.setUsername(userName);
        dataSource.setPassword(password);
        return dataSource;
    }


}
