package com.heybit.backend.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

  INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "내부 서버 오류가 발생했습니다."),
  INVALID_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
  MALFORMED_JSON(HttpStatus.BAD_REQUEST, "잘못된 JSON 형식입니다."),
  TYPE_MISMATCH(HttpStatus.BAD_REQUEST, "파라미터 타입이 올바르지 않습니다."),
  MISSING_PARAMETER(HttpStatus.BAD_REQUEST, "필수 요청 파라미터가 누락되었습니다."),
  VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "요청 데이터 검증에 실패했습니다."),
  API_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 API 요청입니다."),

  // User
  USER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 사용자를 찾을 수 없습니다."),
  DUPLICATE_NICKNAME(HttpStatus.CONFLICT, "이미 존재하는 닉네임입니다."),

  //Survey
  ALREADY_SURVEY(HttpStatus.BAD_REQUEST, "설문이 이미 존재합니다 "),

  //Timer
  TIMER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 타이머를 찾을 수 없습니다."),
  NOT_OWNER_OF_TIMER(HttpStatus.FORBIDDEN, "타이머에 대한 권한이 없습니다."),

  //TimerResult
  ALREADY_REGISTERED_RESULT(HttpStatus.BAD_REQUEST, "타이머 결과가 이미 등록되어 있습니다."),

  //VotePost, Vote
  ALREADY_VOTED(HttpStatus.BAD_REQUEST, "이미 투표한 글입니다."),
  VOTE_POST_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 투표글이 존재하지 않습니다."),
  NOT_AUTHOR_OF_VOTE_POST(HttpStatus.FORBIDDEN, "본인이 작성한 글만 삭제할 수 있습니다."),

  // Notification
  NOTIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 알림을 찾을 수 없습니다."),
  UNAUTHORIZED_ACCESS(HttpStatus.FORBIDDEN, "알림에 대한 권한이 없습니다."),

  // Authentication
  UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증이 필요합니다."),
  INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다.");

  private final HttpStatus status;
  private final String message;

  ErrorCode(HttpStatus status, String message) {
    this.status = status;
    this.message = message;
  }

}
