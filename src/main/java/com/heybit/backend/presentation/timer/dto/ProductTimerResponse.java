package com.heybit.backend.presentation.timer.dto;

import com.heybit.backend.domain.timer.ProductTimer;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ProductTimerResponse {

  private Long timerId;
  private String status;;
  private String name;
  private String description;
  private int amount;
  private LocalDateTime endTime;
  private boolean withVotePost;

  public static ProductTimerResponse from(ProductTimer timer, boolean withVotePost) {
    return ProductTimerResponse.builder()
        .timerId(timer.getId())
        .status(timer.getStatus().name())
        .name(timer.getProductInfo().getName())
        .description(timer.getProductInfo().getDescription())
        .amount(timer.getProductInfo().getAmount())
        .endTime(timer.getEndTime())
        .withVotePost(withVotePost)
        .build();
  }
}
