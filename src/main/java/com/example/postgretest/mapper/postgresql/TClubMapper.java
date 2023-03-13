package com.example.postgretest.mapper.postgresql;

import com.example.postgretest.entity.TClub;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * (TClub)表数据库访问层
 *
 * @author makejava
 * @since 2023-03-13 09:45:19
 */
public interface TClubMapper extends BaseMapper<TClub> {

    /**
     * 查询指定行数据
     *
     * @param tClub 查询条件
     * @return 对象列表
     */
    List<TClub> queryAllByLimit(TClub tClub);


    /**
     * 批量新增数据（MyBatis原生foreach方法）
     *
     * @param entities List<TClub> 实例对象列表
     * @return 影响行数
     */
    int insertBatch(@Param("entities") List<TClub> entities);

    /**
     * 批量按主键更新数据（MyBatis原生foreach方法）
     *
     * @param entities List<TClub> 实例对象列表
     * @return 影响行数
     * @throws org.springframework.jdbc.BadSqlGrammarException 入参是空List的时候会抛SQL语句错误的异常，请自行校验入参
     */
    int updateBatch(@Param("entities") List<TClub> entities);

}
