package com.example.postgretest.controller;

import com.example.postgretest.entity.CassandraEntity;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "cassandra测试表")
@RestController
@RequestMapping("cassandra")
public class CassandraController {

    @Autowired
    private CassandraTemplate cassandraTemplate;

    //然后可以使用对应方法
    @ResponseBody
    @GetMapping(value = "/select")
    public List<CassandraEntity> select() {
         return cassandraTemplate.select("select * from cassandraentity", CassandraEntity.class);
    }

    @ResponseBody
    @PostMapping(value = "/insert")
    public CassandraEntity insert(@RequestBody CassandraEntity cassandraEntity) {
        return cassandraTemplate.insert(cassandraEntity);
    }
}
