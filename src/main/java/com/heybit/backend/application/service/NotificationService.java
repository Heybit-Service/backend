package com.heybit.backend.application.service;

import com.heybit.backend.domain.notification.Notification;
import com.heybit.backend.domain.notification.NotificationRepository;
import com.heybit.backend.domain.notification.NotificationType;
import com.heybit.backend.domain.notification.ReferenceType;
import com.heybit.backend.domain.votepost.ProductVotePostRepository;
import com.heybit.backend.global.exception.ApiException;
import com.heybit.backend.global.exception.ErrorCode;
import com.heybit.backend.presentation.notification.dto.NotificationResponse;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationService {

  private final NotificationRepository notificationRepository;
  private final ProductVotePostRepository productVotePostRepository;
  private final UserService userService;

  public void save(
      String name, Long userId, ReferenceType referenceType, Long referenceId, NotificationType type, Duration duration
  ) {
    Notification notification = Notification.builder()
        .title(name)
        .body(type.generateMessage(duration))
        .referenceType(referenceType)
        .referenceId(referenceId)
        .type(type)
        .userId(userId)
        .viewed(false)
        .build();

    notificationRepository.save(notification);
  }

  @Transactional(readOnly = true)
  public List<NotificationResponse> getAllNotificationsByUserId(Long userId) {
    List<Notification> notifications = notificationRepository.findAllByUserIdOrderByCreatedAtDesc(userId);
    if (notifications.isEmpty()) {
      return Collections.emptyList();
    }

    List<Long> timerIds = notifications.stream()
        .map(Notification::getReferenceId)
        .toList();

    Set<Long> voteLinkedTimerIds = productVotePostRepository.findAllByProductTimerIdIn(timerIds)
        .stream()
        .map(vp -> vp.getProductTimer().getId())
        .collect(Collectors.toSet());

    return notifications.stream()
        .map(n -> NotificationResponse.from(n, voteLinkedTimerIds.contains(n.getReferenceId())))
        .toList();
  }

  @Transactional
  public void updateAllToRead(Long userId) {
    List<Notification> unreadNotifications = notificationRepository.findAllByUserIdAndViewedFalse(userId);
    for (Notification notification : unreadNotifications) {
      notification.markAsViewed();
    }
  }


  @Transactional
  public void deleteById(Long notificationId, Long userId) {
    Notification notification = notificationRepository.findById(notificationId)
        .orElseThrow(() -> new ApiException(ErrorCode.NOTIFICATION_NOT_FOUND));

    if (!notification.getUserId().equals(userId)) {
      throw new ApiException(ErrorCode.UNAUTHORIZED_ACCESS);
    }

    notificationRepository.delete(notification);
  }

  public void deleteAll(Long userId) {
    List<Notification> notifications = notificationRepository.findAllByUserId(userId);
    notificationRepository.deleteAllInBatch(notifications);
  }
}
