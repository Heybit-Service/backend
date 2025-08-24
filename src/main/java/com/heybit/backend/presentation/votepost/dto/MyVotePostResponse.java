package com.heybit.backend.presentation.votepost.dto;

import com.heybit.backend.domain.productinfo.ProductInfo;
import com.heybit.backend.domain.timer.ProductTimer;
import com.heybit.backend.domain.timer.TimerStatus;
import com.heybit.backend.domain.vote.VoteRepository.VoteStats;
import com.heybit.backend.domain.votepost.ProductVotePost;
import com.heybit.backend.presentation.vote.dto.VoteStatsDto;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class MyVotePostResponse {

  private String name;
  private String description;
  private int amount;
  private String imageUrl;
  private boolean inProgress;
  private LocalDateTime endTime;
  private VoteStatsDto voteStats;

  public static MyVotePostResponse from(ProductVotePost post, VoteStats stats) {
    ProductTimer timer = post.getProductTimer();
    ProductInfo info = timer.getProductInfo();
    VoteStatsDto dto = (stats != null) ? VoteStatsDto.from(stats) : VoteStatsDto.empty();
    TimerStatus status = timer.getStatus();

    return MyVotePostResponse.builder()
        .name(info.getName())
        .description(info.getDescription())
        .amount(info.getAmount())
        .imageUrl(info.getImageUrl())
        .voteStats(dto)
        .inProgress (status == TimerStatus.IN_PROGRESS)
        .endTime(timer.getEndTime())
        .build();
  }
}
