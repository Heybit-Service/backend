package com.heybit.backend.application.service;

import com.heybit.backend.domain.votepost.ProductVotePost;
import com.heybit.backend.domain.votepost.ProductVotePostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductVotePostService {

  private final ProductVotePostRepository productVotePostRepository;

  public void save(ProductVotePost votePost) {
    productVotePostRepository.save(votePost);
  }
}
