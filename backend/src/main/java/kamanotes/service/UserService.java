package kamanotes.service;

import kamanotes.model.base.ApiResponse;
import kamanotes.model.dto.user.LoginRequest;
import kamanotes.model.dto.user.RegisterRequest;
import kamanotes.model.dto.user.UpdateUserRequest;
import kamanotes.model.dto.user.UserQueryParam;
import kamanotes.model.entity.User;
import kamanotes.model.vo.user.AvatarVO;
import kamanotes.model.vo.user.RegisterVO;
import kamanotes.model.vo.user.LoginUserVO;
import kamanotes.model.vo.user.UserVO;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Transactional
public interface UserService {
    ApiResponse<RegisterVO> register(RegisterRequest request);
    ApiResponse<LoginUserVO> login(LoginRequest request);
    ApiResponse<LoginUserVO> whoami();
    ApiResponse<UserVO> getUserInfo(Long userId);
    ApiResponse<LoginUserVO> updateUserInfo(UpdateUserRequest request);
    Map<Long, User> getUserMapByIds(List<Long> authorIds);
    ApiResponse<List<User>> getUserList(UserQueryParam userQueryParam);
    ApiResponse<AvatarVO> uploadAvatar(MultipartFile file);
}


