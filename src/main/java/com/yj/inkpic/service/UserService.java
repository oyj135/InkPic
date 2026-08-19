package com.yj.inkpic.service;

import com.baomidou.mybatisplus.spring.service.IService;
import com.yj.inkpic.model.entity.User;
import com.yj.inkpic.model.vo.LoginUserVO;

import javax.servlet.http.HttpServletRequest;

/**
* @author OuYJ
* @description 针对表【user(用户)】的数据库操作Service
* @createDate 2026-08-14 17:14:53
*/
public interface UserService extends IService<User> {

    /**
     * 用户注册
     * @param userAccount 用户账号
     * @param userPassword 用户密码
     * @param checkPassword 确认密码
     * @return 用户id
     */
    Long userRegister(String userAccount, String userPassword, String checkPassword);

    /**
     * 用户登录
     * @param userAccount 用户账号
     * @param userPassword 用户密码
     * @return 登录用户信息 (脱敏)
     */
    LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request);


    /**
     * 获取当前登录用户
     * @return
     */
    User getLoginUser();

    /**
     * 获取脱敏的已登录用户信息
     * @param user
     * @return
     */
    LoginUserVO getLoginUserVO(User user);
}
