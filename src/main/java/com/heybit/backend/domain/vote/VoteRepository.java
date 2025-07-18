package com.heybit.backend.domain.vote;

import org.springframework.data.jpa.repository.JpaRepository;

public interface VoteRepository extends JpaRepository<Vote, Long> {

  boolean existsByUserIdAndProductVotePostId(Long userId, Long postId);

  void deleteByUserIdAndProductVotePostId(Long userId, Long postId);
}
