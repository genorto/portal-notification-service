package com.mipt.notification.telegram;

import com.mipt.notification.client.UserServiceClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@Component
public class NotificationBot extends TelegramLongPollingBot {

    @Value("${telegram.bot.token}")
    private String botToken;

    @Value("${telegram.bot.username}")
    private String botUsername;

    private final UserServiceClient userServiceClient;

    public NotificationBot(UserServiceClient userServiceClient) {
        this.userServiceClient = userServiceClient;
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) return;

        Long chatId = update.getMessage().getChatId();
        String tgUsername = update.getMessage().getFrom().getUserName();

        if (tgUsername == null || tgUsername.isBlank()) return;

        String userId = userServiceClient.getUserIdByTelegramUsername(tgUsername);
        if (userId == null) return;

        Long existingChatId = userServiceClient.getTelegramChatId(userId);
        if (chatId.equals(existingChatId)) return;

        userServiceClient.saveTelegramChatId(userId, chatId);
        sendMessage(chatId, "✅ Уведомления от Portal МФТИ подключены.");
        log.info("Registered chatId={} for userId={} (@{})", chatId, userId, tgUsername);
    }

    public void sendMessage(Long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Failed to send message to chatId: {}", chatId, e);
        }
    }
}
