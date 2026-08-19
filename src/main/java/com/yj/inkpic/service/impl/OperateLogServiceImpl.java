package com.yj.inkpic.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yj.inkpic.common.ErrorCode;
import com.yj.inkpic.excption.BusinessException;
import com.yj.inkpic.model.entity.OperateLog;
import com.yj.inkpic.service.OperateLogService;
import com.yj.inkpic.mapper.OperateLogMapper;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/**
* @author OuYJ
* @description 针对表【operate_log(操作日志表)】的数据库操作Service实现
* @createDate 2026-08-18 21:08:18
*/
@Service
public class OperateLogServiceImpl extends ServiceImpl<OperateLogMapper, OperateLog>
    implements OperateLogService{

    /**
     * 异步保存操作日志
     * @param operateLog 操作日志
     */
    @Override
    public OperateLog saveOperateLog(OperateLog operateLog) {
        if (operateLog == null) {
            log.error("保存操作日志失败: 日志对象为空");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "操作日志为空");
        }

        // 异步保存操作日志
        CompletableFuture.runAsync(() -> {
            try {
                // 保存操作日志
                boolean save = this.save(operateLog);
                if (!save) {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "保存操作日志失败, 数据库错误");
                }
            } catch (Exception e) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "保存操作日志失败");
            }
        });
        return operateLog;
    }
}




