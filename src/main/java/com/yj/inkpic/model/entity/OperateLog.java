package com.yj.inkpic.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * 操作日志表
 * @author OuYJ
 * @TableName operate_log
 */
@TableName(value ="operate_log")
@Data
public class OperateLog implements Serializable {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 操作人ID
     */
    private Long userId;

    /**
     * 操作人账号
     */
    private String userName;

    /**
     * 操作模块(如：用户管理)
     */
    private String module;

    /**
     * 操作类型(新增、修改、删除)
     */
    private String type;

    /**
     * 请求方法(类名.方法名)
     */
    private String method;

    /**
     * 方法参数
     */
    private String methodParam;

    /**
     * 返回结果
     */
    private String result;

    /**
     * 操作状态(1成功 0失败)
     */
    private Integer status;

    /**
     * 错误消息(操作失败时记录)
     */
    private String errorMsg;

    /**
     * 执行耗时(毫秒)
     */
    private Integer costTime;

    /**
     * 操作时间
     */
    private LocalDateTime operateTime;

    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}