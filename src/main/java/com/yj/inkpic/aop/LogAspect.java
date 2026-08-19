package com.yj.inkpic.aop;

import cn.hutool.json.JSONUtil;
import com.yj.inkpic.annotation.LogOperation;
import com.yj.inkpic.common.ErrorCode;
import com.yj.inkpic.excption.BusinessException;
import com.yj.inkpic.model.entity.OperateLog;
import com.yj.inkpic.service.OperateLogService;
import com.yj.inkpic.utils.BaseContext;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

@Aspect
@Component
@Slf4j
public class LogAspect {

    @Resource
    private OperateLogService operateLogService;

    /**
     * 环绕通知拦截带有 @Log 注解的方法
     */
    @Around("@annotation(logOperation)")
    public Object around(ProceedingJoinPoint point, LogOperation logOperation) throws Throwable {

        long startTime = System.currentTimeMillis();
        
        // 获取方法名
        String methodName = point.getSignature().getName();
        // 获取注解
        String module = logOperation.module();
        String type = logOperation.type();
        // 构建日志实体
        OperateLog operateLog = new OperateLog();
        operateLog.setModule(module);
        operateLog.setType(type);
        operateLog.setMethod(methodName);

        // 记录请求参数
        try {
            Object[] args = point.getArgs();
            if (args != null && args.length > 0) {
                operateLog.setMethodParam(JSONUtil.toJsonStr(args));
            }
        } catch (Exception e) {
            log.warn("记录请求参数异常: {}", e.getMessage());
        }

        // 执行目标方法
        Object result = null;
        try {
            result = point.proceed();
            operateLog.setStatus(1); // 成功
            // 记录返回结果
            operateLog.setResult(JSONUtil.toJsonStr(result));
        } catch (Throwable e) {
            operateLog.setStatus(0); // 失败
            operateLog.setErrorMsg(e.getMessage().length() > 2000 ? e.getMessage().substring(0, 2000) : e.getMessage());
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "日志操作失败"); // 异常继续抛出，交给全局异常处理器
        } finally {
            operateLog.setCostTime((int) (System.currentTimeMillis() - startTime));
            operateLog.setOperateTime(LocalDateTime.now());
            
            // 获取当前登录用户ID和账号，
             operateLog.setUserId(BaseContext.getCurrentUser().getId());
             operateLog.setUserName(BaseContext.getCurrentUser().getUserAccount());

            // 异步保存日志，避免影响业务接口性能
            // todo: 后续可使用消息队列
            CompletableFuture.runAsync(() -> {
                try {
                    OperateLog savedOperateLog = operateLogService.saveOperateLog(operateLog);
                    log.info("保存操作日志成功: {}", savedOperateLog);
                } catch (Exception e) {
                    log.error("保存操作日志异常: {}, 日志内容: {}", e.getMessage(), operateLog);
                }
            });
        }
        return result;
    }
}
