package kamanotes.controller;

import kamanotes.model.base.ApiResponse;
import kamanotes.service.EmailService;
import kamanotes.utils.ApiResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@RestController
@RequestMapping("/api/email")
public class EmailController {
    @Autowired
    private EmailService emailService;

    @GetMapping("/verify-code")
    public ApiResponse<Void> sendVerifyCode(@RequestParam @NotBlank @Email String email) {
        try {
            emailService.sendVerificationCode(email);
            return ApiResponseUtil.success(null);
        } catch (Exception e) {
            return ApiResponseUtil.error(e.getMessage());
        }
    }
}
