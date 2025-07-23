package com.heybit.backend.application.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

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
class DeleteTimerServiceTest {

  @Autowired
  private DeleteTimerService deleteTimerService;

  @Autowired
  private ProductTimerRepository productTimerRepository;

  @Autowired
  private ProductInfoRepository productInfoRepository;

  @Autowired
  private ProductVotePostRepository productVotePostRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private VoteRepository voteRepository;

  private User user;
  private User otherUser;
  private ProductTimer timer;
  private ProductVotePost votePost;

  @BeforeEach
  void setUp() {
    user = userRepository.save(User.builder()
        .nickname("owner")
        .email("owner@example.com")
        .role(Role.USER)
        .status(UserStatus.ACTIVE)
        .build());

    otherUser = userRepository.save(User.builder()
        .nickname("other")
        .email("other@example.com")
        .role(Role.USER)
        .status(UserStatus.ACTIVE)
        .build());

    // 타이머 생성
    timer = productTimerRepository.save(ProductTimer.builder()
        .user(user)
        .startTime(LocalDateTime.now().minusHours(1))
        .endTime(LocalDateTime.now().plusHours(1))
        .status(TimerStatus.IN_PROGRESS)
        .productInfo(productInfoRepository.save(
            ProductInfo.builder()
                .name("Test Product")
                .amount(10000)
                .category(Category.ETC)
                .build()))
        .build());

    ProductVotePost votePost = productVotePostRepository.save(
        ProductVotePost.builder()
            .productTimer(timer)
            .build()
    );

    voteRepository.saveAll(List.of(
        Vote.builder()
            .user(user)
            .productVotePost(votePost)
            .result(true)
            .build()
    ));

  }

  @Test
  @DisplayName("소유자가 아닌 경우 삭제 시도하면 예외 발생")
  void deleteTimer_NotOwnerOfTimer() {
    ApiException exception = assertThrows(ApiException.class,
        () -> deleteTimerService.execute(timer.getId(), otherUser.getId()));
    assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.NOT_OWNER_OF_TIMER);
  }

  @Test
  @DisplayName("타이머 정상 삭제")
  void deleteTimer_successfully() {
    // delete 전 존재하는지 확인
    assertThat(productTimerRepository.findById(timer.getId())).isPresent();
    deleteTimerService.execute(timer.getId(), user.getId());

    // 삭제 후 타이머, 상품 정보, 투표글 등이 삭제됐는지 검증
    assertThat(productTimerRepository.findById(timer.getId())).isEmpty();
    assertThat(productInfoRepository.findById(timer.getProductInfo().getId())).isEmpty();
    assertThat(productVotePostRepository.findByProductTimerId(timer.getId())).isEmpty();
    assertThat(productVotePostRepository.findByProductTimerId(timer.getId())).isEmpty();
  }

}