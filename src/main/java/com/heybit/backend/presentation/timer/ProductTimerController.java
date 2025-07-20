package com.heybit.backend.presentation.timer;

import com.heybit.backend.application.usecase.CreateTimerUseCase;
import com.heybit.backend.presentation.timer.dto.ProductTimerRequest;
import com.heybit.backend.security.oauth.LoginUser;
import jakarta.validation.Valid;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("api/v1/timer")
public class ProductTimerController {

  private final CreateTimerUseCase createTimerUseCase;

  @PostMapping()
  public ResponseEntity<Long> createTimer(
      @RequestPart("data") @Valid ProductTimerRequest request,
      @RequestPart(value = "img", required = false) MultipartFile img,
      @LoginUser Long userId
  ) throws IOException {
    Long timerId = createTimerUseCase.execute(request, userId, img);
    return ResponseEntity.ok(timerId);
  }

}
