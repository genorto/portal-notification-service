package com.mipt.notification.consumer;

import com.mipt.notification.event.MainPageEvent;
import com.mipt.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MainPageEventConsumer {

 private final NotificationService notificationService;

 @KafkaListener(topics = "${kafka.topic.mainpage-events:mainpage-events}", groupId = "${spring.kafka.consumer.group-id:notification-service}")
 public void consume(MainPageEvent event) {
  log.info("Received MAINPAGE event: {} for user: {}", event.getEventType(), event.getUserId());

  try {
   notificationService.sendMainPageNotification(event);
   log.info("MainPage event processed successfully");
  } catch (Exception e) {
   log.error("Error processing mainpage event: {}", event.getEventType(), e);
  }
 }
}
