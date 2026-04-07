package com.mipt.notification.consumer;

import com.mipt.notification.event.ReviewEvent;
import com.mipt.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReviewEventConsumer {

    private final NotificationService notificationService;

    @KafkaListener(
            topics = "${kafka.topic.review-events:review-events}",
            groupId = "${spring.kafka.consumer.group-id:notification-service}"
    )
    public void consume(ReviewEvent event) {
        log.info("Received REVIEW event: {} for seller: {}",
                event.getEventType(), event.getSellerId());

        try {
            notificationService.sendReviewNotification(event);
            log.info("Review event processed successfully");
        } catch (Exception e) {
            log.error("Error processing review event: {}", event.getEventType(), e);
        }
    }
}