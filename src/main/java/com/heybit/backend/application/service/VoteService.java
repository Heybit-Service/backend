package com.heybit.backend.application.service;

import com.heybit.backend.domain.user.User;
import com.heybit.backend.domain.user.UserRepository;
import com.heybit.backend.domain.vote.Vote;
import com.heybit.backend.domain.vote.VoteRepository;
import com.heybit.backend.domain.votepost.ProductVotePost;
import com.heybit.backend.domain.votepost.ProductVotePostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VoteService {

  private final VoteRepository voteRepository;
  private final ProductVotePostRepository votePostRepository;
  private final UserRepository userRepository;

  @Transactional
  public void vote(Long votePostId, Long userId, boolean result) {
    ProductVotePost votePost = votePostRepository.findById(votePostId)
        .orElseThrow(() -> new IllegalArgumentException("존재한지 않는 투표글입니다"));

    User user = userRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다"));

    // 중복 투표 방지
    if (voteRepository.existsByUserIdAndProductVotePostId(userId, votePostId)) {
      throw new IllegalStateException("이미 투표한 글입니다");
    }

    voteRepository.save(
        Vote.builder()
            .user(user)
            .productVotePost(votePost)
            .result(result)
            .build()
    );
  }

  @Transactional
  public void cancelVote(Long votePostId, Long userId) {

    voteRepository.deleteByUserIdAndProductVotePostId(userId, votePostId);
  }
}
