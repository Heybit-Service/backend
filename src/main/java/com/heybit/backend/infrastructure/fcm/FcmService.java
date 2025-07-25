package com.heybit.backend.infrastructure.fcm;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class FcmService {

  public void sendMessage() {
    //TODO: FCM 로직 작성
    log.info("Sending message");
  }
}
