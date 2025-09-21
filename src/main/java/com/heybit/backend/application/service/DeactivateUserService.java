package com.heybit.backend.application.service;

import com.heybit.backend.application.usecase.DeactivateUserUseCase;
import com.heybit.backend.domain.timer.ProductTimer;
import com.heybit.backend.domain.user.User;
import com.heybit.backend.domain.user.UserRepository;
import com.heybit.backend.infrastructure.quartz.NotificationJobSchedulerFactory;
import com.heybit.backend.infrastructure.s3.S3UploadComponent;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeactivateUserService implements DeactivateUserUseCase {

  private final UserService userService;
  private final ProductTimerService productTimerService;
  private final ProductVotePostService votePostService;
  private final NotificationJobSchedulerFactory jobSchedulerFactory;
  private final S3UploadComponent s3UploadComponent;

  @Transactional
  public void deactivate(Long userId) {
    User user = userService.getById(userId);

    List<ProductTimer> timers = productTimerService.findAllByUserId(userId);

    for (ProductTimer timer : timers) {
      if (timer.getProductInfo().getImageUrl() != null) {
        s3UploadComponent.delete(timer.getProductInfo().getImageUrl());
      }

      jobSchedulerFactory.cancelAllByTimerId(timer.getId());

      votePostService.deleteVotePostWithVotesByTimerId(timer.getId());

      productTimerService.delete(timer);
    }
    user.deleteUser();
  }

}
