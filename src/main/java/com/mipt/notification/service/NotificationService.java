package com.mipt.notification.service;

import com.mipt.notification.event.AdvertisementEvent;
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

        String message = formatMessage(event);

        // Отправка в Telegram
        // telegramService.sendMessage(event.getAuthorId().toString(), message);

        // Отправка на email
        // emailService.sendEmail(getUserEmail(event.getAuthorId()), "Уведомление", message);

        log.info("Notification sent: {}", message);
    }

    private String formatMessage(AdvertisementEvent event) {
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
}