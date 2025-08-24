package com.heybit.backend.presentation.report.dto;

import com.heybit.backend.domain.productinfo.Category;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TopCategoryAmountResponse {

  private final Category category;
  private final long totalAmount;

}
