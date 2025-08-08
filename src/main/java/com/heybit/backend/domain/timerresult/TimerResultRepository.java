package com.heybit.backend.domain.timerresult;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TimerResultRepository extends JpaRepository<TimerResult, Long> {

  List<TimerResult> findByProductTimerIdIn(List<Long> timerIds);

  boolean existsByProductTimerId(Long productTimerId);

  Optional<TimerResult> findByProductTimerId(Long id);

  @Query("""
      SELECT r
      FROM TimerResult r
      JOIN FETCH r.productTimer t
      JOIN FETCH t.productInfo i
      WHERE t.user.id = :userId
        AND (t.status = 'COMPLETED' OR t.status = 'ABANDONED')
      """)
  List<TimerResult> findCompletedResultsOfTimerWithInfoByUserId(Long userId);
}
