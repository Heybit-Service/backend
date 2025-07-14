package com.heybit.backend.application.service;


import com.heybit.backend.domain.timer.ProductTimer;
import com.heybit.backend.domain.timer.ProductTimerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductTimerService {

  private final ProductTimerRepository productTimerRepository;

  public ProductTimer save(ProductTimer timer) {
    return productTimerRepository.save(timer);
  }

  public ProductTimer findById(Long id) {
    return productTimerRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 타이머입니다."));
  }
}
