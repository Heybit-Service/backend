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

  private Long referenceId;
  private NotificationType type;
  private String title;
  private String message;
  private boolean withVote;

  public static NotificationResponse from(Notification entity, boolean withVote) {
    return NotificationResponse.builder()
        .referenceId(entity.getReferenceId())
        .title(entity.getTitle())
        .type(entity.getType())
        .message(entity.getBody())
        .withVote(withVote)
        .build();
  }
}
