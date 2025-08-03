package com.heybit.backend.presentation.notification;

import com.heybit.backend.application.service.NotificationService;
import com.heybit.backend.global.response.ApiResponseEntity;
import com.heybit.backend.presentation.notification.dto.NotificationResponse;
import com.heybit.backend.security.oauth.LoginUser;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

  private final NotificationService notificationService;

  @GetMapping("/me")
  public ApiResponseEntity<List<NotificationResponse>> getAllNotificationsByUserId(@LoginUser Long userId) {
    List<NotificationResponse> responses = notificationService.getAllNotificationsByUserId(userId);
    notificationService.updateAllToRead(userId);
    return ApiResponseEntity.success(responses);
  }

  @GetMapping("/notifications/unread-exists")
  public ApiResponseEntity<Boolean> hasUnreadNotifications(@LoginUser Long userId) {
    boolean hasUnread = notificationService.checkUnreadNotifications(userId);
    return ApiResponseEntity.success(hasUnread);
  }

  @DeleteMapping("/{id}")
  public ApiResponseEntity<Void> deleteNotificationById(@PathVariable Long id, @LoginUser Long userId
  ) {
    notificationService.deleteById(userId, id);
    return ApiResponseEntity.success();
  }

  @DeleteMapping
  public ApiResponseEntity<Void> deleteAllNotifications(@LoginUser Long userId) {
    notificationService.deleteAll(userId);
    return ApiResponseEntity.success();
  }

}
