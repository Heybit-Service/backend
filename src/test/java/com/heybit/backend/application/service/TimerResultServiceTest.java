package com.heybit.backend.application.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

import com.heybit.backend.application.scheduler.TimerNotificationScheduler;
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
import com.heybit.backend.global.exception.ApiException;
import com.heybit.backend.global.exception.ErrorCode;
import com.heybit.backend.presentation.timer.dto.CompletedTimerResponse;
import com.heybit.backend.presentation.timerresult.dto.TimerResultRequest;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class TimerResultServiceTest {

  @Autowired
  private TimerResultService timerResultService;

  @Autowired
  private ProductTimerRepository productTimerRepository;

  @Autowired
  private TimerResultRepository timerResultRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private ProductInfoRepository productInfoRepository;

  @MockitoBean
  private TimerNotificationScheduler timerNotificationScheduler;

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
      LocalDateTime endTime
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

    return timer;
  }

  @Test
  @DisplayName("타이머 결과 저장 및 타이머 상태 변경 테스트")
  void saveResult_successfully() {
    //given
    ProductTimer timer = createTimer("에어팟",
        3000,
        LocalDateTime.now().minusHours(1),
        LocalDateTime.now().plusHours(1)
    );

    timer.updateState(TimerStatus.WAITING);

    TimerResultRequest request = TimerResultRequest.builder()
        .productTimerId(timer.getId())
        .result(ResultType.SAVED)
        .amount(3000)
        .build();

    // when
    timerResultService.registerResult(request);

    // then
    TimerResult savedResult = timerResultRepository.findByProductTimerId(timer.getId())
        .orElseThrow(() -> new IllegalArgumentException("타이머 결과가 저장되지 않음"));

    assertThat(savedResult.getResult()).isEqualTo(ResultType.SAVED);
    assertThat(savedResult.getSavedAmount()).isEqualTo(3000);

    ProductTimer updatedTimer = productTimerRepository.findById(timer.getId())
        .orElseThrow();
    assertThat(updatedTimer.getStatus()).isEqualTo(TimerStatus.COMPLETED);
  }

  @Test
  @DisplayName("이미 결과가 등록된 타이머에 결과 등록 시도 시 예외가 발생")
  void saveResult_AlreadyRegistered() {
    // given
    ProductTimer timer = createTimer("에어팟",
        3000,
        LocalDateTime.now().minusHours(1),
        LocalDateTime.now().plusHours(1)
    );

    timer.updateState(TimerStatus.WAITING);

    TimerResultRequest request = TimerResultRequest.builder()
        .productTimerId(timer.getId())
        .result(ResultType.SAVED)
        .amount(3000)
        .build();

    timerResultService.registerResult(request);

    // when & then
    assertThatThrownBy(() -> timerResultService.registerResult(request))
        .isInstanceOf(ApiException.class)
        .hasMessageContaining(ErrorCode.ALREADY_REGISTERED_RESULT.getMessage());
  }

  @DisplayName("완료된 타이머에 대한 정보를 조회 한다")
  @Test
  void getCompletedResultsByUser_success() {
    // given
    ProductTimer completedTimer = createTimer("성공타이머",
        3000,
        LocalDateTime.now().minusHours(12),
        LocalDateTime.now().minusMinutes(30)
    );

    ProductTimer abandonedTimer = createTimer("포기타이머",
        6000,
        LocalDateTime.now().minusMinutes(450),
        LocalDateTime.now().minusMinutes(1)
    );

    timerResultRepository.save(
        TimerResult.builder()
            .productTimer(completedTimer)
            .result(ResultType.SAVED)
            .savedAmount(completedTimer.getProductInfo().getAmount())
            .build()
    );

    timerResultRepository.save(
        TimerResult.builder()
            .productTimer(abandonedTimer)
            .result(ResultType.PURCHASED)
            .consumedAmount(abandonedTimer.getProductInfo().getAmount())
            .build()
    );

    completedTimer.updateState(TimerStatus.COMPLETED);
    abandonedTimer.updateState(TimerStatus.ABANDONED);

    // when
    List<CompletedTimerResponse> responses = timerResultService.getCompletedResultsByUserId(
        user.getId());

    System.out.println(responses);

    assertThat(responses)
        .hasSize(2)
        .extracting(
            CompletedTimerResponse::getName,
            CompletedTimerResponse::isSuccess
        )
        .containsExactlyInAnyOrder(
            tuple("성공타이머", true),
            tuple("포기타이머", false)
        );


  }

  @Test
  @DisplayName("타이머를 포기할 때 결과 저장 및 스케줄러 취소 테스트")
  void abandonTimerResult_successfully() {
    // given
    ProductTimer timer = createTimer("포기타이머",
        5000,
        LocalDateTime.now().minusHours(1),
        LocalDateTime.now().plusHours(1)
    );

    TimerResultRequest request = TimerResultRequest.builder()
        .productTimerId(timer.getId())
        .result(ResultType.PURCHASED)
        .amount(5000)
        .build();

    // when
    timerResultService.abandonTimerResult(request);

    // then
    TimerResult savedResult = timerResultRepository.findByProductTimerId(timer.getId())
        .orElseThrow(() -> new IllegalArgumentException("타이머 결과가 저장되지 않음"));

    assertThat(savedResult.getResult()).isEqualTo(ResultType.PURCHASED);
    assertThat(savedResult.getConsumedAmount()).isEqualTo(5000);

    ProductTimer updatedTimer = productTimerRepository.findById(timer.getId())
        .orElseThrow();
    assertThat(updatedTimer.getStatus()).isEqualTo(TimerStatus.ABANDONED);
    verify(timerNotificationScheduler, atLeastOnce()).cancelTimerNotificationJob(anyLong());
  }

}

