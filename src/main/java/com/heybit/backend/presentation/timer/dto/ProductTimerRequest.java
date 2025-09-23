package com.heybit.backend.presentation.timer.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.heybit.backend.domain.productinfo.Category;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
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
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssX")
  private OffsetDateTime startTime;

  @NotNull
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssX")
  private OffsetDateTime endTime;

  private boolean withVotePost;


  // TODO: 추후 아예 UTC 기반 처리로 확장 예정
  public LocalDateTime getStartTimeAsLocal() {
    return startTime.atZoneSameInstant(ZoneId.of("Asia/Seoul")).toLocalDateTime();
  }

  public LocalDateTime getEndTimeAsLocal() {
    return endTime.atZoneSameInstant(ZoneId.of("Asia/Seoul")).toLocalDateTime();
  }
}
