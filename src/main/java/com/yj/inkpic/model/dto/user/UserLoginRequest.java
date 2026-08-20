package com.yj.inkpic.model.dto.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author <a href="https://www.ouyangjian.com/">YJ.渔夫.星辰</a>
 * @Date 2026/8/14
 *
 * 用户登录请求
 */
@Data
@ApiModel("用户登录请求")
public class UserLoginRequest {

    @ApiModelProperty("用户账号")
    private String userAccount;

    @ApiModelProperty("用户密码")
    private String userPassword;
}
