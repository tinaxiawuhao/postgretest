package com.example.postgretest.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.postgretest.entity.TsKv;
import com.example.postgretest.entity.base.Table;
import com.github.pagehelper.PageInfo;
import com.example.postgretest.entity.base.SearchPage;

/**
 * (TsKv)表服务接口
 *
 * @author makejava
 * @since 2023-03-13 10:47:15
 */
public interface TsKvService extends IService<TsKv> {

    /**
     * 通过ID查询单条数据
     *
     * @param ts 主键
     * @return 实例对象
     */
    TsKv queryById(Long ts);

    /**
     * 分页查询
     *
     * @param tsKv        筛选条件
     * @param pageRequest 分页对象
     * @return 查询结果
     */
    PageInfo<TsKv> queryByPage(TsKv tsKv, SearchPage pageRequest);

    /**
     * 新增数据
     *
     * @param tsKv 实例对象
     * @return 实例对象
     */
    TsKv insert(TsKv tsKv);

    /**
     * 创建超表
     *
     * @param tableName 表名
     * @param column 列名
     */
    void createHypertable(Table table);

    /**
     * 创建分布式超表
     *
     * @param tableName 表名
     * @param column 列名
     */
    void createDistributedHypertable(Table table);
}
