package com.heybit.backend.application.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.heybit.backend.domain.productinfo.Category;
import com.heybit.backend.domain.productinfo.ProductInfo;
import com.heybit.backend.domain.productinfo.ProductInfoRepository;
import com.heybit.backend.domain.timer.ProductTimer;
import com.heybit.backend.domain.timer.ProductTimerRepository;
import com.heybit.backend.domain.timer.TimerStatus;
import com.heybit.backend.domain.timerresult.ResultType;
import com.heybit.backend.domain.timerresult.TimerResult;
import com.heybit.backend.domain.timerresult.TimerResultRepository;
import com.heybit.backend.domain.user.Role;
import com.heybit.backend.domain.user.User;
import com.heybit.backend.domain.user.UserRepository;
import com.heybit.backend.domain.user.UserStatus;
import com.heybit.backend.domain.votepost.ProductVotePostRepository;
import com.heybit.backend.presentation.report.dto.MonthlyReportResponse;
import com.heybit.backend.presentation.report.dto.TotalReportResponse;
import com.heybit.backend.testsupport.MysqlTestContainerBase;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.YearMonth;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Transactional
class ReportServiceTest extends MysqlTestContainerBase {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private ProductInfoRepository productInfoRepository;

  @Autowired
  private ProductTimerRepository productTimerRepository;

  @Autowired
  private TimerResultRepository timerResultRepository;

  @Autowired
  private ProductVotePostRepository productVotePostRepository;

  @Autowired
  private ReportService reportService;

  private static final ObjectMapper mapper = new ObjectMapper();

  private User user;

  @BeforeEach
  void setUp() {
    user = userRepository.save(
        User.builder()
            .nickname("jun")
            .email("jun@example.com")
            .role(Role.USER)
            .status(UserStatus.ACTIVE)
            .build());

    // 테스트용 데이터 생성
    createTimer(
        Category.FOOD,
        2000,
        LocalDateTime.now().minusHours(12),
        LocalDateTime.now().minusMinutes(30),
        TimerStatus.COMPLETED,
        ResultType.SAVED);

    createTimer(
        Category.TRANSPORT,
        5000,
        LocalDateTime.now().minusHours(12),
        LocalDateTime.now().minusMinutes(30),
        TimerStatus.COMPLETED,
        ResultType.PURCHASED
    );

    createTimer(
        Category.CLOTHES,
        10000,
        LocalDateTime.now().minusHours(12),
        LocalDateTime.now().minusMinutes(10),
        TimerStatus.COMPLETED,
        ResultType.SAVED
    );

    createTimer(
        Category.FOOD,
        8000,
        LocalDateTime.now().minusHours(12),
        LocalDateTime.now().minusMinutes(30),
        TimerStatus.COMPLETED,
        ResultType.PURCHASED
    );
  }

  private Long createTimer(Category category,
      int amount,
      LocalDateTime start,
      LocalDateTime end,
      TimerStatus status,
      ResultType result
  ) {

    ProductInfo info = productInfoRepository.save(
        ProductInfo.builder()
            .name("상품-" + category)
            .amount(amount)
            .category(category)
            .build());

    ProductTimer timer = productTimerRepository.save(
        ProductTimer.builder()
            .startTime(start)
            .endTime(end)
            .status(status)
            .productInfo(info)
            .user(user)
            .build());

    timerResultRepository.save(
        TimerResult.builder()
            .productTimer(timer)
            .result(result)
            .savedAmount(result == ResultType.SAVED ? amount : 0)
            .consumedAmount(result == ResultType.PURCHASED ? amount : 0)
            .build());

    return timer.getId();
  }

  @Test
  @DisplayName("달별 리포트 조회 기능 테스트")
  void testMonthlyReport_August2025() throws JsonProcessingException {
    YearMonth currentMonth = YearMonth.now();

    //when
    MonthlyReportResponse response = reportService.getMonthlyReport(user.getId(), currentMonth);

    //then
    assertThat(response.getYear()).isEqualTo(currentMonth.getYear());
    assertThat(response.getMonth()).isEqualTo(currentMonth.getMonthValue());
    assertThat(response.getDailySummaries()).hasSize(1);

    // 성공률 확인
    assertThat(response.getSuccessRate().getSuccessRatePercent()).isGreaterThan(0);

    // 카테고리 실패 확인
    assertThat(response.getCategoryFailures())
        .anySatisfy(c -> assertThat(c.getCategory()).isEqualTo("TRANSPORT"));

    // 요일/시간대 집계 확인
    assertThat(response.getDayAndTimeFailures().getByWeekday())
        .containsKeys(
            "MONDAY", "TUESDAY", "WEDNESDAY",
            "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY"
        );
    assertThat(response.getDayAndTimeFailures().getByTimeZone())
        .containsKeys(
            "NIGHT", "MORNING", "LUNCH", "AFTERNOON", "EVENING"
        );

    System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(response));
  }

  @Test
  @DisplayName("전체 기간 리포트 조회 기능 테스트")
  void testTotalReport() throws JsonProcessingException {

    int currentYear = YearMonth.now().getYear();

    // when
    TotalReportResponse response = reportService.getTotalReport(user.getId());

    // then
    assertThat(response.getTotalSavedAmount()).isEqualTo(2000 + 10000);
    assertThat(response.getMonthSummaries())
        .anySatisfy(m -> assertThat(m.getYear()).isEqualTo(currentYear));
    assertThat(response.getSuccessRate().getSuccessRatePercent()).isGreaterThan(0);
    assertThat(response.getCategoryFailures())
        .anySatisfy(c -> assertThat(c.getCategory()).isEqualTo("FOOD"));

    System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(response));
  }

}

