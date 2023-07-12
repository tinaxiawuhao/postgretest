package com.example.postgretest.controller;

import com.example.postgretest.entity.TsKv;
import com.example.postgretest.entity.base.SearchPage;
import com.example.postgretest.exception.basic.APIResponse;
import com.example.postgretest.mapper.influx.InfluxRepository;
import com.example.postgretest.service.TsKvService;
import com.github.pagehelper.PageInfo;
import com.influxdb.query.FluxTable;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * (Influx)表控制层
 *
 * @author makejava
 * @since 2023-03-13 09:45:19
 */
@Api(tags = "Influx操作")
@RestController
@RequestMapping("influx")
public class InfluxController {

    /**
     * 服务对象
     */
    @Resource
    private InfluxRepository influxRepository;


    @GetMapping(value = "/save")
    @ApiOperation(value = "保存", notes = "保存")
    public APIResponse save() {
        Map<String,Object> map=new HashMap<>();
        map.put("humidity",121);
        map.put("deviceId",11);
        this.influxRepository.save("tableName",map);
        return APIResponse.ok();
    }

    @GetMapping(value = "/findAll")
    @ApiOperation(value = "发现", notes = "发现")
    public APIResponse<List<FluxTable>> findAll() {
        return APIResponse.ok(this.influxRepository.findAll());
    }

    @GetMapping(value = "/insertData")
    @ApiOperation(value = "同步写入API", notes = "同步写入API")
    public APIResponse insertData() {
        this.influxRepository.insertData();
        return APIResponse.ok();
    }

    @GetMapping(value = "/aysnInserData")
    @ApiOperation(value = "异步写入API", notes = "异步写入API")
    public APIResponse aysnInserData() {
        this.influxRepository.aysnInserData();
        return APIResponse.ok();
    }

    @GetMapping(value = "/delete")
    @ApiOperation(value = "删除", notes = "删除")
    public APIResponse delete() {
        this.influxRepository.delete();
        return APIResponse.ok();
    }

    @GetMapping(value = "/query")
    @ApiOperation(value = "查询", notes = "查询")
    public APIResponse query() {
        this.influxRepository.query();
        return APIResponse.ok();
    }
}
