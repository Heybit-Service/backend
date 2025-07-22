package com.heybit.backend.presentation.timer.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ProductTimerResponse {

  private Long timerId;
  private boolean active;
  private String name;
  private String description;
  private int amount;
  private LocalDateTime endTime;
  private boolean withVotePost;
}
