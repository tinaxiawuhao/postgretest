package com.example.postgretest;

import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//必须去除自动加载配置多数据源才能使用
//@SpringBootApplication(exclude = MybatisAutoConfiguration.class)
@SpringBootApplication
@MapperScan(basePackages = "com.example.postgretest.mapper")
public class PostgretestApplication {

    public static void main(String[] args) {
        SpringApplication.run(PostgretestApplication.class, args);
    }

}
