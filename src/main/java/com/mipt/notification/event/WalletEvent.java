package com.mipt.notification.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class WalletEvent extends BaseEvent {
 private UUID walletOwnerId;
 private UUID operationId;
 private UUID clientId;
 private UUID performerId;
 private String operationType;
 private Long amount;
 private String title;
 private String details;
 private Instant timestamp;
}
