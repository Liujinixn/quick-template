package com.quick.config.datasource;

import com.alibaba.druid.pool.DruidDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

/**
 * 系统日志数据库-数据源配置
 *
 * @author Liujinxin
 */
@Configuration
@MapperScan(basePackages = {"com.quick.log.mapper"}, sqlSessionFactoryRef = "logSqlSessionFactory")
public class LogDruidDataSourceConfig {

    @Value("${spring.datasource.log.druid.logMapperLocations}")
    private String logMapperLocations;

    @ConfigurationProperties(prefix = "spring.datasource.log.druid")
    @Bean(name = "logDataSource")
    public DataSource logDataSource() {
        return new DruidDataSource();
    }

    /**
     * SqlSessionFactory配置
     */
    @Bean(name = "logSqlSessionFactory")
    public SqlSessionFactory logSqlSessionFactory(@Qualifier("logDataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource);
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        sqlSessionFactoryBean.setMapperLocations(resolver.getResources(logMapperLocations));
        /*mybatis 设置驼峰规则*/
        org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
        configuration.setMapUnderscoreToCamelCase(true);
        configuration.setLogImpl(org.apache.ibatis.logging.stdout.StdOutImpl.class);
        sqlSessionFactoryBean.setConfiguration(configuration);
        return sqlSessionFactoryBean.getObject();
    }

    /**
     * 配置事物管理器
     */
    @Bean(name = "logTransactionManager")
    public DataSourceTransactionManager logTransactionManager(@Qualifier("logDataSource") DataSource dataSource) {
        DataSourceTransactionManager dataSourceTransactionManager = new DataSourceTransactionManager();
        dataSourceTransactionManager.setDataSource(dataSource);
        return dataSourceTransactionManager;
    }
}
