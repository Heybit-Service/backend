package com.heybit.backend.application.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import com.heybit.backend.domain.notification.Notification;
import com.heybit.backend.domain.notification.NotificationRepository;
import com.heybit.backend.domain.notification.NotificationType;
import com.heybit.backend.domain.notification.ReferenceType;
import com.heybit.backend.domain.productinfo.Category;
import com.heybit.backend.domain.productinfo.ProductInfo;
import com.heybit.backend.domain.productinfo.ProductInfoRepository;
import com.heybit.backend.domain.timer.ProductTimer;
import com.heybit.backend.domain.timer.ProductTimerRepository;
import com.heybit.backend.domain.timer.TimerStatus;
import com.heybit.backend.domain.user.Role;
import com.heybit.backend.domain.user.User;
import com.heybit.backend.domain.user.UserRepository;
import com.heybit.backend.domain.user.UserStatus;
import com.heybit.backend.domain.votepost.ProductVotePost;
import com.heybit.backend.domain.votepost.ProductVotePostRepository;
import com.heybit.backend.global.exception.ApiException;
import com.heybit.backend.global.exception.ErrorCode;
import com.heybit.backend.presentation.notification.dto.NotificationResponse;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class NotificationServiceTest {

  @Autowired
  private NotificationService notificationService;

  @Autowired
  private ProductVotePostRepository productVotePostRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private NotificationRepository notificationRepository;

  @Autowired
  private ProductInfoRepository productInfoRepository;

  @Autowired
  private ProductTimerRepository productTimerRepository;

  private User user;

  @BeforeEach
  void setUp() {
    user = userRepository.save(User.builder()
        .nickname("jun")
        .email("jun@example.com")
        .role(Role.USER)
        .status(UserStatus.ACTIVE)
        .build());
  }

  private ProductTimer createTimer(String name, int amount, LocalDateTime start,
      LocalDateTime endTime, boolean withVotePost) {
    var productInfo = productInfoRepository.save(
        ProductInfo.builder()
            .name(name)
            .amount(amount)
            .category(Category.ETC)
            .build());

    var timer = productTimerRepository.save(
        ProductTimer.builder()
            .startTime(start)
            .endTime(endTime)
            .status(TimerStatus.IN_PROGRESS)
            .productInfo(productInfo)
            .user(user)
            .build());

    if (withVotePost) {
      productVotePostRepository.save(
          ProductVotePost.builder()
              .productTimer(timer)
              .build());
    }

    return timer;
  }

  private Notification createNotification(
      Long userId,
      NotificationType type,
      ReferenceType referenceType,
      Long referenceId,
      boolean viewed
  ) {
    return notificationRepository.save(Notification.builder()
        .userId(userId)
        .type(type)
        .title("title")
        .body("body")
        .referenceType(referenceType)
        .referenceId(referenceId)
        .viewed(viewed)
        .build());
  }

  @Test
  @DisplayName("타이머 알람 조회시 투표글 연동 여부도 함께 반환")
  void getAllNotificationsByUserId_withVote_successfully() {
    // given
    ProductTimer withVoteTimer = createTimer(
        "투표글 있음",
        1000,
        LocalDateTime.now().minusHours(1),
        LocalDateTime.now().plusHours(1),
        true);

    ProductTimer noVoteTimer = createTimer(
        "투표글 없음",
        500,
        LocalDateTime.now().minusHours(1),
        LocalDateTime.now().plusHours(1),
        false);

    createNotification(
        user.getId(),
        NotificationType.COMPLETED,
        ReferenceType.PRODUCT_TIMER,
        withVoteTimer.getId(),
        false
    );

    createNotification(
        user.getId(),
        NotificationType.COMPLETED,
        ReferenceType.PRODUCT_TIMER,
        noVoteTimer.getId(),
        false
    );

    // when
    List<NotificationResponse> responses = notificationService.getAllNotificationsByUserId(
        user.getId());

    responses.forEach(r -> System.out.println("response id: " + r.getReferenceId() + ", withVote: " + r.isWithVote()));
    System.out.println(withVoteTimer.getId());
    System.out.println(noVoteTimer.getId());

    // then
    assertThat(responses).hasSize(2);

    NotificationResponse votedResponse = responses.stream()
        .filter(r -> r.getReferenceId().equals(withVoteTimer.getId()))
        .findFirst()
        .orElseThrow();

    NotificationResponse noVoteResponse = responses.stream()
        .filter(r -> r.getReferenceId().equals(noVoteTimer.getId()))
        .findFirst()
        .orElseThrow();

    assertThat(votedResponse.isWithVote()).isTrue();
    assertThat(noVoteResponse.isWithVote()).isFalse();
  }

  @Test
  @DisplayName("사용자의 모든 알림을 읽음 처리한다")
  void updateAllToRead_successfully() {
    // given
    Notification unread1 = createNotification(
        user.getId(),
        NotificationType.COMPLETED,
        ReferenceType.PRODUCT_TIMER,
        1L,
        false
    );

    Notification unread2 = createNotification(
        user.getId(),
        NotificationType.COMPLETED,
        ReferenceType.PRODUCT_TIMER,
        2L,
        false
    );

    // when
    notificationService.updateAllToRead(user.getId());

    // then
    List<Notification> notifications = notificationRepository.findAllByUserId(user.getId());
    assertThat(notifications).allMatch(Notification::isViewed);
  }

  @Test
  @DisplayName("사용자의 모든 알림을 삭제한다")
  void deleteAllNotifications_successfully() {
    // given
    createNotification(
        user.getId(),
        NotificationType.COMPLETED,
        ReferenceType.PRODUCT_TIMER,
        1L,
        false
    );

    createNotification(
        user.getId(),
        NotificationType.COMPLETED,
        ReferenceType.PRODUCT_TIMER,
        2L,
        false
    );

    // when
    notificationService.deleteAll(user.getId());

    // then
    List<Notification> notifications = notificationRepository.findAllByUserId(user.getId());
    assertThat(notifications).isEmpty();
  }


  @Test
  @DisplayName("알림 삭제 - 성공 테스트")
  void deleteNotificationById_successfully() {
    Notification saved = createNotification(
        user.getId(),
        NotificationType.COMPLETED,
        ReferenceType.PRODUCT_TIMER,
        2L,
        false
    );

    notificationService.deleteById(saved.getId(), user.getId());

    boolean exists = notificationRepository.existsById(saved.getId());
    assertThat(exists).isFalse();
  }

  @Test
  @DisplayName("알림 삭제 - 실페 테스트 다른 유저가 삭제 시도 시 예외 발생")
  void deleteNotification_fail_Unauthorized() {
    Notification saved = createNotification(
        user.getId(),
        NotificationType.COMPLETED,
        ReferenceType.PRODUCT_TIMER,
        2L,
        false
    );

    Long otherUserId = user.getId() + 1;

    assertThatThrownBy(() -> notificationService.deleteById(saved.getId(), otherUserId))
        .isInstanceOf(ApiException.class)
        .hasMessageContaining(ErrorCode.UNAUTHORIZED_ACCESS.getMessage());
  }

  @Test
  @DisplayName("알림 삭제 - 실패 테스트 존재하지 않는 알림 조회 시도 시 예외 발생")
  void deleteNotification_fail_NotFound() {
    Long invalidId = 999L;

    assertThatThrownBy(() -> notificationService.deleteById(invalidId, user.getId()))
        .isInstanceOf(ApiException.class)
        .hasMessageContaining(ErrorCode.NOTIFICATION_NOT_FOUND.getMessage());
  }


}