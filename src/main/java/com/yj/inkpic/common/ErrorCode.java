package com.yj.inkpic.common;

import lombok.Getter;

/**
 * 自定义错误码
 *
 */
@Getter
public enum ErrorCode {

    /**
     * 40000 - 40999：客户端错误（参数、格式、业务校验不通过等）。
     * 40100 - 40199：认证与授权相关（登录、权限、Token）。
     * 41000 - 41999：特定资源错误（如文件上传、图片处理）。
     * 40900 - 40999：状态冲突（数据重复、乐观锁冲突）。
     * 50000 - 50999：服务端错误（系统异常、第三方服务宕机等）。
     */
    SUCCESS(0, "ok"),
    PARAMS_ERROR(40000, "请求参数错误"),
    PARAMS_IS_NULL(40001, "必填参数为空"),
    NOT_LOGIN_ERROR(40100, "未登录"),
    TOKEN_EXPIRED(40101, "登录已过期"),
    NO_AUTH_ERROR(40103, "无权限"),
    FORBIDDEN_ERROR(40300, "禁止访问"),
    TOO_MANY_REQUESTS(40301, "请求过于频繁"),
    NOT_FOUND_ERROR(40400, "请求数据不存在"),
    DATA_ALREADY_EXIST(40900, "数据已存在"),
    DATA_CONFLICT(40901, "数据冲突，请刷新重试"),
    FILE_UPLOAD_FAILED(41000, "文件上传失败"),
    FILE_SIZE_EXCEED(41001, "文件大小超限"),
    FILE_TYPE_ERROR(41002, "文件类型不合法"),
    SYSTEM_ERROR(50000, "系统内部异常"),
    OPERATION_ERROR(50001, "操作失败");



    /**
     * 状态码
     */
    private final int code;

    /**
     * 信息
     */
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
