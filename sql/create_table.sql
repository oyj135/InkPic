# 创建数据库
create database if not exists `inkpic`;

use inkpic;

-- 用户表
create table if not exists user
(
    id           bigint auto_increment comment 'id' primary key,
    userAccount  varchar(256)                           not null comment '账号',
    userPassword varchar(512)                           not null comment '密码',
    userName     varchar(256)                           null comment '用户昵称',
    userAvatar   varchar(1024)                          null comment '用户头像',
    userProfile  varchar(512)                           null comment '用户简介',
    userRole     varchar(256) default 'user'            not null comment '用户角色：user/admin',
    vipExpireTime datetime     null comment '会员过期时间',
    vipCode       varchar(128) null comment '会员兑换码',
    vipNumber     bigint       null comment '会员编号',
    shareCode     varchar(20)  DEFAULT NULL COMMENT '分享码',
    inviteUser    bigint       DEFAULT NULL COMMENT '邀请用户 id',
    editTime     datetime     default CURRENT_TIMESTAMP not null comment '编辑时间',
    createTime   datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime   datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    deleted     tinyint      default 0                 not null comment '是否删除 0-未删除，1-已删除',
    UNIQUE KEY uk_userAccount (userAccount),
    INDEX idx_userName (userName)
) comment '用户' collate = utf8mb4_unicode_ci;

-- 操作日志表
CREATE TABLE `operate_log` (
                               `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                               `userId` bigint DEFAULT NULL COMMENT '操作人ID',
                               `userName` varchar(50) DEFAULT NULL COMMENT '操作人账号',
                               `module` varchar(50) DEFAULT NULL COMMENT '操作模块(如：用户管理)',
                               `type` varchar(50) DEFAULT NULL COMMENT '操作类型(如：新增、修改、删除、查询)',
                               `method` varchar(200) DEFAULT NULL COMMENT '请求方法(类名.方法名)',
                               `methodParam` text DEFAULT NULL COMMENT '方法参数',
                               `result` text DEFAULT NULL COMMENT '返回结果',
                               `status` tinyint DEFAULT 1 COMMENT '操作状态(1成功 0失败)',
                               `errorMsg` text DEFAULT NULL COMMENT '错误消息(操作失败时记录)',
                               `costTime` int DEFAULT NULL COMMENT '执行耗时(毫秒)',
                               `operateTime` datetime default CURRENT_TIMESTAMP not null COMMENT '操作时间',
                               PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表' collate = utf8mb4_unicode_ci;


