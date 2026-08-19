package com.yj.inkpic.aop;

import com.yj.inkpic.annotation.AuthCheck;
import com.yj.inkpic.common.ErrorCode;
import com.yj.inkpic.excption.BusinessException;
import com.yj.inkpic.model.dto.UserJwtDTO;
import com.yj.inkpic.model.entity.User;
import com.yj.inkpic.model.enums.UserRoleEnum;
import com.yj.inkpic.service.UserService;
import com.yj.inkpic.utils.BaseContext;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author <a href="https://www.ouyangjian.com/">YJ.渔夫.星辰</a>
 * @Date 2026/8/18
 * 用户角色效验切面
 */

@Aspect
@Component
public class AuthInterceptor {

    /**
     * 执行拦截
     * @param joinPoint 连接点
     * @param authCheck 用户权限注解
     * @return
     * @throws Throwable
     */
    @Around("@annotation(authCheck)")
    public Object doInterceptor(ProceedingJoinPoint joinPoint, AuthCheck authCheck) throws Throwable {
        // 获取注解的角色要求
        String mustRole = authCheck.mustRole();

        // 转换为枚举值
        UserRoleEnum mustRoleEnum = UserRoleEnum.getEnumByValue(mustRole);
        // 如果不需要权限，放行
        if (mustRoleEnum == null) {
            return joinPoint.proceed();
        }

        // 获取当前登录用户
        UserJwtDTO currentUser = BaseContext.getCurrentUser();
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "用户未登录");
        }
        // 以下代码，必须要有权限才能通过
        UserRoleEnum userRoleEnum = UserRoleEnum.getEnumByValue(currentUser.getUserRole());
        if (userRoleEnum == null) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "用户角色非法");
        }
        // 要求必须要有管理员权限，否则拒绝
        if (UserRoleEnum.ADMIN.equals(mustRoleEnum) && !UserRoleEnum.ADMIN.equals(userRoleEnum)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        return joinPoint.proceed();
    }
}
