package com.example.postgretest;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(basePackages = "com.example.postgretest.mapper")
public class PostgretestApplication {

    public static void main(String[] args) {
        SpringApplication.run(PostgretestApplication.class, args);
    }

}
