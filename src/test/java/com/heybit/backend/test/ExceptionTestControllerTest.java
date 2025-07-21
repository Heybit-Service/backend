package com.heybit.backend.test;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class ExceptionTestControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  @DisplayName("ApiException 예외 발생 시 ApiResponseEntity 형식으로 응답한다")
  void apiException_shouldReturnStructuredErrorResponse() throws Exception {
    mockMvc.perform(get("/test/api-exception"))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
        .andExpect(jsonPath("$.data").doesNotExist())
        .andDo(print());
  }

  @Test
  @DisplayName("RuntimeException 발생 시 ApiResponseEntity 형식으로 응답한다")
  void runtimeException_shouldReturnStructuredErrorResponse() throws Exception {
    mockMvc.perform(get("/test/runtime-exception"))
        .andExpect(status().isInternalServerError())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.status").value(500))
        .andExpect(jsonPath("$.message").value("내부 서버 오류가 발생했습니다."))
        .andExpect(jsonPath("$.data").doesNotExist())
        .andDo(print());
  }
}