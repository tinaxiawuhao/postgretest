package com.example.postgretest.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.postgretest.entity.base.SearchPage;
import com.example.postgretest.entity.TsKv;
import com.example.postgretest.entity.base.Table;
import com.example.postgretest.mapper.postgresql.TsKvMapper;
import com.example.postgretest.service.TsKvService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * (TsKv)表服务实现类
 *
 * @author makejava
 * @since 2023-03-13 10:47:15
 */
@Service("tsKvService")
public class TsKvServiceImpl extends ServiceImpl<TsKvMapper, TsKv> implements TsKvService {
    @Resource
    private TsKvMapper tsKvMapper;

    /**
     * 通过ID查询单条数据
     *
     * @param ts 主键
     * @return 实例对象
     */
    @Override
    public TsKv queryById(Long ts) {
        return this.tsKvMapper.selectById(ts);
    }

    /**
     * 分页查询
     *
     * @param tsKv        筛选条件
     * @param pageRequest 分页对象
     * @return 查询结果
     */
    @Override
    public PageInfo<TsKv> queryByPage(TsKv tsKv, SearchPage pageRequest) {
        PageHelper.startPage(pageRequest.getPage(), pageRequest.getSize());
        List<TsKv> list = this.tsKvMapper.queryAllByLimit(tsKv);
        return new PageInfo<>(list);
    }

    /**
     * 新增数据
     *
     * @param tsKv 实例对象
     * @return 实例对象
     */
    @Override
    @Transactional
    public TsKv insert(TsKv tsKv) {
        this.tsKvMapper.insert(tsKv);
        return tsKv;
    }

    /**
     * 创建超表
     *
     * @param table 表名
     */
    @Override
    public void createHypertable(Table table) {
        this.tsKvMapper.createHypertable(table);
    }

    /**
     * 创建分布式超表
     *
     * @param table 表名
     */
    @Override
    public void createDistributedHypertable(Table table) {
        this.tsKvMapper.createDistributedHypertable(table);
    }

}
