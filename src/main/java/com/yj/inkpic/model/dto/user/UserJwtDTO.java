package com.yj.inkpic.model.dto.user;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户 jwt信
 */
@Data
public class UserJwtDTO implements Serializable {
    /**
     * id
     */
    private Long id;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 用户角色：user/admin
     */
    private String userRole;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}