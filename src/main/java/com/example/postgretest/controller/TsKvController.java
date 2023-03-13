package com.example.postgretest.controller;

import com.example.postgretest.entity.base.SearchPage;
import com.example.postgretest.entity.TsKv;
import com.example.postgretest.entity.base.Table;
import com.example.postgretest.exception.basic.APIResponse;
import com.example.postgretest.service.TsKvService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * (TsKv)表控制层
 *
 * @author makejava
 * @since 2023-03-13 10:47:15
 */
@Api(tags = "时序表")
@RestController
@RequestMapping("tsKv")
public class TsKvController {
    /**
     * 服务对象
     */
    @Resource
    private TsKvService tsKvService;

    /**
     * 分页查询
     *
     * @param tsKv        筛选条件
     * @param pageRequest 分页对象
     * @return 查询结果
     */
    @PostMapping(value = "/queryByPage")
    @ApiOperation(value = "获取分页数据", notes = "获取分页数据")
    public APIResponse<PageInfo<TsKv>> queryByPage(@RequestBody TsKv tsKv, SearchPage pageRequest) {
        return APIResponse.ok(this.tsKvService.queryByPage(tsKv, pageRequest));
    }

    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @GetMapping(value = "/queryById/{id}")
    @ApiOperation(value = "根据id获取单条数据", notes = "根据id获取单条数据")
    public APIResponse<TsKv> queryById(@PathVariable("id") Long id) {
        return APIResponse.ok(this.tsKvService.queryById(id));
    }

    /**
     * 新增数据
     *
     * @param tsKv 实体
     * @return 新增结果
     */
    @PostMapping(value = "/add")
    @ApiOperation(value = "新增数据", notes = "新增数据")
    public APIResponse<TsKv> add(@RequestBody @Valid TsKv tsKv) {
        return APIResponse.ok(this.tsKvService.insert(tsKv));
    }
}

