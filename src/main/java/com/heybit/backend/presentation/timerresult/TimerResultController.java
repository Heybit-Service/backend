package com.heybit.backend.presentation.timerresult;

import com.heybit.backend.application.service.TimerResultService;
import com.heybit.backend.global.response.ApiResponseEntity;
import com.heybit.backend.presentation.timer.dto.CompletedTimerResponse;
import com.heybit.backend.presentation.timerresult.dto.TimerResultRequest;
import com.heybit.backend.security.oauth.LoginUser;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TimerResultController {

  private final TimerResultService timerResultService;

  @PostMapping("/api/v1/timer-results")
  public ApiResponseEntity<Long> registerTimerResult(@RequestBody TimerResultRequest request) {
    Long resultId = timerResultService.registerResult(request);
    return ApiResponseEntity.success(resultId);
  }

  @PostMapping("/api/v1/timer-abandon")
  public ApiResponseEntity<Long> registerTimerAbandon(@RequestBody TimerResultRequest request) {
    Long resultId = timerResultService.abandonTimerResult(request);
    return ApiResponseEntity.success(resultId);
  }

  @GetMapping("/api/v1/timers/history")
  public ApiResponseEntity<List<CompletedTimerResponse>> getCompletedResultsByUserId(@LoginUser Long userId) {
    List<CompletedTimerResponse> responses = timerResultService.getCompletedResultsByUserId(userId);
    return ApiResponseEntity.success(responses);
  }

}
