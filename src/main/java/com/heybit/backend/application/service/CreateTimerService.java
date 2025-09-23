package com.heybit.backend.application.service;

import com.heybit.backend.application.scheduler.TimerNotificationScheduler;
import com.heybit.backend.application.usecase.CreateTimerUseCase;
import com.heybit.backend.domain.productinfo.ProductInfo;
import com.heybit.backend.domain.timer.ProductTimer;
import com.heybit.backend.domain.timer.TimerStatus;
import com.heybit.backend.domain.user.User;
import com.heybit.backend.domain.votepost.ProductVotePost;
import com.heybit.backend.global.exception.ApiException;
import com.heybit.backend.global.exception.ErrorCode;
import com.heybit.backend.infrastructure.s3.S3UploadComponent;
import com.heybit.backend.presentation.timer.dto.ProductTimerRequest;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class CreateTimerService implements CreateTimerUseCase {

  private final UserService userService;
  private final ProductInfoService productInfoService;
  private final ProductVotePostService productVotePostService;
  private final ProductTimerService productTimerService;
  private final S3UploadComponent s3UploadComponent;
  private final TimerNotificationScheduler timerNotificationScheduler;

  @Override
  public Long execute(ProductTimerRequest request, Long userId, MultipartFile imageFile) throws IOException {
    LocalDateTime startTime = request.getStartTimeAsLocal();
    LocalDateTime endTime = request.getEndTimeAsLocal();

    validateTimes(startTime, endTime);

    // 이미지가 존재할 경우 업로드
    String imageUrl = null;
    if (imageFile != null && !imageFile.isEmpty()) {
      imageUrl = s3UploadComponent.upload(imageFile, userId);
    }

    User user = userService.getById(userId);

    ProductInfo info = ProductInfo.builder()
        .name(request.getName())
        .amount(request.getAmount())
        .category(request.getCategory())
        .imageUrl(imageUrl)
        .description(request.getDescription())
        .build();

    ProductTimer timer = ProductTimer.builder()
        .user(user)
        .productInfo(info)
        .startTime(startTime)
        .endTime(endTime)
        .status(TimerStatus.IN_PROGRESS)
        .build();

    productInfoService.save(info);
    productTimerService.save(timer);

    if (request.isWithVotePost()) {
      ProductVotePost votePost = ProductVotePost.builder()
          .productTimer(timer)
          .build();

      productVotePostService.save(votePost);
    }

    timerNotificationScheduler.scheduleTimerNotificationJob(
        timer.getStartTime(),
        timer.getEndTime(),
        timer.getId(),
        info.getName() // ==  timer.getProductInfo.getName()
    );

    return timer.getId();
  }

  private void validateTimes(LocalDateTime startTime, LocalDateTime endTime) {
    ZoneId kstZone = ZoneId.of("Asia/Seoul");
    LocalDateTime nowKST = LocalDateTime.now(kstZone);

    if (!startTime.isBefore(endTime)) {
      throw new ApiException(ErrorCode.INVALID_TIMER_TIME);
    }

    if (!endTime.isAfter(nowKST)) {
      throw new ApiException(ErrorCode.INVALID_TIMER_TIME);
    }
  }
}

