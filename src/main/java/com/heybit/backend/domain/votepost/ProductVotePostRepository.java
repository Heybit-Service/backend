package com.heybit.backend.domain.votepost;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductVotePostRepository extends JpaRepository<ProductVotePost, Long> {

  Optional<ProductVotePost> findByProductTimerId(Long aLong);

  // 참여한 투표는 제외한 진행중인 타이머에 대한 투표 최신 등록순 조회 쿼리
  @Query("""
          SELECT p FROM ProductVotePost p
          JOIN FETCH p.productTimer t
          JOIN FETCH t.productInfo
          LEFT JOIN Vote v ON v.productVotePost.id=p.id AND v.user.id= :userId
          WHERE t.status = 'IN_PROGRESS'
            AND v.id IS NULL
          ORDER BY t.startTime DESC
      """)
  List<ProductVotePost> findInProgressPostsUserNotVoted(@Param("userId") Long userId);

  // 모든 진행중인 타이머에 대한 투표 최신 등록순 조회 쿼리 (정렬기준 내가 투표 안한 거 먼저, 한 거는 나중에 )
  @Query("""
            SELECT p FROM ProductVotePost p
            JOIN FETCH p.productTimer t
            JOIN FETCH t.productInfo
            LEFT JOIN Vote v ON v.productVotePost.id=p.id AND v.user.id= :userId
            WHERE t.status = 'IN_PROGRESS'
            ORDER BY
                CASE WHEN v.id IS NULL THEN 0 ELSE 1 END, t.startTime DESC
      """)
  List<ProductVotePost> findInProgressPostsOrderByNotVotedFirst(@Param("userId") Long userId);

  // 내 등록한 투표글의 현황 조회 쿼리
  @Query("""
          SELECT p FROM ProductVotePost p
          JOIN FETCH p.productTimer t
          JOIN FETCH t.productInfo
          WHERE t.user.id = :userId
            AND t.status = 'IN_PROGRESS'
      """)
  List<ProductVotePost> findMyInProgressPosts(@Param("userId") Long userId);

  boolean existsByProductTimerId(Long id);

  void deleteByProductTimerId(Long timerId);

  Collection<ProductVotePost> findAllByProductTimerIdIn(List<Long> timerIds);
}

