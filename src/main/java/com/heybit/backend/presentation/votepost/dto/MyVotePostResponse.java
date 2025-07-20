package com.heybit.backend.presentation.votepost.dto;

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
  private int buyCount;
  private int holdCount;
  private int holdPercent;
}
