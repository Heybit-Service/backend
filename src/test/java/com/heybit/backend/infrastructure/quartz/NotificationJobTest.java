package com.heybit.backend.infrastructure.quartz;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.heybit.backend.application.service.NotificationService;
import com.heybit.backend.domain.notification.NotificationType;
import com.heybit.backend.domain.productinfo.Category;
import com.heybit.backend.domain.productinfo.ProductInfo;
import com.heybit.backend.domain.productinfo.ProductInfoRepository;
import com.heybit.backend.domain.timer.ProductTimer;
import com.heybit.backend.domain.timer.ProductTimerRepository;
import com.heybit.backend.domain.timer.TimerStatus;
import com.heybit.backend.domain.user.Role;
import com.heybit.backend.domain.user.User;
import com.heybit.backend.domain.user.UserRepository;
import com.heybit.backend.domain.user.UserStatus;
import com.heybit.backend.domain.votepost.ProductVotePost;
import com.heybit.backend.domain.votepost.ProductVotePostRepository;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class NotificationJobTest {

  @Autowired
  private Scheduler scheduler;

  @Autowired
  private ProductTimerRepository productTimerRepository;

  @Autowired
  private ProductInfoRepository productInfoRepository;

  @Autowired
  private ProductVotePostRepository productVotePostRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private NotificationService notificationService;

  @Autowired
  private EntityManager em;

  private User user;

  @BeforeEach
  void setUp() {
    user = userRepository.save(User.builder()
        .nickname("jun")
        .email("jun@example.com")
        .role(Role.USER)
        .status(UserStatus.ACTIVE)
        .build());
  }

  @AfterEach
  void cleanUp() {
    productVotePostRepository.deleteAll();
    productTimerRepository.deleteAll();
    productInfoRepository.deleteAll();
    userRepository.deleteAll();
  }

  private ProductTimer createTimer(
      String name,
      int amount,
      LocalDateTime start,
      LocalDateTime endTime,
      boolean withVotePost
  ) {
    var productInfo = productInfoRepository.save(
        ProductInfo.builder()
            .name(name)
            .amount(amount)
            .category(Category.ETC)
            .build());

    var timer = productTimerRepository.save(
        ProductTimer.builder()
            .startTime(start)
            .endTime(endTime)
            .status(TimerStatus.IN_PROGRESS)
            .productInfo(productInfo)
            .user(user)
            .build());

    if (withVotePost) {
      productVotePostRepository.save(
          ProductVotePost.builder()
              .productTimer(timer)
              .build());
    }

    return timer;
  }

  @Test
  void testNotificationJobExecutesSuccessfully() throws Exception {
    // given
    ProductTimer timer = createTimer("타이머1",
        10000,
        LocalDateTime.now(),
        LocalDateTime.now().plusHours(1),
        true
    );

    JobDataMap jobDataMap = new JobDataMap();
    jobDataMap.put("timerId", timer.getId());
    jobDataMap.put("type", NotificationType.COMPLETED);

    JobDetail jobDetail = JobBuilder.newJob(NotificationJob.class)
        .withIdentity("testJob", "testGroup")
        .usingJobData(jobDataMap)
        .storeDurably()
        .build();

    Trigger trigger = TriggerBuilder.newTrigger()
        .forJob(jobDetail)
        .startNow()
        .build();

    // When
    scheduler.scheduleJob(jobDetail, trigger);

    // Then
    Thread.sleep(3000); // Job 실행 기다림 (비동기니까 약간 대기 필요)

    em.clear();

    ProductTimer updatedTimer = productTimerRepository.findById(timer.getId())
        .orElseThrow(() -> new IllegalStateException("타이머 없음"));
    assertEquals(TimerStatus.WAITING, updatedTimer.getStatus()); // 타이머가 완료되면 결과입력를 대기하는 상태로 변함
  }

}