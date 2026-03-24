package com.mipt.notification.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdvertisementEvent {
    private String eventType;
    private UUID advertisementId;
    private UUID authorId;
    private String advertisementName;
    private String category;
    private Long price;
    private String status;
    private Instant timestamp;
    private String details;
}