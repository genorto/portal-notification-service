package com.mipt.notification.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReviewEvent extends BaseEvent{
    private UUID reviewId;
    private UUID sellerId;
    private UUID buyerId;
    private UUID advertisementId;
    private Integer rating;
    private String comment;
    private Boolean isAnonymous;
    private Boolean isVerifiedPurchase;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt;

    // Для обновления рейтинга
    private Double averageRating;
    private Integer totalReviews;
    private Integer fiveStarCount;
    private Integer fourStarCount;
    private Integer threeStarCount;
    private Integer twoStarCount;
    private Integer oneStarCount;
}