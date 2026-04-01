package com.mipt.notification.consumer;

import com.mipt.notification.event.SearchHistoryEvent;
import com.mipt.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SearchHistoryEventConsumer {

 private final NotificationService notificationService;

 @KafkaListener(topics = "${kafka.topic.search-history-events:search-history-events}", groupId = "${spring.kafka.consumer.group-id:notification-service}")
 public void consume(SearchHistoryEvent event) {
  log.info("Received SEARCH_HISTORY event for user: {}",
    event.getUserId());

  try {
   notificationService.sendSearchHistoryNotification(event);
   log.info("Search history event processed successfully");
  } catch (Exception e) {
   log.error("Error processing search history event", e);
  }
 }
}
