package com.heybit.backend.domain.notification;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

  List<Notification> findAllByUserIdOrderByCreatedAtDesc(Long userId);

  List<Notification> findAllByUserIdAndViewedFalse(Long userId);

  List<Notification> findAllByUserId(Long attr0);

  boolean existsByUserIdAndViewedFalse(Long userId);
}
