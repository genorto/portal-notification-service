package com.mipt.notification.consumer;

import com.mipt.notification.event.AdvertisementEvent;
import com.mipt.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdvertisementEventConsumer {

    private final NotificationService notificationService;

    @KafkaListener(
            topics = "${kafka.topic.advertisement-events:advertisement-events}",
            groupId = "${spring.kafka.consumer.group-id:notification-service}"
    )
    public void consume(AdvertisementEvent event) {
        log.info("Received event: {} for advertisement: {}",
                event.getEventType(), event.getAdvertisementId());

        try {
            notificationService.sendNotification(event);
            log.info("Event processed successfully");
        } catch (Exception e) {
            log.error("Error processing event: {}", event.getEventType(), e);
        }
    }
}