package com.heybit.backend.application.service;

import com.heybit.backend.domain.timer.ProductTimer;
import com.heybit.backend.domain.timer.ProductTimerRepository;
import com.heybit.backend.domain.timer.TimerStatus;
import com.heybit.backend.domain.timerresult.ResultType;
import com.heybit.backend.domain.timerresult.TimerResult;
import com.heybit.backend.domain.timerresult.TimerResultRepository;
import com.heybit.backend.global.exception.ApiException;
import com.heybit.backend.global.exception.ErrorCode;
import com.heybit.backend.presentation.timerresult.dto.TimerResultRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TimerResultService {

  private final TimerResultRepository timerResultRepository;
  private final ProductTimerRepository productTimerRepository;

  @Transactional
  public Long registerResult(TimerResultRequest request) {
    if (timerResultRepository.existsByProductTimerId(request.getProductTimerId())) {
      throw new ApiException(ErrorCode.ALREADY_REGISTERED_RESULT);
    }

    ProductTimer timer = productTimerRepository.findById(request.getProductTimerId())
        .orElseThrow(() -> new ApiException(ErrorCode.TIMER_NOT_FOUND));

    TimerResult result = TimerResult.builder()
        .productTimer(timer)
        .result(request.getResult())
        .savedAmount(request.getResult() == ResultType.SAVED ? request.getAmount() : 0)
        .consumedAmount(request.getResult() == ResultType.PURCHASED ? request.getAmount() : 0)
        .userComment(request.getUserComment())
        .build();

    timerResultRepository.save(result);
    timer.updateState(TimerStatus.COMPLETED);

    return result.getId();
  }
}
