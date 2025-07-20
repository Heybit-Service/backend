package com.heybit.backend.domain.timer;


import com.heybit.backend.domain.BaseTimeEntity;
import com.heybit.backend.domain.productinfo.ProductInfo;
import com.heybit.backend.domain.user.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductTimer extends BaseTimeEntity {

  @Id
  @GeneratedValue
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  private User user;

  @OneToOne
  @JoinColumn(name = "product_info_id", unique = true)
  private ProductInfo productInfo;

  private LocalDateTime startTime;
  private LocalDateTime endTime;

  @Enumerated(EnumType.STRING)
  private TimerStatus status;

  public void updateState(TimerStatus status) {
    this.status = status;
  }
}
