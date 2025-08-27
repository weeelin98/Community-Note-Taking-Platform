package kamanotes.mapper;

import kamanotes.model.dto.user.UserQueryParam;
import kamanotes.model.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper {
    int insert(User user);
    User findById(@Param("userId") Long userId);
    List<User> findByIdBatch(@Param("userIds") List<Long> userIds);
    User findByAccount(@Param("account") String account);
    List<User> findByQueryParam(@Param("queryParams") UserQueryParam queryParams,
                                @Param("limit") Integer limit,
                                @Param("offset") Integer offset);
    int countByQueryParam(@Param("queryParams") UserQueryParam queryParams);
    int update(User user);
    int updateLastLoginAt(@Param("userId") Long userId);
    int getTodayLoginCount();
    int getTodayRegisterCount();
    int getTotalRegisterCount();
    User findByEmail(@Param("email") String email);
    List<User> searchUsers(@Param("keyword") String keyword,
                          @Param("limit") int limit,
                          @Param("offset") int offset);
}


