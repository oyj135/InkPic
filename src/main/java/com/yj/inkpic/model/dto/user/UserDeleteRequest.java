package com.yj.inkpic.model.dto.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author <a href="https://www.ouyangjian.com/">YJ.渔夫.星辰</a>
 * @Date 2026/8/19
 * 删除用户请求
 */
@Data
@ApiModel(value = "UserDeleteRequest", description = "用户删除请求")
public class UserDeleteRequest implements Serializable {

    /**
     * 用户ID
     */
    @ApiModelProperty("用户ID")
    private Long id;

    @Serial
    private static final long serialVersionUID = 1L;
}

