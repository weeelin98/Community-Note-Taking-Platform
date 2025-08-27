package kamanotes.controller;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import kamanotes.model.base.ApiResponse;
import kamanotes.model.dto.user.LoginRequest;
import kamanotes.model.dto.user.RegisterRequest;
import kamanotes.model.dto.user.UpdateUserRequest;
import kamanotes.model.dto.user.UserQueryParam;
import kamanotes.model.entity.User;
import kamanotes.model.vo.user.AvatarVO;
import kamanotes.model.vo.user.LoginUserVO;
import kamanotes.model.vo.user.RegisterVO;
import kamanotes.model.vo.user.UserVO;
import kamanotes.service.UserService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/users")
    public ApiResponse<RegisterVO> register(
            @Valid
            @RequestBody
            RegisterRequest request) {
        return userService.register(request);
    }

    @PostMapping("/users/login")
    public ApiResponse<LoginUserVO> login(
            @Valid
            @RequestBody
            LoginRequest request) {
        return userService.login(request);
    }

    @PostMapping("/users/whoami")
    public ApiResponse<LoginUserVO> whoami() {
        return userService.whoami();
    }

    @GetMapping("/users/{userId}")
    public ApiResponse<UserVO> getUserInfo(
            @PathVariable
            @Pattern(regexp = "\\d+", message = "ID 格式错误")
            Long userId) {
        return userService.getUserInfo(userId);
    }

    @PatchMapping("/users/me")
    public ApiResponse<LoginUserVO> updateUserInfo(
            @Valid
            @RequestBody
            UpdateUserRequest request) {
        return userService.updateUserInfo(request);
    }

    @PostMapping("/users/avatar")
    public ApiResponse<AvatarVO> uploadAvatar(
            @RequestParam("file") MultipartFile file) {
        return userService.uploadAvatar(file);
    }

    @GetMapping("/admin/users")
    public ApiResponse<List<User>> adminGetUser(
            @Valid UserQueryParam queryParam) {
        return userService.getUserList(queryParam);
    }
}


