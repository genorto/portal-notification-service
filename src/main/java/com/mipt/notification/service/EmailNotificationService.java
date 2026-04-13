package com.mipt.notification.service;

import com.mipt.notification.client.UserServiceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailNotificationService {

    private final JavaMailSender mailSender;
    private final UserServiceClient userServiceClient;

    @Value("${spring.mail.username:}")
    private String defaultFrom;

    @Value("${notification.email.subject-prefix:[Portal Notification]}")
    private String subjectPrefix;

    public void sendMessage(String receiverKey, String message) {
        sendMessage(receiverKey, null, message);
    }

    public void sendMessage(String receiverKey, String fallbackEmail, String message) {
        String recipientEmail = resolveEmail(receiverKey, fallbackEmail);
        if (!StringUtils.hasText(recipientEmail)) {
            log.warn("Email получателя не найден для receiverKey={}. Пропускаем отправку.",
                    receiverKey);
            return;
        }

        if (!StringUtils.hasText(defaultFrom)) {
            log.warn("spring.mail.username не настроен. Пропускаем отправку email.");
            return;
        }

        try {
            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setFrom(defaultFrom);
            mail.setTo(recipientEmail);
            mail.setSubject(subjectPrefix + " Новое уведомление");
            mail.setText(message);
            mailSender.send(mail);
            log.info("Email отправлен на {} для receiverKey={}", recipientEmail, receiverKey);
        } catch (Exception e) {
            log.error("Ошибка отправки email на {}: {}", recipientEmail, e.getMessage(), e);
        }
    }

    private String resolveEmail(String receiverKey, String fallbackEmail) {
        if (StringUtils.hasText(receiverKey)) {
            String emailFromBackend = userServiceClient.getEmailByUserId(receiverKey);
            if (StringUtils.hasText(emailFromBackend)) {
                return emailFromBackend;
            }
        }

        if (StringUtils.hasText(fallbackEmail)) {
            return fallbackEmail;
        }

        return null;
    }
}
