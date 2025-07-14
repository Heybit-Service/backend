package com.heybit.backend.application.usecase;

import com.heybit.backend.presentation.timer.dto.ProductTimerRequest;
import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;

public interface CreateTimerUseCase {

  Long execute(ProductTimerRequest request, Long userId, MultipartFile imgUrl) throws IOException;

}
