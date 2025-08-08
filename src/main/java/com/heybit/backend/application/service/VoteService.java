package com.heybit.backend.application.service;

import com.heybit.backend.domain.timer.ProductTimer;
import com.heybit.backend.domain.timer.TimerStatus;
import com.heybit.backend.domain.timerresult.ResultType;
import com.heybit.backend.domain.timerresult.TimerResult;
import com.heybit.backend.domain.user.User;
import com.heybit.backend.domain.user.UserRepository;
import com.heybit.backend.domain.vote.Vote;
import com.heybit.backend.domain.vote.VoteRepository;
import com.heybit.backend.domain.vote.VoteRepository.VoteStats;
import com.heybit.backend.domain.vote.VoteResultType;
import com.heybit.backend.domain.votepost.ProductVotePost;
import com.heybit.backend.domain.votepost.ProductVotePostRepository;
import com.heybit.backend.global.exception.ApiException;
import com.heybit.backend.global.exception.ErrorCode;
import com.heybit.backend.global.resolver.VotePostStatusResolver;
import com.heybit.backend.presentation.vote.dto.VotedPostResponse;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VoteService {

  private final VoteRepository voteRepository;
  private final ProductVotePostRepository votePostRepository;
  private final UserService userService;
  private final TimerResultService timerResultService;

  @Transactional
  public void vote(Long votePostId, Long userId, VoteResultType result) {
    ProductVotePost votePost = votePostRepository.findById(votePostId)
        .orElseThrow(() -> new ApiException(ErrorCode.VOTE_POST_NOT_FOUND));

    User user = userService.getById(userId);

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

  @Transactional(readOnly = true)
  public List<VotedPostResponse> getMyVotedPosts(Long userId) {

    List<Vote> votes = voteRepository.findAllByUserIdWithPostAndTimer(userId);

    // VoteStats 집계
    List<Long> postIds = votes.stream()
        .map(v -> v.getProductVotePost().getId())
        .distinct()
        .toList();

    Map<Long, VoteStats> statsMap = voteRepository.countBuyHoldByPostIds(postIds).stream()
        .collect(Collectors.toMap(VoteStats::getPostId, v -> v));

    // TimerResult 상태 Map 생성
    List<Long> timerIds = votes.stream()
        .map(v -> v.getProductVotePost().getProductTimer().getId())
        .distinct()
        .toList();

    Map<Long, ResultType> resultStatusMap = timerResultService.getResultTypeMapByTimerIds(timerIds);

    // 각 투표글에 대한 정보를 조합하여 응답 객체 생성
    return votes.stream()
        .map(v -> {
          Long postId = v.getProductVotePost().getId();
          Long timerId = v.getProductVotePost().getProductTimer().getId();

          VoteStats stats = statsMap.get(postId);
          ResultType resultStatus = resultStatusMap.get(timerId);
          String statusString = VotePostStatusResolver.resolveStatus(
              v.getProductVotePost().getProductTimer().getStatus(),
              resultStatus
          );

          return VotedPostResponse.of(v, stats.getBuyCount(), stats.getHoldCount(), statusString);
        })
        .toList();
  }

  @Transactional
  public void cancelVote(Long votePostId, Long userId) {

    voteRepository.deleteByUserIdAndProductVotePostId(userId, votePostId);
  }
}
