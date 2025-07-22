package com.heybit.backend.application.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

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
import com.heybit.backend.presentation.timerresult.dto.TimerResultRequest;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class ProductTimerResultServiceTest {

  @Autowired
  private TimerResultService timerResultService;

  @Autowired
  private ProductTimerRepository productTimerRepository;

  @Autowired
  private TimerResultRepository timerResultRepository;

  private ProductTimer timer;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private ProductInfoRepository productInfoRepository;

  @BeforeEach
  void setUp() {
    User user = userRepository.save(User.builder()
        .nickname("jun")
        .email("jun@example.com")
        .role(Role.USER)
        .status(UserStatus.ACTIVE)
        .build());

    ProductInfo info = productInfoRepository.save(ProductInfo.builder()
        .name("에어팟")
        .amount(10000)
        .category(Category.ETC)
        .build());

    timer = productTimerRepository.save(ProductTimer.builder()
        .startTime(LocalDateTime.now().minusHours(1))
        .endTime(LocalDateTime.now().plusHours(1))
        .status(TimerStatus.IN_PROGRESS)
        .user(user)
        .productInfo(info)
        .build());
  }

  @Test
  @DisplayName("타이머 결과 저장 및 타이머 상태 변경 테스트")
  void saveResult_successfully() {
    //given
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
}

