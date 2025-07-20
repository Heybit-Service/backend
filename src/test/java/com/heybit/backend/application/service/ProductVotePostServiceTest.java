package com.heybit.backend.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

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
import com.heybit.backend.presentation.votepost.dto.MyVotePostResponse;
import com.heybit.backend.presentation.votepost.dto.ProductVotePostResponse;
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
class ProductVotePostServiceTest {

  @Autowired
  private ProductVotePostService votePostService;

  @Autowired
  private CreateTimerService createTimerService;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private ProductTimerRepository productTimerRepository;

  @Autowired
  private ProductVotePostRepository votePostRepository;

  @Autowired
  private VoteRepository voteRepository;

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

  private Long createTimer(String name, int amount, LocalDateTime start,
      LocalDateTime endTime, Long userId)
      throws IOException {
    return createTimerService.execute(
        ProductTimerRequest.builder()
            .name(name)
            .amount(amount)
            .description(name + " 설명")
            .category(Category.DAILY)
            .startTime(start)
            .endTime(endTime)
            .withVotePost(true)
            .build(),
        userId,
        null
    );
  }

  @Test
  @DisplayName("IN_PROGRESS 상태인 투표글만 조회_성공테스트")
  void getInProgressVotePosts_success() throws IOException {
    Long inProgressId = createTimer(
        "IN_PROGRESS 상품",
        10000,
        LocalDateTime.now().minusMinutes(10),
        LocalDateTime.now().plusHours(1),
        user.getId()
    );

    Long completedId = createTimer(
        "COMPLETED 상품",
        20000,
        LocalDateTime.now().minusHours(2),
        LocalDateTime.now().minusHours(1),
        user.getId()
    );

    Long abandonedId = createTimer(
        "ABANDONED 상품",
        30000,
        LocalDateTime.now().minusHours(1),
        LocalDateTime.now().minusHours(2),
        user.getId()
    );

    productTimerRepository.findById(completedId)
        .ifPresent(timer -> timer.updateState(TimerStatus.COMPLETED));

    productTimerRepository.findById(abandonedId)
        .ifPresent(timer -> timer.updateState(TimerStatus.ABANDONED));

    List<ProductVotePostResponse> results = votePostService.getAllInProgressPosts(user.getId());

    assertEquals(1, results.size());
    assertEquals("IN_PROGRESS 상품", results.get(0).getName());
  }

  @Test
  @DisplayName("IN_PROGRESS 상태이지만 이미 투표한 글은 제외됨")
  void getInProgressVotePosts_without_voted() throws IOException {
    Long timerId = createTimer(
        "투표한 상품",
        15000,
        LocalDateTime.now().minusMinutes(10),
        LocalDateTime.now().plusHours(1),
        user.getId()
    );
    ProductVotePost votePost = votePostRepository.findByProductTimerId(timerId)
        .orElseThrow(() -> new IllegalStateException("투표글이 존재하지 않습니다"));

    voteRepository.save(Vote.builder()
        .user(user)
        .productVotePost(votePost)
        .result(true)
        .build());

    List<ProductVotePostResponse> results = votePostService.getAllInProgressPosts(user.getId());

    assertEquals(0, results.size());
  }

  @Test
  @DisplayName("내가 작성한 진행중 투표글 통계 포함 조회")
  void getMyInProgressVotePosts_success() throws IOException {
    Long timerId1 = createTimer(
        "에어팟",
        300000,
        LocalDateTime.now().minusHours(1),
        LocalDateTime.now().plusHours(3),
        user.getId()
    );

    Long timerId2 = createTimer(
        "아이패드",
        1000000,
        LocalDateTime.now().minusHours(1),
        LocalDateTime.now().plusHours(2),
        user.getId()
    );

    User user1 = userRepository.save(
        User.builder()
            .nickname("jun1")
            .email("jun1@example.com")
            .role(Role.USER)
            .status(UserStatus.ACTIVE)
            .build());

    User user2 = userRepository.save(
        User.builder()
            .nickname("jun2")
            .email("jun2@example.com")
            .role(Role.USER)
            .status(UserStatus.ACTIVE)
            .build());

    ProductVotePost post1 = votePostRepository.findByProductTimerId(timerId1)
        .orElseThrow();
    ProductVotePost post2 = votePostRepository.findByProductTimerId(timerId2)
        .orElseThrow();

    // post 1에 투표
    voteRepository.save(Vote.builder().user(user1).productVotePost(post1).result(true).build());
    voteRepository.save(Vote.builder().user(user2).productVotePost(post1).result(true).build());

    // post 2에 투표
    voteRepository.save(Vote.builder().user(user1).productVotePost(post2).result(false).build());

    List<MyVotePostResponse> result = votePostService.getMyInProgressVotePosts(user.getId());

    assertThat(result).hasSize(2);

    Map<String, MyVotePostResponse> responseMap = result.stream()
        .collect(Collectors.toMap(
            MyVotePostResponse::getName,
            response -> response
        ));

    MyVotePostResponse resultByPost1 = responseMap.get(
        post1.getProductTimer().getProductInfo().getName());
    if (resultByPost1 == null) {
      throw new IllegalArgumentException("해당 결과가 없습니다");
    }

    assertThat(resultByPost1.getBuyCount()).isEqualTo(2);
    assertThat(resultByPost1.getHoldCount()).isEqualTo(0);
    assertThat(resultByPost1.getHoldPercent()).isEqualTo(0);

    MyVotePostResponse resultByPost2 = responseMap.get(
        post2.getProductTimer().getProductInfo().getName());
    if (resultByPost2 == null) {
      throw new IllegalArgumentException("해당 결과가 없습니다");
    }
    ;

    assertThat(resultByPost2.getBuyCount()).isEqualTo(0);
    assertThat(resultByPost2.getHoldCount()).isEqualTo(1);
    assertThat(resultByPost2.getHoldPercent()).isEqualTo(100);
  }
}
