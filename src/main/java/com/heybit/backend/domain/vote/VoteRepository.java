package com.heybit.backend.domain.vote;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface VoteRepository extends JpaRepository<Vote, Long> {

  boolean existsByUserIdAndProductVotePostId(Long userId, Long postId);

  void deleteByUserIdAndProductVotePostId(Long userId, Long postId);

  void deleteByProductVotePostId(Long postId);

  @Query("""
          SELECT v.productVotePost.id AS postId,
                 SUM(CASE WHEN v.result = true THEN 1 ELSE 0 END) AS buyCount,
                 SUM(CASE WHEN v.result = false THEN 1 ELSE 0 END) AS holdCount
          FROM Vote v
          WHERE v.productVotePost.id IN :postIds
          GROUP BY v.productVotePost.id
      """)
  List<VoteStats> countBuyHoldByPostIds(List<Long> postIds);

  @Query("""
      SELECT v.productVotePost.id AS postId,
             SUM(CASE WHEN v.result = true THEN 1 ELSE 0 END) AS buyCount,
             SUM(CASE WHEN v.result = false THEN 1 ELSE 0 END) AS holdCount
      FROM Vote v
      WHERE v.productVotePost.id = :postId
      GROUP BY v.productVotePost.id
  """)
  Optional<VoteStats> countBuyHoldByPostId(Long postId);

  interface VoteStats {

    Long getPostId();

    Long getBuyCount();

    Long getHoldCount();
  }
}
