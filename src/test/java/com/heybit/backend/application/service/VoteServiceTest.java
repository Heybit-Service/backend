package com.heybit.backend.application.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.heybit.backend.domain.productinfo.Category;
import com.heybit.backend.domain.productinfo.ProductInfo;
import com.heybit.backend.domain.productinfo.ProductInfoRepository;
import com.heybit.backend.domain.timer.ProductTimer;
import com.heybit.backend.domain.timer.ProductTimerRepository;
import com.heybit.backend.domain.timer.TimerStatus;
import com.heybit.backend.domain.timerresult.ResultType;
import com.heybit.backend.domain.timerresult.TimerResult;
import com.heybit.backend.domain.timerresult.TimerResultRepository;
import com.heybit.backend.domain.user.Role;
import com.heybit.backend.domain.user.User;
import com.heybit.backend.domain.user.UserRepository;
import com.heybit.backend.domain.user.UserStatus;
import com.heybit.backend.domain.vote.Vote;
import com.heybit.backend.domain.vote.VoteRepository;
import com.heybit.backend.domain.vote.VoteResultType;
import com.heybit.backend.domain.votepost.ProductVotePost;
import com.heybit.backend.domain.votepost.ProductVotePostRepository;
import com.heybit.backend.presentation.timer.dto.ProductTimerRequest;
import com.heybit.backend.presentation.vote.dto.VotedPostResponse;
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
class VoteServiceTest {

  @Autowired
  private VoteService voteService;

  @Autowired
  private VoteRepository voteRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private ProductVotePostRepository votePostRepository;

  @Autowired
  private ProductTimerRepository timerRepository;

  @Autowired
  private ProductInfoRepository productInfoRepository;

  @Autowired
  private CreateTimerService createTimerService;

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

  private ProductTimer createTimer(
      String name,
      int amount,
      LocalDateTime start,
      LocalDateTime endTime,
      TimerStatus status,
      boolean withVotePost
  ) {
    var productInfo = productInfoRepository.save(
        ProductInfo.builder()
            .name(name)
            .amount(amount)
            .category(Category.ETC)
            .build());

    var timer = timerRepository.save(
        ProductTimer.builder()
            .startTime(start)
            .endTime(endTime)
            .status(status)
            .productInfo(productInfo)
            .user(user)
            .build());

    if (withVotePost) {
      votePostRepository.save(
          ProductVotePost.builder()
              .productTimer(timer)
              .build());
    }

    return timer;
  }

  @Test
  @DisplayName("투표완료 히면 Vote가 저장된다")
  void vote_success() throws IOException {
    // given
    Long timerId = createTimer(
        "에어팟",
        200000,
        LocalDateTime.now().minusMinutes(5),
        LocalDateTime.now().plusHours(1),
        TimerStatus.IN_PROGRESS,
        true
    ).getId();;

    ProductVotePost votePost = votePostRepository.findByProductTimerId(timerId).orElseThrow();

    // when
    voteService.vote(votePost.getId(), user.getId(), VoteResultType.HOLD);

    // then
    boolean exists = voteRepository.existsByUserIdAndProductVotePostId(user.getId(),
        votePost.getId());

    assertThat(exists).isTrue();

    Vote vote = voteRepository.findAll().get(0);

    assertThat(vote.getResult()).isEqualTo(VoteResultType.HOLD);
    assertThat(vote.getUser().getId()).isEqualTo(user.getId());
  }

  @Test
  @DisplayName("이미 투표한 글에 다시 투표하면 예외 발생")
  void vote_fail_alreadyExists() throws IOException {
    // given
    Long timerId = createTimer(
        "에어팟",
        200000,
        LocalDateTime.now().minusMinutes(5),
        LocalDateTime.now().plusHours(1),
        TimerStatus.IN_PROGRESS,
        true
    ).getId();;

    ProductVotePost votePost = votePostRepository.findByProductTimerId(timerId).orElseThrow();

    voteService.vote(votePost.getId(), user.getId(), VoteResultType.HOLD);

    // when & then
    assertThatThrownBy(() ->
        voteService.vote(votePost.getId(), user.getId(), VoteResultType.BUY)
    ).isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("이미 투표한 글입니다");
  }

  @Test
  @DisplayName("투표 취소하면 해당 Vote가 삭제된다")
  void cancelVote_success() throws IOException {
    // given
    Long timerId = createTimer(
        "에어팟",
        200000,
        LocalDateTime.now().minusMinutes(5),
        LocalDateTime.now().plusHours(1),
        TimerStatus.IN_PROGRESS,
        true
    ).getId();;

    ProductVotePost votePost = votePostRepository.findByProductTimerId(timerId).orElseThrow();
    voteService.vote(votePost.getId(), user.getId(), VoteResultType.HOLD);

    assertThat(
        voteRepository.existsByUserIdAndProductVotePostId(user.getId(), votePost.getId())).isTrue();

    // when
    voteService.cancelVote(votePost.getId(), user.getId());

    // then
    assertThat(voteRepository.existsByUserIdAndProductVotePostId(user.getId(), votePost.getId()))
        .isFalse();
  }
  
}