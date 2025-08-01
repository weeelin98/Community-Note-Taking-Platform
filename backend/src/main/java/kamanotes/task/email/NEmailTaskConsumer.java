package com.kama.notes.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kama.notes.model.enums.redisKey.RedisKey;
import com.kama.notes.task.email.EmailTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
@Component
public class NEmailTaskConsumer {
    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Value("827333717@qq.com")
    private String from;

    @Scheduled(fixedDelay = 3000)
    public void resume() throws JsonProcessingException {
        String emailQueueKey = RedisKey.emailTaskQueue();

        while (true) {


            String emailTaskJson = redisTemplate.opsForList().rightPop(emailQueueKey);

            if (emailTaskJson == null) {
                break;
            }


            EmailTask emailTask = objectMapper.readValue(emailTaskJson, EmailTask.class);
            String email = emailTask.getEmail();
            String verificationCode = emailTask.getCode();


            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom(from);
            mailMessage.setTo(email);
            mailMessage.setSubject("KamaNote- Verification Code");
            mailMessage.setText("Your Code is:" + verificationCode + "，Expired in" + 5 + "minutes.Thank you.");

            mailSender.send(mailMessage);


            redisTemplate.opsForValue().set(RedisKey.registerVerificationCode(email), verificationCode, 5, TimeUnit.MINUTES);
        }
    }

}
