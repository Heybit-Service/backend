package com.heybit.backend.presentation.vote.dto;

import com.heybit.backend.domain.productinfo.ProductInfo;
import com.heybit.backend.domain.timer.ProductTimer;
import com.heybit.backend.domain.vote.Vote;
import com.heybit.backend.domain.votepost.ProductVotePost;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class VotedPostResponse {

  private Long votePostId;
  private String name;
  private int amount;
  private String myVote;
  private String status;
  private Long holdCount;
  private Long buyCount;
  private LocalDate votedAt;

  public static VotedPostResponse of(Vote vote, Long buyCount, Long stopCount, String resultString) {
    ProductVotePost votePost = vote.getProductVotePost();
    ProductTimer timer = votePost.getProductTimer();
    ProductInfo info = timer.getProductInfo();

    return VotedPostResponse.builder()
        .name(info.getName())
        .amount(info.getAmount())
        .myVote(vote.getResult().name())
        .buyCount(buyCount)
        .holdCount(stopCount)
        .status(resultString)
        .votedAt(vote.getCreatedAt().toLocalDate())
        .build();
  }
}
