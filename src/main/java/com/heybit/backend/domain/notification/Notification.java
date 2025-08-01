package com.heybit.backend.domain.notification;

import com.heybit.backend.domain.BaseTimeEntity;
import com.heybit.backend.domain.timer.ProductTimer;
import com.heybit.backend.domain.user.User;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Notification extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Long userId;

  @Enumerated(EnumType.STRING)
  private NotificationType type;

  private String title;

  private String body;

  private Long referenceId;

  private ReferenceType referenceType;

  private boolean isRead;
}
