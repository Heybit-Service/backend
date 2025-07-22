package com.heybit.backend.presentation.timerresult.dto;

import com.heybit.backend.domain.timerresult.ResultType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class TimerResultRequest {
  private Long productTimerId;
  private ResultType result;
  private int amount;
  private String userComment;
}
