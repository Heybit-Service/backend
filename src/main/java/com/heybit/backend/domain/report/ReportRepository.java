package com.heybit.backend.domain.report;

import com.heybit.backend.domain.report.stat.CategoryFailureStat;
import com.heybit.backend.domain.report.stat.DailySummaryStat;
import com.heybit.backend.domain.report.stat.MonthSaveStat;
import com.heybit.backend.domain.report.stat.SuccessRateStat;
import com.heybit.backend.domain.report.stat.TimeZoneStat;
import com.heybit.backend.domain.report.stat.WeekdayStat;
import com.heybit.backend.domain.timerresult.TimerResult;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReportRepository extends JpaRepository<TimerResult, Long> {

  // 일별 합계
  @Query("""
          SELECT DATE(tr.createdAt) AS date,
                 COALESCE(SUM(tr.savedAmount), 0) AS savedAmount,
                 COALESCE(SUM(tr.consumedAmount), 0) AS consumedAmount
          FROM TimerResult tr
          JOIN tr.productTimer pt
          WHERE pt.user.id = :userId
            AND tr.createdAt BETWEEN :start AND :end
          GROUP BY DATE(tr.createdAt)
          ORDER BY DATE(tr.createdAt)
      """)
  List<DailySummaryStat> fetchDailySummaries(@Param("userId") Long userId,
      @Param("start") LocalDateTime start,
      @Param("end") LocalDateTime end);

  // 타이머 성공률
  @Query("""
      SELECT CASE WHEN COUNT(tr) = 0 THEN 0.0
                  ELSE (SUM(CASE WHEN tr.result = 'SAVED' THEN 1 ELSE 0 END) * 1.0 / COUNT(tr)) * 100.0
             END AS successRate,
             COUNT(tr) AS totalCount,
             COALESCE(SUM(CASE WHEN tr.result = 'SAVED' THEN 1 ELSE 0 END), 0) AS successCount
      FROM TimerResult tr
      JOIN tr.productTimer pt
      WHERE pt.user.id = :userId
        AND tr.createdAt BETWEEN :start AND :end
      """)
  SuccessRateStat fetchSuccessRate(@Param("userId") Long userId,
      @Param("start") LocalDateTime start,
      @Param("end") LocalDateTime end);

  // 카테고리별 실패 통계
  @Query("""
      SELECT pi.category AS category,
             COUNT(tr) AS failCount,
             SUM(COALESCE(tr.consumedAmount, 0)) AS failAmount
      FROM TimerResult tr
      JOIN tr.productTimer pt
      JOIN pt.productInfo pi
      WHERE pt.user.id = :userId
        AND tr.result = 'PURCHASED'
        AND tr.createdAt BETWEEN :start AND :end
      GROUP BY pi.category
      ORDER BY COUNT(tr) DESC
      """)
  List<CategoryFailureStat> fetchCategoryFailureStats(@Param("userId") Long userId,
      @Param("start") LocalDateTime start,
      @Param("end") LocalDateTime end);

  // 전체 실패 횟수
  @Query("""
      SELECT COUNT(tr)
      FROM TimerResult tr
      JOIN tr.productTimer pt
      WHERE pt.user.id = :userId
        AND tr.result = 'PURCHASED'
        AND tr.createdAt BETWEEN :start AND :end
      """)
  long fetchTotalFailCount(@Param("userId") Long userId,
      @Param("start") LocalDateTime start,
      @Param("end") LocalDateTime end);

  // 요일별 타이머 등록 수
  @Query("""
      SELECT FUNCTION('DAYNAME', pt.createdAt) AS dayName,
             COUNT(pt) AS count
      FROM ProductTimer pt
      JOIN pt.user u
      WHERE u.id = :userId
        AND pt.createdAt BETWEEN :start AND :end
      GROUP BY FUNCTION('DAYNAME', pt.createdAt)
      """)
  List<WeekdayStat> fetchWeekdayStats(@Param("userId") Long userId,
      @Param("start") LocalDateTime start,
      @Param("end") LocalDateTime end);

  // 시간대별 타이머 등록 수
  @Query("""
      SELECT CASE
               WHEN FUNCTION('HOUR', pt.createdAt) BETWEEN 0 AND 5 THEN 'NIGHT'
               WHEN FUNCTION('HOUR', pt.createdAt) BETWEEN 6 AND 10 THEN 'MORNING'
               WHEN FUNCTION('HOUR', pt.createdAt) BETWEEN 11 AND 13 THEN 'LUNCH'
               WHEN FUNCTION('HOUR', pt.createdAt) BETWEEN 14 AND 16 THEN 'AFTERNOON'
               WHEN FUNCTION('HOUR', pt.createdAt) BETWEEN 17 AND 19 THEN 'EVENING'
               ELSE 'NIGHT'
             END AS timeSlot,
             COUNT(pt) AS count
      FROM ProductTimer pt
      JOIN pt.user u
      WHERE u.id = :userId
        AND pt.createdAt BETWEEN :start AND :end
      GROUP BY timeSlot
      """)
  List<TimeZoneStat> fetchTimeZoneStats(@Param("userId") Long userId,
      @Param("start") LocalDateTime start,
      @Param("end") LocalDateTime end);

  // 월별 SAVE 금액
  @Query("""
      SELECT FUNCTION('YEAR', tr.createdAt) AS year,
             FUNCTION('MONTH', tr.createdAt) AS month,
             COALESCE(SUM(tr.savedAmount), 0) AS savedAmount
      FROM TimerResult tr
      JOIN tr.productTimer pt
      WHERE pt.user.id = :userId
        AND tr.createdAt BETWEEN :start AND :end
      GROUP BY FUNCTION('YEAR', tr.createdAt), FUNCTION('MONTH', tr.createdAt)
      ORDER BY FUNCTION('YEAR', tr.createdAt), FUNCTION('MONTH', tr.createdAt)
      """)
  List<MonthSaveStat> fetchMonthlySaves(@Param("userId") Long userId,
      @Param("start") LocalDateTime start,
      @Param("end") LocalDateTime end);

  // 총 SAVE 금액
  @Query("""
      SELECT COALESCE(SUM(tr.savedAmount), 0)
      FROM TimerResult tr
      JOIN tr.productTimer pt
      WHERE pt.user.id = :userId
        AND tr.createdAt BETWEEN :start AND :end
      """)
  long fetchTotalSaved(@Param("userId") Long userId,
      @Param("start") LocalDateTime start,
      @Param("end") LocalDateTime end);
}
