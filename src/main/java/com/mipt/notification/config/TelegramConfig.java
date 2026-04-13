package com.mipt.notification.config;


import com.mipt.notification.telegram.NotificationBot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Slf4j
@Configuration
public class TelegramConfig {

    @Bean
    @ConditionalOnProperty(name = "telegram.bot.enabled", havingValue = "true",
            matchIfMissing = true)
    public TelegramBotsApi telegramBotsApi(NotificationBot notificationBot) {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(notificationBot);
            log.info("✅ Telegram bot registered successfully");
            return botsApi;
        } catch (TelegramApiException e) {
            log.error("❌ Failed to register Telegram bot", e);
            throw new RuntimeException("Failed to register Telegram bot", e);
        }
    }
}