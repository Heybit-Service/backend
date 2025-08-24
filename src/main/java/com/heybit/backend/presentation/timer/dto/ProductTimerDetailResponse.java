package com.heybit.backend.presentation.timer.dto;

import com.heybit.backend.domain.productinfo.ProductInfo;
import com.heybit.backend.domain.timer.ProductTimer;
import com.heybit.backend.presentation.vote.dto.VoteStatsDto;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ProductTimerDetailResponse {

  private String name;
  private String description;
  private String imageUrl;
  private int amount;
  private String status;
  private LocalDateTime endTime;
  private boolean withVotePost;
  //투표가 있다면
  private int buyCount;
  private int holdCount;
  private int holdPercent;

  public static ProductTimerDetailResponse from(ProductTimer timer, String status) {
    ProductInfo info = timer.getProductInfo();
    return ProductTimerDetailResponse.builder()
        .name(info.getName())
        .description(info.getDescription())
        .imageUrl(info.getImageUrl())
        .amount(info.getAmount())
        .status(status)
        .endTime(timer.getEndTime())
        .withVotePost(false)
        .build();
  }

  public static ProductTimerDetailResponse from(ProductTimer timer, VoteStatsDto dto, String status) {
    ProductInfo info = timer.getProductInfo();
    return ProductTimerDetailResponse.builder()
        .name(info.getName())
        .description(info.getDescription())
        .imageUrl(info.getImageUrl())
        .amount(info.getAmount())
        .status(status)
        .endTime(timer.getEndTime())
        .withVotePost(true)
        .buyCount(dto.getBuyCount())
        .holdCount(dto.getHoldCount())
        .holdPercent(dto.getHoldPercent())
        .build();
  }
}
