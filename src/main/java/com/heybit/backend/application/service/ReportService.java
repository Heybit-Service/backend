package com.heybit.backend.application.service;

import com.heybit.backend.domain.report.ReportRepository;
import com.heybit.backend.domain.report.stat.MonthSaveStat;
import com.heybit.backend.domain.report.stat.SuccessRateStat;
import com.heybit.backend.domain.report.stat.TimeZoneStat;
import com.heybit.backend.domain.report.stat.WeekdayStat;
import com.heybit.backend.presentation.report.dto.CategoryFailureResponse;
import com.heybit.backend.presentation.report.dto.CommonReportResponse;
import com.heybit.backend.presentation.report.dto.DailySummaryResponse;
import com.heybit.backend.presentation.report.dto.DayAndTimeFailuresResponse;
import com.heybit.backend.presentation.report.dto.MonthSummaryResponse;
import com.heybit.backend.presentation.report.dto.MonthlyReportResponse;
import com.heybit.backend.presentation.report.dto.SuccessRateResponse;
import com.heybit.backend.presentation.report.dto.TotalReportResponse;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReportService {

  private static final List<DayOfWeek> FIXED_WEEKDAYS =
      List.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
          DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY);

  private static final List<String> FIXED_TIMEZONES =
      List.of("NIGHT", "MORNING", "LUNCH", "AFTERNOON", "EVENING");

  private final ReportRepository reportRepository;
  private final UserService userService;

  public MonthlyReportResponse getMonthlyReport(Long userId, YearMonth month) {
    LocalDateTime start = month.atDay(1).atStartOfDay();
    LocalDateTime end = month.atEndOfMonth().atTime(LocalTime.MAX);

    CommonReportResponse reportStats = fetchReportStatistics(userId, start, end);

    List<DailySummaryResponse> dailySummaries =
        reportRepository.fetchDailySummaries(userId, start, end)
            .stream()
            .map(DailySummaryResponse::from)
            .collect(Collectors.toList());

    return MonthlyReportResponse.builder()
        .year(month.getYear())
        .month(month.getMonthValue())
        .dailySummaries(dailySummaries)
        .successRate(reportStats.getSuccessRate())
        .categoryFailures(reportStats.getCategoryFailures())
        .dayAndTimeFailures(reportStats.getDayAndTimeFailures())
        .build();
  }

  public TotalReportResponse getTotalReport(Long userId) {
    LocalDateTime joinedAt = userService.getById(userId).getCreatedAt();
    LocalDateTime start = joinedAt.toLocalDate().atStartOfDay();
    LocalDateTime end = LocalDateTime.now().with(LocalTime.MAX);

    CommonReportResponse reportStats = fetchReportStatistics(userId, start, end);

    List<MonthSaveStat> monthlySaves = reportRepository.fetchMonthlySaves(userId, start, end);
    List<MonthSummaryResponse> monthSummaries = monthlySaves.stream()
        .map(MonthSummaryResponse::from)
        .collect(Collectors.toList());

    return TotalReportResponse.builder()
        .totalSavedAmount(reportRepository.fetchTotalSaved(userId, start, end))
        .monthSummaries(monthSummaries)
        .successRate(reportStats.getSuccessRate())
        .categoryFailures(reportStats.getCategoryFailures())
        .dayAndTimeFailures(reportStats.getDayAndTimeFailures())
        .build();
  }

  private CommonReportResponse fetchReportStatistics(Long userId, LocalDateTime start,
      LocalDateTime end) {
    SuccessRateResponse successRate = fetchSuccessRate(userId, start, end);
    List<CategoryFailureResponse> categoryFailures = fetchCategoryFailures(userId, start, end);

    List<WeekdayStat> weekdayStats = reportRepository.fetchWeekdayStats(userId, start, end);
    List<TimeZoneStat> timeZoneStats = reportRepository.fetchTimeZoneStats(userId, start, end);
    DayAndTimeFailuresResponse dayAndTimeFailures = buildDayAndTimeFailures(weekdayStats,
        timeZoneStats);

    return new CommonReportResponse(successRate, categoryFailures, dayAndTimeFailures);
  }

  private SuccessRateResponse fetchSuccessRate(Long userId, LocalDateTime start,
      LocalDateTime end) {
    SuccessRateStat stat = reportRepository.fetchSuccessRate(userId, start, end);
    return SuccessRateResponse.from(stat);
  }

  private List<CategoryFailureResponse> fetchCategoryFailures(Long userId, LocalDateTime start,
      LocalDateTime end) {
    long totalFailCount = reportRepository.fetchTotalFailCount(userId, start, end);
    return reportRepository.fetchCategoryFailureStats(userId, start, end).stream()
        .map(stat -> CategoryFailureResponse.from(stat, totalFailCount))
        .collect(Collectors.toList());
  }

  private DayAndTimeFailuresResponse buildDayAndTimeFailures(List<WeekdayStat> weekdayStats,
      List<TimeZoneStat> timeZoneStats) {
    Map<String, Integer> weekdayMap = new LinkedHashMap<>();
    for (DayOfWeek day : FIXED_WEEKDAYS) {
      weekdayMap.put(day.name(), 0);
    }
    for (WeekdayStat s : weekdayStats) {
      if (weekdayMap.containsKey(s.getDayName())) {
        weekdayMap.put(s.getDayName(), s.getCount());
      }
    }

    Map<String, Integer> timeZoneMap = new LinkedHashMap<>();
    for (String timeZone : FIXED_TIMEZONES) {
      timeZoneMap.put(timeZone, 0);
    }
    for (TimeZoneStat stat : timeZoneStats) {
      if (timeZoneMap.containsKey(stat.getTimeSlot())) {
        timeZoneMap.put(stat.getTimeSlot(), stat.getCount());
      }
    }

    return DayAndTimeFailuresResponse.builder()
        .byWeekday(weekdayMap)
        .byTimeZone(timeZoneMap)
        .build();
  }
}

