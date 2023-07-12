package com.example.postgretest.mapper.influx;

import com.example.postgretest.entity.TerminalHeartBeatModel;
import org.springframework.stereotype.Repository;
import plus.ojbk.influxdb.core.InfluxdbTemplate;
import plus.ojbk.influxdb.core.Op;
import plus.ojbk.influxdb.core.Query;
import plus.ojbk.influxdb.core.enums.Order;
import plus.ojbk.influxdb.core.model.QueryModel;
import plus.ojbk.influxdb.util.InfluxdbUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Repository
public class InfluxRepository {

    @Resource
    private InfluxdbTemplate influxdbTemplate;

    /**
     * 获取集合
     * @param map
     * @param start
     * @param end
     * @return
     */
    public List<TerminalHeartBeatModel> getList(Map<String,Object> map, LocalDateTime start, LocalDateTime end){
        //类似MybatisPlus中的QueryWrapper
        QueryModel queryModel = new QueryModel();
        queryModel.setMeasurement(InfluxdbUtils.getMeasurement(TerminalHeartBeatModel.class));
        //Map<String, Object> map = new TreeMap<>();
        //map.put("mac", "00:aa:00:aa:00:00");
        queryModel.setMap(map);
        queryModel.setStart(start);
        queryModel.setEnd(end);
        queryModel.setUseTimeZone(true);  //时区
        queryModel.setOrder(Order.DESC);  //排序
        //where 条件中额外参数可放入model.setMap();
        queryModel.setWhere(Op.where(queryModel));
        return influxdbTemplate.selectList(Query.build(queryModel), TerminalHeartBeatModel.class);
    }
    /** 新增 */
    public void save(TerminalHeartBeatModel model){
        influxdbTemplate.insert(model);
    }


}