package com.heybit.backend.domain.notificationtoken;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationTokenRepository extends JpaRepository<NotificationToken, Long> {

  Optional<NotificationToken> findByUserIdAndOsType(Long user_id, OsType osType);

  List<NotificationToken> findAllByUserId(Long userId);
}
