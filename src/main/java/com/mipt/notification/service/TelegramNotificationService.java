package com.mipt.notification.service;

import com.mipt.notification.telegram.NotificationBot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Slf4j
@Service
public class TelegramNotificationService {

 private final NotificationBot notificationBot;
 private final UserRegistrationService registrationService;

  @Value("${telegram.bot.token:}")
  private String botToken;

  @Value("${telegram.bot.default-chat-id:}")
  private String defaultChatId;

  @Value("${telegram.bot.chat-id-map:}")
  private String chatIdMap;

 public TelegramNotificationService(NotificationBot notificationBot,
         UserRegistrationService registrationService) {
  this.notificationBot = notificationBot;
  this.registrationService = registrationService;
 }

 /**
  * Отправка сообщения пользователю
  * @param receiverKey идентификатор получателя (userId из системы)
  * @param message текст сообщения
  */
 public void sendMessage(String receiverKey, String message) {
  if (!StringUtils.hasText(botToken)) {
   log.warn("Telegram bot token is not configured. Skip sending message.");
   return;
  }

  Long chatId = resolveChatId(receiverKey);
  if (chatId == null) {
   log.warn("Telegram chat id is not resolved for receiver: {}. Skip sending.", receiverKey);
   return;
  }

  notificationBot.sendMessage(chatId, message);
  log.info("Telegram message sent to chatId={} for receiver={}", chatId, receiverKey);
 }

 /**
  * Определение chatId по receiverKey
  * Приоритет:
  * 1. Зарегистрированные пользователи (userId из системы)
  * 2. Маппинг из конфигурации (chat-id-map)
  * 3. Default chat id из конфигурации
  */
 private Long resolveChatId(String receiverKey) {
  // 1. Проверяем зарегистрированных пользователей
  if (StringUtils.hasText(receiverKey)) {
   Long registeredChatId = registrationService.getChatIdByUserId(receiverKey);
   if (registeredChatId != null) {
    log.debug("Found registered chatId for user: {}", receiverKey);
    return registeredChatId;
   }
  }

  // 2. Проверяем маппинг из конфигурации (для обратной совместимости)
  if (StringUtils.hasText(chatIdMap) && StringUtils.hasText(receiverKey)) {
   String[] pairs = chatIdMap.split(",");
   for (String pair : pairs) {
    String[] keyValue = pair.split(":", 2);
    if (keyValue.length == 2 && receiverKey.equals(keyValue[0].trim())) {
     try {
      Long chatId = Long.parseLong(keyValue[1].trim());
      log.debug("Found mapped chatId for receiver: {}", receiverKey);
      return chatId;
     } catch (NumberFormatException e) {
      log.warn("Invalid chatId format in mapping: {}", keyValue[1]);
     }
    }

  // 3. Используем default chat id (для тестирования)
  if (StringUtils.hasText(defaultChatId)) {
   try {
    Long chatId = Long.parseLong(defaultChatId);
    log.debug("Using default chatId for receiver: {}", receiverKey);
    return chatId;
   } catch (NumberFormatException e) {
    log.warn("Invalid default chatId format: {}", defaultChatId);
   }
  }

  log.warn("No chatId resolved for receiver: {}", receiverKey);
  return null;
 }
}
