package kamanotes.service.impl;

import kamanotes.annotation.NeedLogin;
import kamanotes.model.base.ApiResponse;
import kamanotes.model.base.Pagination;
import kamanotes.model.dto.user.LoginRequest;
import kamanotes.model.dto.user.RegisterRequest;
import kamanotes.model.dto.user.UpdateUserRequest;
import kamanotes.model.dto.user.UserQueryParam;
import kamanotes.model.entity.User;
import kamanotes.mapper.UserMapper;
import kamanotes.model.vo.user.AvatarVO;
import kamanotes.model.vo.user.RegisterVO;
import kamanotes.model.vo.user.LoginUserVO;
import kamanotes.model.vo.user.UserVO;
import kamanotes.scope.RequestScopeData;
import kamanotes.service.EmailService;
import kamanotes.service.FileService;
import kamanotes.service.UserService;
import kamanotes.utils.ApiResponseUtil;
import kamanotes.utils.JwtUtil;
import kamanotes.utils.PaginationUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@Log4j2
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private FileService fileService;

    @Autowired
    private RequestScopeData requestScopeData;

    @Autowired
    private EmailService emailService;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse<RegisterVO> register(RegisterRequest request) {
        User existingUser = userMapper.findByAccount(request.getAccount());

        if (existingUser != null) {
            return ApiResponseUtil.error("账号重复");
        }

        if (request.getEmail() != null && !request.getEmail().isEmpty()) {
            existingUser = userMapper.findByEmail(request.getEmail());
            if (existingUser != null) {
                return ApiResponseUtil.error("邮箱已被使用");
            }

            if (request.getVerifyCode() == null || request.getVerifyCode().isEmpty()) {
                return ApiResponseUtil.error("请提供邮箱验证码");
            }

            if (!emailService.checkVerificationCode(request.getEmail(), request.getVerifyCode())) {
                return ApiResponseUtil.error("验证码无效或已过期");
            }
        }

        User user = new User();
        BeanUtils.copyProperties(request, user);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        try {
            userMapper.insert(user);
            String token = jwtUtil.generateToken(user.getUserId());

            RegisterVO registerVO = new RegisterVO();
            BeanUtils.copyProperties(user, registerVO);
            userMapper.updateLastLoginAt(user.getUserId());

            return ApiResponseUtil.success("注册成功", registerVO, token);
        } catch (Exception e) {
            log.error("注册失败", e);
            return ApiResponseUtil.error("注册失败，请稍后再试");
        }
    }

    @Override
    public ApiResponse<LoginUserVO> login(LoginRequest request) {
        User user = null;

        if (request.getAccount() != null && !request.getAccount().isEmpty()) {
            user = userMapper.findByAccount(request.getAccount());
        } else if (request.getEmail() != null && !request.getEmail().isEmpty()) {
            user = userMapper.findByEmail(request.getEmail());
        } else {
            return ApiResponseUtil.error("请提供账号或邮箱");
        }

        if (user == null) {
            return ApiResponseUtil.error("用户不存在");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ApiResponseUtil.error("密码错误");
        }

        String token = jwtUtil.generateToken(user.getUserId());

        LoginUserVO userVO = new LoginUserVO();
        BeanUtils.copyProperties(user, userVO);

        userMapper.updateLastLoginAt(user.getUserId());

        return ApiResponseUtil.success("登录成功", userVO, token);
    }


    @Override
    public ApiResponse<LoginUserVO> whoami() {
        Long userId = requestScopeData.getUserId();

        if (userId == null) {
            return ApiResponseUtil.error("用户 ID 异常");
        }

        try {
            User user = userMapper.findById(userId);
            if (user == null) {
                return ApiResponseUtil.error("用户不存在");
            }

            String newToken = jwtUtil.generateToken(userId);
            if (newToken == null) {
                return ApiResponseUtil.error("系统错误");
            }

            LoginUserVO userVO = new LoginUserVO();
            BeanUtils.copyProperties(user, userVO);

            userMapper.updateLastLoginAt(userId);
            return ApiResponseUtil.success("自动登录成功", userVO, newToken);
        } catch (Exception e) {
            return ApiResponseUtil.error("系统错误");
        }
    }

    @Override
    public ApiResponse<UserVO> getUserInfo(Long userId) {

        User user = userMapper.findById(userId);

        if (user == null) {
            return ApiResponseUtil.error("用户不存在");
        }

        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);

        return ApiResponseUtil.success("获取用户信息成功", userVO);
    }

    @Override
    @Transactional
    @NeedLogin
    public ApiResponse<LoginUserVO> updateUserInfo(UpdateUserRequest request) {
        Long userId = requestScopeData.getUserId();

        User user = new User();
        BeanUtils.copyProperties(request, user);
        user.setUserId(userId);

        try {
            userMapper.update(user);
            return ApiResponseUtil.success("更新成功");
        } catch (Exception e) {
            return ApiResponseUtil.error("更新失败");
        }
    }

    @Override
    public Map<Long, User> getUserMapByIds(List<Long> authorIds) {
        if (authorIds.isEmpty()) return Collections.emptyMap();
        List<User> users = userMapper.findByIdBatch(authorIds);
        return users.stream()
                .collect(Collectors.toMap(User::getUserId, user -> user));
    }

    @Override
    public ApiResponse<List<User>> getUserList(UserQueryParam userQueryParam) {
        int total = userMapper.countByQueryParam(userQueryParam);
        int offset = PaginationUtils.calculateOffset(userQueryParam.getPage(), userQueryParam.getPageSize());
        Pagination pagination = new Pagination(userQueryParam.getPage(), userQueryParam.getPageSize(), total);

        try {
            List<User> users = userMapper.findByQueryParam(userQueryParam, userQueryParam.getPageSize(), offset);
            return ApiResponseUtil.success("获取用户列表成功", users, pagination);
        } catch (Exception e) {
            return ApiResponseUtil.error(e.getMessage());
        }
    }

    @Override
    public ApiResponse<AvatarVO> uploadAvatar(MultipartFile file) {
        try {
            String url = fileService.uploadImage(file);
            AvatarVO avatarVO = new AvatarVO();
            avatarVO.setUrl(url);
            return ApiResponseUtil.success("上传成功", avatarVO);
        } catch (Exception e) {
            return ApiResponseUtil.error(e.getMessage());
        }
    }
}


