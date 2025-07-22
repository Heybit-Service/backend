package com.heybit.backend.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.*;

import com.heybit.backend.domain.productinfo.Category;
import com.heybit.backend.domain.timer.ProductTimer;
import com.heybit.backend.domain.timer.ProductTimerRepository;
import com.heybit.backend.domain.timer.TimerStatus;
import com.heybit.backend.domain.user.Role;
import com.heybit.backend.domain.user.User;
import com.heybit.backend.domain.user.UserRepository;
import com.heybit.backend.domain.user.UserStatus;
import com.heybit.backend.presentation.timer.dto.ProductTimerRequest;
import com.heybit.backend.presentation.timer.dto.ProductTimerResponse;
import com.heybit.backend.presentation.votepost.dto.MyVotePostResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class ProductTimerServiceTest {

  @Autowired
  private ProductTimerService productTimerService;

  @Autowired
  private CreateTimerService createTimerService;

  @Autowired
  private UserRepository userRepository;

  private User user;
  @Autowired
  private ProductTimerRepository productTimerRepository;

  @BeforeEach
  void setUp() {
    user = userRepository.save(User.builder()
        .nickname("jun")
        .email("jun@example.com")
        .role(Role.USER)
        .status(UserStatus.ACTIVE)
        .build());
  }

  private Long createTimer(
      String name,
      int amount,
      LocalDateTime start,
      LocalDateTime endTime,
      Long userId,
      boolean withVotePost
  ) throws IOException {

    return createTimerService.execute(
        ProductTimerRequest.builder()
            .name(name)
            .amount(amount)
            .description(name + " 설명")
            .category(Category.DAILY)
            .startTime(start)
            .endTime(endTime)
            .withVotePost(withVotePost)
            .build(),
        userId,
        null
    );
  }

  @Test
  @DisplayName("진행 중인 타이머 조회 및 정렬 순서 검증 - 투표글 존재 여부 포함")
  void getProgressTimer_withVotePost_successfully() throws IOException {
    // given
    createTimer("타이머1",
        10000,
        LocalDateTime.now().minusHours(1),
        LocalDateTime.now().plusHours(1),
        user.getId(),
        true
    );

    createTimer("종료시간 지남",
        20000,
        LocalDateTime.now().minusHours(1),
        LocalDateTime.now().minusMinutes(1),
        user.getId(),
        true
    );

    createTimer("투표글 없음",
        10000,
        LocalDateTime.now().minusHours(2),
        LocalDateTime.now().plusHours(2),
        user.getId(),
        false
    );

    List<ProductTimerResponse> responses = productTimerService.getProgressTimer(user.getId());

    assertThat(responses).hasSize(3);

    // 정렬 기준: 종료된 타이머가 먼저, 이후에는 생성일 순
    assertThat(responses)
        .extracting(ProductTimerResponse::getName, ProductTimerResponse::isActive,
            ProductTimerResponse::isWithVotePost)
        .containsExactly(
            tuple("종료시간 지남", false, true),
            tuple("타이머1", true, true),
            tuple("투표글 없음", true, false)
        );

  }

  @Test
  @DisplayName("진행 중인 타이머만 조회되는지 검증")
  void getProgressTimer_successfully() throws IOException {
    // given
    Long activeTimerId = createTimer("진행중인 타이머",
        10000,
        LocalDateTime.now().minusHours(1),
        LocalDateTime.now().plusHours(1),
        user.getId(),
        true
    );

    Long endedTimerId = createTimer("완료된 타이머",
        20000,
        LocalDateTime.now().minusHours(1),
        LocalDateTime.now().minusMinutes(1),
        user.getId(),
        true
    );

    ProductTimer endedTimer = productTimerRepository.findById(endedTimerId)
        .orElseThrow(() -> new IllegalArgumentException("해당 결과가 없습니다"));

    endedTimer.updateState(TimerStatus.COMPLETED);
    productTimerRepository.save(endedTimer);

    List<ProductTimerResponse> responses = productTimerService.getProgressTimer(user.getId());

    assertThat(responses).hasSize(1);
    assertThat(responses.get(0).getTimerId()).isEqualTo(activeTimerId);

  }
}