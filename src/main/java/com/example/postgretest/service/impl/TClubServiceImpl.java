package com.example.postgretest.service.impl;

import com.example.postgretest.entity.TClub;
import com.example.postgretest.mapper.postgresql.TClubMapper;
import com.example.postgretest.service.TClubService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.pagehelper.PageHelper;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;
import com.github.pagehelper.PageInfo;
import com.example.postgretest.entity.base.SearchPage;

import javax.annotation.Resource;
import java.util.List;

/**
 * (TClub)表服务实现类
 *
 * @author makejava
 * @since 2023-03-13 09:45:19
 */
@Service("tClubService")
public class TClubServiceImpl extends ServiceImpl<TClubMapper, TClub> implements TClubService {
    @Resource
    private TClubMapper tClubMapper;

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override
    public TClub queryById(Integer id) {
        return this.tClubMapper.selectById(id);
    }

    /**
     * 分页查询
     *
     * @param tClub       筛选条件
     * @param pageRequest 分页对象
     * @return 查询结果
     */
    @Override
    public PageInfo<TClub> queryByPage(TClub tClub, SearchPage pageRequest) {
        PageHelper.startPage(pageRequest.getPage(), pageRequest.getSize());
        List<TClub> list = this.tClubMapper.queryAllByLimit(tClub);
        return new PageInfo<>(list);
    }

    /**
     * 新增数据
     *
     * @param tClub 实例对象
     * @return 实例对象
     */
    @Override
    @Transactional
    public TClub insert(TClub tClub) {
        this.tClubMapper.insert(tClub);
        return tClub;
    }

    /**
     * 修改数据
     *
     * @param tClub 实例对象
     * @return 实例对象
     */
    @Override
    @Transactional
    public TClub update(TClub tClub) {
        this.tClubMapper.update(tClub, Wrappers.<TClub>lambdaUpdate().eq(TClub::getId, tClub.getId()));
        return this.queryById(tClub.getId());
    }

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    @Override
    @Transactional
    public boolean deleteById(Integer id) {
        return this.tClubMapper.deleteById(id) > 0;
    }
}
