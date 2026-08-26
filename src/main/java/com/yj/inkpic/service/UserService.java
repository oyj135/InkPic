package com.yj.inkpic.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yj.inkpic.model.dto.user.UserAddRequest;
import com.yj.inkpic.model.dto.user.UserQueryRequest;
import com.yj.inkpic.model.dto.user.UserUpdateRequest;
import com.yj.inkpic.model.entity.User;
import com.yj.inkpic.model.vo.LoginUserVO;
import com.yj.inkpic.model.vo.UserVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

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
     * 获取当前登录用户信息
     * @return
     */
    User getLoginUser();

    /**
     * 获取脱敏的已登录用户信息
     * @param user
     * @return
     */
    LoginUserVO getLoginUserVO(User user);

    /**
     * 用户注销
     *
     * @param request 请求（用于获取 token）
     * @return 注销是否成功
     */
    boolean userLogout(HttpServletRequest request);

    /**
     * 添加用户
     *
     * @param userAddRequest
     * @return
     */
    Long addUser(UserAddRequest userAddRequest);

    /**
     * 更新用户信息
     *
     * @param userUpdateRequest
     * @return
     */
    Boolean updateUser(UserUpdateRequest userUpdateRequest);

    /**
     * 获取用户VO信息
     *
     * @param user
     * @return
     */
    UserVO getUserVO(User user);

    /**
     * 获取用户VO信息列表
     *
     * @param userList
     * @return
     */
    List<UserVO> getUserVOList(List<User> userList);

    /**
     * 获取用户查询条件构造器
     *
     * @param userQueryRequest
     * @return
     */
    Wrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest);

    /**
     * 是否为管理员
     *
     * @param user 当前登录的 user
     * @return 是否是管理员 true 表示 是 、false 表示 否
     */
    boolean isAdmin(User user);


}
