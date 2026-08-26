package com.yj.inkpic.model.dto.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author <a href="https://www.ouyangjian.com/">YJ.渔夫.星辰</a>
 * @Date 2026/8/19
 * <p>
 * 用户更新请求
 */
@Data
@ApiModel(value = "UserUpdateRequest", description = "用户更新请求")
public class UserUpdateRequest implements Serializable {

    /**
     * 用户id
     */
    @ApiModelProperty("用户id")
    private Long id;

    /**
     * 用户昵称
     */
    @ApiModelProperty("用户昵称")
    private String userName;

    /**
     * 账号
     */
    @ApiModelProperty("账号")
    private String userAccount;

    /**
     * 用户头像
     */
    @ApiModelProperty("用户头像")
    private String userAvatar;

    /**
     * 用户简介
     */
    @ApiModelProperty("用户简介")
    private String userProfile;

    /**
     * 用户角色: user, admin
     */
    @ApiModelProperty("用户角色: user, admin")
    private String userRole;

    @Serial
    private static final long serialVersionUID = 1L;
}
