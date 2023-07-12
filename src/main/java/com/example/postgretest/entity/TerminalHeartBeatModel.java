package com.example.postgretest.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import lombok.*;
import lombok.experimental.Accessors;
import org.influxdb.annotation.Column;
import org.influxdb.annotation.Measurement;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "terminal_heart_beat", description = "")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Measurement(name = "terminal_heart_beat")
public class TerminalHeartBeatModel {
    @Column(name = "id", tag = true)  //tag 可以理解为influxdb的索引
    private Long id;
    @Column(name = "mac")
    private String mac;
    @Column(name = "time")
    private LocalDateTime time;
}