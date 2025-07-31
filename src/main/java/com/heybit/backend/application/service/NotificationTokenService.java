package com.heybit.backend.application.service;

import com.heybit.backend.domain.notificationtoken.NotificationToken;
import com.heybit.backend.domain.notificationtoken.NotificationTokenRepository;
import com.heybit.backend.domain.notificationtoken.OsType;
import com.heybit.backend.domain.user.User;
import com.heybit.backend.domain.user.UserRepository;
import com.heybit.backend.global.exception.ApiException;
import com.heybit.backend.global.exception.ErrorCode;
import com.heybit.backend.presentation.notificationtoken.dto.NotificationTokenRequest;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationTokenService {

  private final NotificationTokenRepository tokenRepository;
  private final UserRepository userRepository;

  @Transactional
  public void saveOrUpdateToken(Long userId, String token, OsType osType) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

    Optional<NotificationToken> existingToken =
        tokenRepository.findByUserIdAndOsType(userId, osType);

    if (existingToken.isPresent()) {
      existingToken.get().updateToken(token);
    } else {
      NotificationToken newToken = NotificationToken.builder()
          .user(user)
          .token(token)
          .osType(osType)
          .build();
      tokenRepository.save(newToken);
    }
  }

  public Map<OsType, String> getTokensByOsType(Long userId) {
    return tokenRepository.findAllByUserId(userId).stream()
        .collect(Collectors.toMap(NotificationToken::getOsType, NotificationToken::getToken));
  }
}
