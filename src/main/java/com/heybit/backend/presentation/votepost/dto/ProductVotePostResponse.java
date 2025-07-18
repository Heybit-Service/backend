package com.heybit.backend.presentation.votepost.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ProductVotePostResponse {

  private Long votePostId;
  private String name;
  private String imageUrl;
  private int amount;
  private String description;
  private String writer;
  private LocalDateTime endTime;
}
