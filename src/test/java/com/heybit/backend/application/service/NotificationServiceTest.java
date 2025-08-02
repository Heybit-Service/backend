package com.heybit.backend.application.service;

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
import com.heybit.backend.presentation.notification.dto.NotificationResponse;
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

    notificationRepository.save(Notification.builder()
        .userId(user.getId())
        .type(NotificationType.COMPLETED)
        .referenceType(ReferenceType.PRODUCT_TIMER)
        .referenceId(withVoteTimer.getId())
        .build());

    notificationRepository.save(Notification.builder()
        .userId(user.getId())
        .type(NotificationType.COMPLETED)
        .referenceType(ReferenceType.PRODUCT_TIMER)
        .referenceId(noVoteTimer.getId())
        .build());

    notificationRepository.save(Notification.builder()
        .userId(user.getId())
        .type(NotificationType.NEARLY_DONE)
        .referenceType(ReferenceType.PRODUCT_TIMER)
        .referenceId(999L)
        .build());

    // when
    List<NotificationResponse> responses = notificationService.getAllNotificationsByUserId(
        user.getId());

    // then
    assertThat(responses).hasSize(3);

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

}