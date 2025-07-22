package com.heybit.backend.domain.timerresult;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TimerResultRepository extends JpaRepository<TimerResult, Long> {

  boolean existsByProductTimerId(Long productTimerId);

  Optional<TimerResult> findByProductTimerId(Long id);
}
