package com.mipt.notification.consumer;

import com.mipt.notification.event.UserEvent;
import com.mipt.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserEventConsumer {

 private final NotificationService notificationService;

 @KafkaListener(topics = "${kafka.topic.user-events:user-events}", groupId = "${spring.kafka.consumer.group-id:notification-service}")
 public void consume(UserEvent event) {
  log.info("Received USER event: {} for user: {}",
    event.getEventType(), event.getUserId());

  try {
   notificationService.sendUserNotification(event);
   log.info("User event processed successfully");
  } catch (Exception e) {
   log.error("Error processing user event: {}", event.getEventType(), e);
  }
 }
}
