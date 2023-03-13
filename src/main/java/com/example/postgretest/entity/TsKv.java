package com.example.postgretest.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * (TsKv)实体类
 *
 * @author makejava
 * @since 2023-03-13 10:48:33
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("ts_kv")
@ApiModel(value = "TsKv对象", description = "")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TsKv extends Model<TsKv> implements Serializable {
    private static final long serialVersionUID = 785990795612696926L;
    /**
     * 时间戳
     */
    @ApiModelProperty(value = "时间戳", required = true)
    @NotNull(message = "时间戳不能为空")
    private Long ts;
    /**
     * key值
     */
    @ApiModelProperty(value = "key值", required = true)
    @NotNull(message = "key值不能为空")
    private Integer key;
    /**
     * 布尔值
     */
    @ApiModelProperty(value = "布尔值", required = true)
    @NotNull(message = "布尔值不能为空")
    private Boolean boolV;
    /**
     * 字符值
     */
    @ApiModelProperty(value = "字符值", required = true)
    @NotNull(message = "字符值不能为空")
    private String strV;
    /**
     * 长整型值
     */
    @ApiModelProperty(value = "长整型值", required = true)
    @NotNull(message = "长整型值不能为空")
    private Long longV;
    /**
     * 浮点值
     */
    @ApiModelProperty(value = "浮点值", required = true)
    @NotNull(message = "浮点值不能为空")
    private Double dblV;
    /**
     * json值
     */
    @ApiModelProperty(value = "json值", required = true)
    @NotNull(message = "json值不能为空")
    private String jsonV;
}
