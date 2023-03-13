package com.example.postgretest.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * (TClub)实体类
 *
 * @author makejava
 * @since 2023-03-13 09:45:19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_club")
@ApiModel(value = "TClub对象", description = "")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TClub extends Model<TClub> implements Serializable {
    private static final long serialVersionUID = -49588746826117826L;
    /**
     * 主键
     */
    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    /**
     * 姓名
     */
    @ApiModelProperty(value = "姓名", required = true)
    @NotNull(message = "姓名不能为空")
    private String name;
    /**
     * 金额
     */
    @ApiModelProperty(value = "金额", required = true)
    @NotNull(message = "金额不能为空")
    private Integer money;
    /**
     * 昵称
     */
    @ApiModelProperty(value = "昵称", required = true)
    @NotNull(message = "昵称不能为空")
    private String nickName;
    /**
     * 生日
     */
    @ApiModelProperty(value = "生日", required = true)
    @NotNull(message = "生日不能为空")
    private Date birthday;
    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间", required = true)
    @NotNull(message = "创建时间不能为空")
    private Date createTime;
    /**
     * 修改时间
     */
    @ApiModelProperty(value = "修改时间", required = true)
    @NotNull(message = "修改时间不能为空")
    private Date updateTime;
}
