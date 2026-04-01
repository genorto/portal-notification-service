package com.mipt.notification.service;

import com.mipt.notification.event.AdvertisementEvent;
import com.mipt.notification.event.ChatEvent;
import com.mipt.notification.event.FavoriteEvent;
import com.mipt.notification.event.MainPageEvent;
import com.mipt.notification.event.SearchHistoryEvent;
import com.mipt.notification.event.UserEvent;
import com.mipt.notification.event.WalletEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

        // Позже добавим реальные сервисы
        // private final TelegramBotService telegramService;
        // private final EmailService emailService;

        public void sendNotification(AdvertisementEvent event) {
                log.info("Processing notification for event: {}", event.getEventType());
                String message = formatAdvertisementMessage(event);
                sendToChannels(event.getAuthorId().toString(), message);
                log.info("Advertisement notification sent: {}", message);
        }

        public void sendUserNotification(UserEvent event) {
                log.info("Processing user notification for event: {}", event.getEventType());
                String message = formatUserMessage(event);
                sendToChannels(event.getUserId().toString(), message);
                log.info("User notification sent: {}", message);
        }

        public void sendChatNotification(ChatEvent event) {
                log.info("Processing chat notification for event: {}", event.getEventType());
                String message = formatChatMessage(event);
                sendToChannels(event.getReceiverId().toString(), message);
                log.info("Chat notification sent: {}", message);
        }

        public void sendFavoriteNotification(FavoriteEvent event) {
                log.info("Processing favorite notification for event: {}", event.getEventType());
                String message = formatFavoriteMessage(event);
                sendToChannels(event.getUserId().toString(), message);
                log.info("Favorite notification sent: {}", message);
        }

        public void sendSearchHistoryNotification(SearchHistoryEvent event) {
                log.info("Processing search history notification");
                String message = formatSearchHistoryMessage(event);
                sendToChannels(event.getUserId().toString(), message);
                log.info("Search history notification sent: {}", message);
        }

        public void sendWalletNotification(WalletEvent event) {
                log.info("Processing wallet notification for event: {}", event.getEventType());
                String message = formatWalletMessage(event);
                String receiver = event.getWalletOwnerId() != null
                                ? event.getWalletOwnerId().toString()
                                : "wallet";
                sendToChannels(receiver, message);
                log.info("Wallet notification sent: {}", message);
        }

        public void sendMainPageNotification(MainPageEvent event) {
                log.info("Processing mainpage notification for event: {}", event.getEventType());
                String message = formatMainPageMessage(event);
                if (event.getUserId() != null) {
                        sendToChannels(event.getUserId().toString(), message);
                }
                log.info("MainPage notification sent: {}", message);
        }

        private void sendToChannels(String userId, String message) {
                // Отправка в Telegram
                // telegramService.sendMessage(userId, message);

                // Отправка на email
                // emailService.sendEmail(getUserEmail(userId), "Уведомление", message);
        }

        private String formatAdvertisementMessage(AdvertisementEvent event) {
                return switch (event.getEventType()) {
                        case "ADVERTISEMENT_CREATED" ->
                                String.format("✅ Объявление \"%s\" создано в статусе черновика",
                                                event.getAdvertisementName());

                        case "ADVERTISEMENT_PUBLISHED" ->
                                String.format("🚀 Объявление \"%s\" опубликовано! Цена: %d руб.",
                                                event.getAdvertisementName(), event.getPrice());

                        case "ADVERTISEMENT_PRICE_CHANGED" ->
                                String.format("💰 В объявлении \"%s\" изменена цена на %d руб. %s",
                                                event.getAdvertisementName(), event.getPrice(), event.getDetails());

                        case "ADVERTISEMENT_UPDATED" ->
                                String.format("✏️ Объявление \"%s\" обновлено",
                                                event.getAdvertisementName());

                        case "ADVERTISEMENT_PAUSED" ->
                                String.format("⏸️ Объявление \"%s\" приостановлено",
                                                event.getAdvertisementName());

                        case "ADVERTISEMENT_DELETED" ->
                                String.format("🗑️ Объявление \"%s\" удалено",
                                                event.getAdvertisementName());

                        case "FAVORITE_TOGGLED" ->
                                String.format("⭐ Объявление \"%s\" добавлено в избранное",
                                                event.getAdvertisementName());

                        default ->
                                String.format("ℹ️ Событие: %s для объявления \"%s\"",
                                                event.getEventType(), event.getAdvertisementName());
                };
        }

        private String formatUserMessage(UserEvent event) {
                return switch (event.getEventType()) {
                        case "USER_REGISTERED" ->
                                String.format("🎉 Добро пожаловать, %s! Вы успешно зарегистрировались",
                                                event.getUsername());

                        case "USER_UPDATED" ->
                                String.format("✏️ Профиль пользователя %s обновлен",
                                                event.getUsername());

                        case "USER_DELETED" ->
                                String.format("😞 Аккаунт %s удален",
                                                event.getUsername());

                        case "USER_STATUS_CHANGED" ->
                                String.format("ℹ️ Статус пользователя %s изменен: %s",
                                                event.getUsername(), event.getDetails());

                        default ->
                                String.format("ℹ️ Событие пользователя: %s",
                                                event.getEventType());
                };
        }

        private String formatChatMessage(ChatEvent event) {
                return switch (event.getEventType()) {
                        case "MESSAGE_RECEIVED" ->
                                String.format("💬 Новое сообщение: %s",
                                                event.getMessageText());

                        case "CHAT_CREATED" ->
                                "💬 Чат создан";

                        case "CHAT_ARCHIVED" ->
                                "📦 Чат архивирован";

                        default ->
                                String.format("ℹ️ Событие чата: %s",
                                                event.getEventType());
                };
        }

        private String formatFavoriteMessage(FavoriteEvent event) {
                if (Boolean.TRUE.equals(event.getAdded())) {
                        return String.format("⭐ Объявление \"%s\" добавлено в избранное",
                                        event.getAdvertisementName());
                } else {
                        return String.format("☆ Объявление \"%s\" удалено из избранного",
                                        event.getAdvertisementName());
                }
        }

        private String formatSearchHistoryMessage(SearchHistoryEvent event) {
                return String.format("🔍 Новый поиск: \"%s\" в категории %s",
                                event.getSearchQuery(), event.getCategory());
        }

        private String formatWalletMessage(WalletEvent event) {
                return switch (event.getEventType()) {
                        case "WALLET_CREATED" ->
                                String.format("💳 Кошелек создан для пользователя %s", event.getWalletOwnerId());

                        case "WALLET_OPERATION_CREATED" ->
                                String.format("💸 Операция %s на сумму %d (%s)",
                                                event.getOperationType(), event.getAmount(), event.getTitle());

                        default ->
                                String.format("ℹ️ Событие кошелька: %s", event.getEventType());
                };
        }

        private String formatMainPageMessage(MainPageEvent event) {
                return switch (event.getEventType()) {
                        case "MAINPAGE_FAVORITE_ADDED" ->
                                String.format("⭐ Объявление %s добавлено в избранное", event.getAdvertisementId());

                        case "MAINPAGE_FAVORITE_REMOVED" ->
                                String.format("☆ Объявление %s удалено из избранного", event.getAdvertisementId());

                        default ->
                                String.format("ℹ️ Событие mainpage: %s", event.getEventType());
                };
        }
}