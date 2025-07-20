package com.heybit.backend.presentation.votepost.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class MyVotedPostResponse {

  private Long votePostId;
  private String name;
  private int amount;
  private String imageUrl;
  private boolean myVote;
  private boolean isProgress;
  private boolean result;  // 결과가 있다면
}
