package com.heybit.backend.application.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import com.heybit.backend.domain.productinfo.Category;
import com.heybit.backend.domain.timer.ProductTimerRepository;
import com.heybit.backend.domain.user.Role;
import com.heybit.backend.domain.user.User;
import com.heybit.backend.domain.user.UserRepository;
import com.heybit.backend.domain.user.UserStatus;
import com.heybit.backend.domain.vote.Vote;
import com.heybit.backend.domain.vote.VoteRepository;
import com.heybit.backend.domain.votepost.ProductVotePost;
import com.heybit.backend.domain.votepost.ProductVotePostRepository;
import com.heybit.backend.presentation.timer.dto.ProductTimerRequest;
import java.io.IOException;
import java.time.LocalDateTime;
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
  private CreateTimerService createTimerService;

  @Test
  @DisplayName("투표완료 히면 Vote가 저장된다")
  void vote_success() throws IOException {
    // given
    User user = userRepository.save(User.builder()
        .nickname("jun")
        .email("jun@test.com")
        .status(UserStatus.ACTIVE)
        .role(Role.USER)
        .build());

    Long timerId = createTimerService.execute(
        ProductTimerRequest.builder()
            .name("에어팟")
            .amount(200000)
            .category(Category.DAILY)
            .startTime(LocalDateTime.now().minusMinutes(5))
            .endTime(LocalDateTime.now().plusHours(1))
            .withVotePost(true)
            .build(),
        user.getId(),
        null
    );

    ProductVotePost votePost = votePostRepository.findByProductTimerId(timerId).orElseThrow();

    // when
    voteService.vote(votePost.getId(), user.getId(), true);

    // then
    boolean exists = voteRepository.existsByUserIdAndProductVotePostId(user.getId(),
        votePost.getId());

    assertThat(exists).isTrue();

    Vote vote = voteRepository.findAll().get(0);

    assertThat(vote.getResult()).isTrue();
    assertThat(vote.getUser().getId()).isEqualTo(user.getId());
  }

  @Test
  @DisplayName("이미 투표한 글에 다시 투표하면 예외 발생")
  void vote_fail_alreadyExists() throws IOException {
    // given
    User user = userRepository.save(User.builder()
        .nickname("jun")
        .email("jun@test.com")
        .status(UserStatus.ACTIVE)
        .role(Role.USER)
        .build());

    Long timerId = createTimerService.execute(
        ProductTimerRequest.builder()
            .name("에어팟")
            .amount(100000)
            .category(Category.ETC)
            .startTime(LocalDateTime.now().minusMinutes(5))
            .endTime(LocalDateTime.now().plusHours(1))
            .withVotePost(true)
            .build(),
        user.getId(),
        null
    );

    ProductVotePost votePost = votePostRepository.findByProductTimerId(timerId).orElseThrow();

    voteService.vote(votePost.getId(), user.getId(), true);

    // when & then
    assertThatThrownBy(() ->
        voteService.vote(votePost.getId(), user.getId(), false)
    ).isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("이미 투표한 글입니다");
  }

  @Test
  @DisplayName("투표 취소하면 해당 Vote가 삭제된다")
  void cancelVote_success() throws IOException {
    // given
    User user = userRepository.save(User.builder()
        .nickname("jun")
        .email("jun@test.com")
        .status(UserStatus.ACTIVE)
        .role(Role.USER)
        .build());

    Long timerId = createTimerService.execute(
        ProductTimerRequest.builder()
            .name("에어팟")
            .amount(80000)
            .category(Category.DAILY)
            .startTime(LocalDateTime.now().minusMinutes(5))
            .endTime(LocalDateTime.now().plusHours(1))
            .withVotePost(true)
            .build(),
        user.getId(),
        null
    );

    ProductVotePost votePost = votePostRepository.findByProductTimerId(timerId).orElseThrow();

    voteService.vote(votePost.getId(), user.getId(), true);

    assertThat(
        voteRepository.existsByUserIdAndProductVotePostId(user.getId(), votePost.getId())).isTrue();

    // when
    voteService.cancelVote(votePost.getId(), user.getId());

    // then
    assertThat(voteRepository.existsByUserIdAndProductVotePostId(user.getId(), votePost.getId()))
        .isFalse();
  }
}