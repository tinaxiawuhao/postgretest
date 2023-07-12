package com.example.postgretest.mapper.influx;

import com.example.postgretest.config.InfluxConfig;
import com.example.postgretest.entity.Temperature;
import com.influxdb.client.*;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import com.influxdb.query.FluxTable;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

@Repository
public class InfluxRepository {

    @Resource
    private InfluxDBClient influxDBClient;

    @Resource
    private InfluxConfig influxConfig;

    /**
     * 保存
     *
     * @param tableName 表名
     * @param fields
     */
    public void save(String tableName, Map<String, Object> fields) {
        WriteOptions writeOptions = WriteOptions.builder()
                .batchSize(5000)
                .flushInterval(1000)
                .bufferLimit(10000)
                .jitterInterval(1000)
                .retryInterval(5000)
                .build();
        try (WriteApi writeApi = influxDBClient.getWriteApi(writeOptions)) {
            String deviceId = fields.get("deviceId").toString();
            fields.remove("deviceId");
            Point point = Point
                    .measurement(tableName)
                    .addTag("deviceId", deviceId)
                    .addFields(fields)
                    .time(Instant.now(), WritePrecision.NS);
            writeApi.writePoint(influxConfig.getBucket(), influxConfig.getOrg(), point);
//            writeApi.writePoint(point);
        }
    }

    /**
     * 查询语法说明
     * 1、bucket 桶
     * 2、range 指定起始时间段
     * range有两个参数start，stop，stop不设置默认为当前。
     * range可以是相对的（使用负持续时间）或绝对（使用时间段）
     * 3、filter 过滤条件查询 _measurement 表  _field 字段
     * 4、yield()函数作为查询结果输出过滤的tables。
     *
     */
    public List<FluxTable> findAll() {
        String sql = "from(bucket: \"%s\") |> range(start: -1m)";
        sql += "  |> filter(fn: (r) => r._measurement == \"%s\" and";
        sql += "  r._field == \"%s\")";
        sql += "  |> yield()";
        String flux = String.format(sql, influxConfig.getBucket(), "java_api_bucket", "humidity");
        QueryApi queryApi = influxDBClient.getQueryApi();
        return queryApi.query(flux);
    }

    /**
     * 同步写入API WriteApiBlocking
     * client.getWriteApiBlocking()
     */
    public void insertData() {
        WriteApiBlocking writeApiBlocking = influxDBClient.getWriteApiBlocking();
        // 1. 通过Influx 行协议的方法写入 writeRecord
        // 选择时间戳模式
        writeApiBlocking.writeRecord(WritePrecision.MS, "temperature,city=bj value=34.2");

        // 2. 通过写入 Point点API
        Point point = Point.measurement("temperature")
                .addTag("city", "sh")
                .addField("value", 32.12);
        writeApiBlocking.writePoint(point);

        // 3. POJO的模式写入
        Temperature temperature = new Temperature();
        temperature.setCity("sc");
        temperature.setValue(40.1);
        writeApiBlocking.writeMeasurement(WritePrecision.MS, temperature);
    }

    /**
     * 异步写入API
     * 1. 默认INFLUX 在将请求存放在缓冲区 每1s(可调整) 或者 每批次数据大于1000条(可调整) 才会一次性发送给Influx Server
     * 2. 调用flush会在强制发送缓冲区的请求
     * 3. 调用close会强制发送缓冲区数据
     * 4. 其他写入API 与同步写 一样 可以使用 行协议 Point 或者 POJO
     */

    public void aysnInserData() {
        WriteOptions options = WriteOptions.builder()
                // 每批次500条
                .batchSize(500)
                // 每2s 刷写一次
                .flushInterval(2000)
                .build();
        WriteApi writeApi = influxDBClient.getWriteApi(options);

        Temperature temperature = new Temperature();
        temperature.setCity("hk");
        temperature.setValue(23.1);
        writeApi.writeMeasurement(WritePrecision.MS, temperature);

        writeApi.flush();

    }

    /**
     * 删除某一时间窗口指定的数据
     */

    public void delete() {

        /**
         *  只能通过tag进行操作
         *  "tag1=\"value1\" and (tag2=\"value2\" and tag3!=\"value3\")"
         *
         */
        String predictSql = "city=\"hk\"";
        DeleteApi deleteApi = influxDBClient.getDeleteApi();
        deleteApi.delete(OffsetDateTime.now().minusHours(12),
                OffsetDateTime.now(),
                predictSql,
                influxConfig.getBucket(),
                influxConfig.getOrg()
        );
    }


    public List<Temperature> query(){
        QueryApi queryApi = influxDBClient.getQueryApi();
        String queryFlux =
                "from(bucket: \"java_api_bucket\")\n" +
                        "  |> range(start: -12h)\n" +
                        "  |> filter(fn: (r) => r[\"_measurement\"] == \"temperature\")\n" +
                        "  |> filter(fn: (r) => r[\"_field\"] == \"value\")\n" +
                        "  |> filter(fn: (r) => r[\"city\"] == \"sh\")\n" +
                        "  |> aggregateWindow(every: 5s, fn: mean, createEmpty: false)\n" +
                        "  |> yield(name: \"mean\")";
        return queryApi.query(queryFlux, Temperature.class);
    }


}