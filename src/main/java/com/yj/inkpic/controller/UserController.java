package com.yj.inkpic.controller;

import com.yj.inkpic.annotation.LogOperation;
import com.yj.inkpic.common.BaseResponse;
import com.yj.inkpic.common.ErrorCode;
import com.yj.inkpic.common.ResultUtils;
import com.yj.inkpic.excption.ThrowUtils;
import com.yj.inkpic.model.dto.UserLoginRequest;
import com.yj.inkpic.model.dto.UserRegisterRequest;
import com.yj.inkpic.model.entity.User;
import com.yj.inkpic.model.vo.LoginUserVO;
import com.yj.inkpic.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author <a href="https://www.ouyangjian.com/">YJ.渔夫.星辰</a>
 * @Date 2026/8/14
 *
 * 用户接口
 */

@Api("用户接口")
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    /**
     * 用户注册
     * @param userRegisterRequest 用户注册请求
     * @return 新用户id
     */
    @PostMapping("/register")
    @ApiOperation("用户注册")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        // 效验
        ThrowUtils.throwIf(userRegisterRequest == null, ErrorCode.PARAMS_ERROR);
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();

        Long userId = userService.userRegister(userAccount, userPassword, checkPassword);
        return ResultUtils.success(userId);

    }

    /**
     * 用户登录
     * @param userLoginRequest 用户登录请求
     * @return 登录用户信息（脱敏)
     */
    @PostMapping("/login")
    @ApiOperation("用户登录")
    public BaseResponse<LoginUserVO> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        // 效验
        ThrowUtils.throwIf(userLoginRequest == null, ErrorCode.PARAMS_ERROR);
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        LoginUserVO loginUserVO = userService.userLogin(userAccount,userPassword, request);
        return ResultUtils.success(loginUserVO);
    }

    /**
     * 获取当前登录用户信息
     * @return 当前登录用户信息
     */
    @GetMapping("/get/login")
    @ApiOperation("获取当前登录用户信息")
    public BaseResponse<LoginUserVO> getLoginUser() {
        User loginUser = userService.getLoginUser();
        return ResultUtils.success(userService.getLoginUserVO(loginUser));
    }
}
