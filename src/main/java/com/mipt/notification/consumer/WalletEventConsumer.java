package com.mipt.notification.consumer;

import com.mipt.notification.event.WalletEvent;
import com.mipt.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class WalletEventConsumer {

 private final NotificationService notificationService;

 @KafkaListener(topics = "${kafka.topic.wallet-events:wallet-events}", groupId = "${spring.kafka.consumer.group-id:notification-service}")
 public void consume(WalletEvent event) {
  log.info("Received WALLET event: {} operation: {}", event.getEventType(), event.getOperationId());

  try {
   notificationService.sendWalletNotification(event);
   log.info("Wallet event processed successfully");
  } catch (Exception e) {
   log.error("Error processing wallet event: {}", event.getEventType(), e);
  }
 }
}
