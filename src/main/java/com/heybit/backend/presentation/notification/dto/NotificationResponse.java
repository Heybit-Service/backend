package com.heybit.backend.presentation.notification.dto;

import com.heybit.backend.domain.notification.Notification;
import com.heybit.backend.domain.notification.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationResponse {

  private Long id;
  private Long referenceId;
  private NotificationType type;
  private String title;
  private String message;
  private boolean withVote;
  private boolean viewed;

  public static NotificationResponse from(Notification entity, boolean withVote) {
    return NotificationResponse.builder()
        .id(entity.getId())
        .title(entity.getTitle())
        .type(entity.getType())
        .referenceId(entity.getReferenceId())
        .message(entity.getBody())
        .withVote(withVote)
        .viewed(entity.isViewed())
        .build();
  }
}
