package com.heybit.backend.infrastructure.fcm;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FirebaseConfig {

  @Value("${firebase.config.base64}")
  private String base64Config;

  @PostConstruct
  public void init() throws IOException {
    if (FirebaseApp.getApps().isEmpty()) {
      byte[] decodedBytes = Base64.getDecoder().decode(base64Config);
      try (InputStream serviceAccount = new ByteArrayInputStream(decodedBytes)) {
        FirebaseOptions options = FirebaseOptions.builder()
            .setCredentials(GoogleCredentials.fromStream(serviceAccount))
            .build();
        FirebaseApp.initializeApp(options);
      }
    }
  }
}

