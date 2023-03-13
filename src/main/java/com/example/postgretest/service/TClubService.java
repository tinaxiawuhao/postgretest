package com.example.postgretest.service;

import com.example.postgretest.entity.TClub;
import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.example.postgretest.entity.base.SearchPage;

/**
 * (TClub)表服务接口
 *
 * @author makejava
 * @since 2023-03-13 09:45:19
 */
public interface TClubService extends IService<TClub> {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    TClub queryById(Integer id);

    /**
     * 分页查询
     *
     * @param tClub       筛选条件
     * @param pageRequest 分页对象
     * @return 查询结果
     */
    PageInfo<TClub> queryByPage(TClub tClub, SearchPage pageRequest);

    /**
     * 新增数据
     *
     * @param tClub 实例对象
     * @return 实例对象
     */
    TClub insert(TClub tClub);

    /**
     * 修改数据
     *
     * @param tClub 实例对象
     * @return 实例对象
     */
    TClub update(TClub tClub);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    boolean deleteById(Integer id);

}
