package com.example.postgretest.controller;

import com.example.postgretest.entity.TerminalHeartBeatModel;
import com.example.postgretest.exception.basic.APIResponse;
import com.example.postgretest.mapper.influx.InfluxRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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
        this.influxRepository.save(TerminalHeartBeatModel.builder()
                .id(1L)
                .mac("00:aa:00:aa:00:00")
                .time(LocalDateTime.now()).build());
        return APIResponse.ok();
    }

    @GetMapping(value = "/getList")
    @ApiOperation(value = "发现", notes = "发现")
    public APIResponse<List<TerminalHeartBeatModel>> getList() {
        Map<String, Object> map = new TreeMap<>();
        map.put("mac", "00:aa:00:aa:00:00");
        return APIResponse.ok(this.influxRepository.getList(map,LocalDateTime.of(2023,7,12,15, 1, 1),LocalDateTime.now()));
    }

}
