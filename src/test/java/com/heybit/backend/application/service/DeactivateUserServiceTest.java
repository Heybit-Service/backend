package com.heybit.backend.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

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
import com.heybit.backend.infrastructure.quartz.NotificationJobSchedulerFactory;
import com.heybit.backend.infrastructure.s3.S3UploadComponent;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class DeactivateUserServiceTest {

  @Autowired
  private DeactivateUserService deactivateUserService;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private ProductInfoRepository productInfoRepository;

  @Autowired
  private ProductTimerRepository productTimerRepository;

  @Autowired
  private ProductVotePostRepository votePostRepository;

  @MockitoBean
  private S3UploadComponent s3UploadComponent;

  @MockitoBean
  private NotificationJobSchedulerFactory jobSchedulerFactory;

  private User user;

  @BeforeEach
  void setUp() {
    user = User.builder()
        .nickname("testUser")
        .email("testUser@email.com")
        .role(Role.USER)
        .status(UserStatus.ACTIVE)
        .build();
    userRepository.save(user);

    ProductTimer timer = productTimerRepository.save(ProductTimer.builder()
        .user(user)
        .startTime(LocalDateTime.now().minusHours(1))
        .endTime(LocalDateTime.now().plusHours(1))
        .status(TimerStatus.IN_PROGRESS)
        .productInfo(productInfoRepository.save(
            ProductInfo.builder()
                .name("Test Product")
                .amount(10000)
                .category(Category.ETC)
                .imageUrl("testImageUrl")
                .build()))
        .build());
    productTimerRepository.save(timer);

    // 3. VotePost 생성 (0..1 관계)
    ProductVotePost votePost = ProductVotePost.builder()
        .productTimer(timer)
        .build();
    votePostRepository.save(votePost);
  }

  @Test
  void testDeactivateUser() {
    // when
    deactivateUserService.deactivate(user.getId());

    // then
    List<ProductTimer> timers = productTimerRepository.findAllByUserId(user.getId());
    List<ProductVotePost> votePosts = votePostRepository.findAll();

    User updatedUser = userRepository.findById(user.getId()).orElseThrow();

    assertThat(timers).isEmpty();
    assertThat(votePosts).isEmpty();

    assertThat(updatedUser.getStatus()).isEqualTo(UserStatus.DELETED);

    verify(s3UploadComponent, atLeastOnce()).delete(anyString());
    verify(jobSchedulerFactory, atLeastOnce()).cancelAllByTimerId(anyLong());
  }
}