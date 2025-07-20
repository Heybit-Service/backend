package com.heybit.backend.application.service;

import com.heybit.backend.domain.productinfo.ProductInfo;
import com.heybit.backend.domain.vote.VoteRepository;
import com.heybit.backend.domain.vote.VoteRepository.VoteStats;
import com.heybit.backend.domain.votepost.ProductVotePost;
import com.heybit.backend.domain.votepost.ProductVotePostRepository;
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
    List<ProductVotePost> posts = productVotePostRepository.findInProgressPostsUserNotVoted(userId);

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

    int buyCount = stats != null ? stats.getBuyCount().intValue() : 0;
    int holdCount = stats != null ? stats.getHoldCount().intValue() : 0;
    int total = buyCount + holdCount;
    int holdPercent = total == 0 ? 0 : (holdCount * 100) / total;

    return MyVotePostResponse.builder()
        .name(info.getName())
        .description(info.getDescription())
        .amount(info.getAmount())
        .imageUrl(info.getImageUrl())
        .buyCount(buyCount)
        .holdCount(holdCount)
        .holdPercent(holdPercent)
        .build();
  }

  @Transactional
  public void deleteVotePost(Long votePostId, Long userId) {
    ProductVotePost votePost = productVotePostRepository.findById(votePostId)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 투표글입니다"));

    if (!votePost.getProductTimer().getUser().getId().equals(userId)) {
      throw new IllegalArgumentException("본인이 작성한 글만 삭제할 수 있습니다");
    }

    voteRepository.deleteByProductVotePostId(votePostId);
    productVotePostRepository.deleteById(votePostId);
  }

  public void save(ProductVotePost votePost) {
    productVotePostRepository.save(votePost);
  }
}
