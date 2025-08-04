package com.heybit.backend.presentation.timer;

import com.heybit.backend.application.service.ProductTimerService;
import com.heybit.backend.application.usecase.CreateTimerUseCase;
import com.heybit.backend.application.usecase.DeleteTimerUseCase;
import com.heybit.backend.global.response.ApiResponseEntity;
import com.heybit.backend.presentation.timer.dto.ProductTimerDetailResponse;
import com.heybit.backend.presentation.timer.dto.ProductTimerRequest;
import com.heybit.backend.presentation.timer.dto.ProductTimerResponse;
import com.heybit.backend.security.oauth.LoginUser;
import jakarta.validation.Valid;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/timer")
public class ProductTimerController {

  private final CreateTimerUseCase createTimerUseCase;
  private final DeleteTimerUseCase deleteTimerUseCase;
  private final ProductTimerService productTimerService;

  @GetMapping("/current")
  public ApiResponseEntity<List<ProductTimerResponse>> getProgressAndWaitingTimers(@LoginUser Long userId) {
    return ApiResponseEntity.success(productTimerService.getProgressAndWaitingTimers(userId));
  }

  @GetMapping("/{timerId}")
  public ApiResponseEntity<ProductTimerDetailResponse> getTimerDetail(
      @LoginUser Long userId,
      @PathVariable Long timerId
  ) {
    return ApiResponseEntity.success(productTimerService.getProductTimerDetail(userId, timerId));
  }

  @PostMapping()
  public ApiResponseEntity<Long> createTimer(
      @RequestPart("data") @Valid ProductTimerRequest request,
      @RequestPart(value = "img", required = false) MultipartFile img,
      @LoginUser Long userId
  ) throws IOException {
    Long timerId = createTimerUseCase.execute(request, userId, img);
    return ApiResponseEntity.success(timerId);
  }

  @DeleteMapping("/{timerId}")
  public void deleteTimer(
      @LoginUser Long userId,
      @PathVariable Long timerId
  ) {
    deleteTimerUseCase.execute(userId, timerId);
  }
}
