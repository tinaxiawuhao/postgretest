package com.example.postgretest.datasource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;


@Configuration
@MapperScan(basePackages = "com.example.postgretest.mapper.postgresql",sqlSessionTemplateRef = "secondarySqlSessionTemplate")
@ConditionalOnProperty(prefix = "datasource", value = "type", havingValue = "postgresql")
public class SecondaryDataSourceConfig {

    @Bean(name = "secondaryDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.secondary")
    public DataSource secondaryDataSource(){
        return DataSourceBuilder.create().build();
    }

    @Bean("secondarySqlSessionFactory")
    public SqlSessionFactory secondarySqlSessionFactory(
            @Qualifier("secondaryDataSource") DataSource dataSource)
            throws Exception{
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        // 设置xml文件存放位置
        bean.setMapperLocations(new PathMatchingResourcePatternResolver()
                .getResources("classpath:mapper/postgresql/*.xml"));
        bean.setTypeAliasesPackage("com.example.postgretest.entity");
        return bean.getObject();
    }

    @Bean("secondaryTransactionManager")
    public DataSourceTransactionManager secondaryTransactionManager(
            @Qualifier("secondaryDataSource") DataSource dataSource){
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean("secondarySqlSessionTemplate")
    public SqlSessionTemplate secondarySqlSessionTemplate(
            @Qualifier("secondarySqlSessionFactory") SqlSessionFactory sqlSessionFactory){
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}