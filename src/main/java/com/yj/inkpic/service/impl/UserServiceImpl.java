package com.yj.inkpic.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yj.inkpic.common.ErrorCode;
import com.yj.inkpic.constant.JwtClaimsConstant;
import com.yj.inkpic.constant.RedisConstant;
import com.yj.inkpic.constant.UserConstant;
import com.yj.inkpic.excption.BusinessException;
import com.yj.inkpic.model.dto.user.UserAddRequest;
import com.yj.inkpic.model.dto.user.UserJwtDTO;
import com.yj.inkpic.model.dto.user.UserQueryRequest;
import com.yj.inkpic.model.dto.user.UserUpdateRequest;
import com.yj.inkpic.model.entity.User;
import com.yj.inkpic.model.enums.UserRoleEnum;
import com.yj.inkpic.model.vo.LoginUserVO;
import com.yj.inkpic.model.vo.UserVO;
import com.yj.inkpic.properties.JwtProperties;
import com.yj.inkpic.service.UserService;
import com.yj.inkpic.mapper.UserMapper;
import com.yj.inkpic.utils.BaseContext;
import com.yj.inkpic.utils.EncryptPassword;
import com.yj.inkpic.utils.JwtUtil;
import com.yj.inkpic.utils.LoginRateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.yj.inkpic.constant.UserConstant.DEFAULT_PASSWORD;

/**
* @author OuYJ
* @description 针对表【user(用户)】的数据库操作Service实现
* @createDate 2026-08-14 17:14:53
*/
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

    @Resource
    private JwtProperties jwtProperties;

    @Resource
    private LoginRateLimiter loginRateLimiter;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 用户注册
     * @param userAccount 用户账号
     * @param userPassword 用户密码
     * @param checkPassword 确认密码
     * @return 新用户id
     */
    @Override
    public Long userRegister(String userAccount, String userPassword, String checkPassword) {
        // 1. 效验参数
        if (StrUtil.hasBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号过短");
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过短");
        }
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次密码不一致");
        }
        // 2. 检查账号是否重复
        QueryWrapper<User> qw = new QueryWrapper<>();
        qw.eq("userAccount", userAccount);
        long count = this.baseMapper.selectCount(qw);
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号已存在");
        }
        // 3. 加密密码
        String encryptPassword = EncryptPassword.getEncryptPassword(userPassword);
        // 4. 插入数据
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        user.setUserName("无名氏");
        user.setUserRole(UserRoleEnum.USER.getValue());
        boolean saveResult = this.save(user);
        if (!saveResult) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败，数据库错误");
        }
        if (user.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户为空");
        }
        // 5. 返回结果
        return user.getId();
    }

    /**
     * 用户登录
     * @param userAccount 用户账号
     * @param userPassword 用户密码
     * @return 登录用户信息（脱敏)
     */
    @Override
    public LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 1.效验参数
        if (StrUtil.hasBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        // todo 后期可实现验证码校验
        // 2.检查账号是否存在
        QueryWrapper<User> qw = new QueryWrapper<>();
        qw.eq("userAccount", userAccount);
        User user = this.baseMapper.selectOne(qw);
        if (user == null) {
            log.info("user login failed, userAccount not exist");
            loginFailed(userAccount);
        }
        // 3.校验密码（BCrypt 每次哈希不同，需用 matches 比对）
        if (!EncryptPassword.matches(userPassword, user.getUserPassword())) {
            log.info("user login failed, password not match");
            loginFailed(userAccount);
        }
        UserJwtDTO userJwtDTO = BeanUtil.copyProperties(user, UserJwtDTO.class);
        Map<String, Object> userMap = BeanUtil.beanToMap(userJwtDTO, new HashMap<>(),
                CopyOptions.create()
                        .setIgnoreNullValue(true)
                        .setFieldValueEditor((filedName,filedValue) -> filedValue.toString()));
        // 3. 生成 jwt 令牌（作为不可猜测的凭证，payload 只放 userId，用户信息以 Redis 为准）
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.USER_ID, user.getId());

        // 令牌生成
        String token = JwtUtil.createJWT(
                jwtProperties.getUserSecretKey(),
                jwtProperties.getUserTtl(),
                claims
        );
        // 4. 将 token 存入 Redis
        String tokenKey = RedisConstant.LOGIN_TOKEN_KEY + token;
        stringRedisTemplate.opsForHash().putAll(tokenKey, userMap);
        stringRedisTemplate.expire(tokenKey, jwtProperties.getUserTtl(), TimeUnit.MILLISECONDS);
        // 5. 返回脱敏信息
        LoginUserVO loginUserVO = new LoginUserVO();
        BeanUtils.copyProperties(user, loginUserVO);
        // 设置 token
        loginUserVO.setToken(token);
        return loginUserVO;
    }

    /**
     * 登录失败处理
     * @param userAccount 用户账号
     */
    private void loginFailed(String userAccount) {
        loginRateLimiter.recordAttempt(userAccount);
        LoginRateLimiter.RateLimitResult result = loginRateLimiter.checkBlock(userAccount);
        throw new BusinessException(ErrorCode.PARAMS_ERROR, buildLoginFailMsg(result));
    }

    /**
     * 构建登录失败提示信息
     * @param result 登录限制结果
     * @return 提示信息
     */
    private String buildLoginFailMsg(LoginRateLimiter.RateLimitResult result) {
        long remaining = result.remainingAttempts();
        if (remaining > 0) {
            return "用户不存在或密码错误, 还剩 " + remaining + " 次机会";
        }
        long ttlMin = Math.max(1, (result.ttlSeconds() + 59) / 60);
        return "登录失败次数过多，账号已锁定，请 " + ttlMin + " 分钟后再试";
    }

    /**
     * 获取当前登录用户
     * @return 当前登录用户信息
     */
    @Override
    public User getLoginUser() {
        // 先判断是否已经登录
        UserJwtDTO currentUser = BaseContext.getCurrentUser();
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "未登录");
        }
        // 从数据库查询
        Long userId = currentUser.getId();
        User user = this.getById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "未登录");
        }
        return user;
    }

    /**
     * 获取脱敏的已登录用户信息
     * @param user 用户信息
     * @return 脱敏的用户信息
     */
    @Override
    public LoginUserVO getLoginUserVO(User user) {
        if (user == null) {
            return null;
        }
        LoginUserVO loginUserVO = new LoginUserVO();
        BeanUtils.copyProperties(user, loginUserVO);
        return loginUserVO;
    }

    /**
     * 用户注销
     *
     * @param request 请求（用于获取 token）
     * @return true 注销成功，false 注销失败
     */
    @Override
    public boolean userLogout(HttpServletRequest request) {
        // 1.获取请求头的 token
        String token = request.getHeader("token");
        if (StrUtil.isBlank(token)) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "未登录");
        }
        // 2.删除 Redis 中的 token，使其立即失效
        stringRedisTemplate.delete(RedisConstant.LOGIN_TOKEN_KEY + token);
        // 3.清理 ThreadLocal
        BaseContext.removeCurrentUser();
        return true;
    }

    /**
     * 新增用户
     *
     * @param userAddRequest 用户新增请求
     * @return 用户id
     */
    @Override
    public Long addUser(UserAddRequest userAddRequest) {
        // 1. 判断参数是否为空
        if (userAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        // 2. 判断账号是否重复
        String userAccount = userAddRequest.getUserAccount();
        if (StrUtil.hasBlank(userAccount)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号不能为空");
        }
        QueryWrapper<User> qw = new QueryWrapper<>();
        qw.eq("userAccount", userAccount);
        long count = this.baseMapper.selectCount(qw);
        if (count > 0) {
            throw new BusinessException(ErrorCode.DATA_ALREADY_EXIST, "用户账号已存在");
        }
        User user = new User();
        BeanUtils.copyProperties(userAddRequest, user);
        // 3. 填充密码
        String encryptPassword = EncryptPassword.getEncryptPassword(DEFAULT_PASSWORD);
        user.setUserPassword(encryptPassword);
        // 4. 插入数据库
        boolean save = this.save(user);
        if (!save) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "新增用户失败");
        }
        return user.getId();
    }

    /**
     * 更新用户信息
     *
     * @param userUpdateRequest 用户更新请求
     * @return 更新结果
     */
    @Override
    public Boolean updateUser(UserUpdateRequest userUpdateRequest) {
        // 1. 判断参数是否为空
        if (userUpdateRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        // 2. 获取当前登录用户，校验操作权限
        User loginUser = getLoginUser();
        boolean isAdmin = isAdmin(loginUser);

        Long targetId = userUpdateRequest.getId();
        // 普通用户只能修改自己的资料；管理员可修改任意用户
        if (targetId != null && !loginUser.getId().equals(targetId) && !isAdmin) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权修改其他用户信息");
        }
        // 若未指定 id，默认修改当前登录用户
        if (targetId == null) {
            userUpdateRequest.setId(loginUser.getId());
        }

        // 3. 构建更新对象，禁止普通用户修改角色（防提权）
        User user = new User();
        BeanUtils.copyProperties(userUpdateRequest, user);
        if (!isAdmin) {
            user.setUserRole(UserConstant.DEFAULT_ROLE);
        }
        boolean update = this.updateById(user);
        if (!update) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新用户失败");
        }
        return true;
    }

    /**
     * 获取用户VO信息
     *
     * @param user 用户信息
     * @return 脱敏的用户信息
     */
    @Override
    public UserVO getUserVO(User user) {
        if (user == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为null");
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }

    /**
     * 获取用户VO信息列表
     *
     * @param userList 用户信息列表
     * @return 脱敏的用户信息列表
     */
    @Override
    public List<UserVO> getUserVOList(List<User> userList) {
        if (CollUtil.isEmpty(userList)) {
            return new ArrayList<>();
        }
        return userList.stream().map(this::getUserVO).collect(Collectors.toList());
    }


    /**
     * 允许排序的字段白名单
     */
    private static final Map<String, String> SORT_FIELD_WHITELIST = new HashMap<>() {{
        put("id", "id");
        put("createTime", "createTime");
        put("updateTime", "updateTime");
        put("userRole", "userRole");
    }};

    /**
     * 获取查询条件
     *
     * @param userQueryRequest 查询条件请求
     * @return 查询条件
     */
    @Override
    public QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest) {
        if (userQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        Long id = userQueryRequest.getId();
        String userAccount = userQueryRequest.getUserAccount();
        String userName = userQueryRequest.getUserName();
        String userProfile = userQueryRequest.getUserProfile();
        String userRole = userQueryRequest.getUserRole();
        String sortField = userQueryRequest.getSortField();
        String sortOrder = userQueryRequest.getSortOrder();

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(ObjUtil.isNotNull(id), "id", id);
        queryWrapper.eq(StrUtil.isNotBlank(userRole), "userRole", userRole);
        queryWrapper.like(StrUtil.isNotBlank(userAccount), "userAccount", userAccount);
        queryWrapper.like(StrUtil.isNotBlank(userName), "userName", userName);
        queryWrapper.like(StrUtil.isNotBlank(userProfile), "userProfile", userProfile);
        // 排序字段必须在白名单内，否则忽略，杜绝 SQL 注入
        String safeSortColumn = SORT_FIELD_WHITELIST.get(sortField);
        if (StrUtil.isNotEmpty(safeSortColumn)) {
            queryWrapper.orderBy(true, "ascend".equals(sortOrder), safeSortColumn);
        }
        return queryWrapper;
    }

    /**
     *  判断用户是否为管理员
     * @param user 当前登录的 user
     * @return true 是管理员，false 不是管理员
     */
    @Override
    public boolean isAdmin(User user) {
        return user != null && UserRoleEnum.ADMIN.getValue().equals(user.getUserRole());
    }

}




