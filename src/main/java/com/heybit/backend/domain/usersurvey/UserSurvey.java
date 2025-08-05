package com.heybit.backend.domain.usersurvey;

import com.heybit.backend.domain.BaseTimeEntity;
import com.heybit.backend.domain.user.User;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Generated;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserSurvey extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToOne(fetch = FetchType.LAZY)
  private User user;

  @Enumerated(EnumType.STRING)
  private ConsumptionTime consumptionTime;

  @Enumerated(EnumType.STRING)
  private ImpulseFrequency impulseFrequency;

  @Enumerated(EnumType.STRING)
  private PurchaseTrigger purchaseTrigger;

  @Enumerated(EnumType.STRING)
  private HabitImprovementReason improvementReason;

}
