package com.heybit.backend.application.service;

import com.heybit.backend.domain.productinfo.ProductInfo;
import com.heybit.backend.domain.productinfo.ProductInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductInfoService {

  private final ProductInfoRepository productInfoRepository;

  public ProductInfo save(ProductInfo info) {
    return productInfoRepository.save(info);
  }
}
