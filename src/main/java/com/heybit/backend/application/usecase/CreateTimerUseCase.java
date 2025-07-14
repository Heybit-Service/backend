package com.heybit.backend.application.usecase;

import com.heybit.backend.presentation.timer.dto.ProductTimerRequest;
import java.io.IOException;

public interface CreateTimerUseCase {

  Long execute(ProductTimerRequest request, Long userId) throws IOException;

}
