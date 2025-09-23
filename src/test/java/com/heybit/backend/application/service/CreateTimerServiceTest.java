package com.heybit.backend.application.service;

import static org.junit.jupiter.api.Assertions.*;

import com.heybit.backend.domain.productinfo.Category;
import com.heybit.backend.domain.productinfo.ProductInfoRepository;
import com.heybit.backend.domain.timer.ProductTimer;
import com.heybit.backend.domain.timer.ProductTimerRepository;
import com.heybit.backend.domain.user.Role;
import com.heybit.backend.domain.user.User;
import com.heybit.backend.domain.user.UserRepository;
import com.heybit.backend.domain.user.UserStatus;
import com.heybit.backend.domain.votepost.ProductVotePost;
import com.heybit.backend.domain.votepost.ProductVotePostRepository;
import com.heybit.backend.presentation.timer.dto.ProductTimerRequest;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


@SpringBootTest
@Transactional
class CreateTimerServiceTest {
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

  @Test
  @DisplayName("타이머 생성 성공 테스트")
  void createTimer_withVotePost_savesSuccessfully() throws Exception {
    // given
    User user = userRepository.save(User.builder()
        .nickname("tester")
        .email("tester@example.com")
        .role(Role.USER)
        .status(UserStatus.ACTIVE)
        .build());

    OffsetDateTime startTimeUTC = OffsetDateTime.now(ZoneOffset.UTC).plusHours(1);
    OffsetDateTime endTimeUTC = OffsetDateTime.now(ZoneOffset.UTC).plusHours(2);

    ProductTimerRequest request = ProductTimerRequest.builder()
        .name("테스트 상품")
        .amount(15000)
        .category(Category.ETC)
        .startTime(startTimeUTC)
        .endTime(endTimeUTC)
        .withVotePost(true)
        .build();

    // when
    Long timerId = createTimerService.execute(request, user.getId(), null);

    // then
    ProductTimer timer = productTimerRepository.findById(timerId)
        .orElseThrow(() -> new AssertionError("타이머가 저장되지 않았습니다."));

    LocalDateTime expectedStartKST = startTimeUTC.atZoneSameInstant(ZoneId.of("Asia/Seoul")).toLocalDateTime();
    LocalDateTime expectedEndKST = endTimeUTC.atZoneSameInstant(ZoneId.of("Asia/Seoul")).toLocalDateTime();

    assertEquals("테스트 상품", timer.getProductInfo().getName());
    assertEquals(expectedStartKST, timer.getStartTime());
    assertEquals(expectedEndKST, timer.getEndTime());
    assertTrue(productVotePostRepository.findByProductTimerId(timerId).isPresent());
  }

  @Test
  void createTimer_withoutVotePost_savesSuccessfully() throws Exception {
    // given
    MultipartFile file = null;

    User user = userRepository.save(User.builder()
        .nickname("tester")
        .email("tester@example.com")
        .role(Role.USER)
        .status(UserStatus.ACTIVE)
        .build());

    OffsetDateTime startTimeUTC = OffsetDateTime.now(ZoneOffset.UTC).plusHours(1);
    OffsetDateTime endTimeUTC = OffsetDateTime.now(ZoneOffset.UTC).plusHours(2);

    ProductTimerRequest request = ProductTimerRequest.builder()
        .name("테스트 상품2")
        .amount(5000)
        .category(Category.ETC)
        .startTime(startTimeUTC)
        .endTime(endTimeUTC)
        .withVotePost(false)
        .build();

    // when
    Long timerId = createTimerService.execute(request, user.getId(), file);

    // then
    ProductTimer timer = productTimerRepository.findById(timerId)
        .orElseThrow(() -> new AssertionError("타이머가 저장되지 않았습니다."));

    LocalDateTime expectedStartKST = startTimeUTC.atZoneSameInstant(ZoneId.of("Asia/Seoul")).toLocalDateTime();
    LocalDateTime expectedEndKST = endTimeUTC.atZoneSameInstant(ZoneId.of("Asia/Seoul")).toLocalDateTime();

    assertEquals("테스트 상품2", timer.getProductInfo().getName());
    assertEquals(expectedStartKST, timer.getStartTime());
    assertEquals(expectedEndKST, timer.getEndTime());
    assertFalse(productVotePostRepository.findByProductTimerId(timerId).isPresent());
  }

}