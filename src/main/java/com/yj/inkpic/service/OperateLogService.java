package com.yj.inkpic.service;

import com.baomidou.mybatisplus.spring.service.IService;
import com.yj.inkpic.model.entity.OperateLog;

/**
* @author OuYJ
* @description 针对表【operate_log(操作日志表)】的数据库操作Service
* @createDate 2026-08-18 21:08:18
*/
public interface OperateLogService extends IService<OperateLog> {

    /**
     * 异步保存操作日志
     * @param operateLog
     */
    OperateLog saveOperateLog(OperateLog operateLog);
}
