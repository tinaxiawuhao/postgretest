package com.example.postgretest.controller;

import com.example.postgretest.entity.TClub;
import com.example.postgretest.entity.base.SearchPage;
import com.example.postgretest.exception.basic.APIResponse;
import com.example.postgretest.service.TClubService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * (TClub)表控制层
 *
 * @author makejava
 * @since 2023-03-13 09:45:19
 */
@Api(tags = "信息表")
@RestController
@RequestMapping("tClub")
public class TClubController {
    /**
     * 服务对象
     */
    @Resource
    private TClubService tClubService;

    /**
     * 分页查询
     *
     * @param tClub       筛选条件
     * @param pageRequest 分页对象
     * @return 查询结果
     */
    @PostMapping(value = "/queryByPage")
    @ApiOperation(value = "获取分页数据", notes = "获取分页数据")
    public APIResponse<PageInfo<TClub>> queryByPage(@RequestBody TClub tClub, SearchPage pageRequest) {
        return APIResponse.ok(this.tClubService.queryByPage(tClub, pageRequest));
    }

    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @GetMapping(value = "/queryById/{id}")
    @ApiOperation(value = "根据id获取单条数据", notes = "根据id获取单条数据")
    public APIResponse<TClub> queryById(@PathVariable("id") Integer id) {
        return APIResponse.ok(this.tClubService.queryById(id));
    }

    /**
     * 新增数据
     *
     * @param tClub 实体
     * @return 新增结果
     */
    @PostMapping(value = "/add")
    @ApiOperation(value = "新增数据", notes = "新增数据")
    public APIResponse<TClub> add(@RequestBody @Valid TClub tClub) {
        return APIResponse.ok(this.tClubService.insert(tClub));
    }

    /**
     * 编辑数据
     *
     * @param tClub 实体
     * @return 编辑结果
     */
    @PutMapping(value = "/edit")
    @ApiOperation(value = "编辑数据", notes = "编辑数据")
    public APIResponse<TClub> edit(@RequestBody @Valid TClub tClub) {
        return APIResponse.ok(this.tClubService.update(tClub));
    }

    /**
     * 删除数据
     *
     * @param id 主键
     * @return 删除是否成功
     */
    @DeleteMapping(value = "/deleteById")
    @ApiOperation(value = "根据主键删除数据", notes = "根据主键删除数据")
    public APIResponse<Boolean> deleteById(Integer id) {
        return APIResponse.ok(this.tClubService.deleteById(id));
    }

}

