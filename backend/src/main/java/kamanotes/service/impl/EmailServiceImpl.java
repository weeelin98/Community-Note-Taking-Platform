package kamanotes.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import kamanotes.model.enums.redisKey.RedisKey;
import kamanotes.service.EmailService;
import kamanotes.task.email.EmailTask;
import kamanotes.utils.RandomCodeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
public class EmailServiceImpl implements EmailService {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Value("${mail.verify-code.limit-expire-seconds}")
    private int limitExpireSeconds;

    @Override
    public String sendVerificationCode(String email) {

        if (isVerificationCodeRateLimited(email)) {
            throw new RuntimeException("Too many requests. Please try again in 60 seconds.");
        }


        String verificationCode = RandomCodeUtil.generateNumberCode(6);


        try {


            EmailTask emailTask = new EmailTask();


            emailTask.setEmail(email);
            emailTask.setCode(verificationCode);
            emailTask.setTimestamp(System.currentTimeMillis());


            String emailTaskJson = objectMapper.writeValueAsString(emailTask);
            String queueKey = RedisKey.emailTaskQueue();
            redisTemplate.opsForList().leftPush(queueKey, emailTaskJson);


            String emailLimitKey = RedisKey.registerVerificationLimitCode(email);
            redisTemplate.opsForValue().set(emailLimitKey, "1", limitExpireSeconds, TimeUnit.SECONDS);

            return verificationCode;
        } catch (Exception e) {
            log.error("Failed to send verification code", e);
            throw new RuntimeException("Failed to send verification code. Please try again later.");
        }
    }

    @Override
    public boolean checkVerificationCode(String email, String code) {
        String redisKey = RedisKey.registerVerificationCode(email);
        String verificationCode = redisTemplate.opsForValue().get(redisKey);

        if (verificationCode != null && verificationCode.equals(code)) {
            redisTemplate.delete(redisKey);
            return true;
        }
        return false;
    }

    @Override
    public boolean isVerificationCodeRateLimited(String email) {
        String redisKey = RedisKey.registerVerificationLimitCode(email);
        return redisTemplate.opsForValue().get(redisKey) != null;
    }
}
