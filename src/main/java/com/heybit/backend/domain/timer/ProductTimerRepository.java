package com.heybit.backend.domain.timer;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductTimerRepository extends JpaRepository<ProductTimer, Long> {

  @Query("""
          select pt
          from ProductTimer pt
          where pt.user.id = :userId
            and pt.status in ('IN_PROGRESS', 'WAITING')
          order by 
            case pt.status when 'WAITING' then 0 else 1 end asc,
            pt.endTime desc
      """)
  List<ProductTimer> findUncompletedTimersByUserOrderByWaitingFirstAndEndTimeDesc(
      @Param("userId") Long userId);

}
