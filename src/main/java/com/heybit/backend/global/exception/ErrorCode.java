package com.heybit.backend.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

  INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "내부 서버 오류가 발생했습니다."),
  INVALID_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다"),

  // User
  USER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 사용자를 찾을 수 없습니다."),
  DUPLICATE_NICKNAME(HttpStatus.CONFLICT, "이미 존재하는 닉네임입니다."),

  //Timer
  TIMER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 타이머를 찾을 수 없습니다."),

  //VotePost, Vote
  ALREADY_VOTED(HttpStatus.BAD_REQUEST, "이미 투표한 글입니다."),
  VOTE_POST_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 투표글이 존재하지 않습니다."),
  NOT_AUTHOR_OF_VOTE_POST(HttpStatus.FORBIDDEN, "본인이 작성한 글만 삭제할 수 있습니다."),

  // Authentication
  INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다.");

  private final HttpStatus status;
  private final String message;

  ErrorCode(HttpStatus status, String message) {
    this.status = status;
    this.message = message;
  }

}
