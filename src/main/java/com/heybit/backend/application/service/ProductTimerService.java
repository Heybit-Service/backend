package com.heybit.backend.application.service;


import com.heybit.backend.domain.timer.ProductTimer;
import com.heybit.backend.domain.timer.ProductTimerRepository;
import com.heybit.backend.domain.votepost.ProductVotePostRepository;
import com.heybit.backend.global.exception.ApiException;
import com.heybit.backend.global.exception.ErrorCode;
import com.heybit.backend.presentation.timer.dto.ProductTimerResponse;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductTimerService {

  private final ProductTimerRepository productTimerRepository;
  private final ProductVotePostRepository productVotePostRepository;

  // 진행 중인 타이머(IN_progress)
  public List<ProductTimerResponse> getProgressTimer(Long userId) {
    List<ProductTimer> timers = productTimerRepository.findProgressTimersOrderByPriority(userId);

    return timers.stream()
        .map(timer -> ProductTimerResponse.from(
            timer,
            productVotePostRepository.existsByProductTimerId(timer.getId())
        ))
        .collect(Collectors.toList());
  }


  public ProductTimer save(ProductTimer timer) {
    return productTimerRepository.save(timer);
  }

  public ProductTimer findById(Long id) {
    return productTimerRepository.findById(id)
        .orElseThrow(() -> new ApiException(ErrorCode.TIMER_NOT_FOUND));
  }

  public void delete(ProductTimer timer) {
  }
}
