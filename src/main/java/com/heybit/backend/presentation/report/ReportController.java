package com.heybit.backend.presentation.report;

import com.heybit.backend.application.service.ReportService;
import com.heybit.backend.global.response.ApiResponseEntity;
import com.heybit.backend.presentation.report.dto.MonthlyReportResponse;
import com.heybit.backend.presentation.report.dto.TotalReportResponse;
import com.heybit.backend.security.oauth.LoginUser;
import java.time.YearMonth;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RequiredArgsConstructor
@RequestMapping("api/v1/reports")
public class ReportController {

  private final ReportService reportService;

  @GetMapping("/monthly")
  public MonthlyReportResponse getMonthlyReport(
      @LoginUser Long userId,
      @RequestParam @DateTimeFormat(pattern = "yyyy-MM") YearMonth month
  ) {
    return reportService.getMonthlyReport(userId, month);
  }

  @GetMapping("/total")
  public ApiResponseEntity<TotalReportResponse> getTotalReport(@LoginUser Long userId) {
    return ApiResponseEntity.success(reportService.getTotalReport(userId));
  }

}
