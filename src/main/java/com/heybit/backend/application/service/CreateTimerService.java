package com.heybit.backend.application.service;

import com.heybit.backend.application.scheduler.TimerNotificationScheduler;
import com.heybit.backend.application.usecase.CreateTimerUseCase;
import com.heybit.backend.domain.productinfo.ProductInfo;
import com.heybit.backend.domain.timer.ProductTimer;
import com.heybit.backend.domain.user.User;
import com.heybit.backend.domain.votepost.ProductVotePost;
import com.heybit.backend.infrastructure.s3.S3UploadComponent;
import com.heybit.backend.presentation.timer.dto.ProductTimerRequest;
import java.io.IOException;
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
        .startTime(request.getStartTime())
        .endTime(request.getEndTime())
        .build();

    productInfoService.save(info);
    productTimerService.save(timer);

    if (request.isWithVotePost()) {
      ProductVotePost votePost = ProductVotePost.builder()
          .productTimer(timer)
          .build();

      productVotePostService.save(votePost);
    }

    // 타이머에 대한 알림 예약 스케줄링
    timerNotificationScheduler.scheduleTimerNotifications(timer);

    return timer.getId();
  }
}
