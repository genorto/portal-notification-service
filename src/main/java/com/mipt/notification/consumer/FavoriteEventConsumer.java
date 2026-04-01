package com.mipt.notification.consumer;

import com.mipt.notification.event.FavoriteEvent;
import com.mipt.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class FavoriteEventConsumer {

 private final NotificationService notificationService;

 @KafkaListener(topics = "${kafka.topic.favorite-events:favorite-events}", groupId = "${spring.kafka.consumer.group-id:notification-service}")
 public void consume(FavoriteEvent event) {
  log.info("Received FAVORITE event: {} for advertisement: {}",
    event.getEventType(), event.getAdvertisementId());

  try {
   notificationService.sendFavoriteNotification(event);
   log.info("Favorite event processed successfully");
  } catch (Exception e) {
   log.error("Error processing favorite event: {}", event.getEventType(), e);
  }
 }
}
