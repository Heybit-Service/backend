package com.heybit.backend.application.service;

import com.heybit.backend.application.scheduler.TimerNotificationScheduler;
import com.heybit.backend.domain.timer.ProductTimer;
import com.heybit.backend.domain.timer.ProductTimerRepository;
import com.heybit.backend.domain.timer.TimerStatus;
import com.heybit.backend.domain.timerresult.ResultType;
import com.heybit.backend.domain.timerresult.TimerResult;
import com.heybit.backend.domain.timerresult.TimerResultRepository;
import com.heybit.backend.global.exception.ApiException;
import com.heybit.backend.global.exception.ErrorCode;
import com.heybit.backend.presentation.timer.dto.CompletedTimerResponse;
import com.heybit.backend.presentation.timerresult.dto.TimerResultRequest;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TimerResultService {

  private final TimerResultRepository timerResultRepository;
  private final ProductTimerRepository productTimerRepository;
  private final TimerNotificationScheduler timerNotificationScheduler;

  @Transactional
  public Long registerResult(TimerResultRequest request) {
    ProductTimer timer = findTimerOrThrow(request.getProductTimerId());

    if (timer.getStatus() != TimerStatus.WAITING) {
      throw new ApiException(ErrorCode.INVALID_TIMER_STATE);
    }

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

  @Transactional
  public Long abandonTimerResult(TimerResultRequest request) {
    ProductTimer timer = findTimerOrThrow(request.getProductTimerId());

    TimerResult result =  TimerResult.builder()
        .productTimer(timer)
        .result(ResultType.PURCHASED)
        .savedAmount(0)
        .consumedAmount(request.getAmount())
        .userComment(request.getUserComment())
        .build();

    timerResultRepository.save(result);

    timerNotificationScheduler.cancelTimerNotificationJob(timer.getId());

    timer.updateState(TimerStatus.ABANDONED);

    return result.getId();
  }

  @Transactional(readOnly = true)
  public List<CompletedTimerResponse> getCompletedResultsByUserId(Long userId) {
    return timerResultRepository
        .findCompletedResultsOfTimerWithInfoByUserId(userId)
        .stream()
        .map(CompletedTimerResponse::from)
        .toList();
  }

  @Transactional(readOnly = true)
  public Map<Long, ResultType> getResultTypeMapByTimerIds(List<Long> timerIds) {
    List<TimerResult> results = timerResultRepository.findByProductTimerIdIn(timerIds);

    return results.stream()
        .collect(Collectors.toMap(
            result -> result.getProductTimer().getId(),
            TimerResult::getResult
        ));
  }

  public TimerResult findByProductTimerId(Long timerId) {
    return timerResultRepository.findByProductTimerId(timerId)
        .orElse(null);
  }

  private ProductTimer findTimerOrThrow(Long timerId) {
    if (timerResultRepository.existsByProductTimerId(timerId)) {
      throw new ApiException(ErrorCode.ALREADY_REGISTERED_RESULT);
    }
    return productTimerRepository.findById(timerId)
        .orElseThrow(() -> new ApiException(ErrorCode.TIMER_NOT_FOUND));
  }

}
