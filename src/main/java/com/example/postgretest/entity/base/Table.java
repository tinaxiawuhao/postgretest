package com.example.postgretest.entity.base;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Table {

    @ApiModelProperty(value = "表名")
    private String tableName = "ts_kv";

    @ApiModelProperty(value = "列名")
    private String column = "ts";
}
