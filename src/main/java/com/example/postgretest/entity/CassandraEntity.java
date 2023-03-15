package com.example.postgretest.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;

import javax.validation.constraints.NotNull;

/**
 * (TsKv)实体类
 *
 * @author makejava
 * @since 2023-03-13 10:48:33
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "CassandraEntity对象", description = "测试cassandra对象")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CassandraEntity {
    /**
     * 时间戳
     */
    @PrimaryKey
    @ApiModelProperty(value = "id", required = true)
    @NotNull(message = "id不能为空")
    private Integer id;
    /**
     * key值
     */
    @ApiModelProperty(value = "key值", required = true)
    @NotNull(message = "key值不能为空")
    private String key;
    /**
     * key值
     */
    @ApiModelProperty(value = "名称", required = true)
    @NotNull(message = "名称不能为空")
    private String name;
}
