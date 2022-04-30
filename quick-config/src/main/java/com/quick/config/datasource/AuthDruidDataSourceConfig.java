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
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

/**
 * 系统基础数据库-数据源配置
 *
 * @author Liujinxin
 */
@Configuration
@MapperScan(basePackages = {"com.quick.auth.mapper"}, sqlSessionFactoryRef = "authSqlSessionFactory")
public class AuthDruidDataSourceConfig {

    @Value("${spring.datasource.auth.druid.authMapperLocations}")
    private String authMapperLocations;

    @ConfigurationProperties(prefix = "spring.datasource.auth.druid")
    @Bean(name = "authDataSource")
    @Primary
    public DataSource authDataSource() {
        return new DruidDataSource();
    }

    /**
     * SqlSessionFactory配置
     */
    @Bean(name = "authSqlSessionFactory")
    @Primary
    public SqlSessionFactory authSqlSessionFactory(@Qualifier("authDataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource);
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        sqlSessionFactoryBean.setMapperLocations(resolver.getResources(authMapperLocations));

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
    @Bean(name = "authTransactionManager")
    @Primary
    public DataSourceTransactionManager authTransactionManager(@Qualifier("authDataSource") DataSource dataSource) {
        DataSourceTransactionManager dataSourceTransactionManager = new DataSourceTransactionManager();
        dataSourceTransactionManager.setDataSource(dataSource);
        return dataSourceTransactionManager;
    }
}
