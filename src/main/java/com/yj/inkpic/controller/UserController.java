package com.yj.inkpic.controller;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yj.inkpic.annotation.AuthCheck;
import com.yj.inkpic.annotation.LogOperation;
import com.yj.inkpic.common.BaseResponse;
import com.yj.inkpic.common.ErrorCode;
import com.yj.inkpic.common.ResultUtils;
import com.yj.inkpic.constant.LogConstant;
import com.yj.inkpic.constant.RedisConstant;
import com.yj.inkpic.constant.UserConstant;
import com.yj.inkpic.excption.BusinessException;
import com.yj.inkpic.excption.ThrowUtils;
import com.yj.inkpic.model.dto.user.*;
import com.yj.inkpic.model.entity.User;
import com.yj.inkpic.model.vo.LoginUserVO;
import com.yj.inkpic.model.vo.UserVO;
import com.yj.inkpic.service.UserService;
import com.yj.inkpic.utils.LoginRateLimiter;
import com.yj.inkpic.utils.RegexUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author <a href="https://www.ouyangjian.com/">YJ.渔夫.星辰</a>
 * @Date 2026/8/14
 *
 * 用户接口
 */

@Api(tags = "用户模块接口")
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Resource
    private UserService userService;

    @Resource
    private LoginRateLimiter loginRateLimiter;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

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
     * 发送验证码
     * @param phone 手机号
     * @param request 请求
     * @return 发送结果
     */
    @PostMapping("/code")
    @ApiOperation("发送验证码")
    public BaseResponse<String> sendCode(String phone, HttpServletRequest request) {
        // 1.效验手机号
        ThrowUtils.throwIf(RegexUtils.isPhoneInvalid(phone), ErrorCode.PARAMS_ERROR, "手机号格式不正确");
        // 2.生成验证码
        String code = RandomUtil.randomNumbers(6);
        // 3.存入session
        request.getSession().setAttribute("code", code);
        // 4. 保存验证码到 redis
        stringRedisTemplate.opsForValue().set(RedisConstant.LOGIN_CODE_KEY + phone, code);
        log.info("发送验证码成功:{}", code);
        // 5. 返回
        return ResultUtils.success("发送成功");
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
        // 登录限流
        LoginRateLimiter.RateLimitResult rateLimitResult = loginRateLimiter.checkBlock(userAccount);
        boolean blocked = rateLimitResult.blocked();
        long time = Math.max(1, (rateLimitResult.ttlSeconds() + 59) / 60);
        ThrowUtils.throwIf(blocked, ErrorCode.TOO_MANY_REQUESTS, "登录失败次数过多，账号已锁定，请 " + time + " 分钟后再试");
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

    /**
     * 用户注销
     *
     * @param request 请求
     * @return 注销结果
     */
    @PostMapping("/logout")
    @ApiOperation("用户注销")
    public BaseResponse<Boolean> userLogout(HttpServletRequest request) {
        boolean result = userService.userLogout(request);
        return ResultUtils.success(result);
    }

    /**
     * 新增用户
     *
     * @param userAddRequest 用户新增请求
     * @return 新增用户id
     */
    @PostMapping("/add")
    @ApiOperation("添加用户")
    @LogOperation(module = LogConstant.USER_MANAGER, type = LogConstant.ADD_OPERATION)
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> addUser(@RequestBody UserAddRequest userAddRequest) {
        ThrowUtils.throwIf(userAddRequest == null, ErrorCode.PARAMS_ERROR);
        Long userId = userService.addUser(userAddRequest);
        return ResultUtils.success(userId);
    }

    /**
     * 更新用户信息
     *
     * @param userUpdateRequest 用户更新请求
     * @return 更新结果
     */
    @PostMapping("/update")
    @ApiOperation("更新用户信息")
    @LogOperation(module = LogConstant.USER_MANAGER, type = LogConstant.UPDATE_OPERATION)
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateUser(@RequestBody UserUpdateRequest userUpdateRequest) {
        ThrowUtils.throwIf(userUpdateRequest == null, ErrorCode.PARAMS_ERROR);
        Boolean res = userService.updateUser(userUpdateRequest);
        return ResultUtils.success(res);
    }

    /**
     * 删除用户
     *
     * @param userDeleteRequest 用户删除请求
     * @return 删除结果
     */
    @PostMapping("/delete")
    @ApiOperation("删除用户")
    @LogOperation(module = LogConstant.USER_MANAGER, type = LogConstant.DELETE_OPERATION)
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteUser(@RequestBody UserDeleteRequest userDeleteRequest) {
        if (userDeleteRequest.getId() == null || userDeleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户ID不合法");
        }
        boolean result = userService.removeById(userDeleteRequest.getId());
        return ResultUtils.success(result);
    }

    /**
     * 根据id获取用户信息 (仅管理员可查)
     *
     * @param id 用户id
     * @return 用户信息
     */
    @GetMapping("/get")
    @ApiOperation("根据id获取用户信息 (仅管理员可查)")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<User> getUserById(Long id) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        User user = userService.getById(id);
        return ResultUtils.success(user);
    }

    /**
     * 根据id获取用户封装信息
     *
     * @param id 用户id
     * @return 用户封装信息
     */
    @GetMapping("/get/vo")
    @ApiOperation("根据id获取用户VO信息")
    @AuthCheck
    public BaseResponse<UserVO> getUserVOById(Long id) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        BaseResponse<User> response = getUserById(id);
        User user = response.getData();
        return ResultUtils.success(userService.getUserVO(user));
    }

    /**
     * 分页获取用户封装列表
     *
     * @param userQueryRequest 用户查询请求
     * @return 用户封装列表
     */
    @PostMapping("/list/page/vo")
    @ApiOperation("分页获取用户封装列表")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<UserVO>> listUserVOByPage(@RequestBody UserQueryRequest userQueryRequest) {
        ThrowUtils.throwIf(userQueryRequest == null, ErrorCode.PARAMS_ERROR);
        int current = userQueryRequest.getCurrent();
        int pageSize = userQueryRequest.getPageSize();
        Page<User> userPage = userService.page(new Page<>(current, pageSize), userService.getQueryWrapper(userQueryRequest));
        Page<UserVO> userVoPage = new Page<>(current, pageSize, userPage.getTotal());
        List<UserVO> userVOList = userService.getUserVOList(userPage.getRecords());
        userVoPage.setRecords(userVOList);
        return ResultUtils.success(userVoPage);
    }

}
