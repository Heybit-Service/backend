package com.heybit.backend.application.service;

import com.heybit.backend.domain.productinfo.ProductInfo;
import com.heybit.backend.domain.vote.VoteRepository;
import com.heybit.backend.domain.votepost.ProductVotePost;
import com.heybit.backend.domain.votepost.ProductVotePostRepository;
import com.heybit.backend.presentation.votepost.dto.ProductVotePostResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductVotePostService {

  private final ProductVotePostRepository productVotePostRepository;
  private final VoteRepository voteRepository;

  public List<ProductVotePostResponse> getAllInProgressPosts() {
    List<ProductVotePost> posts = productVotePostRepository.findInProgressPostsUserNotVoted(1L);

    return posts.stream()
        .map(post -> {
          ProductInfo info = post.getProductTimer().getProductInfo();
          return ProductVotePostResponse.builder()
              .votePostId(post.getId())
              .name(info.getName())
              .amount(info.getAmount())
              .description(info.getDescription())
              .imageUrl(info.getImageUrl())
              .build();
        })
        .toList();
  }

  public void save(ProductVotePost votePost) {
    productVotePostRepository.save(votePost);
  }
}
