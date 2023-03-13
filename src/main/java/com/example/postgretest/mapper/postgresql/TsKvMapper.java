package com.example.postgretest.mapper.postgresql;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.postgretest.entity.TsKv;
import com.example.postgretest.entity.base.Table;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * (TsKv)表数据库访问层
 *
 * @author makejava
 * @since 2023-03-13 10:47:15
 */
public interface TsKvMapper extends BaseMapper<TsKv> {

    /**
     * 查询指定行数据
     *
     * @param tsKv 查询条件
     * @return 对象列表
     */
    List<TsKv> queryAllByLimit(TsKv tsKv);


    /**
     * 批量新增数据（MyBatis原生foreach方法）
     *
     * @param entities List<TsKv> 实例对象列表
     * @return 影响行数
     */
    int insertBatch(@Param("entities") List<TsKv> entities);

    /**
     * 批量按主键更新数据（MyBatis原生foreach方法）
     *
     * @param entities List<TsKv> 实例对象列表
     * @return 影响行数
     * @throws org.springframework.jdbc.BadSqlGrammarException 入参是空List的时候会抛SQL语句错误的异常，请自行校验入参
     */
    int updateBatch(@Param("entities") List<TsKv> entities);

    /**
     * 创建超表
     *
     * @param table Table 表信息
     */
    void createHypertable(@Param("table") Table table);

    /**
     * 创建分布式超表
     *
     * @param table Table 表信息
     */
    void createDistributedHypertable(@Param("table") Table table);
}
