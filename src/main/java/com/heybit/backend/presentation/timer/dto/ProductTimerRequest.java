package com.heybit.backend.presentation.timer.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.heybit.backend.domain.productinfo.Category;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductTimerRequest {

  @NotBlank
  private String name;

  @NotNull
  @Min(value = 0)
  private Integer amount;

  @Size(max = 80)
  private String description;

  @NotNull
  private Category category;

  @NotNull
  @Future
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime startTime;

  @NotNull
  @Future
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime endTime;

  private boolean withVotePost;

  private MultipartFile imageFile;
}
