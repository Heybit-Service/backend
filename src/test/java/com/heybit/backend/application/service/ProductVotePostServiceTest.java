package com.heybit.backend.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

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
import com.heybit.backend.domain.vote.VoteResultType;
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
  private ProductInfoRepository productInfoRepository;

  @Autowired
  private ProductTimerRepository productTimerRepository;

  @Autowired
  private ProductVotePostRepository productVotePostRepository;

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

  private Long createTimer(
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

    var timer = productTimerRepository.save(
        ProductTimer.builder()
            .startTime(start)
            .endTime(endTime)
            .status(status)
            .productInfo(productInfo)
            .user(user)
            .build());

    if (withVotePost) {
      productVotePostRepository.save(
          ProductVotePost.builder()
              .productTimer(timer)
              .build());
    }

    return timer.getId();
  }

  @Test
  @DisplayName("IN_PROGRESS, WAITING 상태인 투표글 조회_성공테스트")
  void getInProgressVotePosts_success() throws IOException {
    User otherUser = userRepository.save(User.builder()
        .nickname("other")
        .email("other@example.com")
        .role(Role.USER)
        .status(UserStatus.ACTIVE)
        .build());

    Long inProgressId = createTimer(
        "IN_PROGRESS 상품",
        10000,
        LocalDateTime.now().minusMinutes(10),
        LocalDateTime.now().plusHours(1),
        TimerStatus.IN_PROGRESS,
        true
    );

    Long waitingId = createTimer(
        "WAITING 상품",
        10000,
        LocalDateTime.now().minusMinutes(10),
        LocalDateTime.now().plusHours(1),
        TimerStatus.WAITING,
        true
    );

    Long completedId = createTimer(
        "COMPLETED 상품",
        20000,
        LocalDateTime.now().minusHours(2),
        LocalDateTime.now().minusHours(1),
        TimerStatus.COMPLETED,
        true
    );

    Long abandonedId = createTimer(
        "ABANDONED 상품",
        30000,
        LocalDateTime.now().minusHours(1),
        LocalDateTime.now().minusHours(2),
        TimerStatus.ABANDONED,
        true
    );

    List<ProductVotePostResponse> results = votePostService.getAllInProgressPosts(otherUser.getId());

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
        TimerStatus.IN_PROGRESS,
        true
    );
    ProductVotePost votePost = productVotePostRepository.findByProductTimerId(timerId)
        .orElseThrow(() -> new IllegalStateException("투표글이 존재하지 않습니다"));

    voteRepository.save(Vote.builder()
        .user(user)
        .productVotePost(votePost)
        .result(VoteResultType.HOLD)
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
        TimerStatus.IN_PROGRESS,
        true
    );

    Long timerId2 = createTimer(
        "아이패드",
        1000000,
        LocalDateTime.now().minusHours(1),
        LocalDateTime.now().plusHours(2),
        TimerStatus.IN_PROGRESS,
        true
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

    ProductVotePost post1 = productVotePostRepository.findByProductTimerId(timerId1)
        .orElseThrow();
    ProductVotePost post2 = productVotePostRepository.findByProductTimerId(timerId2)
        .orElseThrow();

    // post 1에 투표
    voteRepository.save(Vote.builder().user(user1).productVotePost(post1).result(VoteResultType.HOLD).build());
    voteRepository.save(Vote.builder().user(user2).productVotePost(post1).result(VoteResultType.HOLD).build());

    // post 2에 투표
    voteRepository.save(Vote.builder().user(user1).productVotePost(post2).result(VoteResultType.BUY).build());

    List<MyVotePostResponse> result = votePostService.getMyVotePosts(user.getId());

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

    assertThat(resultByPost1.getVoteStats().getBuyCount()).isEqualTo(0);
    assertThat(resultByPost1.getVoteStats().getHoldCount()).isEqualTo(2);
    assertThat(resultByPost1.getVoteStats().getHoldPercent()).isEqualTo(100);

    MyVotePostResponse resultByPost2 = responseMap.get(
        post2.getProductTimer().getProductInfo().getName());
    if (resultByPost2 == null) {
      throw new IllegalArgumentException("해당 결과가 없습니다");
    }
    ;

    assertThat(resultByPost2.getVoteStats().getBuyCount()).isEqualTo(1);
    assertThat(resultByPost2.getVoteStats().getHoldCount()).isEqualTo(0);
    assertThat(resultByPost2.getVoteStats().getHoldPercent()).isEqualTo(0);
  }
}
