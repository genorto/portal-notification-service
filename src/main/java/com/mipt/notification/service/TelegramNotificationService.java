package com.mipt.notification.service;

import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

@Slf4j
@Service
public class TelegramNotificationService {

 private final RestClient restClient;

 public TelegramNotificationService() {
  this(RestClient.create());
 }

 TelegramNotificationService(RestClient restClient) {
  this.restClient = restClient;
 }

 @Value("${telegram.bot.token:}")
 private String botToken;

 @Value("${telegram.bot.default-chat-id:}")
 private String defaultChatId;

 @Value("${telegram.bot.chat-id-map:}")
 private String chatIdMap;

 public void sendMessage(String receiverKey, String message) {
  if (!StringUtils.hasText(botToken)) {
   log.warn("Telegram bot token is not configured. Skip sending message.");
   return;
  }

  String chatId = resolveChatId(receiverKey);
  if (!StringUtils.hasText(chatId)) {
   log.warn("Telegram chat id is not configured for receiver: {}. Skip sending.", receiverKey);
   return;
  }

  try {
   restClient.post()
           .uri("https://api.telegram.org/bot{token}/sendMessage", botToken)
           .contentType(MediaType.APPLICATION_JSON)
           .body(Map.of(
                   "chat_id", chatId,
                   "text", message))
           .retrieve()
           .toBodilessEntity();

   log.info("Telegram message sent to chatId={}", chatId);
  } catch (Exception ex) {
   log.error("Failed to send Telegram message for receiver={}.", receiverKey, ex);
  }
 }

 private String resolveChatId(String receiverKey) {
  if (StringUtils.hasText(chatIdMap) && StringUtils.hasText(receiverKey)) {
   String[] pairs = chatIdMap.split(",");
   for (String pair : pairs) {
    String[] keyValue = pair.split(":", 2);
    if (keyValue.length == 2 && receiverKey.equals(keyValue[0].trim())) {
     return keyValue[1].trim();
    }
   }
  }

  return defaultChatId;
 }
}