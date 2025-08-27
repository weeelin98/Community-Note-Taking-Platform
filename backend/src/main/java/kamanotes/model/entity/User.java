package kamanotes.model.entity;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private Long userId;
    private String account;
    private String username;
    private String password;
    private Integer gender;
    private LocalDate birthday;
    private String avatarUrl;
    private String email;
    private String school;
    private String signature;
    private Integer isBanned;
    private Integer isAdmin;
    private LocalDateTime lastLoginAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}


