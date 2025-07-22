package com.heybit.backend.application.service;

import com.heybit.backend.application.scheduler.TimerNotificationScheduler;
import com.heybit.backend.application.usecase.DeleteTimerUseCase;
import com.heybit.backend.domain.timer.ProductTimer;
import com.heybit.backend.global.exception.ApiException;
import com.heybit.backend.global.exception.ErrorCode;
import com.heybit.backend.infrastructure.s3.S3UploadComponent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class DeleteTimerService implements DeleteTimerUseCase {

  private final ProductTimerService productTimerService;
  private final TimerNotificationScheduler timerNotificationScheduler;
  private final ProductInfoService productInfoService;
  private final ProductVotePostService productVotePostService;
  private final S3UploadComponent s3UploadComponent;

  @Override
  public void execute(Long timerId, Long userId) {

    ProductTimer timer = productTimerService.findById(timerId);
    if (!timer.getUser().getId().equals(userId)) {
      throw new ApiException(ErrorCode.NOT_OWNER_OF_TIMER);
    }

    String imageUrl = timer.getProductInfo().getImageUrl();
    if (imageUrl != null) {
      s3UploadComponent.delete(imageUrl);
    }

    timerNotificationScheduler.cancelTimerNotifications(timer);
    productVotePostService.deleteVotePostWithVotesByTimerId(timerId);
    productTimerService.delete(timer);
    productInfoService.delete(timer.getProductInfo());
  }
}
