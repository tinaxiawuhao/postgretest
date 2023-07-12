package com.example.postgretest.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("temperature")
@ApiModel(value = "Temperature对象", description = "")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Temperature {
    /**
     * 时间戳
     */
    @ApiModelProperty(value = "城市", required = true)
    @NotNull(message = "城市不能为空")
    private String city;
    /**
     * key值
     */
    @ApiModelProperty(value = "value值", required = true)
    @NotNull(message = "value值不能为空")
    private Double value;
}
