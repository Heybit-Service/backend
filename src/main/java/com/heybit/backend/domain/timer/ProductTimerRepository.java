package com.heybit.backend.domain.timer;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductTimerRepository extends JpaRepository<ProductTimer, Long> {

  // IN_PROGRESS 상태인 타이머 중 종료된 것(endTime 이후, 타이머 결과가 등록되어야 완료로 상태가 변경됨)은 먼저 그 후 생성 순으로 정렬
  @Query("""
          select pt
          from ProductTimer pt
          where pt.user.id = :userId
            and pt.status = 'IN_PROGRESS'
          order by 
            case when pt.endTime < current_timestamp then 0 else 1 end asc,  
            pt.createdAt asc
      """)
  List<ProductTimer> findProgressTimersOrderByPriority(@Param("userId") Long userId);

}
