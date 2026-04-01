package com.mipt.notification.consumer;

import com.mipt.notification.event.ChatEvent;
import com.mipt.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatEventConsumer {

 private final NotificationService notificationService;

 @KafkaListener(topics = "${kafka.topic.chat-events:chat-events}", groupId = "${spring.kafka.consumer.group-id:notification-service}")
 public void consume(ChatEvent event) {
  log.info("Received CHAT event: {} for chat: {}",
    event.getEventType(), event.getChatId());

  try {
   notificationService.sendChatNotification(event);
   log.info("Chat event processed successfully");
  } catch (Exception e) {
   log.error("Error processing chat event: {}", event.getEventType(), e);
  }
 }
}
