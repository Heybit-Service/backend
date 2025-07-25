package com.heybit.backend.application.service;

import com.heybit.backend.domain.notification.Notification;
import com.heybit.backend.domain.notification.NotificationRepository;
import com.heybit.backend.domain.notification.NotificationType;
import com.heybit.backend.domain.timer.ProductTimer;
import java.time.Duration;
import java.util.Timer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {

  private final NotificationRepository notificationRepository;

  public void save(
      String name, Long userId, String referenceType, Long referenceId, NotificationType type, Duration duration
  ) {
    Notification notification = Notification.builder()
        .title(name)
        .body(type.generateMessage(duration))
        .referenceType(referenceType)
        .referenceId(referenceId)
        .type(type)
        .userId(userId)
        .build();

    notificationRepository.save(notification);
  }

}
