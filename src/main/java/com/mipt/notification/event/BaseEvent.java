package com.mipt.notification.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseEvent {
 protected String eventType;
 protected UUID sourceId;
 protected Instant timestamp;
 protected String source;

 public BaseEvent(String eventType, UUID sourceId, String source) {
  this.eventType = eventType;
  this.sourceId = sourceId;
  this.source = source;
  this.timestamp = Instant.now();
 }
}
