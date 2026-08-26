package com.yj.inkpic.model.dto.user;

import com.yj.inkpic.common.PageRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author <a href="https://www.ouyangjian.com/">YJ.渔夫.星辰</a>
 * @Date 2026/8/19
 * 查询用户请求
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel(value = "UserQueryRequest", description = "用户查询请求")
public class UserQueryRequest extends PageRequest implements Serializable {

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

