package kamanotes.service;

public interface NEmailService
{
    /**
     * Send Verification email
     *
     * @@param email target email
     * @retun codes  would be send
     * @throws RuntimeException
     */

    String sendVerificationCode(String email);

    /**
     * Verify the codes
     *
     * @param  email email address
     * @param code the verification code
     * @return true means verified
     */

    boolean checkVerificationCode(String email, String code);

    /**
     * if the email at flow limit state
     *
     * @param email email address
     * @return true means limited, can't send more codes
     */

    boolean isVerificationCodeRateLimited(String email);
}
