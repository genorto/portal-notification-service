package com.mipt.notification.service;

import com.mipt.notification.client.UserServiceClient;
import com.mipt.notification.telegram.NotificationBot;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class TelegramNotificationService {

    private final NotificationBot notificationBot;
    private final UserServiceClient userServiceClient;

    @Value("${telegram.bot.token:}")
    private String botToken;

    public void sendMessage(String userId, String message) {
        if (!StringUtils.hasText(botToken)) {
            log.debug("Telegram bot token not configured, skipping");
            return;
        }
        if (!StringUtils.hasText(userId)) return;

        // Берём chatId из БД — не зависит от перезапусков сервиса
        Long chatId = userServiceClient.getTelegramChatId(userId);
        if (chatId == null) {
            log.debug("No telegram chatId for userId={}, skipping", userId);
            return;
        }

        notificationBot.sendMessage(chatId, message);
        log.info("Telegram message sent to chatId={} (userId={})", chatId, userId);
    }
}