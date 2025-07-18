package com.heybit.backend.application.service;

import static org.junit.jupiter.api.Assertions.*;

import com.heybit.backend.domain.productinfo.Category;
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
import com.heybit.backend.presentation.timer.dto.ProductTimerRequest;
import com.heybit.backend.presentation.votepost.dto.ProductVotePostResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;


@SpringBootTest
@Transactional
class ProductVotePostServiceTest {

  @Autowired
  private ProductVotePostService productVotePostService;

  @Autowired
  private CreateTimerService createTimerService;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private ProductTimerRepository productTimerRepository;

  @Autowired
  private ProductVotePostRepository productVotePostRepository;

  @Autowired
  private VoteRepository voteRepository;

  @Test
  @DisplayName("IN_PROGRESS 상태인 투표글만 조회_성공테스트")
  void getInProgressVotePosts_success() throws Exception {

    // given
    User user = userRepository.save(User.builder()
        .nickname("유저")
        .email("test@example.com")
        .role(Role.USER)
        .status(UserStatus.ACTIVE)
        .build());

    // IN_PROGRESS 타이머 생성
    Long inProgressTimerId = createTimerService.execute(
        ProductTimerRequest.builder()
            .name("IN_PROGRESS 상품")
            .amount(10000)
            .category(Category.ETC)
            .startTime(LocalDateTime.now().minusMinutes(10))
            .endTime(LocalDateTime.now().plusHours(1))
            .withVotePost(true)
            .build(),
        user.getId(),
        null
    );

    Long completedTimerId = createTimerService.execute(
        ProductTimerRequest.builder()
            .name("COMPLETED 상품")
            .amount(20000)
            .category(Category.ETC)
            .startTime(LocalDateTime.now().minusHours(2))
            .endTime(LocalDateTime.now().minusHours(1))
            .withVotePost(true)
            .build(),
        user.getId(),
        null
    );

    Long abandonedTimerId = createTimerService.execute(
        ProductTimerRequest.builder()
            .name("ABANDONED 상품")
            .amount(30000)
            .category(Category.ETC)
            .startTime(LocalDateTime.now().minusHours(3))
            .endTime(LocalDateTime.now().minusHours(2))
            .withVotePost(true)
            .build(),
        user.getId(),
        null
    );

    // 타이머 상태 변경
    productTimerRepository.findById(completedTimerId).ifPresent(timer -> {
      timer.updateState(TimerStatus.COMPLETED);
    });
    productTimerRepository.findById(abandonedTimerId).ifPresent(timer -> {
      timer.updateState(TimerStatus.ABANDONED);
    });

    // when
    List<ProductVotePostResponse> results = productVotePostService.getAllInProgressPosts();

    // then
    assertEquals(1, results.size(), "IN_PROGRESS 상태인 투표글만 조회되어야 함");
    assertEquals("IN_PROGRESS 상품", results.get(0).getName());
  }

  @Test
  @DisplayName("IN_PROGRESS 상태이지만 이미 투표한 글은 제외됨")
  void getInProgressVotePosts_without_voted() throws IOException {
    // given
    User user = userRepository.save(User.builder()
        .nickname("유저")
        .email("test@example.com")
        .role(Role.USER)
        .status(UserStatus.ACTIVE)
        .build());

    // IN_PROGRESS 타이머 생성
    Long timerId = createTimerService.execute(
        ProductTimerRequest.builder()
            .name("투표한 상품")
            .amount(15000)
            .category(Category.ETC)
            .startTime(LocalDateTime.now().minusMinutes(10))
            .endTime(LocalDateTime.now().plusHours(1))
            .withVotePost(true)
            .build(),
        user.getId(),
        null
    );

    // 투표글 엔티티 조회
    ProductVotePost votePost = productVotePostRepository.findByProductTimerId(timerId)
        .orElseThrow(() -> new IllegalStateException("투표글이 존재하지 않습니다"));

    // user가 해당 글에 투표
    voteRepository.save(Vote.builder()
        .user(user)
        .productVotePost(votePost)
        .result(true)
        .build());

    // when
    List<ProductVotePostResponse> results = productVotePostService.getAllInProgressPosts();

    // then
    assertEquals(0, results.size(), "이미 투표한 글은 조회되지 않아야합니다");
  }

}