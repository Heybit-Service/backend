package com.heybit.backend.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
import com.heybit.backend.domain.vote.Vote;
import com.heybit.backend.domain.vote.VoteRepository;
import com.heybit.backend.domain.votepost.ProductVotePost;
import com.heybit.backend.domain.votepost.ProductVotePostRepository;
import com.heybit.backend.global.exception.ApiException;
import com.heybit.backend.global.exception.ErrorCode;
import com.heybit.backend.presentation.timer.dto.ProductTimerDetailResponse;
import com.heybit.backend.presentation.timer.dto.ProductTimerResponse;
import java.io.IOException;
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
class ProductTimerServiceTest {

  @Autowired
  private ProductTimerService productTimerService;

  @Autowired
  private CreateTimerService createTimerService;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private ProductTimerRepository productTimerRepository;

  @Autowired
  private ProductInfoRepository productInfoRepository;

  @Autowired
  private ProductVotePostRepository productVotePostRepository;

  @Autowired
  private VoteRepository voteRepository;

  private User user;
  private ProductVotePost productVotePost;


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
      boolean withVotePost
  ) {
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
      productVotePost = productVotePostRepository.save(
          ProductVotePost.builder()
              .productTimer(timer)
              .build());
    }

    return timer.getId();
  }

  @Test
  @DisplayName("진행 중인 타이머 조회 및 정렬 순서 검증 - 투표글 존재 여부 포함")
  void getProgressTimer_withVotePost_successfully() throws IOException {
    // given
    createTimer("타이머1",
        10000,
        LocalDateTime.now().minusHours(1),
        LocalDateTime.now().plusHours(1),
        true
    );

    createTimer("종료시간 지남",
        20000,
        LocalDateTime.now().minusHours(1),
        LocalDateTime.now().minusMinutes(1),
        true
    );

    createTimer("투표글 없음",
        10000,
        LocalDateTime.now().minusHours(2),
        LocalDateTime.now().plusHours(2),
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
        true
    );

    Long endedTimerId = createTimer("완료된 타이머",
        20000,
        LocalDateTime.now().minusHours(1),
        LocalDateTime.now().minusMinutes(1),
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

  @Test
  @DisplayName("타이머 상세 조회 검증 투표가 존재하면 투표 통계 포함 조회")
  void getProductTimerDetail_withVoteStats() {

    // given
    Long timerId = createTimer("타이머1",
        10000,
        LocalDateTime.now().minusHours(1),
        LocalDateTime.now().plusHours(1),
        true
    );

    User otherUser = userRepository.save(User.builder()
        .nickname("other")
        .email("other@example.com")
        .role(Role.USER)
        .status(UserStatus.ACTIVE)
        .build());

    voteRepository.save(
        Vote.builder()
            .productVotePost(productVotePost)
            .user(otherUser)
            .result(false)
            .build());

    ProductTimerDetailResponse response = productTimerService
        .getProductTimerDetail(
            user.getId(),
            timerId);

    assertThat(response).isNotNull();
    assertThat(response.getName()).isEqualTo("타이머1");
    assertThat(response.getBuyCount()).isEqualTo(0);
    assertThat(response.getHoldCount()).isEqualTo(1);
    assertThat(response.getHoldPercent()).isEqualTo(100);
  }

  @Test
  @DisplayName("존재하지 않는 타이머 조회시 예외발생 ")
  void getProductTimerDetail_timerNotFound() {
    Long invalidTimerId = 999999L;

    ApiException ex = assertThrows(ApiException.class, () ->
        productTimerService.getProductTimerDetail(user.getId(), invalidTimerId));

    assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.TIMER_NOT_FOUND);
  }
}