package com.example.postgretest.config;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.cassandra.config.AbstractCassandraConfiguration;

@EqualsAndHashCode(callSuper = true)
@Configuration
@Data
@ConfigurationProperties(prefix = "spring.data.cassandra")
public class CassandraConfig extends AbstractCassandraConfiguration {
    //空间名称
    private String keyspaceName;
    //节点IP（连接的集群节点IP）
    private String contactPoints;
    //端口
    private int port;
    //集群名称
    private String clusterName;
    //用户名
    private String username;
    //密码呢
    private String password;

}