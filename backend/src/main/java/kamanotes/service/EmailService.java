package kamanotes.service;

public interface EmailService {
    String sendVerificationCode(String email);
    boolean checkVerificationCode(String email, String code);
    boolean isVerificationCodeRateLimited(String email);
}


