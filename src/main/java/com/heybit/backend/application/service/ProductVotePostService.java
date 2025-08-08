package com.heybit.backend.application.service;

import com.heybit.backend.domain.productinfo.ProductInfo;
import com.heybit.backend.domain.vote.VoteRepository;
import com.heybit.backend.domain.vote.VoteRepository.VoteStats;
import com.heybit.backend.domain.votepost.ProductVotePost;
import com.heybit.backend.domain.votepost.ProductVotePostRepository;
import com.heybit.backend.global.exception.ApiException;
import com.heybit.backend.global.exception.ErrorCode;
import com.heybit.backend.presentation.vote.dto.VoteStatsDto;
import com.heybit.backend.presentation.votepost.dto.MyVotePostResponse;
import com.heybit.backend.presentation.votepost.dto.ProductVotePostResponse;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductVotePostService {

  private final ProductVotePostRepository productVotePostRepository;
  private final VoteRepository voteRepository;

  public List<ProductVotePostResponse> getAllInProgressPosts(Long userId) {
    List<ProductVotePost> posts = productVotePostRepository.findInProgressPostsUserNotVotedAndNotOwned(userId);

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

  public List<MyVotePostResponse> getMyInProgressVotePosts(Long userId) {

    List<ProductVotePost> myPosts =
        productVotePostRepository.findMyInProgressPosts(userId);

    List<Long> postIds = myPosts.stream()
        .map(ProductVotePost::getId)
        .collect(Collectors.toList());

    if (postIds.isEmpty()) {
      return Collections.emptyList();
    }

    List<VoteRepository.VoteStats> statsList = voteRepository.countBuyHoldByPostIds(postIds);

    Map<Long, VoteStats> statsMap = statsList.stream()
        .collect(Collectors.toMap(VoteRepository.VoteStats::getPostId, v -> v));

    return myPosts.stream()
        .map(post -> toMyVotePostResponse(post, statsMap.get(post.getId())))
        .collect(Collectors.toList());
  }

  private MyVotePostResponse toMyVotePostResponse(ProductVotePost post, VoteStats stats) {
    ProductInfo info = post.getProductTimer().getProductInfo();
    VoteStatsDto dto = (stats != null) ? VoteStatsDto.from(stats) : VoteStatsDto.empty();

    return MyVotePostResponse.builder()
        .name(info.getName())
        .description(info.getDescription())
        .amount(info.getAmount())
        .imageUrl(info.getImageUrl())
        .buyCount(dto.getBuyCount())
        .holdCount(dto.getHoldCount())
        .holdPercent(dto.getHoldPercent())
        .build();
  }

  @Transactional
  public void deleteVotePost(Long votePostId, Long userId) {
    ProductVotePost votePost = productVotePostRepository.findById(votePostId)
        .orElseThrow(() -> new ApiException(ErrorCode.VOTE_POST_NOT_FOUND));

    if (!votePost.getProductTimer().getUser().getId().equals(userId)) {
      throw new ApiException(ErrorCode.NOT_AUTHOR_OF_VOTE_POST);
    }

    voteRepository.deleteByProductVotePostId(votePostId);
    productVotePostRepository.deleteById(votePostId);
  }

  public void save(ProductVotePost votePost) {
    productVotePostRepository.save(votePost);
  }

  @Transactional
  void deleteVotePostWithVotesByTimerId(Long timerId) {
    productVotePostRepository.findByProductTimerId(timerId)
        .ifPresent(votePost -> {
          voteRepository.deleteByProductVotePostId(votePost.getId());
          productVotePostRepository.delete(votePost);
        });
  }

}
