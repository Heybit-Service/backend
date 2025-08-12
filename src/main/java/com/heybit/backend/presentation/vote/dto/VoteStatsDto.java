package com.heybit.backend.presentation.vote.dto;

import com.heybit.backend.domain.vote.VoteRepository.VoteStats;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class VoteStatsDto {

  private final int buyCount;
  private final int holdCount;
  private final int holdPercent;

  public static VoteStatsDto empty() {
    return VoteStatsDto.builder()
        .buyCount(0)
        .holdCount(0)
        .holdPercent(0)
        .build();
  }

  public static VoteStatsDto from(VoteStats stats) {
    if (stats == null) {
      return empty();
    }
    int buyCount = stats.getBuyCount() != null ? stats.getBuyCount().intValue() : 0;
    int holdCount = stats.getHoldCount() != null ? stats.getHoldCount().intValue() : 0;

    return VoteStatsDto.builder()
        .buyCount(buyCount)
        .holdCount(holdCount)
        .holdPercent(calculateHoldPercent(buyCount, holdCount))
        .build();
  }

  private static int calculateHoldPercent(int buyCount, int holdCount) {
    int total = buyCount + holdCount;
    if (total == 0) {
      return 0;
    }
    double percent = holdCount * 100.0 / total;
    return (int) Math.round(percent);
  }

}
