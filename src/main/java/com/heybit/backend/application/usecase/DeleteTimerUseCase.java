package com.heybit.backend.application.usecase;

public interface DeleteTimerUseCase {

  void execute(Long timerId, Long userId);

}
